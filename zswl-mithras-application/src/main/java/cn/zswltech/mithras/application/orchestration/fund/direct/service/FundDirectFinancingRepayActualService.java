package cn.zswltech.mithras.application.orchestration.fund.direct.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingAssetPoolDetailRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingRepayActualImportRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingRepayActualListREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingRepayActualListRSP;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.projectprocess.enums.projestablish.PayType;
import cn.zswltech.mithras.fund.excel.importer.FundFinancingRepayImporter;
import cn.zswltech.mithras.fund.excel.model.FundFinancingRepayEstimateExcelModel;
import cn.zswltech.mithras.fund.directfinancing.application.convert.FundDirectFinancingRepayActualConverter;
import cn.zswltech.mithras.fund.directfinancing.application.FundDirectFinancingAssetPoolService;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.FundDirectFinancingRepayActualExcelModel;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.FundDirectFinancingRepayActualExporter;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingRepayActualMapper;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.model.CashFlowCalculateBO;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Service
public class FundDirectFinancingRepayActualService
        extends ServiceImpl<FundDirectFinancingRepayActualMapper, FundDirectFinancingRepayActual> {

    @Resource
    private FundFinancingRepayImporter financingRepayImporter;
    @Resource
    private FundDirectFinancingRepayActualConverter baseConverter;
    @Resource
    private FundDirectFinancingRepayActualExporter directFinancingRepayActualExporter;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;
    @Resource
    private FundReceiptFlowDetailService receiptFlowDetailService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private FundReceiptRepayCashFlowService receiptRepayCashFlowService;
    @Resource
    private FundDirectFinancingAssetPoolService fundDirectFinancingAssetPoolService;
    @Resource
    private FundDirectFinancingRepayActualSplitService fundDirectFinancingRepayActualSplitService;

    public List<FundDirectFinancingRepayActual> listByCashFlowCodes(Collection<String> cashFlowCodes) {
        LambdaQueryWrapper<FundDirectFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingRepayActual::getCashFlowCode, cashFlowCodes);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void generateCashFlow(Long financingId) {
        FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = directFinancingBaseInfoService.getById(financingId);
        if (Objects.isNull(fundDirectFinancingBaseInfo)) {
            throw new MithrasException("直融数据不存在");
        }
        // 移除已有的未核销数据
        List<FundDirectFinancingRepayActual> exist = this.listByFinancingId(financingId);
        Map<String, FundReceiptRepayCashFlow> cashFlowMap = receiptRepayCashFlowService.queryByFinancingId(financingId, FinancingTypeEnum.DIRECT);
        // modify 260119 如果存在不是未核销的现金流，则不允许重新测算
        if (checkRepayCashFlowState(cashFlowMap)) {
            throw new MithrasException("无法重新测算");
        }
        //移除期项大于0的记录
        exist.removeIf(e -> e.getPhase() == 0);
        this.removeByIds(exist.stream().map(FundDirectFinancingRepayActual::getId).collect(Collectors.toList()));
        // 校验
        Assert.notNull(fundDirectFinancingBaseInfo.getFinancingAmount(), () -> MithrasException.newException("<融资金额>不能为空"));
        Assert.notBlank(fundDirectFinancingBaseInfo.getRepayFrequency(), () -> MithrasException.newException("<还款频率>不能为空"));
        Assert.notBlank(fundDirectFinancingBaseInfo.getRepayWay(), () -> MithrasException.newException("<还款方式>不能为空"));
        Assert.notNull(fundDirectFinancingBaseInfo.getAverageCouponRate(), () -> MithrasException.newException("<票面加权平均利率>不能为空"));
        Assert.notNull(fundDirectFinancingBaseInfo.getCarryInterestTime(), () -> MithrasException.newException("<起息日>不能为空"));
        Assert.notNull(fundDirectFinancingBaseInfo.getFinancingMonth(), () -> MithrasException.newException("<融资期限（月）>不能为空"));
        // 生成现金流
        CashFlowCalculateBO cashFlowCalculateBO = new CashFlowCalculateBO();
        cashFlowCalculateBO.setInterestWay(InterestWayEnum.ACTUAL_RATE.name());
        cashFlowCalculateBO.setPayType(PayType.AFTERWARD.name());
        cashFlowCalculateBO.setCreditAmount(Optional.ofNullable(fundDirectFinancingBaseInfo.getFinancingAmount()).orElse(0L) * 10000);
        cashFlowCalculateBO.setRepayRate(fundDirectFinancingBaseInfo.getRepayFrequency());
        cashFlowCalculateBO.setRentalCalcType(fundDirectFinancingBaseInfo.getRepayWay());
        cashFlowCalculateBO.setInterestRate(Optional.ofNullable(fundDirectFinancingBaseInfo.getAverageCouponRate()).map(e -> Integer.valueOf(e.toString())).orElse(0));
        cashFlowCalculateBO.setStartDate(fundDirectFinancingBaseInfo.getCarryInterestTime());
        cashFlowCalculateBO.setRepayTimes(FinancialUtil.calcRepayTimes(fundDirectFinancingBaseInfo.getFinancingMonth(), fundDirectFinancingBaseInfo.getRepayFrequency()));
        cashFlowCalculateBO.setTotalMonth(fundDirectFinancingBaseInfo.getFinancingMonth());
        List<CashFlowBO> cashFlowList = FinancialUtil.calcCashFlow(cashFlowCalculateBO);
        // 转化并存储
        if (CollectionUtil.isEmpty(cashFlowList)) {
            return;
        }
        List<FundDirectFinancingRepayActual> list = cashFlowList.stream()
                .filter(e -> e.getCashFlowPhase() > 0)
                .map(e -> {
                    FundDirectFinancingRepayActual repayActual = new FundDirectFinancingRepayActual();
                    repayActual.setFinancingId(financingId);
                    repayActual.setCashFlowCode(generateCashFlowCode(fundDirectFinancingBaseInfo.getFinancingCode(), e.getCashFlowPhase()));
                    repayActual.setRepayDate(e.getCashFlowDate());
                    repayActual.setPhase(e.getCashFlowPhase());
                    repayActual.setRepayAmount(e.getRent());
                    repayActual.setPrincipleAmount(e.getPrincipal());
                    repayActual.setInterestAmount(e.getInterest());
                    repayActual.setRemainingPrincipleAmount(e.getRemainingPrincipal());
                    repayActual.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());
                    return repayActual;
                })
                .sorted(Comparator.comparing(FundDirectFinancingRepayActual::getPhase))
                .collect(Collectors.toList());
        // 现金流最后一期还款日为到期日
        directFinancingBaseInfoService.updateDurationTime(financingId ,list.get(list.size() - 1).getRepayDate());
        this.saveBatch(list);
    }

    private boolean checkRepayCashFlowState(Map<String, FundReceiptRepayCashFlow> cashFlowMap) {
        List<FundReceiptRepayCashFlow> collect = cashFlowMap.values().stream().filter(v -> !Objects.equals(Optional.ofNullable(v).map(FundReceiptRepayCashFlow::getWriteOffState).orElse(null), CashFlowState.NO_WRITE_OFF.name())).collect(Collectors.toList());
        return !CollectionUtil.isEmpty(collect);
    }

    public List<FundDirectFinancingRepayActual> listByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundDirectFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingRepayActual::getFinancingId, financingId);
        query.orderByAsc(FundDirectFinancingRepayActual::getRepayDate);
        query.orderByAsc(FundDirectFinancingRepayActual::getPhase);
        return this.list(query);
    }

    public List<FundDirectFinancingRepayActual> listByFinancingIdExcludeZeroPhase(Long financingId) {
        LambdaQueryWrapper<FundDirectFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.eq(FundDirectFinancingRepayActual::getFinancingId, financingId);
        query.ne(FundDirectFinancingRepayActual::getPhase, 0);
        query.orderByAsc(FundDirectFinancingRepayActual::getPhase);
        return this.list(query);
    }

    public PageR<FundDirectFinancingRepayActualListRSP> list(FundDirectFinancingRepayActualListREQ req) {
        Page<FundDirectFinancingRepayActual> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                        .eq(FundDirectFinancingRepayActual::getFinancingId, req.getFinancingId())
                        .orderByAsc(FundDirectFinancingRepayActual::getPhase));
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Map<String, FundReceiptRepayCashFlow> cashFlowMap = receiptRepayCashFlowService.queryByFinancingId(req.getFinancingId(), FinancingTypeEnum.DIRECT);
        List<FundDirectFinancingRepayActualListRSP> rspList = baseConverter.entity2ListRsp(page.getRecords());
        for (FundDirectFinancingRepayActualListRSP rsp : rspList) {
            FundReceiptRepayCashFlow repayCashFlow = cashFlowMap.get(rsp.getCashFlowCode());
            if(repayCashFlow != null){
                rsp.setWriteOffStatus(repayCashFlow.getWriteOffState());
            }
        }
        return PageR.of(page, rspList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public FundDirectFinancingRepayActualImportRSP importExcel(Long financingId, InputStream inputStream, Boolean isCheck) {
        // 查询已有的数据
        FundDirectFinancingBaseInfo financingBaseInfo = directFinancingBaseInfoService.getById(financingId);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        // 产品明细金额之和需等于融资金额
        List<FundDirectFinancingProductDetail> productDetailList = SpringUtil.getBean(FundDirectFinancingProductDetailService.class).listByFinancingId(financingId);
        long sum = productDetailList.stream().filter(e -> Objects.nonNull(e.getIssuanceAmount())).mapToLong(FundDirectFinancingProductDetail::getIssuanceAmount).sum();
        if (!Objects.equals(financingBaseInfo.getFinancingAmount(), sum)) {
            throw new MithrasException("产品发行金额合计不等于融资金额，请检查");
        }
        // 如果已经还过钱了就不允许导入了
        if (this.isExistWrittenOffCashFlow(financingId)) {
            throw new MithrasException("存在已经核销的数据，不允许重新导入");
        }
        //数据装换
        List<FundFinancingRepayEstimateExcelModel> excelModelList = financingRepayImporter.parse(inputStream);
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("没有从导入文件中获取到符合条件的数据，请检查文件"));
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            Assert.notNull(excelModel.getRepayPhase(), () -> MithrasException.newException("期项不得为空"));
            Assert.notNull(excelModel.getRepayDate(), () -> MithrasException.newException("日期不得为空"));
        }
        // ABS，ABN仅导入日期 自动测算现金流
        if(Arrays.asList(DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name()).contains(financingBaseInfo.getDirectFinancingType())){
            //数据必须包含第0期
            Assert.isTrue(excelModelList.stream().anyMatch(f -> f.getRepayPhase() == 0), () -> MithrasException.newException("缺少第0期"));
            //测算
            List<FundDirectFinancingRepayActual> repayActualList = calculateCashFlow(financingBaseInfo, excelModelList);
            //删除之前数据
            remove(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery().eq(FundDirectFinancingRepayActual::getFinancingId, financingId));
            saveBatch(repayActualList);
            FundDirectFinancingRepayActualImportRSP rsp = new FundDirectFinancingRepayActualImportRSP();
            rsp.setInterestDiff(0L);
            // 按照产品明细进行拆分
            fundDirectFinancingRepayActualSplitService.trySplit(financingId);
            return rsp;
        }
        List<FundDirectFinancingRepayActual> existList = list(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                .eq(FundDirectFinancingRepayActual::getFinancingId, financingId));
        Map<Integer, FundDirectFinancingRepayActual> repayActualMap = existList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActual::getPhase, e -> e));
        BigDecimal totalAmount = excelModelList.stream().map(FundFinancingRepayEstimateExcelModel::getPrincipleAmount).map(e -> Objects.isNull(e) ? BigDecimal.ZERO : e).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!Long.valueOf(totalAmount.longValue()).equals(financingBaseInfo.getFinancingAmount())) {
            throw MithrasException.newException("本金之和不等于融资金额，请检查");
        }
        List<FundFinancingRepayEstimateExcelModel> zeroPhase = excelModelList.stream().filter(f -> f.getRepayPhase() == 0).collect(Collectors.toList());
        Assert.isTrue(CollectionUtil.isNotEmpty(zeroPhase) && Objects.equals(zeroPhase.get(0).getRemainingPrincipleAmount().longValue(), financingBaseInfo.getFinancingAmount()), () -> MithrasException.newException("第0期现金流不存在或与融资金额不一致，请检查"));
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

                remainingPrincipleAmount = remainingPrincipleAmount.subtract(Optional.ofNullable(excelModel.getPrincipleAmount()).orElse(BigDecimal.ZERO));
                excelModel.setRemainingPrincipleAmount(remainingPrincipleAmount);
                // 利息之和
                interestAmountSum = interestAmountSum.add(Optional.ofNullable(excelModel.getInterestAmount()).orElse(BigDecimal.ZERO));
                repayDate = excelModel.getRepayDate();
            }
        }
        Assert.isTrue(CollectionUtil.isEmpty(errorPhaseList), () -> MithrasException.newException(String.join(",", errorPhaseList)));

        if(isCheck != null && isCheck){
            // 校验导入的每期利息之和与系统计算的利息合计值 ∑(剩余本金*对应计息天数*合同利率/360) 的差额是否大于10元
            BigDecimal contractRate = Optional.ofNullable(financingBaseInfo.getAverageCouponRate()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
            BigDecimal systemCalculateResult = systemInterestAmount.multiply(contractRate).divide(BigDecimal.valueOf(360000000), 10, RoundingMode.HALF_UP);
            FundDirectFinancingRepayActualImportRSP rsp = new FundDirectFinancingRepayActualImportRSP();
            rsp.setInterestDiff(Util.toMithrasUnit(systemCalculateResult.subtract(interestAmountSum).abs()));
            return rsp;
        }

        List<FundDirectFinancingRepayActual> fundFinancingRepayActualList = excelModelList.stream().map(item -> {
            FundDirectFinancingRepayActual repayActual = new FundDirectFinancingRepayActual();
            repayActual.setFinancingId(financingId);
            // 生成现金流编号
            repayActual.setCashFlowCode(generateCashFlowCode(financingBaseInfo.getFinancingCode(), item.getRepayPhase()));
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
            FundDirectFinancingRepayActual exist = repayActualMap.get(item.getRepayPhase());
            if (Objects.nonNull(exist)) {
                repayActual.setId(exist.getId());
                repayActualMap.remove(item.getRepayPhase());
            }
            return repayActual;
        }).collect(Collectors.toList());
        saveOrUpdateBatch(fundFinancingRepayActualList);
        // map中如果有剩余说明是需要删除的
        if (CollectionUtil.isNotEmpty(repayActualMap)) {
            Collection<FundDirectFinancingRepayActual> repayActualList = repayActualMap.values();
            List<Long> ids = repayActualList.stream().map(FundDirectFinancingRepayActual::getId).collect(Collectors.toList());
            removeByIds(ids);
        }
        directFinancingBaseInfoService.updateFinancingCost(financingId);
        // 现金流最后一期还款日为到期日
        directFinancingBaseInfoService.updateDurationTime(financingBaseInfo.getId() ,fundFinancingRepayActualList.get(fundFinancingRepayActualList.size() - 1).getRepayDate());
        return null;
    }

    public void exportExcel(ServletOutputStream outputStream, Long financingId) {
        List<FundDirectFinancingRepayActual> repayActualList = list(
                Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                        .eq(FundDirectFinancingRepayActual::getFinancingId, financingId));
        Assert.notEmpty(repayActualList, () -> MithrasException.newException("没有可导出的数据"));
        List<FundDirectFinancingRepayActualExcelModel> excelModelList = repayActualList.stream().map(item -> {
            FundDirectFinancingRepayActualExcelModel excelModel = new FundDirectFinancingRepayActualExcelModel();
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
            if (Objects.nonNull(item.getPrePayDifference())) {
                excelModel.setPrePayDifference(BigDecimal.valueOf(item.getPrePayDifference()).divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 2, RoundingMode.HALF_UP));
            }
            return excelModel;
        }).collect(Collectors.toList());
        directFinancingRepayActualExporter.exportExcel(excelModelList, outputStream);
    }


    /**
     * 直融ABS/ABN 现金流测算
     * @param financingBaseInfo
     * @return
     */
    public List<FundDirectFinancingRepayActual> calculateCashFlow(FundDirectFinancingBaseInfo financingBaseInfo, List<FundFinancingRepayEstimateExcelModel> excelModelList){
        Assert.notNull(financingBaseInfo.getCarryInterestTime(), () -> MithrasException.newException("合同起息日不存在"));
        FundDirectFinancingAssetPoolDetailRSP assetPoolDetail = fundDirectFinancingAssetPoolService.detail(financingBaseInfo.getId());
        Assert.notNull(assetPoolDetail, () -> MithrasException.newException("资产池信息不存在"));
        Assert.notNull(assetPoolDetail.getPackageDate(), () -> MithrasException.newException("封包日不存在"));
        Assert.notNull(financingBaseInfo.getAverageCouponRate(), () -> MithrasException.newException("票面加权利率不存在"));
        Assert.isFalse(excelModelList.stream().anyMatch(f -> f.getRepayPhase() == null || f.getRepayDate() == null), () -> MithrasException.newException("期项和时间不得为空"));
        //关联合同明细（质押明细）
        List<FundDirectFinancingPledgeInfo> pledgeInfoList = directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getFinancingId, financingBaseInfo.getId()));
        Assert.isTrue(CollectionUtil.isNotEmpty(pledgeInfoList), () -> MithrasException.newException("关联合同不存在"));

        // 获取关联合同的现金流
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByContractIds(pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
        // 封包日 应还的租金总额计算时按封包日为第0期日期
        LocalDate packageDate = assetPoolDetail.getPackageDate();
        //各期项的时间、金额初始化起息日
        LocalDate calculateTimeStart = financingBaseInfo.getCarryInterestTime();
        //取最大期限
        FundFinancingRepayEstimateExcelModel excelModelLastPhase = excelModelList.stream().max(Comparator.comparing(FundFinancingRepayEstimateExcelModel::getRepayPhase)).get();
        LocalDate lastTime = excelModelLastPhase.getRepayDate();
        //最后一期还款日-计算日
        if (Objects.nonNull(financingBaseInfo.getCalculateDay())) {
            lastTime = lastTime.minusDays(financingBaseInfo.getCalculateDay());
        }
        final LocalDate targetDate = lastTime;
        //取现金流日期 > 资产池封包日期 且 现金流日期≤ 目标截止日期  的本金求和
        long principalSum = contractRentActualList.stream().filter(f -> f.getCashFlowDate().isAfter(assetPoolDetail.getPackageDate())
                && !f.getCashFlowDate().isAfter(targetDate)).mapToLong(ContractRentActual::getPrincipal).sum();
        //融资金额*10000  因为直融表的金额有是元作为单位，进度需要所以转换。
        long remainingAmount = financingBaseInfo.getFinancingAmount() * 10000L;

        List<FundDirectFinancingRepayActual> repayActualList = new ArrayList<>();
        //组装第0期
        FundDirectFinancingRepayActual repayActualZero = new FundDirectFinancingRepayActual();
        repayActualZero.setPhase(0);
        repayActualZero.setFinancingId(financingBaseInfo.getId());
        repayActualZero.setRepayDate(financingBaseInfo.getCarryInterestTime());
        repayActualZero.setRemainingPrincipleAmount(remainingAmount);
        repayActualZero.setCashFlowCode(String.format("%s-%03d", financingBaseInfo.getFinancingCode(), 0));
        repayActualList.add(repayActualZero);
        //一期一期计算
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            if(excelModel.getRepayPhase().equals(0)){
                continue;
            }
            //计算明细
            FundDirectFinancingRepayActual repayActual = getRepayActual(financingBaseInfo, excelModel, contractRentActualList, calculateTimeStart, remainingAmount, principalSum);
            //封包日 应还的租金总额及本金计算时按封包日为第0期日期
            if (excelModel.getRepayPhase().equals(1)) {
                FundDirectFinancingRepayActual repayActualFirst = getRepayActual(financingBaseInfo, excelModel, contractRentActualList, packageDate, remainingAmount, principalSum);
                if (ObjectUtil.isNotEmpty(repayActualFirst)) {
                    repayActual.setRepayAmount(repayActualFirst.getRepayAmount());
                    repayActual.setPrincipleAmount(repayActualFirst.getPrincipleAmount());
                    repayActual.setRemainingPrincipleAmount(repayActualFirst.getRemainingPrincipleAmount());
                    // 调整预付差额
                    long prePayDifference = Optional.ofNullable(repayActual.getRepayAmount()).orElse(0L) - Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) - Optional.ofNullable(repayActual.getInterestAmount()).orElse(0L);
                    repayActual.setPrePayDifference(prePayDifference);
                }
            }
            if (Objects.nonNull(financingBaseInfo.getCalculateDay())) {
                calculateTimeStart = excelModel.getRepayDate().minusDays(financingBaseInfo.getCalculateDay());
            } else {
                calculateTimeStart = excelModel.getRepayDate();
            }
            // 调整剩余本金
            remainingAmount = Optional.ofNullable(repayActual.getRemainingPrincipleAmount()).orElse(0L);
            repayActualList.add(repayActual);
        }
        if(CollectionUtil.isNotEmpty(repayActualList)){
            long principleSum = repayActualList.stream().filter(f -> f.getPrincipleAmount() != null).mapToLong(FundDirectFinancingRepayActual::getPrincipleAmount).sum();
            long principleAll = financingBaseInfo.getFinancingAmount() * 10000L;
            FundDirectFinancingRepayActual repayActual = repayActualList.get(repayActualList.size() - 1);
            if(principleSum != principleAll){
                // 最后一期要补回差额
                long difference = principleAll - principleSum;
                repayActual.setPrincipleAmount(Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) + difference);
                repayActual.setRemainingPrincipleAmount(Optional.ofNullable(repayActual.getRemainingPrincipleAmount()).orElse(0L) - difference);
                repayActual.setPrePayDifference(Optional.ofNullable(repayActual.getPrePayDifference()).orElse(0L) - difference);
            }
            // 现金流最后一期还款日为到期日
            directFinancingBaseInfoService.updateDurationTime(financingBaseInfo.getId() ,repayActual.getRepayDate());
        }
        return repayActualList;

    }

    /**
     * 组装直融-实际还款表明细数据
     * @param financingBaseInfo 直融信息
     * @param excelModel excle 导入数据
     * @param contractRentActualList 关联合同收款明细
     * @param calculateTimeStart 起息日
     * @param remainingAmount 融资金额
     * @param principalSum 现金流日期 > 资产池封包日期 且 现金流日期≤ 目标截止日期  的本金求和
     *
     */
    private FundDirectFinancingRepayActual getRepayActual(FundDirectFinancingBaseInfo financingBaseInfo, FundFinancingRepayEstimateExcelModel excelModel, List<ContractRentActual> contractRentActualList,
                                                          LocalDate calculateTimeStart, long remainingAmount, long principalSum) {
        FundDirectFinancingRepayActual repayActual = new FundDirectFinancingRepayActual();
        repayActual.setFinancingId(financingBaseInfo.getId());
        repayActual.setRepayDate(excelModel.getRepayDate());
        repayActual.setPhase(excelModel.getRepayPhase());
        repayActual.setCashFlowCode(String.format("%s-%03d", financingBaseInfo.getFinancingCode(), excelModel.getRepayPhase()));
        repayActual.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());

        // 获取当前期项时间期间内的底层合同对应的现金流  取还款日在起息日-截止日（还款日-计算日）
        List<ContractRentActual> rentActualList = new ArrayList<>();
        for (ContractRentActual item : contractRentActualList) {
            // 检查当前元素的现金流日期是否在指定范围内
            LocalDate endDate = excelModel.getRepayDate();
            if (Objects.nonNull(financingBaseInfo.getCalculateDay())) {
                endDate = endDate.minusDays(financingBaseInfo.getCalculateDay());
            }
            if (!item.getCashFlowDate().isBefore(calculateTimeStart) && item.getCashFlowDate().isBefore(endDate)) {
                rentActualList.add(item);
            }
        }
        repayActual.setRemainingPrincipleAmount(remainingAmount);
        if (CollectionUtil.isNotEmpty(rentActualList)) {
            // 还款金额 = 计算日+起息日期间的底层合同的租金金额之和
            BigDecimal rentSum = rentActualList.stream().map(m -> BigDecimal.valueOf(m.getRent())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayActual.setRepayAmount(rentSum.longValue());

            // 金额需要控制精度，最后的入库数据只可以精确到分
            // 本金 = 据底层合同的每期租金本金的分布比例将融资金额分摊至每期（某期本金=某期还款金额对应底层租金本金/租金之和*融资金额）
            long currencyPrincipal = rentActualList.stream().mapToLong(ContractRentActual::getPrincipal).sum();
            //这里需要转换*10000  因为直融表的金额有是元作为单位，这里和计划表的 元*10000单位不一致 所以需要转换。
            BigDecimal financingPrincipal = BigDecimal.valueOf(currencyPrincipal).divide(BigDecimal.valueOf(principalSum), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(financingBaseInfo.getFinancingAmount() * 10000));
            Long principal = Util.mithrasLongDecimalTwo(financingPrincipal.longValue());
            repayActual.setPrincipleAmount(principal);

            // 利息 = 上期剩余本金*票面加权平均利率*间隔天数
            long gapDay = ChronoUnit.DAYS.between(calculateTimeStart, excelModel.getRepayDate());
            //计算 每一天的利率  票面加权平均利率/360天/100百分比单位/10000 转换约定
            BigDecimal averageCouponDayRate = BigDecimal.valueOf(financingBaseInfo.getAverageCouponRate()).divide(new BigDecimal("360000000"), 10, RoundingMode.HALF_UP);
            BigDecimal financingInterest = BigDecimal.valueOf(remainingAmount).multiply(averageCouponDayRate).multiply(BigDecimal.valueOf(gapDay));
            Long interest = Util.mithrasLongDecimalTwo(financingInterest.longValue());
            repayActual.setInterestAmount(interest);

            // 预付差额
            BigDecimal prePayDifference = rentSum.subtract(BigDecimal.valueOf(principal)).subtract(BigDecimal.valueOf(interest));
            repayActual.setPrePayDifference(prePayDifference.longValue());

            // 更新剩余本金
            remainingAmount = remainingAmount - principal;
            repayActual.setRemainingPrincipleAmount(remainingAmount);
            repayActual.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());
        }
        return repayActual;
    }


    @Transactional(rollbackFor = Throwable.class)
    public void saveCashFlowAgain(Long directFinancingId){
        FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingBaseInfoService.getById(directFinancingId);
        if(!Arrays.asList(FundFinancingStatusEnum.NEW.name(), FundFinancingStatusEnum.EFFECT.name()).contains(directFinancingBaseInfo.getFinancingStatus())){
            return;
        }
        List<FundDirectFinancingRepayActual> repayActualList = list(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                .eq(FundDirectFinancingRepayActual::getFinancingId, directFinancingId)
                .orderByAsc(FundDirectFinancingRepayActual::getPhase));
        if(CollectionUtil.isNotEmpty(repayActualList)){
            List<FundFinancingRepayEstimateExcelModel> excelModelList = repayActualList.stream().map(item -> {
                FundFinancingRepayEstimateExcelModel excelModel = new FundFinancingRepayEstimateExcelModel();
                excelModel.setRepayDate(item.getRepayDate());
                excelModel.setRepayPhase(item.getPhase());
                return excelModel;
            }).collect(Collectors.toList());
            List<FundDirectFinancingRepayActual> repayActualListNew = calculateCashFlow(directFinancingBaseInfo, excelModelList);
            if(repayActualListNew != null) {
                remove(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery().eq(FundDirectFinancingRepayActual::getFinancingId, directFinancingId));
                saveBatch(repayActualListNew);
            }
        }

    }

        /**
         * ABS/ABN 现金流核销后更新现金流数据
         * @param financingId
         * @param currencyCashFlowCode
         */
    @Transactional(rollbackFor = Throwable.class)
    public void updateNextPhase(Long financingId, String currencyCashFlowCode){
        String[] split = currencyCashFlowCode.split("-");
        int currencyPhase = Integer.parseInt(split[split.length -1]);

        FundDirectFinancingBaseInfo financingBaseInfo = directFinancingBaseInfoService.getById(financingId);
        List<FundDirectFinancingRepayActual> repayActualList = directFinancingRepayActualService.listByFinancingId(financingId);
        // 拿到实际核销记录
        FundReceiptRepayBaseInfo repayBaseInfo = receiptRepayBaseInfoService.getOne(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                .eq(FundReceiptRepayBaseInfo::getFinancingType, FinancingTypeEnum.DIRECT.name()));
        List<FundReceiptFlowDetail> flowDetailList = receiptFlowDetailService.listByReceiptRepayId(repayBaseInfo.getId());
        List<FundReceiptFlowDetail> currencyCashFlowDetail = flowDetailList.stream().filter(f -> Objects.equals(f.getCashFlowCode(), currencyCashFlowCode)).collect(Collectors.toList());

        Map<Integer, FundDirectFinancingRepayActual> repayActualMap = repayActualList.stream().collect(Collectors.toMap(FundDirectFinancingRepayActual::getPhase, Function.identity()));
        FundDirectFinancingRepayActual currencyRepayActual = repayActualMap.get(currencyPhase);
        FundDirectFinancingRepayActual nextRepayActual = repayActualMap.get(currencyPhase + 1);

        Long preRemainingPrincipleAmount = 0L;
        for (int i = currencyPhase - 1; i >= 0; i--) {
            FundDirectFinancingRepayActual repayActual = repayActualMap.get(i);
            if(repayActual.getRemainingPrincipleAmount() != null){
                preRemainingPrincipleAmount = repayActual.getRemainingPrincipleAmount();
                break;
            }
        }
        Long originPrincipleAmount = currencyRepayActual.getPrincipleAmount();
        // 更新当期预付差额 = 还款金额-实际核销本金-实际核销利息；
        long principalAmountSum = currencyCashFlowDetail.stream().filter(f -> f.getPrincipalAmount() != null).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum();
        long interestAmountSum = currencyCashFlowDetail.stream().filter(f -> f.getInterestAmount() != null).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum();
        Long currencyPrePay = Optional.ofNullable(currencyRepayActual.getRepayAmount()).orElse(0L) - principalAmountSum - interestAmountSum;
        currencyRepayActual.setPrincipleAmount(principalAmountSum);
        currencyRepayActual.setInterestAmount(interestAmountSum);
        currencyRepayActual.setRemainingPrincipleAmount(preRemainingPrincipleAmount - principalAmountSum);
        currencyRepayActual.setPrePayDifference(currencyPrePay);
        updateById(currencyRepayActual);

        /**
         * 更新核销下一期现金流的本金=原计划本金+上一期计划本金-上一期核销本金；
         * 更新核销下一期现金流的利息=上期剩余本金*票面加权平均利率*间隔天数
         * 更新核销下一期现金流的预付差额=还款金额-本金-利息
         */
        if(nextRepayActual != null){
            // 本金
            long principleAmount = originPrincipleAmount + Optional.ofNullable(nextRepayActual.getPrincipleAmount()).orElse(0L) - principalAmountSum;
            // 利息
            long gapDay = ChronoUnit.DAYS.between(currencyRepayActual.getRepayDate(), nextRepayActual.getRepayDate());
            BigDecimal averageCouponDayRate = BigDecimal.valueOf(financingBaseInfo.getAverageCouponRate()).divide(new BigDecimal("360000000"), 10, RoundingMode.HALF_UP);
            BigDecimal financingInterest = BigDecimal.valueOf(currencyRepayActual.getRemainingPrincipleAmount()).multiply(averageCouponDayRate).multiply(BigDecimal.valueOf(gapDay));
            // 预付差额
            long prepayDifference = Optional.ofNullable(nextRepayActual.getRepayAmount()).orElse(0L) - principleAmount - financingInterest.longValue();

            nextRepayActual.setPrincipleAmount(principleAmount);
            nextRepayActual.setInterestAmount(Util.mithrasLongDecimalTwo(financingInterest.longValue()));
            nextRepayActual.setPrePayDifference(prepayDifference);
            updateById(nextRepayActual);
        }

    }

    /**
     * ABS/ABN 底层合同还款计划变更时，同步更新变更日以后的现金流，本金按剩余本金分摊
     * @param contractId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void updateCashFlow(Long contractId) {
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(contractId);
        LocalDate changeDate = Optional.ofNullable(contractReceiptList.stream().max(Comparator.comparing(ContractReceipt::getUpdateTime)).get().getChangeDate()).orElse(LocalDate.now());

        List<FundDirectFinancingPledgeInfo> pledgeInfoList = directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(pledgeInfoList)) {
            return;
        }
        List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                .in(FundDirectFinancingBaseInfo::getId, pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()))
                .in(FundDirectFinancingBaseInfo::getDirectFinancingType, Arrays.asList(DirectFinancingType.ABS.name(), DirectFinancingType.ABN.name()))
                .notIn(FundDirectFinancingBaseInfo::getFinancingStatus, Arrays.asList(FundFinancingStatusEnum.SETTLE.name(), FundFinancingStatusEnum.CLOSE.name())));
        if (CollectionUtil.isEmpty(directFinancingBaseInfoList)) {
            return;
        }
        // 资产端合同通常只能被资金端关联一次
        FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingBaseInfoList.get(0);
        List<FundDirectFinancingPledgeInfo> pledgeInfoAllList = directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getFinancingId, directFinancingBaseInfo.getId()));
        // 获取关联合同变更日后的现金流
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByContractIds(pledgeInfoAllList.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
        List<FundDirectFinancingRepayActual> repayActualListAll = directFinancingRepayActualService.listByFinancingId(directFinancingBaseInfo.getId());
        List<FundDirectFinancingRepayActual> repayActualList = repayActualListAll.stream().filter(f -> !f.getRepayDate().isBefore(changeDate) && f.getPhase() != 0).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(repayActualList)) {
            return;
        }
        // 若存在已核销的期项，本次不做更新现金流处理
        if (repayActualList.stream().filter(f -> !Objects.equals(f.getWriteOffStatus(), CashFlowState.NO_WRITE_OFF.name())).count() > 0) {
            return;
        }
        // 起始日取变更日后的上一期
        Integer phase = repayActualList.get(0).getPhase();
        LocalDate calculateTimeStart = Optional.ofNullable(repayActualListAll.stream().filter(f -> f.getPhase() == phase - 1).collect(Collectors.toList())).map(m -> m.get(0).getRepayDate()).orElse(directFinancingBaseInfo.getCarryInterestTime());
        LocalDate lastTime = repayActualList.stream().max(Comparator.comparing(FundDirectFinancingRepayActual::getPhase)).map(FundDirectFinancingRepayActual::getRepayDate).get();
        long financingAmount = repayActualList.stream().mapToLong(FundDirectFinancingRepayActual::getPrincipleAmount).sum();
        LocalDate finalCalculateTimeStart = calculateTimeStart;

        long principalSum = contractRentActualList.stream().filter(f -> !f.getCashFlowDate().isBefore(finalCalculateTimeStart)
                && !f.getCashFlowDate().isAfter(lastTime)).mapToLong(ContractRentActual::getPrincipal).sum();
        long remainingAmount = financingAmount;


        for (FundDirectFinancingRepayActual repayActual : repayActualList) {
            // 获取当前期项时间期间内的底层合同对应的现金流
            List<ContractRentActual> rentActualList = new ArrayList<>();
            for (ContractRentActual item : contractRentActualList) {
                // 检查当前元素的现金流日期是否在指定范围内
                if (!item.getCashFlowDate().isBefore(calculateTimeStart) && item.getCashFlowDate().isBefore(repayActual.getRepayDate())) {
                    rentActualList.add(item);
                }
            }

            // 还款金额 = 计算日+起息日期间的底层合同的租金金额之和
            BigDecimal rentSum = rentActualList.stream().map(m -> BigDecimal.valueOf(m.getRent())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayActual.setRepayAmount(rentSum.longValue());

            // 本金 = 据底层合同的每期租金本金的分布比例将融资金额分摊至每期（某期本金=某期还款金额对应底层租金本金/租金之和*融资金额）
            long currencyPrincipal = rentActualList.stream().mapToLong(ContractRentActual::getPrincipal).sum();
            BigDecimal financingPrincipal = BigDecimal.valueOf(currencyPrincipal).divide(BigDecimal.valueOf(principalSum), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(financingAmount));
            Long principal = Util.mithrasLongDecimalTwo(financingPrincipal.longValue());
            repayActual.setPrincipleAmount(principal);

            // 利息 = 上期剩余本金*票面加权平均利率*间隔天数
            long gapDay = ChronoUnit.DAYS.between(calculateTimeStart, repayActual.getRepayDate());
            BigDecimal averageCouponDayRate = BigDecimal.valueOf(directFinancingBaseInfo.getAverageCouponRate()).divide(new BigDecimal("360000000"), 10, RoundingMode.HALF_UP);
            BigDecimal financingInterest = BigDecimal.valueOf(remainingAmount).multiply(averageCouponDayRate).multiply(BigDecimal.valueOf(gapDay));
            Long interest = Util.mithrasLongDecimalTwo(financingInterest.longValue());
            repayActual.setInterestAmount(interest);

            // 预付差额
            BigDecimal prePayDifference = rentSum.subtract(BigDecimal.valueOf(principal)).subtract(BigDecimal.valueOf(interest));
            repayActual.setPrePayDifference(prePayDifference.longValue());

            // 更新剩余本金
            remainingAmount = remainingAmount - principal;
            repayActual.setRemainingPrincipleAmount(remainingAmount);
            repayActual.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());

            calculateTimeStart = repayActual.getRepayDate();
        }
        if (CollectionUtil.isNotEmpty(repayActualList)) {
            long principleSum = repayActualList.stream().filter(f -> f.getPrincipleAmount() != null).mapToLong(FundDirectFinancingRepayActual::getPrincipleAmount).sum();
            long principleAll = directFinancingBaseInfo.getFinancingAmount() * 10000L;
            if (principleSum != principleAll) {
                // 最后一期要补回差额
                long difference = principleAll - principleSum;
                FundDirectFinancingRepayActual repayActual = repayActualList.get(repayActualList.size() - 1);
                repayActual.setPrincipleAmount(repayActual.getPrincipleAmount() + difference);
                repayActual.setRemainingPrincipleAmount(repayActual.getRemainingPrincipleAmount() - difference);
                repayActual.setPrePayDifference(repayActual.getPrePayDifference() - difference);
            }
        }
        directFinancingRepayActualService.updateBatchById(repayActualList);
    }


    private String generateCashFlowCode(String financingCode, Integer cashFlowPhase) {
        return String.format("%s-%03d", financingCode, cashFlowPhase);
    }

    private boolean isExistWrittenOffCashFlow(Long financingId) {
        LambdaQueryWrapper<FundReceiptRepayBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptRepayBaseInfo::getFinancingId, financingId);
        query.eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT");
        FundReceiptRepayBaseInfo receiptRepayBaseInfo = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).getOne(query);
        if (Objects.isNull(receiptRepayBaseInfo)) {
            return false;
        }
        LambdaQueryWrapper<FundReceiptRepayCashFlow> cashFlowQuery = Wrappers.lambdaQuery();
        cashFlowQuery.eq(FundReceiptRepayCashFlow::getReceiptRepayId, receiptRepayBaseInfo.getId());
        cashFlowQuery.gt(FundReceiptRepayCashFlow::getPhase, 0);
        cashFlowQuery.ne(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.NO_WRITE_OFF.name());
        return SpringUtil.getBean(FundReceiptRepayCashFlowService.class).count(cashFlowQuery) > 0;
    }


    /**
     * 新导入实际还款数据
     *
     * @param financingId 融资合同ID
     * @param inputStream Excel文件输入流
     * @param isCheck     是否校验数据[实际导入与系统计算结果对比]
     * @return 导入结果
     */
    @Transactional(rollbackFor = Throwable.class)
    public FundDirectFinancingRepayActualImportRSP importExcelActual(Long financingId, InputStream inputStream, Boolean isCheck) {
        // 查询已有的数据
        FundDirectFinancingBaseInfo financingBaseInfo = directFinancingBaseInfoService.getById(financingId);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        // 产品明细金额之和需等于融资金额
        List<FundDirectFinancingProductDetail> productDetailList = SpringUtil.getBean(FundDirectFinancingProductDetailService.class).listByFinancingId(financingId);
        long sum = productDetailList.stream().filter(e -> Objects.nonNull(e.getIssuanceAmount())).mapToLong(FundDirectFinancingProductDetail::getIssuanceAmount).sum();
        if (!Objects.equals(financingBaseInfo.getFinancingAmount(), sum)) {
            throw new MithrasException("产品发行金额合计不等于融资金额，请检查");
        }
        //excel数据校验
        List<FundFinancingRepayEstimateExcelModel> excelModelList = checkExcelModelList(inputStream,financingBaseInfo);
        //取数据库还款计划 确认那些期次已核销/部分核销 这些期次不允许修改
        List<FundDirectFinancingRepayActual> existList = this.listByFinancingId(financingId);
        //已核销+部分核销期次
        Map<Integer, FundDirectFinancingRepayActual> alreadyActualMap = new HashMap<>();
        //核销中
        Map<Integer, FundDirectFinancingRepayActual> ingActualMap = new HashMap<>();
        //全量
        Map<Integer, FundDirectFinancingRepayActual> repayActualMap = new HashMap<>();
        Map<String, FundReceiptRepayCashFlow> cashFlowMap = new HashMap<>();
        Long alreadyWriteOff = 0l;
        if (!CollectionUtil.isEmpty(existList)) {
            cashFlowMap = receiptRepayCashFlowService.queryByFinancingId(financingId, FinancingTypeEnum.DIRECT);
            for (FundDirectFinancingRepayActual t : existList) {
                // 取对应现金流核销状态
                String writeOffState = Optional.ofNullable(cashFlowMap.get(t.getCashFlowCode())).map(FundReceiptRepayCashFlow::getWriteOffState).orElse(CashFlowState.NO_WRITE_OFF.name());
                if (!Arrays.asList(CashFlowState.WRITE_OFF_ING.name(), CashFlowState.NO_WRITE_OFF.name()).contains(writeOffState)) {
                    alreadyActualMap.put(t.getPhase(), t);
                    //已核销期次本金汇总
                    alreadyWriteOff = alreadyWriteOff + Optional.ofNullable(t.getPrincipleAmount()).orElse(0L);
                }
                if(Objects.equals(CashFlowState.WRITE_OFF_ING.name(),writeOffState)){
                    ingActualMap.put(t.getPhase(), t);
                }
                repayActualMap.put(t.getPhase(), t);
            }
        }
        long remainingAmount = financingBaseInfo.getFinancingAmount() * 10000L;
        if (alreadyWriteOff >= remainingAmount) {
            throw new MithrasException("当前已核销本金>= 融资金额，不允许导入");
        }


        // ABS，ABN仅导入日期 自动测算现金流
        if (Arrays.asList(DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name()).contains(financingBaseInfo.getDirectFinancingType())) {
            return importExcelActualAbsOrAbn(financingBaseInfo, excelModelList, alreadyActualMap,ingActualMap);
        }
        // 其他类型 基本已excel为准
        FundFinancingRepayEstimateExcelModel zeroPhaseRePayModel = excelModelList.get(0);
        List<String> errorPhaseList = new ArrayList<>();
        BigDecimal remainingPrincipleAmount = zeroPhaseRePayModel.getRemainingPrincipleAmount();
        LocalDate repayDate = zeroPhaseRePayModel.getRepayDate();
        //模板导入利息
        BigDecimal interestAmountSum = BigDecimal.ZERO;
        //系统计算利息
        BigDecimal systemInterestAmountSum = BigDecimal.ZERO;
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            //校验每一期的还款金额与本金、利息之和是否一致
            BigDecimal excelPrincipleAmt = Optional.ofNullable(excelModel.getPrincipleAmount()).orElse(BigDecimal.ZERO);
            BigDecimal excelInterestAmt = Optional.ofNullable(excelModel.getInterestAmount()).orElse(BigDecimal.ZERO);
            BigDecimal excelRepayAmt = Optional.ofNullable(excelModel.getRepayAmount()).orElse(BigDecimal.ZERO);
            Integer repayPhase = excelModel.getRepayPhase();
            if ((excelPrincipleAmt.add(excelInterestAmt)).compareTo(excelRepayAmt) != 0) {
                errorPhaseList.add(String.format("第%s期还款金额与本金、利息之和不一致，请检查", repayPhase));
            }
            // 每一期的剩余本金改为系统测算
            if (repayPhase > 0) {
                // 系统计算的利息合计值 ∑(剩余本金*对应计息天数*合同利率/360)
                long gapDay = ChronoUnit.DAYS.between(repayDate, excelModel.getRepayDate());
                systemInterestAmountSum = systemInterestAmountSum.add(remainingPrincipleAmount.multiply(BigDecimal.valueOf(gapDay)));
                interestAmountSum = interestAmountSum.add(Optional.ofNullable(excelModel.getInterestAmount()).orElse(BigDecimal.ZERO));
                // 系统计算的剩余本金 = 上一期剩余本金 - 本期本金  这里以系统计算为准
                remainingPrincipleAmount = remainingPrincipleAmount.subtract(Optional.ofNullable(excelModel.getPrincipleAmount()).orElse(BigDecimal.ZERO));
                excelModel.setRemainingPrincipleAmount(remainingPrincipleAmount);
                repayDate = excelModel.getRepayDate();
            }
        }
        Assert.isTrue(CollectionUtil.isEmpty(errorPhaseList), () -> MithrasException.newException(String.join(",", errorPhaseList)));
        if (isCheck != null && isCheck) {
            // 校验导入的每期利息之和与系统计算的利息合计值 ∑(剩余本金*对应计息天数*合同利率/360) 的差额是否大于10元 不知道为啥是汇总计算。
            BigDecimal averageCouponRate = Optional.ofNullable(financingBaseInfo.getAverageCouponRate()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);
            BigDecimal systemCalculateResult = systemInterestAmountSum.multiply(averageCouponRate).divide(BigDecimal.valueOf(360000000), 10, RoundingMode.HALF_UP);
            FundDirectFinancingRepayActualImportRSP rsp = new FundDirectFinancingRepayActualImportRSP();
            //返回差额
            rsp.setInterestDiff(Util.toMithrasUnit(systemCalculateResult.subtract(interestAmountSum).abs()));
            return rsp;
        }
        List<FundDirectFinancingRepayActual> fundFinancingRepayActualList = calculateCashFlowOthers(financingBaseInfo, excelModelList, alreadyActualMap, repayActualMap,ingActualMap);
        saveOrUpdateBatch(fundFinancingRepayActualList);
        // map中如果有剩余说明是需要删除的
        if (CollectionUtil.isNotEmpty(repayActualMap)) {
            Collection<FundDirectFinancingRepayActual> repayActualList = repayActualMap.values();
            List<Long> ids = repayActualList.stream().map(FundDirectFinancingRepayActual::getId).collect(Collectors.toList());
            removeByIds(ids);
        }
        directFinancingBaseInfoService.updateFinancingCost(financingId);
        // 现金流最后一期还款日为到期日
        directFinancingBaseInfoService.updateDurationTime(financingBaseInfo.getId(), fundFinancingRepayActualList.get(fundFinancingRepayActualList.size() - 1).getRepayDate());
        return null;
    }


    /**
     * 计算现金流其他项
     *
     * @param financingBaseInfo 融资基础信息
     * @param excelModelList    还款计划列表
     * @param alreadyActualMap   已核销的还款计划
     * @param repayActualMap    实际还款计划map
     * @return 现金流其他项列表
     */
    public List<FundDirectFinancingRepayActual> calculateCashFlowOthers(FundDirectFinancingBaseInfo financingBaseInfo, List<FundFinancingRepayEstimateExcelModel> excelModelList,
                                                                        Map<Integer, FundDirectFinancingRepayActual> alreadyActualMap, Map<Integer, FundDirectFinancingRepayActual> repayActualMap,Map<Integer, FundDirectFinancingRepayActual> ingActualMap) {
        Long financingId = financingBaseInfo.getId();
        //转换单位
        BigDecimal remainingPrincipleAmount = excelModelList.get(0).getRemainingPrincipleAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE));
        List<FundDirectFinancingRepayActual> fundFinancingRepayActualList = new ArrayList<>();
        for (FundFinancingRepayEstimateExcelModel item : excelModelList) {
            FundDirectFinancingRepayActual repayActual = new FundDirectFinancingRepayActual();
            repayActual.setFinancingId(financingId);
            repayActual.setCashFlowCode(generateCashFlowCode(financingBaseInfo.getFinancingCode(), item.getRepayPhase()));
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
            //数据库是否存在
            FundDirectFinancingRepayActual existRepay = repayActualMap.get(item.getRepayPhase());
            if (Objects.nonNull(existRepay)) {
                repayActual.setId(existRepay.getId());
                repayActualMap.remove(item.getRepayPhase());
            }
            //存在已核销金额的 按核销的为准
            FundDirectFinancingRepayActual existAlready = alreadyActualMap.get(item.getRepayPhase());
            if (Objects.nonNull(existAlready)) {
                repayActual.setRepayDate(existAlready.getRepayDate());
                repayActual.setId(existAlready.getId());
                repayActual.setPrincipleAmount(existAlready.getPrincipleAmount());
                repayActual.setInterestAmount(existAlready.getInterestAmount());
                repayActual.setRepayAmount(existAlready.getRepayAmount());
                repayActual.setWriteOffStatus(existAlready.getWriteOffStatus());
                alreadyActualMap.remove(item.getRepayPhase());
            }
            FundDirectFinancingRepayActual ingRepay = ingActualMap.get(item.getRepayPhase());
            if (Objects.nonNull(ingRepay)){
                repayActual.setWriteOffStatus(ingRepay.getWriteOffStatus());
            }
            long principleAmount = Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L);
            remainingPrincipleAmount = remainingPrincipleAmount.subtract(new BigDecimal(principleAmount));
            //重新计算
            repayActual.setRemainingPrincipleAmount(remainingPrincipleAmount.longValue());
            fundFinancingRepayActualList.add(repayActual);
        }
        if (CollectionUtil.isNotEmpty(alreadyActualMap)) {
            throw new MithrasException(String.format("已核销期次不允许删除，请检查还款日期等参数"));
        }
        //校验后一期日期 ≥ 前一期日期
        for (int i = 1; i < fundFinancingRepayActualList.size(); i++) {
            // 获取前一期和当前期的模型
            FundDirectFinancingRepayActual pre = fundFinancingRepayActualList.get(i - 1);
            FundDirectFinancingRepayActual curr = fundFinancingRepayActualList.get(i);
            // 获取前后两期的日期（以Date为例，其他类型见注释）
            LocalDate prevDate = pre.getRepayDate();
            LocalDate currDate = curr.getRepayDate();
            // 核心判断：当前期日期 < 前一期日期 → 抛出异常
            if (currDate.isBefore(prevDate)) {
                throw new MithrasException(String.format("已核销部分不允许修改，导致存在后续期次的还款日期小于前面期次的还款日期，请检查还款日期等参数"));
            }
        }
        return fundFinancingRepayActualList;
    }


    /**
     * 导入ABS/ABN融资还款实际数据
     */
    private FundDirectFinancingRepayActualImportRSP importExcelActualAbsOrAbn(FundDirectFinancingBaseInfo financingBaseInfo,
                                                                              List<FundFinancingRepayEstimateExcelModel> excelModelList,
                                                                              Map<Integer, FundDirectFinancingRepayActual> alreadyActualMap,
                                                                              Map<Integer, FundDirectFinancingRepayActual> ingActualMap ) {
        Long financingId = financingBaseInfo.getId();
        //测算
        List<FundDirectFinancingRepayActual> repayActualList = calculateCashFlowAbsOrAbn(financingBaseInfo, excelModelList, alreadyActualMap,ingActualMap);
        //删除之前数据
        remove(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery().eq(FundDirectFinancingRepayActual::getFinancingId, financingId));
        saveBatch(repayActualList);
        FundDirectFinancingRepayActualImportRSP rsp = new FundDirectFinancingRepayActualImportRSP();
        rsp.setInterestDiff(0L);
        // 按照产品明细进行拆分
        fundDirectFinancingRepayActualSplitService.trySplit(financingId);
        return rsp;
    }


    /**
     * abc/abn现金流测算
     *
     * @param financingBaseInfo 直融基础信息
     * @param excelModelList    导入数据
     * @param alreadyActualMap   已核销期次
     * @return
     */
    public List<FundDirectFinancingRepayActual> calculateCashFlowAbsOrAbn(FundDirectFinancingBaseInfo financingBaseInfo, List<FundFinancingRepayEstimateExcelModel> excelModelList,
                                                                          Map<Integer, FundDirectFinancingRepayActual> alreadyActualMap,
                                                                          Map<Integer, FundDirectFinancingRepayActual> ingActualMap ) {

        Assert.notNull(financingBaseInfo.getCarryInterestTime(), () -> MithrasException.newException("合同起息日不存在"));
        FundDirectFinancingAssetPoolDetailRSP assetPoolDetail = fundDirectFinancingAssetPoolService.detail(financingBaseInfo.getId());
        Assert.notNull(assetPoolDetail, () -> MithrasException.newException("资产池信息不存在"));
        // 封包日 = 第0期还款日期
        LocalDate packageDate = assetPoolDetail.getPackageDate();
        Assert.notNull(packageDate, () -> MithrasException.newException("封包日不存在"));
        Assert.notNull(financingBaseInfo.getAverageCouponRate(), () -> MithrasException.newException("票面加权利率不存在"));
        Assert.isFalse(excelModelList.stream().anyMatch(f -> f.getRepayPhase() == null || f.getRepayDate() == null), () -> MithrasException.newException("期项和时间不得为空"));
        //关联合同明细（质押明细）
        List<FundDirectFinancingPledgeInfo> pledgeInfoList = directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                .eq(FundDirectFinancingPledgeInfo::getFinancingId, financingBaseInfo.getId()));
        Assert.isTrue(CollectionUtil.isNotEmpty(pledgeInfoList), () -> MithrasException.newException("关联合同不存在"));
        // 获取关联合同的现金流
        List<ContractRentActual> contractRentActualList = contractRentActualService.listByContractIds(pledgeInfoList.stream().map(FundDirectFinancingPledgeInfo::getContractId).collect(Collectors.toList()));
        //记录各期项的时间、金额初始化起息日
        LocalDate calculateTimeStart = financingBaseInfo.getCarryInterestTime();
        //取最大期限
        FundFinancingRepayEstimateExcelModel excelModelLastPhase = excelModelList.stream().max(Comparator.comparing(FundFinancingRepayEstimateExcelModel::getRepayPhase)).get();
        //最后一期还款日-计算日-- 后续计算调整
        LocalDate lastTime = Optional.ofNullable(financingBaseInfo.getCalculateDay())
                .map(calculateDay -> excelModelLastPhase.getRepayDate().minusDays(calculateDay))
                .orElse(excelModelLastPhase.getRepayDate());
        // 目标截止日期 = 最后一期还款日-计算日
        final LocalDate targetDate = lastTime;
        //取现金流日期 > 资产池封包日期 且 现金流日期≤ 目标截止日期  的本金求和
        long principalSum = contractRentActualList.stream().filter(f -> f.getCashFlowDate().isAfter(packageDate)
                && !f.getCashFlowDate().isAfter(targetDate)).mapToLong(ContractRentActual::getPrincipal).sum();
        //融资金额*10000  因为直融表的金额有是元作为单位，进度需要所以转换。
        long remainingAmount = financingBaseInfo.getFinancingAmount() * 10000L;
        List<FundDirectFinancingRepayActual> newRepayActualList = new ArrayList<>();
        //组装第0期
        FundDirectFinancingRepayActual repayActualZero = new FundDirectFinancingRepayActual();
        repayActualZero.setPhase(0);
        repayActualZero.setFinancingId(financingBaseInfo.getId());
        repayActualZero.setRepayDate(financingBaseInfo.getCarryInterestTime());
        repayActualZero.setRemainingPrincipleAmount(remainingAmount);
        repayActualZero.setCashFlowCode(generateCashFlowCode(financingBaseInfo.getFinancingCode(), repayActualZero.getPhase()));
        newRepayActualList.add(repayActualZero);
        //一期一期计算
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            if (excelModel.getRepayPhase().equals(0)) {
                continue;
            }
            //按起租日计算一版
            FundDirectFinancingRepayActual repayActual = getRepayActual2(financingBaseInfo, excelModel, contractRentActualList, alreadyActualMap,ingActualMap, calculateTimeStart, remainingAmount, principalSum);
            if (excelModel.getRepayPhase().equals(1)) {
                //第一期计算自封宝日开始 获取本金/应还总额/预付差额
                FundDirectFinancingRepayActual repayActualFirst = getRepayActual2(financingBaseInfo, excelModel, contractRentActualList, alreadyActualMap,ingActualMap, packageDate, remainingAmount, principalSum);
                if (ObjectUtil.isNotEmpty(repayActualFirst)) {
                    repayActual.setRepayAmount(repayActualFirst.getRepayAmount());
                    repayActual.setPrincipleAmount(repayActualFirst.getPrincipleAmount());
                    repayActual.setRemainingPrincipleAmount(repayActualFirst.getRemainingPrincipleAmount());
                    // 调整预付差额
                    long prePayDifference = Optional.ofNullable(repayActual.getRepayAmount()).orElse(0L) - Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) - Optional.ofNullable(repayActual.getInterestAmount()).orElse(0L);
                    repayActual.setPrePayDifference(prePayDifference);
                }
            }
            //已核销因为日期取的是之前的，那么起息日也许已已核销的日期为准计算下一期次
            calculateTimeStart = Optional.ofNullable(financingBaseInfo.getCalculateDay())
                    .map(calculateDay -> repayActual.getRepayDate().minusDays(calculateDay))
                    .orElse(repayActual.getRepayDate());
            // 重新赋值 剩余未还本金
            remainingAmount = Optional.ofNullable(repayActual.getRemainingPrincipleAmount()).orElse(0L);
            newRepayActualList.add(repayActual);
        }
        //新的实际租金
        long principleSum = newRepayActualList.stream().filter(f -> f.getPrincipleAmount() != null).mapToLong(FundDirectFinancingRepayActual::getPrincipleAmount).sum();
        long principleAll = financingBaseInfo.getFinancingAmount() * 10000L;
        FundDirectFinancingRepayActual repayActual = newRepayActualList.get(newRepayActualList.size() - 1);
        //轧差处理
        if (principleSum != principleAll) {
            long difference = principleAll - principleSum;
            repayActual.setPrincipleAmount(Optional.ofNullable(repayActual.getPrincipleAmount()).orElse(0L) + difference);
            repayActual.setPrePayDifference(Optional.ofNullable(repayActual.getPrePayDifference()).orElse(0L) - difference);
        }
        //校验后一期日期 ≥ 前一期日期
        for (int i = 1; i < newRepayActualList.size(); i++) {
            // 获取前一期和当前期的模型
            FundDirectFinancingRepayActual pre = newRepayActualList.get(i - 1);
            FundDirectFinancingRepayActual curr = newRepayActualList.get(i);
            // 获取前后两期的日期（以Date为例，其他类型见注释）
            LocalDate prevDate = pre.getRepayDate();
            LocalDate currDate = curr.getRepayDate();
            // 核心判断：当前期日期 < 前一期日期 → 抛出异常    可能存在起息日、封包日在第一期还款日之后?
            if (currDate.isBefore(prevDate) && curr.getPhase() != 1 ) {
                throw new MithrasException(String.format("期次 %d 的还款日期 %s 小于前面期次 %d 的还款日期 %s，请检查", curr.getPhase(), currDate, pre.getPhase(), prevDate));
            }
            if (alreadyActualMap.containsKey(curr.getPhase())) {
                alreadyActualMap.remove(curr.getPhase());
            }
        }
        if (CollectionUtil.isNotEmpty(alreadyActualMap)) {
            throw new MithrasException(String.format("已核销部分不允许修改，请检查还款日期等参数"));
        }
        // 现金流最后一期还款日为到期日
        directFinancingBaseInfoService.updateDurationTime(financingBaseInfo.getId(), repayActual.getRepayDate());
        return newRepayActualList;
    }


    /**
     * 组装直融-实际还款表明细数据
     * 其中单位要注意，1、融资表数据库档位是元   而还款表是 元*10000
     *
     * @param financingBaseInfo      直融信息
     * @param excelModel             excle 导入数据
     * @param contractRentActualList 关联合同收款明细
     * @param calculateTimeStart     起息日
     * @param remainingAmount        融资金额
     * @param principalSum           现金流日期 > 资产池封包日期 且 现金流日期≤ 目标截止日期  的本金求和
     * @param alreadyActualMap        已核销、部分核销数组
     *
     */
    private FundDirectFinancingRepayActual getRepayActual2(FundDirectFinancingBaseInfo financingBaseInfo, FundFinancingRepayEstimateExcelModel excelModel,
                                                           List<ContractRentActual> contractRentActualList, Map<Integer, FundDirectFinancingRepayActual> alreadyActualMap,
                                                           Map<Integer, FundDirectFinancingRepayActual> ingActualMap ,
                                                           LocalDate calculateTimeStart,
                                                           long remainingAmount, long principalSum) {
        FundDirectFinancingRepayActual repayActual = new FundDirectFinancingRepayActual();
        //期次
        Integer repayPhase = excelModel.getRepayPhase();
        repayActual.setFinancingId(financingBaseInfo.getId());
        repayActual.setRepayDate(excelModel.getRepayDate());
        repayActual.setPhase(repayPhase);
        repayActual.setCashFlowCode(generateCashFlowCode(financingBaseInfo.getFinancingCode(), repayPhase));
        repayActual.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());
        FundDirectFinancingRepayActual alreadyActual = alreadyActualMap.get(repayPhase);
        FundDirectFinancingRepayActual ingActual = alreadyActualMap.get(repayPhase);
        //已核销、部分核销 那么以核销为准，修改剩余未还本金
        if (Objects.nonNull(alreadyActual)) {
            repayActual.setRepayDate(alreadyActual.getRepayDate());
            repayActual.setRepayAmount(alreadyActual.getRepayAmount());
            repayActual.setPrincipleAmount(alreadyActual.getPrincipleAmount());
            repayActual.setInterestAmount(alreadyActual.getInterestAmount());
            repayActual.setPrePayDifference(alreadyActual.getPrePayDifference());
            //剩余未还本金
            remainingAmount = remainingAmount - repayActual.getPrincipleAmount();
            repayActual.setRemainingPrincipleAmount(remainingAmount);
            repayActual.setWriteOffStatus(repayActual.getWriteOffStatus());
            return repayActual;
        }
        if (Objects.nonNull(ingActual)){
            repayActual.setWriteOffStatus(CashFlowState.WRITE_OFF_ING.name());
        }
        repayActual.setRemainingPrincipleAmount(remainingAmount);
        // 取当前期项时间期间内的底层合同对应的现金流  取还款日在【上一期截止日-当期截止日）的租金   如果存在上一期截止日比还款日期之前就不管了
        List<ContractRentActual> rentActualList = new ArrayList<>();
        for (ContractRentActual item : contractRentActualList) {
            LocalDate endDate = Optional.ofNullable(financingBaseInfo.getCalculateDay())
                    .map(calculateDay -> excelModel.getRepayDate().minusDays(calculateDay))
                    .orElse(excelModel.getRepayDate());
            if (!item.getCashFlowDate().isBefore(calculateTimeStart) && item.getCashFlowDate().isBefore(endDate)) {
                rentActualList.add(item);
            }
        }
        //如果没有 就无需设置
        if (CollectionUtil.isEmpty(rentActualList)) {
            return repayActual;
        }
        // 还款金额 = 计算日+起息日期间的底层合同的租金金额之和
        BigDecimal rentSum = rentActualList.stream().map(m -> BigDecimal.valueOf(m.getRent())).reduce(BigDecimal.ZERO, BigDecimal::add);
        repayActual.setRepayAmount(rentSum.longValue());
        // 金额需要控制精度，最后的入库数据只可以精确到分
        // 本金 = 起息日-计算日的底层合同租金的本金之和/所有合同全量租金的本金之和*融资金额）
        long currencyPrincipal = rentActualList.stream().mapToLong(ContractRentActual::getPrincipal).sum();
        //这里需要转换*10000  因为直融表的金额有是元作为单位，这里和计划表的 元*10000单位不一致 所以需要转换。
        BigDecimal financingPrincipal = BigDecimal.valueOf(currencyPrincipal).divide(BigDecimal.valueOf(principalSum), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(financingBaseInfo.getFinancingAmount() * 10000));
        Long principal = Util.mithrasLongDecimalTwo(financingPrincipal.longValue());
        repayActual.setPrincipleAmount(principal);
        // 利息 = 上期剩余本金* （每一天的利率）*间隔天数
        //间隔天数
        long gapDay = ChronoUnit.DAYS.between(calculateTimeStart, excelModel.getRepayDate());
        //每一天的利率= 票面加权平均利率/360天/100百分比单位/10000 转换约定
        BigDecimal averageCouponDayRate = BigDecimal.valueOf(financingBaseInfo.getAverageCouponRate()).divide(new BigDecimal("360000000"), 10, RoundingMode.HALF_UP);
        BigDecimal financingInterest = BigDecimal.valueOf(remainingAmount).multiply(averageCouponDayRate).multiply(BigDecimal.valueOf(gapDay));
        Long interest = Util.mithrasLongDecimalTwo(financingInterest.longValue());
        repayActual.setInterestAmount(interest);
        // 预付差额
        BigDecimal prePayDifference = rentSum.subtract(BigDecimal.valueOf(principal)).subtract(BigDecimal.valueOf(interest));
        repayActual.setPrePayDifference(prePayDifference.longValue());
        // 更新剩余本金
        remainingAmount = remainingAmount - principal;
        repayActual.setRemainingPrincipleAmount(remainingAmount);
        repayActual.setWriteOffStatus(CashFlowState.NO_WRITE_OFF.name());
        return repayActual;
    }


    //检查导入数据是否符合要求
    private List<FundFinancingRepayEstimateExcelModel> checkExcelModelList(InputStream inputStream, FundDirectFinancingBaseInfo financingBaseInfo) {
        //数据装换
        List<FundFinancingRepayEstimateExcelModel> excelModelList = financingRepayImporter.parse(inputStream);
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("没有从导入文件中获取到符合条件的数据，请检查文件"));
        Set<Integer> phaseSet = new HashSet<>();
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            Assert.notNull(excelModel.getRepayPhase(), () -> MithrasException.newException("期项不得为空"));
            Assert.notNull(excelModel.getRepayDate(), () -> MithrasException.newException("日期不得为空"));
            if (!phaseSet.add(excelModel.getRepayPhase())) {
                throw new MithrasException("期项不能重复，请检查");
            }
        }
        //按其次排序
        List<FundFinancingRepayEstimateExcelModel> excelModels = excelModelList.stream().sorted(Comparator.comparing(FundFinancingRepayEstimateExcelModel::getRepayPhase)).collect(Collectors.toList());
        if (Objects.isNull(excelModels.get(0)) || !Objects.equals(financingBaseInfo.getFinancingAmount(), excelModels.get(0).getRemainingPrincipleAmount().longValue())) {
            throw new MithrasException("第0期现金流不存在或现金流金额与融资金额不一致，请检查");
        }
        //  校验后一期日期 ≥ 前一期日期
        for (int i = 1; i < excelModels.size(); i++) {
            // 获取前一期和当前期的模型
            FundFinancingRepayEstimateExcelModel prevModel = excelModels.get(i - 1);
            FundFinancingRepayEstimateExcelModel currModel = excelModels.get(i);
            // 获取前后两期的日期（以Date为例，其他类型见注释）
            LocalDate prevDate = prevModel.getRepayDate();
            LocalDate currDate = currModel.getRepayDate();
            // 核心判断：当前期日期 < 前一期日期 → 抛出异常
            if (currDate.isBefore(prevDate) && currModel.getRepayPhase() != 1) {
                throw new MithrasException(String.format("存在后续期次的还款日期小于前面期次的还款日期，请检查"));
            }
        }
        return excelModels;
    }

}
