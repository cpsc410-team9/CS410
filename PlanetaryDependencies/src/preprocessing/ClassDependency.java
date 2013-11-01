package preprocessing;

import java.util.ArrayList;

public class ClassDependency {
	
	public static int COMPOSITION = 0;
	public static int AGGREGATION = 1;
	public static int REALIZATION = 2;
	public static int UNIDIRECTIONAL_ASSOCIATION = 3;
	public static int BIDIRECTIONAL_ASSOCIATION = 4;

	public String className;
	public String packageName;
	public ArrayList<Association> associations;
	
	public ClassDependency(String name){
		className = name;
		associations = new ArrayList<Association>();
	}
	
	public int planetaryRadius;

	//to be used in the future
	
	public class Association{
		public String associatedWith;
		public int associationType;
		public int associationDirection;
	}
}
