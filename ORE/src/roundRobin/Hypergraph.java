package roundRobin;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class Hypergraph {
	public static void main(String[] args) throws HttpException, IOException {
		HttpClient client = Machine.createClient();
		GetMethod gm = Machine.makeMethod("http://localhost:8080/ORE/hyper?parts=4&factor=5", "none");
		client.executeMethod(gm);
		String response = gm.getResponseBodyAsString();
		System.out.println(response);
	}
}
