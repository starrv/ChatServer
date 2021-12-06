package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketPermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import main.ChatServerThread;
import main.Functions;
import main.OneTimePad;

public class ChatServer implements Runnable, KeyListener
{
	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket server = null;
	private Thread thread = null;
	private int clientCount = 0;
	private SocketPermission p2;
	private int serverPort;
	private String serverHost="localhost";
	
	
	public ChatServer(int port)
	{
		serverPort=createServer(port);
	}
	
	
	private int createServer(int port)
	{
		PrintStream o=null;
		try
		{
			o = new PrintStream(new FileOutputStream("./log.txt",true));
			System.setOut(o); 
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Functions.printMessage("Trying with port "+port);
		try
		{
			Functions.printMessage("Obtaining socket permission for "+serverHost+":"+port+" please wait...");
			p2 = new SocketPermission(serverHost+":"+port,  "listen, accept, connect, resolve");
			Functions.printMessage("Binding to port " + port + " please wait...");
			Functions.printMessage(serverHost+":"+port+" can do these actions: "+p2.getActions());
			server = new ServerSocket(port);
			start();
		}
		catch(Exception e)
		{
			Functions.printMessage("ERROR: "+e.getMessage());
		}
		return port;
	}

	public int getServerPort()
	{
		return serverPort;
	}
	
	public String getServerHost()
	{
		return serverHost;
	}
	
	//same as previous versions (i.e. same as version 3 and 2)	
	public void start(){
		if(thread==null){
			thread = new Thread(this);
			thread.start();
		}
	}	
	//same as previous versions (i.e. same as version 3 and 2)	
	public void stop(){
		if(thread !=null){
			thread=null;
		}
	}
	//same as previous version (i.e. same as version 3)
	public void run() {
		// TODO Auto-generated method stub
		while(thread != null)
		{
			try
			{
				Functions.printMessage("Waiting for a client...");
				addClient(server.accept());
			}
			catch(Exception e)
			{
				Functions.printMessage("error accepting the client "+e.getMessage());
			}
		}
	}
	
	/*public synchronized void handlePrivateEncrypted(String ID, String fromID, String input, String key)
	{
		if(findClient(ID)!=-1)
		{
			String message=OneTimePad.decryptMessage(input, key);
			clients[findClient(ID)].send("Private encrypted message from User " + fromID + ": " +message);
			clients[findClient(fromID)].send("You said: " +message+" encrypted as "+input);
			Functions.printMessage("Private Encrypted message '"+input+"' sent from " + fromID+" to "+ID);
			if(input.equals("bye"))
			{
				removeClient(fromID);
			}
		}
		else
		{
			clients[findClient(fromID)].send("Couldn't send message, no such user with ID " + ID);
		}
	}*/
	
	public synchronized void handle(String fromID, String input, String key)
	{
		String message=OneTimePad.decryptMessage(input, key);
		Functions.printMessage("Message from " + fromID+ ": " + message);
		for(int i=0; i<clientCount; i++)
		{
			if(clients[i].getID()!=fromID)
			{
				clients[i].send("User " + fromID + " said: " +message);
			}
			else
			{
				clients[i].send("You said: " +message);
			}
		}
		if(input.equals("bye"))
		{
			removeClient(fromID);
		}	
	}
	
	public synchronized void handlePrivate(String[] toIDs, String fromID, String input, String key)
	{
		String message=OneTimePad.decryptMessage(input, key);
		for(int i=0; i<toIDs.length; i++)
		{
			if(findClient(toIDs[i])!=-1)
			{
				clients[findClient(toIDs[i])].send("Private message from User " + fromID + ": " +message);
				clients[findClient(fromID)].send("You said: " +message);
				Functions.printMessage("Private message '"+message+"' sent from " + fromID+" to "+toIDs[i]);
				if(message.equals("bye"))
				{
					removeClient(fromID);
				}
			}
			else
			{
				clients[findClient(fromID)].send("Couldn't send message, no such user with ID " + toIDs[i]);
			}
		}
	}
	
	/*private void sendToAllButOne(String message, String ID)
	{
		for(int i=0; i<clientCount; i++)
		{
			if(clients[i].getID()!=ID)
			{
				clients[i].send(message);
			}
		}
	}*/
	
	private void send(String message, String ID)
	{
		for(int i=0; i<clientCount; i++)
		{
			if(clients[i].getID()==ID)
			{
				clients[i].send(message);
				break;
			}
		}
	}
	
	/*private void sendToAll(String message)
	{
		for(int i=0; i<clientCount; i++)
		{
			clients[i].send(message);
		}
	}*/
	
	public synchronized void removeClient(String ID)
	{
		int pos = findClient(ID);
		if(pos >= 0)
		{
			ChatServerThread toTerminate = clients[pos];
			Functions.printMessage("Removing client " +(pos+1)+" with ID "+ ID);
			if ( pos < clientCount-1)
			{
				for (int i = pos+1; i < clientCount; i++)
				{
					clients[i-1] = clients[i];
				}
			}
			clientCount--;
			//sendToAll("Client "+(pos+1)+" with ID "+ ID + " removed");
			printAllClients();
			try
			{
				toTerminate.close();
			}
			catch(IOException ioe)
			{
				Functions.printMessage("Error closing thread: " + ioe);
			}
		}
	}

	
	private int findClient(String ID)
	{
		Functions.printMessage("finding client with ID "+ID);
		for(int i=0; i<clientCount; i++){
			if(clients[i].getID().equals(ID)){
				Functions.printMessage("Found client with ID named "+ID);
				return i;
			}
		}
		Functions.printMessage("No such client with ID "+ID);
		return -1; //if ID not found in array	
	}
	
	private void printAllClients()
	{
		for(int i=0; i<clientCount; i++)
		{
			String ID=clients[i].getID();
			String IDs="ids~The IDs of the group chat participants are: \n";
			for(int j=0; j<clientCount; j++)
			{
				if(clients[j].getID()!=ID)
				{
					IDs+=clients[j].getID()+"\n";
				}
			}
			IDs+="You: "+ID;
			clients[i].send(IDs);
			Functions.printMessage(IDs);
		}
	}
	private synchronized void addClient(Socket socket)
	{
		if(clientCount < clients.length)
		{
			Functions.printMessage("Client "+ (clientCount+1) + " accepted on : " + socket);
			clients[clientCount] = new ChatServerThread(this, socket);
			if(!(clients[clientCount].getID().equalsIgnoreCase("")))
			{																																	
				try
				{
					clients[clientCount].open();
					clients[clientCount].start();
					clientCount++;
					//sendToAllButOne("Client "+ clientCount + " accepted with ID "+clients[clientCount-1].getID(), clients[clientCount-1].getID());
					send("id~"+clients[clientCount-1].getID(),clients[clientCount-1].getID());
					printAllClients();
				}
				catch(IOException ioe)
				{                                                                                                                                                                                                                                            
					Functions.printMessage("Error opening thread: " + ioe.getMessage());
				}			
			}
		}
		else
		{
			Functions.printMessage("Client was refused: maximum " + clients.length + " reached.");
		}
	}
	
	public ChatServerThread[] getClients()
	{
		return clients;
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getKeyCode());
	}
	
	static void printTime()
	{
	   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
	   LocalDateTime now = LocalDateTime.now();  
	   System.out.print("@ "+dtf.format(now)+": ");  
	}

}
