package main;

import java.util.ArrayList;
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
    protected long population;
    /**
     * Summ for country
     */
    protected long employmentFactory;
    /**
     * Summ for country
     */
    protected long employmentRGO;

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
    private String tag;

    /**
     * (Actual supply) - (producers stockpile)
     */
    private float actualSupplyWithDeductions;

    private Map<Product, Float> intermediate = new HashMap<>();

    public void add(Country that) {
        super.add(that);
        population += that.population;

        goldIncome += that.goldIncome;

        workforceRGO += that.workforceRGO;
        workforceFactory += that.workforceFactory;
        employmentRGO += that.employmentRGO;
        employmentFactory += that.employmentFactory;
    }

    private float getIntermediate(Product product) {
        return intermediate.computeIfAbsent(product, (k) -> 0.f);
    }

    public void addIntermediate(Product product, float value) {
        float old = getIntermediate(product);
        intermediate.put(product, old + value);

        ProductStorage storage = findStorage(product);
        storage.incGdp(-value);
    }

/*
    public void addSold(Product product, float value) {
        findStorage(product).incGdp(value);
    }
*/

    public Country(String tag) {
        super();
        this.tag = tag;
        this.officialName = tag;
    }

    public void calcGdpPart(Country totalCountry) {
        GDPPart = gdp / totalCountry.gdp * 100;
    }


    @Override
    public int compareTo(Country that) {
        return Float.compare(gdp, that.gdp);
    }

    public boolean exist() {
        return population > 0;
    }

    public ProductStorage findStorage(Product product) {
        return storageMap.computeIfAbsent(product.getName(), k -> new ProductStorage(product));
    }

    public long getEmployment() {
        return employmentRGO;
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

    public float getUnemploymentRateRgo() {
        return (float) ((workforceRGO - employmentRGO) * 4. / population * 100);
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

        if (!tag.equals(Report.TOTAL_TAG))
            gdp = 0;

        actualSupplyWithDeductions = 0;
    }

    /**
     * Calculate inside-country data
     */
    void innerCalculation() {
        clearCalculated();
        for (ProductStorage productStorage : getStorage().values()) {

            productStorage.innerCalculations();
            
            totalSupply += productStorage.getTotalSupplyPounds();
            actualSupply += productStorage.getActualSupplyPounds();
            actualDemand += productStorage.getActualDemandPounds();
            imported += productStorage.getImportedPounds();
            exported += productStorage.getExportedPounds();
            gdp += productStorage.getGdpPounds();

            actualSupplyWithDeductions += (productStorage.getActualSupply() - getIntermediate(productStorage.product))
                    * productStorage.getPrice();

        }

        GDPPerCapita = gdp / (float) population * 100000;

    }

    @Override
    public String toString() {
        return "Country [tag=" + tag + ", officialName=" + officialName + "]";
    }

    public Map<String, ProductStorage> getStorage() {
        return storageMap;
    }

    public float getActualSupplyWithDeductions() {
        return actualSupplyWithDeductions;
    }
}
