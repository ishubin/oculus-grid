package net.mindengine.oculus.grid.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

public class DefaultGridStorage implements Storage {

    /**
     * Path to storage on file system 
     */
    private String storagePath;
    
    private static final String _GRIDPROJECT = ".gridproject".intern();
    private static final String PROJECT_NAME = "project.name".intern();
    private static final String PROJECT_VERSION = "project.version".intern();
    private static final String CONTROL_KEY = "control.key".intern();
    private static final String UPLOAD_DATE = "upload.date".intern();
    private static final String UPLOAD_USER = "upload.user".intern();
    
    
    private static void appendProperty(StringBuffer buffer, String name, String value) {
        buffer.append(name);
        buffer.append("=");
        buffer.append(value);
        buffer.append("\r\n");
    }
    
    public static void writeGridProject(String pathToProject, String projectName, String projectVersion, String controlKey, Date date, String userName) throws IOException {
        File file = new File(pathToProject+File.separator+_GRIDPROJECT);
        file.createNewFile();
        
        StringBuffer buff = new StringBuffer();
        appendProperty(buff, PROJECT_NAME, projectName);
        appendProperty(buff, PROJECT_VERSION, projectVersion);
        appendProperty(buff, CONTROL_KEY, controlKey);
        appendProperty(buff, UPLOAD_DATE, date.toString());
        appendProperty(buff, UPLOAD_USER, userName);
        
        FileUtils.writeStringToFile(file, buff.toString(), "UTF-8");
    }
    
    @Override
    public String putProjectZip(String name, String version, byte[] content, String user) throws Exception{
        //TODO Implement thread lock based on name and version
        StringBuffer buff = new StringBuffer(name);
        buff.append("-");
        buff.append(version);
        
        String synchronizedKey = buff.toString().intern();
        
        synchronized(synchronizedKey) {
        
            String pathToProject = getPathToProject(name, version);
            File file = new File(pathToProject);
            if(!file.exists()) {
                file.mkdirs();
            }
            
            if(!file.isDirectory()){
                throw new RuntimeException("Cannot upload project, given path is not a directory: "+file.getAbsolutePath());
            }
            
            StringBuffer path = new StringBuffer(pathToProject);
            path.append(File.separator);
            path.append(name);
            path.append("-");
            path.append(version);
            path.append(".zip");
            FileUtils.writeByteArrayToFile(new File(pathToProject.toString()), content);
            
            String controlKey = generateControlKey();
            writeGridProject(pathToProject, name, version, controlKey, new Date(), user);
            return controlKey;
        }
    }
    
    public static String generateControlKey() {
        return UUID.randomUUID().toString();
    }
    
    public static void extractZip(byte[]content, String tempDir, String destinationFolder) throws Exception {
        
        //Generating temporary file
        StringBuffer tempFilePath = new StringBuffer(tempDir);
        tempFilePath.append(File.separator);
        tempFilePath.append(Long.toString(new Date().getTime()));
        tempFilePath.append(Integer.toString(new Random().nextInt(10000000)+10000));
        tempFilePath.append(".zip");
        
        File tempFile = new File(tempFilePath.toString());
        try{
            FileUtils.writeByteArrayToFile(new File(tempFilePath.toString()), content);
            
            ZipFile zipFile = new ZipFile(tempFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
    
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File file = new File(destinationFolder + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    file.mkdir();
                }
                else {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    int size = 2048;
                    byte[] buffer = new byte[size];
    
                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
                    while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
                        bos.write(buffer, 0, size);
                    }
                    bos.flush();
                    bos.close();
                    fos.close();
                    bis.close();
                }
    
                //TODO remove println
                System.out.println(entry.getName());
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            tempFile.delete();
        }
    }


    @Override
    public String readProjectControlKey(String name, String version) {
        File file = new File(getPathToGridProjectfile(name, version));
        if(file.exists()) {
            Properties properties = new Properties();
            try {
                properties.load(new FileReader(file));
                String controlKey = (String) properties.get(CONTROL_KEY);
                if(controlKey!=null) {
                    return controlKey.trim();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    
    public String getPathToGridProjectfile(String name, String version) {
        StringBuffer path = new StringBuffer(getPathToProject(name, version));
        path.append(File.separator);
        path.append(_GRIDPROJECT);
        return path.toString();
    }
    
    public String getPathToProject(String projectName, String version) {
        StringBuffer buff = new StringBuffer(storagePath);
        if(!storagePath.endsWith(File.separator)) {
            buff.append(File.separator);
        }
        buff.append(projectName);
        buff.append(File.separator);
        buff.append(version);
        return buff.toString();
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}
