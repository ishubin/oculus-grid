/*******************************************************************************
* 2012 Ivan Shubin http://mindengine.net
* 
* This file is part of MindEngine.net Oculus Grid.
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with Oculus Grid.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
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
