package cn.zswltech.mithras.kpi.excel.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceRecordInfo;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceBaseInfoService;
import cn.zswltech.mithras.kpi.application.performance.KpiPerformanceMainInfoService;
import cn.zswltech.mithras.kpi.service.KpiPerformanceRecordInfoService;
import cn.zswltech.mithras.kpi.excel.model.PersonalExcelModel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
public class PersonalListener extends AnalysisEventListener<PersonalExcelModel> {
    private boolean isFirst = true;
    @Setter
    private Long mainId;
    @Setter
    private Long year;
    @Setter
    private Map<Integer, String> headMap;
    @Resource
    private UserDOMapper userManager;
    @Resource
    private KpiPerformanceBaseInfoService performanceBaseInfoService;
    @Resource
    private KpiPerformanceRecordInfoService performanceRecordInfoService;
    @Resource
    private KpiPerformanceMainInfoService performanceMainInfoService;
    @Resource
    private OrgDOMapper orgMapper;

    @Override
    public void invoke(PersonalExcelModel personalExcelModel, AnalysisContext analysisContext) {
        PerformanceBaseInfo performanceBaseInfo = new PerformanceBaseInfo();
        performanceBaseInfo.setMainId(mainId);
        performanceBaseInfo.setYear(year);
        String businessType = Optional.ofNullable(BusinessTypeEnum.getByDisplay(personalExcelModel.getBusinessType()))
                .map(Enum::name).orElse(null);
        performanceBaseInfo.setBusinessType(businessType);
        Map<String, Long> userMap = userManager.selectAll().stream().collect(Collectors.toMap(UserDO::getAccount, UserDO::getId));
        if (ObjectUtil.isNotNull(userMap.get(personalExcelModel.getLoginAccountName()))) {
            performanceBaseInfo.setBusinessUserId(userMap.get(personalExcelModel.getLoginAccountName()));
        }
        if (!StringUtils.isEmpty(personalExcelModel.getDepartment())) {
            List<OrgDO> dos = orgMapper.queryByName(personalExcelModel.getDepartment(), 1);
            performanceBaseInfo.setBelongDeptName(personalExcelModel.getDepartment());
            if (!CollectionUtils.isEmpty(dos)) {
                performanceBaseInfo.setBelongDeptId(dos.get(0).getId());
            }
        }
        Long advertisingAmount = Optional.ofNullable(personalExcelModel.getInvestmentTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setAdvertisingAmount(advertisingAmount);
        Long revenueTarget = Optional.ofNullable(personalExcelModel.getRevenueTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setRevenueTarget(revenueTarget);

        Long assetBalanceTarget = Optional.ofNullable(personalExcelModel.getAssetBalanceTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setAssetBalanceTarget(assetBalanceTarget);

        Long profitTarget = Optional.ofNullable(personalExcelModel.getProfitTarget())
                .map(obj -> obj.multiply(BigDecimal.valueOf(100000000)).longValue())
                .orElse(0L);
        performanceBaseInfo.setProfitTarget(profitTarget);
        performanceBaseInfo.setBelongType(BelongTypeEnum.PERSONAL.name());
        if(ObjectUtil.isNotNull(personalExcelModel) && isFirst){
            List<PerformanceBaseInfo> performanceBaseInfoList = performanceBaseInfoService.list(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                    .eq(PerformanceBaseInfo::getYear, year)
                    .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.PERSONAL.name()));
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

        List<PerformanceRecordInfo> performanceRecordInfoList = new ArrayList<>();
//        BigDecimal januaryTarget = personalExcelModel.getJanuaryTarget();
//        if (ObjectUtil.isNotNull(januaryTarget)) {
//            PerformanceRecordInfo januaryTargetRecord = new PerformanceRecordInfo();
//            januaryTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            januaryTargetRecord.setMonth(1);
//            januaryTargetRecord.setTargetAmount(januaryTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(januaryTargetRecord);
//        }
//        BigDecimal februaryTarget = personalExcelModel.getFebruaryTarget();
//        if (ObjectUtil.isNotNull(februaryTarget)) {
//            PerformanceRecordInfo februaryTargetRecord = new PerformanceRecordInfo();
//            februaryTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            februaryTargetRecord.setMonth(2);
//            februaryTargetRecord.setTargetAmount(februaryTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(februaryTargetRecord);
//        }
//
//        BigDecimal marchTarget = personalExcelModel.getMarchTarget();
//        if (ObjectUtil.isNotNull(marchTarget)) {
//            PerformanceRecordInfo marchTargetRecord = new PerformanceRecordInfo();
//            marchTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            marchTargetRecord.setMonth(3);
//            marchTargetRecord.setTargetAmount(marchTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(marchTargetRecord);
//        }
//
//        BigDecimal aprilTarget = personalExcelModel.getAprilTarget();
//        if (ObjectUtil.isNotNull(aprilTarget)) {
//            PerformanceRecordInfo aprilTargetRecord = new PerformanceRecordInfo();
//            aprilTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            aprilTargetRecord.setMonth(4);
//            aprilTargetRecord.setTargetAmount(aprilTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(aprilTargetRecord);
//        }
//
//        BigDecimal mayTarget = personalExcelModel.getMayTarget();
//        if (ObjectUtil.isNotNull(mayTarget)) {
//            PerformanceRecordInfo mayTargetRecord = new PerformanceRecordInfo();
//            mayTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            mayTargetRecord.setMonth(5);
//            mayTargetRecord.setTargetAmount(mayTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(mayTargetRecord);
//        }
//
//        BigDecimal juneTarget = personalExcelModel.getJuneTarget();
//        if (ObjectUtil.isNotNull(juneTarget)) {
//            PerformanceRecordInfo juneTargetRecord = new PerformanceRecordInfo();
//            juneTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            juneTargetRecord.setMonth(6);
//            juneTargetRecord.setTargetAmount(juneTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(juneTargetRecord);
//        }
//
//        BigDecimal julyTarget = personalExcelModel.getJulyTarget();
//        if (ObjectUtil.isNotNull(julyTarget)) {
//            PerformanceRecordInfo julyTargetRecord = new PerformanceRecordInfo();
//            julyTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            julyTargetRecord.setMonth(7);
//            julyTargetRecord.setTargetAmount(julyTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(julyTargetRecord);
//        }
//
//        BigDecimal augustTarget = personalExcelModel.getAugustTarget();
//        if (ObjectUtil.isNotNull(augustTarget)) {
//            PerformanceRecordInfo augustTargetRecord = new PerformanceRecordInfo();
//            augustTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            augustTargetRecord.setMonth(8);
//            augustTargetRecord.setTargetAmount(augustTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(augustTargetRecord);
//        }
//
//        BigDecimal septemberTarget = personalExcelModel.getSeptemberTarget();
//        if (ObjectUtil.isNotNull(septemberTarget)) {
//            PerformanceRecordInfo septemberTargetRecord = new PerformanceRecordInfo();
//            septemberTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            septemberTargetRecord.setMonth(9);
//            septemberTargetRecord.setTargetAmount(septemberTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(septemberTargetRecord);
//        }
//
//        BigDecimal octoberTarget = personalExcelModel.getOctoberTarget();
//        if (ObjectUtil.isNotNull(octoberTarget)) {
//            PerformanceRecordInfo octoberTargetRecord = new PerformanceRecordInfo();
//            octoberTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            octoberTargetRecord.setMonth(10);
//            octoberTargetRecord.setTargetAmount(octoberTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(octoberTargetRecord);
//        }
//
//        BigDecimal novemberTarget = personalExcelModel.getNovemberTarget();
//        if (ObjectUtil.isNotNull(novemberTarget)) {
//            PerformanceRecordInfo novemberTargetRecord = new PerformanceRecordInfo();
//            novemberTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            novemberTargetRecord.setMonth(11);
//            novemberTargetRecord.setTargetAmount(novemberTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(novemberTargetRecord);
//        }
//
//        BigDecimal decemberTarget = personalExcelModel.getDecemberTarget();
//        if (ObjectUtil.isNotNull(decemberTarget)) {
//            PerformanceRecordInfo decemberTargetRecord = new PerformanceRecordInfo();
//            decemberTargetRecord.setPerformanceId(performanceBaseInfo.getId());
//            decemberTargetRecord.setMonth(12);
//            decemberTargetRecord.setTargetAmount(decemberTarget.multiply(BigDecimal.valueOf(100000000)).longValue());
//            performanceRecordInfoList.add(decemberTargetRecord);
//        }

        if (!CollectionUtils.isEmpty(performanceRecordInfoList)) {
            performanceRecordInfoService.saveBatch(performanceRecordInfoList);
        }

        if (!CollectionUtils.isEmpty(this.headMap)) {
            this.headMap.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        isFirst = true;
        log.info("个人数据导入完成");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("表头信息：{}", headMap);
        this.headMap = headMap;
        super.invokeHeadMap(headMap, context);
    }
}
