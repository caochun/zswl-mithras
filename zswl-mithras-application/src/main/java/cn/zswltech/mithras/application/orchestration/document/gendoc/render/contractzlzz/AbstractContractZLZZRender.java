package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contractzlzz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.customer.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.contract.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import com.deepoove.poi.data.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/4/20
 * @description
 */
public abstract class AbstractContractZLZZRender<T> extends AbstractContractRender<T> {
    protected TableRenderData renderTenantry(String title, List<ContractTenantry> contractTenantryList, boolean fixSpace) {
        List<Long> clientIds = contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList());
        Map<Long, List<CorpAddressInfoLib>> corpAddressInfoLibMap = businessDataRepository.getCorpAddressMap(clientIds);
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoLibMap = businessDataRepository.getCorpCommerceMap(clientIds);
        List<RowRenderData> tableDataList = new ArrayList<>();
        MergeCellRule.MergeCellRuleBuilder mergeCellRuleBuilder = MergeCellRule.builder();
        for (int i = 0; i < contractTenantryList.size(); i++) {
            ContractTenantry contractTenantry = contractTenantryList.get(i);
            tableDataList.add(Rows.of((fixSpace ? " " : "") + title + "：" + contractTenantry.getLesseeName(), null).rowExactHeight(0.7).verticalCenter().create());
            tableDataList.add(Rows.of((fixSpace ? " " : "") + "法定代表人：" + Optional.ofNullable(corpCommerceInfoLibMap.get(contractTenantry.getLesseeId())).map(CorpCommerceInfo::getCorpRepresent).orElse(null), null).rowExactHeight(0.7).verticalCenter().create());
            tableDataList.add(Rows.of((fixSpace ? " " : "") + "联系地址：" + this.getCorpRegistryAddress(corpAddressInfoLibMap.get(contractTenantry.getLesseeId())), null).rowExactHeight(0.7).verticalCenter().create());
            if (Objects.nonNull(contractTenantry.getContactId())) {
                CorpContactInfoLib corpContactInfoLib = this.getNewestContact(contractTenantry.getContactId());
                tableDataList.add(Rows.of((fixSpace ? " " : "") + "联系人：" + Optional.ofNullable(corpContactInfoLib.getName()).orElse(""), (fixSpace ? " " : "") + "联系人手机：" + Optional.ofNullable(corpContactInfoLib.getTelephone()).orElse("")).rowExactHeight(0.7).verticalCenter().create());
                tableDataList.add(Rows.of((fixSpace ? " " : "") + "电子邮箱：" + Optional.ofNullable(corpContactInfoLib.getMail()).orElse(""), (fixSpace ? " " : "") + "固定电话：").rowExactHeight(0.7).verticalCenter().create());
            } else {
                tableDataList.add(Rows.of((fixSpace ? " " : "") + "联系人：", (fixSpace ? " " : "") + "联系人手机：").rowExactHeight(0.7).verticalCenter().create());
                tableDataList.add(Rows.of((fixSpace ? " " : "") + "电子邮箱：", (fixSpace ? " " : "") + "固定电话：").rowExactHeight(0.7).verticalCenter().create());
            }
            // 合并单元格
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(i * 5, 0), MergeCellRule.Grid.of(i * 5, 1));
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(i * 5 + 1, 0), MergeCellRule.Grid.of(i * 5 + 1, 1));
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(i * 5 + 2, 0), MergeCellRule.Grid.of(i * 5 + 2, 1));
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).mergeRule(mergeCellRuleBuilder.build()).width(15, new double[]{7.5, 7.5}).create();
    }

    protected TableRenderData renderLeaseItemTable(List<ContractLeaseItem> contractLeaseItemList) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        // 表头行
        tableDataList.add(Rows.of("序号", "租赁物描述", "数量", "单位", "价格（元）", "供货商", "备注").textBold().center().create());
        double count = 0;
        double total = 0;
        // 数据行
        for (ContractLeaseItem contractLeaseItem : contractLeaseItemList) {
            if (StrUtil.isBlank(contractLeaseItem.getRowData())) {
                continue;
            }
            String sequence = null;
            String name = null;
            String quantity = null;
            String unit = null;
            String originalBookValue = null;
            String supplier = null;
            Map<String, Object> cellMap = JSONUtil.toBean(contractLeaseItem.getRowData().replace("\n", "\\n"), Map.class);
            if (Objects.nonNull(cellMap.get("序号"))) {
                sequence = cellMap.get("序号").toString();
            }
            if (Objects.nonNull(cellMap.get("名称"))) {
                name = cellMap.get("名称").toString();
            }
            if (Objects.nonNull(cellMap.get("数量"))) {
                quantity = cellMap.get("数量").toString();
                if (NumberUtil.isNumber(quantity)) {
                    count = count + Double.parseDouble(quantity);
                }
            }
            if (Objects.nonNull(cellMap.get("计量单位"))) {
                unit = cellMap.get("计量单位").toString();
            }
            if (Objects.nonNull(cellMap.get("账面原值（元）"))) {
                originalBookValue = cellMap.get("账面原值（元）").toString();
                String s = originalBookValue.replace(",", "");
                if (NumberUtil.isNumber(s)) {
                    total = total + Double.parseDouble(s);
                }
            }
            if (Objects.nonNull(cellMap.get("供应商"))) {
                supplier = cellMap.get("供应商").toString();
            }
            tableDataList.add(Rows.of(sequence, name, quantity, unit, originalBookValue, supplier, null).center().create());
        }
        // 合计行
        tableDataList.add(Rows.of("合计", null, count > 0 ? BigDecimal.valueOf(count).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() : "", null, total > 0 ? BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).toPlainString() : "", null, null).center().create());
        // 合并单元格
        MergeCellRule.MergeCellRuleBuilder mergeCellRuleBuilder = MergeCellRule.builder();
        // 合并单元格
        mergeCellRuleBuilder.map(MergeCellRule.Grid.of(contractLeaseItemList.size() + 1, 0), MergeCellRule.Grid.of(contractLeaseItemList.size() + 1, 1));
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).mergeRule(mergeCellRuleBuilder.build()).width(16, new double[]{1.5, 3.5, 1.5, 1.5, 2.5, 3.5, 2}).create();
    }

    protected TableRenderData renderRentTable(ContractBaseInfo contractBaseInfo, ContractLeasePrice contractLeasePrice, List<RentHelper> beforeRentList, List<RentHelper> rentList, long rentTotal, boolean isActual) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        // 租前息
        tableDataList.add(Rows.of("期数", "租前息支付日", "每期租前息", "本租期起止日").center().create());
        for (int i = 0; i < beforeRentList.size(); i++) {
            RentHelper current = beforeRentList.get(i);
            StringBuilder stringBuilder = new StringBuilder();
            if (i == 0) {
                LocalDate localDate = isActual ? contractBaseInfo.getActualLeaseDate() : contractBaseInfo.getEstimatedLeaseDate();
                if (Objects.nonNull(localDate)) {
                    stringBuilder.append(LocalDateTimeUtil.format(localDate, DatePattern.NORM_DATE_PATTERN));
                } else {
                    stringBuilder.append("          ");
                }
            } else {
                if (Objects.nonNull(beforeRentList.get(i - 1).getCashFlowDate())) {
                    LocalDate ld = beforeRentList.get(i - 1).getCashFlowDate().plus(1L, ChronoUnit.DAYS);
                    stringBuilder.append(LocalDateTimeUtil.format(ld, DatePattern.NORM_DATE_PATTERN));
                } else {
                    stringBuilder.append("          ");
                }
            }
            stringBuilder.append(" - ");
            if (Objects.nonNull(current.getCashFlowDate())) {
                stringBuilder.append(LocalDateTimeUtil.format(current.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            } else {
                stringBuilder.append("          ");
            }
            tableDataList.add(this.renderRentRow(current, stringBuilder.toString()));
        }
        // 租金
        tableDataList.add(Rows.of("期数", "租金支付日", "每期租金金额", "本租期起止日").center().create());
        // 首期租金
        String firstRentStart = "          ";
        String firstRentEnd = "          ";
        if (CollectionUtil.isNotEmpty(beforeRentList)) {
            LocalDate firstRentStartDate = beforeRentList.get(beforeRentList.size() - 1).getCashFlowDate();
            if (Objects.nonNull(firstRentStartDate)) {
                firstRentStart = LocalDateTimeUtil.format(firstRentStartDate, DatePattern.NORM_DATE_PATTERN);
            }
        }
        if (CollectionUtil.isNotEmpty(rentList)) {
            LocalDate firstRentEndDate = rentList.get(0).getCashFlowDate();
            if (Objects.nonNull(firstRentEndDate)) {
                firstRentEnd = LocalDateTimeUtil.format(firstRentEndDate, DatePattern.NORM_DATE_PATTERN);
            }
        }
        tableDataList.add(Rows.of("首期租金", "起租前", Optional.ofNullable(contractLeasePrice.getDownPayment()).map(this::toYuan).orElse(null), firstRentStart + " - " + firstRentEnd).center().create());
        for (int i = 0; i < rentList.size(); i++) {
            RentHelper current = rentList.get(i);
            StringBuilder stringBuilder = new StringBuilder();
            if (i == 0) {
                if (CollectionUtil.isNotEmpty(beforeRentList)) {
                    RentHelper beforeLast = beforeRentList.get(beforeRentList.size() - 1);
                    if (Objects.nonNull(beforeLast.getCashFlowDate())) {
                        stringBuilder.append(LocalDateTimeUtil.format(beforeLast.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
                    } else {
                        stringBuilder.append("          ");
                    }
                } else {
                    stringBuilder.append("          ");
                }
            } else {
                if (Objects.nonNull(rentList.get(i - 1).getCashFlowDate())) {
                    LocalDate ld = rentList.get(i - 1).getCashFlowDate().plus(1L, ChronoUnit.DAYS);
                    stringBuilder.append(LocalDateTimeUtil.format(ld, DatePattern.NORM_DATE_PATTERN));
                } else {
                    stringBuilder.append("          ");
                }
            }
            stringBuilder.append(" - ");
            if (Objects.nonNull(current.getCashFlowDate())) {
                stringBuilder.append(LocalDateTimeUtil.format(current.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN));
            } else {
                stringBuilder.append("          ");
            }
            tableDataList.add(this.renderRentRow(current, stringBuilder.toString()));
        }
        // 合计行
        tableDataList.add(Rows.of("租前息及租金合计", null, this.toYuan(rentTotal), null).center().create());
        // 合并单元格
        MergeCellRule mergeCellRule = MergeCellRule.builder().map(
                MergeCellRule.Grid.of(1 + beforeRentList.size() + 1 + 1 + rentList.size(), 0),
                MergeCellRule.Grid.of(1 + beforeRentList.size() + 1 + 1 + rentList.size(), 1)).build();
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).mergeRule(mergeCellRule).width(15, new double[]{2.5, 3.5, 3.5, 5.5}).create();
    }

    protected String renderRentTableFileSignText(List<ContractTenantry> contractTenantryList) {
        StringBuilder stringBuilder = new StringBuilder();
        if (contractTenantryList.size() == 1) {
            stringBuilder.append("承租人：").append(contractTenantryList.get(0).getLesseeName()).append("（盖章）").append("\n    ");
            stringBuilder.append("签署日期：【    】年【  】月【  】日");
        } else {
            for (int i = 1; i <= contractTenantryList.size(); i++) {
                ContractTenantry contractTenantry = contractTenantryList.get(i - 1);
                stringBuilder.append("承租人").append(i).append("：").append(contractTenantry.getLesseeName()).append("（盖章）").append("\n    ");
                stringBuilder.append("签署日期：【    】年【  】月【  】日");
                if (i != contractTenantryList.size()) {
                    stringBuilder.append("\n\n\n    ");
                }
            }
        }
        return stringBuilder.toString();
    }

    private RowRenderData renderRentRow(RentHelper current, String startEndText) {
        return Rows.of(
                Optional.ofNullable(current.getCashFlowPhase()).map(String::valueOf).orElse(null),
                Optional.ofNullable(current.getCashFlowDate()).map(e -> LocalDateTimeUtil.format(e, DatePattern.NORM_DATE_PATTERN)).orElse(null),
                Optional.ofNullable(current.getRent()).map(this::toYuan).orElse(null),
                startEndText
        ).center().create();
    }

    @Data
    public static class RentHelper {
        /**
         * 日期
         */
        private LocalDate cashFlowDate;

        /**
         * 期项
         */
        private Integer cashFlowPhase;

        /**
         * 租金
         */
        private Long rent;

        /**
         * 本金
         */
        private Long principal;

        /**
         * 利息
         */
        private Long interest;
    }
}
