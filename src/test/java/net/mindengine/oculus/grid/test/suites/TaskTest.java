package net.mindengine.oculus.grid.test.suites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.oculus.experior.suite.Suite;
import net.mindengine.oculus.experior.suite.XmlSuiteParser;
import net.mindengine.oculus.grid.client.GridClient;
import net.mindengine.oculus.grid.domain.task.DefaultTask;
import net.mindengine.oculus.grid.domain.task.SuiteTask;
import net.mindengine.oculus.grid.domain.task.TaskInformation;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.server.Server;
import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;
import net.mindengine.oculus.grid.service.exceptions.IncorrectTaskException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TaskTest {
    static final Server server = new Server();
    
    static Thread serverThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                server.startServer(8100, "server");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });
    
    @BeforeClass
    public static void startServer() throws InterruptedException {
        serverThread.start();
        Thread.sleep(4000);
    }
    
    @SuppressWarnings("deprecation")
    @AfterClass
    public static void stopServer() throws Exception {
        server.stopServer();
        serverThread.stop();
        Thread.sleep(4000);
    }
    
    @Test
    public void serverSendsRecievesTask() throws Exception{
        GridClient client = new GridClient();
        client.setServerHost("localhost");
        client.setServerPort(8100);
        client.setServerName("server");
        
        ClientServerRemoteInterface remote = client.getServer();
        
        assertNotNull(remote);
        
        DefaultTask task = new DefaultTask();
        task.setName("sample task1");
        
        task.setAgentNames(new String[]{"agent1"});
        task.setCreatedDate(new Date(1234567));
        
        List<SuiteTask> suiteTasks = new LinkedList<SuiteTask>();
        SuiteTask suiteTask = new SuiteTask();
        suiteTasks.add(suiteTask);
        
        suiteTask.setAgentNames(new String[]{"agent2", "agent3"});
        suiteTask.setName("sample suite task");
        
        Suite suite = XmlSuiteParser.parse(new File(getClass().getResource("/sample-suite.xml").toURI()));
        suiteTask.setSuite(suite);
        task.setSuiteTasks(suiteTasks);
        
        Long taskId = remote.runTask(task);
        assertNotNull(taskId);
        assertTrue(taskId>0L);
        Thread.sleep(1000);
        
        TaskInformation[] tasks = remote.getTasks(null);
        assertNotNull(tasks);
        assertEquals(1, tasks.length);
        assertEquals("sample task1", tasks[0].getTaskName());
        assertEquals(1, (int)tasks[0].getChildTasksAmount());
        assertNotNull(tasks[0].getTaskStatus());
        assertNull(tasks[0].getTaskStatus().getAssignedAgent());
        assertEquals(TaskStatus.WAITING, tasks[0].getTaskStatus().getStatus());
        assertNotNull(tasks[0].getTaskId());
        
        TaskInformation[]childTasks = remote.getTasks(tasks[0].getTaskId());
        assertNotNull(childTasks);
        assertEquals(1, childTasks.length);
        assertEquals("sample suite task", childTasks[0].getTaskName());
        assertEquals(0, (int)childTasks[0].getChildTasksAmount());
        assertNotNull(childTasks[0].getTaskStatus());
        assertNull(childTasks[0].getTaskStatus().getAssignedAgent());
        assertEquals(TaskStatus.WAITING, childTasks[0].getTaskStatus().getStatus());
        assertNotNull(childTasks[0].getTaskId());
        assertEquals(tasks[0].getTaskId(), childTasks[0].getParentId());
        
        //TODO verify suite data
    }
    
    @Test
    public void retrievesOnlyTasksWhichBelongToUser() {
        //TODO
    }
    
    @Test(expected=IncorrectTaskException.class)
    public void emptyTaskGivesError() throws Exception {
        GridClient client = new GridClient();
        client.setServerHost("localhost");
        client.setServerPort(8100);
        client.setServerName("server");
        
        ClientServerRemoteInterface remote = client.getServer();
        assertNotNull(remote);
        
        DefaultTask task = new DefaultTask();
        task.setAgentNames(new String[]{"agent1"});
        task.setCreatedDate(new Date(1234567));
        List<SuiteTask> suiteTasks = new LinkedList<SuiteTask>();
        task.setSuiteTasks(suiteTasks);
        
        
        
        remote.runTask(task);
    }
    
}
