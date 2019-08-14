package com.fansrn.common;

import cn.hutool.core.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.jbpm.api.*;
import org.jbpm.api.task.Task;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;


/**
 * description jBPM工具类
 *
 * @author fansrn
 * @date 17:14 2019/8/12
 */
@Slf4j
public class JbpmUtils {

    private static final String CLASSPATH = "jpdl/";
    private static final String FIX_ZIP = ".zip";
    private static final String FIX_XML = ".jpdl.xml";
    private static final String FIX_PNG = ".png";

    /**
     * description 初始化数据库环境
     *
     * @author faonsrn
     * @date 11:45 2019/8/13
     */
    public static void initDatabase() {
        // 加载jbpm配置 文件
        Configuration configureation = new Configuration();
        //  通过Configuration对象，创建流程引擎对象（自动加载jbpm.cfg.xml）
        configureation.buildProcessEngine();
    }

    /*
       private static ProcessEngine getProcessEngine(String configurationFileName) {
           // 从指定配置文件获取 ProcessEngine
           return new Configuration()
                   .setResource(configurationFileName)
                   .buildProcessEngine();
       }
     */

    /**
     * description 获取默认 ProcessEngine
     *
     * @return ProcessEngine
     * @author fansrn
     * @date 17:56 2019/8/12
     */
    private static ProcessEngine getDefaultProcessEngine() {
        return Configuration.getProcessEngine();
    }

    /**
     * 获取六大 Service
     */
    public static ExecutionService getExecutionService() {
        return getDefaultProcessEngine().getExecutionService();
    }

    public static RepositoryService getRepositoryService() {
        return getDefaultProcessEngine().getRepositoryService();
    }

    public static TaskService getTaskService() {
        return getDefaultProcessEngine().getTaskService();
    }

    /*
        public static HistoryService getHistoryService() {
            return getDefaultProcessEngine().getHistoryService();
        }

        public static IdentityService getIdentityService() {
            return getDefaultProcessEngine().getIdentityService();
        }

        public static ManagementService getManagementService() {
            return getDefaultProcessEngine().getManagementService();
        }
     */

    /*
     * 流程部署相关方法，可自行挑选方法进行部署：deployment.addResourceFromClasspath
     *                                      deployment.addResourceFromFile
     *                                      deployment.addResourceFromInputStream
     *                                      deployment.addResourceFromString
     *                                      deployment.addResourceFromUrl
     *                                      deployment.addResourcesFromZipInputStream
     */

    /**
     * description 流程部署 - 通过xml文件部署{@link NewDeployment#addResourceFromClasspath(String)}
     *
     * @param workflowName name of workflow
     * @param fileName     name of xml and png
     * @return Deployment
     * @author fansrn
     * @date 10:39 2019/8/13
     */
    public static NewDeployment deployFromClasspath(String workflowName, String fileName) {
        try {
            NewDeployment deployment = getRepositoryService().createDeployment();

            deployment.addResourceFromClasspath(CLASSPATH + fileName + FIX_XML)
                    .addResourceFromClasspath(CLASSPATH + fileName + FIX_PNG)
                    .setName(workflowName)
                    .setTimestamp(System.currentTimeMillis())
                    .deploy();
            return deployment;
        } catch (Exception e) {
            log.error("JbpmUtils: deploy from classpath error !", e);
        }
        return null;
    }

    /**
     * description 流程部署 - 通过 ZipInputStream 部署{@link NewDeployment#addResourcesFromZipInputStream(ZipInputStream)}
     *
     * @param workflowName namw of workflow
     * @param fileName     name of file
     * @return Deployment
     * @author faonsrn
     * @date 14:08 2019/8/13
     */
    public static NewDeployment deployFromZipInputStream(String workflowName, String fileName) {
        try {
            InputStream ins = ClassLoaderUtil.getClassLoader().getResourceAsStream(CLASSPATH + fileName + FIX_ZIP);
            if (Objects.nonNull(ins)) {
                ZipInputStream zipInputStream = new ZipInputStream(ins);
                NewDeployment deployment = getRepositoryService().createDeployment();
                deployment.addResourcesFromZipInputStream(zipInputStream)
                        .setName(workflowName)
                        .setTimestamp(System.currentTimeMillis())
                        .deploy();
                return deployment;
            }
            return null;
        } catch (Exception e) {
            log.error("JbpmUtils: deploy from zip error !", e);
        }
        return null;
    }

