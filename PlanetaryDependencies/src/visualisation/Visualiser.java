package visualisation;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;

import control.Main;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import preprocessing.ClassDependency;
import preprocessing.ClassDependency.Association;
import preprocessing.ClassPacket;

public class Visualiser {
	public static ClassPacket test;
	public static Graph<String, String> g;

	public static void process(ArrayList<ClassDependency> analyserOutput) {
		
		// TODO perform full rendering ultimately, for now, use this to perform text output.
		System.out.println();
		System.out.println("Visualiser started.");
    	g = new SparseMultigraph<String, String>();
    	
    	//populate vertices
		for(ClassDependency cd : analyserOutput){    		
			g.addVertex(cd.className);
		}
		
		//populate edges
		for(ClassDependency cd : analyserOutput){    		
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
				g.addEdge(cd.className+"-"+a.associatedWith, cd.className,a.associatedWith);
			}
		}
		
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
		Layout<String, String> layout = new ISOMLayout<String, String>(g);
		layout.setSize(new Dimension(1024,768)); 
		BasicVisualizationServer<String,String> vv =
		new BasicVisualizationServer<String,String>(layout);
		vv.setPreferredSize(new Dimension(1024,768)); 
		/* Transformer<Integer,Paint> vertexPaint = new Transformer<Integer,Paint>() {
			 public Paint transform(Integer i) {
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
			 vv.getRenderContext().setVertexDrawPaintTransformer(vertexPaint);
			 vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer); */
			 
			 JFrame frame = new JFrame("Graph Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);      

	}


}
