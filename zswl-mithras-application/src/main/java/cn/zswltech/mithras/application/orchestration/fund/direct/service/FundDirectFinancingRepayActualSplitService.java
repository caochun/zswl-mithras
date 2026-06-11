package cn.zswltech.mithras.application.orchestration.fund.direct.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.dto.capital.FinanceRepaySplitREQ;
import cn.zswltech.mithras.dto.capital.FinanceRepaySplitRSP;
import cn.zswltech.mithras.dto.capital.FinanceRepaySplitRecordREQ;
import cn.zswltech.mithras.dto.capital.FinanceRepaySplitRecordRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectRepayActualSplitRSP;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.FinancePaymentWriteOffOrderEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.directfinancing.model.*;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingRepayActualSplitMapper;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/3/28
 * @description
 */
@Slf4j
@Service
public class FundDirectFinancingRepayActualSplitService extends ServiceImpl<FundDirectFinancingRepayActualSplitMapper, FundDirectFinancingRepayActualSplit> {
    @Resource
    private FundDirectFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingProductDetailService productDetailService;
    @Resource
    private FundDirectFinancingRepayActualService repayActualService;
    @Resource
    private FundReceiptRepayCashFlowService receiptRepayCashFlowService;
    @Resource
    private FundDirectFinancingRepayActualSplitRecordService repayActualSplitRecordService;

