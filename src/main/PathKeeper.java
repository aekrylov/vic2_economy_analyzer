package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 * Class for handling savegame path reading, finding and loading from a file
 */
public class PathKeeper {
    static public String SAVE_PATH;
    static public String LOCALISATION_PATH;
    static public String MOD_PATH;

    /**
     * Checking if the path file exists. If not, attempts to construct
     * the default save game paths. If the file exist, reads from it.
     */
    public static void checkPaths() {
        if ((new File("./path.txt")).exists()) {
            load();
        } else {
            checkSaveGamePath();
            checkInstallPath();
        }
    }

    /**
     * Gets the user of the system and constructs a default save game path.
     * If it is not found, sets the path to "".
     */
    private static void checkSaveGamePath() {
        String user = System.getProperty("user.name");
        SAVE_PATH = "C:/Users/" + user + "/Documents/Paradox Interactive/Victoria II/save games/";
        if (!(new File(SAVE_PATH)).exists()) {
            SAVE_PATH = "";
        }
    }

    /**
     * Checks several places where I think the game directory could be. Starting from the newest version
     */
    private static void checkInstallPath() {
        /* Heart of Darkness */
        if ((new File("C:/Program Files (x86)/Paradox Interactive/Victoria II - A Heart of Darkness")).exists()) {
            LOCALISATION_PATH = "C:/Program Files (x86)/Paradox Interactive/Victoria II - A Heart of Darkness";
        } else if ((new File("C:/Program Files (x86)/Steam/steamapps/common/Victoria II - A Heart of Darkness")).exists()) {
            LOCALISATION_PATH = "C:/Program Files (x86)/Steam/steamapps/common/Victoria II - A Heart of Darkness";
        } else if ((new File("C:/Program Files/Paradox Interactive/Victoria II - A Heart of Darkness")).exists()) {
            LOCALISATION_PATH = "C:/Program Files/Paradox Interactive/Victoria II - A Heart of Darkness";
        }
		/* A House Divided */
        else if ((new File("C:/Program Files (x86)/Paradox Interactive/Victoria 2 A House Divided")).exists()) {
            LOCALISATION_PATH = "C:/Program Files (x86)/Paradox Interactive/Victoria 2 A House Divided";
        } else if ((new File("C:/Program Files (x86)/Steam/steamapps/common/Victoria 2 A House Divided")).exists()) {
            LOCALISATION_PATH = "C:/Program Files (x86)/Steam/steamapps/common/Victoria 2 A House Divided";
        } else if ((new File("C:/Program Files/Paradox Interactive/Victoria 2 A House Divided")).exists()) {
            LOCALISATION_PATH = "C:/Program Files/Paradox Interactive/Victoria 2 A House Divided";
        }
		/* Vanilla */
        else if ((new File("C:/Program Files (x86)/Paradox Interactive/Victoria 2")).exists()) {
            LOCALISATION_PATH = "C:/Program Files (x86)/Paradox Interactive/Victoria 2";
        } else if ((new File("C:/Program Files (x86)/Steam/steamapps/common/Victoria 2")).exists()) {
            LOCALISATION_PATH = "C:/Program Files (x86)/Steam/steamapps/common/Victoria 2";
        } else if ((new File("C:/Program Files/Paradox Interactive/Victoria 2")).exists()) {
            LOCALISATION_PATH = "C:/Program Files/Paradox Interactive/Victoria 2";
        } else {
            LOCALISATION_PATH = "";
        }
    }

    /**
     * This method saves the paths so the user does not have
     * to choose the file every time. Saved after every loading of a savefile
     * in GuiController.startIssueFired
     *
     * @throws IOException
     */
    public static void save() {

        Properties props = new Properties();
        BufferedWriter out;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("path.txt"), "UTF-8"));
            if (LOCALISATION_PATH != null) props.setProperty("gamePatch", LOCALISATION_PATH);
            if (SAVE_PATH != null) props.setProperty("saveGame", SAVE_PATH);
            if (MOD_PATH != null) props.setProperty("modPath", MOD_PATH);
            try {
                props.store(out, null);

            } catch (IOException e) {
                //TODO be the man, write something
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
				
    }

    /**
     * Reads file ./paths.txt.
     * First line is the SAVE_PATH, second the INSTALLPATH
     */
    private static void load() {

        Properties props = new Properties();
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream("./path.txt"), "UTF-8");
            try {
                props.load(in);
            } catch (IOException e) {
                //TODO be the man, write something
            }

            LOCALISATION_PATH = props.getProperty("gamePatch");
            SAVE_PATH = props.getProperty("saveGame");
            MOD_PATH = props.getProperty("modPath");

        } catch (UnsupportedEncodingException | FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		
    }


}
