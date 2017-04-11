package org.victoria2.tools.vic2sgea.entities;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/2/17 1:23 PM
 * <p>
 * Base class for charts
 */
public abstract class EconomySubject {

    float exported;
    float imported;

    /**
     * It is real production for that good in country, without unsold goods, in pieces. Used as GDP
     */
    float sold;

    float bought;

    /**
     * It is total supply for that good in country (including unsold), in pieces
     */
    float totalSupply;

    /**
     * (Actual supply) - (producers stockpile) for storages
     * total production - total producers stockpile for countries
     */
    protected float gdp;

    /**
     * Adds values of another subject
     * Note that different subclasses may use different units
     *
     * @param that another subject
     * @throws RuntimeException if runtime classes are not equal
     */
    public void add(EconomySubject that) throws RuntimeException {
        if (!getClass().equals(that.getClass())) {
            throw new RuntimeException("Incompatible classes");
        }
        totalSupply += that.totalSupply;
        sold += that.sold;
        bought += that.bought;

        exported += that.exported;
        imported += that.imported;

        gdp += that.gdp;
    }

    /**
     * This method should calculate any computational heavy values
     * It makes object state consistent
     */
    public abstract void innerCalculations();

    public float getSold() {
        return sold;
    }

    public float getTotalSupply() {
        return totalSupply;
    }

    public float getBought() {
        return bought;
    }

    public float getExported() {
        return exported;
    }

    public float getImported() {
        return imported;
    }

    public void setExported(float exported) {
        this.exported = exported;
    }

    public void setImported(float imported) {
        this.imported = imported;
    }

    public void setSold(float sold) {
        this.sold = sold;
    }

    public void setBought(float bought) {
        this.bought = bought;
    }

    public void setTotalSupply(float totalSupply) {
        this.totalSupply = totalSupply;
    }

    public float getGdp() {
        return gdp;
    }

    public void setGdp(float gdp) {
        this.gdp = gdp;
    }

    public void incGdp(float value) {
        gdp += value;
    }
}
