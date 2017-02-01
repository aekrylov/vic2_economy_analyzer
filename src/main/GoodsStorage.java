package main;

import java.util.Comparator;


//public class GoodsStorage implements Comparable<GoodsStorage>{
public class GoodsStorage{
	public GoodsStorage(Product igood ) {
		item = igood;	
		
	}
	/** temp*/
	public float worldmarketPool;
	public float actualSoldWorld;
	
	public Product item;
	/**It is real production for that good in country, without unsold goods, in pieces. Used as GDP*/
	public  float actualSupply;
	/** It is total supply for that good in country (including unsold), in pieces*/
	public float savedCountrySupply;
	/**Its total consumed for that good in country, including import?, in pieces same as actualDemand*/
	public float actualSoldDomestic;
	/**Thats for that good in country, in pieces*/
	public float actualDemand;
	/**Thats for that good in country, in pieces*/
	public float export;
	/**Thats for that good in country, in pieces*/
	public float imported;
	public float MaxDemand;
	/*@Override
	public int compareTo(GoodsStorage in) {
		int result=0;
		
		if (actualSupply*item.price==in.actualSupply*in.item.price) result=0;
		else if (in.actualSupply*in.item.price>actualSupply*item.price)result=1;
		else if (in.actualSupply*in.item.price<actualSupply*item.price)result=-1;
		return result;
	} */
	public static class actualSupplySort implements Comparator<GoodsStorage>{

		@Override
		public int compare(GoodsStorage arg0, GoodsStorage arg1) {
			int result=0;			
			if (arg0.actualSupply*arg0.item.price==arg1.actualSupply*arg1.item.price) result=0;
			else if (arg1.actualSupply*arg1.item.price>arg0.actualSupply*arg0.item.price)result=1;
			else if (arg1.actualSupply*arg1.item.price<arg0.actualSupply*arg0.item.price)result=-1;
			return result;
		}
		
	}
	public static class exportSort implements Comparator<GoodsStorage>{

		@Override
		public int compare(GoodsStorage arg0, GoodsStorage arg1) {
			int result=0;			
			if (arg0.export*arg0.item.price==arg1.export*arg1.item.price) result=0;
			else if (arg1.export*arg1.item.price>arg0.export*arg0.item.price)result=1;
			else if (arg1.export*arg1.item.price<arg0.export*arg0.item.price)result=-1;
			return result;
		}
		
	}
	public static class actualDemandSort implements Comparator<GoodsStorage>{

		@Override
		public int compare(GoodsStorage arg0, GoodsStorage arg1) {
			int result=0;			
			if (arg0.actualDemand*arg0.item.price==arg1.actualDemand*arg1.item.price) result=0;
			else if (arg1.actualDemand*arg1.item.price>arg0.actualDemand*arg0.item.price)result=1;
			else if (arg1.actualDemand*arg1.item.price<arg0.actualDemand*arg0.item.price)result=-1;
			return result;
		}
		
	}
	public static class importedSort implements Comparator<GoodsStorage>{

		@Override
		public int compare(GoodsStorage arg0, GoodsStorage arg1) {
			int result=0;			
			if (arg0.imported*arg0.item.price==arg1.imported*arg1.item.price) result=0;
			else if (arg1.imported*arg1.item.price>arg0.imported*arg0.item.price)result=1;
			else if (arg1.imported*arg1.item.price<arg0.imported*arg0.item.price)result=-1;
			return result;
		}
		
	}
	public static class savedCountrySupplySort implements Comparator<GoodsStorage>{

		@Override
		public int compare(GoodsStorage arg0, GoodsStorage arg1) {
			int result=0;			
			if (arg0.savedCountrySupply*arg0.item.price==arg1.savedCountrySupply*arg1.item.price) result=0;
			else if (arg1.savedCountrySupply*arg1.item.price>arg0.savedCountrySupply*arg0.item.price)result=1;
			else if (arg1.savedCountrySupply*arg1.item.price<arg0.savedCountrySupply*arg0.item.price)result=-1;
			return result;
		}
		
	}
	
}
