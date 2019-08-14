import cn.hutool.core.collection.CollectionUtil;
import com.fansrn.common.JbpmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.NewDeployment;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * description 流程部署、执行测试类
 *
 * @author fansrn
 * @date 10:43 2019/8/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
@Slf4j
public class JbpmTest {

    /*
     * ver 1
     * private static final String WORKFLOW_NAME = "holiday approval";
     * private static final String FILENAME = "holiday";
     *
     * private static final String DEPLOYMENT_ID = "1";
     * private static final String PROCESS_DEFINITION_ID = "holiday-1";
     * private static final String PROCESS_DEFINITION_KEY = "holiday";
     * private static final String PROCESS_INSTANCE_ID = "holiday.10001";
     *
     * private static final String STAFF = "staff";
     * private static final String STAFF_TASK_ID = "10002";
     * private static final String LEADER = "leader";
     * private static final String LEADER_TASK_ID = "20001";
     */

    private static final String WORKFLOW_NAME = "holiday approval";
    private static final String FILENAME = "holiday";

    private static final String DEPLOYMENT_ID = "30001";

    private static final String PROCESS_DEFINITION_ID = "holiday-2";
    private static final String PROCESS_DEFINITION_KEY = "holiday";

    private static final String PROCESS_INSTANCE_ID = "holiday.40001";

    private static final String STAFF = "staff";
    private static final String STAFF_TASK_ID = "40002";
    private static final String LEADER = "leader";
    private static final String LEADER_TASK_ID = "50001";

    /**
     * description 初始化数据库环境
     *
     * @author fansrn
     * @date 11:48 2019/8/13
     */
    @Test
    public void initDatabase() {
        JbpmUtils.initDatabase();
    }

    /**
     * description 部署流程
     *
     * @author fansrn
     * @date 11:48 2019/8/13
     */
    @Test
    public void deployFromClasspath() {
        NewDeployment deployment = JbpmUtils.deployFromClasspath(WORKFLOW_NAME, FILENAME);
        if (Objects.nonNull(deployment)) {
            log.info("流程定义id:         {}", deployment.getId());
            log.info("流程定义name:       {}", deployment.getName());
            log.info("流程定义timestamp:  {}", new Date(deployment.getTimestamp()));
            log.info("流程定义state:      {}", deployment.getState());
        }
        /*
         * ver 1
         * 2019-08-13 11:58:59.471 [main] INFO  JbpmTest - 流程定义id:         1
         * 2019-08-13 11:58:59.471 [main] INFO  JbpmTest - 流程定义name:       holiday approval
         * 2019-08-13 11:58:59.471 [main] INFO  JbpmTest - 流程定义timestamp:  Tue Aug 13 15:36:11 CST 2019
         * 2019-08-13 11:58:59.471 [main] INFO  JbpmTest - 流程定义state:      active
         */
    }

    @Test
    public void deployFromZip() {
        NewDeployment deployment = JbpmUtils.deployFromZipInputStream(WORKFLOW_NAME, FILENAME);
        if (Objects.nonNull(deployment)) {
            log.info("流程定义id:         {}", deployment.getId());
            log.info("流程定义name:       {}", deployment.getName());
            log.info("流程定义timestamp:  {}", new Date(deployment.getTimestamp()));
            log.info("流程定义state:      {}", deployment.getState());
        }
        /*
         * ver 2
         * 2019-08-13 16:52:47.805 [main] INFO  JbpmTest - 流程定义id:         30001
         * 2019-08-13 16:52:47.806 [main] INFO  JbpmTest - 流程定义name:       holiday approval
         * 2019-08-13 16:52:47.807 [main] INFO  JbpmTest - 流程定义timestamp:  Tue Aug 13 16:52:46 CST 2019
         * 2019-08-13 16:52:47.808 [main] INFO  JbpmTest - 流程定义state:      active
         */
    }

    /**
     * description 删除流程
     *
     * @author fansrn
     * @date 11:48 2019/8/13
     */
    @Test
    public void del() {
        if (JbpmUtils.del(DEPLOYMENT_ID)) {
            log.info("流程删除成功！");
        } else {
            log.info("流程删除失败！");
        }
    }

