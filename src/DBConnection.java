import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Blob;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {

	public static Connection getConnection(){

		// first we need to load the driver
		String jdbcDriver = "com.mysql.jdbc.Driver";
		try {
			Class.forName(jdbcDriver); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		String user = "root";     // mysql username
		String password = "mysql"; // mysql password
		String connStr = "jdbc:mysql://localhost/webserver";  // mysql database name

		// Get username and password
		
		//the following is used to mask the password
		
		

		System.out.println("Connecting to the database...");
	
		Connection conn;
		// Connect to the database
		try{
			conn = DriverManager.getConnection(connStr, user, password);
			System.out.println("Connection Successful");
		}
		catch(SQLException e){
			System.out.println("Connection ERROR");
			e.printStackTrace();	
			return null;
		}

		return conn;
}
	
	
	public static void init(){
		Connection conn=DBConnection.getConnection();
		try {
			Statement stmt=conn.createStatement();
			stmt.execute("Drop table if exists Files;");
			System.out.println("No tables");
			stmt.execute("create table if not exists Files(fileName text,fileContent BLOB);");
			stmt.execute("create table if not exists Users(user text,password text,email text);");
			stmt.execute("create table if not exists Posts(user text,post text);");
			stmt.execute("insert ignore into Users values('user','1111','user@email.com');");
			System.out.println("Tables created");
			
			
			
			PreparedStatement preStmt = null;
			
			File[] listOfFiles = new File("./files").listFiles();
			for (File file : listOfFiles) {
				if(file.isFile()){
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					DataInputStream fin = new DataInputStream(fileInputStream);
					byte buf[] = new byte[(int) file.length()];
					fin.readFully(buf);					
					preStmt = conn.prepareStatement("insert ignore into Files (fileName,fileContent) VALUES ( ?,? )");
					Blob blob = conn.createBlob();					
					blob.setBytes(1, buf);
					preStmt.setString(1, file.getName());					
					preStmt.setBlob(2, blob);					
					preStmt.executeUpdate();
					System.out.println(file.getName()+" inserted");
					preStmt.close();
					fileInputStream.close();
					} catch (Exception e ) {
						System.err.println("SQLException : " + e.getMessage());
					}

			
		
	}
			}
		}
		 catch (Exception e ) {
				System.err.println("SQLException : " + e.getMessage());
			}

	}
	
	
	
	public static byte[] getFile(String filename){
		Blob blob =null;
		byte[] content = null;

		try {
			Connection conn = DBConnection.getConnection();
			System.out.println("connected.");
			PreparedStatement preStmt = conn.prepareStatement("select fileContent from Files where fileName = ?");
			preStmt.setString(1, filename);
			ResultSet rs = preStmt.executeQuery();
			if(rs.next()){
				blob = rs.getBlob(1);
				int bloblength = (int)blob.length();
				content = blob.getBytes(1, bloblength);
				blob.free();
			}
			preStmt.close();
			rs.close();
			
		} catch (SQLException e) {
			System.out.println("Problem with JDBC Connection\n");
			e.printStackTrace();
			System.exit(4);
		}
		
		
		
		return content;
	}
	
	
	
	
}
	
	


	
	

	

