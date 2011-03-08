package ore.client.generators;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;


public class FileResourceGenerator implements WorkloadGenerator {

	@Override
	public List generate() throws Exception {
		FileInputStream fis = new FileInputStream("C:\\Temp\\test.text");
		ObjectInputStream in = new ObjectInputStream(fis);
		List users = (LinkedList) in.readObject();
		in.close();
		return users;
	}

}
