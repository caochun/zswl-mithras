package cn.zswltech.mithras.kpi.excel.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceRecordInfo;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceMainInfoService;
import cn.zswltech.mithras.kpi.service.KpiPerformanceRecordInfoService;
import cn.zswltech.mithras.kpi.excel.model.CompanyExcelModel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/7/1/12:02
 * @description
 */
@Slf4j
@Component
public class CompanyListener extends AnalysisEventListener<CompanyExcelModel> {
    private boolean isFirst = true;
    @Setter
    private Long mainId;
    @Setter
    private Long year;
    @Resource
    private KpiPerformanceBaseInfoService performanceBaseInfoService;
    @Resource
    private KpiPerformanceRecordInfoService performanceRecordInfoService;
    @Resource
    private KpiPerformanceMainInfoService performanceMainInfoService;

    @Override
    public void invoke(CompanyExcelModel companyExcelModel, AnalysisContext analysisContext) {
        PerformanceBaseInfo performanceBaseInfo = new PerformanceBaseInfo();
        performanceBaseInfo.setMainId(mainId);
        String businessType = Optional.ofNullable(BusinessTypeEnum.getByDisplay(companyExcelModel.getBusinessType()))
                .map(Enum::name).orElse(null);
        performanceBaseInfo.setBusinessType(businessType);
        Long assetBalanceTarget = Optional.ofNullable(companyExcelModel.getAssetBalanceTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setAssetBalanceTarget(assetBalanceTarget);
        performanceBaseInfo.setYear(year);
        Long advertisingAmount = Optional.ofNullable(companyExcelModel.getInvestmentTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setAdvertisingAmount(advertisingAmount);
        Long revenueTarget = Optional.ofNullable(companyExcelModel.getRevenueTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setRevenueTarget(revenueTarget);
        Long profitTarget = Optional.ofNullable(companyExcelModel.getProfitTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setProfitTarget(profitTarget);
        Long consultingFeeIncome =  Optional.ofNullable(companyExcelModel.getConsultingFeeIncome())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setConsultingFeeIncome(consultingFeeIncome);
        Long interestIncome =  Optional.ofNullable(companyExcelModel.getInterestIncome())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setInterestIncome(interestIncome);
        Long bizFee =  Optional.ofNullable(companyExcelModel.getBizFee())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBizFee(bizFee);
        Long businessTripFee =  Optional.ofNullable(companyExcelModel.getBusinessTripFee())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBusinessTripFee(businessTripFee);
        Long businessServeFee =  Optional.ofNullable(companyExcelModel.getBusinessServeFee())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBusinessServeFee(businessServeFee);
        Long beforeProfitTarget =  Optional.ofNullable(companyExcelModel.getBeforeProfitTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBeforeProfitTarget(beforeProfitTarget);
        performanceBaseInfo.setBelongType(BelongTypeEnum.COMPANY.name());
        if(ObjectUtil.isNotNull(companyExcelModel) && isFirst){
            List<PerformanceBaseInfo> performanceBaseInfoList = performanceBaseInfoService.list(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                    .eq(PerformanceBaseInfo::getYear, year)
                    .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.COMPANY.name()));
            if (!CollectionUtils.isEmpty(performanceBaseInfoList)) {
                List<PerformanceRecordInfo> performanceRecordInfoList = performanceRecordInfoService.list(Wrappers.<PerformanceRecordInfo>lambdaQuery()
                        .in(PerformanceRecordInfo::getPerformanceId, performanceBaseInfoList.stream().map(PerformanceBaseInfo::getId).collect(Collectors.toList())));
                performanceRecordInfoService.removeByIds(performanceRecordInfoList.stream().map(PerformanceRecordInfo::getId).collect(Collectors.toList()));
                performanceBaseInfoService.removeByIds(performanceBaseInfoList.stream().map(PerformanceBaseInfo::getId).collect(Collectors.toList()));
            }
            //只删除一次
            isFirst = false;
        }
        performanceBaseInfoService.save(performanceBaseInfo);

        //构建record表信息
        List<PerformanceRecordInfo> performanceRecordInfos = new ArrayList<>();
        PerformanceRecordInfo januaryTarget = new PerformanceRecordInfo();
        januaryTarget.setPerformanceId(performanceBaseInfo.getId());
        januaryTarget.setMonth(1);
        januaryTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getJanuaryTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(januaryTarget);

        PerformanceRecordInfo februaryTarget = new PerformanceRecordInfo();
        februaryTarget.setPerformanceId(performanceBaseInfo.getId());
        februaryTarget.setMonth(2);
        februaryTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getFebruaryTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(februaryTarget);

        PerformanceRecordInfo marchTarget = new PerformanceRecordInfo();
        marchTarget.setPerformanceId(performanceBaseInfo.getId());
        marchTarget.setMonth(3);
        marchTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getMarchTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(marchTarget);

        PerformanceRecordInfo aprilTarget = new PerformanceRecordInfo();
        aprilTarget.setPerformanceId(performanceBaseInfo.getId());
        aprilTarget.setMonth(4);
        aprilTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getAprilTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(aprilTarget);

        PerformanceRecordInfo mayTarget = new PerformanceRecordInfo();
        mayTarget.setPerformanceId(performanceBaseInfo.getId());
        mayTarget.setMonth(5);
        mayTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getMayTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(mayTarget);

        PerformanceRecordInfo juneTarget = new PerformanceRecordInfo();
        juneTarget.setPerformanceId(performanceBaseInfo.getId());
        juneTarget.setMonth(6);
        juneTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getJuneTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(juneTarget);

        PerformanceRecordInfo julyTarget = new PerformanceRecordInfo();
        julyTarget.setPerformanceId(performanceBaseInfo.getId());
        julyTarget.setMonth(7);
        julyTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getJulyTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(julyTarget);

        PerformanceRecordInfo augustTarget = new PerformanceRecordInfo();
        augustTarget.setPerformanceId(performanceBaseInfo.getId());
        augustTarget.setMonth(8);
        augustTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getAugustTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(augustTarget);

        PerformanceRecordInfo septemberTarget = new PerformanceRecordInfo();
        septemberTarget.setPerformanceId(performanceBaseInfo.getId());
        septemberTarget.setMonth(9);
        septemberTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getSeptemberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(septemberTarget);

        PerformanceRecordInfo octoberTarget = new PerformanceRecordInfo();
        octoberTarget.setPerformanceId(performanceBaseInfo.getId());
        octoberTarget.setMonth(10);
        octoberTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getOctoberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(octoberTarget);

        PerformanceRecordInfo novemberTarget = new PerformanceRecordInfo();
        novemberTarget.setPerformanceId(performanceBaseInfo.getId());
        novemberTarget.setMonth(11);
        novemberTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getNovemberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(novemberTarget);

        PerformanceRecordInfo decemberTarget = new PerformanceRecordInfo();
        decemberTarget.setPerformanceId(performanceBaseInfo.getId());
        decemberTarget.setMonth(12);
        decemberTarget.setTargetAmount(Optional.ofNullable(companyExcelModel.getDecemberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(decemberTarget);

        if (!CollectionUtils.isEmpty(performanceRecordInfos)) {
            performanceRecordInfoService.saveBatch(performanceRecordInfos);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("公司sheet页所有数据解析完成");
        isFirst = true;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("表头信息：{}", headMap);
        super.invokeHeadMap(headMap, context);
    }
}
