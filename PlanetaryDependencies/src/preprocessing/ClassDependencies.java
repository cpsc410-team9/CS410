package preprocessing;

import java.util.ArrayList;

public class ClassDependencies {
	
	public static int COMPOSITION = 0;
	public static int AGGREGATION = 1;
	public static int DEPENDENCY = 2;

	public static int UNIDIRECTIONAL = 3;
	public static int BIDIRECTIONAL = 4;

	public String className;
	public String packageName;
	public ArrayList<Association> associations;
	
	public ClassDependencies(String name){
		className = name;
		associations = new ArrayList<Association>();
	}
	
	public int planetaryRadius;

	//to be used in the future
	
	int orbitalRadius;
	int Color;
	boolean orbitsClockwise;
	
	public class Association{
		public String dependentOn;
		public int associationType;
		public int associationDirection;
	}
}
