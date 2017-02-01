package main;

import java.util.Comparator;

/**
 * @author nash
 * This is static-alike class for report
 *
 */
public class Product {
	String name="";	
	public String getName() {
		return name;
	}
	public float getPrice() {
		return price;
	}
	public long getConsumption() {
		long result=0;
		if (supply>=consumption && consumption<=affordable)
			if (supply<=affordable)
				result=(long)supply;
			else
				result=(long)affordable;	
		else
			result=(long)consumption;
		return result;
		//return (long)consumption;
		
	}
	public long getRealSupply() {
		return (long)supply;
	}
	public long getAffordable() {
		return (long)affordable;
	}
	public long getMaxDemand() {
		return (long)maxDemand;
	}
	public long getActualBought() {
		return (long)actualBought;
	}
	
	public float getBasePrice() {
		return basePrice;
	}
	public float getMinPrice() {
		return basePrice/5;
	}
	public float getMaxPrice() {
		return basePrice*5;
	}
	public float getInflation() {
		return price/basePrice*100;
	}
	public String getTrend() {
		String result = null;
		if (trend<0)result="DOWN";
		if (trend>0)result="UP";
		if (trend==0)result="";
		return result;
	}
	public static class NameComparator implements Comparator<Product>{

		@Override
		public int compare(Product first, Product second) {
			//int result=0;
			return first.name.compareTo(second.name);
			
			//return result;
		}
		
	}
	public float getOverproduced() {
		return supply/affordable*100;
	}
	/**global*/
	public float price=0;
	/**global, in pieces*/
	public float consumption;
	/**global*/
	public float supply;
	/**global, in pieces*/
	public float affordable;
	/**global, in pieces*/
	public float maxDemand;
	/**global, in pieces*/
	public float basePrice;
	/**global*/
	public float trend;
	/**global*/
	public float actualBought;
	/**global, in pieces, how much was throwed to global market*/
	public float worldmarketPool;
	/**global, in pieces*/
	public float actualSoldWorld;
	//float minPrice;
	//float maxPrice;
	// trend & OVERPRoduced make like function
	public Product(String iname, float iprice) {
		super();
		this.name= iname;
		this.price= iprice;		
	}
}
