import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class FileCacheLFU extends FileCache{
private ReentrantLock lock= new ReentrantLock();


public FileCacheLFU()
{
	super();
}



public void replace(Path p){
	lock.lock();
	try{
	Entry<Path, Date> min = null;
	AccessCounter ac=super.getCounter();
	HashMap<Path,String> m=super.getMap();
	Path minp=ac.getnRemMinKey();
	m.remove(minp);
	m.put(p, ""+p.hashCode());
	super.setCounter(ac);
	super.setMap(m);
	 System.out.println("Replaced with:"+p.toString());
	//System.out.println(min.getKey());
	}
	finally{
		lock.unlock();
	}
}

}
