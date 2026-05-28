package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowUserApiService;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.mithras.dto.flow.form.ChooseAdjudicatorReq;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 表单处理器
 *
 * @author zhaozhengkang
 * @date 2022/8/30 15:44 AM
 * @deprecated 替代类 cn.zswltech.mithras.service.flow.dynamicform.projreview.SetPricingChooseApproveAuthHandler  为了兼容老流程暂时保留该类
 */
@Deprecated
@Component
public class SetPricingFinalAdjudicatorHandler implements DynamicFormHandler {

    @Resource
    private FlowUserApiService userApiService;
    @Resource
    private Id2NameService id2NameService;
    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private FlowVariableApiService flowVariableApiService;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        if(ObjectUtil.isEmpty(formMap.get(getType().name()))){
            throw new MithrasException("终审人不能为空");
        }
    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        ChooseAdjudicatorReq chooseAdjudicatorReq =
                JSON.parseObject((JSON.toJSONString(formMap.get(getType().name()))),ChooseAdjudicatorReq.class);
        Map<String, Object> variables = new HashMap<>();
        variables.put("pricingFinalAdjudicator", chooseAdjudicatorReq.getAdjudicator());
        flowVariableApiService.setVariables(taskResp.getProcessInstanceId(), variables);
    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_pricingChooseAdjudicator;
    }

}
