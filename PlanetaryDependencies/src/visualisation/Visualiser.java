package visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
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

import javafx.stage.Screen;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import control.Main;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
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
import edu.uci.ics.jung.visualization.renderers.Renderer.Edge;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import preprocessing.ClassDependency;
import preprocessing.ClassDependency.Association;
import preprocessing.ClassPacket;

public class Visualiser {
	VFrame frame = new VFrame("Visualiser");
	public ClassPacket test;
	public Graph<StarVertex, String> starMap;
	public Graph<ClassDependency, CustomEdge> solarSystem;
	VisualizationViewer<StarVertex, String> starView;
	VisualizationViewer<ClassDependency, CustomEdge> solarSystemView;

	Layout<StarVertex, String> starMapLayout;
	Layout<ClassDependency, CustomEdge> solarSystemLayout;
	
	public void process(ArrayList<ClassDependency> analyserOutput) {

		// TODO perform full rendering ultimately, for now, use this to perform
		// text output.
		System.out.println();
		System.out.println("Visualiser started.");
		starMap = new SparseMultigraph<StarVertex, String>();
		solarSystem = new SparseMultigraph<ClassDependency, CustomEdge>();
		starMapLayout = new ISOMLayout<StarVertex, String>(starMap);
		solarSystemLayout = new FRLayout<ClassDependency, CustomEdge>(
				solarSystem);
		ArrayList<StarVertex> StarVertices = StarVertex
				.classDependencyToStarVertex(analyserOutput);
		displayPackageGraph(StarVertices);

		displayTextOutput(analyserOutput);
		setupCanvas(analyserOutput);

	}


	private class CustomEdge {
		String from;
		String to;
		int type;
	}
	
	// Changes colour/size for planet/class vertices
	public void shapePlanetVertices(
			VisualizationViewer<ClassDependency, CustomEdge> solarSystemView2) {
		Transformer<ClassDependency, Paint> vertexPaint = new Transformer<ClassDependency, Paint>() {
			public Paint transform(ClassDependency c) {
				// vertex will flash different colours if use the code below
				// return new Color((float) Math.random(),(float)
				// Math.random(),(float) Math.random());
				return c.colour;
			}
		};
		Transformer<ClassDependency, Shape> vertexSize = new Transformer<ClassDependency, Shape>() {
			public Shape transform(ClassDependency i) {
				Ellipse2D circle = new Ellipse2D.Double(-1, -1, 2, 2);
				int r = i.lineCount / 10;
				return AffineTransform.getScaleInstance(r, r)
						.createTransformedShape(circle);
			}
		};
		
		Transformer<ClassDependency, Integer> labelOffset = new Transformer<ClassDependency, Integer>(){
			public Integer transform(ClassDependency i){
				return 5 + (i.lineCount/10);
			}
		};

		Transformer<ClassDependency, String> label = new Transformer<ClassDependency, String>() {
			public String transform(ClassDependency i) {
				return i.className;
			}
		};
		
		//just wanted to see the edges since the background is black
		//this can be deleted when the edges are implemented
		Transformer<CustomEdge, Paint> edgePaint = new Transformer<CustomEdge, Paint>() {
			public Paint transform(CustomEdge ce) {
				switch(ce.type){
				case ClassDependency.COMPOSITION:
					return Color.WHITE;
				case ClassDependency.AGGREGATION:
					return Color.PINK;
				case ClassDependency.REALIZATION:
					return Color.CYAN;
				default:
					return Color.GREEN;
					
				}
			}
		};
		//this can be deleted when the edges are implemented
		solarSystemView2.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
		solarSystemView2.getRenderContext().setArrowDrawPaintTransformer(edgePaint);
		
		solarSystemView2.getRenderContext().setVertexLabelTransformer(label);
		
		solarSystemView2.getRenderContext().setVertexShapeTransformer(vertexSize);
		solarSystemView2.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
		solarSystemView2.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		solarSystemView2.setForeground(Color.WHITE);
		solarSystemView2.setBackground(new Color(0, 0, 0, 0));

	}

	// Changes colour/size for star/package vertices
	public void shapeStarVertices(
			VisualizationViewer<StarVertex, String> vv) {
		Transformer<StarVertex, Paint> vertexPaint = new Transformer<StarVertex, Paint>() {
			public Paint transform(StarVertex s) {
				return new Color(255, 255, 0);
			}
		};

		Transformer<StarVertex, Shape> vertexSize = new Transformer<StarVertex, Shape>() {
			public Shape transform(StarVertex s) {
				Ellipse2D circle = new Ellipse2D.Double(-1, -1, 2, 2);
				int radius = s.starSize*2;
				return AffineTransform.getScaleInstance(radius, radius)
						.createTransformedShape(circle);
			}
		};

		Transformer<StarVertex, String> label = new Transformer<StarVertex, String>() {
			public String transform(StarVertex i) {
				return i.toString();
			}
		};
		
		Transformer<String, Paint> edgePaint = new Transformer<String, Paint>() {
			public Paint transform(String s) {
				return Color.WHITE;
			}
		};
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);

		vv.getRenderContext().setVertexLabelTransformer(label);
		vv.getRenderContext().setVertexShapeTransformer(vertexSize);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		System.out.println("components in frame : " + vv.getComponentCount());
		
