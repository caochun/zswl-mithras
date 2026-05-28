package cn.zswltech.mithras.service.service.kpi.excel.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.enums.kpi.BelongTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.BusinessTypeEnum;
import cn.zswltech.mithras.service.mapper.model.kpi.PerformanceBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.PerformanceRecordInfo;
import cn.zswltech.mithras.service.service.kpi.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiPerformanceMainInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiPerformanceRecordInfoService;
import cn.zswltech.mithras.service.service.kpi.excel.DeptExcelModel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/7/1/12:02
 * @description
 */
@Slf4j
@Component
public class DepartmentListener extends AnalysisEventListener<DeptExcelModel> {
    private boolean isFirst = true;
    @Setter
    private Long mainId;
    @Setter
    private Long year;
    @Resource
    private OrgDOMapper orgMapper;
    @Resource
    private KpiPerformanceBaseInfoService performanceBaseInfoService;
    @Resource
    private KpiPerformanceRecordInfoService performanceRecordInfoService;
    @Resource
    private KpiPerformanceMainInfoService performanceMainInfoService;

    @Override
    public void invoke(DeptExcelModel deptExcelModel, AnalysisContext analysisContext) {
        PerformanceBaseInfo performanceBaseInfo = new PerformanceBaseInfo();
        performanceBaseInfo.setMainId(mainId);
        performanceBaseInfo.setYear(year);
        String businessType = Optional.ofNullable(BusinessTypeEnum.getByDisplay(deptExcelModel.getBusinessType()))
                .map(Enum::name).orElse(null);
        performanceBaseInfo.setBusinessType(businessType);
        if (!StringUtils.isEmpty(deptExcelModel.getDepartment())) {
            List<OrgDO> dos = orgMapper.queryByName(deptExcelModel.getDepartment(), 1);
            if (!CollectionUtils.isEmpty(dos)) {
                performanceBaseInfo.setBelongDeptId(dos.get(0).getId());
            } else {
                performanceBaseInfo.setBelongDeptName(deptExcelModel.getDepartment());
            }
        }
        Long advertisingAmount = Optional.ofNullable(deptExcelModel.getAnnualInvestmentTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setAdvertisingAmount(advertisingAmount);
        Long revenueTarget = Optional.ofNullable(deptExcelModel.getAnnualRevenueTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setRevenueTarget(revenueTarget);
        Long profitTarget = Optional.ofNullable(deptExcelModel.getAnnualProfitTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setProfitTarget(profitTarget);
        Long assetBalanceTarget = Optional.ofNullable(deptExcelModel.getAssetBalanceTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setAssetBalanceTarget(assetBalanceTarget);
        Long consultingFeeIncome =  Optional.ofNullable(deptExcelModel.getConsultingFeeIncome())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setConsultingFeeIncome(consultingFeeIncome);
        Long interestIncome =  Optional.ofNullable(deptExcelModel.getInterestIncome())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setInterestIncome(interestIncome);
        Long bizFee =  Optional.ofNullable(deptExcelModel.getBizFee())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBizFee(bizFee);
        Long businessTripFee =  Optional.ofNullable(deptExcelModel.getBusinessTripFee())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBusinessTripFee(businessTripFee);
        Long businessServeFee =  Optional.ofNullable(deptExcelModel.getBusinessServeFee())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBusinessServeFee(businessServeFee);
        Long beforeProfitTarget =  Optional.ofNullable(deptExcelModel.getBeforeProfitTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setBeforeProfitTarget(beforeProfitTarget);
        performanceBaseInfo.setBelongType(BelongTypeEnum.DEPARTMENT.name());
        if (ObjectUtil.isNotNull(deptExcelModel) && isFirst) {
            List<PerformanceBaseInfo> performanceBaseInfoList = performanceBaseInfoService.list(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                    .eq(PerformanceBaseInfo::getYear, year)
                    .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name()));
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

        List<PerformanceRecordInfo> performanceRecordInfos = new ArrayList<>();
        PerformanceRecordInfo januaryTarget = new PerformanceRecordInfo();
        januaryTarget.setPerformanceId(performanceBaseInfo.getId());
        januaryTarget.setMonth(1);
        januaryTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getJanuaryTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(januaryTarget);

        PerformanceRecordInfo februaryTarget = new PerformanceRecordInfo();
        februaryTarget.setPerformanceId(performanceBaseInfo.getId());
        februaryTarget.setMonth(2);
        februaryTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getFebruaryTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(februaryTarget);

        PerformanceRecordInfo marchTarget = new PerformanceRecordInfo();
        marchTarget.setPerformanceId(performanceBaseInfo.getId());
        marchTarget.setMonth(3);
        marchTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getMarchTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(marchTarget);

        PerformanceRecordInfo aprilTarget = new PerformanceRecordInfo();
        aprilTarget.setPerformanceId(performanceBaseInfo.getId());
        aprilTarget.setMonth(4);
        aprilTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getAprilTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(aprilTarget);

        PerformanceRecordInfo mayTarget = new PerformanceRecordInfo();
        mayTarget.setPerformanceId(performanceBaseInfo.getId());
        mayTarget.setMonth(5);
        mayTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getMayTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(mayTarget);

        PerformanceRecordInfo juneTarget = new PerformanceRecordInfo();
        juneTarget.setPerformanceId(performanceBaseInfo.getId());
        juneTarget.setMonth(6);
        juneTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getJuneTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(juneTarget);

        PerformanceRecordInfo julyTarget = new PerformanceRecordInfo();
        julyTarget.setPerformanceId(performanceBaseInfo.getId());
        julyTarget.setMonth(7);
        julyTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getJulyTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(julyTarget);

        PerformanceRecordInfo augustTarget = new PerformanceRecordInfo();
        augustTarget.setPerformanceId(performanceBaseInfo.getId());
        augustTarget.setMonth(8);
        augustTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getAugustTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(augustTarget);

        PerformanceRecordInfo septemberTarget = new PerformanceRecordInfo();
        septemberTarget.setPerformanceId(performanceBaseInfo.getId());
        septemberTarget.setMonth(9);
        septemberTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getSeptemberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(septemberTarget);

        PerformanceRecordInfo octoberTarget = new PerformanceRecordInfo();
        octoberTarget.setPerformanceId(performanceBaseInfo.getId());
        octoberTarget.setMonth(10);
        octoberTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getOctoberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(octoberTarget);

        PerformanceRecordInfo novemberTarget = new PerformanceRecordInfo();
        novemberTarget.setPerformanceId(performanceBaseInfo.getId());
        novemberTarget.setMonth(11);
        novemberTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getNovemberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(novemberTarget);

        PerformanceRecordInfo decemberTarget = new PerformanceRecordInfo();
        decemberTarget.setPerformanceId(performanceBaseInfo.getId());
        decemberTarget.setMonth(12);
        decemberTarget.setTargetAmount(Optional.ofNullable(deptExcelModel.getDecemberTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L));
        performanceRecordInfos.add(decemberTarget);

        if (!performanceRecordInfos.isEmpty()) {
            performanceRecordInfoService.saveBatch(performanceRecordInfos);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        isFirst = true;
        log.info("部门数据导入完成");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("表头信息：{}", headMap);
        super.invokeHeadMap(headMap, context);
    }
}
