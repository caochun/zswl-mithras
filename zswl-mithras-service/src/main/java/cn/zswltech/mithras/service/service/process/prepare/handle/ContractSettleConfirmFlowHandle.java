package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.rule.DataAuthProcessRule;
import cn.zswltech.mithras.service.auth.rule.DataAuthSponsorUserRule;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static java.lang.String.valueOf;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class ContractSettleConfirmFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private DataAuthSponsorUserRule dataAuthSponsorUserRule;
    @Resource
    private DataAuthProcessRule dataAuthProcessRule;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        ContractBaseInfo baseInfo = contractBaseInfoService.getById(Long.valueOf(prepare.getBusinessId()));
        dataAuthSponsorUserRule.check(BusinessModuleEnum.CONTRACT, Long.valueOf(prepare.getBusinessId()));
        dataAuthProcessRule.check(BusinessModuleEnum.CONTRACT, Long.valueOf(prepare.getBusinessId()));
        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setBusinessKey(valueOf(prepare.getBusinessId()));
        String processInstanceId;
        processInstanceId = getBean(FlowProcessApiService.class).start(req);
        if (ObjectUtil.isNotEmpty(baseInfo)) {
            bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
        }
        return processInstanceId;
    }

}
