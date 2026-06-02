package cn.zswltech.mithras.service.gendoc.render;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.*;

/**
 * @author dingqi
 * @date 2022/10/9
 * @description 放款审批表（保理）
 */
@Component
public class PaymentApprovalBLRender extends AbstractBasicRender<PaymentBaseInfo> {
    private static final String FILE_NAME = "保理业务放款审批表" + GlobalConstants.OFFICE_WORD_SUFFIX;

    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;

    @Override
    public String render(OutputStream outputStream, PaymentBaseInfo paymentBaseInfo) throws Exception {
        String templatePath = "/doc/放款_放款审批_保理.docx";
        Map<String, Object> renderMap = new HashMap<>(64);
        // 捞数据
        OrgDO orgDO = businessDataRepository.getOrgById(paymentBaseInfo.getConBizDeptId());
        UserDO userDO = businessDataRepository.getUser(paymentBaseInfo.getCreateBy());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(paymentBaseInfo.getContractId());
        ProcessResp latestProcess = this.getLatestProcess(
                Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()),
                Collections.singletonList(contractBaseInfoLib.getProjReviewId().toString()),
                Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType())
        );
        // 填充所需数据
        renderMap.put(PaymentApprovalBLRender.RenderParameterKeyHolder.BIZ_DEPT, Optional.ofNullable(orgDO).map(OrgDO::getName).orElse(""));
        renderMap.put(PaymentApprovalBLRender.RenderParameterKeyHolder.SPONSOR_NAME, Optional.ofNullable(userDO).map(UserDO::getUserName).orElse(""));
        renderMap.put(PaymentApprovalBLRender.RenderParameterKeyHolder.REVIEW_FLOW_ID, Optional.ofNullable(latestProcess).map(ProcessResp::getProcessInstanceId).orElse(""));
        renderMap.put(PaymentApprovalBLRender.RenderParameterKeyHolder.BL_CONTRACT_CODE, paymentBaseInfo.getContractCode());
        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(PaymentApprovalBLRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);
        return FILE_NAME;
    }

    private static class RenderParameterKeyHolder {
        // 发起部门
        private static final String BIZ_DEPT = "bizDept";
        // 项目主办/付款发起人
        private static final String SPONSOR_NAME = "sponsorName";
        // 债务人
        private static final String DEBTOR_NAME = "debtorName";
        // 债权人
        private static final String CREDITOR_NAME = "creditorName";
        // 保理授信额度
        private static final String BL_CREDIT_AMOUNT_WAN = "blCreditAmountW";
        // 本次保理合同额
        private static final String BL_CONTRACT_AMOUNT_WAN = "blContractAmountW";
        // 保理保证金
        private static final String BL_EARNEST_WAN = "blEarnestW";
        // 保理手续费
        private static final String BL_CONSULTING_FEE_WAN = "blConsultingFeeW";
        // 保理期限
        private static final String BL_MONTH = "blMonth";
        // 租赁授信额度
        private static final String ZL_CREDIT_AMOUNT_WAN = "zlCreditAmountW";
        // 本次租赁合同额
        private static final String ZL_CONTRACT_AMOUNT_WAN = "zlContractAmountW";
        // 租赁保证金
        private static final String ZL_EARNEST_WAN = "zlEarnestW";
        // 租赁手续费
        private static final String ZL_CONSULTING_FEE_WAN = "zlConsultingFeeW";
        // 租赁期限
        private static final String ZL_MONTH = "zlMonth";
        // 保理合同编号
        private static final String BL_CONTRACT_CODE = "blContractCode";
        // 评审会流程id
        private static final String REVIEW_FLOW_ID = "reviewFlowId";
    }
}
