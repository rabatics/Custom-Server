import java.io.PrintStream;
import java.util.ArrayList;

public interface Controller {

	public void handle(String user,PrintStream out);
	public void handle(ArrayList<String> user,PrintStream out);
	
	
	
}
