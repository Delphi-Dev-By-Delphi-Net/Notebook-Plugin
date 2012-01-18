package delphi.net.notebook;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Notebook extends JavaPlugin {
	
	Logger log = Logger.getLogger("Minecraft");
	FileConfiguration config;
	private NotebookCommandExecutor comExecutor;
	private static String pName = "Notebook: ";
	
	// Section Keys
	final String mainKEY = "MAIN";
	final String userKEY = "USER_SETTINGS";
	// Config Keys
	final String frunKEY = "FIRST_RUN";
	final String totalNotesKEY = "TOTAL_NOTES";
	final String totalTasksKEY = "TOTAL_TASKS";
	final String totalNotebooksKEY = "TOTAL_NOTEBOOKS";
	final String totalProjectsKEY = "TOTAL_PROJECTS";
	
	//Values set from config.yml
	private int totalNotes;
	private int totalTasks;
	private int totalNotebooks;
	private int totalProjects;
	
	
	public void onDisable() {
		// TODO Finish On Disable Commands
		log.info(pName+"Disabled");
	}

	public void onEnable() {
		log.info(pName+"Starting..");
		config = getConfig();
		comExecutor = new NotebookCommandExecutor(this);
		getCommand("nb").setExecutor(comExecutor);
		boolean firstRun = config.getConfigurationSection(mainKEY).getBoolean(frunKEY);
		if(firstRun){
			config.createSection(mainKEY);
			config.createSection(userKEY);
			config.getConfigurationSection(mainKEY).set(frunKEY, false);
			config.getConfigurationSection(mainKEY).set(totalNotesKEY, 0);
			config.getConfigurationSection(mainKEY).set(totalTasksKEY, 0);
			config.getConfigurationSection(mainKEY).set(totalNotebooksKEY, 0);
			config.getConfigurationSection(mainKEY).set(totalProjectsKEY, 0);
			//config.getConfigurationSection(userKEY).set("Note Creation Message", "Note Created");
			saveConfig();
			totalNotes =0;
			totalTasks=0;
			totalNotebooks=0;
			totalProjects=0;
			log.info(pName+"First Run Setup Done.OK");
		}else{
			totalNotes = config.getConfigurationSection(mainKEY).getInt(totalNotesKEY);
			totalTasks = config.getConfigurationSection(mainKEY).getInt(totalTasksKEY);
			totalNotebooks = config.getConfigurationSection(mainKEY).getInt(totalNotebooksKEY);
			totalProjects = config.getConfigurationSection(mainKEY).getInt(totalProjectsKEY);
			log.info(pName+"Load Complete,");
			log.info(pName+"Started.OK");
		}
		
	}
	
	// Method for adding a new notebook
	public void createNotebook(Player p, String notebookName, boolean personal) {
		Player player = p;
		String playerName = p.getDisplayName().toString();
		String notebook = notebookName;
		if(config.getConfigurationSection(notebook) !=null){
			player.sendMessage(pName+"A notebook with that name allready exists");
		}else{
			config.createSection(notebook);
			config.getConfigurationSection(notebook).set("TOTAL_ENTRIES", 0);
			config.getConfigurationSection(notebook).set("OWNER", playerName);
			config.getConfigurationSection(notebook).createSection("NOTES");
			if(personal){
				config.getConfigurationSection(notebook).set("PRIVATE", true);
			}else{
				config.getConfigurationSection(notebook).set("PRIVATE", false);
			}
			totalNotebooks++;
			config.getConfigurationSection(mainKEY).set(totalNotebooksKEY, totalNotebooks);
			saveConfig();
			player.sendMessage("Notebook "+notebook+" Created");
			log.info(pName+playerName+" Created Notebook "+notebook);
		}
	}
	
	// Currently Unused Method for deleting noteboos
	public void deleteNotebook(Player p, String notebookName){
		
	}
	
	//method for adding an entry
	public void addEntry(Player p, String name, String noteT, String[] args){
		String playerName =p.getDisplayName().toString();
		if(config.getConfigurationSection(name) !=null){
			String owner = config.getConfigurationSection(name).getString("OWNER");
			boolean personal = config.getConfigurationSection(name).getBoolean("PRIVATE");
			if(playerName.equals(owner) || !personal){
				String[] data = args;
				String noteTitle = noteT;
				int i =data.length;
				StringBuilder builder = new StringBuilder();
				int index = config.getConfigurationSection(name).getInt("TOTAL_ENTRIES");
				for(int counter=3; counter < data.length; counter++){
					builder.append(data[counter]+" ");
				}
				String theText = builder.toString();
				config.getConfigurationSection(name).createSection("ENTRY_"+index);
				config.getConfigurationSection(name).getConfigurationSection("NOTES").set("note_"+index, noteTitle);
				config.getConfigurationSection(name).getConfigurationSection("ENTRY_"+index).set("Note", theText);
				p.sendMessage("Entry added at index"+index );
				index++;
				config.getConfigurationSection(name).set("TOTAL_ENTRIES", index);
				saveConfig();
				
			}else{
				p.sendMessage("You do not have permission to edit this notebook");
			}
		}else{
			p.sendMessage("Notebook not found");
		}
	}
	
	// method for appending an entry
	public void appendEntry(Player p, String name, String[] args, int index){
		String playerName = p.getDisplayName();
		if(config.getConfigurationSection(name) !=null){
			String owner = config.getConfigurationSection(name).getString("OWNER");
			boolean personal = config.getConfigurationSection(name).getBoolean("PRIVATE");
			if(owner.equals(playerName) || !personal){
				if(config.getConfigurationSection(name).getConfigurationSection("ENTRY_"+index) !=null){
					String[] passed = args;
					String origional = config.getConfigurationSection(name).getConfigurationSection("ENTRY_"+index).getString("Note");
					int totalE = passed.length;
					StringBuilder builder = new StringBuilder();
					builder.append(origional+" ");
					for(int i=3; i < totalE; i++){
						builder.append(passed[i]);
					}
					String NewNOTE = builder.toString();
					config.getConfigurationSection(name).getConfigurationSection("ENTRY_"+index).set("Note", NewNOTE);
					saveConfig();
					p.sendMessage("Changes Made Sucessfully");
				}else{
					p.sendMessage("No entry with that index exists");
				}
			}else{
				p.sendMessage("You do not have permission to edit this notebook");
			}
		}else{
			p.sendMessage("Notebook not found");
		}
	}
	
	// Method for listng the entries in a notebook
	public void listEntries(Player p, String name){
		String playerName = p.getDisplayName();
		if(config.getConfigurationSection(name) !=null){
			String owner = config.getConfigurationSection(name).getString("OWNER");
			boolean personal = config.getConfigurationSection(name).getBoolean("PRIVATE");
			if(owner.equals(playerName) || !personal){
				int totalE = config.getConfigurationSection(name).getInt("TOTAL_ENTRIES");
				if(totalE >=1){
					for(int i=0; i < totalE; i++){
						String message = config.getConfigurationSection(name).getConfigurationSection("NOTES").getString("note_"+i);
						p.sendMessage(message+" at index "+i);
					}
				}else{
					p.sendMessage("Notebook contains no entries");
				}
			}
		}else{
			p.sendMessage("Notebook not found");
		}
	}
	
	// Method for reading the notebook entries
	public void readEntry(Player p, String name, int entry){
		String playerName = p.getDisplayName();
		if(config.getConfigurationSection(name) !=null){
			String owner = config.getConfigurationSection(name).getString("OWNER");
			boolean personal = config.getConfigurationSection(name).getBoolean("PRIVATE");
			if(playerName.equals(owner) || !personal){
				if(config.getConfigurationSection(name).getConfigurationSection("ENTRY_"+entry) !=null){
					String entryText = config.getConfigurationSection(name).getConfigurationSection("ENTRY_"+entry).getString("Note");
					p.sendMessage(entryText);
				}else{
					p.sendMessage("No entry with that index exists");
				}
			}else{
				p.sendMessage("You do not have permission to edit this notebook");
			}
		}else{
			p.sendMessage("Notebook not found");
		}
	}

}
