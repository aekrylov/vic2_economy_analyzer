package org.victoria2.tools.vic2sgea.main;

import eug.parser.EUGFileIO;
import eug.parser.NameFilter;
import eug.shared.GenericObject;
import eug.shared.ObjectVariable;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Report {

    public long popCount;
    private String currentDate;
    private Country playerCountry;
    private String startDate;

    public static final String TOTAL_TAG = "TOT";

    // name -> product
    private Map<String, Product> productMap = new HashMap<>();
    //tag -> country
    private Map<String, Country> countries = new HashMap<>();

    public Product findProduct(String name) {
        return productMap.get(name);
    }

    private Product findProductOrCreate(String name) {
        return productMap.computeIfAbsent(name, Product::new);
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public Country getPlayerCountry() {
        return playerCountry;
    }

    public String getStartDate() {
        return startDate;
    }

    public Country getCountry(String tag) {
        return countries.get(tag);
    }

    public Collection<Country> getCountryList() {
        return countries.values();
    }

    public Collection<Product> getProductList() {
        return productMap.values();
    }

    /**
     * Reads the specified save game file fully and constructs full report
     *
     * @param savePath path to save file
     * @param filter if object filter should be used while loading save game
     * @throws RuntimeException if runtime exception occurs
     */
    public Report(String savePath, String gamePath, String modPath, boolean filter) throws RuntimeException {
        Country total = new Country(TOTAL_TAG);
        total.population = 1;
        countries.put(TOTAL_TAG, total);

        System.out.println("Loading products");
        //load all existing products
        Set<Product> products = ReportHelpers.readProducts(modPath);
        if (products == null)
            products = ReportHelpers.readProducts(gamePath);
        else
            products.addAll(ReportHelpers.readProducts(gamePath));

        productMap = products.stream()
                .collect(Collectors.toMap(Product::getName, Function.identity()));


        System.out.println("Nash: loading savegame... free memory is " + ReportHelpers.getFreeMemory());
        GenericObject root = filter ? EUGFileIO.load(savePath, new UnwantedObjectFilter()) : EUGFileIO.load(savePath);

        loadGlobalProductInfo(root);

        Vic2SaveGameCustom save = new Vic2SaveGameCustom(root);

        System.out.println("Nash: processing savegame data... free memory is " + ReportHelpers.getFreeMemory());
        loadCountries(save);
        loadPops(save);
        countTotals();

        System.out.println("Nash: reading localizations...  free memory is " + ReportHelpers.getFreeMemory());
        readLocalisations(gamePath);
        readLocalisations(modPath);

        loadMisc(root);
    }

    public Report(String savePath, String gamePath, String modPath) {
        this(savePath, gamePath, modPath, true);
    }

    /**
     * Loads dates and player country
     *
     * @param root root of savegame
     */
    private void loadMisc(GenericObject root) {
        currentDate = root.getString("date");
        playerCountry = countries.get(root.getString("player"));
        startDate = root.getString("start_date");

    }

    /**
     * Reads localisations for given game/mod path
     */
    private void readLocalisations(String path) {

        try {
            List<File> loclist = ReportHelpers.getLocalisationFiles(path);
            for (File csv : loclist) {
                readCountryNames(csv);
            }

        } catch (NullPointerException | IOException e) {
            System.err.println("Nash: Some or all the of the csv files could not be loaded");
        }

    }

    /**
     * Reads the given file and for any given line checks if the tag is equal to the
     * one in countrylist.
     *
     * @param file file to read
     * @throws IOException
     */
    private void readCountryNames(File file) throws IOException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "Cp1251"); // This encoding seems to work for ö and ü
        BufferedReader in = new BufferedReader(reader);

        String line;
        while ((line = in.readLine()) != null) {
            String[] dataArray = line.split(";");
            if (dataArray.length < 1)
                continue;

            setCountryName(dataArray[0], dataArray[1]);
        }
        in.close();
    }

    private void setCountryName(String tag, String name) {
        try {
            countries.get(tag).setOfficialName(name);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Count total values and set GDP rankings
     */
    private void countTotals() {

        //removing empty countries
        countries = countries.values()
                .stream()
                .filter(Country::exist)
                .collect(Collectors.toMap(Country::getTag, e -> e));

        List<Country> countryList = new ArrayList<>(countries.values());

        Country totalCountry = countries.get(TOTAL_TAG);

        for (Country country : countryList) {
            if (country.getTag().equals(TOTAL_TAG))
                continue;

            // calculating real totalSupply (without wasted)
            country.innerCalculations();
            totalCountry.add(country);

            for (ProductStorage storage : country.getStorage().values()) {
                ProductStorage totalStorage = totalCountry.findStorage(storage.product);
                totalStorage.add(storage);
            }
        }

        //totalCountry.innerCalculations();
        totalCountry.setOfficialName("Total");
        for (Country country : countryList) {
            country.calcGdpPart(totalCountry);
        }

        countryList.sort(Comparator.reverseOrder());
        int calc = 0;
        for (Country country : countryList) {
            country.GDPPlace = calc;
            calc++;
        }
    }

    /**
     * Loads global product info
     *
     * @param root root object of savegame file
     * @throws NullPointerException if savegame is invalid
     */
    private void loadGlobalProductInfo(GenericObject root) throws NullPointerException {

        //------------------------------------
        // global data loading
        //------------------------------------
        GenericObject worldmarket = root.getChild("worldmarket");

        //load global product data
        Map<String, BiConsumer<Product, Float>> fieldMap = new HashMap<>();
        fieldMap.put("price_pool", Product::setPrice);
        fieldMap.put("price_change", Product::setTrend);
        fieldMap.put("demand", Product::setMaxDemand);
        fieldMap.put("real_demand", Product::setDemand);
        fieldMap.put("actual_sold", Product::setConsumption);
        fieldMap.put("supply_pool", Product::setSupply);
        fieldMap.put("actual_sold_world", Product::setActualSoldWorld);
        fieldMap.put("worldmarket_pool", Product::setWorldmarketPool);

        for (Map.Entry<String, BiConsumer<Product, Float>> entry : fieldMap.entrySet()) {
            GenericObject object = worldmarket.getChild(entry.getKey());
            BiConsumer<Product, Float> setter = entry.getValue();

            List<ObjectVariable> list = object.values;
            for (ObjectVariable var : list) {
                setter.accept(findProductOrCreate(var.getName()), Float.valueOf(var.getValue()));
            }
        }
    }

    /**
     * Loads countries, states and buildings
     */
    private void loadCountries(Vic2SaveGameCustom save) {


        //------------------------------------
        // country data loading
        //------------------------------------

        for (GenericObject countryObject : save.getCountries()) {
            Country country = new Country(countryObject.name);

            Map<String, BiConsumer<ProductStorage, Float>> fieldsMap = new HashMap<>();
            fieldsMap.put("saved_country_supply", ProductStorage::setTotalSupply);
            fieldsMap.put("domestic_demand_pool", ProductStorage::setMaxDemand);
            fieldsMap.put("actual_sold_domestic", (productStorage, value) -> {
                productStorage.setActualSoldDomestic(value);
                productStorage.setActualDemand(value);
            });

            for (Map.Entry<String, BiConsumer<ProductStorage, Float>> entry : fieldsMap.entrySet()) {
                GenericObject object = countryObject.getChild(entry.getKey());
                for (ObjectVariable productVar : object.values) {
                    Product product = findProduct(productVar.getName());
                    ProductStorage storage = country.findStorage(product);

                    entry.getValue().accept(storage, Float.valueOf(productVar.getValue()));

                }
            }

            //count factory workers
            for (GenericObject stateObject : save.getStates(countryObject)) {

                for (GenericObject building : stateObject.getChildren("state_buildings")) {

                    //if it has employment, it is a factory
                    GenericObject employment = building.getChild("employment");
                    if (employment != null) {
                        GenericObject stockpile = building.getChild("stockpile");

                        //assuming that factory stockpile is close to its consumption the previous day
                        for (ObjectVariable good : stockpile.values) {
                            country.addIntermediate(findProduct(good.getName()), Float.parseFloat(good.getValue()));
                        }

                        country.employmentFactory += ReportHelpers.getEmployeeCount(building);
                    }
                }

            }

            countries.put(country.getTag(), country);
        }
    }

    private void loadPops(Vic2SaveGameCustom save) {
        popCount = 0;

        for (GenericObject province : save.provinces.values()) {
            String ownerTag = province.getString("owner");
            if (ownerTag.isEmpty()) {
                ownerTag = TOTAL_TAG;
            }

            Country owner = countries.get(ownerTag);
            for (GenericObject object : province.children) {

                // population calculation {
                if (object.contains("size")) {
                    popCount++;
                    int popSize = object.getInt("size");
                    //todo why x4

                    owner.population += popSize * 4;

                    if (object.name.equalsIgnoreCase("farmers") || object.name.equalsIgnoreCase("labourers") || object.name.equalsIgnoreCase("slaves")) {
                        owner.workforceRGO += popSize;
                    } else if (object.name.equalsIgnoreCase("clerks") || object.name.equalsIgnoreCase("craftsmen")) {
                        owner.workforceFactory += popSize;
                    } else if (object.name.equalsIgnoreCase("artisans") && object.containsValue("production_type")) {

                        GenericObject stockpile = object.getChild("stockpile");
                        if (stockpile != null) {
                            for (ObjectVariable good : stockpile.values) {
                                owner.addIntermediate(findProduct(good.getName()), Float.parseFloat(good.getValue()));
                            }
                        }
                    }
                } else {
                    if (object.name.equalsIgnoreCase("rgo")) {
                        //exact rgo output is not shown, we can guess based on last_income
                        Product output = findProduct(object.getString("goods_type"));
                        double lastIncome = object.getDouble("last_income") / 1000;

                        // gold income calculation
                        if (output.getName().equalsIgnoreCase("precious_metal"))
                            owner.goldIncome += lastIncome;

                        //count RGO employees
                        owner.employmentRGO += ReportHelpers.getEmployeeCount(object);

                    }

                }
            }
        }
    }

    static class UnwantedObjectFilter implements NameFilter {

        private static final List<String> unwantedCommonTags = Arrays.asList(
                "issues", "province_pop_id", "military_construction", "unit_names",
                "leader", "army", "navy", "trade", "ai",
                "rebel_faction", "previous_war", "news_collector");

        //todo names duplicate
        private static final List<String> countryTags = Arrays.asList("saved_country_supply",
                "domestic_demand_pool", "actual_sold_domestic", "state");

        //todo leave only the tags needed

        private boolean isCountry(GenericObject object) {
            return object.name.matches("[A-Z][A-Z0-9]{2}") && !object.isRoot() && object.getParent().isRoot();
        }

        private boolean isProvince(GenericObject object) {
            return object.getParent() != null && object.getParent().isRoot() && object.name.matches("[0-9]{1,4}");
        }

        @Override
        public Boolean apply(GenericObject object, String s) {
            boolean discard = unwantedCommonTags.contains(s) || isCountry(object) && !countryTags.contains(s);
            return !discard;
        }
    }
}
