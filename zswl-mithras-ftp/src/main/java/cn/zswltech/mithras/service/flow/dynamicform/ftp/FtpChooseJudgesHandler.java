package cn.zswltech.mithras.service.flow.dynamicform.ftp;

import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.domain.req.SetTaskApproverReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.form.SelectUserREQ;
import cn.zswltech.mithras.dto.flow.form.SelectUserRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.domain.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;



@Component
public class FtpChooseJudgesHandler implements DynamicFormHandler {

    @Resource
    private FlowUserApiService userApiService;
    @Resource
    private Id2NameService id2NameService;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("定价委员选择不能为空");
        }
        List<SelectUserREQ> selectUserList = JSONArray.parseArray(JSON.toJSONString(formMap.get(getType().name()))).toJavaList(SelectUserREQ.class);
        if (CollectionUtils.isEmpty(selectUserList)) {
            throw new MithrasException("定价委员选择不能为空");
        }
        long userCount = selectUserList.stream().map(SelectUserREQ::getUserId).filter(Objects::nonNull).count();
        if (userCount == 0L) {
            throw new MithrasException("定价委员选择不能为空");
        }
//        if (userCount != 5L) {
//            throw new MithrasException("必须选择五位定价委员进行上会");
//        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        List<SelectUserREQ> selectUserList = JSONArray.parseArray(JSON.toJSONString(formMap.get(getType().name()))).toJavaList(SelectUserREQ.class);
        List<String> judgeIdList = selectUserList.stream().map(SelectUserREQ::getUserId).filter(Objects::nonNull).distinct().map(String::valueOf).collect(Collectors.toList());
        SetTaskApproverReq flowReq = new SetTaskApproverReq();
        flowReq.setProcessInstanceId(taskResp.getProcessInstanceId());
        flowReq.setApproverIdList(judgeIdList);
        // 需要写死评委会投票节点id 评审委员非公开投票
        flowReq.setActivityId("userTask_pricing_committee");
        userApiService.setTaskApprover(flowReq);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        // 读变量 有值显值 没值返回默认的
        List<String> userIdList = userApiService.getTaskApprover(rsp.getProcessInstanceId(), "userTask_pricing_committee");
        userIdList = CollectionUtils.isNotEmpty(userIdList) ? userIdList
                : userServiceAPI.getUsersByjobcod(JobEnum.pricingcommitteemember.name()).stream().map(UserDO::getId).distinct().map(String::valueOf).collect(Collectors.toList());
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
        return FlowDynamicFormEnum.ftp_chooseJudges;
    }

}
