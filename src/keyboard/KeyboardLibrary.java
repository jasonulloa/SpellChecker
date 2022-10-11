package keyboard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class KeyboardLibrary {
	//disable the constructor to avoid instances of KeyboardLibrary
	private KeyboardLibrary() {};
	
	//list all the registered keyboard mappings 
	static final Map<String,Keyboard> keyboards;
	
	//static constructor
	static {
		keyboards = new HashMap<String,Keyboard>();
	}
	
	//returns registered keyboard under given name
	public static Keyboard getKeyboard(String name) {
		return keyboards.get(name);
	}
	
	//load a new keyboard under given name
	public static boolean loadKeyboard(String name, String filepath) {
		//fail if keyboard name is already registered
		if(keyboards.containsKey(name)) {
			return false;
		}
		
		File config = new File(filepath);
		
		//fail if file doesn't exist
		if(!config.exists()) {
			return false;
		}
		
		boolean loaded = false;
		
		//attempting to load keyboard
		try {
			Keyboard kb = new Keyboard(config);
			keyboards.put(name, kb);
			loaded = true;
		} catch (IOException ioe) {
			System.out.println("Failed to load keyboard '" + name + "' at " + filepath);
		}
		
		return loaded;
	}
}