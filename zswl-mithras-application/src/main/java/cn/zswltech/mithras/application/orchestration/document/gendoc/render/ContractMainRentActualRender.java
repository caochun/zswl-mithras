package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import cn.zswltech.mithras.kpi.application.config.KpiParameterConfigService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
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
 * @date 2022/8/22
 * @description
 */
@Component
public class ContractMainRentActualRender extends AbstractContractRender<ContractBaseInfo> {
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        // 获取数据
        // 承租人列表
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        // 主承租人
        ContractTenantry mainTenanry = contractTenantryMap.values()
                .stream()
                .filter(t -> LesseeTypeEnum.MAIN_LESSSEE.name().equals(t.getLesseeType()))
                .findFirst()
                .orElse(null);
        // 实际租金列表
        List<ContractRentActual> rentActualList = businessDataRepository.listFirstContractRentActual(contractBaseInfo.getId())
                .stream()
                .sorted(Comparator.comparing(ContractRentActual::getCashFlowPhase))
                .collect(Collectors.toList());
        List<ContractRentActual> contractRentActualList = new ArrayList<>();
        //  增加首期利息
        if (!ObjectUtils.isEmpty(rentActualList)) {
            ContractRentActual rentActual = rentActualList.get(0);
            ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, rentActual.getContractId()).one();
            Long firstInstallmentInterest = contractLeasePrice.getFirstInstallmentInterest();
            if (!ObjectUtils.isEmpty(contractLeasePrice) && !ObjectUtils.isEmpty(firstInstallmentInterest) && firstInstallmentInterest > 0) {
                ContractRentActual contractRentActual = new ContractRentActual();
                contractRentActual.setContractId(rentActual.getContractId());
                contractRentActual.setReceiptId(rentActual.getReceiptId());
                contractRentActual.setCashFlowDate(rentActual.getCashFlowDate());
                contractRentActual.setCashFlowPhase(0);
                contractRentActual.setRent(firstInstallmentInterest);
                contractRentActual.setInterest(firstInstallmentInterest);
                contractRentActual.setPrincipal(0L);
                contractRentActual.setRemainingPrincipal(0L);
                contractRentActualList.add(contractRentActual);
            }
            contractRentActualList.addAll(rentActualList);
        }

        Long rentTotal = contractRentActualList.stream().map(ContractRentActual::getRent).filter(Objects::nonNull).reduce(Long::sum).orElse(null);
        // 租金总和
        renderMap.put("rentTotalCN", Optional.ofNullable(rentTotal).map(e -> NumberChineseFormatter.format(NumberUtil.div(e.toString(), GlobalConstants.MONEY_MULTIPLE).doubleValue(), true, true)).orElse("             "));
        renderMap.put("rentTotal", Optional.ofNullable(rentTotal).map(this::toYuan).orElse("          "));

        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("mainLesseeName", Optional.ofNullable(mainTenanry)
                .map(ContractTenantry::getLesseeName)
                .orElse(""));
        renderMap.put("rentActualTable", renderRentActualTable(contractRentActualList));

        // 利息总和
        Long interestTotal = contractRentActualList.stream().map(ContractRentActual::getInterest).filter(Objects::nonNull).reduce(Long::sum).orElse(null);
        BigDecimal taxRate = kpiParameterConfigService.getTaxRate(TaxRateEnum.XMS_ZL_HZ);
        renderMap.put(ContractMainRentActualRender.RenderParameterKeyHolder.INTEREST_EXCLUDE_TAX, Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, null)).orElse("        "));
        renderMap.put(ContractMainRentActualRender.RenderParameterKeyHolder.INTEREST_TAX, Optional.ofNullable(calculateTaxValue(interestTotal, taxRate, taxRate)).orElse("        "));

        InputStream inputStream;
        if (contractTenantryMap.size() == 1) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_实际租金表_单一承租人.docx");
        } else {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_实际租金表_共同承租人.docx");
            renderMap.put("jointLesseeName", contractTenantryMap.values().stream().filter(ct -> LesseeTypeEnum.JOINT_LESSEE.name().equals(ct.getLesseeType())).map(ContractTenantry::getLesseeName).collect(Collectors.joining("、")));
        }
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1-" + ContractSubTypeEnum.ACTUAL_RENT.getSort() + "." + ContractSubTypeEnum.ACTUAL_RENT.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private TableRenderData renderRentActualTable(List<ContractRentActual> infoList) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("期数", "租金支付日", "租金", "其中：租金", null).center().create());
        tableDataList.add(Rows.of(null, null, null, "租赁成本", "租赁利息").center().create());
        for (ContractRentActual info : infoList) {
            tableDataList.add(Rows.of(Optional.ofNullable(info.getCashFlowPhase()).map(c -> String.format("第%s期", c)).orElse(""),
                            Optional.ofNullable(info.getCashFlowDate()).map(LocalDateTimeUtil::formatNormal).orElse(""),
                            Optional.ofNullable(info.getRent()).map(this::toYuan2Digit).orElse(""),
                            Optional.ofNullable(info.getPrincipal()).map(this::toYuan2Digit).orElse(""),
                            Optional.ofNullable(info.getInterest()).map(this::toYuan2Digit).orElse(""))
                    .center().create());
        }
        // 2023-02-28 应客户要求 实际租金表的合计行去掉
        // 2023-03-02 应客户要求 实际租金表的合计行加上 为空值
        tableDataList.add(Rows.of("合计", "", "", "", "").center().create());
//                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getRent())).mapToLong(ContractRentActual::getRent).sum()),
//                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getPrincipal())).mapToLong(ContractRentActual::getPrincipal).sum()),
//                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getInterest())).mapToLong(ContractRentActual::getInterest).sum())
//        ).center().create());
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray)
                .mergeRule(MergeCellRule.builder()
                        .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))
                        .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(1, 1))
                        .map(MergeCellRule.Grid.of(0, 2), MergeCellRule.Grid.of(1, 2))
                        .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4))
                        .build())
                .width(15.44D, new double[]{2.19D, 2.75D, 3.75D, 3.5D, 3.25D})
                .create();

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
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private FileTemplate getFileTemplate(ContractBaseInfo contractBaseInfo) {
        // 承租人列表
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        // 主承租人
        ContractTenantry mainTenantry = contractTenantryMap.values()
                .stream()
                .filter(t -> LesseeTypeEnum.MAIN_LESSSEE.name().equals(t.getLesseeType()))
                .findFirst()
                .orElse(null);
        FileTemplate fileTemplate;
        if (contractTenantryMap.size() == 1) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_实际租金表_单一承租人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_实际租金表_共同承租人.docx");
        }
        return fileTemplate;
    }


    private static class RenderParameterKeyHolder {
        // 租赁利息-不含税
        public static final String INTEREST_EXCLUDE_TAX = "interestExcludeTax";
        // 租赁利息-税额
        public static final String INTEREST_TAX = "interestTax";
    }

}
