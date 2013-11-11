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
	public static ClassPacket test;
	public static Graph<String, String> starMap;
	public static Graph<String, String> solarSystem;
	static VisualizationViewer<String,String> vv;
	static Layout<String, String> starMapLayout;
	static Layout<String, String> solarSystemLayout;

	
	public static void process(ArrayList<ClassDependency> analyserOutput) {
		
		// TODO perform full rendering ultimately, for now, use this to perform text output.
		System.out.println();
		System.out.println("Visualiser started.");
		starMap = new SparseMultigraph<String, String>();
		solarSystem = new SparseMultigraph<String, String>();

		starMapLayout = new ISOMLayout<String, String>(starMap);
		solarSystemLayout = new SpringLayout<String, String>(solarSystem);
		displayPackageGraph(analyserOutput);
		
		displayTextOutput(analyserOutput);
		setupCanvas(analyserOutput);

	}

	
	private static void setupCanvas(final ArrayList<ClassDependency> analyserOutput) {
		starMapLayout.setSize(new Dimension(1024,768)); 
		vv = new VisualizationViewer<String,String>(starMapLayout);
		vv.setPreferredSize(new Dimension(1024,768)); 
		DefaultModalGraphMouse<String, String> mouse = new DefaultModalGraphMouse<String, String>();
		mouse.setMode(Mode.PICKING);
        vv.setGraphMouse(mouse);
		final PickedState<String> pickedState = vv.getPickedVertexState();
		// Attach the listener that will print when the vertices selection changes.
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
		        		vv.setGraphLayout(starMapLayout);		

		            }
		        }
		    }
		});
		JFrame frame = new JFrame("Graph Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);   		
	}

	protected static void graphSelectedClassSolarSystem(String vertex, ArrayList<ClassDependency> list) {
		solarSystem = new SparseMultigraph<String, String>();

		for(ClassDependency cd : list){
			if(cd.packageName.equals(vertex)){
				solarSystem.addVertex(cd.packageName);
			}
		}
		for(ClassDependency cd : list){
			if(cd.packageName.equals(vertex)){
				for(Association a : cd.associations){
					solarSystem.addVertex(a.associatedWith);
					solarSystem.addEdge(cd.packageName+"-"+a.associatedWith, cd.packageName,a.associatedWith);
				}
			}
		}
		solarSystemLayout.setGraph(solarSystem);
		vv.setGraphLayout(solarSystemLayout);		

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
