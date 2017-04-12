package org.victoria2.tools.vic2sgea.main;

import eug.parser.CWordFile;
import eug.parser.ChildrenConsumer;
import eug.shared.GenericObject;
import eug.shared.ObjectVariable;
import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.entities.ProductStorage;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Report {

    public long popCount = 0;
    private String currentDate;
    private Country playerCountry;
    private String startDate;

    public static final String TOTAL_TAG = "TOT";
    public static final String TOTAL_PRODUCT = "total_pounds";

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

        SimpleLogger logger = new SimpleLogger();

        System.out.println("Loading products");
        //load all existing products
        Set<Product> products = ReportHelpers.readProducts(modPath);
        if (products == null)
            products = ReportHelpers.readProducts(gamePath);
        else
            products.addAll(ReportHelpers.readProducts(gamePath));

        productMap = products.stream()
                .collect(Collectors.toMap(Product::getName, Function.identity()));

        logger.logMemoryUsage();
        System.out.println("Nash: loading savegame...");
        CWordFile loader = new CWordFile();
        GenericObject root = filter ? loader.load(savePath, new GenericObjectConsumer()) : loader.load(savePath);

        countTotals();

        logger.logMemoryUsage();
        System.out.println("Nash: reading localizations...");
        readLocalisations(gamePath);
        readLocalisations(modPath);

        loadMisc(root);
        System.out.println("Nash: loading complete");
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
            if (dataArray == null || dataArray.length <= 1) // <=1 - otherwise dataArray[1] gaves Out of Boundaries exception in setCountryName(dataArray[0], dataArray[1]) some times - nash
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

        Country totalCountry = countries.computeIfAbsent(TOTAL_TAG, Country::new);

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
            country.setGDPPlace(calc);
            calc++;
        }

        Product total = new Product(TOTAL_PRODUCT);
        float totalItems = 0;
        for (Product product : productMap.values()) {
            float volume = product.getConsumption();
            total.supply += product.getSupply() * product.price;
            total.consumption += product.getConsumption() * product.price;
            total.demand += product.getDemand() * product.price;
            total.maxDemand += product.getMaxDemand() * product.price;
            total.actualSupply += product.getActualSupply() * product.price;

            total.basePrice += product.getBasePrice() * volume;
            total.price += product.getPrice() * volume;

            totalItems += volume;
        }
        total.basePrice /= totalItems;
        total.price /= totalItems;
        productMap.put(TOTAL_PRODUCT, total);
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
        fieldMap.put("supply_pool", Product::setSupply);

        //todo it seems that actual_sold and actual_sold_world add up to real_demand
        fieldMap.put("actual_sold", Product::incConsumption);
        fieldMap.put("actual_sold_world", (product, value) -> {
            product.setActualSoldWorld(value);
            product.incConsumption(value);
        });

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
     * Loads country market data and factories
     * @param countryObject country object
     */
    private void loadCountry(GenericObject countryObject) {
        Country country = countries.computeIfAbsent(countryObject.name, Country::new);

        Map<String, BiConsumer<ProductStorage, Float>> fieldsMap = new HashMap<>();
        fieldsMap.put("saved_country_supply", ProductStorage::setTotalSupply);
        fieldsMap.put("domestic_demand_pool", ProductStorage::setMaxDemand);
        fieldsMap.put("actual_sold_domestic", (productStorage, value) -> {
            productStorage.setSoldDomestic(value);
            productStorage.setBought(value);
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
        for (GenericObject stateObject : countryObject.getChildren("state")) {

            for (GenericObject building : stateObject.getChildren("state_buildings")) {

                //if it has employment, it is a factory
                GenericObject employment = building.getChild("employment");
                if (employment != null) {
                    GenericObject stockpile = building.getChild("stockpile");

                    //assuming that factory stockpile is close to its consumption the previous day
                    for (ObjectVariable good : stockpile.values) {
                        country.addIntermediate(findProduct(good.getName()), Float.parseFloat(good.getValue()));
                    }

                    country.addEmploymentFactory(ReportHelpers.getEmployeeCount(building));
                }
            }

        }
    }

    /**
     * Processes POPs and RGO in a given province
     *
     * @param province province object
     */
    private void loadProvince(GenericObject province) {
        String ownerTag = province.getString("owner");
        if (ownerTag.isEmpty()) {
            ownerTag = TOTAL_TAG;
        }

        Country owner = countries.computeIfAbsent(ownerTag, Country::new);
        for (GenericObject object : province.children) {

            // population calculation {
            if (object.contains("size")) {
                popCount++;
                int popSize = object.getInt("size");
                owner.addPopulation(popSize * 4);

                if (ReportHelpers.POPS_RGO.contains(object.name)) {
                    owner.workforceRGO += popSize;
                } else if (ReportHelpers.POPS_FACTORY.contains(object.name)) {
                    owner.workforceFactory += popSize;
                } else if (ReportHelpers.POPS_ARTISANS.contains(object.name) && object.containsValue("production_type")) {

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
                        owner.addGoldIncome((float) lastIncome);

                    //count RGO employees
                    owner.addEmploymentRgo(ReportHelpers.getEmployeeCount(object));

                }

            }
        }
    }

    /**
     * Consumer for root's children
     */
    class GenericObjectConsumer implements ChildrenConsumer {

        @Override
        public void accept(GenericObject object) {
            if (isProvince(object)) {
                loadProvince(object);
            } else if (isCountry(object)) {
                loadCountry(object);
            } else if (isWorldMarket(object)) {
                loadGlobalProductInfo(object.getRoot());
            }
        }

        private boolean isProvince(GenericObject object) {
            return object.name.matches("[0-9]{1,4}");
        }

        private boolean isCountry(GenericObject object) {
            return object.name.matches("[A-Z][A-Z0-9]{2}") && object.containsChild("state");
        }

        private boolean isWorldMarket(GenericObject object) {
            return object.name.equals("worldmarket");
        }

    }
}
