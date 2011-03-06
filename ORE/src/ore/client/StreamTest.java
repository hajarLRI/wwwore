package ore.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class StreamTest {
	public static void main(String[] args) throws HttpException, IOException {
		HttpClient client = Machine.createClient();
		GetMethod method = Machine.makeMethod("http://localhost:8080/ORE/test", "none");
		client.executeMethod(method);
		InputStream is = method.getResponseBodyAsStream();
		Reader r = new InputStreamReader(is);
		char[] buffer = new char[16];
		int size;
		while((size = r.read(buffer)) != -1) {
			char[] data = new char[size];
			System.arraycopy(buffer, 0, data, 0, size);
			String pr = new String(data);
			System.out.print(pr);
		}
	}
}
