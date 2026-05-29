package cn.zswltech.mithras.service.service.fund.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayActualListRSP;
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingChangeSubTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingSceneEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.enums.payment.WriteOffTypeEnum;
import cn.zswltech.mithras.service.excel.exporter.FundFinancingRepayActualExporter;
import cn.zswltech.mithras.service.excel.importer.FundFinancingRepayImporter;
import cn.zswltech.mithras.service.excel.model.FundFinancingRepayActualExcelModel;
import cn.zswltech.mithras.service.excel.model.FundFinancingRepayEstimateExcelModel;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingRepayActualMapper;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActualLib;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.fund.financing.fms.FundFinancingBaseInfoStateMachine;
import cn.zswltech.mithras.service.service.fund.financing.fms.FundFinancingContext;
import cn.zswltech.mithras.service.service.fund.financing.fms.FundFinancingEvent;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.service.lib.fund.financing.FundFinancingRepayActualLibService;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl.FundFinancingRepayActualLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FundFinancingRepayActualService extends ServiceImpl<FundFinancingRepayActualMapper, FundFinancingRepayActual> {
    @Resource
    private FundFinancingRepayImporter financingRepayImporter;
    @Resource
    private FundFinancingRepayActualExporter financingRepayActualExporter;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingRepayActualLibService financingRepayActualLibService;
    @Resource
    private FundFinancingRepayActualLibHandler financingRepayActualLibHandler;
    @Resource
    private FundFinancingBaseInfoStateMachine stateMachine;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;
    @Resource
    private FundReceiptRepayCashFlowService receiptRepayCashFlowService;
    @Resource
    private FundFinancingPlanService financingPlanService;

    public List<FundFinancingRepayActual> listByFinancingIdAndDate(Long financingId, LocalDate repayDate) {
        LambdaQueryWrapper<FundFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingRepayActual::getFinancingId, financingId);
        query.eq(FundFinancingRepayActual::getRepayDate, repayDate);
        return this.list(query);
    }

    public List<FundFinancingRepayActualListRSP> list(SingleFinancingIdREQ req) {
        List<FundFinancingRepayActual> dataList;
        if (StrUtil.isBlank(req.getVersion())) {
            dataList = this.listByFinancingId(req.getFinancingId());
        } else {
            List<FundFinancingRepayActualLib> libList = financingRepayActualLibService.listByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            dataList = libList.stream().map(financingRepayActualLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
        if (CollectionUtil.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        // 若现金流已核销，展示实际核销的数据
        Map<String, FundReceiptRepayCashFlow> cashFlowMap = receiptRepayCashFlowService.queryByFinancingId(req.getFinancingId(), FinancingTypeEnum.INDIRECT);
        if(CollectionUtil.isNotEmpty(cashFlowMap)){
            List<String> cashFlowCodeList = cashFlowMap.values().stream().filter(f -> !Objects.equals(CashFlowState.NO_WRITE_OFF.name(), f.getWriteOffState()))
                    .map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toList());
            Map<String, List<FundReceiptFlowDetail>> flowDetailMap = CollectionUtil.isNotEmpty(cashFlowCodeList) ? fundReceiptFlowDetailService.listByCashFlowCodes(cashFlowCodeList)
                    .stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode)) : Collections.emptyMap();
            for (FundFinancingRepayActual fundFinancingRepayActual : dataList) {
                if(fundFinancingRepayActual.getPhase() == 0){
                    continue;
                }
                FundReceiptRepayCashFlow repayCashFlow = cashFlowMap.get(fundFinancingRepayActual.getCashFlowCode());
                if(repayCashFlow == null){
                    continue;
                }
                fundFinancingRepayActual.setWriteOffStatus(repayCashFlow.getWriteOffState());

                List<FundReceiptFlowDetail> flowDetailList = flowDetailMap.get(fundFinancingRepayActual.getCashFlowCode());
                if(CollectionUtil.isEmpty(flowDetailList)){
                    continue;
                }
                LocalDate repayDate = null;
                Long principleAmount = 0L;
                Long interestAmount = 0L;
                Long repayAmount = 0L;
                for (FundReceiptFlowDetail fundReceiptFlowDetail : flowDetailList) {
                    // 有多个核销时 核销日期取最后一个即可
                    repayDate = fundReceiptFlowDetail.getCashFlowDate();
                    principleAmount += fundReceiptFlowDetail.getPrincipalAmount();
                    interestAmount += fundReceiptFlowDetail.getInterestAmount();
                    repayAmount += fundReceiptFlowDetail.getTotalAmount();
                }
                fundFinancingRepayActual.setRepayDate(repayDate);
                fundFinancingRepayActual.setPrincipleAmount(principleAmount);
                fundFinancingRepayActual.setInterestAmount(interestAmount);
                fundFinancingRepayActual.setRepayAmount(repayAmount);
            }
        }



        return this.convertToRSPList(dataList);
    }

    public List<FundFinancingRepayActual> listByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingRepayActual::getFinancingId, financingId);
        query.orderByAsc(FundFinancingRepayActual::getPhase);
        return this.list(query);
    }

    public Map<Long, List<FundFinancingRepayActual>> getMapByFinancingId(Collection<Long> financingIdList) {
        if(CollectionUtil.isNotEmpty(financingIdList)){
            LambdaQueryWrapper<FundFinancingRepayActual> query = Wrappers.lambdaQuery();
            query.in(FundFinancingRepayActual::getFinancingId, financingIdList);
            query.orderByAsc(FundFinancingRepayActual::getPhase);
            List<FundFinancingRepayActual> list = this.list(query);
            if(CollectionUtil.isNotEmpty(list)){
                return list.stream().collect(Collectors.groupingBy(FundFinancingRepayActual::getFinancingId));
            }
        }
        return Collections.emptyMap();
    }

    public List<FundFinancingRepayActualListRSP> convertToRSPList(List<FundFinancingRepayActual> financingRepayActualList) {
        return financingRepayActualList
                .stream()
                .map(item -> {
                    FundFinancingRepayActualListRSP rsp = new FundFinancingRepayActualListRSP();
                    BeanUtil.copyProperties(item, rsp);
                    rsp.setRepayDate(LocalDateTimeUtil.format(item.getRepayDate(), DatePattern.NORM_DATE_PATTERN));
                    return rsp;
                })
                .sorted(Comparator.comparing(FundFinancingRepayEstimateListRSP::getPhase))
                .collect(Collectors.toList());
    }

    @Resource
    private FundFinancingPlanService fundFinancingPlanService;

    @Transactional(rollbackFor = Throwable.class)
    public FundFinancingRepayActualImportRSP importExcel(Long financingId, InputStream inputStream, String scene, Boolean isCheck) {
        List<FundFinancingRepayEstimateExcelModel> excelModelList = financingRepayImporter.parse(inputStream);
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("没有从导入文件中获取到符合条件的数据，请检查文件"));
//        excelModelList = excelModelList.stream().filter(f -> f.getRepayPhase() != 0).collect(Collectors.toList());
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            Assert.notNull(excelModel.getRepayPhase(), () -> MithrasException.newException("期项不得为空"));
            Assert.notNull(excelModel.getRepayDate(), () -> MithrasException.newException("日期不得为空"));
        }
        // 查询已有的数据
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        // 校验金额
        BigDecimal totalAmount = excelModelList.stream().map(FundFinancingRepayEstimateExcelModel::getPrincipleAmount).map(e -> Objects.isNull(e) ? BigDecimal.ZERO : e).reduce(BigDecimal.ZERO, BigDecimal::add);
        FundFinancingPlan plan = fundFinancingPlanService.getOne(
                Wrappers.<FundFinancingPlan>lambdaQuery()
                        .eq(FundFinancingPlan::getFinancingId, financingId)
                        .last("limit 1"));
        if (ObjectUtil.isEmpty(plan)) {
            throw MithrasException.newException("请先编辑并保存融资方案信息");
        }
        List<FundFinancingRepayEstimateExcelModel> zeroPhase = excelModelList.stream().filter(f -> f.getRepayPhase() == 0).collect(Collectors.toList());
        Assert.isTrue(CollectionUtil.isNotEmpty(zeroPhase) && Objects.equals(zeroPhase.get(0).getRemainingPrincipleAmount().longValue(), plan.getFinancingAmount() / 10000), () -> MithrasException.newException("第0期现金流不存在或与融资金额不一致，请检查"));
        if (!Long.valueOf(totalAmount.longValue()).equals(plan.getFinancingAmount() / 10000)) {
            throw MithrasException.newException("本金之和不等于融资金额，请检查");
        }
