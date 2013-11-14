package preprocessing;

import java.awt.Color;
import java.util.ArrayList;

public class ClassDependency {
	
	public static int COMPOSITION = 0;
	public static int AGGREGATION = 1;
	public static int REALIZATION = 2;
	public static int UNIDIRECTIONAL_ASSOCIATION = 3;
	public static int BIDIRECTIONAL_ASSOCIATION = 4;
	public static int lineCount =0;

	public String className;
	public String packageName;
	public ArrayList<Association> associations;
	public Color colour;
	
	public ClassDependency(String name, int lines){
		className = name;
		lineCount = lines;
		associations = new ArrayList<Association>();
		colour = new Color(generateFloat(),generateFloat(),generateFloat());
	}
	
	//floats between 60-255 ( for light colours)
	public float generateFloat(){
	 double rand = Math.random();
	 if(rand*255 > 100){
		 return (float) rand;
	 } else {
		return generateFloat();
	 }
	}
	
	public int planetaryRadius;

	//to be used in the future
	
	public class Association{
		public String associatedWith;
		public int associationType;
	}
}
