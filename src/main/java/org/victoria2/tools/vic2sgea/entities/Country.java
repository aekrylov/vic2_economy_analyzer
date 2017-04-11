package org.victoria2.tools.vic2sgea.entities;

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

    @NonSerializable //too expensive to serialize this
    private Map<String, ProductStorage> storageMap = new HashMap<>();
    @NonSerializable
    private String officialName = "";

    private float GDPPart;

    private int GDPPlace;
    /**
     * Summ for country in pounds
     */
    private float goldIncome;
    private String tag;

    @NonSerializable //temporary variable
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

    /**
     * Adds intermediate consumption for given product in that country
     *
     * @param product product
     * @param value   value of consumption to add
     */
    public void addIntermediate(Product product, float value) {
        float old = getIntermediate(product);
        intermediate.put(product, old + value);

        ProductStorage storage = findStorage(product);
        storage.incGdp(-value);
    }

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
        return gdp / (float) population * 100000;
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
        long totalWorkforce = workforceRGO + workforceFactory;
        return (float) totalWorkforce * 4 / population * 100;
    }

    public float getUnemploymentRateRgo() {
        return (float) ((workforceRGO - employmentRGO) * 4. / population * 100);
    }

    public float getUnemploymentRateFactory() {
        return (workforceFactory - employmentFactory) / (float) workforceFactory * 100;
    }

    public long getWorkforceRgo() {
        return workforceRGO;
    }

    public long getWorkforceFactory() {
        return workforceFactory;
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
        sold = 0;
        bought = 0;
        imported = 0;
        exported = 0;

        gdp = 0;

    }

    /**
     * Calculate inside-country data
     */
    public void innerCalculations() {
        clearCalculated();
        for (ProductStorage productStorage : getStorage().values()) {

            productStorage.innerCalculations();

            totalSupply += productStorage.getTotalSupplyPounds();
            sold += productStorage.getActualSupplyPounds();
            bought += productStorage.getActualDemandPounds();
            imported += productStorage.getImportedPounds();
            exported += productStorage.getExportedPounds();
            gdp += productStorage.getGdpPounds();

        }

    }

    @Override
    public String toString() {
        return "Country [tag=" + tag + ", officialName=" + officialName + "]";
    }

    public Map<String, ProductStorage> getStorage() {
        return storageMap;
    }

    public void setGDPPlace(int GDPPlace) {
        this.GDPPlace = GDPPlace;
    }

    public void addPopulation(int value) {
        population += value;
    }

    public void addEmploymentRgo(int value) {
        employmentRGO += value;
    }

    public void addEmploymentFactory(int value) {
        employmentFactory += value;
    }

    public void addGoldIncome(float value) {
        goldIncome += value;
    }
}
