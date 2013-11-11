package visualisation;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Stroke;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.sun.prism.BasicStroke;
import com.sun.prism.paint.Color;

public class JungTest {

    Graph<Integer, String> g;
    /** Creates a new instance of SimpleGraphView 
     * @return */
    public JungTest() {
    	g = new SparseMultigraph<Integer, String>();
    	g.addVertex((Integer)1);
    	g.addVertex((Integer)2);
    	g.addVertex((Integer)3);
    	g.addEdge("Edge-A", 1, 2); 
    	g.addEdge("Edge-B", 2, 3);
    	System.out.println("The graph g = " + g.toString());
    	Graph<Integer, String> g2 = new SparseMultigraph<Integer, String>();
    	g2.addVertex((Integer)1);
    	g2.addVertex((Integer)2);
    	g2.addVertex((Integer)3);
    	g2.addEdge("Edge-A", 1,3);
    	g2.addEdge("Edge-B", 2,3, EdgeType.DIRECTED);
    	g2.addEdge("Edge-C", 3, 2, EdgeType.DIRECTED);
    	g2.addEdge("Edge-P", 2,3); 
    	System.out.println("The graph g2 = " + g2.toString()); 
    }
    
	public static void main(String[] args) {
		 JungTest sgv = new JungTest();
		Layout<Integer, String> layout = new CircleLayout(sgv.g);
		layout.setSize(new Dimension(300,300)); 
		BasicVisualizationServer<Integer,String> vv =
		new BasicVisualizationServer<Integer,String>(layout);
		vv.setPreferredSize(new Dimension(350,350)); 
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
