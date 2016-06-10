

public class RunnableTest implements Runnable{
	private String s;
	
	public RunnableTest(String s){
		this.s = s;
	}
	
	public void run(){
		for(int i=0; i<10; i++){
			System.out.println(s);
		}
	}
}
