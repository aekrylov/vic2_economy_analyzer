package main;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the flag and a longer name than just three letters.
 * There will only be one instance of this class for each country as opposed to <code>JoinedCountry</code>,
 * which has many instances.
 */
public class Country extends EconomySubject implements Comparable<Country> {

    /**
     * Summ for country in pounds
     */
    public long population;
    /**
     * Summ for country
     */
    public long employmentFactory;
    /**
     * Summ for country
     */
    public long employmentRGO;
    public float unemploymentRate;
    /**
     * Summ for country
     */

    public long workforceFactory;
    /**
     * Summ for country
     */
    public long workforceRGO;
    private Map<String, ProductStorage> storageMap = new HashMap<>();
    private String officialName = "";
    /**
     * Summ for country in pounds
     */
    private float GDPPart;
    /**
     * Summ for country in pounds
     */
    private float GDPPerCapita;
    /**
     * Summ for country
     */
    int GDPPlace;
    /**
     * Summ for country in pounds
     */
    float goldIncome;
    ArrayList<Province> provinces = new ArrayList<>();
    private Image flag;
    private String tag;

    /**
     * Summ for country in pounds
     */


    public Country(String tag) {
        super();
        this.tag = tag;
        this.officialName = tag;
    }

    public void calcGDPPerCapita(Country totalCountry) {
        GDPPart = actualSupply / totalCountry.actualSupply * 100;

        GDPPerCapita = actualSupply / (float) population * 100000;
    }


    @Override
    public int compareTo(Country that) {
        return Float.compare(actualSupply, that.actualSupply);
    }

    public boolean exist() {
        return population > 0;
    }

    public ProductStorage findStorage(String name) {
        return storageMap.get(name);
    }

    public long getEmployment() {
        return employmentRGO;
    }

    public Image getFlag() {
        return flag;
    }

    public void setFlag(Image flag) {
        this.flag = flag;
    }

    public float getGDPPart() {
        return GDPPart;
    }

    public int getGDPPlace() {
        return GDPPlace;
    }

    public long getGoldIncome() {
        return (long) goldIncome;
    }

    public String getOfficialName() {
        if (officialName.startsWith(" ")) {
            return officialName.substring(1);
        } else
            return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getPopulation() {
        return population;
    }

    public float getUnemploymentRateFactory() {
        return (workforceFactory - employmentFactory) * 4 / (float) population * 100;
    }

    public long getWorkforce() {
        return workforceRGO;
    }

    /**
     * Calculate inside-country data
     */
    void innerCalculation() {

        for (ProductStorage productStorage : getStorage()) {

            // calculating real supply
            float thrownToMarket = (productStorage.savedSupply - productStorage.actualSoldDomestic);

            if (thrownToMarket <= 0) productStorage.actualSupply = productStorage.savedSupply;
            else {
                if (productStorage.product.name.equalsIgnoreCase("precious_metal"))
                    productStorage.actualSupply = productStorage.savedSupply;
                else if (productStorage.product.worldmarketPool > 0)
                    productStorage.actualSupply = productStorage.actualSoldDomestic + thrownToMarket * productStorage.product.actualSoldWorld / productStorage.product.worldmarketPool;
                else
                    productStorage.actualSupply = productStorage.actualSoldDomestic;
            }
            //calculating import (without wasted)
            productStorage.imported = productStorage.actualDemand - productStorage.actualSupply;

            if (productStorage.imported < 0) productStorage.imported = 0;

            //calculating exported
            float exp;
            if (!productStorage.product.name.equalsIgnoreCase("precious_metal")) {
                exp = (productStorage.actualSupply - productStorage.actualDemand);//???!!!!
                if (exp > 0) {
                    productStorage.exported = exp;
                } else productStorage.exported = 0;
            }
            //

            actualSupply += productStorage.getActualSupplyPounds();
            actualDemand += productStorage.getActualDemandPounds();
            imported += productStorage.getImportedPounds();
            exported += productStorage.getExportedPounds();
        }

        unemploymentRate = (workforceRGO - employmentRGO) * 4 / (float) population * 100;
    }

    @Override
    public String toString() {
        return "Country [tag=" + tag + ", officialName=" + officialName
                + ", flag=" + flag + "]";
    }

    public Collection<ProductStorage> getStorage() {
        return storageMap.values();
    }

    public void addStorage(ProductStorage product) {
        storageMap.put(product.product.getName(), product);
    }

    public float getGdpPerCapita() {
        return GDPPerCapita;
    }

    public float getUnemploymentRate() {
        return unemploymentRate;
    }
}
