package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.map.MapUtil;
import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.req.SetTaskApproverReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.flow.core.service.impl.FlowModelService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.form.SelectUserREQ;
import cn.zswltech.mithras.dto.flow.form.SelectUserRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.domain.enums.ProcessVarEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表单处理器
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:44 AM
 */
@Component
public class ChooseJudgesHandler implements DynamicFormHandler {

    @Resource
    private FlowUserApiService userApiService;
    @Resource
    private Id2NameService id2NameService;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private FlowModelService flowModelService;
    @Resource
    private RuntimeService runtimeService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("评委成员选择不能为空");
        }
        List<SelectUserREQ> selectUserList = JSONArray.parseArray(JSON.toJSONString(formMap.get(getType().name()))).toJavaList(SelectUserREQ.class);
        if (CollectionUtils.isEmpty(selectUserList)) {
            throw new MithrasException("评委成员选择不能为空");
        }
        long userCount = selectUserList.stream().map(SelectUserREQ::getUserId).filter(Objects::nonNull).count();
        if (userCount == 0L) {
            throw new MithrasException("评委成员选择不能为空");
        }
        if (userCount != 5L) {
            throw new MithrasException("必须选择五位评审委员进行上会");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        List<SelectUserREQ> selectUserList = JSONArray.parseArray(JSON.toJSONString(formMap.get(getType().name()))).toJavaList(SelectUserREQ.class);
        List<String> judgeIdList = selectUserList.stream().map(SelectUserREQ::getUserId).filter(Objects::nonNull).distinct().map(String::valueOf).collect(Collectors.toList());
        SetTaskApproverReq flowReq = new SetTaskApproverReq();
        flowReq.setProcessInstanceId(taskResp.getProcessInstanceId());
        flowReq.setApproverIdList(judgeIdList);
        // 需要写死评委会投票节点id 评审委员非公开投票
        flowReq.setActivityId("userTask_juryVote");
        userApiService.setTaskApprover(flowReq);
        // 评审会秘书发起会议纪要审批
        flowReq.setActivityId("userTask_juryMeetingReview");

        // 判断存量流程节点
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(taskResp.getProcessInstanceId()).singleResult();
        FlowNode flowNode = flowModelService.findSingleFlowNode(processInstance.getProcessDefinitionId(), "userTask_juryMeetingReview_member");
        // 授信授信评审创建/授信评审变更流程、项目评审创建/项目评审变更流程
        List<String> needJuryDirectorFlow = Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name(), ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name());
        if(needJuryDirectorFlow.contains(taskResp.getModelKey()) && Objects.nonNull(flowNode)) {
            // 评审会主任(按需求应该只有一人，单开节点审批)
            boolean hasJuryDirector = false;
            List<String> directorIdList = sysUserService.queryJobUserIds(JobEnum.Jurydirector.name()).stream().map(String::valueOf).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(directorIdList) && judgeIdList.contains(directorIdList.get(0))) {
                judgeIdList.removeAll(directorIdList);
                flowReq.setApproverIdList(judgeIdList);
                // 节点拆分成员和主任
                flowReq.setActivityId("userTask_juryMeetingReview_member");
                hasJuryDirector = true;
            }
            // 设置流程参数
            flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), MapUtil.of(ProcessVarEnum.projReviewHasJuryDirector.name(), hasJuryDirector));
        }

        userApiService.setTaskApprover(flowReq);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        // 读变量 有值显值 没值返回默认的
        List<String> userIdList = userApiService.getTaskApprover(rsp.getProcessInstanceId(), "userTask_juryVote");
        userIdList = CollectionUtils.isNotEmpty(userIdList) ? userIdList
                : userServiceAPI.getUsersByjobcod(JobEnum.Jury.name()).stream().map(UserDO::getId).distinct().map(String::valueOf).collect(Collectors.toList());
        List<SelectUserRSP> selectUserList = userIdList.stream()
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .distinct()
                .map(u -> SelectUserRSP.builder()
                        .userId(u)
                        .value(u)
                        .build())
                .collect(Collectors.toList());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(selectUserList.stream().map(SelectUserRSP::getUserId).collect(Collectors.toSet()));
        selectUserList.forEach(s -> {
            s.setUserName(userNameMap.get(s.getUserId()));
            s.setLabel(userNameMap.get(s.getUserId()));
        });
        rsp.getDynamicFormData().put(getType().name(), selectUserList);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_chooseJudges;
    }

}
