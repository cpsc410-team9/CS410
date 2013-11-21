package preprocessing;

import java.awt.Color;
import java.util.ArrayList;

public class ClassDependency {
	
	public final static int COMPOSITION = 0;
	public final static int AGGREGATION = 1;
	public final static int REALIZATION = 2;
	public final static int UNIDIRECTIONAL_ASSOCIATION = 3;
	public final static int BIDIRECTIONAL_ASSOCIATION = 4;
	public int lineCount =0;

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
	
	/**
	 * floats between 60-255 (for planet colours)
	 * @return
	 *float
	 */
	public float generateFloat(){
	 double rand = Math.random();
	 if(rand*255 > 100){
		 return (float) rand;
	 } else {
		return generateFloat();
	 }
	}
	
	/**
	 * line count for radius of planets
	 */
	public int planetaryRadius;


	public class Association{
		public String associatedWith;
		public int associationType;
	}
}
