package cn.zswltech.mithras.service.gendoc.render.contractzlzz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.contract.core.application.ContractLeasePriceService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2023/4/19
 * @description 租赁-直租-主合同-概算租金及租前息支付表
 */
@Component
public class ContractZLZZEstimatePayRender extends AbstractContractZLZZRender<ContractBaseInfo> {
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>(32);
        renderMap.put("contractCode", contractBaseInfo.getContractCode());

        // 获取概算租金表
        List<ContractRentEstimate> estimateList = businessDataRepository.listContractRentEstimate(contractBaseInfo.getId());
        Assert.notEmpty(estimateList, () -> MithrasException.newException("概算租金表为空"));
        List<RentHelper> beforeRentList = new LinkedList<>();
        List<RentHelper> startRentList = new LinkedList<>();
        // 排序
        estimateList.sort(Comparator.comparing(ContractRentEstimate::getCashFlowDate));
        estimateList.sort(Comparator.comparing(ContractRentEstimate::getId));
        // 拆分租前和起租
        boolean isBefore = true;
        for (ContractRentEstimate contractRentEstimate : estimateList) {
            if (isBefore) {
                if (Objects.nonNull(contractRentEstimate.getPrincipal()) && contractRentEstimate.getPrincipal() == 0) {
                    beforeRentList.add(BeanUtil.copyProperties(contractRentEstimate, RentHelper.class));
                } else {
                    isBefore = false;
                    startRentList.add(BeanUtil.copyProperties(contractRentEstimate, RentHelper.class));
                }
            } else {
                startRentList.add(BeanUtil.copyProperties(contractRentEstimate, RentHelper.class));
            }
        }
        //  增加首期租金
        if(!ObjectUtils.isEmpty(startRentList)){
            ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, contractBaseInfo.getId()).one();
            if (!ObjectUtils.isEmpty(contractLeasePrice)
                    && !ObjectUtils.isEmpty( contractLeasePrice.getFirstInstallmentInterest()) &&  contractLeasePrice.getFirstInstallmentInterest()> 0) {
                RentHelper zeroRent = new RentHelper();
                zeroRent.setCashFlowPhase(0);
                zeroRent.setRent(contractLeasePrice.getFirstInstallmentInterest());
                zeroRent.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
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
        renderMap.put("beforeRepayTimes", beforeRentList.size());
        LocalDate beforeStartDate = null;
        if (CollectionUtil.isNotEmpty(beforeRentList)) {
            beforeStartDate = beforeRentList.get(0).getCashFlowDate();
        }
        if (Objects.nonNull(beforeStartDate)) {
            renderMap.put("beforePlanStartYear", beforeStartDate.getYear());
            renderMap.put("beforePlanStartMonth", beforeStartDate.getMonthValue());
            renderMap.put("beforePlanStartDay", beforeStartDate.getDayOfMonth());
        } else {
            renderMap.put("beforePlanStartYear", "    ");
            renderMap.put("beforePlanStartMonth", "   ");
            renderMap.put("beforePlanStartDay", "   ");
        }
        if (CollectionUtil.isNotEmpty(beforeRentList)) {
            LocalDate start = beforeRentList.get(0).getCashFlowDate();
            LocalDate end = beforeRentList.get(beforeRentList.size() - 1).getCashFlowDate();
            renderMap.put("beforeMonthCount", LocalDateTimeUtil.between(start.atStartOfDay(), end.atStartOfDay(), ChronoUnit.MONTHS) + 1);
        } else {
            renderMap.put("beforeMonthCount", 0);
        }

        // 报价方案
        ContractLeasePrice contractLeasePrice = this.businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        Assert.notNull(contractLeasePrice, () -> MithrasException.newException("报价方案为空"));
        renderMap.put("leaseYear", Optional.ofNullable(contractLeasePrice.getLeaseMonthCount()).map(e -> String.valueOf(BigDecimal.valueOf(e).divide(BigDecimal.valueOf(12), RoundingMode.UP).intValue())).orElse("     "));
        renderMap.put("repayTimes", Optional.ofNullable(contractLeasePrice.getRepayTimesTotal()).map(String::valueOf).orElse("    "));
        if (Objects.nonNull(contractBaseInfo.getEstimatedLeaseDate())) {
            renderMap.put("planStartYear", contractBaseInfo.getEstimatedLeaseDate().getYear());
            renderMap.put("planStartMonth", contractBaseInfo.getEstimatedLeaseDate().getMonthValue());
            renderMap.put("planStartDay", contractBaseInfo.getEstimatedLeaseDate().getDayOfMonth());
        } else {
            renderMap.put("planStartYear", "    ");
            renderMap.put("planStartMonth", "   ");
            renderMap.put("planStartDay", "   ");
        }
        renderMap.put("contractAmountCN", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("contractAmount", Optional.ofNullable(contractLeasePrice.getApplyCreditAmount()).map(this::toYuan).orElse("          "));
        RateType beforeRateType = RateType.of(contractLeasePrice.getBeforeRateType());
        renderMap.put("beforeRateType", Optional.ofNullable(beforeRateType).map(RateType::display).orElse("       "));
        renderMap.put("beforeInterestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getBeforeLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getBeforeLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        RateType rateType = RateType.of(contractLeasePrice.getRateType());
        renderMap.put("rateType", Optional.ofNullable(rateType).map(RateType::display).orElse("       "));
        renderMap.put("interestRate", NumberUtil.div(String.valueOf(Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0)), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        renderMap.put("cashFlowAmountTotalCN", NumberChineseFormatter.format(NumberUtil.div(String.valueOf(rentTotal), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true));
        renderMap.put("cashFlowAmountTotal", this.toYuan(rentTotal));

        // 租前息和租金支付表格
        renderMap.put("rentTable", this.renderRentTable(contractBaseInfo, contractLeasePrice, beforeRentList, startRentList, rentTotal, false));

        // 签名
        List<ContractTenantry> contractTenantryList = this.listContractTenantry(contractBaseInfo.getId());
        Assert.notEmpty(contractTenantryList, () -> MithrasException.newException("承租人为空"));
        renderMap.put("leaseNameListText", this.renderRentTableFileSignText(contractTenantryList));

        // 渲染文档
        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-直租合同", "1-3.概算租金及租前息支付表.docx");
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractSubTypeEnum.ZL_ZZ_ESTIMATE_PAY.display() + GlobalConstants.OFFICE_WORD_SUFFIX;
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
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-3.概算租金及租前息支付表.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-直租合同", "1-3.概算租金及租前息支付表.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
