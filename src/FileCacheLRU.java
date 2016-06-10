import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class FileCacheLRU extends FileCache{
private ReentrantLock lock= new ReentrantLock();


public FileCacheLRU()
{
	super();
}




public void replace(Path p){
	lock.lock();
	try{
	Entry<Path, Date> min = null;
	HashMap<Path,Date> m=super.getLrumap();
	for (Entry<Path, Date> entry : m.entrySet()) {
	    if (min == null || min.getValue().compareTo(entry.getValue())<0) {
	        min = entry;
	    }
	}
	m.remove(min.getKey());
	m.put(p, new Date());
	HashMap<Path,String> map=super.getMap();
	map.remove(min.getKey());
	map.put(p, ""+p.hashCode());
	super.setMap(map);
 System.out.println("Replaced with:"+p.toString());
	//System.out.println(min.getKey());
	}
	finally{
		lock.unlock();
	}
}

}
