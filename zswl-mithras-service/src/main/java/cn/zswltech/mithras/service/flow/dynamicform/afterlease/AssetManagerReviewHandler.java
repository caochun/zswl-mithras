package cn.zswltech.mithras.service.flow.dynamicform.afterlease;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.domain.req.SetTaskApproverReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.form.SelectUserREQ;
import cn.zswltech.mithras.dto.flow.form.SelectUserRSP;
import cn.zswltech.mithras.dto.flow.form.SetProjectClassifyREQ;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表单处理器（租后检查报告(一般检查)-资产管理复核表单）
 *
 * @author luyujie
 * @date 2025/12/4
 */
@Component
public class AssetManagerReviewHandler implements DynamicFormHandler {

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
            throw new MithrasException("资产管理复核选择不能为空");
        }
        SelectUserREQ selectUser = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SelectUserREQ.class);
        if (Objects.isNull(selectUser) || selectUser.getUserId()==null || StringUtils.isBlank(selectUser.getUserId().toString())) {
            throw new MithrasException("资产管理复核选择不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        SetTaskApproverReq flowReq = new SetTaskApproverReq();
        flowReq.setProcessInstanceId(taskResp.getProcessInstanceId());
        //获取资产管理复核人
        SelectUserREQ selectmanage = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SelectUserREQ.class);
        List<String> manageIdList = new ArrayList<>();
        manageIdList.add(selectmanage.getUserId().toString());
        flowReq.setApproverIdList(manageIdList);
        //资产管理复核
        flowReq.setActivityId("assetManagementReview");
        userApiService.setTaskApprover(flowReq);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        // 读变量 有值显值 没值返回默认的
        List<String> userIdList = userApiService.getTaskApprover(rsp.getProcessInstanceId(), "assetManagementReview");
        userIdList = CollectionUtils.isNotEmpty(userIdList) ? userIdList
                : userServiceAPI.getUsersByjobcod(JobEnum.assetmanagement.name()).stream().map(UserDO::getId).distinct().map(String::valueOf).collect(Collectors.toList());
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
        return FlowDynamicFormEnum.afterLeaseCheckReport_assetManager;
    }

    @Data
    public static class FormData {
        /**
         * 资产管理复核岗
         */
        private String userId;

    }
}
