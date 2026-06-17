package cn.zswltech.mithras.payment.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.payment.application.PaymentWorkflowPort;
import cn.zswltech.mithras.payment.application.PaymentWorkflowProcessSnapshot;
import cn.zswltech.mithras.payment.application.render.PaymentApprovalRenderSnapshot;
import cn.zswltech.mithras.payment.application.render.PaymentApprovalRenderSupportPort;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/10/9
 * @description 放款审批表（租赁）
 */
@Component
public class PaymentApprovalZLRender {
    private static final String FILE_NAME = "租赁业务放款审批表" + GlobalConstants.OFFICE_WORD_SUFFIX;
    private static final String PUBLIC_UTILITIES_PROJECT_TYPE = "PUBLIC_UTILITIES";

    @Resource
    private PaymentApprovalRenderSupportPort paymentApprovalRenderSupportPort;
    @Resource
    private PaymentWorkflowPort paymentWorkflowPort;

    public String render(OutputStream outputStream, PaymentBaseInfo paymentBaseInfo) throws Exception {
        String templatePath;
        Map<String, Object> renderMap = new HashMap<>(32);
        // 捞数据
        PaymentApprovalRenderSnapshot renderSnapshot = paymentApprovalRenderSupportPort.getLeaseRenderSnapshot(paymentBaseInfo);
        if (PUBLIC_UTILITIES_PROJECT_TYPE.equals(renderSnapshot.getProjectType())) {
//            templatePath = "/doc/放款_放款审批_产业.docx";
            templatePath = "/doc/放款_放款审批_平台.docx";
        } else {
//            templatePath = "/doc/放款_放款审批_平台.docx";
            templatePath = "/doc/放款_放款审批_产业.docx";
        }
        PaymentWorkflowProcessSnapshot latestProcess = paymentWorkflowPort.getLatestPassedProjectReviewProcess(renderSnapshot.getProjReviewId());
        // 填充所需数据
        renderMap.put(RenderParameterKeyHolder.BIZ_DEPT, Optional.ofNullable(renderSnapshot.getBizDeptName()).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_NAME, Optional.ofNullable(renderSnapshot.getSponsorName()).orElse(""));
        renderMap.put(RenderParameterKeyHolder.LESSEE_NAME, Optional.ofNullable(renderSnapshot.getLesseeName()).orElse(""));
        renderMap.put(RenderParameterKeyHolder.CREDIT_AMOUNT_WAN, Optional.ofNullable(renderSnapshot.getApplyCreditAmount()).map(v -> this.toWan(v) + "万元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.EARNEST_WAN, Optional.ofNullable(renderSnapshot.getEarnestMoney()).map(v -> this.toWan(v) + "万元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.MONTH_COUNT, Optional.ofNullable(renderSnapshot.getLeaseMonthCount()).map(v -> v + "个月").orElse(""));
        renderMap.put(RenderParameterKeyHolder.PAY_AMOUNT_WAN, this.toWan(paymentBaseInfo.getApplyPaymentAmount()) + "万元");
        renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE_WAN, Optional.ofNullable(renderSnapshot.getConsultingFee()).map(v -> this.toWan(v) + "万元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.NOMINAL_PRICE, Optional.ofNullable(renderSnapshot.getNominalPrice()).map(v -> this.toYuan(v) + "元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CODE, paymentBaseInfo.getContractCode());
        renderMap.put(RenderParameterKeyHolder.LEGAL_PERSON, Optional.ofNullable(renderSnapshot.getLegalPerson()).orElse(""));
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CONSULTING_CODE, renderSnapshot.getConsultingContractCode());
        if (CollectionUtil.isNotEmpty(renderSnapshot.getGuarantorContractCodes())) {
            renderMap.put(RenderParameterKeyHolder.CONTRACT_GUARANTOR_TEXT, CharSequenceUtil.join("、", renderSnapshot.getGuarantorContractCodes()));
        }
        if (CollectionUtil.isNotEmpty(renderSnapshot.getMortgageContractCodes())) {
            renderMap.put(RenderParameterKeyHolder.CONTRACT_MORTGAGE_TEXT, CharSequenceUtil.join("、", renderSnapshot.getMortgageContractCodes()));
        }
        renderMap.put(RenderParameterKeyHolder.REVIEW_FLOW_ID, Optional.ofNullable(latestProcess).map(PaymentWorkflowProcessSnapshot::getProcessInstanceId).orElse(""));
        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(PaymentApprovalZLRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);
        return FILE_NAME;
    }

    private String toYuan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.00##", bigDecimal);
    }

    private String toWan(Long dbNumber) {
        if (Objects.isNull(dbNumber)) {
            return null;
        }
        BigDecimal bigDecimal = NumberUtil.div(dbNumber.toString(), String.valueOf(10000 * Long.parseLong(GlobalConstants.MONEY_MULTIPLE)));
        return NumberUtil.decimalFormat(",##0.00######", bigDecimal);
    }

    private static class RenderParameterKeyHolder {
        // 发起部门
        public static final String BIZ_DEPT = "bizDept";
        // 项目主办/付款发起人
        public static final String SPONSOR_NAME = "sponsorName";
        // 承租人
        public static final String LESSEE_NAME = "lesseeName";
        // 授信金额
        public static final String CREDIT_AMOUNT_WAN = "creditAmountW";
        // 保证金
        public static final String EARNEST_WAN = "earnestW";
        // 租期
        public static final String MONTH_COUNT = "monthCount";
        // 本次投放金额
        public static final String PAY_AMOUNT_WAN = "payAmountW";
        // 服务费/咨询费
        public static final String CONSULTING_FEE_WAN = "consultingFeeW";
        // 名义价款
        public static final String NOMINAL_PRICE = "nominalPrice";
        // 租赁合同编号
        public static final String CONTRACT_CODE = "contractCode";
        // 承租人法人代表
        public static final String LEGAL_PERSON = "legalPerson";
        // 咨询合同编号
        public static final String CONTRACT_CONSULTING_CODE = "contractConsultingCode";
        // 保证合同编号
        public static final String CONTRACT_GUARANTOR_TEXT = "contractGuarantorText";
        // 抵押合同编号
        public static final String CONTRACT_MORTGAGE_TEXT = "contractMortgageText";
        // 评审会流程id
        public static final String REVIEW_FLOW_ID = "reviewFlowId";
    }
}
