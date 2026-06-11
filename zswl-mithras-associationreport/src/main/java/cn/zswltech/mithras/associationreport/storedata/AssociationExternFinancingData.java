package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.associationreport.AssociationReportException;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationExternalFinancingService;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.capital.enums.FinancePaymentWriteOffOrderEnum;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.*;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingProductDetailMapper;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingRepayActualMapper;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingRepayActualSplitMapper;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingRepayActualSplitRecordMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationExternalFinancing;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingPlanMapper;
import cn.zswltech.mithras.fund.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2025/4/18
 * @description 对外融资清单
 */
@Slf4j
@Component
public class AssociationExternFinancingData extends AbstractDataStore<AssociationExternalFinancing> {
    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        // 无需前置数据
        return true;
    }

    @Override
    protected List<AssociationExternalFinancing> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【对外融资清单】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<对外融资清单>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司对外融资清单（季报）");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<对外融资清单>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationExternalFinancing> list = new LinkedList<>();
        for (int i = 3; i < rows.size() - 1; i++) {
            List<Object> row = rows.get(i);
            if (row.get(0).toString().contains("备注：")) {
                break;
            }
            try {
                list.add(this.convert(i -2, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【对外融资清单】-第{}行数据处理异常", (i-2), e);
                throw new MithrasException("数据处理异常");
            }
        }
        return list;
    }

    @Override
    protected List<AssociationExternalFinancing> parseFromSystemData(AssociationReport associationReport) {
        List<AssociationExternalFinancing> result = new LinkedList<>();
        // 确定数据截止日期
        LocalDate targetDate = this.ensureMetricDate(associationReport);
        // 间接融资
        List<AssociationExternalFinancing> indirectList = this.indirect(targetDate);
        if (CollectionUtil.isNotEmpty(indirectList)) {
            result.addAll(indirectList);
        }
        // 直接融资
        List<AssociationExternalFinancing> directList = this.direct(targetDate);
        if (CollectionUtil.isNotEmpty(directList)) {
            result.addAll(directList);
        }
        return result;
    }

    @Override
    protected void check(List<AssociationExternalFinancing> dataList) {
        List<String> errorList = new ArrayList<>();
        dataList.forEach(e -> {
            if (ObjectUtil.isEmpty(e.getOnum())) {
                errorList.add("A列：序号，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getLoanBal())) {
                errorList.add("B列：借款余额，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinBusiTypeCode()) || Objects.equals(e.getFinBusiTypeCode(), DICT_UNKNOWN_CODE)) {
                errorList.add("C列：融资业务类型，必填字段,取值字典范围");
            }
            if (ObjectUtil.isEmpty(e.getCptlProv())) {
                errorList.add("D列：资金提供方，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinIntr())) {
                errorList.add("E列：融资利率，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinLoanDate())) {
                errorList.add("F列：借款日期，必填字段");
            }
            if (ObjectUtil.isEmpty(e.getFinMatuDate())) {
                errorList.add("G列：到期日，必填字段");
            }
        });
        if (CollectionUtil.isNotEmpty(errorList)) {
            throw new AssociationReportException(errorList);
        }
    }

    @Override
    protected IService<AssociationExternalFinancing> serviceBean() {
        return SpringUtil.getBean(AssociationExternalFinancingService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0010;
    }

    private AssociationExternalFinancing convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationExternalFinancing bean = new AssociationExternalFinancing();
        bean.setRowNum(rowNum);
        bean.setOp("insert");
        bean.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        bean.setLoanBal(parseBigDecimal((row.get(1))));
        //融资类型
        Map<String, String> contractTypeMap = dictNameMap.get(AssociationDictionaryCategoryEnum.EVT00052.name());
        if (ObjectUtil.isNotEmpty(contractTypeMap)) {
            bean.setFinBusiTypeCode(contractTypeMap.get(Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null)));
        } else {
            bean.setFinBusiTypeCode(DICT_UNKNOWN_CODE);
        }
        bean.setCptlProv(Optional.ofNullable(row.get(3)).map(Object::toString).orElse(null));
        bean.setFinIntr(parseBigDecimal(row.get(4)));
        bean.setFinLoanDate(parseDateTime(row.get(5)));
        bean.setFinMatuDate(parseDateTime(row.get(6)));
        return bean;
    }

    private LocalDate parseDateTime(Object o) {
        if (ObjectUtil.isEmpty(o)) {
            return null;
        }
        return DateUtil.parse(o.toString()).toLocalDateTime().toLocalDate();
    }

    private List<AssociationExternalFinancing> indirect(LocalDate targetDate) {
        Map<String, String> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap().get(AssociationDictionaryCategoryEnum.EVT00052.name());
        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.in(FundFinancingBaseInfo::getFinancingStatus, ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
        query.le(FundFinancingBaseInfo::getActualLoanDate, targetDate);
        List<FundFinancingBaseInfo> todoList = SpringUtil.getBean(FundFinancingBaseInfoMapper.class).selectList(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return Collections.emptyList();
        }
        List<AssociationExternalFinancing> result = new LinkedList<>();
        for (FundFinancingBaseInfo fundFinancingBaseInfo : todoList) {
            try {
                AssociationExternalFinancing currentReportValue = this.buildFromIndirect(dictNameMap, fundFinancingBaseInfo, targetDate);
                if (Objects.nonNull(currentReportValue)) {
                    result.add(currentReportValue);
                }
            } catch (Exception e) {
                log.error("金融局报送【对外融资清单】间接融资自动取值发生异常[{}]", JSONUtil.toJsonStr(fundFinancingBaseInfo), e);
            }
        }
        return result;
    }

    private AssociationExternalFinancing buildFromIndirect(Map<String, String> dictNameMap, FundFinancingBaseInfo fundFinancingBaseInfo, LocalDate targetDate) {
        AssociationExternalFinancing instance = new AssociationExternalFinancing();
        FundFinancingPlan fundFinancingPlan = SpringUtil.getBean(FundFinancingPlanMapper.class).selectOne(
                Wrappers.<FundFinancingPlan>lambdaQuery()
                        .eq(FundFinancingPlan::getFinancingId, fundFinancingBaseInfo.getId())
                        .last("limit 1")
        );
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoMapper.class).selectOne(
                Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                        .eq(FundReceiptRepayBaseInfo::getFinancingId, fundFinancingBaseInfo.getId())
                        .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                        .last("limit 1")
        );
        if (Objects.isNull(fundFinancingPlan) || Objects.isNull(fundReceiptRepayBaseInfo)) {
            return null;
        }
        List<FundReceiptFlowDetail> flowDetailList = SpringUtil.getBean(FundReceiptFlowDetailMapper.class).selectList(
                Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .eq(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfo.getId())
        );
        long repayPrincipal = flowDetailList.stream()
                .filter(e -> StrUtil.equals(e.getCashFlowItem(), FinanceCashFlowItemEnum.REPAY.name()))
                .filter(e -> !e.getCashFlowDate().isAfter(targetDate))
                .filter(e -> Objects.nonNull(e.getPrincipalAmount()))
                .mapToLong(FundReceiptFlowDetail::getPrincipalAmount)
                .sum();
        long remainingPrincipal = fundFinancingPlan.getFinancingAmount() - repayPrincipal;
        if (remainingPrincipal <= 0) {
            return null;
        }
        // 未还本金之和
        instance.setLoanBal(Util.millimeterLong2WanBigDecimal(remainingPrincipal));
        // 融资业务类型
        if (StrUtil.equalsAny(fundFinancingBaseInfo.getBusinessType(), FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(),  FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name())) {
            instance.setFinBusiTypeCode(dictNameMap.get("其他"));
        } else {
            if (StrUtil.equals(fundFinancingBaseInfo.getTimeLimitType(), FundFinancingTimeLimitTypeEnum.LONG_TERM_LOAN.name())) {
                instance.setFinBusiTypeCode(dictNameMap.get("长期银行贷款"));
            } else {
                instance.setFinBusiTypeCode(dictNameMap.get("短期银行贷款"));
            }
        }
        // 资金提供方
        List<FundFinancingCreditRef> relationList = SpringUtil.getBean(FundFinancingCreditRefService.class).queryByFinancingId(fundFinancingBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(relationList)) {
            List<FundOrganization> orgList = SpringUtil.getBean(FundOrganizationService.class).listByIds(relationList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()));
            instance.setCptlProv(Optional.ofNullable(orgList).map(e -> e.get(0).getAbbreviation()).orElse(null));
        }
        // 融资利率
        int interestRate = Optional.ofNullable(fundFinancingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(fundFinancingPlan.getLprAddPercent()).orElse(0);
        instance.setFinIntr(BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP));
        // 借款日期
        instance.setFinLoanDate(fundFinancingBaseInfo.getActualLoanDate());
        // 到期日期
        instance.setFinMatuDate(fundFinancingBaseInfo.getActualExpireDate());
        return instance;
    }

    private List<AssociationExternalFinancing> direct(LocalDate targetDate) {
        Map<String, String> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap().get(AssociationDictionaryCategoryEnum.EVT00052.name());
        LambdaQueryWrapper<FundDirectFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingBaseInfo::getFinancingStatus, ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
        query.le(FundDirectFinancingBaseInfo::getCarryInterestTime, targetDate);
        List<FundDirectFinancingBaseInfo> todoList = SpringUtil.getBean(FundDirectFinancingBaseInfoMapper.class).selectList(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return Collections.emptyList();
        }
        List<AssociationExternalFinancing> result = new LinkedList<>();
        for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : todoList) {
            // 直融需要拆到产品明细层面
            List<FundDirectFinancingProductDetail> productDetailList = SpringUtil.getBean(FundDirectFinancingProductDetailMapper.class).selectList(
                    Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                            .eq(FundDirectFinancingProductDetail::getFinancingId, fundDirectFinancingBaseInfo.getId())
                            .orderByAsc(FundDirectFinancingProductDetail::getId)
            );
            if (CollectionUtil.isEmpty(productDetailList)) {
                continue;
            }
            for (FundDirectFinancingProductDetail productDetail : productDetailList) {
                try {
                    AssociationExternalFinancing currentReportValue = this.buildFromDirect(dictNameMap, fundDirectFinancingBaseInfo, productDetail, targetDate);
                    if (Objects.nonNull(currentReportValue)) {
                        result.add(currentReportValue);
                    }
                } catch (Exception e) {
                    log.error("金融局报送【对外融资清单】直接融资自动取值发生异常[{}]", JSONUtil.toJsonStr(productDetail), e);
                }
            }
        }
        return result;
    }

    private AssociationExternalFinancing buildFromDirect(Map<String, String> dictNameMap, FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, FundDirectFinancingProductDetail productDetail, LocalDate targetDate) {
        AssociationExternalFinancing instance = new AssociationExternalFinancing();
        // 资金提供方取承销商
        instance.setCptlProv(fundDirectFinancingBaseInfo.getConsignee());
        // 融资利率
        long interestRate = Optional.ofNullable(productDetail.getIssuanceRate()).orElse(0L);
        instance.setFinIntr(BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP));
        // 借款日期
        instance.setFinLoanDate(fundDirectFinancingBaseInfo.getCarryInterestTime());
        // 需要分类型处理的取值逻辑
        List<FundDirectFinancingRepayActual> repayActualList = SpringUtil.getBean(FundDirectFinancingRepayActualMapper.class).selectList(
                Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                        .eq(FundDirectFinancingRepayActual::getFinancingId, fundDirectFinancingBaseInfo.getId())
                        .orderByAsc(FundDirectFinancingRepayActual::getRepayDate)
                        .orderByAsc(FundDirectFinancingRepayActual::getPhase)
        );
        if (StrUtil.equalsAny(fundDirectFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name())) {
            // 找到计划还款日期在目标日期之前的还款计划（因为拆分后的核销明细不带日期，所以只能这样去筛选）
            repayActualList.removeIf(e -> e.getRepayDate().isAfter(targetDate));
            Set<String> targetCashFlowCode = repayActualList.stream().map(FundDirectFinancingRepayActual::getCashFlowCode).collect(Collectors.toSet());
            long repayPrincipal = 0L;
            if (CollectionUtil.isNotEmpty(targetCashFlowCode)) {
                List<FundDirectFinancingRepayActualSplitRecord> repayActualSplitRecordList = SpringUtil.getBean(FundDirectFinancingRepayActualSplitRecordMapper.class).selectList(
                        Wrappers.<FundDirectFinancingRepayActualSplitRecord>lambdaQuery()
                                .eq(FundDirectFinancingRepayActualSplitRecord::getProductDetailId, productDetail.getId())
                                .eq(FundDirectFinancingRepayActualSplitRecord::getCashFlowItem, FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())
                );
                // 特殊逻辑，因为拆分表是后期加的，已经结清的融资不补历史数据，所以拆分表没有数据且融资状态已经结清的特殊处理
                if (CollectionUtil.isEmpty(repayActualSplitRecordList) && StrUtil.equals(fundDirectFinancingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.SETTLE.name())) {
                    repayPrincipal = Optional.ofNullable(productDetail.getIssuanceAmount()).orElse(0L) * 10000;
                } else {
                    repayPrincipal = repayActualSplitRecordList.stream()
                            .filter(e -> StrUtil.equals(e.getCashFlowItem(), "PRINCIPAL"))
                            .filter(e -> targetCashFlowCode.contains(e.getCashFlowCodeParent()))
                            .filter(e -> Objects.nonNull(e.getWriteOffAmount()))
                            .mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount)
                            .sum();
                }
            }
            long remainingPrincipal = Optional.ofNullable(productDetail.getIssuanceAmount()).orElse(0L) * 10000 - repayPrincipal;
            if (remainingPrincipal <= 0) {
                return null;
            }
            // 未还本金之和
            instance.setLoanBal(Util.millimeterLong2WanBigDecimal(remainingPrincipal));
            // 到期日期
            List<FundDirectFinancingRepayActualSplit> repayActualSplitList = SpringUtil.getBean(FundDirectFinancingRepayActualSplitMapper.class).selectList(
                    Wrappers.<FundDirectFinancingRepayActualSplit>lambdaQuery()
                            .eq(FundDirectFinancingRepayActualSplit::getProductDetailId, productDetail.getId())
            );
            repayActualSplitList.removeIf(e -> Objects.isNull(e.getRemainingPrincipalAmount()) || e.getRemainingPrincipalAmount() == 0);
//            if (CollectionUtil.isNotEmpty(repayActualSplitList)) {
//                repayActualSplitList.sort(Comparator.comparing(FundDirectFinancingRepayActualSplit::getRepayDate).reversed());
//                instance.setFinMatuDate(repayActualSplitList.get(0).getRepayDate());
//            }
            if (Objects.nonNull(productDetail.getExpectedExpirationDate())) {
                instance.setFinMatuDate(productDetail.getExpectedExpirationDate());
            }
            // 融资业务类型
            instance.setFinBusiTypeCode(dictNameMap.get("资产证券化融资"));
        } else {
            // 寻找核销明细
            List<FundReceiptFlowDetail> detailList = this.getFinancingRepayDetail(fundDirectFinancingBaseInfo.getId(), FinancingTypeEnum.DIRECT.name());
            // 取到目标日期为止，后面的不要
            detailList.removeIf(e -> e.getCashFlowDate().isAfter(targetDate));
            long repayPrincipal = 0L;
            if (CollectionUtil.isNotEmpty(detailList)) {
                repayPrincipal = detailList.stream()
                        .filter(e -> StrUtil.equals(e.getCashFlowItem(), FinanceCashFlowItemEnum.REPAY.name()))
                        .filter(e -> Objects.nonNull(e.getPrincipalAmount()))
                        .mapToLong(FundReceiptFlowDetail::getPrincipalAmount)
                        .sum();
            }
            long remainingPrincipal = Optional.ofNullable(fundDirectFinancingBaseInfo.getFinancingAmount()).orElse(0L) * 10000 - repayPrincipal;
            if (remainingPrincipal <= 0) {
                return null;
            }
            // 未还本金之和
            instance.setLoanBal(Util.millimeterLong2WanBigDecimal(remainingPrincipal));
            // 到期日期
            if (CollectionUtil.isNotEmpty(repayActualList)) {
                repayActualList.sort(Comparator.comparing(FundDirectFinancingRepayActual::getRepayDate).reversed());
                instance.setFinMatuDate(repayActualList.get(0).getRepayDate());
            }
            // 融资业务类型
            instance.setFinBusiTypeCode(dictNameMap.get("债券融资"));
        }
        return instance;
    }

    private List<FundReceiptFlowDetail> getFinancingRepayDetail(Long financingId, String financingType) {
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoMapper.class).selectOne(
                Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                        .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                        .eq(FinancingTypeEnum.DIRECT.name().equals(financingType), FundReceiptRepayBaseInfo::getFinancingType, financingType)
                        .isNull(!FinancingTypeEnum.DIRECT.name().equals(financingType), FundReceiptRepayBaseInfo::getFinancingType)
                        .last("limit 1")
        );
        if (ObjectUtil.isEmpty(fundReceiptRepayBaseInfo)) {
            return ListUtil.empty();
        }
        return SpringUtil.getBean(FundReceiptFlowDetailMapper.class).selectList(
                Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .eq(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfo.getId())
                        .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())
        );
    }
}
