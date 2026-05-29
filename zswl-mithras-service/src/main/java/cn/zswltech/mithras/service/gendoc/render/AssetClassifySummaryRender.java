package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyMaterialsEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifySuggestEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.service.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/9/20
 * @description
 */
@Slf4j
@Component
public class AssetClassifySummaryRender extends AbstractBasicRender<AssetClassify> {
    @Resource
    private FileTemplateService fileTemplateService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractRentActualService contractRentActualService;

    @Override
    public String render(OutputStream outputStream, AssetClassify assetClassify) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        LocalDate firstDay = DateUtil.ensureQuarterFirstDay(assetClassify.getYear(), assetClassify.getQuarter());
        LocalDate lastDay = DateUtil.ensureQuarterLastDay(assetClassify.getYear(), assetClassify.getQuarter());
        renderMap.put("quarterFirstDay", LocalDateTimeUtil.format(firstDay, DatePattern.NORM_DATE_PATTERN));
        renderMap.put("quarterLastDay", LocalDateTimeUtil.format(lastDay, DatePattern.NORM_DATE_PATTERN));
        // 找到最近一次初分的人
        Long userId = assetClassify.getCreateBy();
        renderMap.put("operator", Optional.ofNullable(id2NameService.sysUserId2NameSingle(userId)).orElse(""));
        LocalDate now = LocalDate.now();
        renderMap.put("operationDate", LocalDateTimeUtil.format(now, DatePattern.NORM_DATE_PATTERN));
        renderMap.put("assetClassifyResultTable", this.buildTableRenderData(assetClassify));
        InputStream inputStream = fileTemplateService.getTemplate("资产五级分类", "资产五级分类_认定汇总审批表.docx");
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return AssetClassifyMaterialsEnum.ASSET_CLASSIFY_SUMMARY.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private TableRenderData buildTableRenderData(AssetClassify assetClassify) {
        RowRenderData headerRow = Rows.of("序号", "客户名称", "投放金额/万元", "风险敞口/万元", "剩余租期", "上次分类结果", "本次分类结果", "风险拨备计提比例", "风险管理部负责人", "法律合规部负责人", "首席风险官").center().create();// 优化需求，增加一列:法律合规部负责人
        List<AssetClassifyClient> assetClassifyClientList = assetClassifyClientService.listByAssetClassifyId(assetClassify.getId());
        int rowCount = assetClassifyClientList.size() + 1;
        RowRenderData[] rows = new RowRenderData[rowCount];
        rows[0] = headerRow;
        MergeCellRule mergeCellRule = null;
        if (CollectionUtil.isNotEmpty(assetClassifyClientList)) {
            try {
                for (int i = 0; i < assetClassifyClientList.size(); i++) {
                    final AssetClassifyClient assetClassifyClient = assetClassifyClientList.get(i);
                    final int realIndex = i + 1;
                    rows[realIndex] = buildRowData(realIndex, assetClassifyClient);
                }
            } catch (Exception e) {
                log.error("生成客户分类结果明细表格发生异常", e);
                throw new MithrasException("生成客户分类结果明细表格发生异常");
            }
            mergeCellRule = MergeCellRule.builder()
                    .map(MergeCellRule.Grid.of(1, 8), MergeCellRule.Grid.of(rowCount - 1, 8))  // 风险管理部负责人
                    .map(MergeCellRule.Grid.of(1, 9), MergeCellRule.Grid.of(rowCount - 1, 9))  // 法律合规部负责人
                    .map(MergeCellRule.Grid.of(1, 10), MergeCellRule.Grid.of(rowCount - 1, 10)) // 首席风险官
                    .build();
        }
        return Tables.of(rows).mergeRule(mergeCellRule)
                .width(26.5, new double[]{1, 3.2, 3.2, 3.2, 3.2, 2, 2, 2, 2.4, 2.4, 2.4})
                .create();// 优化需求，增加一列
    }