    /**
     * description 流程定义信息查询
     *
     * @author fansrn
     * @date 14:45 2019/8/13
     */
    @Test
    public void query() {
        ProcessDefinition processDefinition = JbpmUtils.getProcessDefinition(DEPLOYMENT_ID);
        if (Objects.nonNull(processDefinition)) {
            log.info("流程定义id：      {}", processDefinition.getId());
            log.info("流程定义key：     {}", processDefinition.getKey());
            log.info("流程定义name：    {}", processDefinition.getName());
            log.info("流程定义version： {}", processDefinition.getVersion());
            /*
             * ver 1
             * 2019-08-13 14:55:21.283 [main] INFO  JbpmTest - 流程定义id：      holiday-1
             * 2019-08-13 14:55:21.283 [main] INFO  JbpmTest - 流程定义key：     holiday
             * 2019-08-13 14:55:21.283 [main] INFO  JbpmTest - 流程定义name：    holiday
             * 2019-08-13 14:55:21.283 [main] INFO  JbpmTest - 流程定义version： 1
             *
             * ver 2
             * 2019-08-13 16:54:36.708 [main] INFO  JbpmTest - 流程定义id：      holiday-2
             * 2019-08-13 16:54:36.708 [main] INFO  JbpmTest - 流程定义key：     holiday
             * 2019-08-13 16:54:36.708 [main] INFO  JbpmTest - 流程定义name：    holiday
             * 2019-08-13 16:54:36.708 [main] INFO  JbpmTest - 流程定义version： 2
             */
        }
    }

    /**
     * description 启动流程
     *
     * @author fansrn
     * @date 15:13 2019/8/13
     */
    @Test
    public void start() {
//        ProcessInstance processInstance = JbpmUtils.startByKey(PROCESS_DEFINITION_ID);
        ProcessInstance processInstance = JbpmUtils.startById(PROCESS_DEFINITION_ID);
        if (Objects.nonNull(processInstance)) {
            log.info("流程实例id：    {}", processInstance.getId());
            log.info("流程实例key：   {}", processInstance.getKey());
            log.info("流程实例name：  {}", processInstance.getName());
            log.info("流程实例state： {}", processInstance.getState());
            /*
             * ver 1
             * 2019-08-13 15:12:12.955 [main] INFO  JbpmTest - 流程实例id：    holiday.10001
             * 2019-08-13 15:12:12.955 [main] INFO  JbpmTest - 流程实例key：   null
             * 2019-08-13 15:12:12.955 [main] INFO  JbpmTest - 流程实例name：  null
             * 2019-08-13 15:12:12.955 [main] INFO  JbpmTest - 流程实例state： active-root
             *
             * ver 2
             * 2019-08-13 16:56:01.487 [main] INFO  JbpmTest - 流程实例id：    holiday.40001
             * 2019-08-13 16:56:01.487 [main] INFO  JbpmTest - 流程实例key：   null
             * 2019-08-13 16:56:01.488 [main] INFO  JbpmTest - 流程实例name：  null
             * 2019-08-13 16:56:01.488 [main] INFO  JbpmTest - 流程实例state： active-root
             */
        }
    }

    @Test
    public void queryTask() {
        List<Task> tasks = JbpmUtils.getTasks(PROCESS_INSTANCE_ID);
        if (CollectionUtil.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                log.info("**************************************************");
                log.info("流程任务id：           {}", task.getId());
                log.info("流程任务name：         {}", task.getName());
                log.info("流程任务代理人：        {}", task.getAssignee());
                log.info("流程实例id：           {}", task.getExecutionId());
                log.info("流程任务ActivityName： {}", task.getActivityName());
            }
        }

