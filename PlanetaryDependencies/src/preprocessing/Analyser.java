package preprocessing;

import java.util.ArrayList;
import java.util.Collections;

import preprocessing.ClassDependencies.Association;


public class Analyser {

	/**
	 * @param args
	 */
	public static ArrayList<ClassPacket> allClassPackets = new ArrayList<ClassPacket>();


	/**
	 *  Ready made ClassPackets for testing
	 */
	public static void makeTestPackets(){
		ClassPacket packet1 = new ClassPacket("Pop", "DrinksPackage", 100);
		Collections.addAll(packet1.instantiated,"Table","Chair","Apple","Moose","Squid","MapleTree","Saturn","RocketChair");
		allClassPackets.add(packet1);	
		ClassPacket packet2 = new ClassPacket("Apple", "FruitsPackage", 200);
		Collections.addAll(packet2.instantiated,"Moose","Pop","Orange","KoolAid","Tiger","AlexFraser","MoonHouse","RocketChair");
		allClassPackets.add(packet2);
		ClassPacket packet3 = new ClassPacket("Tiger", "AnimalsPackage", 1000);
		Collections.addAll(packet3.instantiated,"Ranger","Pop","Apple","Car","Poop","Tree","Pluto","FaceTurtle");
		allClassPackets.add(packet3);
		ClassPacket packet4 = new ClassPacket("Hat", "ClothingPackage", 20);
		Collections.addAll(packet4.instantiated,"Mango","Tiger","Watermelon","Moose","Apple","Maple","Yoghurt");
		allClassPackets.add(packet4);

	}


	/** This is the primary method to find the composition dependencies, and it uses the other methods as helpers
	 * @param packet
	 * @return ClassDependencies for the packet
	 */
	public static ClassDependencies findCompositionDependency(ClassPacket packet){
		/*
		 * Shawn: This method is looking good, but if you take a look at the implementation i suggested,
		 * we should refactor this to "findDependencies" and categorize all the associations we find at the same time. 
		 * 
		 * 
		 */
		
		String packetName = packet.className;
		ClassDependencies classDependency = new ClassDependencies(packetName); 
		ArrayList<String> classesThatDependOnDependentClass;
		ArrayList<String> instantiated = packet.instantiated;
		Association dependency;
		for(String dependentClass: instantiated){
			dependency = classDependency.new Association();
			classesThatDependOnDependentClass = findAllClassesThatInstantiate(dependentClass);
			dependency.dependentOn = dependentClass;
			if(classesThatDependOnDependentClass.size() < 3){
				if(classesThatDependOnDependentClass.get(0).equals(packetName) || classesThatDependOnDependentClass.size() == 1){
					dependency.associationType =  ClassDependencies.COMPOSITION;
				}
			}else {
				dependency.associationType =  ClassDependencies.AGGREGATION;
			}
			classDependency.associations.add(dependency);
		}
		for(String dependentClass : packet.staticAccess){
			dependency = classDependency.new Association();
			dependency.dependentOn = dependentClass;
			dependency.associationType = ClassDependencies.REALIZATION;
			classDependency.associations.add(dependency);
		}
		for(String dependentClass : packet.dependencies){
			dependency = classDependency.new Association();
			dependency.dependentOn = dependentClass;
			dependency.associationType = ClassDependencies.DEPENDENCY;
			classDependency.associations.add(dependency);
		}
		return classDependency;
	}

	/**
	 *  Sorts the instantiated classes for quicker searching
	 */
	public static void sortInstantiatedClasses(){
		for(ClassPacket classPacket : allClassPackets){
			Collections.sort(classPacket.instantiated);
		}
	}

	/**
	 * @param name (Class Name for which you want to find all other classes that instantiate this one)
	 * @return ArrayList of the classes which instantiates the input class, the first element is the input class
	 */
	public static ArrayList<String> findAllClassesThatInstantiate(String name){
		ArrayList<String> listOfClasses = new ArrayList<String>();
		listOfClasses.add(name);
		ClassPacket packetHolder;
		for(ClassPacket packet: allClassPackets){
			packetHolder = packet;
			if(packetHolder.className != name || packetHolder.instantiated.size() != 0){
				if(packetHolder.instantiated.contains(name)){
					listOfClasses.add(packetHolder.className);
				}
			}
		}
		return listOfClasses;
	}
	//	public static void main(String[] args) {
	//		// TODO Auto-generated method stub
	//	}

	public static ArrayList<ClassDependencies> analyse(ArrayList<ClassPacket> parserOutput) {
		// TODO complete analysis tool. Feel free to use helper methods, submethods, custom classes, etc. 
		// but this has to be the ultimate returning function.
		System.out.println("Analyser started.");

		ArrayList<ClassDependencies> output = new ArrayList<ClassDependencies>();
		for(ClassPacket cp : parserOutput){
			ClassDependencies temp = findCompositionDependency(cp);
			temp.planetaryRadius = cp.lineCount;
			temp.packageName = cp.packageName;
			output.add(temp);
		}
		return output;

	}

}
