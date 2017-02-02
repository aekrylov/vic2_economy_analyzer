package main;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ProductStorage {
    public ProductStorage(Product igood) {
        product = igood;

    }
	/** temp*/
	public float worldmarketPool;
	public float actualSoldWorld;

    public Product product;
    /**It is real production for that good in country, without unsold goods, in pieces. Used as GDP*/
	public  float actualSupply;
	/** It is total supply for that good in country (including unsold), in pieces*/
	public float savedCountrySupply;
	/**Its total consumed for that good in country, including import?, in pieces same as actualDemand*/
	public float actualSoldDomestic;
	/**Thats for that good in country, in pieces*/
	public float actualDemand;
	/**Thats for that good in country, in pieces*/
    public float exported;
    /**Thats for that good in country, in pieces*/
	public float imported;
	public float MaxDemand;

    private static Map<String, Comparator<ProductStorage>> comparators = new HashMap<>();

    static {
        comparators.put("actualSupply", Comparator.comparing(ProductStorage::getActualSupplyPounds));
        comparators.put("exported", Comparator.comparing(ProductStorage::getExportedPounds));
        comparators.put("imported", Comparator.comparing(ProductStorage::getImportedPounds));
        comparators.put("actualDemand", Comparator.comparing(ProductStorage::getActualDemandPounds));
        comparators.put("savedCountrySupply", Comparator.comparing(ProductStorage::getSavedSupplyPounds));
    }

    public static Comparator<ProductStorage> getComparator(String field) {
        return comparators.get(field);
    }

    public double getActualSupplyPounds() {
        return actualSupply * product.price;
    }

    public double getExportedPounds() {
        return exported * product.price;
    }

    public double getImportedPounds() {
        return imported * product.price;
    }

    public double getActualDemandPounds() {
        return actualDemand * product.price;
    }

    public double getSavedSupplyPounds() {
        return savedCountrySupply * product.price;
    }
	
}
