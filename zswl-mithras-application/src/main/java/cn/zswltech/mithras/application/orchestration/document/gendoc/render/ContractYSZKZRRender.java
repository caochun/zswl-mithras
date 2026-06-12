package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author wangchuanhao
 * @date 2022/11/1
 * @description 融资保理合同（有追明）应收账款转让申请暨确认书
 */
@Component
public class ContractYSZKZRRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        List<ContractTenantry> creditorList = contractTenantryMap.values().stream().filter(t -> CreditorDebtorTypeEnum.CREDITOR.name().equals(t.getLesseeType())).collect(Collectors.toList());
        Set<Long> creditorClientIdSet = creditorList.stream().map(ContractTenantry::getLesseeId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(creditorClientIdSet);
        ContractFactoringPrice contractFactoringPrice = businessDataRepository.getContractFactoringPrice(contractBaseInfo.getId());

        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("contractYear", contractBaseInfo.getContractYear());
        renderMap.put("contractSequence", String.format("%04d", contractBaseInfo.getSequence()));
        renderMap.put("allCreditorName", creditorList.stream().map(c -> clientMap.get(c.getLesseeId()).getClientName()).collect(Collectors.joining("、")));
        renderMap.put("applyCreditAmount", this.toYuan(contractFactoringPrice.getContractAmount()));
        renderMap.put("factoringFinancingProportion", NumberUtil.div(String.valueOf(contractFactoringPrice.getFactoringFinancingProportion()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        renderMap.put("factoringCreditTerm", contractFactoringPrice.getFactoringCreditTerm());
        renderMap.put("factoringRatePercent", NumberUtil.div(String.valueOf(contractFactoringPrice.getFactoringRatePercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        renderMap.put("lprType", Optional.ofNullable(LPRTypeEnum.of(contractFactoringPrice.getLprType())).map(item -> item.display).orElse("      "));
        renderMap.put("lprPercent", NumberUtil.div(String.valueOf(contractFactoringPrice.getLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "%");
        renderMap.put("lprAddPercent", NumberUtil.div(String.valueOf(contractFactoringPrice.getLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
        renderMap.put("rateType", Optional.ofNullable(RateType.of(contractFactoringPrice.getRateType())).map(item -> item.display).orElse("      "));
        renderMap.put("repayCalcType", repayCalcType(contractFactoringPrice.getRepayCalcType()));
        renderMap.put("consultingFee", this.toYuan(contractFactoringPrice.getConsultingFee()));
        renderMap.put("earnestMoney", this.toYuan(contractFactoringPrice.getEarnestMoney()));

        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", "合同_保理合同_附属_应收账款转让申请暨确认书.docx");
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1-" + ContractSubTypeEnum.YSZKZR_SQ_JQRS.getSort() + "." + ContractSubTypeEnum.YSZKZR_SQ_JQRS.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private String repayCalcType(String code) {
        RepayCalcType repayCalcType = RepayCalcType.find(code);
        if (repayCalcType == null) {
            return " ";
        }
        switch (repayCalcType) {
            case YXFXDQHB:
                return "1";
            case QTDQHB:
                return "2";
            case DEBX:
            case DEBJ:
            case BGZHK:
                return "3";
            default:
                return " ";
        }
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", "合同_保理合同_附属_应收账款转让申请暨确认书.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", "合同_保理合同_附属_应收账款转让申请暨确认书.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
