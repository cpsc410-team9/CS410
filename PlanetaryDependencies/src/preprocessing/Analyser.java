package preprocessing;

import java.util.ArrayList;
import java.util.Collections;

import preprocessing.ClassDependency.Association;


public class Analyser {

	/**
	 * @param args
	 */
	public static ArrayList<ClassPacket> allClassPackets = new ArrayList<ClassPacket>();


//	/** 
//	 *  Ready made ClassPackets for testing
//	 */
//	public static void makeTestPackets(){
//		ClassPacket packet1 = new ClassPacket("Pop", "DrinksPackage", 100);
//		Collections.addAll(packet1.instantiated,"Table","Chair","Apple","Moose","Squid","MapleTree","Saturn","RocketChair");
//		allClassPackets.add(packet1);	
//		ClassPacket packet2 = new ClassPacket("Apple", "FruitsPackage", 200);
//		Collections.addAll(packet2.instantiated,"Moose","Pop","Orange","KoolAid","Tiger","AlexFraser","MoonHouse","RocketChair");
//		allClassPackets.add(packet2);
//		ClassPacket packet3 = new ClassPacket("Tiger", "AnimalsPackage", 1000);
//		Collections.addAll(packet3.instantiated,"Ranger","Pop","Apple","Car","Poop","Tree","Pluto","FaceTurtle");
//		allClassPackets.add(packet3);
//		ClassPacket packet4 = new ClassPacket("Hat", "ClothingPackage", 20);
//		Collections.addAll(packet4.instantiated,"Mango","Tiger","Watermelon","Moose","Apple","Maple","Yoghurt");
//		allClassPackets.add(packet4);
//
//	}

	private static ClassDependency generateClassDependencyObject(
			ClassPacket cp) {
		ClassDependency classDependency = new ClassDependency(cp.className);
		classDependency.planetaryRadius = cp.lineCount;
		classDependency.packageName = cp.packageName;
		
		findCompositionDependency(cp, classDependency);
		findRealizationDependency(cp, classDependency);
		findDirectionalAssocation(cp, classDependency);
		
		return classDependency;
	}
	
	/**
	 * @param packet
	 * @param classDependency
	 */
	private static void findDirectionalAssocation(ClassPacket packet,
			ClassDependency classDependency) {
		Association association;
		for(String directionalAssociation : packet.associatedWith){
			association = classDependency.new Association();
			association.associatedWith = directionalAssociation;
			for(ClassPacket classDependentOn : allClassPackets){
				if(classDependentOn.className.equals(directionalAssociation)){
					if(classDependentOn.associatedWith.contains(classDependency.className)){
						association.associationType = ClassDependency.BIDIRECTIONAL_ASSOCIATION;
					}
					else{
						association.associationType = ClassDependency.UNIDIRECTIONAL_ASSOCIATION;
					};
				}
			}
			
			
			classDependency.associations.add(association);
	}
}

	private static void findRealizationDependency(ClassPacket packet,
			ClassDependency classDependency) {
		Association association;
		for(String dependentClass : packet.staticAccess){
			association = classDependency.new Association();
			association.associatedWith = dependentClass;
			association.associationType = ClassDependency.REALIZATION;
			classDependency.associations.add(association);
		}		
	}

	/** This is the primary method to find the composition/aggregation dependencies
	 * @param packet
	 * @return ClassDependencies for the packet
	 */
	public static void findCompositionDependency(ClassPacket packet, ClassDependency classDependency){
		String packetName = packet.className;
		ArrayList<String> classesThatDependOnDependentClass;
		ArrayList<String> instantiated = packet.instantiated;
		Association association;
		for(String dependentClass: instantiated){
			association = classDependency.new Association();
			classesThatDependOnDependentClass = findAllClassesThatInstantiate(dependentClass);
			association.associatedWith = dependentClass;
			if(classesThatDependOnDependentClass.size() < 3){
				if(classesThatDependOnDependentClass.get(0).equals(packetName) || classesThatDependOnDependentClass.size() == 1){
					association.associationType =  ClassDependency.COMPOSITION;
				}
			}else {
				association.associationType =  ClassDependency.AGGREGATION;
			}
			classDependency.associations.add(association);
		}
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

	public static ArrayList<ClassDependency> analyse(ArrayList<ClassPacket> parserOutput) {
		// TODO complete analysis tool. Feel free to use helper methods, submethods, custom classes, etc. 
		// but this has to be the ultimate returning function.
		System.out.println("Analyser started.");
		for(ClassPacket cp : parserOutput){
			allClassPackets.add(cp);
		}
		
		ArrayList<ClassDependency> output = new ArrayList<ClassDependency>();
		for(ClassPacket cp : parserOutput){
			ClassDependency temp = generateClassDependencyObject(cp);
			
			output.add(temp);
		}
		return output;

	}


	

}
