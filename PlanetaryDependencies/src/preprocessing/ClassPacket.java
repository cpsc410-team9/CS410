package preprocessing;

import java.util.ArrayList;

public class ClassPacket {
	
	public String className;
	public ArrayList<String> imported;
	public ArrayList<String> staticAccess;
	public ArrayList<String> instantiated;
	
	public ClassPacket(String name){
		className = name;
		imported = new ArrayList<String>();
		staticAccess = new ArrayList<String>();
		instantiated = new ArrayList<String>();
	}
	public void addToImported(String newClass){
		imported.add(newClass);
	}
	public void addToStaticAccess(String newClass){
		staticAccess.add(newClass);
	}
	public void addToInstantiated(String newClass){
		instantiated.add(newClass);
	}
}
