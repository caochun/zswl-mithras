package cn.zswltech.mithras.contract.application.process.prepare.handle;

import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.contract.application.process.prepare.ContractBizProcessDataPort;
import cn.zswltech.mithras.contract.application.process.prepare.ContractProcessPrepareAuthPort;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
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
    private ContractProcessPrepareAuthPort contractProcessPrepareAuthPort;
    @Resource
    private ContractBizProcessDataPort contractBizProcessDataPort;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        ContractBaseInfo baseInfo = contractBaseInfoService.getById(Long.valueOf(prepare.getBusinessId()));
        contractProcessPrepareAuthPort.checkContractAuth(Long.valueOf(prepare.getBusinessId()));
        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setBusinessKey(valueOf(prepare.getBusinessId()));
        String processInstanceId;
        processInstanceId = getBean(FlowProcessApiService.class).start(req);
        if (ObjectUtil.isNotEmpty(baseInfo)) {
            contractBizProcessDataPort.recordBizData(processInstanceId, baseInfo.getClientId());
        }
        return processInstanceId;
    }
}
