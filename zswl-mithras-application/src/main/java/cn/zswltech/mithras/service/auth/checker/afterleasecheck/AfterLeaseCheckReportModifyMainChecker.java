package cn.zswltech.mithras.service.auth.checker.afterleasecheck;


import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.workflow.application.flow.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.workflow.application.flow.util.FlowUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

@Component
public class AfterLeaseCheckReportModifyMainChecker implements IDataAuthChecker {
    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("id不能为空");
        }
        dataAuthSponsorUserRule.check(businessModule, keyId);
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(keyId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            // 流程为空 放过
            return true;
        }
        boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
        if (!isStartUserNode && !FlowUtil.isSpecificNode(processResp, FlowConstants.PROJECT_MANAGER)) {
            throw new AuthCheckException("该数据处于流程中，且流程不在发起人/项目经理节点，不允许修改数据");
        }
        return true;
    }

}
