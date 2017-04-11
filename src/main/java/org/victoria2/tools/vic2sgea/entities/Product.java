package org.victoria2.tools.vic2sgea.entities;

import javafx.scene.paint.Color;

/**
 * @author nash
 *         This is static-alike class for report
 */
public class Product implements Comparable<Product> {

    /**
     * global
     */
    public float price;
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
    public float demand;
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
    private float trend;
    /**
     * global
     */
    public float actualSupply;
    /**
     * How much was thrown to the world market
     */
    float worldmarketPool;
    /**
     * How much was sold on the world market
     */
    float actualSoldWorld;
    final String name;

    private Color color;

    public Product(String name) {
        this.name = name;
    }

    public Product(String name, float basePrice) {
        this.name = name;
        this.basePrice = basePrice;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public float getConsumption() {
        return consumption;
    }

    public float getSupply() {
        return supply;
    }

    public float getDemand() {
        return demand;
    }

    public float getMaxDemand() {
        return maxDemand;
    }

    public float getActualSupply() {
        return actualSupply;
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
        return supply / demand * 100;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setConsumption(float consumption) {
        this.consumption = consumption;
    }

    public void incConsumption(float value) {
        consumption += value;
    }

    public void setSupply(float supply) {
        this.supply = supply;
    }

    public void setDemand(float demand) {
        this.demand = demand;
    }

    public void setMaxDemand(float maxDemand) {
        this.maxDemand = maxDemand;
    }

    public void setBasePrice(float basePrice) {
        this.basePrice = basePrice;
    }

    public void setTrend(float trend) {
        this.trend = trend;
    }

    public void setActualSupply(float actualSupply) {
        this.actualSupply = actualSupply;
    }

    public void setWorldmarketPool(float worldmarketPool) {
        this.worldmarketPool = worldmarketPool;
    }

    public void setActualSoldWorld(float actualSoldWorld) {
        this.actualSoldWorld = actualSoldWorld;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Product && name.equals(((Product) obj).name);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int compareTo(Product o) {
        return name.compareTo(o.name);
    }
}
