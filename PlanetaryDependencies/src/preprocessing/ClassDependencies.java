package preprocessing;

import java.util.ArrayList;

public class ClassDependencies {
	
	public static int COMPOSITION = 0;
	public static int AGGREGATION = 1;
	public static int DEPENDENCY = 2;

	public static int UNIDIRECTIONAL = 3;
	public static int BIDIRECTIONAL = 4;

	String className;
	String packageName;
	ArrayList<Dependency> dependencies;
	
	public ClassDependencies(String name){
		className = name;
		dependencies = new ArrayList<Dependency>();
	}

	//to be used in the future
	
	int orbitalRadius;
	int Color;
	int planetaryRadius;
	boolean orbitsClockwise;
	
	public class Dependency{
		String dependentOn;
		int dependencyType;
		int dependenyDirection;
	}
}
