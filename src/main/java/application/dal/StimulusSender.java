package application.dal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.stream.Stream;

import org.tinylog.Logger;

/*
 * Primitive TCP Tagging java client for OpenViBE 1.2.x
*/

public class StimulusSender {
	private final String host;
	private final int port;
	private Socket clientSocket;
	private DataOutputStream outputStream;

	public StimulusSender(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	// Open connection to Acquisition Server TCP Tagging
	public void open() throws IOException {
		try {
			Logger.info("Opening socket on: " + host + ":" + port);
			clientSocket = new Socket(host, port);
			Logger.info("Opening a data stream for port " + clientSocket.getPort());
			outputStream = new DataOutputStream(clientSocket.getOutputStream());	
		} catch (IOException e) {
			throw new IOException("Something went wrong while trying to connect to port " + this.port + "\n" + e);
		}
	}

	// Close connection
	public void close() throws IOException {
		if(outputStream != null) {
			outputStream.flush();
			Logger.info("Closing data stream for socket");
			outputStream.close();			
		}
		if(clientSocket != null) {
			Logger.info("Cosing socket:" + port);
			clientSocket.close();			
		}
	}

	// Send stimulation with a timestamp.
	public void send(long stimulation, long timestamp) throws IOException {
		var b = ByteBuffer.allocate(24);
		b.order(ByteOrder.LITTLE_ENDIAN); // Assumes AS runs on LE architecture
		b.putLong(0);
		b.putLong(stimulation); 
		b.putLong(timestamp); 
		
		if(clientSocket != null)
		  Logger.info("Writing to socket on port " + clientSocket.getPort());

		Stream.of(b).forEach(Logger::info);		
		
		if(outputStream != null)
		  outputStream.write(b.array());
	}
}