        /*
         * ver 1
         * step one :
         * 2019-08-13 15:56:33.666 [main] INFO  JbpmTest - **************************************************
         * 2019-08-13 15:56:33.666 [main] INFO  JbpmTest - 流程任务id：           10002
         * 2019-08-13 15:56:33.666 [main] INFO  JbpmTest - 流程任务name：         请假申请
         * 2019-08-13 15:56:33.666 [main] INFO  JbpmTest - 流程任务assignee：     staff
         * 2019-08-13 15:56:33.666 [main] INFO  JbpmTest - 流程实例id：           holiday.10001
         * 2019-08-13 15:56:33.666 [main] INFO  JbpmTest - 流程任务ActivityName： 请假申请
         *
         * step two :
         * 2019-08-13 16:15:39.873 [main] INFO  JbpmTest - **************************************************
         * 2019-08-13 16:15:39.873 [main] INFO  JbpmTest - 流程任务id：           20001
         * 2019-08-13 16:15:39.873 [main] INFO  JbpmTest - 流程任务name：         请假审批
         * 2019-08-13 16:15:39.873 [main] INFO  JbpmTest - 流程任务assignee：     leader
         * 2019-08-13 16:15:39.873 [main] INFO  JbpmTest - 流程实例id：           holiday.10001
         * 2019-08-13 16:15:39.873 [main] INFO  JbpmTest - 流程任务ActivityName： 请假审批
         *
         * step three:
         * nothing ...
         *
         * ver 2
         * step one:
         * 2019-08-13 16:57:00.450 [main] INFO  JbpmTest - **************************************************
         * 2019-08-13 16:57:00.450 [main] INFO  JbpmTest - 流程任务id：           40002
         * 2019-08-13 16:57:00.450 [main] INFO  JbpmTest - 流程任务name：         请假申请
         * 2019-08-13 16:57:00.450 [main] INFO  JbpmTest - 流程任务代理人：        staff
         * 2019-08-13 16:57:00.450 [main] INFO  JbpmTest - 流程实例id：           holiday.40001
         * 2019-08-13 16:57:00.450 [main] INFO  JbpmTest - 流程任务ActivityName： 请假申请
         *
         * step two:
         * 2019-08-13 17:00:22.878 [main] INFO  JbpmTest - **************************************************
         * 2019-08-13 17:00:22.878 [main] INFO  JbpmTest - 流程任务id：           50001
         * 2019-08-13 17:00:22.878 [main] INFO  JbpmTest - 流程任务name：         请假审批
         * 2019-08-13 17:00:22.878 [main] INFO  JbpmTest - 流程任务代理人：        leader
         * 2019-08-13 17:00:22.878 [main] INFO  JbpmTest - 流程实例id：           holiday.40001
         * 2019-08-13 17:00:22.878 [main] INFO  JbpmTest - 流程任务ActivityName： 请假审批
         *
         * step three:
         * nothing ...
         */
    }

    @Test
    public void queryState() {
        String state = JbpmUtils.getState(PROCESS_INSTANCE_ID);
        if (StringUtils.isNotBlank(state)) {
            log.info("流程实例state： {}", state);
        } else {
            log.info("流程 {} 已结束...", PROCESS_INSTANCE_ID);
        }
        /*
         * ver 1
         * step one:
         * 2019-08-13 16:02:09.652 [main] INFO  JbpmTest - 流程实例state： active-root
         *
         * step two:
         * 2019-08-13 16:23:01.345 [main] INFO  JbpmTest - 流程实例state： active-root
         *
         * step three:
         * 2019-08-13 16:31:06.315 [main] INFO  JbpmTest - 流程 holiday.10001 已结束...
         *
         * ver 2
         * step one:
         * 2019-08-13 16:58:10.703 [main] INFO  JbpmTest - 流程实例state： active-root
         *
         * step two:
         * 2019-08-13 17:01:03.113 [main] INFO  JbpmTest - 流程实例state： active-root
         *
         * step three:
         * 2019-08-13 17:03:59.001 [main] INFO  JbpmTest - 流程 holiday.40001 已结束...
         */
    }

    @Test
    public void outcomesStaff() {
        Set<String> outcomes = JbpmUtils.getOutcomes(STAFF_TASK_ID);
        if (CollectionUtil.isNotEmpty(outcomes)) {
            for (String outcome : outcomes) {
                log.info("流程流向： {}", outcome);
            }
        }
        /*
         * ver 2
         * 2019-08-13 16:59:05.739 [main] INFO  JbpmTest - 流程流向： to 审批
         */
    }

    @Test
    public void outcomesLeader() {
        Set<String> outcomes = JbpmUtils.getOutcomes(LEADER_TASK_ID);
        if (CollectionUtil.isNotEmpty(outcomes)) {
            for (String outcome : outcomes) {
                log.info("流程流向： {}", outcome);
            }
        }
        /*
         * ver 2
         * 2019-08-13 17:02:14.671 [main] INFO  JbpmTest - 流程流向： to end
         */
    }

    /*
     * 调用completeTask时，可能会报出以下异常：
     *      could not delete: [org.jbpm.pvm.internal.model.ExecutionImpl#10001]
     * 解决：
     *      修改：<prop key="hibernate.dialect">org.hibernate.dialect.MySQL5Dialect</prop>
     *      改为：<prop key="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</prop>
     */

    @Test
    public void completeStaffTask() {
        log.info(JbpmUtils.completeTask(STAFF_TASK_ID) ? "任务完成" : "任务失败");
    }

    @Test
    public void completeLeaderTask() {
        log.info(JbpmUtils.completeTask(LEADER_TASK_ID) ? "任务完成" : "任务失败");
    }


}
