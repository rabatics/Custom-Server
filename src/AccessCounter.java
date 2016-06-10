import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class AccessCounter {
private HashMap<Path,Integer> map=new HashMap();
private ReentrantLock lock=new ReentrantLock();


public AccessCounter(){}


public void increment(Path a){
	lock.lock();
	try{
		
	
	if(map.containsKey(a)){
		map.put(a, map.get(a)+1);
	}
	else{
		map.put(a, 1);
	}
	}
	finally{
		lock.unlock();
	}
}


public void delete(Path a){
	lock.lock();
	try{
		if(map.containsKey(a)){
	
			map.remove(a);
		}
		else{
		  System.out.println("File does not exist");
		}
	}
	finally{
		
		lock.unlock();
		
	}
	
}



public Path getnRemMinKey(){
	Entry<Path,Integer> min=null;
	for (Entry<Path, Integer> entry : map.entrySet()) {
	    if (min == null || min.getValue().compareTo(entry.getValue())<0) {
	        min = entry;
	    }
	}
		delete(min.getKey());
	return min.getKey();
}


public int getCount(Path a){
	lock.lock();
	try{
		if(map.containsKey(a)){
	
	return map.get(a);
		}
		else{
			return 0;
		}
	}
	finally{
		
		lock.unlock();
		
	}
	
}

public static void main(String[] args) {
	AccessCounter ac=new AccessCounter();
	FileCache fc=new FileCacheLRU();
	ArrayList<Path> paths=new ArrayList();
	paths.add(Paths.get("/file_root/a.html"));
	paths.add(Paths.get("/file_root/b.html"));
	paths.add(Paths.get("/file_root/c.html"));
	paths.add(Paths.get("/file_root/d.html"));
	paths.add(Paths.get("/file_root/e.html"));
	paths.add(Paths.get("/file_root/f.html"));
	
	
	for(int i=0; i<20;i++){
		Runnable req=()->{
			
			Collections.shuffle(paths);	
			Path a=paths.get(1);
			fc.fetch(a);
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ac.increment(a);
			
	    	System.out.println("Count for "+a.toString()+" :"+ac.getCount(a));
	    	
	    	
	   };
	   
	Thread r=new Thread(req);
	r.start();
	
	
}


}
}
