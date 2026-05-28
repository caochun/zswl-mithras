package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.dao.ModelExtMapper;
import cn.zswltech.flow.core.domain.entity.ModelExt;
import cn.zswltech.flow.core.enums.UserDefineTypeEnum;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导出某个人在所有审批流中的可能审批节点
 *
 * @author luyi
 */
@Slf4j
@ActiveProfiles("prod")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 某人审批人 {

    private String username = "刘旭浩";

    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;
    @Resource
    private ModelExtMapper modelExtMapper;
    @Resource
    private RepositoryService repositoryService;


    @Test
    public void export() {
        Example example = new Example(UserDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userName", username);
        List<UserDO> userList = userDOMapper.selectByExample(example);
        if (CollUtil.isEmpty(userList)) {
            System.err.println("用户不存在");
            return;
        }
        UserDO user = userList.get(0);
        //
        Map<String, String> maxModel = new HashMap<>();
        List<Model> modelList = repositoryService.createModelQuery().list();
        for (Model model : modelList) {
            maxModel.putIfAbsent(model.getKey(), model.getId());
            String modelId = maxModel.get(model.getKey());
            String id = model.getId();
            if (id.compareTo(modelId) > 0) {
                maxModel.put(model.getKey(), id);
            }
        }
        //
        List<TaskInfo> infoList = new ArrayList<>();
        List<ProcessDefinition> definitionList = repositoryService.createProcessDefinitionQuery().latestVersion().list();
        for (ProcessDefinition processDefinition : definitionList) {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).list().get(0);
            String key = deployment.getKey();
            String modelId = maxModel.get(key);
            ModelExt query = new ModelExt();
            query.setModelId(modelId);
            ModelExt modelExt = modelExtMapper.selectOne(query);
            String extJson = modelExt.getExtJson();
            JSONObject jo = JSONUtil.parseObj(extJson);
            jo = jo.getJSONObject("userTaskExtMap");
            Set<String> activityIdList = jo.keySet();
            Map<String, FlowElement> flowElementMap = bpmnModel.getMainProcess().getFlowElementMap();
            String processName = bpmnModel.getMainProcess().getName();
            for (String k : activityIdList) {
                JSONObject jsonObject = jo.getJSONObject(k);
                String activityId = jsonObject.getStr("activityId");
                FlowElement flowElement = flowElementMap.get(activityId);
                if (flowElement == null) {
                    System.out.println(1);
                    continue;
                }
                String taskName = flowElement.getName();
                String approvalType = null;
                String jobCode = null;
                UserDefineTypeEnum approverType = UserDefineTypeEnum.getByType(jsonObject.getInt("approverType"));
                if (approverType == UserDefineTypeEnum.PROCESS_START_TARGET_BY_VAR) {
                    approvalType = "变量";
                }
                if (approverType == UserDefineTypeEnum.PROCESS_START_JOB) {
                    approvalType = "岗位";
                    jobCode = jsonObject.getStr("jobCode");
                }
                if (approverType != null) {
                    infoList.add(new TaskInfo().setType(approvalType).setTaskName(taskName).setProcessName(processName).setJobCode(jobCode));
                }
            }
        }
        for (TaskInfo taskInfo : infoList) {
            if (ObjectUtil.equal(taskInfo.getType(), "变量")) {
                String taskName = taskInfo.getTaskName();
                if (taskName.contains("部门负责人") || taskName.contains("业务部负责人")) {
                    taskInfo.setJobCode(JobEnum.businesshead.name());
                } else if (taskName.contains("分管领导")) {
                    taskInfo.setJobCode(JobEnum.leaderincharge.name());
                } else if (taskName.contains("风控经理")) {
                    taskInfo.setJobCode(JobEnum.riskmanager.name());
                } else if (taskName.contains("法务经理")) {
                    taskInfo.setJobCode(JobEnum.legalmanager.name());
                } else if (taskName.contains("项目主办") || taskName.contains("项目协办") || taskName.contains("项目经理") || taskName.contains("跨部门推荐人")) {
                    taskInfo.setJobCode(JobEnum.projmanager.name());
                } else {
                    System.out.println(1);
                }
            }
        }
        List<UserOrgJobDO> jobList = userOrgJobDOMapper.selectJobCodeByUserId(ListUtil.of(user.getId()));
        Set<String> set = jobList.stream().map(UserOrgJobDO::getJobCode).collect(Collectors.toSet());
        List<TaskInfo> inList = new ArrayList<>();
        for (TaskInfo taskInfo : infoList) {
            if (set.contains(taskInfo.getJobCode())) {
                inList.add(new TaskInfo().setJobCode(taskInfo.jobCode).setProcessName(taskInfo.processName).setTaskName(taskInfo.taskName));
            }
        }
        System.out.println(JSONUtil.toJsonStr(inList));
    }


    @Data
    @Accessors(chain = true)
    public static class TaskInfo {
        private String jobCode;
        private String processName;

        private String taskName;

        private String type;

        private String deptName;
    }


}
