package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eug.parser.EUGFileIO;

import eug.shared.GenericObject;
import eug.shared.ObjectVariable;

public class Report {

	public long popCount;

	// Creating a Map so that the number of countries and therefore the number of checks decreases as more countries get names
	private static Map<String, String> countryMap = new HashMap<String, String>(); // TAG and officialname. Like EST and Estonia
	/** Installpath in, list of csv string names out. Half finished, will probably not be completed. */
	public static List<String> ListFiles(String path) throws NullPointerException{

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
		List<String>  locList = new ArrayList<String>();

		String files;
		File folder = new File(path + "/localisation");
		//File folder = new File(path);
		File[] listOfFiles = folder.listFiles();


		for (int i = 0; i < listOfFiles.length; i++) 
		{

			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				if (files.endsWith(".csv") || files.endsWith(".CSV"))
				{
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
	public ArrayList<Product> availableGoods = new ArrayList<Product>();
	public List<ObjectVariable> Head;
	final String closeBracket = "}";
	public ArrayList<Country> countryList = new ArrayList<Country>();

	/**
	 * This is the main reader. It gets a path and reads line by line from it.
	 * If it find a line with a specific keyword, it passes it on to the other
	 * readers in this class. When reading a war, battle or wargoal, the
	 * Processing is set true and all lines are passed to that specific reader
	 * until the Processing is set false. War reading ends when the
	 * bracketCounter is 0 again.
	 * 
	 * @param saveGamePath
	 * @return ArrayList<War>
	 * @throws IOException
	 */

	//String localizationPatch;
	public String modPatch;
	final String openBracket = "{";

	String afterEq(String in) {
		return in.substring(in.indexOf('=') + 1, in.length());
	}
	/*
	 * public void readReference(String line) { Checking the line and if there
	 * is no date Same with start_date
	 * 
	 * if (line.startsWith("date=") && header.getDate().equals("")) { line =
	 * nameExtractor(line, 6, true); header.setDate(line); } Checking if it's
	 * empty is not needed as there is only one line with player= else if
	 * (line.startsWith("player=")) { line = nameExtractor(line, 8, true);
	 * header.setPlayer(line); } else if (line.startsWith("start_date=") &&
	 * header.getStart_date().equals("")) { line = nameExtractor(line, 12,
	 * true); header.setStart_date(line); } }
	 */
	String beforeEq(String in) {
		return in.substring(0, in.indexOf('='));
	}

	/** Reads the given file and for any given line checks if the tag is equal to the
	 * one in countrylist. 
	 * @param filename
	 * @throws IOException
	 */
	private  void CSVReader(String filename) throws IOException {
		/* The same reader as in SaveGameReader */
		//InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "ISO8859_1"); // This encoding seems to work for ö and ü
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "Cp1251"); // This encoding seems to work for ö and ü
		BufferedReader scanner = new BufferedReader(reader);

		String line;
		while ((line = scanner.readLine()) != null) {
			String[] dataArray = line.split(";"); // Splitting the line

			// Checking for every country in map if the first part of line is equal to key
			// If it is, set it as value
			// Every time a match is found, the matching item is removed from the map so next time less checks are required
			Iterator<Entry<String, String>> it = countryMap.entrySet().iterator();
			while (it.hasNext()) { 
				Map.Entry<String, String> entry = it.next();
				if (entry.getKey().equals(dataArray[0])) {
					entry.setValue(dataArray[1]);
					mapCleaner(entry);
					it.remove();

				}
			}

		}
		// Close the file once all data has been read.
		scanner.close();


	}

	/**
	 * Returns an official name for the given tag. If not found, the same tag is
	 * returned (but this is unlikely)
	 */
	public String findOfficalName(String tag) {
		for (Country country : countryList) {
			if (country.getTag().equals(tag)) {
				return country.getOfficialName();
			}
		}
		return tag;

	}

	Country getCountry(String tag)  {
		Country result=new Country(null);
		for (Country everyCountry:countryList){
			if (everyCountry.getTag().equalsIgnoreCase(tag)){
				result=everyCountry;
				break;
			}
		}

		return result;
		// TODO exceptions
	}
	public Country findCountry(String name)  {
		Country result=null;
		for (Country everyCountry:countryList){
			if (everyCountry.getOfficialName().equalsIgnoreCase(name)){
				result=everyCountry;
				break;
			}
		}

		return result;
		// TODO exceptions
	}

	// public SaveHeader header;// = new Reference(); // public so it can be
	// used by all methods

	public Product findProduct(String name) {
		/*int i;
		for (i = 0; i < availableGoods.size() - 1; i++) {
			if (availableGoods.get(i).name.equalsIgnoreCase(name) == true) {
				break;
			}
		}
		return availableGoods.get(i);*/
		for (Product everyProduct :availableGoods)
			if (everyProduct.name.equalsIgnoreCase(name))return everyProduct;
		return null;

		// TODO exceptions
	}
	/** Main method of this class. Manages the reading from csv */
	public void LocalisationReader(String inPatch ) {
		// Emptying the countryMap so all a new savegame is read only with that file's countries in map
		countryMap.clear();
		// Adding the countries to map
		for (Country country: countryList) { 
			countryMap.put(country.getTag(), ""); 
		}
		//			countryMapTemp = new HashMap<countryMap;
		/* Checking if checkbutton is selected. if it is, read localisation */

		//if (getLocalisationCheck().isSelected()) {
		try {
			List<String> loclist = ListFiles(inPatch);
			List<String> modLoclist = null; 


			if (modPatch!=null && !modPatch.isEmpty()){
				if (new File(modPatch).exists()) 
					modLoclist=ListFiles(modPatch);				
			}
			//					CSVReader(Reference.INSTALLPATH + "/localisation/text.csv");
			//					CSVReader(Reference.INSTALLPATH + "/localisation/1.2.csv");
			//					CSVReader(Reference.INSTALLPATH + "/localisation/beta2.csv");
			//					CSVReader(Reference.INSTALLPATH + "/localisation/darkness_3_01.csv");
			//					CSVReader(Reference.INSTALLPATH + "/localisation/housedivided.csv");
			//					CSVReader(Reference.INSTALLPATH + "/localisation/newtext.csv");
			for (String filesForLoad : loclist) {
				CSVReader(inPatch + "/localisation/" + filesForLoad);
				//CSVReader(localizationPatch+"/" + filesForLoad);

			}
			if (modLoclist!=null){
				for (String filesForLoad : modLoclist) {
					CSVReader(modPatch+"/localisation/"+ filesForLoad);
					//CSVReader(localizationPatch+"/" + filesForLoad);

				}
			}

		} catch (NullPointerException | IOException e) {
			//getErrorLabel().setText(getErrorLabel().getText() + " Some or all the of the localisation files could not be found. ");
			System.out.println("Nash: Some or all the of the localisation files could not be loaded");
		}   
		//}

		// If some countries have not been found an official name, setting the tag as official name
		for (Country country: countryList) { 
			if (country.getOfficialName().isEmpty()) {
				country.setOfficialName(country.getTag());
			}
		}	

	}

	/** This method gives the entry value to one of the countries in the countyList.
	 * After this the entry can be removed from map. I think doing it like this is most efficient 
	 * in terms of "if" checks required. */
	synchronized private  void mapCleaner(Map.Entry<String, String> entry) {
		for (Country country: countryList) { 
			if (country.getTag().equals(entry.getKey())) {
				country.setOfficialName(entry.getValue());
			}
		}
	}
	public String nameExtractor(String line, int index, boolean removeLast) {
		StringBuilder sb = new StringBuilder(line);
		sb.delete(0, index);
		if (removeLast) {
			sb.setLength(sb.length() - 1); // Removes last "
		}
		return sb.toString();
	}

	public void process() {

		// calculating real supply (without wasted)		
		for (Country everyCountry : countryList) {	
			if (everyCountry.exist()) everyCountry.innerCalculation();
		}

		// calc global data for goods
		for (Country everyCountry : countryList) {
			if (everyCountry.exist())
				for(GoodsStorage everyStorage:everyCountry.storage){
					Product globalProduct = findProduct(everyStorage.item.name);
					globalProduct.actualBought+=everyStorage.actualSupply;
				}
		} 
		Country totalCountry = new Country("");
		//int foundCountries = 0;
		totalCountry.population=0;
		totalCountry.goldIncome=0;
		totalCountry.employmentRGO=0;
		totalCountry.workforceRGO=0;
		for (Country iterator : countryList) { // get summ
			if (iterator.exist()) {

				//iterator.actualSupply+=iterator.goldIncome;
				/*totalCountry.export+=iterator.export;
				totalCountry.actualSupply += iterator.actualSupply;
				totalCountry.actualDemand +=  iterator.actualDemand;
				totalCountry.imported+=iterator.imported;
				 */
				totalCountry.population += iterator.population;				
				totalCountry.goldIncome += iterator.goldIncome;
				totalCountry.employmentRGO += iterator.employmentRGO;
				totalCountry.employmentFactory+=iterator.employmentFactory;
				totalCountry.workforceRGO += iterator.workforceRGO;
				totalCountry.workforceFactory+=iterator.workforceFactory;

				for (GoodsStorage everyStorage:iterator.storage){
					GoodsStorage foundStorage=totalCountry.findStorage(everyStorage.item.name);
					if (foundStorage==null){
						foundStorage = new GoodsStorage(everyStorage.item);	
						totalCountry.storage.add(foundStorage);
					}


					foundStorage.actualDemand+=everyStorage.actualDemand;
					foundStorage.actualSoldDomestic+=everyStorage.actualSoldDomestic;
					foundStorage.actualSupply+=everyStorage.actualSupply;
					foundStorage.export+=everyStorage.export;
					foundStorage.imported+=everyStorage.imported;
					foundStorage.MaxDemand+=everyStorage.MaxDemand;
					foundStorage.savedCountrySupply+=everyStorage.savedCountrySupply;				

				}
			}
		}
		/*//totalCountry.setOfficialName("Total  " + Integer.toString(foundCountries));
		//if (totalCountry.population>0){
		totalCountry.GDPPerCapita=totalCountry.actualSupply / totalCountry.population *100000;
		totalCountry.unemploymentProcent=(float)(totalCountry.workforce-totalCountry.employment)*4/totalCountry.population*100;
		//}
		//else{
		//	totalCountry.GDPPerCapita=Float.NaN;
		//	totalCountry.unemploymentProcent=Float.NaN;
		//}
		 * 
		 */
		totalCountry.innerCalculation();
		totalCountry.export=0;
		totalCountry.imported=0;
		totalCountry.actualSupply=0;
		for (Country everyCountry : countryList) {
			if (everyCountry .exist()){
				totalCountry.export+=everyCountry.export;
				totalCountry.imported+=everyCountry.imported;
				totalCountry.actualSupply+=everyCountry.actualSupply;
			}
		}	
		totalCountry.calcGDPPerCapita(totalCountry);

		for (Country iterator : countryList) {
			if (iterator.exist())
				iterator.calcGDPPerCapita(totalCountry);
		}

		totalCountry.setOfficialName("Total");
		countryList.add(totalCountry);


		Collections.sort(countryList);
		int calc = 0;
		for (int i = 0; i < countryList.size(); i++){

			if (countryList.get(i).population>0)	{
				countryList.get(i).GDPPlace = calc;
				calc++;
			}
		}	

	}
	public GenericObject readSaveHead(String savePatch) throws IOException {
		//localizationPatch=localPatch;


		//------------------------------------
		// global data loading
		//------------------------------------
		GenericObject eugSave = EUGFileIO.load(savePatch);


		GenericObject foundWorldMarket = eugSave.getChild("worldmarket");

		// price loading
		GenericObject  foundPricePool = foundWorldMarket .getChild("price_pool");
		List<ObjectVariable> price_pool = foundPricePool.values;
		for (ObjectVariable iter : price_pool) {
			Product tempGood = new Product(iter.varname, Float.valueOf(iter.getValue()));
			availableGoods.add(tempGood);
		}

		// trend loading
		GenericObject  foundTrend= foundWorldMarket.getChild("price_change");
		List<ObjectVariable> Trends = foundTrend.values;
		for (ObjectVariable everyTrend : Trends) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everyTrend.varname)){					
					everyGood.trend=Float.valueOf(everyTrend.getValue());
					break;
				}
			}			
		}

		//MAx Demand loading
		GenericObject  foundDem= foundWorldMarket.getChild("demand");
		List<ObjectVariable> Demands = foundDem.values;
		for (ObjectVariable everyDemand: Demands) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everyDemand.varname)){					
					everyGood.maxDemand=Float.valueOf(everyDemand.getValue());
					break;
				}
			}			
		}

		// affordable demand loading
		GenericObject  foundConsump= foundWorldMarket.getChild("real_demand");
		List<ObjectVariable> Consumps = foundConsump.values;
		for (ObjectVariable everyConsump: Consumps) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everyConsump.varname)){					
					everyGood.affordable=Float.valueOf(everyConsump.getValue());
					break;
				}
			}			
		}

		//global consumption loading WTF ????? 
		GenericObject  foundSupply= foundWorldMarket.getChild("actual_sold");
		List<ObjectVariable> Supplies = foundSupply.values;
		for (ObjectVariable everySupply: Supplies) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everySupply.varname)){					
					everyGood.consumption=Float.valueOf(everySupply.getValue());
					break;
				}
			}			
		}

		// global Supply loading
		GenericObject  foundRSupply= foundWorldMarket.getChild("supply_pool");
		List<ObjectVariable> RSupplies = foundRSupply.values;
		for (ObjectVariable everyRSupply: RSupplies) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everyRSupply.varname)){					
					everyGood.supply=Float.valueOf(everyRSupply.getValue());
					break;
				}
			}			
		}
		// actual_sold_world
		GenericObject  foundASW= foundWorldMarket.getChild("actual_sold_world");
		List<ObjectVariable> listASW= foundASW.values;
		for (ObjectVariable everyASW: listASW) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everyASW.varname)){					
					everyGood.actualSoldWorld=Float.valueOf(everyASW.getValue());
					break;
				}
			}			
		}

		// worldmarket_pool
		GenericObject  foundWMP= foundWorldMarket.getChild("worldmarket_pool");
		List<ObjectVariable> listWMP= foundWMP.values;
		for (ObjectVariable everyWMP: listWMP) {			
			for (Product everyGood : availableGoods) {
				if (everyGood.name.equalsIgnoreCase(everyWMP.varname)){					
					everyGood.worldmarketPool=Float.valueOf(everyWMP.getValue());
					break;
				}
			}			
		}	
		return eugSave;

	}
	/**Should contain EUG file reading only */ 
	public Vic2SaveGameNash readSaveBody(String savePatch,GenericObject eugSave) throws IOException {



		System.out.println("Nash: openning Vic2SaveGame...  free memory is "+Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
		Vic2SaveGameNash save = new Vic2SaveGameNash(eugSave, savePatch,"", "");


		System.out.println("Nash: preload Provinces...  free memory is "+Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
		save.preloadProvinces();
		System.out.println("Nash: preload Countries...  free memory is "+Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
		save.preloadCountries();
		System.out.println("Nash: preload States...  free memory is "+Wrapper.toKMG(Runtime.getRuntime().freeMemory()));
		save.preloadStates();


		//------------------------------------
		// country data loading
		//------------------------------------

		for (GenericObject everyCountry : save.getCountries()) {
			Country CountryForAdd = new Country(everyCountry.name);

			// load savedCountrySupply (ts)
			GenericObject foundActualSold = everyCountry.getChild("saved_country_supply"); 
			for (ObjectVariable everyGood : foundActualSold.values) {
				Product tempProduct = findProduct(everyGood.varname);
				if (tempProduct !=null){
					GoodsStorage tstorage = new GoodsStorage(tempProduct);
					tstorage.savedCountrySupply=Float.valueOf(everyGood.getValue());
					CountryForAdd.storage.add(tstorage);
				}
				else System.out.println("Nash: findProduct(everyGood.varname) returned NULL");
			}

			// load max demand
			GenericObject foundMaxDemand= everyCountry.getChild("domestic_demand_pool");
			for (ObjectVariable everyGood : foundMaxDemand.values){
				GoodsStorage storage=CountryForAdd.findStorage(everyGood.varname);
				if (storage!=null){					
					storage.MaxDemand=Float.valueOf(everyGood.getValue());
				}
				else {
					storage= new GoodsStorage(findProduct(everyGood.varname));
					storage.MaxDemand=Float.valueOf(everyGood.getValue());					
					CountryForAdd.storage.add(storage);	
				}
			}

			// load internal consumption
			GenericObject foundSold= everyCountry.getChild("actual_sold_domestic");
			for (ObjectVariable everyGood : foundSold.values){
				GoodsStorage storage=CountryForAdd.findStorage(everyGood.varname);
				if (storage!=null){
					storage.actualSoldDomestic=Float.valueOf(everyGood.getValue());				
					storage.actualDemand=storage.actualSoldDomestic;								
				}
				else{					
					storage= new GoodsStorage(findProduct(everyGood.varname));
					storage.actualSoldDomestic=Float.valueOf(everyGood.getValue());				
					storage.actualDemand=storage.actualSoldDomestic;
					CountryForAdd.storage.add(storage);					
				}
			}





			//calculate real demand 
			/*GenericObject foundDemand = everyCountry.getChild("actual_sold_domestic"); 
			for (ObjectVariable everyGood : foundDemand.values) {
				//= new Product(everyGood.varname, Float.valueOf(everyGood.getValue()));
				//Product tempGood=findProduct(everyGood.varname);

				GoodsStorage storage=CountryForAdd.findStorage(everyGood.varname);
				if (storage!=null){
					//storage.actualSoldDomestic=Float.valueOf(everyGood.getValue());				
					//storage.actualDemand=storage.actualSoldDomestic;
					float importDesire=(storage.MaxDemand-storage.actualSupply);
					float worldM = storage.item.actualSoldWorld/storage.item.worldmarketPool;
					if (worldM>=1) // enough goods on market
						storage.actualDemand=Float.valueOf(everyGood.getValue())+importDesire;
					else   //not enought 
						storage.actualDemand=Float.valueOf(everyGood.getValue())+importDesire*worldM;


				}
				//else System.out.println("Nash: findStorage(everyGood.varname) returned NULL");
			}*/
			/*for (GoodsStorage storage:CountryForAdd.storage){
				float importDesire=(storage.MaxDemand-storage.actualSupply);
				float worldM = storage.item.worldmarketPool/storage.item.actualSoldWorld;
				if (worldM>=1) // enough goods on market
					storage.actualDemand=importDesire*worldM;
				else   //not enought 
					storage.actualDemand=storage.actualDemand+importDesire*worldM;
			}*/

			/*// load actual demand
			//GenericObject foundDemand = everyCountry.getChild("domestic_demand_pool"); // it is total demand
			GenericObject foundDemand = everyCountry.getChild("actual_sold_domestic"); // real consumption
			for (ObjectVariable everyGood : foundDemand.values) {
				//= new Product(everyGood.varname, Float.valueOf(everyGood.getValue()));
				Product tempGood=findProduct(everyGood.varname);
				CountryForAdd.actualDemand+=tempGood.price*Float.valueOf(everyGood.getValue());
				//CountryForAdd.actualDemand+=Float.valueOf(everyGood.getValue());
				//CountryForAdd.storage.add(tstorage);				
			}	*/		


			List<ObjectVariable> ghr;
			for (GenericObject everyState:everyCountry.children){
				if (everyState.name.equalsIgnoreCase("state"))
					for (GenericObject everyBuilding: everyState.getChildren("state_buildings")){
						List<GenericObject> currentLevel=everyBuilding.getChildren("employment");
						if (currentLevel!=null && currentLevel.size()>0){
							currentLevel=currentLevel.get(0).getChildren("employees");
							if (currentLevel!=null && currentLevel.size()>0){
								for (GenericObject everyEmpl:currentLevel.get(0).children){
									ghr= everyEmpl.values;
									if (ghr!=null && ghr.size()>0 )CountryForAdd.employmentFactory+=Long.valueOf(ghr.get(0).getValue());

									this.getClass();
								}
							}
						}
						/* for(GenericObject everyEmpl:everyBuilding.getChildren("employment").get(0).getChildren("employees").get(0).children){
						 this.getClass();
					 }*/
						/*everyBuilding.getChildren("employment").get(0).getChildren("employees");
					 everyBuilding.getChildren("employees")
					 this.getClass();*/
					}

			}

			countryList.add(CountryForAdd);
		}










		Head = eugSave.values;


		return save;
	}

	public void populationProcess(Vic2SaveGameNash save){
		Country tempCountry;
		popCount=0;

		for (GenericObject province:save.provinces.values()	){			
			tempCountry=getCountry(province.getString("owner"));
			for (GenericObject POP: province.children){

				// population calculation {
				if (POP.contains("size")) {
					popCount++;
					tempCountry.population+= Long.valueOf(POP.getString("size"))*4;
					if (POP.name.equalsIgnoreCase("farmers")||POP.name.equalsIgnoreCase("labourers")||POP.name.equalsIgnoreCase("slaves")){
						tempCountry.workforceRGO+=Long.valueOf(POP.getString("size"));						 
					}else 
						if (POP.name.equalsIgnoreCase("clerks")||POP.name.equalsIgnoreCase("craftsmen")){
							tempCountry.workforceFactory+=Long.valueOf(POP.getString("size"));
						}
				} else{
					if (POP.name.equalsIgnoreCase("RGO")){ // gold income calculation
						if (POP.values.get(1).getValue().equalsIgnoreCase("precious_metal"))
							tempCountry.goldIncome+= Float.valueOf(POP.values.get(0).getValue())/1000;
						//if (POP.children.get(0).children.get(0).children!=null)
						if (POP.children!=null)
							if (POP.children.size()>0)
								if (POP.children.get(0).children!=null)
									if (POP.children.get(0).children.size()>0)
										if (POP.children.get(0).children.get(0).children!=null)
											for (GenericObject worker:POP.children.get(0).children.get(0).children){
												if (worker.values!=null)
													if (worker.values.size()>0)
														tempCountry.employmentRGO+=Integer.valueOf(worker.values.get(0).getValue());								 
											}
					}

				}		
			}
		}
	}
	public boolean readPrices(String inPatch){
		boolean result=false;

		System.out.println("Nash: attempt to read "+ inPatch+"/common/goods.txt");

		if (inPatch!=null && !inPatch.isEmpty()){


			String targetPatch=inPatch+"/common/goods.txt";

			if (new File(targetPatch).exists()){

				GenericObject goodsScen = EUGFileIO.load(targetPatch);
				if (goodsScen!=null){ 
					List<GenericObject> list=goodsScen.getChild("military_goods").children;
					for (GenericObject everyGood:list){
						Product found= findProduct(everyGood.name);
						found.basePrice=Float.valueOf(everyGood.values.get(0).getValue());						
						//found.basePrice=Float.valueOf(everyGood..getValue())
					}

					list=goodsScen.getChild("raw_material_goods").children;
					for (GenericObject everyGood:list){
						Product found= findProduct(everyGood.name);
						found.basePrice=Float.valueOf(everyGood.values.get(0).getValue());						
						//found.basePrice=Float.valueOf(everyGood..getValue())
					}

					list=goodsScen.getChild("industrial_goods").children;
					for (GenericObject everyGood:list){
						Product found= findProduct(everyGood.name);
						found.basePrice=Float.valueOf(everyGood.values.get(0).getValue());						
						//found.basePrice=Float.valueOf(everyGood..getValue())
					}

					list=goodsScen.getChild("consumer_goods").children;
					for (GenericObject everyGood:list){
						Product found= findProduct(everyGood.name);
						found.basePrice=Float.valueOf(everyGood.values.get(0).getValue());						
						//found.basePrice=Float.valueOf(everyGood..getValue())
					}
					result = true;
				}

			}
		}
		if (!result)System.out.println("Nash: failed to read "+ inPatch+"/common/goods.txt");
		return result;

	}

}
