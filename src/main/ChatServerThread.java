package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import main.ChatServer;
import main.Functions;
import main.IDGenerator;

public class ChatServerThread extends Thread
{
	private ChatServer server = null;
	private Socket socket = null;
	private String ID="";
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;
	private static final int ID_SIZE=5;

	public ChatServerThread(ChatServer _server, Socket _socket) {
		super();
		server = _server;
		socket = _socket;
		// ID = socket.getPort();
		IDGenerator idGenerator=new IDGenerator(50);
		ID = idGenerator.generateID(ID_SIZE);
		Functions.printMessage("Chat Server Thread Info: server: " + server
				+ " socket: " + socket + " ID: " + ID);
	}

	public void send(String msg) 
	{
		try 
		{
			streamOut.writeUTF(msg);
			streamOut.flush();
		} 
		catch (IOException ioe) 
		{
			Functions.printMessage(ID + " ERROR sending: " + ioe.getMessage());
			server.removeClient(ID);
			ID = "";// set ID -1 for the thread...
			//System.exit(0);
		}
	}

	public String getID() {
		return ID;
	}

	public void run() {
		Functions.printMessage("Server Thread " + ID + " running.");
		while (ID != "") {
			try {
				// get message
				String line = streamIn.readUTF();
				Functions.printMessage("Line: "+line);
				StringTokenizer tokenizer = new StringTokenizer(line, "~");
				String key="";
				
					
					if(tokenizer.countTokens()>=2)
					{
						//if send to all
						if (tokenizer.countTokens() == 2) {
							String message=tokenizer.nextToken();
							String oldMessage=message;
							key=tokenizer.nextToken();
							message=OneTimePad.decryptMessage(message, key);
							Functions.printMessage(oldMessage+" decrypted as "+message);
							server.handle(ID, message,key);
						} 
						else 
						{
							//Functions.printMessage(line + " has " + tokenizer.countTokens()+ " tokens.");
							//if private
							if(tokenizer.countTokens()==4)
							{
								String prefix=tokenizer.nextToken();
								String toIDsText = tokenizer.nextToken();
								String message = tokenizer.nextToken();
								String oldMessage=prefix+"~"+toIDsText+"~"+message;
								key = tokenizer.nextToken();
								message=OneTimePad.decryptMessage(oldMessage, key);
								Functions.printMessage(oldMessage+" decrypted as "+message);
								
								tokenizer=new StringTokenizer(message,"~");
								prefix=tokenizer.nextToken();
								String[] toIDs=tokenizer.nextToken().split(" ");
								message=tokenizer.nextToken();
								server.handlePrivate(toIDs, ID, message, key);
							}
							//if server sending info
							else if(tokenizer.countTokens()==3)
							{
								String prefix=tokenizer.nextToken();
								String message = prefix+"~"+tokenizer.nextToken();
								String oldMessage=message;
								key = tokenizer.nextToken();
								message=OneTimePad.decryptMessage(message, key);
								Functions.printMessage(oldMessage+" decrypted as "+message);
								server.handle(ID, message, key);
							}
						}
					}
				
				
			} catch (IOException ioe) {
				// Functions.printMessage(ID + "ERROR reading: " +
				// ioe.getMessage());
				server.removeClient(ID);
				ID = "";// set ID to -1 so it will not enter the loop again
						// instead of deprecated stop()
			}
		}
	}

	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(
				socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(
				socket.getOutputStream()));
	}

	public void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
		if (streamIn != null) {
			streamIn.close();
		}
		if (streamOut != null) {
			streamOut.close();
		}
	}
}
