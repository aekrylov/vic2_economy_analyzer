package org.victoria2.tools.vic2sgea.main;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;


/**
 * Class for handling savegame path reading, finding and loading from a file
 */
public class PathKeeper {
    private static String SAVE_PATH;
    private static String LOCALISATION_PATH;
    private static String MOD_PATH;

    private static Optional<Path> toOptional(String path) {
        return Optional.ofNullable(path)
                .filter(s -> !s.isEmpty())
                .map(Paths::get);
    }

    public static Optional<Path> getSavePath() {
        return toOptional(SAVE_PATH);
    }

    public static Optional<Path> getLocalisationPath() {
        return toOptional(LOCALISATION_PATH);
    }

    public static Optional<Path> getModPath() {
        return toOptional(MOD_PATH);
    }

    /**
     * Checking if the path file exists. If not, attempts to construct
     * the default save game paths. If the file exist, reads from it.
     */
    public static void init() {
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

    public static void save(Path savePath, Path localisationPath, Path modPath) {
        PathKeeper.SAVE_PATH = savePath != null ? savePath.toString() : null;
        PathKeeper.LOCALISATION_PATH = localisationPath != null ? localisationPath.toString() : null;
        PathKeeper.MOD_PATH = modPath != null ? modPath.toString() : null;
        save();
    }

    /**
     * This method saves the paths so the user does not have
     * to choose the file every time. Saved after every loading of a savefile
     * in GuiController.startIssueFired
     *
     * @throws IOException
     */
    private static void save() {

        Properties props = new Properties();
        BufferedWriter out;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("path.txt"), "UTF-8"));
            getLocalisationPath().ifPresent(p -> props.setProperty("gamePatch", p.toString()));
            getSavePath().ifPresent(p -> props.setProperty("saveGame", p.toString()));
            getModPath().ifPresent(p -> props.setProperty("modPath", p.toString()));
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
