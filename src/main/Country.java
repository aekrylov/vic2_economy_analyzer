package main;

import java.util.ArrayList;


import javafx.scene.image.Image;

/** Holds the flag and a longer name than just three letters.   
 * There will only be one instance of this class for each country as opposed to <code>JoinedCountry</code>, 
 * which has many instances.*/
public class Country implements Comparable<Country>{
	/**Summ for country in pounds*/
	public float actualDemand;
	
	/**Summ for country in pounds*/
	public float actualSupply;
	
	
	
	/**Summ for country in pounds*/
	public long export;
	
	private Image flag;
	
	/**Summ for country in pounds*/
	float GDPPart;
	
	/**Summ for country in pounds*/
	float GDPPerCapita;
	
	/**Summ for country */
	int GDPPlace;
	
	/**Summ for country in pounds*/
	float goldIncome;
	
	/**Summ for country in pounds*/
	public long imported;
	
	
	
	/**Summ for country in pounds*/
	public long population;
	
	/** temp*/
	public float savedCountrySupply;
	
	/**Summ for country*/
	public long employmentFactory;
	
	/**Summ for country*/
	public long employmentRGO;
	
	public float unemploymentProcent;
	/**Summ for country */	
	
	public long workforceFactory;
	
	/**Summ for country */	
	public long workforceRGO;
	
	
	protected String officialName="";
	ArrayList<Province> owned= new ArrayList<Province>();
	
	public ArrayList<GoodsStorage> storage= new ArrayList<GoodsStorage>();
	
	private String tag;
	/**Summ for country in pounds*/
	
	
	public Country(String tag) {
		super();
		this.tag = tag;
	}

	public void calcGDPPerCapita(Country totalCountry ) {
		//iterator.actualSupply+=iterator.goldIncome;
		//if (totalCountry.actualSupply>0)
		GDPPart = actualSupply / (float)totalCountry.actualSupply * 100;
		//else
		//	iterator.GDPPart =Float.NaN;
		if (GDPPart < 0.001)GDPPart = 0;
		//if (iterator.population>0){
		GDPPerCapita=actualSupply / (float)population *100000;
		
		//}else{
		//	iterator.GDPPerCapita=Float.NaN;
		//	iterator.unemploymentProcent=Float.NaN;		
		//}

	}


	@Override
	public int compareTo(Country inCountry) {
		int result=0;
		if (actualSupply==inCountry.actualSupply) result=0;
		else if (inCountry.actualSupply>actualSupply)result=1;
		else if (inCountry.actualSupply<actualSupply)result=-1;
		return result;
	}
	public boolean exist(){
		if (population>0)
			return true;
		return false;
	}	
	public GoodsStorage findStorage(String name) {
		/*int i;
		for (i = 0; i < storage.size() - 1; i++) {
			if (storage.get(i).item.name.equalsIgnoreCase(name) == true) {
				break;
			}
		}
		return storage.get(i);*/
		//GoodsStorage result; 
		for (GoodsStorage everyStorage : storage){
			if (everyStorage.item.name.equalsIgnoreCase(name)){
				return everyStorage;
			}

		}
		return null;	
		// TODO exceptions
	}
	public long getActualDemand() {


		return (long)(actualDemand);
		//return actualDemand;

	}
	/**
	 * @return Use it only for tableView!!!!
	 */
	public String getActualSupply() {
		return Wrapper.toKMG((long)actualSupply);	

	}
	public long getEmployment() {
		return employmentRGO;
	}
	public long getExport() {
		return export;
	}
	public Image getFlag() {
		return flag;
	}
	public String getGDPPartTV() {
		return Wrapper.toPercentage(GDPPart);
	}
	/*public float getActualSupply() {
		return actualSupply;	

	}*/
	public String getGDPPerCapitaTV() {
		return Wrapper.toKMG(GDPPerCapita);

	}
	public int getGDPPlace() {
		return GDPPlace;
	}
	public long getGoldIncome() {
		return (long)goldIncome;
	}
	public long getImported() {
		return imported;
	}
	public String getOfficialName() {
		if (officialName.startsWith(" ")){
			return officialName.substring(1);
		}
		else
			return officialName;
	}
	public String getTag() {
		return tag;
	}
	/*public long getPopulation() {
		return population;
	}*/
	public String getPopulationTV() {
		//return Wrapper.formatKMG(String.valueOf(population),10);
		//return MetricPrefix.getResult(1234223.67).getValue();
		return Wrapper.toKMG(population);
	}
	public String getUnemploymentProcentTV() {
		return Wrapper.toPercentage(unemploymentProcent);
	}
	public float getUnemploymentProcentFactory() {
		return (workforceFactory-employmentFactory)*4/(float)population*100;
	}
	public String getUnemploymentProcentFactoryTV() {
		return Wrapper.toPercentage(getUnemploymentProcentFactory());
	}
	public long getWorkforce() {
		return workforceRGO;
	}
	//ArrayList<int> provinceList= new ArrayList<int>(); 
	/**Calculate inside-country data*/
	void innerCalculation()	{
				
		for (GoodsStorage everyStorage:storage){
			
			// calculating real supply 
			float thrownToMarket=(everyStorage.savedCountrySupply-everyStorage.actualSoldDomestic);
			
			if (thrownToMarket<=0)everyStorage.actualSupply=everyStorage.savedCountrySupply;
			else{
				if (everyStorage.item.name.equalsIgnoreCase("precious_metal"))
					everyStorage.actualSupply=everyStorage.savedCountrySupply;
				else
					if (everyStorage.item.worldmarketPool>0)
						everyStorage.actualSupply=everyStorage.actualSoldDomestic+thrownToMarket*everyStorage.item.actualSoldWorld/everyStorage.item.worldmarketPool;
					else
						everyStorage.actualSupply=everyStorage.actualSoldDomestic;
			}
			//calculating import (without wasted)
			everyStorage.imported=everyStorage.actualDemand-everyStorage.actualSupply;

			if (everyStorage.imported<0)everyStorage.imported=0;
			
			//calculating export
			float exp=0;
			if (!everyStorage.item.name.equalsIgnoreCase("precious_metal")){
				exp=(everyStorage.actualSupply-everyStorage.actualDemand);//???!!!!
				if (exp>0){					
					everyStorage.export=exp;
				}
				else everyStorage.export=0;
			}
			//
			everyStorage.actualSoldWorld=everyStorage.item.actualSoldWorld;
			everyStorage.worldmarketPool=everyStorage.item.worldmarketPool;
		}

		// calculating total data for country ()
		
		unemploymentProcent=(workforceRGO-employmentRGO)*4/(float)population*100;
		
		actualSupply=0;
		actualDemand=0;
		imported=0;
		export=0;
		for (GoodsStorage everyGoods:storage){
			actualSupply += everyGoods.actualSupply * everyGoods.item.price;
			actualDemand += everyGoods.actualDemand * everyGoods.item.price;
			imported+=everyGoods.imported * everyGoods.item.price;
			export+=everyGoods.export * everyGoods.item.price;

		}		
	}
	public void setFlag(Image flag) {
		this.flag = flag;
	}
	public void setOfficialName(String officialName) {
		this.officialName = officialName;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}



	@Override
	public String toString() {
		return "Country [tag=" + tag + ", officialName=" + officialName
				+ ", flag=" + flag + "]";
	}

}
