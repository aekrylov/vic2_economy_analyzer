package main;

/**
 * @author nash
 *         This is static-alike class for report
 */
public class Product {
    /**
     * global
     */
    public float price = 0;
    /**
     * global, in pieces
     */
    public float consumption;
    /**
     * global
     */
    public float supply;
    /**
     * global, in pieces
     */
    public float affordable;
    /**
     * global, in pieces
     */
    public float maxDemand;
    /**
     * global, in pieces
     */
    public float basePrice;
    /**
     * global
     */
    //todo
    public float trend;
    /**
     * global
     */
    public float actualBought;
    /**
     * global, in pieces, how much was thrown to global market
     */
    public float worldmarketPool;
    /**
     * global, in pieces
     */
    public float actualSoldWorld;
    String name = "";

    public Product(String iname, float iprice) {
        super();
        this.name = iname;
        this.price = iprice;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public long getConsumption() {
        long result;
        if (supply >= consumption && consumption <= affordable)
            if (supply <= affordable)
                result = (long) supply;
            else
                result = (long) affordable;
        else
            result = (long) consumption;
        return result;
        //return (long)consumption;

    }

    public long getRealSupply() {
        return (long) supply;
    }

    public long getAffordable() {
        return (long) affordable;
    }

    public long getMaxDemand() {
        return (long) maxDemand;
    }

    public long getActualBought() {
        return (long) actualBought;
    }

    public float getBasePrice() {
        return basePrice;
    }

    public float getMinPrice() {
        return basePrice / 5;
    }

    public float getMaxPrice() {
        return basePrice * 5;
    }

    public float getInflation() {
        return price / basePrice * 100;
    }

    public String getTrend() {
        String result = null;
        if (trend < 0) result = "DOWN";
        if (trend > 0) result = "UP";
        if (trend == 0) result = "";
        return result;
    }

    public float getOverproduced() {
        return supply / affordable * 100;
    }

}
