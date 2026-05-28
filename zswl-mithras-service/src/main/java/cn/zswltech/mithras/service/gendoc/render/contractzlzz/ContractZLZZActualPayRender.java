package cn.zswltech.mithras.service.gendoc.render.contractzlzz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.config.TaxRateEnum;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2023/4/19
 * @description 租赁-直租-主合同-实际租金及租前息支付表
 */
@Component
public class ContractZLZZActualPayRender extends AbstractContractZLZZRender<ContractBaseInfo> {
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(32);
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 获取实际租金表
        List<ContractRentActual> actualList = businessDataRepository.listContractRentActual(contractBaseInfo.getId());
        Assert.notEmpty(actualList, () -> MithrasException.newException("实际租金表为空"));
        List<RentHelper> beforeRentList = new LinkedList<>();
        List<RentHelper> startRentList = new LinkedList<>();
        // 排序
//        actualList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
        actualList.sort(Comparator.comparing(ContractRentActual::getId));

        // 拆分租前和起租
        boolean isBefore = true;
        for (ContractRentActual contractRentActual : actualList) {
            if (isBefore) {
                if (Objects.nonNull(contractRentActual.getPrincipal()) && contractRentActual.getPrincipal() == 0) {
                    beforeRentList.add(BeanUtil.copyProperties(contractRentActual, RentHelper.class));
                } else {
                    isBefore = false;
                    startRentList.add(BeanUtil.copyProperties(contractRentActual, RentHelper.class));
                }
            } else {
                startRentList.add(BeanUtil.copyProperties(contractRentActual, RentHelper.class));
            }
        }

        //  增加首期租金
        if(!ObjectUtils.isEmpty(startRentList)){
            ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, contractBaseInfo.getId()).one();
            if (!ObjectUtils.isEmpty(contractLeasePrice)
                    && !ObjectUtils.isEmpty( contractLeasePrice.getFirstInstallmentInterest()) &&  contractLeasePrice.getFirstInstallmentInterest()> 0) {
                RentHelper rentHelper = startRentList.get(0);
                RentHelper zeroRent = new RentHelper();
                zeroRent.setCashFlowPhase(0);
                zeroRent.setRent(contractLeasePrice.getFirstInstallmentInterest());
                zeroRent.setCashFlowDate(rentHelper.getCashFlowDate());
                startRentList.add(zeroRent);
                startRentList = startRentList.stream().sorted(Comparator.comparing(RentHelper::getCashFlowPhase)).collect(Collectors.toList());
            }
        }
        // 记录租金和
        long rentTotal = 0;
        for (RentHelper rentHelper : startRentList) {
            if (Objects.nonNull(rentHelper.getRent())) {
                rentTotal = rentTotal + rentHelper.getRent();
            }
        }
        // 记录利息和
        long interestTotal = 0;
        for (RentHelper rentHelper : startRentList) {
            if (Objects.nonNull(rentHelper.getInterest())) {
                interestTotal = interestTotal + rentHelper.getInterest();
            }
        }
        // 报价方案
        ContractLeasePrice contractLeasePrice = this.businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案为空"));
        // 合同金额
        renderMap.put("contractAmountCN", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("contractAmount", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(this::toYuan).orElse("          "));

        // 租金总和
        renderMap.put("rentTotal", Optional.of(rentTotal).filter(aLong -> aLong != 0).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("rentTotalCN", Optional.of(rentTotal).filter(aLong -> aLong != 0).map(this::toYuan).orElse("          "));

        // 利息总和
        BigDecimal interestAmount = NumberUtil.div(String.valueOf(interestTotal), GlobalConstants.MONEY_MULTIPLE);
        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_AMOUNT_CN, NumberChineseFormatter.format(interestAmount.doubleValue(), true, true));
        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_AMOUNT, Optional.ofNullable(this.toYuan(interestTotal)).orElse("        "));
        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_ZZ);
        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_EXCLUDE_TAX, Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, null)).orElse("       "));
        renderMap.put(ContractZLZZActualPayRender.RenderParameterKeyHolder.INTEREST_TAX, Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, taxRate)).orElse("       "));

        // 租前息和租金支付表格
        renderMap.put("rentTable", this.renderRentTable(contractBaseInfo, contractLeasePrice, beforeRentList, startRentList, rentTotal, true));

        // 签名
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        renderMap.put("leaseNameListText", this.renderRentTableFileSignText(contractTenantryList));

        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "1-4.实际租金及租前息支付表.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractSubTypeEnum.ZL_ZZ_ACTUAL_PAY.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-4.实际租金及租前息支付表.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-4.实际租金及租前息支付表.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 手续费 大写
        public static final String INTEREST_AMOUNT_CN = "interestAmountCN";
        // 手续费 小写
        public static final String INTEREST_AMOUNT = "interestAmount";
        // 手续费-不含税
        public static final String INTEREST_EXCLUDE_TAX = "interestExcludeTax";
        // 手续费-税额
        public static final String INTEREST_TAX = "interestTax";
    }
}
