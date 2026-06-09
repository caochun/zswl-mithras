package cn.zswltech.mithras.service.flow.dynamicform.risk;

import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.domain.req.SetTaskApproverReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.flow.form.SelectUserREQ;
import cn.zswltech.mithras.dto.flow.form.SelectUserRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 资产管理岗表单新增处理
 */
public abstract class AbstractAssetManagementHandler implements DynamicFormHandler, InitializingBean {

    @Resource
    private Id2NameService id2NameService;

    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;

    @Resource
    private FlowUserApiService userApiService;

    /**
     * 资产管理岗与复合岗节点 ID映射
     */
    public Map<String, String> assetManagementAndReviewNodeIdMap = new ConcurrentHashMap<>();

    /**
     * 初始化
     */
    protected abstract void init();

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if (Objects.isNull(formMap.get(getType().name()))) {
            throw new MithrasException("资产管理复核选择不能为空");
        }
        SelectUserREQ selectUser = JSONObject.parseObject(JSON.toJSONString(formMap.get(getType().name())), SelectUserREQ.class);
        if (Objects.isNull(selectUser) || selectUser.getUserId() == null || StringUtils.isBlank(selectUser.getUserId().toString())) {
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
        //资产管理复核节点
        flowReq.setActivityId(this.assetManagementAndReviewNodeIdMap.get(taskResp.getTaskActivityId()));
        userApiService.setTaskApprover(flowReq);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        // 返回系统内所有资产管理岗角色
        List<String> userIdList = userServiceAPI.getUsersByjobcod(JobEnum.assetmanagement.name()).stream().map(UserDO::getId).distinct().map(String::valueOf).collect(Collectors.toList());
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
    public void afterPropertiesSet() throws Exception {
        init();
    }

}
