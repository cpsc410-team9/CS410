package visualisation;

import java.util.ArrayList;

import preprocessing.ClassDependency;
import preprocessing.ClassDependency.Association;

public class StarVertex {
	// This class is a custom vertex for star/package vertices,
	// contains all the necessary fields for info to transform the vertices

	String starName = "";
	int starSize = 20;
	ArrayList<Association> associations;
	private static ArrayList<ClassDependency> classDependencyList;

	public StarVertex(String name, int size, ArrayList<Association> aList) {
		starName = name;
		starSize = size;
		associations = aList;
	}

	public String toString() {
		return starName;
	}

	public static ArrayList<StarVertex> classDependencyToStarVertex(ArrayList<ClassDependency> cList) {
		int i = 0;
		classDependencyList = cList;
		ArrayList<StarVertex> stars = new ArrayList<StarVertex>();
		for (ClassDependency cd : cList) {
			if (stars.size() > 0) {
				i = 0;
				for (StarVertex sv : stars) {
					if (sv.starName.equals(cd.packageName)) {
						i = 1;
						break;
					}
				}
				if (i == 0) {
					ArrayList<Association> aList = getPackageAssociations(
							cd.associations, cd.packageName);
					stars.add(new StarVertex(cd.packageName, numberOfClassesInPackage(cd.packageName),
							aList));
				}
			} else {
				ArrayList<Association> aList = getPackageAssociations(
						cd.associations, cd.packageName);
				stars.add(new StarVertex(cd.packageName, numberOfClassesInPackage(cd.packageName),
						aList));
			}
		}
		System.out.println("end of method, size = " + stars.size());
		return stars;
	}

	public static ArrayList<Association> getPackageAssociations(ArrayList<Association> classAssList, String packageName) {
		ArrayList<Association> aList = new ArrayList<Association>();
		Association a = null;
		ArrayList<ClassDependency> cdList = classDependencyList;
		System.out.println("package: " + packageName);
		for (Association classAss : classAssList) {
			for (ClassDependency cd : cdList) {
				if (cd.className.equals(classAss.associatedWith)) {
					System.out.println("association package added: " + cd.packageName);
					a = cd.new Association();
					a.associatedWith = cd.packageName;
					aList.add(a);
				}
			}
		}
		return aList;
	}
	
	public static int numberOfClassesInPackage(String packageName){ArrayList<ClassDependency> cdList = classDependencyList;
		int num = 0;
		for(ClassDependency cd: cdList){
			if(cd.packageName.equals(packageName)){
				num++;
			}
		}
		return num;
	}
}
