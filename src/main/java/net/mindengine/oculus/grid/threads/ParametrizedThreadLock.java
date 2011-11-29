package net.mindengine.oculus.grid.threads;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used to lock threads based on parameters.
 * @author Ivan Shubin
 *
 */
public class ParametrizedThreadLock {

    private Map<String, ReentrantLock> locksMap = new ConcurrentHashMap<String, ReentrantLock>();
    
    protected String getKey(Object...args) {
        StringBuffer buffer = new StringBuffer();
        
        if(args == null || args.length==0) {
            throw new IllegalArgumentException("Array of arguments should not be null or empty");
        }
        for(Object arg : args) {
            if(arg==null) {
                throw new IllegalArgumentException("Array of arguments should not contain a null value");
            }
            buffer.append(arg.hashCode());
            buffer.append("-");
        }
        return buffer.toString();
    }
    
    protected ReentrantLock createReentrantLock(Object...args) {
        String key = getKey(args);
        
        synchronized(key.intern()){
            if(locksMap.containsKey(key)) {
                return locksMap.get(key);
            }
            else  {
                ReentrantLock lock = new ReentrantLock();
                locksMap.put(key, lock);
                return lock;
            }
        }
    }
    
    public void lock(Object...args){
        try {
            ReentrantLock lock = createReentrantLock(args);
            lock.lock();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void unlock(Object...args) {
        try {
            String key = getKey(args);
            ReentrantLock lock = locksMap.get(key);
            if(lock!=null) {
                lock.unlock();
            }
            if(!lock.hasQueuedThreads()) {
                locksMap.remove(key);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
