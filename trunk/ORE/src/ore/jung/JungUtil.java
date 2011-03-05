package ore.jung;

import java.awt.Dimension;
import java.util.HashSet;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class JungUtil {
	public static Graph generateKleinberg() {
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
		10, 
		.5).create();
	}
	
	public static Graph generatePowerLaw() {
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
				100, 200, 20).create(); 
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
		BarabasiAlbertGenerator g = generateBarbasiAlbert();
		SpringLayout<Integer, Integer> layout = new SpringLayout(g.create());
	    layout.setSize(new Dimension(700,700)); // sets the initial size of the space
	     // The BasicVisualizationServer<V,E> is parameterized by the edge types
	     BasicVisualizationServer<Integer,Integer> vv = 
	              new BasicVisualizationServer<Integer,Integer>(layout);
	     vv.setPreferredSize(new Dimension(750,750)); //Sets the viewing area size
	        
	     JFrame frame = new JFrame("Simple Graph View");
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.getContentPane().add(vv); 
	     frame.pack();
	     frame.setVisible(true);
	     while(true) {
	    	 Thread.sleep(1000);
	    	 g.evolveGraph(1);
	     }
	}
}
