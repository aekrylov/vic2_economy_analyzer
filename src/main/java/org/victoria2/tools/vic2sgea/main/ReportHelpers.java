package org.victoria2.tools.vic2sgea.main;

import eug.parser.EUGFileIO;
import eug.shared.GenericList;
import eug.shared.GenericObject;
import javafx.scene.paint.Color;
import org.victoria2.tools.vic2sgea.entities.Product;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/7/17 9:08 PM
 */
public class ReportHelpers {

    public static final List<String> POPS_RGO = Arrays.asList("farmers", "labourers", "slaves");
    public static final List<String> POPS_FACTORY = Arrays.asList("craftsmen", "clerks");
    public static final List<String> POPS_ARTISANS = Arrays.asList("artisans");

    /**
     * Returns list localisation/*.csv file for given path
     * Half finished, will probably not be completed.
     */
    static List<File> getLocalisationFiles(String path) {

        /*String [] unwanted = {"darkness.csv", "1.1.csv", "1.3.csv", "1.4.csv", "beta1.csv", "beta3.csv", "darkness_3_02.csv",
                "darkness_3_03.csv",
				"event_news.csv",
				"event_news_3_01.csv",
				"housedivided2_1.csv",
				"housedivided2_2.csv",
				"housedivided2_3.csv",
				"newspaper_text.csv",
		"newstext_3_01.csv" };*/

        //the correct platform independent way to join paths
        File folder = Paths.get(path, "localisation").toFile();
        File[] files = folder.listFiles((File file) -> file.isFile() && file.getName().toLowerCase().endsWith(".csv"));

        return new ArrayList<>(Arrays.asList(files));
    }

    /**
     * Reads product info from the given path (base price and color)
     *
     * @param path path to game/mod
     * @return if goods.txt is found and read
     */
    static Set<Product> readProducts(String path) {
        String[] productTypes = {
                "military_goods",
                "raw_material_goods",
                "industrial_goods",
                "consumer_goods"
        };

        Path goodsPath = Paths.get(path, "common", "goods.txt");
        Set<Product> products = new TreeSet<>();
        GenericObject root = EUGFileIO.load(goodsPath.toFile());

        if (root != null) {

            for (String type : productTypes) {
                for (GenericObject object : root.getChild(type).children) {
                    Product product = new Product(object.name, (float) object.getDouble("cost"));

                    //set color
                    GenericList rgb = object.getList("color");
                    double red = Double.valueOf(rgb.get(0)) / 255.;
                    double green = Double.valueOf(rgb.get(1)) / 255.;
                    double blue = Double.valueOf(rgb.get(2)) / 255.;
                    product.setColor(new Color(red, green, blue, 1));

                    products.add(product);
                }
            }
        }
        return products;

    }

    /**
     * Adds up all employment->employees counts
     *
     * @param object object with employment child tag
     * @return total count of employees on object, or 0 if object is not valid
     */
    static int getEmployeeCount(GenericObject object) {
        int count = 0;

        try {
            List<GenericObject> workers = object.getChild("employment").getChild("employees").children;
            for (GenericObject worker : workers) {
                if (worker.getInt("count") > 0) {
                    count += worker.getInt("count");
                }
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }//if no tag is available

        return count;
    }
}
