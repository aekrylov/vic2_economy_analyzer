package org.victoria2.tools.vic2sgea.main;

public class ProductStorage extends EconomySubject {
    public ProductStorage(Product igood) {
        product = igood;
    }

    public final Product product;
    /**
     * It is real production for that good in country, without unsold goods, in pieces. Used as GDP
     */
    public float actualSoldDomestic;
    /**
     * Thats for that good in country, in pieces
     */
    public float maxDemand;

    public void add(ProductStorage that) {
        super.add(that);
        maxDemand += that.maxDemand;
        actualSoldDomestic += that.actualSoldDomestic;
    }

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

    public double getGdpPounds() {
        return gdp * product.price;
    }

    public void innerCalculations() {
        // calculating actual supply
        float thrownToMarket = (totalSupply - actualSoldDomestic);
        if (thrownToMarket <= 0)
            actualSupply = totalSupply;
        else {
            if (product.name.equalsIgnoreCase("precious_metal"))
                actualSupply = totalSupply;
            else if (product.worldmarketPool > 0)
                //todo assuming here that for every country, percentage of goods sold on the world market is the same
                actualSupply = actualSoldDomestic + thrownToMarket * product.actualSoldWorld / product.worldmarketPool;
            else
                actualSupply = actualSoldDomestic;
        }

        //todo
        gdp += actualSupply;

        //calculating import (without wasted)
        imported = Math.max(actualDemand - actualSupply, 0);

        //calculating exported
        if (!product.name.equalsIgnoreCase("precious_metal")) {
            exported = Math.max(actualSupply - actualDemand, 0);//???!!!!
        }

    }

    public void setActualSoldDomestic(float actualSoldDomestic) {
        this.actualSoldDomestic = actualSoldDomestic;
    }

    public void setMaxDemand(float maxDemand) {
        this.maxDemand = maxDemand;
    }
}
