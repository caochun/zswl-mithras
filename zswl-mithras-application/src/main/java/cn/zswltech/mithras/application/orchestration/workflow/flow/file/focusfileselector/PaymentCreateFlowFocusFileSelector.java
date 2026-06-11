package cn.zswltech.mithras.application.orchestration.workflow.flow.file.focusfileselector;

import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.FileBO;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/2/17
 * @description 付款申请创建流程-关注文件筛选
 */
@Component
public class PaymentCreateFlowFocusFileSelector extends AbstractFlowFocusFileSelector {
    @Resource
    private ContractCreateFlowFocusFileSelector contractCreateFlowFocusFileSelector;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Override
    public List<FileBO> listBizImportantFile(String businessKey, String version) {
        // 通过付款id找到合同id
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.parseLong(businessKey));
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款信息不存在");
        }
        String key = Objects.isNull(paymentBaseInfo.getContractId()) ? "-1" : paymentBaseInfo.getContractId().toString();
        return contractCreateFlowFocusFileSelector.listBizImportantFile(key, null);
    }

    @Override
    public List<FileBO> listMeetingDecisionFile(String businessKey, String version) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.parseLong(businessKey));
        if (Objects.isNull(paymentBaseInfo)) {
            throw new MithrasException("付款信息不存在");
        }
        String key = Objects.isNull(paymentBaseInfo.getContractId()) ? "-1" : paymentBaseInfo.getContractId().toString();
        return contractCreateFlowFocusFileSelector.listMeetingDecisionFile(key, null);
    }

    @Override
    protected List<String> bizImportantFileTypes() {
        return Arrays.asList(
                ContractTypeEnum.MAIN_CONTRACT.name(),
                ContractTypeEnum.GUARANTEE_CONTRACT.name()
        );
    }

    @Override
    protected BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.PAYMENT;
    }

    @Override
    public ProcessModelTypeEnum processType() {
        return ProcessModelTypeEnum.PaymentCreateFlow;
    }
}
