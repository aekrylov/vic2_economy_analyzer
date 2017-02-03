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
    private float unemploymentRate;
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
     * WIP real GDP
     */
    private float realGdp;
    private Map<ProductStorage, String> producersConsumption = new HashMap<>();

    /**
     * Summ for country in pounds
     */


    public Country(String tag) {
        super();
        this.tag = tag;
        this.officialName = tag;
    }

    public void calcGdpPart(Country totalCountry) {
        GDPPart = actualSupply / totalCountry.actualSupply * 100;
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

    public float getGdpPerCapita() {
        return GDPPerCapita;
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

    public long getPopulation() {
        return population;
    }

    public float getUnemploymentRate() {
        return unemploymentRate;
    }

    public float getUnemploymentRateFactory() {
        return (workforceFactory - employmentFactory) * 4 / (float) population * 100;
    }

    public long getWorkforce() {
        return workforceRGO;
    }


    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName.trim();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Clears calculated fields so that multiple inner calculations handled correctly
     */
    private void clearCalculated() {
        totalSupply = 0;
        actualSupply = 0;
        actualDemand = 0;
        imported = 0;
        exported = 0;
    }

    /**
     * Calculate inside-country data
     */
    void innerCalculation() {
        clearCalculated();
        for (ProductStorage productStorage : getStorage()) {

            // calculating real totalSupply
            float thrownToMarket = (productStorage.totalSupply - productStorage.actualSoldDomestic);

            if (thrownToMarket <= 0)
                productStorage.actualSupply = productStorage.totalSupply;
            else {
                if (productStorage.product.name.equalsIgnoreCase("precious_metal"))
                    productStorage.actualSupply = productStorage.totalSupply;
                else if (productStorage.product.worldmarketPool > 0)
                    productStorage.actualSupply = productStorage.actualSoldDomestic + thrownToMarket * productStorage.product.actualSoldWorld / productStorage.product.worldmarketPool;
                else
                    productStorage.actualSupply = productStorage.actualSoldDomestic;
            }
            //calculating import (without wasted)
            productStorage.imported = productStorage.actualDemand - productStorage.actualSupply;

            if (productStorage.imported < 0) productStorage.imported = 0;

            //calculating exported
            if (!productStorage.product.name.equalsIgnoreCase("precious_metal")) {
                productStorage.exported = Math.max(productStorage.actualSupply - productStorage.actualDemand, 0);//???!!!!
            }
            //

            totalSupply += productStorage.getTotalSupplyPounds();
            actualSupply += productStorage.getActualSupplyPounds();
            actualDemand += productStorage.getActualDemandPounds();
            imported += productStorage.getImportedPounds();
            exported += productStorage.getExportedPounds();

            GDPPerCapita = actualSupply / (float) population * 100000;
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

}
