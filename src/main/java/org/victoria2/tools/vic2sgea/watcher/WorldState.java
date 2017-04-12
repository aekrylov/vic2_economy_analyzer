package org.victoria2.tools.vic2sgea.watcher;

import org.victoria2.tools.vic2sgea.entities.Country;
import org.victoria2.tools.vic2sgea.entities.Product;
import org.victoria2.tools.vic2sgea.main.Report;

import java.util.Collection;

/**
 * Created by anth on 12.02.2017.
 * <p>
 * Represents state of the world at some point in time.
 * todo merge with report?
 */
public class WorldState {

    private Collection<Country> countries;
    private Collection<Product> products;

    public WorldState(Report report) {
        countries = report.getCountryList();
        products = report.getProductList();
    }
}
