package cn.zswltech.mithras.service.flow.listener.endhandler.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.contract.core.application.ContractRemindRecordService;
import cn.zswltech.mithras.service.service.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.service.service.monthly.MonthlyStampDutyService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 合同起租
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:42 AM
 */
@Component
@Slf4j
public class ContractStartRentProcessEndWorker extends AbstractContractProcessEndWorker {

    @Resource
    private ContractRemindRecordService contractRemindRecordService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;

    @Override
    public void afterAllHook(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {

    }

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
        // 删除可能存在的起租提醒记录
        contractRemindRecordService.deleteByContractId(contractId);
        // 固化付款申请中预关联的借据id
        paymentBaseInfoService.fixedContractReceiptInfo(contractId);
        // FTP考核价格生效
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractId);
        if (CollectionUtil.isNotEmpty(contractReceiptList)) {
            contractReceiptList.forEach(contractReceipt -> ftpAssessmentInfoService.effectByPaymentReceiptId(null, contractReceipt.getId()));
        }
        // 生成FTP计息
        ftpInterestBaseInfoService.tryInitFtpInterest(contractId);
        // 付款申请结束投放
        this.paymentFinish(processInstanceId, contractId);
        // 印花税记录落库
        monthlyStampDutyService.saveRecordProj(contractId);
        // 异步执行收入确认分摊逻辑
        this.doIncomeSharing(modelKey, contractId);
        // 发起租后检查
        this.startAfterLeaseCheck(contractId);
    }

    /**
     * 通知收款
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notifyCollection(String modelKey, Long contractId) {
        super.notifyCollection(modelKey, contractId);
        //20251216 又调整为起租
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        contractService.notifyOnStartRentPass(contractBaseInfo, ProcessModelTypeEnum.getByName(modelKey));
    }

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return ListUtil.toList(ProcessModelTypeEnum.ContractStartRentFlow);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return processPass ? ContractStatus.START_RENT : null;
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return processPass ? ContractProcessStatusEnum.START_RENT_PASS : ContractProcessStatusEnum.START_RENT_CANCEL;
    }

}
