package main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;


/**
 * Class for handling savegame path reading, finding and loading from a file
 */
public class PathKeeper {
    static public String SAVEGAMEPATH;
    static public String LOCALIZATIONPATCH;
    static public String MODPATCH;

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
        SAVEGAMEPATH = "C:/Users/" + user + "/Documents/Paradox Interactive/Victoria II/save games/";
        if (!(new File(SAVEGAMEPATH)).exists()) {
            SAVEGAMEPATH = "";
        }
    }

    /**
     * Checks several places where I think the game directory could be. Starting from the newest version
     */
    private static void checkInstallPath() {
        /* Heart of Darkness */
        if ((new File("C:/Program Files (x86)/Paradox Interactive/Victoria II - A Heart of Darkness")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files (x86)/Paradox Interactive/Victoria II - A Heart of Darkness";
        } else if ((new File("C:/Program Files (x86)/Steam/steamapps/common/Victoria II - A Heart of Darkness")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files (x86)/Steam/steamapps/common/Victoria II - A Heart of Darkness";
        } else if ((new File("C:/Program Files/Paradox Interactive/Victoria II - A Heart of Darkness")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files/Paradox Interactive/Victoria II - A Heart of Darkness";
        }
		/* A House Divided */
        else if ((new File("C:/Program Files (x86)/Paradox Interactive/Victoria 2 A House Divided")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files (x86)/Paradox Interactive/Victoria 2 A House Divided";
        } else if ((new File("C:/Program Files (x86)/Steam/steamapps/common/Victoria 2 A House Divided")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files (x86)/Steam/steamapps/common/Victoria 2 A House Divided";
        } else if ((new File("C:/Program Files/Paradox Interactive/Victoria 2 A House Divided")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files/Paradox Interactive/Victoria 2 A House Divided";
        }
		/* Vanilla */
        else if ((new File("C:/Program Files (x86)/Paradox Interactive/Victoria 2")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files (x86)/Paradox Interactive/Victoria 2";
        } else if ((new File("C:/Program Files (x86)/Steam/steamapps/common/Victoria 2")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files (x86)/Steam/steamapps/common/Victoria 2";
        } else if ((new File("C:/Program Files/Paradox Interactive/Victoria 2")).exists()) {
            LOCALIZATIONPATCH = "C:/Program Files/Paradox Interactive/Victoria 2";
        } else {
            LOCALIZATIONPATCH = "";
        }
    }

    /**
     * Takes the path of the full path of the savegame and return
     * the directory it was in
     *
     * @param path
     */
    private static String getDirectoryOnly(String path) {
        StringBuilder line = new StringBuilder(path);
        int index = line.lastIndexOf("/");

        line.delete(index + 1, line.length());
        SAVEGAMEPATH = line.toString();
        return line.toString();
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
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("path.txt"), "UTF-8"));
            //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("path.txt"),"UTF-8"));
            if (LOCALIZATIONPATCH != null) props.setProperty("gamePatch", LOCALIZATIONPATCH);
            if (SAVEGAMEPATH != null) props.setProperty("saveGame", SAVEGAMEPATH);
            if (MODPATCH != null) props.setProperty("modPatch", MODPATCH);
            try {
                props.store(out, null);

            } catch (IOException e) {
                //TODO be the man, write something
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
				
/*//				FileWriter out = new FileWriter("paths.txt");
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("paths.txt"),"UTF-8"));
				
				if (SAVEGAMEPATH!=null){
				out.write(SAVEGAMEPATH);
				out.write("\n");
				}
				if (LOCALIZATIONPATCH!=null)out.write(LOCALIZATIONPATCH);
				out.close();
			} catch (IOException e) {
				//getErrorLabel().setText(getErrorLabel().getText() + " Could not save the paths.txt.");
				//TODO again extensions
			}
			*/
    }

    /**
     * Reads file ./paths.txt.
     * First line is the SAVEGAMEPATH, second the INSTALLPATH
     */
    private static void load() {

        Properties props = new Properties();
        BufferedWriter out = null;
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream("./path.txt"), "UTF-8");
            try {
                props.load(in);
            } catch (IOException e) {
                //TODO be the man, write something
            }

            LOCALIZATIONPATCH = props.getProperty("gamePatch");
            SAVEGAMEPATH = props.getProperty("saveGame");
            MODPATCH = props.getProperty("modPatch");

        } catch (UnsupportedEncodingException | FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		
		/*try {
			 Lifted from saveGameReader 
			InputStreamReader reader = new InputStreamReader(new FileInputStream("./paths.txt"), "UTF-8"); // This encoding seems to work for õ
			BufferedReader scanner = new BufferedReader(reader);
			
			String line;
			while ((line = scanner.readLine()) != null) {
				 Simple solution to only give value if the paths are empty 
				if (SAVEGAMEPATH==null){
					SAVEGAMEPATH = line;
				}
				else if (LOCALIZATIONPATCH==null){
					LOCALIZATIONPATCH = line;
				}
			}
			scanner.close();
		} catch (NullPointerException | IOException e) {
			//getErrorLabel().setText(getErrorLabel().getText() + " Could not read paths.txt. ");
			//TODO wat the hell again
		}*/
    }


}
