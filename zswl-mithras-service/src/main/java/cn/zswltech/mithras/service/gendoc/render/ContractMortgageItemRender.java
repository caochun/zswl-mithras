package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgage;
import cn.zswltech.mithras.service.mapper.model.contract.ContractMortgageItem;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.alibaba.fastjson.JSONArray;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
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
 * @author wangchuanhao
 * @date 2023/2/15
 * @description 抵押合同-抵押物清单
 */
@Component
public class ContractMortgageItemRender extends AbstractContractRender<ContractMortgage> {

    @Resource
    private Id2NameService id2NameService;

    @Override
    public String render(OutputStream outputStream, ContractMortgage contractMortgage) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        // 获取数据
        ContractBaseInfo contractBaseInfo = getContractBaseInfo(contractMortgage.getContractId());
        // 抵押物清单
        List<ContractMortgageItem> contractMortgageItemList = businessDataRepository.listContractMortgageItem(contractMortgage.getId());
        // 抵押人名称
        List<Long> mortgageIdList = StringUtils.isBlank(contractMortgage.getMortgageIds()) ? new ArrayList<>() : JSONArray.parseArray(contractMortgage.getMortgageIds(), Long.class);
        Map<Long, String> clientNameMap = id2NameService.clientId2Name(mortgageIdList);
        List<String> mortgagaNameList = mortgageIdList.stream().map(clientNameMap::get).filter(StringUtils::isNotBlank).collect(Collectors.toList());

        renderMap.put("mortgageContractCode", contractMortgage.getMortgageContractCode());
        renderMap.put("mortgageNameWithComma", mortgagaNameList.stream().collect(Collectors.joining("、")));
        renderMap.put("mortgageNameWithSeal", mortgagaNameList.stream().map(s -> String.format("抵押人：【%s】（盖章）", s)).collect(Collectors.joining("\n\n\n       ")));
        renderMap.put("mortgageItemTable", renderMortgageItemTable(contractMortgageItemList));

