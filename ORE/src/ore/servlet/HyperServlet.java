package ore.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ore.api.ORE;
import ore.client.HMetis;
import ore.hypergraph.Hypergraph;

public class HyperServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			String parts = request.getParameter("parts");
			String factor = request.getParameter("factor");
			Hypergraph graph = ORE.getGraph();
			HMetis.shmetis(graph, Integer.parseInt(parts), Integer.parseInt(factor));
			graph.toDot(pw);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}
	
}
