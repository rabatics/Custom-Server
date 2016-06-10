import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AddPostController implements Controller {

	@Override
	public void handle(String user, PrintStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(ArrayList<String> user, PrintStream out) {
		Connection conn=DBConnection.getConnection();
		try {
			Statement stmt=conn.createStatement();
			stmt.executeUpdate("insert into Posts values('"+user.get(0)+"','"+user.get(1)+"')");
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