        InputStream inputStream = getBean(FileTemplateService.class).getTemplate("合同-抵押合同", "合同_抵押合同_附属_抵押物清单.docx");


        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return ContractSubTypeEnum.MORTGAGE_ITEM.getDisplay() + "-" + Joiner.on("、").join(mortgagaNameList) + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private TableRenderData renderMortgageItemTable(List<ContractMortgageItem> infoList) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        Map<String, Boolean> colPrintFlagMap = colPrintFlagMap(infoList);
        int printColSize = Math.toIntExact(colPrintFlagMap.values().stream().filter(Boolean.TRUE::equals).count()) + 1;
        tableDataList.add(headBuild(colPrintFlagMap));
        tableDataList.add(secondHeadBuild(colPrintFlagMap));
        for (ContractMortgageItem info : infoList) {
            tableDataList.add(rowBuild(info, colPrintFlagMap));
        }
        tableDataList.add(tailBuild(infoList, colPrintFlagMap));
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        // 每列的宽度
        double colWidth = new BigDecimal(24).divide(new BigDecimal(printColSize), 2, RoundingMode.HALF_UP).doubleValue();
        double[] colWidthArray = new double[printColSize];
        for (int i = 0; i < printColSize; i++) {
            colWidthArray[i] = colWidth;
        }
        // 单元格合并规则
        MergeCellRule.MergeCellRuleBuilder mergeCellRuleBuilder = MergeCellRule.builder();
        if (printColSize > 1) {
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(0, printColSize - 1));
        }
        return Tables.of(tableDataArray)
                .mergeRule(mergeCellRuleBuilder.build())
                .width(24.0D, colWidthArray)
                .create();
    }

    /**
     * 判断某列是否需要输出
     * 当某列的值全为空时，该列需隐藏
     *
     * @param infoList
     * @return
     */
    private Map<String, Boolean> colPrintFlagMap(List<ContractMortgageItem> infoList) {
        Map<String, Boolean> colPrintFlagMap = new LinkedHashMap<>();
        for (ContractMortgageItem info : infoList) {
            if (StringUtils.isNotBlank(info.getCategory())) {
                colPrintFlagMap.put("category", true);
            }
            if (StringUtils.isNotBlank(info.getUniqueIdentifyCodeType())) {
                colPrintFlagMap.put("uniqueIdentifyCodeType", true);
            }
            if (StringUtils.isNotBlank(info.getName())) {
                colPrintFlagMap.put("name", true);
            }
            if (StringUtils.isNotBlank(info.getUniqueIdentifyCode())) {
                colPrintFlagMap.put("uniqueIdentifyCode", true);
            }
            if (StringUtils.isNotBlank(info.getSupplier())) {
                colPrintFlagMap.put("supplier", true);
            }
            if (StringUtils.isNotBlank(info.getQuantity())) {
                colPrintFlagMap.put("quantity", true);
            }
            if (StringUtils.isNotBlank(info.getUnit())) {
                colPrintFlagMap.put("unit", true);
            }
            if (StringUtils.isNotBlank(info.getPurchaseDate())) {
                colPrintFlagMap.put("purchaseDate", true);
            }
            if (Objects.nonNull(info.getOriginalBookValue()) && info.getOriginalBookValue() != 0) {
                colPrintFlagMap.put("originalBookValue", true);
            }
            if (Objects.nonNull(info.getAssessedValue()) && info.getAssessedValue() != 0) {
                colPrintFlagMap.put("assessedValue", true);
            }
            if (Objects.nonNull(info.getOriginalBookNetValue()) && info.getOriginalBookNetValue() != 0) {
                colPrintFlagMap.put("originalBookNetValue", true);
            }
            if (Objects.nonNull(info.getAssessedNetValue()) && info.getAssessedNetValue() != 0) {
                colPrintFlagMap.put("assessedNetValue", true);
            }
            if (StringUtils.isNotBlank(info.getInvoiceCode())) {
                colPrintFlagMap.put("invoiceCode", true);
            }
            if (StringUtils.isNotBlank(info.getStoragePlace())) {
                colPrintFlagMap.put("storagePlace", true);
            }
        }
        return colPrintFlagMap;
    }

    /**
     * 一级标题构建
     *
     * @param colPrintFlagMap
     * @return
     */
    private RowRenderData headBuild(Map<String, Boolean> colPrintFlagMap) {
        List<String> rowDataList = new ArrayList<>();
        int printColSize = Math.toIntExact(colPrintFlagMap.values().stream().filter(Boolean.TRUE::equals).count());
        rowDataList.add("抵押物明细");
        for (int i = 0; i < printColSize; i++) {
            rowDataList.add(null);
        }
        String[] rowDataArray = new String[rowDataList.size()];
        rowDataList.toArray(rowDataArray);
        return Rows.of(rowDataArray).center().create();
    }

    /**
     * 二级标题构建
     *
     * @param colPrintFlagMap
     * @return
     */
    private RowRenderData secondHeadBuild(Map<String, Boolean> colPrintFlagMap) {
        List<String> rowDataList = new ArrayList<>();
        rowDataList.add("序号");
        if (Boolean.TRUE.equals(colPrintFlagMap.get("category"))) {
            rowDataList.add("种类");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("uniqueIdentifyCodeType"))) {
            rowDataList.add("识别号类型");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("name"))) {
            rowDataList.add("名称");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("uniqueIdentifyCode"))) {
            rowDataList.add("唯一识别号");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("supplier"))) {
            rowDataList.add("供应商");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("quantity"))) {
            rowDataList.add("数量");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("unit"))) {
            rowDataList.add("计量单位");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("purchaseDate"))) {
            rowDataList.add("购置日期");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("originalBookValue"))) {
            rowDataList.add("账面原值（元）");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("assessedValue"))) {
            rowDataList.add("评估原值（元）");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("originalBookNetValue"))) {
            rowDataList.add("账面净值（元）");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("assessedNetValue"))) {
            rowDataList.add("评估净值（元）");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("invoiceCode"))) {
            rowDataList.add("发票号");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("storagePlace"))) {
            rowDataList.add("存放地点");
        }

        String[] rowDataArray = new String[rowDataList.size()];
        rowDataList.toArray(rowDataArray);
        return Rows.of(rowDataArray).center().create();
    }

    /**
     * 尾端构建 合计行
     *
     * @param colPrintFlagMap
     * @return
     */
    private RowRenderData tailBuild(List<ContractMortgageItem> infoList, Map<String, Boolean> colPrintFlagMap) {
        long totalOriginalBookValue = 0;
        long totalOriginalBookNetValue = 0;
        long totalAssessedValue = 0;
        long totalAssessedNetValue = 0;
        for (ContractMortgageItem info : infoList) {
            if (Objects.nonNull(info.getOriginalBookValue())) {
                totalOriginalBookValue = totalOriginalBookValue + info.getOriginalBookValue();
            }
            if (Objects.nonNull(info.getOriginalBookNetValue())) {
                totalOriginalBookNetValue = totalOriginalBookNetValue + info.getOriginalBookNetValue();
            }
            if (Objects.nonNull(info.getAssessedValue())) {
                totalAssessedValue = totalAssessedValue + info.getAssessedValue();
            }
            if (Objects.nonNull(info.getAssessedNetValue())) {
                totalAssessedNetValue = totalAssessedNetValue + info.getAssessedNetValue();
            }
        }

        List<String> rowDataList = new ArrayList<>();
        rowDataList.add("合计");
        if (Boolean.TRUE.equals(colPrintFlagMap.get("category"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("uniqueIdentifyCodeType"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("name"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("uniqueIdentifyCode"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("supplier"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("quantity"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("unit"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("purchaseDate"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("originalBookValue"))) {
            rowDataList.add(this.zeroToBlank(this.toYuan(totalOriginalBookValue)));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("assessedValue"))) {
            rowDataList.add(this.zeroToBlank(this.toYuan(totalAssessedValue)));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("originalBookNetValue"))) {
            rowDataList.add(this.zeroToBlank(this.toYuan(totalOriginalBookNetValue)));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("assessedNetValue"))) {
            rowDataList.add(this.zeroToBlank(this.toYuan(totalAssessedNetValue)));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("invoiceCode"))) {
            rowDataList.add("");
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("storagePlace"))) {
            rowDataList.add("");
        }

        String[] rowDataArray = new String[rowDataList.size()];
        rowDataList.toArray(rowDataArray);
        return Rows.of(rowDataArray).center().create();
    }

    /**
     * 生成一行数据
     *
     * @param info
     * @param colPrintFlagMap
     * @return
     */
    private RowRenderData rowBuild(ContractMortgageItem info, Map<String, Boolean> colPrintFlagMap) {
        List<String> rowDataList = new ArrayList<>();
        rowDataList.add(Optional.ofNullable(info.getSequence()).map(Object::toString).orElse(""));
        if (Boolean.TRUE.equals(colPrintFlagMap.get("category"))) {
            rowDataList.add(Optional.ofNullable(info.getCategory()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("uniqueIdentifyCodeType"))) {
            rowDataList.add(Optional.ofNullable(info.getUniqueIdentifyCodeType()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("name"))) {
            rowDataList.add(Optional.ofNullable(info.getName()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("uniqueIdentifyCode"))) {
            rowDataList.add(Optional.ofNullable(info.getUniqueIdentifyCode()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("supplier"))) {
            rowDataList.add(Optional.ofNullable(info.getSupplier()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("quantity"))) {
            rowDataList.add(Optional.ofNullable(info.getQuantity()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("unit"))) {
            rowDataList.add(Optional.ofNullable(info.getUnit()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("purchaseDate"))) {
            rowDataList.add(Optional.ofNullable(info.getPurchaseDate()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("originalBookValue"))) {
            rowDataList.add(Optional.ofNullable(info.getOriginalBookValue()).map(this::toYuan).map(this::zeroToBlank).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("assessedValue"))) {
            rowDataList.add(Optional.ofNullable(info.getAssessedValue()).map(this::toYuan).map(this::zeroToBlank).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("originalBookNetValue"))) {
            rowDataList.add(Optional.ofNullable(info.getOriginalBookNetValue()).map(this::toYuan).map(this::zeroToBlank).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("assessedNetValue"))) {
            rowDataList.add(Optional.ofNullable(info.getAssessedNetValue()).map(this::toYuan).map(this::zeroToBlank).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("invoiceCode"))) {
            rowDataList.add(Optional.ofNullable(info.getInvoiceCode()).orElse(""));
        }
        if (Boolean.TRUE.equals(colPrintFlagMap.get("storagePlace"))) {
            rowDataList.add(Optional.ofNullable(info.getStoragePlace()).orElse(""));
        }

        String[] rowDataArray = new String[rowDataList.size()];
        rowDataList.toArray(rowDataArray);
        return Rows.of(rowDataArray).center().create();
    }

    @Override
    protected Set<Long> signClientIds(ContractMortgage contractMortgage) {
        if (StrUtil.isBlank(contractMortgage.getMortgageIds())) {
            return null;
        }
        List<Long> ids = JSONUtil.toList(contractMortgage.getMortgageIds(), Long.class);
        return new HashSet<>(ids);
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractMortgage contractMortgage) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-抵押合同", "合同_抵押合同_附属_抵押物清单.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractMortgage contractMortgage) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-抵押合同", "合同_抵押合同_附属_抵押物清单.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
