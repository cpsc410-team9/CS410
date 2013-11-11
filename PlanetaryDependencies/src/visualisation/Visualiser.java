package visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

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

	public static void shapeVertices(VisualizationViewer vv){
		 Transformer<String,Paint> vertexPaint = new Transformer<String,Paint>() {
			 public Paint transform(String i) {
			 return Color.GREEN;
			 }
			 };
			 
			 //Stroke to add colour and dashes
			 float dash[] = {10.0f};
			 final Stroke edgeStroke =  new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			 BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
			 Transformer<String, Stroke> edgeStrokeTransformer =
			 new Transformer<String, Stroke>() {
			 public Stroke transform(String s) {
			 return  edgeStroke;
			 }
			 };
			 
		        Transformer<String,Shape> vertexSize = new Transformer<String,Shape>(){
		            public Shape transform(String i){
		                Ellipse2D circle = new Ellipse2D.Double(-15, -15, 30, 30);
		                // in this case, the vertex is twice as large
		                if(i=="Account") return AffineTransform.getScaleInstance(2, 2).createTransformedShape(circle);
		                else return circle;
		            }
		        };
		        
		        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		        
				 vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
				 vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer); 


	}
	
	
	private static void setupCanvas(final ArrayList<ClassDependency> analyserOutput) {
		starMapLayout.setSize(new Dimension(1024,768)); 
		starView = new VisualizationViewer<String,String>(starMapLayout);
		starView.setPreferredSize(new Dimension(1024,768)); 
		solarSystemView = new VisualizationViewer<ClassDependency,String>(solarSystemLayout);
		solarSystemView.setPreferredSize(new Dimension(1024,768)); 

		addHandlers(analyserOutput);
		shapeVertices(starView);
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
		                System.out.println("Solar System: " + vertex);
		            	graphSelectedClassSolarSystem(vertex, analyserOutput);
		            } else {


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
		                System.out.println("Planet: " + vertex.className+"\nSolar System: "+vertex.packageName);
		            } else {
		            }
		        }

		    }
		});	
		solarSystemView.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==3){
					frame.getContentPane().add(starView);
					frame.remove(solarSystemView);
					frame.pack();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
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
		frame.getContentPane().removeAll();
		frame.getContentPane().add(solarSystemView);
		frame.pack();
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
