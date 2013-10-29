package visualisation;

import java.util.ArrayList;

import control.Main;

import preprocessing.ClassDependencies;
import preprocessing.ClassDependencies.Association;
import preprocessing.ClassPacket;

public class Visualiser {
	public static ClassPacket test;

	public static void process(ArrayList<ClassDependencies> analyserOutput) {
		
		// TODO perform full rendering ultimately, for now, use this to perform text output.
		System.out.println();
		System.out.println("Visualiser started.");

		for(ClassDependencies cd : analyserOutput){
			System.out.println("-------------------Scanning Planet-----------------");
			System.out.println("Planet: "+cd.className);
			System.out.println("  Planet radius: "+cd.planetaryRadius);
			System.out.println("  Solar System: "+cd.packageName);
			if(cd.associations.size()==0){
				System.out.println("Associations: No outgoing associations.");
			}
			else{
				System.out.println("  Associated with:");
				for(Association a : cd.associations){
					System.out.print("  - "+a.dependentOn+", Association Type: ");
					switch(a.associationType){
					case 0:
						System.out.println("COMPOSITION");
						break;
					case 1:
						System.out.println("AGGREGATION");
						break;
					case 2:
						System.out.println("REALIZATION");
						break;
					case 3:
						System.out.println("GENERAL DEPENDENCY");
						break;
					case 4:
						System.out.println("UNI-DIRECTIONAL");
						break;
					case 5:
						System.out.println("BI-DIRECTIONAL");
						break;

					}
				}
			}
			System.out.println("----------------------End Scan---------------------");
			System.out.println();

		}

	}

}