    public List<FundDirectFinancingRepayActualSplit> listByProductDetailId(Long productDetailId) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplit> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingRepayActualSplit::getProductDetailId, productDetailId);
        return this.list(query);
    }

    public List<FundDirectFinancingRepayActualSplit> listByParentCashFlowCodes(Collection<String> parentCashFlowCodes) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplit> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingRepayActualSplit::getCashFlowCodeParent, parentCashFlowCodes);
        return this.list(query);
    }

    public List<FundDirectFinancingRepayActualSplit> listByCashFlowCodes(Collection<String> cashFlowCodes) {
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplit> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingRepayActualSplit::getCashFlowCode, cashFlowCodes);
        return this.list(query);
    }

    public List<FundDirectRepayActualSplitRSP> listSplitRspByFinancingId(Long financingId) {
        // 找产品
        List<FundDirectFinancingProductDetail> productDetailList = productDetailService.listByFinancingId(financingId);
        if (CollectionUtil.isEmpty(productDetailList)) {
            return Collections.emptyList();
        }
        // 找拆分明细
        LambdaQueryWrapper<FundDirectFinancingRepayActualSplit> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingRepayActualSplit::getFinancingId, financingId);
        // 仅展示应还总额大于0的数据
        query.gt(FundDirectFinancingRepayActualSplit::getRepayAmount, 0L);
        query.orderByAsc(FundDirectFinancingRepayActualSplit::getProductDetailId);
        query.orderByAsc(FundDirectFinancingRepayActualSplit::getPhase);
        query.orderByAsc(FundDirectFinancingRepayActualSplit::getRepayDate);
        List<FundDirectFinancingRepayActualSplit> splitList = this.list(query);
        if (CollectionUtil.isEmpty(splitList)) {
            return Collections.emptyList();
        }
        Map<Long, List<FundDirectFinancingRepayActualSplit>> splitMap = splitList.stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActualSplit::getProductDetailId));
        // 分组组装
        List<FundDirectRepayActualSplitRSP> result = new ArrayList<>(productDetailList.size());
        for (FundDirectFinancingProductDetail productDetail : productDetailList) {
            List<FundDirectFinancingRepayActualSplit> repayActualSplitList = splitMap.get(productDetail.getId());
            if (CollectionUtil.isEmpty(repayActualSplitList)) {
                continue;
            }
            List<FundDirectRepayActualSplitRSP.RepayData> repayDataList = repayActualSplitList.stream().map(e -> BeanUtil.copyProperties(e, FundDirectRepayActualSplitRSP.RepayData.class)).collect(Collectors.toList());
            FundDirectRepayActualSplitRSP rsp = new FundDirectRepayActualSplitRSP();
            rsp.setSecuritiesCode(productDetail.getSecuritiesCode());
            rsp.setAbbreviation(productDetail.getAbbreviation());
            rsp.setCashFlowList(repayDataList);
            result.add(rsp);
        }
        return result;
    }

    public List<FinanceRepaySplitRSP> listRepaySplitRsp(FinanceRepaySplitREQ req) {
        // 查询拆分明细
        Set<String> cashFlowCodes = req.getTargetCashFlow().stream().map(FinanceRepaySplitREQ.Data::getCashFlowCode).collect(Collectors.toSet());
        List<FundReceiptRepayCashFlow> receiptRepayCashFlowList = receiptRepayCashFlowService.list(
                Wrappers.<FundReceiptRepayCashFlow>lambdaQuery().in(FundReceiptRepayCashFlow::getCashFlowCode, cashFlowCodes)
        );
        if (CollectionUtil.isEmpty(receiptRepayCashFlowList)) {
            return Collections.emptyList();
        }
        List<FundDirectFinancingRepayActualSplit> repayActualSplitList = this.list(
                Wrappers.<FundDirectFinancingRepayActualSplit>lambdaQuery().in(FundDirectFinancingRepayActualSplit::getCashFlowCodeParent, cashFlowCodes).gt(FundDirectFinancingRepayActualSplit::getRepayAmount, 0L)
        );
        if (CollectionUtil.isEmpty(repayActualSplitList)) {
            return Collections.emptyList();
        }
        Set<Long> financingIds = receiptRepayCashFlowList.stream().map(FundReceiptRepayCashFlow::getFinancingId).collect(Collectors.toSet());
        List<FundDirectFinancingBaseInfo> baseInfoList = financingBaseInfoService.listByIds(financingIds);
        Map<Long, FundDirectFinancingBaseInfo> baseInfoMap = baseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, e -> e));
        // 查询一下产品明细
        Set<Long> productDetailIds = repayActualSplitList.stream().map(FundDirectFinancingRepayActualSplit::getProductDetailId).collect(Collectors.toSet());
        List<FundDirectFinancingProductDetail> productDetailList = productDetailService.listByIds(productDetailIds);
        Map<Long, FundDirectFinancingProductDetail> productDetailMap = productDetailList.stream().collect(Collectors.toMap(FundDirectFinancingProductDetail::getId, e -> e));
        // 根据请求参数决定现金流的类型后组装返回
        List<FinanceRepaySplitRSP> rspList = new LinkedList<>();
        for (FundDirectFinancingRepayActualSplit repayActualSplit : repayActualSplitList) {
            FundDirectFinancingBaseInfo baseInfo = baseInfoMap.get(repayActualSplit.getFinancingId());
            FundDirectFinancingProductDetail productDetail = productDetailMap.get(repayActualSplit.getProductDetailId());
            for (FinanceRepaySplitREQ.Data reqData : req.getTargetCashFlow()) {
                if (Objects.equals(repayActualSplit.getCashFlowCodeParent(), reqData.getCashFlowCode())) {
                    FinanceRepaySplitRSP rsp = new FinanceRepaySplitRSP();
                    rsp.setUniqKey(repayActualSplit.getId() + "-" + reqData.getCashFlowItem());
                    rsp.setSplitId(repayActualSplit.getId());
                    rsp.setFinancingId(repayActualSplit.getFinancingId());
                    rsp.setFinancingCode(Optional.ofNullable(baseInfo).map(FundDirectFinancingBaseInfo::getFinancingCode).orElse(null));
                    rsp.setSecuritiesCode(Optional.ofNullable(productDetail).map(FundDirectFinancingProductDetail::getSecuritiesCode).orElse(null));
                    rsp.setAbbreviation(Optional.ofNullable(productDetail).map(FundDirectFinancingProductDetail::getAbbreviation).orElse(null));
                    rsp.setCashFlowCode(repayActualSplit.getCashFlowCodeParent());
                    rsp.setCashFlowItem(reqData.getCashFlowItem());
                    rsp.setSplitCashFlowCode(repayActualSplit.getCashFlowCode());
                    rsp.setPhase(repayActualSplit.getPhase());
                    rsp.setPlanRepayDate(repayActualSplit.getRepayDate());
                    // 查询核销记录计算未还金额
                    List<FundDirectFinancingRepayActualSplitRecord> writeOffRecordList = repayActualSplitRecordService.listByCashFlowCode(repayActualSplit.getCashFlowCode());
                    if (Objects.equals(reqData.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name())) {
                        rsp.setPlanRepayAmount(repayActualSplit.getPrincipalAmount());
                        long actualRepay = writeOffRecordList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), reqData.getCashFlowItem())).mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).sum();
                        rsp.setRemainingAmount(repayActualSplit.getPrincipalAmount() - actualRepay);
                    }
                    if (Objects.equals(reqData.getCashFlowItem(), FinancePaymentWriteOffOrderEnum.INTEREST.name())) {
                        rsp.setPlanRepayAmount(repayActualSplit.getInterestAmount());
                        long actualRepay = writeOffRecordList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), reqData.getCashFlowItem())).mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).sum();
                        rsp.setRemainingAmount(repayActualSplit.getInterestAmount() - actualRepay);
                    }
                    rspList.add(rsp);
                }
            }
        }
        return rspList;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void trySplit(Long financingId) {
        FundDirectFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        if (Objects.isNull(financingBaseInfo)) {
            throw new MithrasException("融资数据不存在");
        }
        if (Objects.isNull(financingBaseInfo.getCarryInterestTime())) {
            throw new MithrasException("请先维护起息日");
        }
        // 查询产品明细和实际还款计划
        List<FundDirectFinancingProductDetail> productDetailList = productDetailService.listByFinancingId(financingId);
        List<FundDirectFinancingRepayActual> repayActualList = repayActualService.listByFinancingId(financingId);
        if (CollectionUtil.isEmpty(productDetailList)) {
            throw new MithrasException("请先维护产品明细");
        }
        if (CollectionUtil.isEmpty(repayActualList)) {
            throw new MithrasException("请先导入实际还款计划");
        }
        productDetailList.sort(Comparator.comparing(FundDirectFinancingProductDetail::getId));
        repayActualList.sort(Comparator.comparing(FundDirectFinancingRepayActual::getPhase));
        // 初始化一些存放数据的变量
        Map<Integer, MyData> toolMap = new HashMap<>();
        Map<Integer, List<FundDirectFinancingRepayActualSplit>> todoMap = new LinkedHashMap<>();
        for (int i = 0; i < productDetailList.size(); i++) {
            FundDirectFinancingProductDetail productDetail = productDetailList.get(i);
            todoMap.put(i + 1, new LinkedList<>());
            MyData myData = new MyData();
            // 扩大10000是为了和实际还款计划的单位一致
            myData.setBizDate(financingBaseInfo.getCarryInterestTime());
            myData.setRemainingPrincipal(Optional.ofNullable(productDetail.getIssuanceAmount()).orElse(0L) * 10000);
            toolMap.put(i + 1, myData);
        }
        // 拆分实际还款计划
        for (FundDirectFinancingRepayActual repayActual : repayActualList) {
            long principal = Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L);
            for (int i = 0; i < productDetailList.size(); i++) {
                FundDirectFinancingProductDetail productDetail = productDetailList.get(i);
                // 计算当前产品当前期项的相关金额
                long productRemainingPrincipal = toolMap.get(i + 1).getRemainingPrincipal();
                long productPrincipal = Math.min(principal, productRemainingPrincipal);
                // 利息计算方式 = 上一期的剩余本金*产品发行利率/360*间隔天数
                BigDecimal interestRate = BigDecimal.valueOf(productDetail.getIssuanceRate()).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP);
                long days = LocalDateTimeUtil.between(toolMap.get(i + 1).getBizDate().atStartOfDay(), repayActual.getRepayDate().atStartOfDay(), ChronoUnit.DAYS);
                long productInterest = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(productRemainingPrincipal).multiply(interestRate).multiply(BigDecimal.valueOf(days)).longValue());
                // 存入待保存
                todoMap.get(i + 1).add(this.convert(i + 1, productDetail, repayActual, productPrincipal, productInterest, productRemainingPrincipal - productPrincipal));
                // 更新工具类后续使用
                toolMap.get(i + 1).setRemainingPrincipal(productRemainingPrincipal - productPrincipal);
                toolMap.get(i + 1).setBizDate(repayActual.getRepayDate());
                // 更新当前实际还款计划当前期项剩余可分配金额
                principal = principal - productPrincipal;
            }
        }
        // 保存拆分后的结果
        List<FundDirectFinancingRepayActualSplit> insertList = new LinkedList<>();
        for (Map.Entry<Integer, List<FundDirectFinancingRepayActualSplit>> entry : todoMap.entrySet()) {
            insertList.addAll(entry.getValue());
        }
        if (CollectionUtil.isNotEmpty(insertList)) {
            // 查询老的状态不是待核销的数据，需要把状态同步到新数据中
            List<FundDirectFinancingRepayActualSplit> oldList = this.list(
                    Wrappers.<FundDirectFinancingRepayActualSplit>lambdaQuery().eq(FundDirectFinancingRepayActualSplit::getFinancingId, financingId).ne(FundDirectFinancingRepayActualSplit::getWriteOffStatus, CashFlowState.NO_WRITE_OFF.name())
            );
            if (CollectionUtil.isNotEmpty(oldList)) {
                Map<String, String> oldMap = oldList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActualSplit::getCashFlowCode, FundDirectFinancingRepayActualSplit::getWriteOffStatus));
                insertList.forEach(e -> {
                    if (StrUtil.isNotBlank(oldMap.get(e.getCashFlowCode()))) {
                        e.setWriteOffStatus(oldMap.get(e.getCashFlowCode()));
                    }
                });
            }
            // 删除老的
            this.remove(Wrappers.<FundDirectFinancingRepayActualSplit>lambdaQuery().eq(FundDirectFinancingRepayActualSplit::getFinancingId, financingId));
            // 保存新的
            this.saveBatch(insertList);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void initHistoryData() {
        ExcelReader excelReader = ExcelUtil.getReader("/Users/dingqi/Downloads/abs还款计划.xlsx");
        List<String> sheetNames = excelReader.getSheetNames();
        for (String sheetName : sheetNames) {
            // Excel已经手工把sheetName修改“产品明细id-序列号”的格式了
            String[] array = sheetName.split("-");
            Long productDetailId = Long.parseLong(array[0]);
            Integer sequence = Integer.parseInt(array[1]);
            FundDirectFinancingProductDetail productDetail = productDetailService.getById(productDetailId);
            FundDirectFinancingBaseInfo baseInfo = financingBaseInfoService.getById(productDetail.getFinancingId());
            List<FundDirectFinancingRepayActual> repayActualList = repayActualService.listByFinancingId(productDetail.getFinancingId());
            List<List<Object>> rowList = excelReader.setSheet(sheetName).read(2);
            LocalDate lastRepayDate = baseInfo.getCarryInterestTime();
            long remainingPrincipal = productDetail.getIssuanceAmount() * 10000;
            int phase = 1;
            for (List<Object> row : rowList) {
                Object repayDateOrigin = row.get(23);
                Object principalOrigin = row.get(24);
                Object interestOrigin = row.get(25);
                if (Objects.isNull(repayDateOrigin) || StrUtil.isBlank(repayDateOrigin.toString())) {
                    // 说明没有数据了
                    break;
                }
                LocalDate repayDate = Optional.ofNullable(repayDateOrigin).map(e -> LocalDateTimeUtil.parseDate(e.toString().substring(0,10), DatePattern.NORM_DATE_PATTERN)).orElse(null);
                Long principal = Optional.ofNullable(principalOrigin).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                Long interest = Optional.ofNullable(interestOrigin).map(e -> Util.mithrasLongDecimalTwo(new BigDecimal(e.toString()).multiply(BigDecimal.valueOf(10000)).longValue())).orElse(0L);
                if (repayDate.isAfter(LocalDate.now())) {
                    // 需要自己算利息
                    long days = LocalDateTimeUtil.between(lastRepayDate.atStartOfDay(), repayDate.atStartOfDay(), ChronoUnit.DAYS);
                    BigDecimal interestRate = BigDecimal.valueOf(productDetail.getIssuanceRate()).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP);
                    interest = Util.mithrasLongDecimalTwo(BigDecimal.valueOf(remainingPrincipal).multiply(interestRate).multiply(BigDecimal.valueOf(days)).longValue());
                }
                // 找父的实际还款计划现金流
                int finalPhase = phase;
                FundDirectFinancingRepayActual repayActual = repayActualList.stream().filter(e -> e.getPhase() == finalPhase).findFirst().orElse(null);
                if (Objects.isNull(repayActual)) {
                    log.error("没有找到对应的实际还款计划[sheetName:{}, repayDate:{}]", sheetName, LocalDateTimeUtil.format(repayDate, DatePattern.NORM_DATE_PATTERN));
                    continue;
                }
                // 保存
                FundDirectFinancingRepayActualSplit repayActualSplit = new FundDirectFinancingRepayActualSplit();
                repayActualSplit.setFinancingId(productDetail.getFinancingId());
                repayActualSplit.setPhase(phase);
                repayActualSplit.setSequence(sequence);
                repayActualSplit.setProductDetailId(productDetail.getId());
                repayActualSplit.setRepayActualId(repayActual.getId());
                repayActualSplit.setCashFlowCodeParent(repayActual.getCashFlowCode());
                repayActualSplit.setCashFlowCode(repayActual.getCashFlowCode() + String.format("-%03d", sequence));
                repayActualSplit.setRepayDate(repayDate);
                repayActualSplit.setPrincipalAmount(principal);
                repayActualSplit.setInterestAmount(interest);
                repayActualSplit.setRemainingPrincipalAmount(remainingPrincipal - principal);
                repayActualSplit.setRepayAmount(principal + interest);
                repayActualSplit.setWriteOffStatus(repayDate.isBefore(LocalDate.now()) ? CashFlowState.WRITTEN_OFF.name() : CashFlowState.NO_WRITE_OFF.name());
                this.save(repayActualSplit);
                if (Objects.equals(repayActualSplit.getWriteOffStatus(), CashFlowState.WRITTEN_OFF.name())) {
                    // 添加核销记录
                    if (principal > 0) {
                        FundDirectFinancingRepayActualSplitRecord record = new FundDirectFinancingRepayActualSplitRecord();
                        record.setFinancingId(productDetail.getFinancingId());
                        record.setProductDetailId(productDetail.getId());
                        record.setCashFlowItem(FinancePaymentWriteOffOrderEnum.PRINCIPAL.name());
                        record.setWriteOffAmount(principal);
                        record.setCashFlowCode(repayActualSplit.getCashFlowCode());
                        record.setCashFlowCodeParent(repayActualSplit.getCashFlowCodeParent());
                        SpringUtil.getBean(FundDirectFinancingRepayActualSplitRecordService.class).save(record);
                    }
                    if (interest > 0) {
                        FundDirectFinancingRepayActualSplitRecord record = new FundDirectFinancingRepayActualSplitRecord();
                        record.setFinancingId(productDetail.getFinancingId());
                        record.setProductDetailId(productDetail.getId());
                        record.setCashFlowItem(FinancePaymentWriteOffOrderEnum.INTEREST.name());
                        record.setWriteOffAmount(interest);
                        record.setCashFlowCode(repayActualSplit.getCashFlowCode());
                        record.setCashFlowCodeParent(repayActualSplit.getCashFlowCodeParent());
                        SpringUtil.getBean(FundDirectFinancingRepayActualSplitRecordService.class).save(record);
                    }
                }
                // 修改中间变量
                remainingPrincipal = remainingPrincipal - principal;
                phase++;
                lastRepayDate = repayDate;
            }
        }
    }

    private FundDirectFinancingRepayActualSplit convert(int sequence, FundDirectFinancingProductDetail productDetail, FundDirectFinancingRepayActual repayActual, long principal, long interest, long remainingPrincipal) {
        FundDirectFinancingRepayActualSplit repayActualSplit = new FundDirectFinancingRepayActualSplit();
        repayActualSplit.setFinancingId(productDetail.getFinancingId());
        repayActualSplit.setProductDetailId(productDetail.getId());
        repayActualSplit.setRepayActualId(repayActual.getId());
        repayActualSplit.setCashFlowCodeParent(repayActual.getCashFlowCode());
        repayActualSplit.setCashFlowCode(repayActual.getCashFlowCode() + String.format("-%03d", sequence));
        repayActualSplit.setSequence(sequence);
        repayActualSplit.setPhase(repayActual.getPhase());
        repayActualSplit.setRepayDate(repayActual.getRepayDate());
        repayActualSplit.setPrincipalAmount(principal);
        repayActualSplit.setInterestAmount(interest);
        repayActualSplit.setRemainingPrincipalAmount(remainingPrincipal);
        repayActualSplit.setRepayAmount(principal + interest);
        repayActualSplit.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());
        return repayActualSplit;
    }

    @Data
    private static class MyData {
        private long remainingPrincipal = 0L;
        private LocalDate bizDate;
    }
}