    /**
     * description 删除流程，此处直接使用的是级联删除，无需判断流程是否启动
     *
     * @param deplotmentId 流程部署id
     * @return result
     * @author faonsrn
     * @date 11:49 2019/8/13
     */
    public static boolean del(String deplotmentId) {
        try {

            /* 非级联删除，只能删除未发布的流程： getRepositoryService().deleteDeployment(deplotmentId); */

            getRepositoryService().deleteDeploymentCascade(deplotmentId);
            return true;
        } catch (Exception e) {
            log.error("JbpmUtils: deleted workflow error ! ", e);
        }
        return false;
    }

    /**
     * description 流程定义查询
     *
     * @param deploymentId 流程部署id
     * @return ProcessDefinition
     * @author faonsrn
     * @date 15:00 2019/8/13
     */
    public static ProcessDefinition getProcessDefinition(String deploymentId) {
        return getRepositoryService()
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId).uniqueResult();
    }

    public static ProcessInstance startByKey(String processDefinitionKey) {
        return startByKey(processDefinitionKey, new HashMap<>());
    }

    /**
     * description 启动
     *
     * @param processInstanceKey 流程定义 key
     * @param variables          流程变量
     * @return ProcessInstance
     * @author faonsrn
     * @date 15:09 2019/8/13
     */
    public static ProcessInstance startByKey(String processInstanceKey, Map<String, Object> variables) {
        return getExecutionService()
                .startProcessInstanceByKey(processInstanceKey, variables);
    }

    public static ProcessInstance startById(String processInstanceId) {
        return startById(processInstanceId, new HashMap<>());
    }

    /**
     * description 启动
     *
     * @param processInstanceId 流程定义 id
     * @param variables         流程变量
     * @return ProcessInstance
     * @author faonsrn
     * @date 15:09 2019/8/13
     */
    public static ProcessInstance startById(String processInstanceId, Map<String, Object> variables) {
        return getExecutionService()
                .startProcessInstanceById(processInstanceId, variables);
    }

    /**
     * description 查询任务
     *
     * @param processInstanceId 流程实例id
     * @return tasks
     * @author faonsrn
     * @date 15:41 2019/8/13
     */
    public static List<Task> getTasks(String processInstanceId) {
        return getTaskService()
                .createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
    }

    /**
     * description 查询任务
     *
     * @param assignee 代理人
     * @return tasks
     * @author faonsrn
     * @date 16:42 2019/8/13
     */
    public static List<Task> getPersonalTasks(String assignee) {
        return getTaskService().findPersonalTasks(assignee);
    }

    /**
     * description 获取流程实例状态
     *
     * @param processInstanceId 流程实例id
     * @return state
     * @author faonsrn
     * @date 15:59 2019/8/13
     */
    public static String getState(String processInstanceId) {
        ProcessInstance processInstance = getExecutionService().createProcessInstanceQuery().processInstanceId(processInstanceId).uniqueResult();
        return Objects.nonNull(processInstance) ? processInstance.getState() : null;
    }

    /**
     * description 完成任务
     *
     * @param taskId 任务id
     * @return result
     * @author faonsrn
     * @date 16:48 2019/8/13
     */
    public static boolean completeTask(String taskId) {
        try {
            getTaskService().completeTask(taskId);
            return true;
        } catch (Exception e) {
            log.error("JbpmUtils: complete task error !", e);
        }
        return false;
    }

    /**
     * description 任务完成后的流向
     *
     * @param taskId 任务id
     * @return outcomes
     * @author faonsrn
     * @date 16:48 2019/8/13
     */
    public static Set<String> getOutcomes(String taskId) {
        return getTaskService().getOutcomes(taskId);
    }

}