    private RowRenderData buildRowData(int sequence, AssetClassifyClient assetClassifyClient) {
        String payAmountTotalText = null;
        String riskExposureText = null;
        String remainingMonthCountText = null;
        AssetClassifyResultEnum lastClassifyResult = AssetClassifyResultEnum.of(assetClassifyClient.getLastClassifyResult());
        AssetClassifyResultEnum classifyResult;
        if (Objects.nonNull(assetClassifyClient.getSuggestResult()) && !Objects.equals(AssetClassifySuggestEnum.UNANIMITY.name(), assetClassifyClient.getSuggestResult())) {
            classifyResult = AssetClassifyResultEnum.of(assetClassifyClient.getSuggestResult());
        } else {
            classifyResult = AssetClassifyResultEnum.of(assetClassifyClient.getInitClassifyResult());
        }
        String provisionText = null;
        // 利用已有的拨备计提数据来确定使用的借据范围
        Map<Long, Integer> provisionMap;
        if (StrUtil.isNotBlank(assetClassifyClient.getProvisions())) {
            List<AssetClassifyClient.ProvisionData> provisionDataList = JSONUtil.toList(assetClassifyClient.getProvisions(), AssetClassifyClient.ProvisionData.class);
            provisionMap = provisionDataList.stream().filter(e -> Objects.nonNull(e.getReceiptId()) && Objects.nonNull(e.getWithdrawalRatio())).collect(Collectors.toMap(AssetClassifyClient.ProvisionData::getReceiptId, AssetClassifyClient.ProvisionData::getWithdrawalRatio));
        } else {
            provisionMap = Collections.emptyMap();
        }
        if (CollectionUtil.isNotEmpty(provisionMap)) {
            Set<Long> receiptIds = provisionMap.keySet();
            List<ContractReceipt> contractReceiptList = contractReceiptService.listByIds(receiptIds);
            // 投放金额
            String totalPayAmountStr = Util.toWanYuan(assetClassifyClient.getAmount());
            if (contractReceiptList.size() > 1) {
                payAmountTotalText = contractReceiptList.size() + "笔共计" + totalPayAmountStr;
            } else {
                payAmountTotalText = totalPayAmountStr;
            }
            // 风险敞口
            String riskExposureStr = Util.toWanYuan(assetClassifyClient.getStockRiskExposure());
            if (contractReceiptList.size() > 1) {
                riskExposureText = contractReceiptList.size() + "笔共计" + riskExposureStr;
            } else {
                riskExposureText = riskExposureStr;
            }
            // 租金表
            List<ContractRentActual> contractRentActualList = contractRentActualService.listByReceipts(receiptIds);
            Map<Long, List<ContractRentActual>> contractRentActualMap = contractRentActualList.stream().filter(e -> Objects.nonNull(e.getCashFlowDate())).collect(Collectors.groupingBy(ContractRentActual::getReceiptId));
            if (contractReceiptList.size() > 1) {
                List<String> strList = new LinkedList<>();
                for (int i = 0; i < contractReceiptList.size(); i++) {
                    ContractReceipt contractReceipt = contractReceiptList.get(i);
                    List<ContractRentActual> rentList = contractRentActualMap.get(contractReceipt.getId());
                    if (CollectionUtil.isNotEmpty(rentList)) {
                        // 计算剩余租期
                        strList.add(String.format("第%s笔%s个月", i + 1, this.calRemainingMonth(rentList)));
                    }
                }
                remainingMonthCountText = CharSequenceUtil.join("\n", strList);
            } else {
                ContractReceipt contractReceipt = contractReceiptList.get(0);
                List<ContractRentActual> rentList = contractRentActualMap.get(contractReceipt.getId());
                if (CollectionUtil.isNotEmpty(rentList)) {
                    remainingMonthCountText = String.format("%s个月", this.calRemainingMonth(rentList));
                }
            }
            if (contractReceiptList.size() > 1) {
                List<String> strList = new ArrayList<>(contractReceiptList.size());
                // 找合同
                List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(contractReceiptList.stream().map(ContractReceipt::getContractId).collect(Collectors.toSet()));
                Map<Long, ContractBaseInfo> contractMap = contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
                for (ContractReceipt contractReceipt : contractReceiptList) {
                    Integer withdrawRatio = provisionMap.get(contractReceipt.getId());
                    // 确定业务类型
                    ContractBaseInfo contractBaseInfo = contractMap.get(contractReceipt.getContractId());
                    ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
                    if (Objects.nonNull(projectBizType)) {
                        String s = Optional.ofNullable(withdrawRatio).map(e -> new BigDecimal(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "%").orElse("0%");
                        strList.add(String.format("%s：%s", projectBizType.display, s));
                    }
                }
                provisionText = CharSequenceUtil.join("\n", strList);
            } else {
                Integer withdrawRatio = provisionMap.get(contractReceiptList.get(0).getId());
                provisionText = Optional.ofNullable(withdrawRatio).map(e -> new BigDecimal(e).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "%").orElse("0%");
            }
        }
        String[] rowData = {String.valueOf(sequence), assetClassifyClient.getClientName(), payAmountTotalText, riskExposureText, remainingMonthCountText, Optional.ofNullable(lastClassifyResult).map(AssetClassifyResultEnum::display).orElse(""), Optional.ofNullable(classifyResult).map(AssetClassifyResultEnum::display).orElse(""), provisionText, null, null, null};
        return Rows.of(rowData).center().create();
    }

    private long calRemainingMonth(List<ContractRentActual> rentList) {
        LocalDate now = LocalDate.now();
        rentList.removeIf(e -> e.getCashFlowDate().isBefore(now));
        rentList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate));
        long remainingMonthCount = 0;
        if (CollectionUtil.isNotEmpty(rentList)) {
            LocalDate last = rentList.get(rentList.size() - 1).getCashFlowDate();
            remainingMonthCount = LocalDateTimeUtil.between(LocalDate.of(now.getYear(), now.getMonthValue(), 1).atStartOfDay(), LocalDate.of(last.getYear(), last.getMonthValue(), last.lengthOfMonth()).atStartOfDay(), ChronoUnit.MONTHS);
        }
        return remainingMonthCount;
    }
}
