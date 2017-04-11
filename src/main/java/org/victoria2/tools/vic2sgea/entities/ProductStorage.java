package org.victoria2.tools.vic2sgea.entities;

public class ProductStorage extends EconomySubject {
    ProductStorage(Product igood) {
        product = igood;
    }

    public final Product product;

    private float soldDomestic;
    /**
     * Thats for that good in country, in pieces
     */
    private float maxDemand;

    public void add(ProductStorage that) {
        super.add(that);
        maxDemand += that.maxDemand;
        soldDomestic += that.soldDomestic;
    }

    public float getPrice() {
        return product.getPrice();
    }

    public float getSoldDomestic() {
        return soldDomestic;
    }

    public double getActualSupplyPounds() {
        return sold * product.price;
    }

    public double getExportedPounds() {
        return exported * product.price;
    }

    public double getImportedPounds() {
        return imported * product.price;
    }

    public double getActualDemandPounds() {
        return bought * product.price;
    }

    public double getTotalSupplyPounds() {
        return totalSupply * product.price;
    }

    public double getGdpPounds() {
        return gdp * product.price;
    }

    public void innerCalculations() {
        // calculating actual supply
        //todo handle common market
        float thrownToMarket = (totalSupply - soldDomestic);
        if (thrownToMarket <= 0)
            sold = totalSupply;
        else {
            if (product.name.equalsIgnoreCase("precious_metal"))
                sold = totalSupply;
            else if (product.worldmarketPool > 0)
                //todo assuming here that for every country, percentage of goods sold on the world market is the same
                sold = soldDomestic + thrownToMarket * product.actualSoldWorld / product.worldmarketPool;
            else
                sold = soldDomestic;
        }

        gdp += sold;

        //if gdp is positive, it means that less goods were consumed than produced.
        // Since producers consume domestic goods first, no need to change gdp
        //if gdp is negative, it means that more goods were consumed than produced.
        // The remaining were imported, so set gdp to 0
        gdp = Math.max(gdp, 0);

        //calculating import (without wasted)
        imported = Math.max(bought - sold, 0);

        //calculating exported
        if (!product.name.equalsIgnoreCase("precious_metal")) {
            exported = Math.max(sold - bought, 0);//???!!!!
            //exported = 0 + thrownToMarket * product.actualSoldWorld / product.worldmarketPool;
        }

        product.actualSupply += sold;

    }

    public void setSoldDomestic(float soldDomestic) {
        this.soldDomestic = soldDomestic;
    }

    public void setMaxDemand(float maxDemand) {
        this.maxDemand = maxDemand;
    }
}
