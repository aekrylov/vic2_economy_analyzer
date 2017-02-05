package main;

import eug.parser.EUGFileIO;
import eug.shared.GenericObject;
import eug.shared.ObjectVariable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Report {

    public long popCount;
    private String currentDate;
    private Country playerCountry;
    private String startDate;

    private static String modPath;
    private static String localisationPath;

    public static final String TOTAL_TAG = "TOT";

    private static final List<String> convoys = Arrays.asList("steamer", "clipper");

    public static void setModPath(String modPath) {
        Report.modPath = modPath;
    }

    public static void setLocalisationPath(String localisationPath) {
        Report.localisationPath = localisationPath;
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
     * Returns list localisation/*.csv file for gicen path
     * Half finished, will probably not be completed.
     */
    private static List<File> getLocalisations(String path) throws NullPointerException {

        // Directory path here
        //	  String path = "."; 
        /*String [] unwanted = {"darkness.csv", "1.1.csv", "1.3.csv", "1.4.csv", "beta1.csv", "beta3.csv", "darkness_3_02.csv",
                "darkness_3_03.csv",
				"event_news.csv",
				"event_news_3_01.csv",
				"housedivided2_1.csv",
				"housedivided2_2.csv",
				"housedivided2_3.csv",
				"newspaper_text.csv",
		"newstext_3_01.csv" };*/

        //the correct platform independent way to join paths
        File folder = Paths.get(path, "localisation").toFile();
        File[] files = folder.listFiles();

        return Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".csv"))
                .collect(Collectors.toList());
    }

    private Map<String, Product> productMap = new HashMap<>();
    private Map<String, Country> countries = new HashMap<>();

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

            setCountryName(dataArray[0], dataArray[1]);
        }
        in.close();
    }

    public Product findProduct(String name) {
        Product tmp = productMap.get(name);
        if (tmp != null)
            return tmp;

        //handle convoys
        if (convoys.contains(name))
            return productMap.get(name + "_convoy");

        return null;
    }

    /**
     * Returns product which name is a prefix of the given string.
     * Performs {@code name.length} hash map searches in worst case
     * Convoys are handled here
     *
     * @param str String to search for
     * @return corresponding Product instance, or null if none found
     */
    private Product findProductPrefix(String str) {
        if (str.isEmpty())
            return null;

        Product product = findProduct(str);
        if (product == null)
            product = findProductPrefix(str.substring(0, str.length() - 1));

        return product;
    }

    /**
     * Reads localisations for given game path
     */
    private void readLocalisations(String path) {

        try {
            List<File> loclist = getLocalisations(path);

            if (modPath != null && new File(modPath).exists()) {
                loclist.addAll(getLocalisations(modPath));
            }

            for (File csv : loclist) {
                readCountryNames(csv);
            }

        } catch (NullPointerException | IOException e) {
            //todo error handling
            System.out.println("Nash: Some or all the of the localisation files could not be loaded");
        }
        //}

    }

    /**
     * Set country name
     */
    synchronized private void setCountryName(String tag, String name) {
        try {
            countries.get(tag).setOfficialName(name);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Reads the specified save game file fully and constructs full report
     *
     * @param filePath path to save file
     * @throws IOException if an IO error occurs
     */
    public Report(String filePath) {
        Country total = new Country(TOTAL_TAG);
        total.population = 1;
        countries.put(TOTAL_TAG, total);

        System.out.println("Nash: reading EUG head... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        GenericObject head = readSaveHead(filePath);

        System.out.println("Nash: reading EUG... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        Vic2SaveGameNash save = readSaveBody(filePath, head);
        System.out.println("Nash: processing POPs... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        countPops(save);

        if (!readProductInfo(modPath))
            readProductInfo(localisationPath);

        System.out.println("Nash: reading localizations...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        readLocalisations(localisationPath);

        //Runtime.getRuntime().gc();

        PathKeeper.save();
        System.out.println("Nash: processing data...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        countTotals();
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
            country.innerCalculation();

            totalCountry.population += country.population;
            totalCountry.goldIncome += country.goldIncome;
            totalCountry.employmentRGO += country.employmentRGO;
            totalCountry.employmentFactory += country.employmentFactory;
            totalCountry.workforceRGO += country.workforceRGO;
            totalCountry.workforceFactory += country.workforceFactory;

            totalCountry.exported += country.exported;
            totalCountry.imported += country.imported;
            totalCountry.actualSupply += country.actualSupply;

            totalCountry.setRealGdp(totalCountry.getRealGdp() + country.getRealGdp());

            for (ProductStorage storage : country.getStorage().values()) {
                Product product = findProduct(storage.product.name);
                product.actualBought += storage.actualSupply;

                ProductStorage totalStorage = totalCountry.findStorage(storage.product.name);
                if (totalStorage == null) {
                    totalStorage = new ProductStorage(storage.product);
                    totalCountry.addStorage(totalStorage);
                }

                totalStorage.actualDemand += storage.actualDemand;
                totalStorage.actualSoldDomestic += storage.actualSoldDomestic;
                totalStorage.actualSupply += storage.actualSupply;
                totalStorage.exported += storage.exported;
                totalStorage.imported += storage.imported;
                totalStorage.MaxDemand += storage.MaxDemand;
                totalStorage.totalSupply += storage.totalSupply;
            }
        }

        totalCountry.innerCalculation();
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

    private GenericObject readSaveHead(String savePatch) {

        //------------------------------------
        // global data loading
        //------------------------------------
        GenericObject eugSave = EUGFileIO.load(savePatch);
        GenericObject worldmarket = eugSave.getChild("worldmarket");

        // price loading
        List<ObjectVariable> price_pool = worldmarket.getChild("price_pool").values;
        for (ObjectVariable iter : price_pool) {
            Product product = new Product(iter.getName(), Float.valueOf(iter.getValue()));
            productMap.put(product.getName(), product);
        }

        //load global product data
        Map<String, BiConsumer<Product, Float>> fieldMap = new HashMap<>();
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
                setter.accept(productMap.get(var.getName()), Float.valueOf(var.getValue()));
            }
        }

        return eugSave;

    }

    /**
     * Should contain EUG file reading only
     */
    private Vic2SaveGameNash readSaveBody(String savePatch, GenericObject eugSave) {


        System.out.println("Nash: openning Vic2SaveGame...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        Vic2SaveGameNash save = new Vic2SaveGameNash(eugSave, savePatch, "", "");


        System.out.println("Nash: preload Provinces...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        save.preloadProvinces();
        System.out.println("Nash: preload Countries...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        save.preloadCountries();
        System.out.println("Nash: preload States...  free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        save.preloadStates();

        //------------------------------------
        // country data loading
        //------------------------------------

        for (GenericObject countryObject : save.getCountries()) {
            Country country = new Country(countryObject.name);

            // load savedCountrySupply (ts)
            GenericObject saved_country_supply = countryObject.getChild("saved_country_supply");
            for (ObjectVariable everyGood : saved_country_supply.values) {
                ProductStorage storage = country.findStorage(everyGood.getName());
                if (storage == null) {
                    storage = new ProductStorage(findProduct(everyGood.getName()));
                    country.addStorage(storage);
                }

                storage.totalSupply = Float.valueOf(everyGood.getValue());
            }

            // load max demand
            GenericObject foundMaxDemand = countryObject.getChild("domestic_demand_pool");
            for (ObjectVariable everyGood : foundMaxDemand.values) {
                ProductStorage storage = country.findStorage(everyGood.getName());
                if (storage == null) {
                    storage = new ProductStorage(findProduct(everyGood.getName()));
                    country.addStorage(storage);
                }

                storage.MaxDemand = Float.valueOf(everyGood.getValue());
            }

            // load internal consumption
            GenericObject foundSold = countryObject.getChild("actual_sold_domestic");
            for (ObjectVariable everyGood : foundSold.values) {
                ProductStorage storage = country.findStorage(everyGood.getName());
                if (storage == null) {
                    storage = new ProductStorage(findProduct(everyGood.getName()));
                    country.addStorage(storage);
                }

                storage.actualSoldDomestic = Float.valueOf(everyGood.getValue());
                storage.actualDemand = storage.actualSoldDomestic;
            }

            //count factory workers
            for (GenericObject stateObject : countryObject.getChildren("state")) {

                for (GenericObject building : stateObject.getChildren("state_buildings")) {

                    //if it has employment, it is a factory
                    GenericObject employment = building.getChild("employment");
                    if (employment != null) {
                        Product output = findProductPrefix(getProductNameFactory(building.getString("building")));
                        //count factory output
                        float supply = Float.parseFloat(building.getString("produces"));
                        float sold = Float.parseFloat(building.getString("last_income")) / 1000 / output.getPrice();

                        ProductStorage storage = country.getRealStorage(output);
                        storage.incTotalSupply(supply);
                        storage.incActualSupply(sold);
                        //todo check leftover

                        GenericObject stockpile = building.getChild("stockpile");

                        //assuming that factory stockpile is close to its consumption the previous day
                        for (ObjectVariable good : stockpile.values) {
                            country.incRealSupply(findProduct(good.getName()), -Float.parseFloat(good.getValue()));
                        }

                        country.employmentFactory += getEmployeeCount(building);
                    }
                }

            }

            countries.put(country.getTag(), country);
        }

        List<ObjectVariable> head = eugSave.values;

        for (ObjectVariable obj : head) {
            if (obj.varname.equalsIgnoreCase("date"))
                currentDate = obj.getValue();
            if (obj.varname.equalsIgnoreCase("player"))
                playerCountry = countries.get(obj.getValue());
            if (obj.varname.equalsIgnoreCase("start_date")) {
                startDate = obj.getValue();
                break;
            }
        }

        return save;
    }

    private void countPops(Vic2SaveGameNash save) {
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
                        String productName = getProductNameArtisans(object.getString("production_type"));
                        Product output = findProduct(productName);

                        //todo incorrect
                        //float supply = Float.parseFloat(object.getString("current_producing"));
                        //float soldDomestic = supply * Float.parseFloat(object.getString("percent_sold_domestic"));
                        //float soldExport = supply * Float.parseFloat(object.getString("percent_sold_export"));

                        float sold = Float.parseFloat(object.getString("production_income")) / 1000 / output.getPrice();

                        ProductStorage storage = owner.getRealStorage(output);
                        //storage.incTotalSupply(supply);
                        storage.incActualSupply(sold);
                        //todo throttle, leftover?

                        GenericObject stockpile = object.getChild("stockpile");
                        if (stockpile != null) {
                            for (ObjectVariable good : stockpile.values) {
                                owner.incRealSupply(findProduct(good.getName()), -Float.parseFloat(good.getValue()));
                            }
                        }
                    }
                } else {
                    if (object.name.equalsIgnoreCase("rgo")) {
                        //exact rgo output is not shown, we can guess based on last_income
                        Product output = findProduct(object.getString("goods_type"));
                        double lastIncome = object.getDouble("last_income") / 1000;
                        double sold = lastIncome / output.getPrice();
                        owner.incRealSupply(output, (float) sold);

                        // gold income calculation
                        if (output.getName().equalsIgnoreCase("precious_metal"))
                            owner.goldIncome += lastIncome;

                        //count RGO employees
                        owner.employmentRGO += getEmployeeCount(object);

                    }

                }
            }
        }
    }

    /**
     * Reads product info from the given path (currently only baseprice)
     *
     * @param path path to game/mod
     * @return if goods.txt is found and read
     */
    private boolean readProductInfo(String path) {
        boolean result = false;
        String goodsPath = path + "/common/goods.txt";

        System.out.println("Nash: attempt to read " + goodsPath);

        if (path != null && !path.isEmpty() && Files.exists(Paths.get(goodsPath))) {

            GenericObject root = EUGFileIO.load(goodsPath);
            if (root != null) {
                List<GenericObject> list;

                String[] productTypes = {
                        "military_goods",
                        "raw_material_goods",
                        "industrial_goods",
                        "consumer_goods"
                };

                for (String type : productTypes) {
                    list = root.getChild(type).children;
                    for (GenericObject object : list) {
                        findProduct(object.name).basePrice = Float.valueOf(object.values.get(0).getValue());
                    }
                }
                result = true;
            }
        }
        if (!result) System.out.println("Nash: failed to read " + goodsPath);
        return result;

    }

    private static String getProductNameArtisans(String productionType) {
        String name = productionType.replace("artisan_", "").replace("winery", "wine");
        if (convoys.contains(name)) {
            name = name + "_convoy";
        }

        return name;
    }

    private static String getProductNameFactory(String buildingType) {
        //Most of HoD factory names handled here
        return getProductNameArtisans(buildingType.replaceAll("_([a-z]+[oe]ry|mill|[a-z]+yard)$", ""));
    }

    /**
     * Adds up all employment->employees counts
     *
     * @param object object with employment child tag
     * @return total count of employees on object, or 0 if object is not valid
     */
    private int getEmployeeCount(GenericObject object) {
        int count = 0;

        try {
            List<GenericObject> workers = object.getChild("employment").getChild("employees").children;
            for (GenericObject worker : workers) {
                if (worker.getInt("count") > 0) {
                    count += worker.getInt("count");
                }
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }//if no tag is available

        return count;
    }

}
