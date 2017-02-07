/**
 *
 */
package org.victoria2.tools.vic2sgea.main;

import eug.shared.GenericObject;
import eug.specific.victoria2.Vic2SaveGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author minetron
 */
public class Vic2SaveGameCustom extends Vic2SaveGame {

    public Vic2SaveGameCustom(GenericObject root) {
        super(root, "", "", "");
        preloadProvinces();
        preloadCountries();
        preloadStates();
    }

    protected Map<GenericObject, List<GenericObject>> states;

    private void initStates() {
        states = new HashMap<>();
        for (GenericObject obj : getCountries()) {
            List<GenericObject> countryStates = new ArrayList<>();
            states.put(obj, countryStates);
            for (GenericObject state : obj.getChildren("state")) {
                countryStates.add(state);
            }
        }

    }

    public void preloadStates() {
        if (states == null)
            initStates();
    }

    public List<GenericObject> getStates(GenericObject country) {
        if (states == null)
            initStates();
        return states.get(country);
    }
}
