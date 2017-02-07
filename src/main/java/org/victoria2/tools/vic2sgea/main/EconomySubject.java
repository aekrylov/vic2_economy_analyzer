package org.victoria2.tools.vic2sgea.main;

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
    float actualSupply;

    float actualDemand;

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
        actualSupply += that.actualSupply;
        actualDemand += that.actualDemand;

        exported += that.exported;
        imported += that.imported;

        gdp += that.gdp;
    }

    /**
     * This method should calculate any computational heavy values
     * It makes object state consistent
     */
    public abstract void innerCalculations();

    public float getActualSupply() {
        return actualSupply;
    }

    public float getTotalSupply() {
        return totalSupply;
    }

    public float getActualDemand() {
        return actualDemand;
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

    public void setActualSupply(float actualSupply) {
        this.actualSupply = actualSupply;
    }

    public void setActualDemand(float actualDemand) {
        this.actualDemand = actualDemand;
    }

    public void setTotalSupply(float totalSupply) {
        this.totalSupply = totalSupply;
    }

    public void incActualSupply(float value) {
        actualSupply += value;
    }

    public void incTotalSupply(float value) {
        totalSupply += value;
    }

    public void incExported(float value) {
        exported += value;
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
