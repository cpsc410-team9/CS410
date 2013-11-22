	package visualisation;
	
	import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import javafx.stage.Screen;
	
	import org.apache.commons.collections15.Transformer;
	
	import preprocessing.ClassDependency;
import preprocessing.ClassDependency.Association;
import preprocessing.ClassPacket;
	
import javax.imageio.ImageIO;
import javax.swing.*;
	
	import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
	public class Visualiser {
		JFrame frame = new JFrame("Visualiser");
		public ClassPacket test;
		public Graph<StarVertex, String> starMap;
		public Graph<ClassDependency, CustomEdge> solarSystem;
		VisualizationViewer<StarVertex, String> starView;
		VisualizationViewer<ClassDependency, CustomEdge> solarSystemView;
	
		Layout<StarVertex, String> starMapLayout;
		Layout<ClassDependency, CustomEdge> solarSystemLayout;
		String currentSolarSystem = "";
	/**
	 *  Takes in list of ClassDependencies and outputs a graph
	 * @param analyserOutput
	 *void
	 */
		public void process(ArrayList<ClassDependency> analyserOutput) {
	
			System.out.println("\nVisualiser started.");
			starMap = new SparseMultigraph<StarVertex, String>();
			solarSystem = new SparseMultigraph<ClassDependency, CustomEdge>();
			starMapLayout = new ISOMLayout<StarVertex, String>(starMap);
			solarSystemLayout = new FRLayout<ClassDependency, CustomEdge>(solarSystem);
	
	
	
			ArrayList<StarVertex> StarVertices = StarVertex.classDependencyToStarVertex(analyserOutput);
	
			displayPackageGraph(StarVertices);
			displayTextOutput(analyserOutput);
			setupCanvas(analyserOutput);
		}
	/**
	 * Custom class for edges, contains the information for associations between vertices
	 *
	 */
		private class CustomEdge {
			String from;
			String to;
			int type;
		}
	
	/**
	 * Alters the planet vertices by changing the colour and size depending on its information
	 * @param VisualizationViewer
	 * @return void
	 */
		public void shapePlanetVertices(final VisualizationViewer<ClassDependency, CustomEdge> vv) {
	
			Transformer<ClassDependency, String> label = new Transformer<ClassDependency, String>() {
				public String transform(ClassDependency i) {
					return i.className;
				}
			};
	
			Transformer<ClassDependency, Font> planetFontTransform = new Transformer<ClassDependency, Font>() {
				@Override
				public Font transform(ClassDependency classDependency) {
					return new Font("Helvetica", Font.BOLD, 20);
				}
			};
	
			Transformer<CustomEdge, Stroke> planetEdgeThickness = new Transformer<CustomEdge, Stroke>() {
				float planetEdgeDash[] = {5.0f};
				@Override
				public Stroke transform(CustomEdge customEdge) {
					return new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 10.0f, planetEdgeDash, 0.0f);
				}
			};
	
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
			Transformer<ClassDependency, Icon> planetIcon = new Transformer<ClassDependency, Icon>(){
				@Override
				public Icon transform(final ClassDependency c) {
					final int radius = c.lineCount/20;
					return new Icon(){
	
						@Override
						public int getIconHeight() {
							return radius;
						}
	
						@Override
						public int getIconWidth() {
							return radius;
						}
	
						@Override
						public void paintIcon(Component arg0, Graphics g, int x, int y) {
							Graphics2D g2 = (Graphics2D)g;
						    GradientPaint p= new GradientPaint(5, 25, c.colour.darker().darker().darker(), 2, 2, c.colour, true);
						    g2.setPaint(p);
							g2.fillOval(x,y,radius,radius);
							try{
								if(c.packageName.equals(currentSolarSystem)){
									g2.setColor(Color.YELLOW);
									float outline = (float) (radius*(0.1) > 5? radius*(0.1) : 5f);
									g2.setStroke(new BasicStroke(outline));
								}
							}catch(Exception e){};
							g2.drawOval(x, y, radius, radius);
						}
					};
				}
	
			};
	
			//Edge and Arrow Properties
			vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
			vv.getRenderContext().setArrowDrawPaintTransformer(edgePaint);
			vv.getRenderContext().setEdgeStrokeTransformer(planetEdgeThickness);
	
			//Vertex Properties
			vv.getRenderContext().setVertexLabelTransformer(label);
			vv.getRenderContext().setVertexFontTransformer(planetFontTransform);
			vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
			vv.setBackground(Color.black);
			vv.setForeground(Color.white);
			vv.getRenderContext().setVertexIconTransformer(planetIcon);
		}
	
		/**
		 * Greys out everything but the selected vertices and their edges
		 * @param VisualizationViewer
		 * @param ClassDependency
		 * 
		 */
		public void selectedPlanetVertices(final VisualizationViewer<ClassDependency, CustomEdge> vv, final ClassDependency cd) {
			Transformer<ClassDependency, String> label = new Transformer<ClassDependency, String>() {
				public String transform(ClassDependency i) {
					return i.className;
				}
			};
			Transformer<CustomEdge, Stroke> planetEdgeThickness = new Transformer<CustomEdge, Stroke>() {
				float planetEdgeDash[] = {5.0f};
				@Override
				public Stroke transform(CustomEdge ce) {
					if(ce.from.equals(cd.className)){
						return new BasicStroke(2.0f);
					}
					else{
						return new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 10.0f, planetEdgeDash, 0.0f);
	
					}
				}
			};
			Transformer<ClassDependency, Font> planetFontTransform = new Transformer<ClassDependency, Font>() {
				@Override
				public Font transform(ClassDependency classDependency) {
					return new Font("Helvetica", Font.BOLD, 20);
				}
			};
	
			Transformer<CustomEdge, Paint> edgePaint = new Transformer<CustomEdge, Paint>() {
				public Paint transform(CustomEdge ce) {
					if(ce.from.equals(cd.className)){
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
					else return Color.gray;
				}
			};
			Transformer<ClassDependency, Icon> planetIcon = new Transformer<ClassDependency, Icon>(){
	
				@Override
				public Icon transform(final ClassDependency c) {
					final int radius = c.lineCount/20;
					return new Icon(){
	
						@Override
						public int getIconHeight() {
							return radius;
						}
	
						@Override
						public int getIconWidth() {
							return radius;
						}
	
						@Override
						public void paintIcon(Component arg0, Graphics g, int x, int y) {
							if(c.className.equals(cd.className)){
								Graphics2D g2 = (Graphics2D) g;

							    GradientPaint p= new GradientPaint(5, 25, c.colour.darker().darker().darker(), 2, 2, c.colour, true);
							    g2.setPaint(p);
								g2.fillOval(x,y,radius,radius);
								try{
									if(c.packageName.equals(currentSolarSystem)){
										g2.setColor(Color.RED);
										float outline = (float) (radius*(0.1) > 5 ? radius*(0.1) : 5f);
										g2.setStroke(new BasicStroke(outline));
									}
								}catch(Exception e){
									e.printStackTrace();
								};
							}
							else{
								Graphics2D g2 = (Graphics2D)g;
								g2.setColor(Color.gray);
								for(Association a : cd.associations){
									if(a.associatedWith.equals(c.className)){
										g2.setColor(c.colour);
										break;
									}
								}
								g2.fillOval(x,y,radius,radius);
								g2.drawOval(x, y, radius, radius);
							}
						}
					};
				}
	
			};
			vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
			vv.getRenderContext().setArrowDrawPaintTransformer(edgePaint);
			vv.getRenderContext().setEdgeStrokeTransformer(planetEdgeThickness);

			vv.getRenderContext().setVertexLabelTransformer(label);
			vv.getRenderContext().setVertexFontTransformer(planetFontTransform);
	
			vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
			vv.setBackground(Color.black);
			vv.setForeground(Color.white);
	
			vv.getRenderContext().setVertexIconTransformer(planetIcon);
		}
	
		/**
		 * Alters the star vertices by changing the colour and size depending on its information
		 * @param VisualizationViewer
		 * @return void
		 */
		public void shapeStarVertices(VisualizationViewer<StarVertex, String> vv) {
			Transformer<StarVertex, Paint> vertexPaint = new Transformer<StarVertex, Paint>() {
				public Paint transform(StarVertex s) {
					//Gradient Transformer is below.
					return new Color(255,255,0);
				}
			};
	
			Transformer<StarVertex, Shape> vertexShape = new Transformer<StarVertex, Shape>() {
				public Shape transform(StarVertex s) {
					Ellipse2D circle = new Ellipse2D.Double(-1, -1, 2, 2);
					int radius = s.starSize*2;
	
					return AffineTransform.getScaleInstance(radius, radius).createTransformedShape(circle);
				}
			};
	
			Transformer<StarVertex, String> label = new Transformer<StarVertex, String>() {
				public String transform(StarVertex i) {
					int relevantIndex = 13;
					if (i.starName.contains("stuffplotter"))
						return i.starName.substring(relevantIndex);
					else return i.toString();
				}
			};
	
			Transformer<StarVertex, Font> fontTransform = new Transformer<StarVertex, Font>() {
				@Override
				public Font transform(StarVertex starVertex) {
					return new Font("Helvetica", Font.BOLD, 15);
				}
			};
	
			Transformer<String, Paint> edgePaint = new Transformer<String, Paint>() {
				public Paint transform(String s) {
					return new Color(177,156,217);
				}
			};
			//From StackOverflow
			Transformer<String, Stroke> edgeThickness = new Transformer<String, Stroke>() {
				float dash[] = { 5.0f };
				public Stroke transform(String s) {
					return new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 10.0f, dash, 0.0f);
				}
			};
	
			//Transformer Edge Setters
			vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
			vv.getRenderContext().setEdgeStrokeTransformer(edgeThickness);
	
			//Transformer Vertex Setters
			vv.getRenderContext().setVertexLabelTransformer(label);
			vv.getRenderContext().setVertexFontTransformer(fontTransform);
			vv.getRenderContext().setVertexShapeTransformer(vertexShape);
			vv.getRenderer().getVertexLabelRenderer().setPosition(Position.N);
			vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
			//GradientVertex.
			vv.getRenderer().setVertexRenderer(new GradientVertexRenderer<StarVertex,String>(Color.white, Color.red, false));
	
			System.out.println("components in frame : " + vv.getComponentCount());
	
			for(Component j :vv.getComponents()){
				System.out.println(j);
			}
			vv.setBackground(Color.black);
			vv.setForeground(Color.lightGray);
		}
	
		/**
		 * Sets the star and solarsystem maps for the graphs
		 * @param analyserOutput
		 *void
		 */
		private void setupCanvas(final ArrayList<ClassDependency> analyserOutput) {
			starMapLayout.setSize(GetDimension());
			starView = new VisualizationViewer<StarVertex, String>(starMapLayout);
			solarSystemView = new VisualizationViewer<ClassDependency, CustomEdge>(solarSystemLayout);
			solarSystemLayout.setSize(GetDimension());
			solarSystemView.setSize(GetDimension());
	
			addHandlers(analyserOutput);
			shapePlanetVertices(solarSystemView);
			shapeStarVertices(starView);
	
			SetSwingValues();
		}
	
		/**
		 * Gets the current screen size
		 * @return
		 *Dimension
		 */
		private Dimension GetDimension(){
			int width = (int) Screen.getPrimary().getBounds().getWidth()-200;
			int height = (int) Screen.getPrimary().getBounds().getHeight();
			return new Dimension(width, height);
		}
	
		private void SetSwingValues(){
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(starView);
			frame.setVisible(true);
		}
	/**
	 *  adds handlers for mouse events
	 * @param analyserOutput
	 *void
	 */
		private void addHandlers(final ArrayList<ClassDependency> analyserOutput) {
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
							selectedPlanetVertices(solarSystemView, vertex);
						} else {
						}
					}
	
				}
			});
			solarSystemView.addMouseListener(new MouseListener() {
	/**
	 * Mouse clicking events for star map,
	 */
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
	
		/**
		 * 
		 * when a star is clicked the respective solarsystem view will appear
		 * @param vertex
		 * @param list
		 *void
		 */
		protected void graphSelectedClassSolarSystem(StarVertex vertex, ArrayList<ClassDependency> list) {
			solarSystem = new SparseMultigraph<ClassDependency, CustomEdge>();
			frame.setLayout(null);
			frame.getContentPane().setBackground(Color.BLACK);
			for (ClassDependency cd : list) {
				if (cd.packageName.equals(vertex.toString())) {
					solarSystem.addVertex(cd);
					currentSolarSystem = vertex.toString();
				}
			}
			for (ClassDependency cd : list) {
				if (cd.packageName.equals(vertex.toString())) {
					for (Association a : cd.associations) {
						ClassDependency temp = findClassDependency(a.associatedWith, list);
						solarSystem.addVertex(temp);
						CustomEdge ce = new CustomEdge();
						ce.from=cd.className;
						ce.to=a.associatedWith;
						ce.type=a.associationType;
						if(ce.type==ClassDependency.UNIDIRECTIONAL_ASSOCIATION){
							solarSystem.addEdge(ce, cd, temp,EdgeType.DIRECTED);
						}else{
							solarSystem.addEdge(ce,cd, temp,EdgeType.UNDIRECTED);
						}
					}
				}
			}
			solarSystem.addVertex(new ClassDependency(currentSolarSystem, 2000));
			solarSystemLayout.setGraph(solarSystem);
			solarSystemView.setGraphLayout(solarSystemLayout);
			frame.getContentPane().remove(starView);
			frame.getContentPane().add(solarSystemView);
			try {
				   Image image = ImageIO.read(new File(System.getProperty("user.dir")+"//resources//legend2.png"));
				   JLabel jl = new JLabel();
				   image = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
				   jl.setIcon(new ImageIcon(image));
				   jl.setBounds(new Rectangle(200,200));
				   jl.setLocation((int)Screen.getPrimary().getBounds().getWidth()-200,100);
				   frame.getContentPane().add(jl);

				  } catch (IOException e) {
				   // TODO Auto-generated catch block
				   e.printStackTrace();
				  }
				  frame.revalidate();
				  frame.repaint();
		}
	
		/**
		 * 
		 * @param associatedWith
		 * @param list
		 * @return
		 *ClassDependency
		 */
		private ClassDependency findClassDependency(String associatedWith, ArrayList<ClassDependency> list) {
			for (ClassDependency cd : list) {
				if (cd.className.equals(associatedWith))
					return cd;
			}
			return null;
		}
	
		/**
		 * Displays the graph of packages
		 * @param starVertices
		 *void
		 */
		private void displayPackageGraph(ArrayList<StarVertex> starVertices) {
			for (StarVertex sv : starVertices) {
				starMap.addVertex(sv);
			}
			for (StarVertex sv : starVertices) {
				for (Association a : sv.associations) {
					StarVertex s = packageNameOf(a.associatedWith, starVertices);
					System.out.println(sv.toString() + "associated w/ " + s.toString());
					starMap.addEdge(sv.starName + "-" + s, sv, s);
				}
			}
	
		}
	
		/**
		 * Gets the package name of the star associated with it
		 * @param associatedWith
		 * @param starVertices
		 * @return
		 *StarVertex
		 */
		private StarVertex packageNameOf(String associatedWith, ArrayList<StarVertex> starVertices) {
			for (StarVertex sv : starVertices) {
				if (sv.starName.equals(associatedWith)) {
					return sv;
				}
			}
			return null;
		}
	
		/**
		 *  Text out put for our Visualiser
		 * @param analyserOutput
		 *void
		 */
		private static void displayTextOutput(ArrayList<ClassDependency> analyserOutput) {
			for (ClassDependency cd : analyserOutput) {
				System.out.println("-------------------Scanning Planet-----------------");
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
				System.out.println("----------------------End Scan---------------------\n");
			}
		}
	}