//        // 校验实际贷款日期
//        if (Objects.nonNull(financingBaseInfo.getActualLoanDate())) {
//            excelModelList.sort(Comparator.comparing(FundFinancingRepayEstimateExcelModel::getRepayPhase));
//            FundFinancingRepayEstimateExcelModel first = excelModelList.get(0);
//            Assert.isTrue(financingBaseInfo.getActualLoanDate().isEqual(first.getRepayDate()), () -> MithrasException.newException("实际贷款日期必须和实际还款表第1行数据日期一致"));
//        }
        // 校验每一期的本金+利息是否等于还款金额
        List<String> errorPhaseList = new ArrayList<>();
        BigDecimal remainingPrincipleAmount = zeroPhase.get(0).getRemainingPrincipleAmount();
        LocalDate repayDate = zeroPhase.get(0).getRepayDate();
        BigDecimal interestAmountSum = BigDecimal.ZERO;
        BigDecimal systemInterestAmount = BigDecimal.ZERO;
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            if(Optional.ofNullable(excelModel.getPrincipleAmount()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(excelModel.getInterestAmount()).orElse(BigDecimal.ZERO))
                    .compareTo(Optional.ofNullable(excelModel.getRepayAmount()).orElse(BigDecimal.ZERO)) != 0){
                errorPhaseList.add(String.format("第%s期还款金额与本金、利息之和不一致，请检查", excelModel.getRepayPhase()));
            }
            // 每一期的剩余本金改为系统测算
            if(excelModel.getRepayPhase() != 0) {
                // 系统计算的利息合计值 ∑(剩余本金*对应计息天数*合同利率/360)
                long gapDay = ChronoUnit.DAYS.between(repayDate, excelModel.getRepayDate());
                systemInterestAmount = systemInterestAmount.add(remainingPrincipleAmount.multiply(BigDecimal.valueOf(gapDay)));

                remainingPrincipleAmount = remainingPrincipleAmount.subtract(excelModel.getPrincipleAmount());
                excelModel.setRemainingPrincipleAmount(remainingPrincipleAmount);
                // 利息之和
                interestAmountSum = interestAmountSum.add(Optional.ofNullable(excelModel.getInterestAmount()).orElse(BigDecimal.ZERO));
                repayDate = excelModel.getRepayDate();
            }
        }
        Assert.isTrue(CollectionUtil.isEmpty(errorPhaseList), () -> MithrasException.newException(String.join(",", errorPhaseList)));

        if(isCheck != null && isCheck){
            // 校验导入的每期利息之和与系统计算的利息合计值 ∑(剩余本金*对应计息天数*合同利率/360) 的差额是否大于10元
            BigDecimal contractRate = Optional.ofNullable(plan.getLprRatePercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(plan.getLprAddPercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO));
            BigDecimal systemCalculateResult = systemInterestAmount.multiply(contractRate).divide(BigDecimal.valueOf(360000000), 10, RoundingMode.HALF_UP);
            FundFinancingRepayActualImportRSP rsp = new FundFinancingRepayActualImportRSP();
            rsp.setInterestDiff(Util.toMithrasUnit(systemCalculateResult.subtract(interestAmountSum).abs()));
            return rsp;
        }

        List<FundFinancingRepayActual> existList = this.listByFinancingId(financingId);
        Map<String, FundReceiptRepayCashFlow> cashFlowMap = receiptRepayCashFlowService.queryByFinancingId(financingId, FinancingTypeEnum.INDIRECT);
        Map<Integer, FundFinancingRepayActual> repayActualMap = existList.stream()
                .filter(f -> Objects.equals(Optional.ofNullable(cashFlowMap.get(f.getCashFlowCode())).map(FundReceiptRepayCashFlow::getWriteOffState).orElse(CashFlowState.NO_WRITE_OFF.name()), CashFlowState.NO_WRITE_OFF.name()))
                .collect(Collectors.toMap(FundFinancingRepayActual::getPhase, e -> e));
        Map<Integer, FundFinancingRepayActual> writeOffingMap = existList.stream()
                .filter(f -> Objects.equals(Optional.ofNullable(cashFlowMap.get(f.getCashFlowCode())).map(FundReceiptRepayCashFlow::getWriteOffState).orElse(CashFlowState.WRITE_OFF_ING.name()), CashFlowState.WRITE_OFF_ING.name()))
                .collect(Collectors.toMap(FundFinancingRepayActual::getPhase, e -> e));
        for (Map.Entry<Integer, FundFinancingRepayActual> entry : writeOffingMap.entrySet()) {
            repayActualMap.putIfAbsent(entry.getKey(), entry.getValue());
        }
        // 取到已核销的期项
        List<Integer> alreadyWriteOffPhase = existList.stream().map(FundFinancingRepayActual::getPhase).collect(Collectors.toList());
        alreadyWriteOffPhase.removeAll(repayActualMap.keySet());

        List<FundFinancingRepayActual> fundFinancingRepayActualList = excelModelList.stream()
                .filter(f -> !alreadyWriteOffPhase.contains(f.getRepayPhase())).map(item -> {
            FundFinancingRepayActual repayActual = new FundFinancingRepayActual();
            repayActual.setFinancingId(financingId);
            // 生成现金流编号
            repayActual.setCashFlowCode(String.format("%s-%03d", financingBaseInfo.getFinancingCode(), item.getRepayPhase()));
            repayActual.setRepayDate(item.getRepayDate());
            repayActual.setPhase(item.getRepayPhase());
            if (Objects.nonNull(item.getPrincipleAmount())) {
                repayActual.setPrincipleAmount(item.getPrincipleAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (Objects.nonNull(item.getInterestAmount())) {
                repayActual.setInterestAmount(item.getInterestAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (Objects.nonNull(item.getRepayAmount())) {
                repayActual.setRepayAmount(item.getRepayAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (Objects.nonNull(item.getRemainingPrincipleAmount())) {
                repayActual.setRemainingPrincipleAmount(item.getRemainingPrincipleAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            // 判断对应期项是否已存在数据
            FundFinancingRepayActual exist = repayActualMap.get(item.getRepayPhase());
            if (Objects.nonNull(exist)) {
                repayActual.setId(exist.getId());
                repayActualMap.remove(item.getRepayPhase());
            }
            return repayActual;
        }).collect(Collectors.toList());
        List<FundReceiptFlowDetail> flowDetailList = CollectionUtil.isNotEmpty(cashFlowMap) ? fundReceiptFlowDetailService.listByCashFlowCodes(cashFlowMap.keySet()) : null;
        if(CollectionUtil.isNotEmpty(flowDetailList)) {
            long alreadyWriteOff = flowDetailList.stream().mapToLong(f -> Optional.ofNullable(f.getPrincipalAmount()).orElse(0L)).sum();
            long noWriteOff = fundFinancingRepayActualList.stream().mapToLong(f -> Optional.ofNullable(f.getPrincipleAmount()).orElse(0L)).sum();
            if(alreadyWriteOff + noWriteOff != financingBaseInfo.getFinancingAmount()){
                throw new MithrasException("已核销总金额+未核销期项总金额不等于融资金额");
            }
        }
        this.saveOrUpdateBatch(fundFinancingRepayActualList);
        // map中如果有剩余说明是需要删除的
        if (CollectionUtil.isNotEmpty(repayActualMap)) {
            Collection<FundFinancingRepayActual> repayActualList = repayActualMap.values();
            List<Long> ids = repayActualList.stream().map(FundFinancingRepayActual::getId).collect(Collectors.toList());
            this.removeByIds(ids);
        }
        // 回填借款日期和到期日期
        fundFinancingRepayActualList.sort(Comparator.comparing(FundFinancingRepayActual::getPhase));
        FundFinancingRepayActual first = fundFinancingRepayActualList.get(0);
        FundFinancingRepayActual last = fundFinancingRepayActualList.get(fundFinancingRepayActualList.size() - 1);
        financingBaseInfoService.modifyActualLoanDate(financingBaseInfo.getId(), first.getRepayDate());
        financingBaseInfoService.modifyActualExpireDate(financingBaseInfo.getId(), last.getRepayDate());
        // 根据不同场景处理后续逻辑
        FundFinancingSceneEnum sceneEnum = FundFinancingSceneEnum.find(scene);
        if (Objects.isNull(sceneEnum)) {
            throw new MithrasException("未定义的导入场景");
        }
        switch (sceneEnum) {
            case CHANGE_LPR: {
                FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(financingBaseInfo, FundFinancingEvent.MODIFY_SAVE, FundFinancingProcessStatus.of(financingBaseInfo.getApprovalStatus()));
                stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_LPR.name());
                break;
            }
            case CHANGE_SETTLE_EARLY: {
                FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(financingBaseInfo, FundFinancingEvent.MODIFY_SAVE, FundFinancingProcessStatus.of(financingBaseInfo.getApprovalStatus()));
                stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
                break;
            }
            case CHANGE_OTHER: {
                FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(financingBaseInfo, FundFinancingEvent.MODIFY_SAVE, FundFinancingProcessStatus.of(financingBaseInfo.getApprovalStatus()));
                stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_OTHER.name());
                break;
            }
        }
        financingPlanService.updateFinancingCost(financingId);
        return null;
    }

    public void exportExcel(OutputStream outputStream, Long financingId) {
        List<FundFinancingRepayActual> repayActualList = this.listByFinancingId(financingId);
        Assert.notEmpty(repayActualList, () -> MithrasException.newException("没有可导出的数据"));
        List<FundFinancingRepayActualExcelModel> excelModelList = repayActualList.stream().map(item -> {
            FundFinancingRepayActualExcelModel excelModel = new FundFinancingRepayActualExcelModel();
            excelModel.setCashFlowCode(item.getCashFlowCode());
            excelModel.setRepayDate(item.getRepayDate());
            excelModel.setRepayPhase(item.getPhase());
            if (Objects.nonNull(item.getPrincipleAmount())) {
                excelModel.setPrincipleAmount(BigDecimal.valueOf(item.getPrincipleAmount()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getInterestAmount())) {
                excelModel.setInterestAmount(BigDecimal.valueOf(item.getInterestAmount()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRepayAmount())) {
                excelModel.setRepayAmount(BigDecimal.valueOf(item.getRepayAmount()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            if (Objects.nonNull(item.getRemainingPrincipleAmount())) {
                excelModel.setRemainingPrincipleAmount(BigDecimal.valueOf(item.getRemainingPrincipleAmount()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            return excelModel;
        }).collect(Collectors.toList());
        financingRepayActualExporter.exportExcel(excelModelList, outputStream);
    }

    public List<FundFinancingRepayActual> listByFinancingIdExcludeZeroPhase(Long financingId) {
        LambdaQueryWrapper<FundFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingRepayActual::getFinancingId, financingId);
        query.ne(FundFinancingRepayActual::getPhase, 0);
        query.orderByAsc(FundFinancingRepayActual::getPhase);
        return this.list(query);
    }
}
