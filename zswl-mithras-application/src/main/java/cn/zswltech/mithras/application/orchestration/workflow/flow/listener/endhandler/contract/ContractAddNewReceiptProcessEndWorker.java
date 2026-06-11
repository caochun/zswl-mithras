package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler.contract;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlyStampDutyService;
import cn.zswltech.mithras.application.orchestration.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 合同新增借据
 *
 * @author wangchuanhao
 * @date 2022/12/15 10:42 AM
 */
@Slf4j
@Component
public class ContractAddNewReceiptProcessEndWorker extends AbstractContractProcessEndWorker {
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private FtpInterestBaseInfoService ftpInterestBaseInfoService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;

    @Override
    public void customProcessPass(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {
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
        // 异步执行收入确认分摊逻辑
        this.doIncomeSharing(modelKey, contractId);
        // 印花税记录落库
        monthlyStampDutyService.saveRecordProj(contractId);
    }

    @Override
    public void afterAllHook(String modelKey, Long contractId, Integer endType, Long startUserId, String processInstanceId) {

    }

    @Override
    public List<ProcessModelTypeEnum> handleModelTypeList() {
        return ListUtil.toList(ProcessModelTypeEnum.ContractAddNewReceiptFlow);
    }

    @Override
    public ContractStatus getContractStatus(String modelKey, boolean processPass) {
        return null;
    }

    @Override
    public ContractProcessStatusEnum getContractProcessStatus(String modelKey, boolean processPass) {
        return processPass ? ContractProcessStatusEnum.NEW_RECEIPT_PASS : ContractProcessStatusEnum.NEW_RECEIPT_CANCEL;
    }

}
