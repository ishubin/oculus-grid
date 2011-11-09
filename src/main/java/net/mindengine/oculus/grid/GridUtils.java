package net.mindengine.oculus.grid;

import java.io.FileNotFoundException;
import java.net.URL;

public class GridUtils {
    
    public static String getMandatoryResourceFile(Class<?> clazz, String resource) throws FileNotFoundException {
        return getMandatoryResource(clazz, resource).getFile();
    }

    public static URL getMandatoryResource(Class<?> clazz, String resource) throws FileNotFoundException {
        URL url = clazz.getResource(resource);
        if(url==null) {
            throw new FileNotFoundException("Cannot find file "+resource);
        }
        return url;
    }

}
