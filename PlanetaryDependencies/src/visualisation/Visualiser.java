package visualisation;

import java.util.ArrayList;

import preprocessing.ClassDependencies;
import preprocessing.ClassDependencies.Association;

public class Visualiser {

	public static void process(ArrayList<ClassDependencies> analyserOutput) {
		// TODO perform full rendering ultimately, for now, use this to perform text output.
		for(ClassDependencies cd : analyserOutput){
			System.out.println("Planet: "+cd.className);
			System.out.println("  Planet radius: "+cd.planetaryRadius);
			System.out.println("  Solar System: "+cd.packageName);
			System.out.println("  Associated with:");
			for(Association a : cd.associations){
				System.out.println("  - "+a.dependentOn+", Association Type: "+a.associationType);
			}


		}
		System.out.println("Visualiser started.");
		
	}

}
