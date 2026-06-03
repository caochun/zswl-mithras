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
import cn.zswltech.mithras.dto.fund.financing.repay.FundFinancingRepayEstimateListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.exporter.FundFinancingRepayEstimateExporter;
import cn.zswltech.mithras.service.excel.importer.FundFinancingRepayImporter;
import cn.zswltech.mithras.service.excel.model.FundFinancingRepayEstimateExcelModel;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingRepayEstimateMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayEstimate;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingRepayEstimateLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingRepayEstimateLibService;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingRepayEstimateLibHandler;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class FundFinancingRepayEstimateService extends ServiceImpl<FundFinancingRepayEstimateMapper, FundFinancingRepayEstimate> {
    @Resource
    private FundFinancingRepayImporter financingRepayImporter;
    @Resource
    private FundFinancingRepayEstimateExporter financingRepayEstimateExporter;
    @Resource
    private FundFinancingRepayEstimateLibService financingRepayEstimateLibService;
    @Resource
    private FundFinancingRepayEstimateLibHandler financingRepayEstimateLibHandler;
    @Resource
    private FundFinancingPlanService financingPlanService;

    public List<FundFinancingRepayEstimateListRSP> list(SingleFinancingIdREQ req) {
        List<FundFinancingRepayEstimate> dataList;
        if (StrUtil.isBlank(req.getVersion())) {
            dataList = this.listByFinancingId(req.getFinancingId());
        } else {
            List<FundFinancingRepayEstimateLib> libList = financingRepayEstimateLibService.listByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            dataList = libList.stream().map(financingRepayEstimateLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
        if (CollectionUtil.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        return this.convertToRSPList(dataList);
    }

    public List<FundFinancingRepayEstimate> listByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundFinancingRepayEstimate> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingRepayEstimate::getFinancingId, financingId);
        query.orderByAsc(FundFinancingRepayEstimate::getPhase);
        return this.list(query);
    }

    public List<FundFinancingRepayEstimateListRSP> convertToRSPList(List<FundFinancingRepayEstimate> financingRepayEstimateList) {
        return financingRepayEstimateList
                .stream()
                .map(item -> {
                    FundFinancingRepayEstimateListRSP rsp = new FundFinancingRepayEstimateListRSP();
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
    public FundFinancingRepayActualImportRSP importExcel(Long financingId, InputStream inputStream, Boolean isCheck) {
        List<FundFinancingRepayEstimateExcelModel> excelModelList = financingRepayImporter.parse(inputStream);
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("没有从导入文件中获取到符合条件的数据，请检查文件"));
        for (FundFinancingRepayEstimateExcelModel excelModel : excelModelList) {
            Assert.notNull(excelModel.getRepayPhase(), () -> MithrasException.newException("期项不得为空"));
            Assert.notNull(excelModel.getRepayDate(), () -> MithrasException.newException("日期不得为空"));
        }
        Assert.isTrue(excelModelList.stream().anyMatch(f -> f.getRepayPhase() == 0), () -> MithrasException.newException("缺少第0期"));
        // 校验金额
        BigDecimal totalAmount = excelModelList.stream()
                .map(FundFinancingRepayEstimateExcelModel::getPrincipleAmount)
                .map(v -> {
                    if (v == null) {
                        return BigDecimal.ZERO;
                    }
                    return v;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
            BigDecimal contractRate = Optional.ofNullable(plan.getLprRatePercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(plan.getLprAddPercent()).map(BigDecimal::new).orElse(BigDecimal.ZERO));
            BigDecimal systemCalculateResult = systemInterestAmount.multiply(contractRate).divide(BigDecimal.valueOf(360000000), 10, RoundingMode.HALF_UP);
            FundFinancingRepayActualImportRSP rsp = new FundFinancingRepayActualImportRSP();
            rsp.setInterestDiff(Util.toMithrasUnit(systemCalculateResult.subtract(interestAmountSum).abs()));
            return rsp;
        }

        // 查询已有的数据
        List<FundFinancingRepayEstimate> existList = this.listByFinancingId(financingId);
        Map<Integer, FundFinancingRepayEstimate> repayEstimateMap = existList.stream().collect(Collectors.toMap(FundFinancingRepayEstimate::getPhase, e -> e));
        List<FundFinancingRepayEstimate> fundFinancingRepayEstimateList = excelModelList.stream().map(item -> {
            item.check();
            FundFinancingRepayEstimate repayEstimate = new FundFinancingRepayEstimate();
            repayEstimate.setFinancingId(financingId);
            repayEstimate.setRepayDate(item.getRepayDate());
            repayEstimate.setPhase(item.getRepayPhase());
            if (Objects.nonNull(item.getPrincipleAmount())) {
                repayEstimate.setPrincipleAmount(item.getPrincipleAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (Objects.nonNull(item.getInterestAmount())) {
                repayEstimate.setInterestAmount(item.getInterestAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (Objects.nonNull(item.getRepayAmount())) {
                repayEstimate.setRepayAmount(item.getRepayAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            if (Objects.nonNull(item.getRemainingPrincipleAmount())) {
                repayEstimate.setRemainingPrincipleAmount(item.getRemainingPrincipleAmount().multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue());
            }
            // 判断对应期项是否已存在数据
            FundFinancingRepayEstimate exist = repayEstimateMap.get(item.getRepayPhase());
            if (Objects.nonNull(exist)) {
                repayEstimate.setId(exist.getId());
                repayEstimateMap.remove(item.getRepayPhase());
            }
            return repayEstimate;
        }).collect(Collectors.toList());
        this.saveOrUpdateBatch(fundFinancingRepayEstimateList);
        // map中如果有剩余说明是需要删除的
        if (CollectionUtil.isNotEmpty(repayEstimateMap)) {
            Collection<FundFinancingRepayEstimate> repayActualList = repayEstimateMap.values();
            List<Long> ids = repayActualList.stream().map(FundFinancingRepayEstimate::getId).collect(Collectors.toList());
            this.removeByIds(ids);
        }
        financingPlanService.updateFinancingCost(financingId);
        return null;
    }

    public void exportExcel(OutputStream outputStream, Long financingId) {
        List<FundFinancingRepayEstimate> repayEstimateList = this.listByFinancingId(financingId);
        Assert.notEmpty(repayEstimateList, () -> MithrasException.newException("没有可导出的数据"));
        List<FundFinancingRepayEstimateExcelModel> excelModelList = repayEstimateList.stream().map(item -> {
            FundFinancingRepayEstimateExcelModel excelModel = new FundFinancingRepayEstimateExcelModel();
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
        financingRepayEstimateExporter.exportExcel(excelModelList, outputStream);
    }
}