		for(Component j :vv.getComponents()){
			System.out.println(j);
		}
		vv.setBackground(new Color(0, 0, 0, 0));
		vv.setForeground(Color.WHITE);
	}

	private void setupCanvas(
			final ArrayList<ClassDependency> analyserOutput) {
		Dimension d = new Dimension((int)Screen.getPrimary().getBounds().getWidth(), (int)Screen.getPrimary().getBounds().getHeight());
		starMapLayout.setSize(d);
		starView = new VisualizationViewer<StarVertex, String>(starMapLayout);
		solarSystemView = new VisualizationViewer<ClassDependency, CustomEdge>(
				solarSystemLayout);
		solarSystemLayout.setSize(d);

		solarSystemView.setSize(d);

		addHandlers(analyserOutput);
		shapePlanetVertices(solarSystemView);
		shapeStarVertices(starView);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);  

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(starView);
		frame.setVisible(true);
	}

	private void addHandlers(
			final ArrayList<ClassDependency> analyserOutput) {
		DefaultModalGraphMouse<String, String> mouse = new DefaultModalGraphMouse<String, String>();
		mouse.setMode(Mode.PICKING);
		starView.setGraphMouse(mouse);
		solarSystemView.setGraphMouse(mouse);
		final PickedState<StarVertex> pickedState = starView
				.getPickedVertexState();
		final PickedState<ClassDependency> pickedState2 = solarSystemView
				.getPickedVertexState();

		pickedState.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Object subject = e.getItem();
				// The graph uses Integers for vertices.
				if (subject instanceof StarVertex) {
					StarVertex vertex = (StarVertex) subject;

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
						System.out.println("Planet: " + vertex.className
								+ "\nSolar System: " + vertex.packageName);
					} else {
					}
				}

			}
		});
		solarSystemView.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					frame.remove(solarSystemView);
					System.out.println("components in frame : " + frame.getContentPane().getComponents().length);
					for(Component J: frame.getContentPane().getComponents()){
						System.out.println(J);
					}
					frame.getContentPane().add(starView);
					
					frame.revalidate();
					frame.repaint();
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

	protected void graphSelectedClassSolarSystem(StarVertex vertex,
			ArrayList<ClassDependency> list) {
		solarSystem = new SparseMultigraph<ClassDependency, CustomEdge>();

		for (ClassDependency cd : list) {
			if (cd.packageName.equals(vertex.toString())) {
				solarSystem.addVertex(cd);
			}
		}
		for (ClassDependency cd : list) {
			if (cd.packageName.equals(vertex.toString())) {
				for (Association a : cd.associations) {
					ClassDependency temp = findClassDependency(
							a.associatedWith, list);
					solarSystem.addVertex(temp);
					CustomEdge ce = new CustomEdge();
					ce.from=cd.className;
					ce.to=a.associatedWith;
					ce.type=a.associationType;
					if(ce.type==ClassDependency.UNIDIRECTIONAL_ASSOCIATION){
						solarSystem.addEdge(ce,
								cd, temp,EdgeType.DIRECTED);
					}
					else{
						solarSystem.addEdge(ce,
								cd, temp,EdgeType.UNDIRECTED);
					}					
					
				}
			}
		}
		solarSystemLayout.setGraph(solarSystem);
		solarSystemView.setGraphLayout(solarSystemLayout);
		frame.getContentPane().remove(starView);
		System.out.println("components in frame (graph selected class: " + frame.getContentPane().getComponents().length);
		for(Component J: frame.getContentPane().getComponents()){
			System.out.println(J);
		}
		frame.getContentPane().add(solarSystemView);
		
		frame.revalidate();
	}

	private ClassDependency findClassDependency(String associatedWith,
			ArrayList<ClassDependency> list) {

		for (ClassDependency cd : list) {
			if (cd.className.equals(associatedWith))
				return cd;
		}
		return null;
	}

	private void displayPackageGraph(ArrayList<StarVertex> starVertices) {
		for (StarVertex sv : starVertices) {
			starMap.addVertex(sv);
		}
		for (StarVertex sv : starVertices) {
			for (Association a : sv.associations) {
				StarVertex s = packageNameOf(a.associatedWith, starVertices);
				System.out.println(sv.toString() + "associated w/ "
						+ s.toString());
				starMap.addEdge(sv.starName + "-" + s, sv, s);
			}
		}

	}

	private StarVertex packageNameOf(String associatedWith,
			ArrayList<StarVertex> starVertices) {
		for (StarVertex sv : starVertices) {
			if (sv.starName.equals(associatedWith)) {
				return sv;
			}
		}
		return null;
	}

	// not used
	/*
	 * private static void drawClassGraph(ArrayList<ClassDependency> list) {
	 * 
	 * // populate vertices for (ClassDependency cd : list) {
	 * //starMap.addVertex(cd.className); }
	 * 
	 * // populate edges for (ClassDependency cd : list) { for (Association a :
	 * cd.associations) { String association = ""; switch (a.associationType) {
	 * case 0: association = "COMPOSITION"; break; case 1: association =
	 * "AGGREGATION"; break; case 2: association = "REALIZATION"; break; case 3:
	 * association = "UNI-DIRECTIONAL ASSOCIATION"; break; case 4: association =
	 * "BI-DIRECTIONAL ASSOCIATION"; break; } starMap.addEdge(cd.className + "-"
	 * + a.associatedWith, cd.className, a.associatedWith); } } }
	 */

	private static void displayTextOutput(
			ArrayList<ClassDependency> analyserOutput) {
		for (ClassDependency cd : analyserOutput) {
			System.out
					.println("-------------------Scanning Planet-----------------");
			System.out.println("Planet: " + cd.className);
			System.out.println("  Planet radius: " + cd.planetaryRadius);
			System.out.println("  Solar System: " + cd.packageName);
			if (cd.associations.size() == 0) {
				System.out.println("Associations: No outgoing associations.");
			} else {
				System.out.println("  Associated with:");
				for (Association a : cd.associations) {
					System.out.print("  - " + a.associatedWith
							+ ", Association Type: ");
					switch (a.associationType) {
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
			System.out
					.println("----------------------End Scan---------------------");
			System.out.println();

		}
	}

}