package visualisation;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import control.Main;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.picking.PickedState;

import preprocessing.ClassDependency;
import preprocessing.ClassDependency.Association;
import preprocessing.ClassPacket;

public class Visualiser {
	static JFrame frame = new JFrame("Visualiser");
	
	public static ClassPacket test;
	public static Graph<String, String> starMap;
	public static Graph<ClassDependency, String> solarSystem;
	static VisualizationViewer<String, String> starView;
	static VisualizationViewer<ClassDependency, String> solarSystemView;

	static Layout<String, String> starMapLayout;
	static Layout<ClassDependency, String> solarSystemLayout;

	
	public static void process(ArrayList<ClassDependency> analyserOutput) {
		
		// TODO perform full rendering ultimately, for now, use this to perform text output.
		System.out.println();
		System.out.println("Visualiser started.");
		starMap = new SparseMultigraph<String, String>();
		solarSystem = new SparseMultigraph<ClassDependency, String>();

		starMapLayout = new ISOMLayout<String, String>(starMap);
		solarSystemLayout = new SpringLayout<ClassDependency, String>(solarSystem);
		displayPackageGraph(analyserOutput);
		
		displayTextOutput(analyserOutput);
		setupCanvas(analyserOutput);

	}

	
	private static void setupCanvas(final ArrayList<ClassDependency> analyserOutput) {
		starMapLayout.setSize(new Dimension(1024,768)); 
		starView = new VisualizationViewer<String,String>(starMapLayout);
		starView.setPreferredSize(new Dimension(1024,768)); 
		solarSystemView = new VisualizationViewer<ClassDependency,String>(solarSystemLayout);
		solarSystemView.setPreferredSize(new Dimension(1024,768)); 

		addHandlers(analyserOutput);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(starView);
		frame.pack();
		frame.setVisible(true);   		
	}

	private static void addHandlers(final ArrayList<ClassDependency> analyserOutput) {
		DefaultModalGraphMouse<String, String> mouse = new DefaultModalGraphMouse<String, String>();
		mouse.setMode(Mode.PICKING);
		starView.setGraphMouse(mouse);
		solarSystemView.setGraphMouse(mouse);
		final PickedState<String> pickedState = starView.getPickedVertexState();
		final PickedState<ClassDependency> pickedState2 = solarSystemView.getPickedVertexState();
		
		pickedState.addItemListener(new ItemListener() {
		 
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    Object subject = e.getItem();
		        // The graph uses Integers for vertices.
		        if (subject instanceof String) {
		        	String vertex = (String) subject;
		            
		            if (pickedState.isPicked(vertex)) {
		                System.out.println("Vertex " + vertex
			                    + " selected");
		            	graphSelectedClassSolarSystem(vertex, analyserOutput);
		            } else {
		                System.out.println("Vertex " + vertex
		                    + " no longer selected");

		            }
		        }
		    }
		});		
		pickedState2.addItemListener(new ItemListener() {
			 
		    @Override
		    public void itemStateChanged(ItemEvent e) {
		    Object subject = e.getItem();
		        // The graph uses Integers for vertices.
		        if (subject instanceof ClassDependency) {
		        	ClassDependency vertex = (ClassDependency) subject;
		            
		            if (pickedState2.isPicked(vertex)) {
		                System.out.println("Vertex " + vertex.className+" in "+vertex.packageName
			                    + " selected");
		            } else {
		                System.out.println("Vertex " + vertex
		                    + " no longer selected");
		        		frame.getContentPane().remove(solarSystemView);
		        		frame.getContentPane().add(starView);

		            }
		        }
		    }
		});		
	}


	protected static void graphSelectedClassSolarSystem(String vertex, ArrayList<ClassDependency> list) {
		solarSystem = new SparseMultigraph<ClassDependency, String>();

		for(ClassDependency cd : list){
			if(cd.packageName.equals(vertex)){
				solarSystem.addVertex(cd);
			}
		}
		for(ClassDependency cd : list){
			if(cd.packageName.equals(vertex)){
				for(Association a : cd.associations){
					ClassDependency temp = findClassDependency(a.associatedWith, list);
					solarSystem.addVertex(temp);
					solarSystem.addEdge(cd.className+"-"+a.associatedWith, cd, temp);
				}
			}
		}
		solarSystemLayout.setGraph(solarSystem);
		solarSystemView.setGraphLayout(solarSystemLayout);		
		frame.getContentPane().remove(starView);
		frame.getContentPane().add(solarSystemView);
		

	}


	private static ClassDependency findClassDependency(String associatedWith,ArrayList<ClassDependency> list) {

		for(ClassDependency cd : list){
			if(cd.className.equals(associatedWith))return cd;
		}
		return null;
	}


	private static void displayPackageGraph(
			ArrayList<ClassDependency> analyserOutput) {
		
		for(ClassDependency cd : analyserOutput){
			starMap.addVertex(cd.packageName);
		}
		for(ClassDependency cd : analyserOutput){
			for(Association a : cd.associations){
				String s = packageNameOf(a.associatedWith,analyserOutput);
				starMap.addEdge(cd.packageName+"-"+s, cd.packageName, s);
			}
		}
		
	}
	private static String packageNameOf(String associatedWith,
			ArrayList<ClassDependency> analyserOutput) {
		for(ClassDependency cd : analyserOutput){
			if(cd.className.equals(associatedWith)){
				return cd.packageName;
			}
		}
		return null;
	}

	private static void drawClassGraph(ArrayList<ClassDependency> list){

    	//populate vertices
		for(ClassDependency cd : list){    		
			starMap.addVertex(cd.className);
		}
		
		//populate edges
		for(ClassDependency cd : list){    		
			for(Association a : cd.associations){
				String association = "";
				switch(a.associationType){
				case 0:
					association="COMPOSITION";
					break;
				case 1:
					association="AGGREGATION";
					break;
				case 2:
					association="REALIZATION";
					break;
				case 3:
					association="UNI-DIRECTIONAL ASSOCIATION";
					break;
				case 4:
					association="BI-DIRECTIONAL ASSOCIATION";
					break;
				}
				starMap.addEdge(cd.className+"-"+a.associatedWith, cd.className,a.associatedWith);
			}
		}
	}
	private static void displayTextOutput(
			ArrayList<ClassDependency> analyserOutput) {
		for(ClassDependency cd : analyserOutput){
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
					System.out.print("  - "+a.associatedWith+", Association Type: ");
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
						System.out.println("UNI-DIRECTIONAL ASSOCIATION");
						break;
					case 4:
						System.out.println("BI-DIRECTIONAL ASSOCIATION");
						break;

					}
				}
			}
			System.out.println("----------------------End Scan---------------------");
			System.out.println();

		}		
	}


}
