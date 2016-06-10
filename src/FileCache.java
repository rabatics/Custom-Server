import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class FileCache {
	private final int MAX_CACHE=2;
private HashMap<Path,String> map=new HashMap();
private HashMap<Path,Date> lrumap=new HashMap();
private ReentrantLock lock=new ReentrantLock();
private AccessCounter counter=new AccessCounter();


	
public String fetch(Path p){
	lock.lock();
	
	try{
		
	if(map.containsKey(p)){
		System.out.println("Fetching "+p.toString());
		lrumap.replace(p, lrumap.get(p), new Date());
		counter.increment(p);
		
		return map.get(p);
	}
	else{
		
		 return cacheFile(p);
	}
	}
	finally{
		lock.unlock();
	}
}

public String cacheFile(Path p){
	
	lock.lock();
	try{
		
	
	if(map.size()==MAX_CACHE){
		
		replace(p);
		return map.get(p);
	}
	else{
		
		map.put(p,""+p.hashCode());
		lrumap.put(p, new Date());
		counter.increment(p);
		System.out.println("Cached "+p.toString());
		return map.get(p);
	}
	}
	finally{
		lock.unlock();
	}
	
}

public void replace(Path p){
	
}


public HashMap<Path, Date> getLrumap() {
	return lrumap;
}

public void setLrumap(HashMap<Path, Date> lrumap) {
	this.lrumap = lrumap;
}

public AccessCounter getCounter() {
	return counter;
}

public void setCounter(AccessCounter counter) {
	this.counter = counter;
}






public HashMap getMap(){
	return map;
}

public void setMap(HashMap m){
	 map=m;
}


public int getMaxSize(){
	return MAX_CACHE;
}
	
}
