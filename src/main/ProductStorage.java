package main;

public class ProductStorage extends EconomySubject {
    public ProductStorage(Product igood) {
        product = igood;

    }

    public final Product product;
    /**It is real production for that good in country, without unsold goods, in pieces. Used as GDP*/
	public float actualSoldDomestic;
	/**Thats for that good in country, in pieces*/
	public float MaxDemand;

    public float getPrice() {
        return product.getPrice();
    }

    public float getActualSoldDomestic() {
        return actualSoldDomestic;
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

    public double getTotalSupplyPounds() {
        return totalSupply * product.price;
    }
	
}
