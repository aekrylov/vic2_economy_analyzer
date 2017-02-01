package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eug.parser.EUGFileIO;

import eug.shared.GenericObject;
import eug.shared.ObjectVariable;

public class Report {

    public long popCount;
    private String currentDate;
    private Country playerCountry;
    private String startDate;

    private static String modPath;
    private static String localisationPath;

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

    public List<Country> getCountryList() {
        return countryList;
    }

    public List<Product> getProducts() {return products; }

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
        List<String> locList = new ArrayList<String>();

        String files;
        File folder = new File(path + "/localisation");
        //File folder = new File(path);
        File[] listOfFiles = folder.listFiles();


        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                if (files.endsWith(".csv") || files.endsWith(".CSV")) {
                    //						for (String un : unwanted) {

                    //							if (!files.equals(un)) {
                    locList.add(files);
                    //								System.out.println(files);
                    //							}

                    //						}

                }
            }
        }

		/* Removing files which I know have no country tags in them */

        //			for (String un: unwanted) {
        //				for (String loc: locList) {
        //					if (loc.equals(un)) {
        //						System.out.println(loc);
        //						locList.remove(un);
        //					}
        //				}
        //			}

        return locList;


    }

    private ArrayList<Product> products = new ArrayList<>();
    private Map<String, Country> countries = new HashMap<>();
    private List<Country> countryList = new ArrayList<>();

    //String localizationPatch;

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
        for (Product product : products)
            if (product.name.equalsIgnoreCase(name)) return product;
        return null;

        // TODO exceptions
    }

    /**
     * Main method of this class. Manages the reading from csv
     */
    public void readLocalisations(String inPatch) {

        try {
            List<String> loclist = listFiles(inPatch);
            List<String> modLoclist = null;


            if (modPath != null && !modPath.isEmpty()) {
                if (new File(modPath).exists())
                    modLoclist = listFiles(modPath);
            }
            //					readCountryNames(Reference.INSTALLPATH + "/localisation/text.csv");
            //					readCountryNames(Reference.INSTALLPATH + "/localisation/1.2.csv");
            //					readCountryNames(Reference.INSTALLPATH + "/localisation/beta2.csv");
            //					readCountryNames(Reference.INSTALLPATH + "/localisation/darkness_3_01.csv");
            //					readCountryNames(Reference.INSTALLPATH + "/localisation/housedivided.csv");
            //					readCountryNames(Reference.INSTALLPATH + "/localisation/newtext.csv");
            for (String filesForLoad : loclist) {
                readCountryNames(inPatch + "/localisation/" + filesForLoad);
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
     * @param filePath path to save file
     * @throws IOException if an IO error occurs
     */
    public Report(String filePath) throws IOException {
        System.out.println("Nash: reading EUG head... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        GenericObject head = readSaveHead(filePath);

        System.out.println("Nash: reading EUG... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        Vic2SaveGameNash save = readSaveBody(filePath, head);
        System.out.println("Nash: processing POPs... free memory is " + Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
        countPops(save);

        if (!readPrices(modPath))
            readPrices(localisationPath);

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
        countryList = countries.values()
                .stream()
                .filter(Country::exist)
                .collect(Collectors.toList());

        Country totalCountry = new Country("");
        //int foundCountries = 0;

        for (Country country : countryList) {

            // calculating real supply (without wasted)
            country.innerCalculation();

            totalCountry.population += country.population;
            totalCountry.goldIncome += country.goldIncome;
            totalCountry.employmentRGO += country.employmentRGO;
            totalCountry.employmentFactory += country.employmentFactory;
            totalCountry.workforceRGO += country.workforceRGO;
            totalCountry.workforceFactory += country.workforceFactory;

            totalCountry.export += country.export;
            totalCountry.imported += country.imported;
            totalCountry.actualSupply += country.actualSupply;

            for (GoodsStorage storage : country.storage) {
                Product product = findProduct(storage.item.name);
                product.actualBought += storage.actualSupply;

                GoodsStorage foundStorage = totalCountry.findStorage(storage.item.name);
                if (foundStorage == null) {
                    foundStorage = new GoodsStorage(storage.item);
                    totalCountry.storage.add(foundStorage);
                }

                foundStorage.actualDemand += storage.actualDemand;
                foundStorage.actualSoldDomestic += storage.actualSoldDomestic;
                foundStorage.actualSupply += storage.actualSupply;
                foundStorage.export += storage.export;
                foundStorage.imported += storage.imported;
                foundStorage.MaxDemand += storage.MaxDemand;
                foundStorage.savedCountrySupply += storage.savedCountrySupply;

            }
        }

        totalCountry.innerCalculation();


        totalCountry.setOfficialName("Total");
        countryList.add(totalCountry);

        for (Country country : countryList) {
            country.calcGDPPerCapita(totalCountry);
        }

        Collections.sort(countryList);
        int calc = 0;
        for (Country country : countryList) {
                country.GDPPlace = calc;
                calc++;
        }

    }

    private GenericObject readSaveHead(String savePatch) throws IOException {
        //localizationPatch=localPatch;


        //------------------------------------
        // global data loading
        //------------------------------------
        GenericObject eugSave = EUGFileIO.load(savePatch);


        GenericObject foundWorldMarket = eugSave.getChild("worldmarket");

        // price loading
        GenericObject foundPricePool = foundWorldMarket.getChild("price_pool");
        List<ObjectVariable> price_pool = foundPricePool.values;
        for (ObjectVariable iter : price_pool) {
            Product product = new Product(iter.varname, Float.valueOf(iter.getValue()));
            products.add(product);
        }

        // trend loading
        GenericObject foundTrend = foundWorldMarket.getChild("price_change");
        List<ObjectVariable> Trends = foundTrend.values;
        for (ObjectVariable everyTrend : Trends) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everyTrend.varname)) {
                    everyGood.trend = Float.valueOf(everyTrend.getValue());
                    break;
                }
            }
        }

        //MAx Demand loading
        GenericObject foundDem = foundWorldMarket.getChild("demand");
        List<ObjectVariable> Demands = foundDem.values;
        for (ObjectVariable everyDemand : Demands) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everyDemand.varname)) {
                    everyGood.maxDemand = Float.valueOf(everyDemand.getValue());
                    break;
                }
            }
        }

        // affordable demand loading
        GenericObject foundConsump = foundWorldMarket.getChild("real_demand");
        List<ObjectVariable> Consumps = foundConsump.values;
        for (ObjectVariable everyConsump : Consumps) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everyConsump.varname)) {
                    everyGood.affordable = Float.valueOf(everyConsump.getValue());
                    break;
                }
            }
        }

        //global consumption loading WTF ????? 
        GenericObject foundSupply = foundWorldMarket.getChild("actual_sold");
        List<ObjectVariable> Supplies = foundSupply.values;
        for (ObjectVariable everySupply : Supplies) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everySupply.varname)) {
                    everyGood.consumption = Float.valueOf(everySupply.getValue());
                    break;
                }
            }
        }

        // global Supply loading
        GenericObject foundRSupply = foundWorldMarket.getChild("supply_pool");
        List<ObjectVariable> RSupplies = foundRSupply.values;
        for (ObjectVariable everyRSupply : RSupplies) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everyRSupply.varname)) {
                    everyGood.supply = Float.valueOf(everyRSupply.getValue());
                    break;
                }
            }
        }
        // actual_sold_world
        GenericObject foundASW = foundWorldMarket.getChild("actual_sold_world");
        List<ObjectVariable> listASW = foundASW.values;
        for (ObjectVariable everyASW : listASW) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everyASW.varname)) {
                    everyGood.actualSoldWorld = Float.valueOf(everyASW.getValue());
                    break;
                }
            }
        }

        // worldmarket_pool
        GenericObject foundWMP = foundWorldMarket.getChild("worldmarket_pool");
        List<ObjectVariable> listWMP = foundWMP.values;
        for (ObjectVariable everyWMP : listWMP) {
            for (Product everyGood : products) {
                if (everyGood.name.equalsIgnoreCase(everyWMP.varname)) {
                    everyGood.worldmarketPool = Float.valueOf(everyWMP.getValue());
                    break;
                }
            }
        }

        return eugSave;

    }

    /**
     * Should contain EUG file reading only
     */
    private Vic2SaveGameNash readSaveBody(String savePatch, GenericObject eugSave) throws IOException {


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
            GenericObject foundActualSold = countryObject.getChild("saved_country_supply");
            for (ObjectVariable everyGood : foundActualSold.values) {
                Product tempProduct = findProduct(everyGood.varname);
                if (tempProduct != null) {
                    GoodsStorage tstorage = new GoodsStorage(tempProduct);
                    tstorage.savedCountrySupply = Float.valueOf(everyGood.getValue());
                    country.storage.add(tstorage);
                } else System.out.println("Nash: findProduct(everyGood.varname) returned NULL");
            }

            // load max demand
            GenericObject foundMaxDemand = countryObject.getChild("domestic_demand_pool");
            for (ObjectVariable everyGood : foundMaxDemand.values) {
                GoodsStorage storage = country.findStorage(everyGood.varname);
                if (storage != null) {
                    storage.MaxDemand = Float.valueOf(everyGood.getValue());
                } else {
                    storage = new GoodsStorage(findProduct(everyGood.varname));
                    storage.MaxDemand = Float.valueOf(everyGood.getValue());
                    country.storage.add(storage);
                }
            }

            // load internal consumption
            GenericObject foundSold = countryObject.getChild("actual_sold_domestic");
            for (ObjectVariable everyGood : foundSold.values) {
                GoodsStorage storage = country.findStorage(everyGood.varname);
                if (storage != null) {
                    storage.actualSoldDomestic = Float.valueOf(everyGood.getValue());
                    storage.actualDemand = storage.actualSoldDomestic;
                } else {
                    storage = new GoodsStorage(findProduct(everyGood.varname));
                    storage.actualSoldDomestic = Float.valueOf(everyGood.getValue());
                    storage.actualDemand = storage.actualSoldDomestic;
                    country.storage.add(storage);
                }
            }

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
            if(!province.contains("owner")) {
                //todo add to total?
                //ignoring
                continue;
            }
            Country owner = countries.get(province.getString("owner"));
            for (GenericObject object : province.children) {

                // population calculation {
                if (object.contains("size")) {
                    popCount++;
                    //todo why x4
                    try{
                        owner.population += Long.valueOf(object.getString("size")) * 4;
                    } catch (NullPointerException ignored) { }
                    if (object.name.equalsIgnoreCase("farmers") || object.name.equalsIgnoreCase("labourers") || object.name.equalsIgnoreCase("slaves")) {
                        owner.workforceRGO += Long.valueOf(object.getString("size"));
                    } else if (object.name.equalsIgnoreCase("clerks") || object.name.equalsIgnoreCase("craftsmen")) {
                        owner.workforceFactory += Long.valueOf(object.getString("size"));
                    }
                } else {
                    if (object.name.equalsIgnoreCase("RGO")) { // gold income calculation
                        if (object.values.get(1).getValue().equalsIgnoreCase("precious_metal"))
                            owner.goldIncome += Float.valueOf(object.values.get(0).getValue()) / 1000;

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

    private boolean readPrices(String inPatch) {
        boolean result = false;

        System.out.println("Nash: attempt to read " + inPatch + "/common/goods.txt");

        if (inPatch != null && !inPatch.isEmpty()) {


            String targetPatch = inPatch + "/common/goods.txt";

            if (new File(targetPatch).exists()) {

                GenericObject goodsScen = EUGFileIO.load(targetPatch);
                if (goodsScen != null) {
                    List<GenericObject> list = goodsScen.getChild("military_goods").children;
                    for (GenericObject everyGood : list) {
                        Product found = findProduct(everyGood.name);
                        found.basePrice = Float.valueOf(everyGood.values.get(0).getValue());
                        //found.basePrice=Float.valueOf(everyGood..getValue())
                    }

                    list = goodsScen.getChild("raw_material_goods").children;
                    for (GenericObject everyGood : list) {
                        Product found = findProduct(everyGood.name);
                        found.basePrice = Float.valueOf(everyGood.values.get(0).getValue());
                        //found.basePrice=Float.valueOf(everyGood..getValue())
                    }

                    list = goodsScen.getChild("industrial_goods").children;
                    for (GenericObject everyGood : list) {
                        Product found = findProduct(everyGood.name);
                        found.basePrice = Float.valueOf(everyGood.values.get(0).getValue());
                        //found.basePrice=Float.valueOf(everyGood..getValue())
                    }

                    list = goodsScen.getChild("consumer_goods").children;
                    for (GenericObject everyGood : list) {
                        Product found = findProduct(everyGood.name);
                        found.basePrice = Float.valueOf(everyGood.values.get(0).getValue());
                        //found.basePrice=Float.valueOf(everyGood..getValue())
                    }
                    result = true;
                }

            }
        }
        if (!result) System.out.println("Nash: failed to read " + inPatch + "/common/goods.txt");
        return result;

    }

}
