package preprocessing;

import java.util.ArrayList;

public class ClassPacket {
	
	public String className;
	public String packageName;
	public int lineCount;
	public ArrayList<String> dependencies;
	public ArrayList<String> staticAccess;
	public ArrayList<String> instantiated;
	
	public ClassPacket(String name, String pName, int count){
		className = name;
		packageName = pName;
		lineCount = count;
		dependencies = new ArrayList<String>();
		staticAccess = new ArrayList<String>();
		instantiated = new ArrayList<String>();
	}
	public void addToDependency(String newClass){
		if (!dependencies.contains(newClass))
			dependencies.add(newClass);
	}
	public void addToStaticAccess(String newClass){
		if(!staticAccess.contains(newClass))
			staticAccess.add(newClass);
	}
	public void addToInstantiated(String newClass){
		if(!instantiated.contains(newClass))
			instantiated.add(newClass);
	}
}
