package cn.zswltech.mithras.service.gendoc.render.contractzlzz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountPayeeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractAccountUseEnum;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2023/4/19
 * @description 租赁-直租-主合同-融资租赁合同
 */
@Component
public class ContractZLZZMainRender extends AbstractContractZLZZRender<ContractBaseInfo> {

    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(128);
        ProjectBizType projectBizType = Assert.notNull(ProjectBizType.of(contractBaseInfo.getBizType()), () -> MithrasException.newException("业务类型不能为空"));
        LeaseType leaseType = Assert.notNull(LeaseType.of(contractBaseInfo.getLeaseType()), () -> MithrasException.newException("租赁类型不能为空"));
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 承租人
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        renderMap.put("tenantryTable", this.renderTenantry("承租人名称", contractTenantryList, true));

        // 报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案为空"));
        renderMap.put("monthCount", Optional.ofNullable(contractLeasePrice.getLeaseMonthCount()).map(String::valueOf).orElse("   "));
        renderMap.put("contractAmountCN", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("contractAmount", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(this::toYuan).orElse("          "));
        renderMap.put("beforeRateType", Optional.ofNullable(contractLeasePrice.getBeforeRateType()).map(e -> Objects.equals(RateType.FIXED.name(), e) ? "1" : "2").orElse("  "));

        //手续费
        BigDecimal commissionAmount = NumberUtil.div(contractLeasePrice.getCommission().toString(), GlobalConstants.MONEY_MULTIPLE);
        renderMap.put(ContractZLZZMainRender.RenderParameterKeyHolder.COMMISSION_AMOUNT_CN, NumberChineseFormatter.format(commissionAmount.doubleValue(), true, true));
        renderMap.put(ContractZLZZMainRender.RenderParameterKeyHolder.COMMISSION_AMOUNT, Optional.ofNullable(this.toYuan(contractLeasePrice.getCommission())).orElse("        "));
        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_ZZ);
        renderMap.put(ContractZLZZMainRender.RenderParameterKeyHolder.COMMISSION_EXCLUDE_TAX, Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, null)).orElse("        "));
        renderMap.put(ContractZLZZMainRender.RenderParameterKeyHolder.COMMISSION_TAX, Optional.ofNullable(calculateTaxValue(contractLeasePrice.getCommission(), taxRate, taxRate)).orElse("         "));

        if (RateType.FIXED.name().equals(contractLeasePrice.getBeforeRateType())) {
            // 固定利率
            renderMap.put("fixedBeforeInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getBeforeLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getBeforeLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            // 浮动利率不填的 默认填'\'
            renderMap.put("floatBeforeLprAdd", "\\");
            renderMap.put("floatBeforeLpr", "\\");
            renderMap.put("floatBeforeLprType", "\\");
            renderMap.put("floatBeforeInterestRate", "\\");
        } else if (RateType.FLOAT.name().equals(contractLeasePrice.getBeforeRateType())) {
            // 浮动利率
            renderMap.put("floatBeforeLprAdd", NumberUtil.div(String.valueOf(contractLeasePrice.getBeforeLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            renderMap.put("floatBeforeLpr", NumberUtil.div(String.valueOf(contractLeasePrice.getBeforeLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
            LPRTypeEnum lprTypeEnum = LPRTypeEnum.of(contractLeasePrice.getBeforeLprType());
            renderMap.put("floatBeforeLprType", Optional.ofNullable(lprTypeEnum).map(e -> e.display).orElse("    "));
            renderMap.put("floatBeforeInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getBeforeLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getBeforeLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            // 固定利率不填的 默认填'\'
            renderMap.put("fixedBeforeInterestRate", "\\");
        }
        renderMap.put("rateType", Optional.ofNullable(contractLeasePrice.getRateType()).map(e -> Objects.equals(RateType.FIXED.name(), e) ? "1" : "2").orElse("  "));
        if (RateType.FIXED.name().equals(contractLeasePrice.getRateType())) {
            // 固定利率
            renderMap.put("fixedInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            renderMap.put("interestRate", renderMap.get("fixedInterestRate"));
            // 浮动利率不填的 默认填'\'
            renderMap.put("floatLprAdd", "\\");
            renderMap.put("floatLpr", "\\");
            renderMap.put("floatLprType", "\\");
            renderMap.put("floatInterestRate", "\\");
        } else if (RateType.FLOAT.name().equals(contractLeasePrice.getRateType())) {
            // 浮动利率
            renderMap.put("floatLprAdd", NumberUtil.div(String.valueOf(contractLeasePrice.getLprAddPercent()), GlobalConstants.MONEY_MULTIPLE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            renderMap.put("floatLpr", NumberUtil.div(String.valueOf(contractLeasePrice.getLprPercent()), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
            LPRTypeEnum lprTypeEnum = LPRTypeEnum.of(contractLeasePrice.getLprType());
            renderMap.put("floatLprType", Optional.ofNullable(lprTypeEnum).map(e -> e.display).orElse("    "));
            renderMap.put("floatInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
            renderMap.put("interestRate", renderMap.get("floatInterestRate"));
            // 固定利率不填的 默认填'\'
            renderMap.put("fixedInterestRate", "\\");
        }
        renderMap.put("downPaymentCN", Optional.ofNullable(contractLeasePrice.getDownPayment()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("           "));
        renderMap.put("downPayment", Optional.ofNullable(contractLeasePrice.getDownPayment()).map(this::toYuan).orElse("          "));
        if (Objects.nonNull(contractLeasePrice.getEarnestMoney()) && contractLeasePrice.getEarnestMoney() > 0) {
            renderMap.put("hasEarnestMoney", 1);
            renderMap.put("earnestCN", NumberChineseFormatter.format(NumberUtil.div(contractLeasePrice.getEarnestMoney().toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true));
            renderMap.put("earnest", this.toYuan(contractLeasePrice.getEarnestMoney()));
        } else {
            renderMap.put("hasEarnestMoney", 2);
            renderMap.put("earnestCN", "\\");
            renderMap.put("earnest", "\\");
        }
        renderMap.put("nominalPriceCN", Optional.ofNullable(contractLeasePrice.getNominalPrice()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("       "));
        renderMap.put("nominalPrice", this.toYuan(contractLeasePrice.getNominalPrice()));

        // 收款账户
        List<ContractAccount> contractAccountList = businessDataRepository.listContractAccount(contractBaseInfo.getId(), ContractAccountUseEnum.ZLSK);
        ContractAccount contractAccountJia = this.findAccount(ContractAccountPayeeTypeEnum.JIA, contractAccountList);
        ContractAccount contractAccountYi = (this.findAccount(ContractAccountPayeeTypeEnum.YI, contractAccountList)!=null)?
                this.findAccount(ContractAccountPayeeTypeEnum.YI,contractAccountList):this.findAccount(ContractAccountPayeeTypeEnum.SELLER,contractAccountList);
        renderMap.put("accountNameJia", Optional.ofNullable(contractAccountJia).map(ContractAccount::getAccountName).orElse("        "));
        renderMap.put("accountBankJia", Optional.ofNullable(contractAccountJia).map(ContractAccount::getAccountAddress).orElse("        "));
        renderMap.put("accountNoJia", Optional.ofNullable(contractAccountJia).map(ContractAccount::getAccountNum).orElse("        "));
        renderMap.put("accountNameYi", Optional.ofNullable(contractAccountYi).map(ContractAccount::getAccountName).orElse("        "));
        renderMap.put("accountBankYi", Optional.ofNullable(contractAccountYi).map(ContractAccount::getAccountAddress).orElse("        "));
        renderMap.put("accountNoYi", Optional.ofNullable(contractAccountYi).map(ContractAccount::getAccountNum).orElse("        "));

        // 担保方式
        List<ContractGuarantor> contractGuarantorList = businessDataRepository.listContractGuarantor(contractBaseInfo.getId());
        renderMap.put("guaranteeWayList", this.renderGuarantorText(contractGuarantorList));

        // 乙方签字
        StringBuilder stringBuilder = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder.append("乙方（盖章）：").append(contractTenantryList.get(0).getLesseeName()).append("\n\n    ");
            stringBuilder.append("法定代表人（签字/签章）/授权代表人（签字）：");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder.append("乙方").append(i).append("（盖章）：").append(contractTenantry.getLesseeName()).append("\n\n    ");
                stringBuilder.append("法定代表人（签字/签章）/授权代表人（签字）：");
                if (i != contractTenantryList.size()) {
                    stringBuilder.append("\n\n    ");
                }
            }
        }
        renderMap.put("tenantryNameListText", stringBuilder.toString());

        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "1.融资租赁合同（直租）.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1." + projectBizType.display + "合同-" + leaseType.display + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private ContractAccount findAccount(ContractAccountPayeeTypeEnum contractAccountPayeeTypeEnum, List<ContractAccount> contractAccountList) {
        if (CollectionUtil.isEmpty(contractAccountList)) {
            return null;
        }
        for (ContractAccount contractAccount : contractAccountList) {
            if (Objects.equals(contractAccount.getPayeeType(), contractAccountPayeeTypeEnum.name())) {
                return contractAccount;
            }
        }
        return null;
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        return contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1.融资租赁合同（直租）.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1.融资租赁合同（直租）.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 手续费 大写
        public static final String COMMISSION_AMOUNT_CN = "commissionAmountCN";
        // 手续费 小写
        public static final String COMMISSION_AMOUNT = "commissionAmount";
        // 手续费-不含税
        public static final String COMMISSION_EXCLUDE_TAX = "commissionExcludeTax";
        // 手续费-税额
        public static final String COMMISSION_TAX = "commissionTax";
    }
}
