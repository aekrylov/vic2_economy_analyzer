package main;


/** This class holds all sorts of info, like the paths to savegames and images. It has also a constructor for some basic data about the save game */
public class SaveHeader {
	/* Reference list to determine the battleType */
	//public final static String [] navalUnitsList = {"clipper_transport", "commerce_raider", "cruiser", "dreadnougth", "frigate", "ironclad", 
	//	"manowar", "monitor", "steam_transport", "battleship" };
	/* Paths here for the time being */
	public  String SAVEGAMEPATH = "";
	public  String INSTALLPATH = "";
	public final  String FLAGPATH = "/flags/"; 
	public  String saveGameFile; // Path to a specific save game
	public  String installFile;
	//public  ArrayList<Country> countryList = new ArrayList<Country>();
	/* Data about the save game */
	private String date = "";
	private String player = "";
	private String start_date ="";
	
	
	public SaveHeader() {
		super();
	}
	
	@Override
	public String toString() {
		return "Reference [date=" + date + ", player=" + player
				+ ", start_date=" + start_date + "]";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	}

