package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description 租赁合同-租赁附表（概算表）
 */
@Component
public class ContractMainRentEstimateRender extends AbstractContractRender<ContractBaseInfo> {
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
        // 报价方案
        ContractLeasePrice contractLeasePrice = businessDataRepository.getContractLeasePrice(contractBaseInfo.getId());
        // 概算租金表 只取现金流项目为最近的
        List<ContractRentEstimate> contractRentEstimateList = businessDataRepository.listContractRentEstimate(contractBaseInfo.getId())
                .stream()
                .sorted(Comparator.comparing(ContractRentEstimate::getCashFlowPhase))
                .collect(Collectors.toList());
        //  增加首期利息
        if (!ObjectUtils.isEmpty(contractRentEstimateList) && !ObjectUtils.isEmpty(contractLeasePrice)) {
            Long firstInstallmentInterest = contractLeasePrice.getFirstInstallmentInterest();
            if (!ObjectUtils.isEmpty(contractLeasePrice) && !ObjectUtils.isEmpty(firstInstallmentInterest) && firstInstallmentInterest > 0) {
                ContractRentEstimate contractRentEstimate = new ContractRentEstimate();
                contractRentEstimate.setContractId(contractBaseInfo.getId());
                contractRentEstimate.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
                contractRentEstimate.setCashFlowPhase(0);
                contractRentEstimate.setRent(firstInstallmentInterest);
                contractRentEstimate.setPrincipal(0L);
                contractRentEstimate.setInterest(firstInstallmentInterest);
                contractRentEstimate.setRemainingPrincipal(0L);
                contractRentEstimateList.add(contractRentEstimate);
                contractRentEstimateList = contractRentEstimateList.stream().sorted(Comparator.comparing(ContractRentEstimate::getCashFlowPhase)).collect(Collectors.toList());
            }
        }
        // 概算租金总额
        BigDecimal rentEstimateAmountTotal = Util.mithrasLong2BigDecimal(contractRentEstimateList.stream()
                .mapToLong(ContractRentEstimate::getRent)
                .sum());

        renderMap.put("contractCode", contractBaseInfo.getContractCode());
//        renderMap.put("leaseYear", Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getLeaseMonthCount).map(l -> new BigDecimal(l).divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()).orElse(""));
        renderMap.put("monthCount", Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getLeaseMonthCount).map(String::valueOf).orElse("   "));
        renderMap.put("estimatedLeaseDate", Optional.ofNullable(contractBaseInfo.getEstimatedLeaseDate()).map(eld -> LocalDateTimeUtil.format(eld, "【yyyy】年【MM】月【dd】日")).orElse("【     】年【  】月【  】"));
        renderMap.put("rentEstimateAmountTotal", toYuan(rentEstimateAmountTotal.multiply(new BigDecimal(10000L)).longValue()));
        renderMap.put("rentEstimateAmountTotalCn", NumberChineseFormatter.format(rentEstimateAmountTotal.doubleValue(), true, true));
        renderMap.put("rentEstimateTable", renderRentEstimateTable(contractRentEstimateList));

        renderMap.put("mainLesseeName", Optional.ofNullable(mainTenanry)
                .map(ContractTenantry::getLesseeName)
                .orElse(""));

        InputStream inputStream;
        if (contractTenantryMap.size() == 1) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_租赁附表（概算表）_单一承租人.docx");
        } else {
            //
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_租赁附表（概算表）_共同承租人.docx");
            renderMap.put("jointLesseeName", contractTenantryMap.values().stream().filter(ct -> LesseeTypeEnum.JOINT_LESSEE.name().equals(ct.getLesseeType())).map(ContractTenantry::getLesseeName).collect(Collectors.joining("、")));
        }

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1-" + ContractSubTypeEnum.ESTIMATE_RENT.getSort() + "." + ContractSubTypeEnum.ESTIMATE_RENT.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private TableRenderData renderRentEstimateTable(List<ContractRentEstimate> infoList) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("期数", "租金支付日", "租金", "其中：租金", null).center().create());
        tableDataList.add(Rows.of(null, null, null, "租赁成本", "租赁利息").center().create());
        for (ContractRentEstimate info : infoList) {
            tableDataList.add(Rows.of(Optional.ofNullable(info.getCashFlowPhase()).map(String::valueOf).orElse(""),
                            Optional.ofNullable(info.getCashFlowDate()).map(LocalDateTimeUtil::formatNormal).orElse(""),
                            Optional.ofNullable(info.getRent()).map(this::toYuan2Digit).orElse(""),
                            Optional.ofNullable(info.getPrincipal()).map(this::toYuan2Digit).orElse(""),
                            Optional.ofNullable(info.getInterest()).map(this::toYuan2Digit).orElse(""))
                    .center().create());
        }
        tableDataList.add(Rows.of("合计", "",
                this.toYuan2Digit(infoList.stream().filter(m -> Objects.nonNull(m.getRent())).mapToLong(ContractRentEstimate::getRent).sum()),
                this.toYuan2Digit(infoList.stream().filter(m -> Objects.nonNull(m.getPrincipal())).mapToLong(ContractRentEstimate::getPrincipal).sum()),
                this.toYuan2Digit(infoList.stream().filter(m -> Objects.nonNull(m.getInterest())).mapToLong(ContractRentEstimate::getInterest).sum())
        ).center().create());
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray)
                .mergeRule(MergeCellRule.builder()
                        .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))
                        .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(1, 1))
                        .map(MergeCellRule.Grid.of(0, 2), MergeCellRule.Grid.of(1, 2))
                        .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4))
                        .build())
                .width(15.92D, new double[]{1.38D, 3.67D, 3.5D, 3.685D, 3.685D})
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
        FileTemplate fileTemplate;
        if (contractTenantryMap.size() == 1) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_租赁附表（概算表）_单一承租人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_租赁附表（概算表）_共同承租人.docx");
        }
        return fileTemplate;
    }
}
