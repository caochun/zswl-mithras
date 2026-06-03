package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class ContractStartRentAutoFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.ContractStartRentAutoFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.valueOf(prepare.getBusinessId()));
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        // modify 260316 在提交时前端新增调用了校验接口，如果校验不通过，用户仍点击确认，则继续正常发起流程
        //check(paymentBaseInfo);
        StartProcessReq req = new StartProcessReq();
        req.setBusinessKey(String.valueOf(contractBaseInfo.getId()));
        req.setStartUserId(String.valueOf(contractBaseInfo.getProjSponsorUserId()));
        req.setStartUserDeptId(String.valueOf(contractBaseInfo.getBizDeptId()));
        Map<String, Object> varMap = new HashMap<>();
        // 添加参数是为了流程结束时能准确知道应该变更哪一个付款申请的状态
        varMap.put(PaymentBaseInfoService.CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY, paymentBaseInfo.getId());
        varMap.put("projectSponsor", Collections.singletonList(String.valueOf(contractBaseInfo.getProjSponsorUserId())));
        req.setVariables(varMap);
        //req.setModelKey(ProcessModelTypeEnum.ContractStartRentAutoFlow.name());
        req.setModelKey(ProcessModelTypeEnum.ContractStartRentFlow.name());
        req.setSubModule(ContractStatus.START_RENT.name());
        req.setProcessInstanceName(contractBaseInfo.getContractCode() + ProcessModelTypeEnum.ContractStartRentAutoFlow.getDisplay());
        return flowProcessApiService.start(req);
    }
    private void check(PaymentBaseInfo paymentBaseInfo){
        // 校验实际IRR
        contractReceiptService.checkActualIrrByContractId(paymentBaseInfo.getContractId());
    }


}
