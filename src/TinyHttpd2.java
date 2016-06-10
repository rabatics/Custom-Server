import java.net.Authenticator;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.File;

public class TinyHttpd2{
	private static final int PORT = 4000;
	private ServerSocket serverSocket;
	private AccessCounter ac=new AccessCounter();
	private FileCache fc=new FileCacheLRU();
	

	public void init(){
		try{
			try{
				StaticThreadPool pool = StaticThreadPool.getInstance(2, true);
				DBConnection.init();
				//Connection conn=DBConnection.getConnection();
				
				
				serverSocket = new ServerSocket(PORT);
				
				System.out.println("Socket created.");
				System.out.println( "Listening to a connection on the local port " +
						serverSocket.getLocalPort() + "..." );
				while(true){	
					
					
					
				
					Socket	client = serverSocket.accept();
					
					System.out.println( "\nA connection established with the remote port " + 
										client.getPort() + " at " +
										client.getInetAddress().toString() );
					Runnable r=()->{
						executeCommand( client );
					};
					pool.execute(r);
					//new Thread(r).start();
					//new Thread(new TinyHttpd3(client)).start();
				}
			}
			finally{
			
				serverSocket.close();
			}
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}

	public void executeCommand( Socket client ){
		try {
			client.setSoTimeout(30000);
			BufferedReader in = new BufferedReader( new InputStreamReader( client.getInputStream() ) );  
			PrintStream out = new PrintStream( client.getOutputStream() );  
			String inputFile = "index.html";
			Boolean http11=false;
			try {
				System.out.println( "I/O setup done" );
				
				String line = in.readLine();
				StringTokenizer st = new StringTokenizer(line);
				String command = st.nextToken();
			//	http11=line.contains("HTTP/1.1");
				if(command.equals("GET") || command.equals("HEAD")){
					doGet(line,st,out,command,client);
				}
				else if(command.equals("POST")){
					doPost(line,st,out,command,in);
				}
				else{
					out.println("HTTP/1.0 501 Not Implemented");
				}
				
				while( line != null ) {
					System.out.println(line);
					if(line.equals("")){
						break;
					}
					line = in.readLine();
				}
				System.out.println(line);
				out.flush();
			}
			finally{
				in.close();
				out.close();
			//	Boolean ka=client.getKeepAlive();
			//	
				if(!http11){
				client.close();
				System.out.println( "A connection is closed." );	
				}
				else{
					client.setSoTimeout(5000);
					
				}
							
			}
		}
		catch(Exception exception) {
			exception.printStackTrace();
		}
	} 
	
	private void sendFile(PrintStream out, File file,byte[] content){
		try{
			fc.fetch(file.toPath());
			ac.increment(file.toPath());
			DataInputStream fin = new DataInputStream(
			        new ByteArrayInputStream(content));
			try{
				/*out.println("HTTP/1.0 200 OK");
				out.println("Content-Type: text/html");*/
			//Authenticator a=new Authenticator();
				int len = (int) file.length();
				out.println("Content-Length: " + len);
				out.println("Date: " + new Date());
				out.println(""); 

				byte buf[] = new byte[len];
				fin.readFully(buf);
				if(len != 0){
					out.write(buf, 0, len);
				}
				out.flush();
			}
			finally{
				fin.close();
			}
		}
		catch(IOException exception){
			exception.printStackTrace();
		}
	}
	
	
	public void doGet(String line,StringTokenizer st,PrintStream out,String command,Socket client){
		String inputFile="index.html";
		inputFile = st.nextToken();
		
		if(inputFile.startsWith("/")){
			inputFile = "." + inputFile;
			File file = new File(inputFile);
		byte[] file1=DBConnection.getFile(file.getName());
			if(file.exists()){
				out.println("HTTP/1.1 200 OK");
				out.println("Content-Type: " + getFileExtension(inputFile));
				out.println("Server: " + client.getLocalAddress());
			}
			else{
				out.println("HTTP/1.1 404 Not Found");
				out.println("Content-Type: " + getFileExtension(inputFile));
			}
			System.out.println(file.getName() + " requested.");
			
			if(command.equals("GET")){
				sendFile(out,file, file1);
			}
			
		}
		else{
			out.println("HTTP/1.0 400 Bad Request");
		}
		
	}
	
	
	
	public void doPost(String line,StringTokenizer st,PrintStream out,String command,BufferedReader in){
		String inputFile="index.html";
		StringBuilder raw = new StringBuilder();
		StringBuilder body = new StringBuilder();
		int contentLength = 0;
		try {
			while(!(line = in.readLine()).equals("")){
				System.out.println(line);
				raw.append('\n' + line);
				final String contentHeader = "Content-Length: ";
				if(line.startsWith(contentHeader)){
					contentLength = Integer.parseInt(line.substring(contentHeader.length()));
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int c = 0;
		for(int i=0; i<contentLength; i++){
			try {
				c = in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			body.append((char)c);
		}
		System.out.println(body.toString());
		
	//	out.println("Server: " + client.getLocalAddress());
		 String bo=body.toString();
		 ArrayList<String> keyval=new ArrayList<String>(Arrays.asList(bo.split("&")));
		 HashMap<String,String> values=new HashMap<>();
		ArrayList<String> user=new ArrayList<>();
		int i=0;
		 for(String s:keyval){
			 ArrayList<String> k=new ArrayList<String>(Arrays.asList(s.split("=")));
			// for()
			 values.put(k.get(0), k.get(1));
			 if(k.get(0).contentEquals("user")){
			 user.add(0, k.get(1));
			 }
			 else if(k.get(0).contentEquals("pass")){
				 user.add(1,k.get(1));
			 }
			 else if(k.get(0).contentEquals("email")){
				 String e=k.get(1);
				 if(e.contains("%40")){
					 e.replace("%40", "@");
				 }
				 user.add(2,e);
			 }
			 else if(k.get(0).contentEquals("post")){
				 String e=k.get(1).replaceAll("[^a-zA-Z0-9/]" , " ");
				 user.add(1,e);
			 }
			 i++;
		 }
		 
		 
		 
		 //||k.get(i).contentEquals("pass")||k.get(i).contentEquals("email")||k.get(i).contentEquals("post")){
		 
		 String task=values.get("task");
		 switch(task){
		 case "login":LoginController log=new LoginController();
		 				log.handle(user, out);
		 				break;
		 case "register": RegisterController reg=new RegisterController();
		 					reg.handle(user, out);
		 					break;
		 case "addpost":AddPostController ap=new AddPostController();
		 				ap.handle(user, out);
		 
		 }
		 
		 out.print("HTTP/1.1 200 OK\r\n");
			out.print("Content-Type: text/html\r\n");
			 out.print("\r\n");		 
		 String bod="";
		 for(Entry<String,String> e:values.entrySet()){
		
			 bod=bod+"<p>"+e.getKey()+" = "+e.getValue()+"</p>";
		 }
		out.print("<html><h3>Data submitted by user:</h3> " + bod+"</html>");
		
	}
	
	
	
	private String getFileExtension(String inputFile){
		if(inputFile.contains(".html")){
			return "text/html";
		}
		else if(inputFile.contains(".jpg")){
			return "image/*";
		}
		else{
			return "Incompatible Extension: The file type requested is not supported.";
		}
	}
	
	public static void main(String[] args) {
		TinyHttpd2 server = new TinyHttpd2();
		server.init();
	}

}
