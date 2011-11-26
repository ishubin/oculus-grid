/*******************************************************************************
 * 2011 Ivan Shubin http://mindengine.net
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
 * along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskInformation;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.domain.task.TestStatus;
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
        assertEquals(Task.TYPE_MULTITASK, tasks[0].getType());
        
        //Fetching child tasks
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
        assertEquals(Task.TYPE_SUITETASK, childTasks[0].getType());
        

        assertNotNull(childTasks[0].getTaskStatus().getSuiteInformation());
        assertNotNull(childTasks[0].getTaskStatus().getSuiteInformation().getTests());
        assertEquals(3, (int)childTasks[0].getTaskStatus().getSuiteInformation().getTests().size());
        
        List<TestStatus> tests = childTasks[0].getTaskStatus().getSuiteInformation().getTests();
        assertEquals("net.mindengine.oculus.experior.samples.Sample2_B", tests.get(0).getMapping());
        assertEquals(1, (long)tests.get(0).getCustomId());
        
        assertEquals("net.mindengine.oculus.experior.samples.Sample2_B", tests.get(1).getMapping());
        assertEquals(345, (long)tests.get(1).getCustomId());
        
        assertEquals("net.mindengine.oculus.experior.samples.Sample2_A", tests.get(2).getMapping());
        assertEquals(123, (long)tests.get(2).getCustomId());
        
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
