package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjectType;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractMortgageLibService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/10/9
 * @description 放款审批表（租赁）
 */
@Component
public class PaymentApprovalZLRender extends AbstractBasicRender<PaymentBaseInfo> {
    private static final String FILE_NAME = "租赁业务放款审批表" + GlobalConstants.OFFICE_WORD_SUFFIX;

    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private ContractMortgageLibService contractMortgageLibService;

    @Override
    public String render(OutputStream outputStream, PaymentBaseInfo paymentBaseInfo) throws Exception {
        String templatePath;
        Map<String, Object> renderMap = new HashMap<>(32);
        // 捞数据
        OrgDO orgDO = businessDataRepository.getOrgById(paymentBaseInfo.getConBizDeptId());
        UserDO userDO = businessDataRepository.getUser(paymentBaseInfo.getCreateBy());
        Client client = businessDataRepository.getClient(paymentBaseInfo.getClientId());
        CorpCommerceInfoLib corpCommerceInfoLib = businessDataRepository.getCorpCommerceInfo(paymentBaseInfo.getClientId());
        ContractLeasePrice contractLeasePriceLib = contractLeasePriceLibService.getLatestLib(paymentBaseInfo.getContractId());
        ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibService.getLatest(paymentBaseInfo.getContractId());
        List<ContractGuarantorLib> contractGuarantorLibList = contractGuarantorLibService.listByVersion(paymentBaseInfo.getContractId(), contractBaseInfoLib.getVersion());
        List<ContractMortgageLib> contractMortgageLibList = contractMortgageLibService.listByVersion(paymentBaseInfo.getContractId(), contractBaseInfoLib.getVersion());
        ProjectType projectType = ProjectType.of(contractBaseInfoLib.getProjectType());
        if (projectType == ProjectType.PUBLIC_UTILITIES) {
//            templatePath = "/doc/放款_放款审批_产业.docx";
            templatePath = "/doc/放款_放款审批_平台.docx";
        } else {
//            templatePath = "/doc/放款_放款审批_平台.docx";
            templatePath = "/doc/放款_放款审批_产业.docx";
        }
        ProcessResp latestProcess = this.getLatestProcess(
                Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name()),
                Collections.singletonList(contractBaseInfoLib.getProjReviewId().toString()),
                Arrays.asList(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType())
        );
        // 填充所需数据
        renderMap.put(RenderParameterKeyHolder.BIZ_DEPT, Optional.ofNullable(orgDO).map(OrgDO::getName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_NAME, Optional.ofNullable(userDO).map(UserDO::getUserName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.LESSEE_NAME, Optional.ofNullable(client).map(Client::getClientName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.CREDIT_AMOUNT_WAN, Optional.ofNullable(contractLeasePriceLib).map(v -> this.toWan(v.getApplyCreditAmount()) + "万元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.EARNEST_WAN, Optional.ofNullable(contractLeasePriceLib).map(v -> this.toWan(v.getEarnestMoney()) + "万元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.MONTH_COUNT, Optional.ofNullable(contractLeasePriceLib).map(v -> v.getLeaseMonthCount() + "个月").orElse(""));
        renderMap.put(RenderParameterKeyHolder.PAY_AMOUNT_WAN, this.toWan(paymentBaseInfo.getApplyPaymentAmount()) + "万元");
        renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE_WAN, Optional.ofNullable(contractLeasePriceLib).map(v -> this.toWan(v.getConsultingFee()) + "万元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.NOMINAL_PRICE, Optional.ofNullable(contractLeasePriceLib).map(v -> this.toYuan(v.getNominalPrice()) + "元").orElse(""));
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CODE, paymentBaseInfo.getContractCode());
        renderMap.put(RenderParameterKeyHolder.LEGAL_PERSON, Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getCorpRepresent).orElse(""));
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CONSULTING_CODE, contractBaseInfoLib.getConsultingContractCode());
        if (CollectionUtil.isNotEmpty(contractGuarantorLibList)) {
            renderMap.put(RenderParameterKeyHolder.CONTRACT_GUARANTOR_TEXT, CharSequenceUtil.join("、", contractGuarantorLibList.stream().map(ContractGuarantor::getGuarantorContractCode).collect(Collectors.toList())));
        }
        if (CollectionUtil.isNotEmpty(contractMortgageLibList)) {
            renderMap.put(RenderParameterKeyHolder.CONTRACT_MORTGAGE_TEXT, CharSequenceUtil.join("、", contractMortgageLibList.stream().map(ContractMortgage::getMortgageContractCode).collect(Collectors.toList())));
        }
        renderMap.put(RenderParameterKeyHolder.REVIEW_FLOW_ID, Optional.ofNullable(latestProcess).map(ProcessResp::getProcessInstanceId).orElse(""));
        // 渲染
        XWPFTemplate template = XWPFTemplate.compile(PaymentApprovalZLRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);
        return FILE_NAME;
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
