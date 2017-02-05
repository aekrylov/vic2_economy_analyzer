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
public class Vic2SaveGameNash extends Vic2SaveGame {

    public Vic2SaveGameNash(GenericObject root, String savePath,
                            String mainPath, String modName) {
        super(root, savePath, mainPath, modName);
        // TODO Auto-generated constructor stub
    }

    protected Map<Integer, GenericObject> states;
    protected int lastStateId;

    private void initStates() {

        states = new HashMap<>(500);

        //for faster result this could be redone with this.getCountryMap()
        for (GenericObject obj : root.children) {
            if (tagPattern.matcher(obj.name).matches()) { // if object is a country
                countryMap.put(obj.name, obj);

                int i = 0;
                for (GenericObject state : obj.children) {
                    if (state.name.equalsIgnoreCase("state")) {
                        states.put(i, state);
                        i++;
                    }
                }
            }
        }

    }

    private void initStates2(GenericObject country) {

        states = new HashMap<Integer, GenericObject>(500);

        //for faster result this could be redone with this.getCountryMap()
        for (GenericObject obj : root.children) {
            //if (tagPattern.matcher(obj.name).matches()) { // its all countries
            // countryMap.put(obj.name, obj);

            int i = 0;
            for (GenericObject state : obj.children) {
                if (state.name.equalsIgnoreCase("state")) {
                    states.put(i, state);
                    i++;
                }
            }
            // }
        }

    }

    public void preloadStates() {
        if (states == null)
            initStates();
    }

    public List<GenericObject> getStates() {
        return new ArrayList<GenericObject>(states.values());
    }
}
