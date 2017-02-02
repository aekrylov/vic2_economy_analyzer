package main;

import eug.parser.EUGFileIO;
import eug.shared.GenericObject;
import eug.shared.ObjectVariable;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Report {

    public long popCount;
    private String currentDate;
    private Country playerCountry;
    private String startDate;

    private static String modPath;
    private static String localisationPath;

    public static final String TOTAL_TAG = "TOT";

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
     * Installpath in, list of csv string names out. Half finished, will probably not be completed.
     */
    private static List<String> listFiles(String path) throws NullPointerException {

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

        File folder = new File(path + "/localisation");
        File[] files = folder.listFiles();

        return Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".csv"))
                .map(File::getName)
                .collect(Collectors.toList());


    }

    private Map<String, Product> productMap = new HashMap<>();
    private Map<String, Country> countries = new HashMap<>();

    /**
     * Reads the given file and for any given line checks if the tag is equal to the
     * one in countrylist.
     *
     * @param filename
     * @throws IOException
     */
    private void readCountryNames(String filename) throws IOException {
        /* The same reader as in SaveGameReader */
        //InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "ISO8859_1"); // This encoding seems to work for ö and ü
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "Cp1251"); // This encoding seems to work for ö and ü
        BufferedReader scanner = new BufferedReader(reader);

        String line;
        while ((line = scanner.readLine()) != null) {
            String[] dataArray = line.split(";");

            setCountryName(dataArray[0], dataArray[1]);
        }
        scanner.close();
    }

    public Product findProduct(String name) {
        return productMap.get(name);
    }

    /**
     * Reads localisations for given game path
     */
    private void readLocalisations(String path) {

        try {
            List<String> loclist = listFiles(path);
            List<String> modLoclist = null;


            if (modPath != null && !modPath.isEmpty()) {
                if (new File(modPath).exists())
                    modLoclist = listFiles(modPath);
            }
            for (String filesForLoad : loclist) {
                readCountryNames(path + "/localisation/" + filesForLoad);
                //readCountryNames(localizationPatch+"/" + filesForLoad);

            }
            if (modLoclist != null) {
                for (String filesForLoad : modLoclist) {
                    readCountryNames(modPath + "/localisation/" + filesForLoad);
                    //readCountryNames(localizationPatch+"/" + filesForLoad);

                }
            }

        } catch (NullPointerException | IOException e) {
            //getErrorLabel().setText(getErrorLabel().getText() + " Some or all the of the localisation files could not be found. ");
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

            for (ProductStorage storage : country.getStorage()) {
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
        //localizationPatch=localPatch;


        //------------------------------------
        // global data loading
        //------------------------------------
        GenericObject eugSave = EUGFileIO.load(savePatch);
        GenericObject worldmarket = eugSave.getChild("worldmarket");

        // price loading
        List<ObjectVariable> price_pool = worldmarket.getChild("price_pool").values;
        for (ObjectVariable iter : price_pool) {
            Product product = new Product(iter.varname, Float.valueOf(iter.getValue()));
            productMap.put(product.getName(), product);
        }

        //load global product data
        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put("price_change", "trend");
        fieldMap.put("demand", "maxDemand");
        fieldMap.put("real_demand", "affordable");
        fieldMap.put("actual_sold", "consumption");
        fieldMap.put("supply_pool", "supply");
        fieldMap.put("actual_sold_world", "actualSoldWorld");
        fieldMap.put("worldmarket_pool", "worldmarketPool");

        try {
            for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                GenericObject object = worldmarket.getChild(entry.getKey());
                Field field = Product.class.getField(entry.getValue());

                List<ObjectVariable> list = object.values;
                for (ObjectVariable variable : list) {
                    field.setFloat(productMap.get(variable.varname), Float.valueOf(variable.getValue()));
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
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
            for (ObjectVariable productObject : saved_country_supply.values) {
                Product tempProduct = findProduct(productObject.varname);
                if (tempProduct != null) {
                    ProductStorage tstorage = new ProductStorage(tempProduct);
                    tstorage.totalSupply = Float.valueOf(productObject.getValue());
                    country.addStorage(tstorage);
                } else System.err.println("Nash: findProduct(productObject.varname) returned NULL");
            }

            // load max demand
            GenericObject foundMaxDemand = countryObject.getChild("domestic_demand_pool");
            for (ObjectVariable everyGood : foundMaxDemand.values) {
                ProductStorage storage = country.findStorage(everyGood.varname);
                if (storage != null) {
                    storage.MaxDemand = Float.valueOf(everyGood.getValue());
                } else {
                    storage = new ProductStorage(findProduct(everyGood.varname));
                    storage.MaxDemand = Float.valueOf(everyGood.getValue());
                    country.addStorage(storage);
                }
            }

            // load internal consumption
            GenericObject foundSold = countryObject.getChild("actual_sold_domestic");
            for (ObjectVariable everyGood : foundSold.values) {
                ProductStorage storage = country.findStorage(everyGood.varname);
                if (storage != null) {
                    storage.actualSoldDomestic = Float.valueOf(everyGood.getValue());
                    storage.actualDemand = storage.actualSoldDomestic;
                } else {
                    storage = new ProductStorage(findProduct(everyGood.varname));
                    storage.actualSoldDomestic = Float.valueOf(everyGood.getValue());
                    storage.actualDemand = storage.actualSoldDomestic;
                    country.addStorage(storage);
                }
            }

            //count factory workers
            List<ObjectVariable> ghr;
            for (GenericObject stateObject : countryObject.children) {
                if (stateObject.name.equalsIgnoreCase("state"))
                    for (GenericObject everyBuilding : stateObject.getChildren("state_buildings")) {
                        List<GenericObject> currentLevel = everyBuilding.getChildren("employment");
                        if (currentLevel != null && currentLevel.size() > 0) {
                            currentLevel = currentLevel.get(0).getChildren("employees");
                            if (currentLevel != null && currentLevel.size() > 0) {
                                for (GenericObject everyEmpl : currentLevel.get(0).children) {
                                    ghr = everyEmpl.values;
                                    if (ghr != null && ghr.size() > 0)
                                        country.employmentFactory += Long.valueOf(ghr.get(0).getValue());

                                    this.getClass();
                                }
                            }
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
                    }
                } else {
                    if (object.name.equalsIgnoreCase("RGO")) {
                        // gold income calculation
                        if (object.values.get(1).getValue().equalsIgnoreCase("precious_metal"))
                            owner.goldIncome += Float.valueOf(object.values.get(0).getValue()) / 1000;

                        //count RGO employees
                        try {
                            List<GenericObject> workers = object.children.get(0).children.get(0).children;
                            for (GenericObject worker : workers) {
                                if (worker.values != null)
                                    if (worker.values.size() > 0)
                                        owner.employmentRGO += Integer.valueOf(worker.values.get(0).getValue());
                            }

                        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
                        }

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

}
