package ore.client;

import java.io.IOException;
import java.io.InputStream;

public interface StreamingResponse {
	InputStream getResponseBodyAsStream() throws IOException;
	void releaseConnection();
}
