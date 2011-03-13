package ore.jung;

import java.awt.Dimension;
import java.util.HashSet;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class JungUtil {
	public static Graph<Integer, Integer> generateErdosRenyi(int nodeSize, double p) {
		return new KleinbergSmallWorldGenerator(new org.apache.commons.collections15.Factory<Graph<Integer,Integer>>() {
			@Override
			public Graph<Integer, Integer> create() {
				return new UndirectedSparseGraph();
			}
		}, 
		new org.apache.commons.collections15.Factory<Integer>() {
			private int count = 0;
			@Override
			public Integer create() {
				return (count++);
			}
			
		}, 
		new org.apache.commons.collections15.Factory<Integer>() {
			private int count = 0;
			@Override
			public Integer create() {
				return count++;
			}
		}, 
		nodeSize, p).create();
	}
	
	public static Graph<Integer, Integer> generateKleinberg(int rowSize, int columnSize, boolean isToroidal, double clusteringExponent) {
		return new KleinbergSmallWorldGenerator(new org.apache.commons.collections15.Factory<Graph<Integer,Integer>>() {
			@Override
			public Graph<Integer, Integer> create() {
				return new UndirectedSparseGraph();
			}
		}, 
		new org.apache.commons.collections15.Factory<Integer>() {
			private int count = 0;
			@Override
			public Integer create() {
				return (count++);
			}
			
		}, 
		new org.apache.commons.collections15.Factory<Integer>() {
			private int count = 0;
			@Override
			public Integer create() {
				return count++;
			}
		}, 
		rowSize, columnSize, 
		clusteringExponent, isToroidal).create();
	}
	
	public static Graph<Integer, Integer> generatePowerLaw(int numVertices, int numEdges, int r) {
		return new EppsteinPowerLawGenerator(
				new org.apache.commons.collections15.Factory<Graph<Integer,Integer>>() {
					@Override
					public Graph<Integer, Integer> create() {
						return new UndirectedSparseGraph();
					}
				}, 
				new org.apache.commons.collections15.Factory<Integer>() {
					private int count = 0;
					@Override
					public Integer create() {
						return (count++);
					}
					
				}, 
				new org.apache.commons.collections15.Factory<Integer>() {
					private int count = 0;
					@Override
					public Integer create() {
						return count++;
					}
				}, 
				numVertices, numEdges, r).create(); 
	}
	
	public static  BarabasiAlbertGenerator generateBarbasiAlbert() {
		return new BarabasiAlbertGenerator(
				new org.apache.commons.collections15.Factory<Graph<Integer,Integer>>() {
					@Override
					public Graph<Integer, Integer> create() {
						return new UndirectedSparseGraph();
					}
				}, 
				new org.apache.commons.collections15.Factory<Integer>() {
					private int count = 0;
					@Override
					public Integer create() {
						return (count++);
					}
					
				}, 
				new org.apache.commons.collections15.Factory<Integer>() {
					private int count = 0;
					@Override
					public Integer create() {
						return count++;
					}
				}, 1000, 100, new HashSet<Integer>()); 
	}
	
	public static void main(String[] args) throws InterruptedException {
		Graph<Integer, Integer> g = generatePowerLaw(1000, 100, 5); 
	        // Layout<V, E>, VisualizationComponent<V,E>
	        Layout<Integer, String> layout = new CircleLayout(g);
	        layout.setSize(new Dimension(1000,500));
	        VisualizationViewer<Integer,String> vv = 
	                new VisualizationViewer<Integer,String>(layout);
	        vv.setPreferredSize(new Dimension(1000,500));
	        // Show vertex and edge labels
	        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
	        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
	        // Create a graph mouse and add it to the visualization component
	        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
	        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	        gm.setZoomAtMouse(true);
	       
	        vv.setGraphMouse(gm); 
	        
	        JFrame frame = new JFrame("Interactive Graph View 1");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add(vv);
	        frame.pack();
	        frame.setVisible(true);       
	}
}
