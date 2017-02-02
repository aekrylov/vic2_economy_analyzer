package main;

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

}
