import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class LoginController  implements Controller{

	@Override
	public void handle(String user, PrintStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(ArrayList<String> user, PrintStream out) {
		// TODO Auto-generated method stub
		Connection conn=DBConnection.getConnection();
		try {
			Statement stmt =conn.createStatement();
			ResultSet rs1=stmt.executeQuery("select * from Users where user='"+user.get(0)+"' and password='"+user.get(1)+"';");
			if(rs1.next()){
				out.print("HTTP/1.1 200 OK\r\n");
				out.print("Content-Type: text/html\r\n");
				 out.print("\r\n");
				 out.print("<!DOCTYPE html><head><title>Forum</title></head><body>");
				 String body="<p>Forum Posts:</p>";
				ResultSet rs= stmt.executeQuery("select * from Posts;");
				while(rs.next()){
					body=body+"<p>"+rs.getString("user")+" :   "+rs.getString("post")+"</p><br />";
				}
				
				body=body+"<br /><form action='' method=post><input type=hidden name=task value=addpost /><input type=hidden name=user value="+user.get(0)+" /><br /><input type=text name=post required=required /><br /><input type=submit></form></html>";
			out.print(body);
			}
			else{
			byte[] index=DBConnection.getFile("index.html");
			File file=new File("./files/index.html");
			DataInputStream fin = new DataInputStream(
			        new ByteArrayInputStream(index));
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
		}
			
		
		 catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	}


