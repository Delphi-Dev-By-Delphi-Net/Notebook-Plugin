package delphi.net.notebook;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotebookCommandExecutor implements CommandExecutor {

	private Notebook plugin;
	
	public NotebookCommandExecutor(Notebook notebook) {
		plugin = notebook;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg3) {
		Player p =(Player)sender;
		if(arg3.length ==3 && command.getName().equalsIgnoreCase("nb")){
			if(arg3[0].equalsIgnoreCase("create")){
				if(arg3[1] !=null){//the notebook name
					if(arg3[2].contains("public")){
						String notebookName = arg3[1];
						plugin.createNotebook(p, notebookName, false);
						return true;
					}else if(arg3[2].contains("private")){
						String notebookName = arg3[1];
						plugin.createNotebook(p, notebookName, true);
						return true;
					}else{
						return false;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}	
		}else if(arg3.length >=4 && command.getName().equalsIgnoreCase("nb") && arg3[0].equalsIgnoreCase("addentry")){
			String notebookName = arg3[1];
			String noteTitle = arg3[2];
			String[] string = arg3;
			plugin.addEntry(p, notebookName,noteTitle, string);
			return true;
		}else if(arg3.length ==2 && command.getName().equalsIgnoreCase("nb") && arg3[0].equalsIgnoreCase("list")){
			String notebookName = arg3[1];
			plugin.listEntries(p, notebookName);
			return true;
		}else if(arg3.length >=4 && command.getName().equalsIgnoreCase("nb") && arg3[0].equalsIgnoreCase("add")){
			String notebookName = arg3[1];
			String noteIndex = arg3[2];
			boolean ok = true;
			int index;
			try{
			index = Integer.parseInt(noteIndex);
			}catch(NumberFormatException e){
			ok = false;
			index=0;
			}
			if(ok){
				String[] theString = arg3;
				plugin.appendEntry(p, notebookName, theString, index);
				return true;
			}else{
				p.sendMessage("Invalid Index");
				return false;
			}
		}else if(command.getName().equalsIgnoreCase("nb") && arg3[0].equalsIgnoreCase("read")){
			String entryname = arg3[1];
			String entryIndex = arg3[2];
			boolean oka =false;
			int index;
			try{
				index = Integer.parseInt(entryIndex);
			}catch(NumberFormatException e){
				return false;
			}
			plugin.readEntry(p, entryname, index);
			return true;
			
		}else{
			return false;
		}
		//TODO Next command
	}
}
