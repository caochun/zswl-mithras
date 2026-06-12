package cn.zswltech.mithras.application.orchestration.fund;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.dto.fund.FundCreditGuaranteeDetailDto;
import cn.zswltech.mithras.credit.creditlimit.service.CreditLimitManagerService;
import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.fund.application.credit.FundCreditGuaranteeDetailService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.persistence.model.credit.FundCredit;
import cn.zswltech.mithras.fund.persistence.model.credit.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.fund.persistence.model.credit.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitOccupyBO;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.Function0Arg;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zswl
 */
@Service
@Slf4j
public class FundInitService {

    @Resource
    private FundCreditService creditService;
    @Resource
    private FundFinancingService financingService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundCreditGuaranteeDetailService creditGuaranteeDetailService;
    @Resource
    private CreditLimitManagerService creditLimitManagerService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundGuaranteeInfoService guaranteeInfoService;
    @Resource
    private FundGuaranteeAgencyService guaranteeAgencyService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundOrganizationService fundOrganizationService;

    @Transactional(rollbackFor = Throwable.class)
    public void creditLimitNo(){
        List<FundCredit> creditList = creditService.listByIds(Arrays.asList(33L,2L,5L,26L,52L));
        Map<Long, List<FundCreditGuaranteeDetail>> guaranteeDetail = creditGuaranteeDetailService.getByCreditIdList(creditList.stream().map(FundCredit::getId).collect(Collectors.toList()));
        for (FundCredit fundCredit : creditList) {
            List<FundCreditGuaranteeDetail> guaranteeDetailList = guaranteeDetail.get(fundCredit.getId());
            List<FundCreditGuaranteeDetailDto> detailDtoList = new ArrayList<>();
            if(CollectionUtil.isNotEmpty(guaranteeDetailList)){
                detailDtoList = BeanUtil.copyToList(guaranteeDetailList, FundCreditGuaranteeDetailDto.class);
            }
            creditService.creditCreateOrUpdate(fundCredit ,detailDtoList);
        }
//        throw new MithrasException("执行完毕");
    }


    @Transactional(rollbackFor = Throwable.class)
    public void financingOccupyRecyclableNo(){
        // 循环授信占用
        List<FundCredit> creditList = creditService.listByIds(Arrays.asList(33L,2L,5L,26L,52L));
        Map<Long, List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByCreditId(creditList.stream().map(FundCredit::getId).collect(Collectors.toList()));
        refMap = refMap.values().stream().flatMap(Collection::stream).collect(Collectors.groupingBy(FundFinancingCreditRef::getFinancingId));
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.listByIds(refMap.keySet());
        Map<Long, FundFinancingPlan> planMap = financingPlanService.getMapByFinancingIds(refMap.keySet());
        Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(refMap.keySet(), FinancingTypeEnum.INDIRECT);
        Set<Long> set = new HashSet<>();
        for (FundFinancingBaseInfo financingBaseInfo : financingBaseInfoList) {
            if(Objects.equals(FundFinancingProcessStatus.NEW_UN_SUBMIT.name(), financingBaseInfo.getApprovalStatus())){
                // 新建未提交的不占用
                continue;
            }
            Long financingId = financingBaseInfo.getId();
            FundFinancingCreditRef ref = refMap.get(financingId).get(0);
            BigDecimal guaranteeRate = BigDecimal.ZERO;
            FundFinancingPlan financingPlan = planMap.get(financingId);
            if(financingPlan.getGuaranteeInfo() != null){
                List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSON.parseArray(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
                BigDecimal guaranteeAmountSum = BigDecimal.valueOf(guaranteeInfoList.stream().mapToLong(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).sum());
                guaranteeRate = guaranteeAmountSum.divide(BigDecimal.valueOf(financingPlan.getFinancingAmount()), 10, RoundingMode.HALF_UP);
            }
            CreditLimitOccupyBO occupyBO = new CreditLimitOccupyBO();
            occupyBO.setBizTargetKey(String.valueOf(financingId));
            occupyBO.setGrantSubjectKey(String.valueOf(ref.getOrganizationId()));
            occupyBO.setBizSourceKey(String.valueOf(ref.getCreditId()));
            occupyBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            // 占用金额
            Long occupyAmount = Arrays.asList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()).contains(financingBaseInfo.getFinancingStatus())
                    ? remainingAmountMap.getOrDefault(financingId, 0L) : financingBaseInfo.getFinancingAmount();
            occupyBO.setAmount(occupyAmount);
            // 占用金额*担保比例
            occupyBO.setGuaranteeAmount(BigDecimal.valueOf(occupyAmount).multiply(guaranteeRate).longValue());
            occupyBO.setHappenDate(LocalDate.now());
            try {
                creditLimitManagerService.occupy(occupyBO);
            }catch (Exception e){
                log.error("初始化失败的循环授信:{},{}",e,occupyBO.getBizSourceKey());
                set.add(ref.getCreditId());
            }

        }
        log.info("循环错误授信");
        set.forEach(System.out::println);
//        throw new MithrasException("执行完成");
    }





    @XxlJob("initCreditLimit")
    @Transactional(rollbackFor = Throwable.class)
    public void creditLimit(){
        List<FundCredit> creditList = creditService.list(Wrappers.<FundCredit>lambdaQuery()
                .eq(FundCredit::getEffective, YesOrNoNumberEnum.YES.getCode())
                .ge(FundCredit::getEffectiveDateTo, LocalDate.now()));
        Map<Long, List<FundCreditGuaranteeDetail>> guaranteeDetail = creditGuaranteeDetailService.getByCreditIdList(creditList.stream().map(FundCredit::getId).collect(Collectors.toList()));
        for (FundCredit fundCredit : creditList) {
            List<FundCreditGuaranteeDetail> guaranteeDetailList = guaranteeDetail.get(fundCredit.getId());
            List<FundCreditGuaranteeDetailDto> detailDtoList = new ArrayList<>();
            if(CollectionUtil.isNotEmpty(guaranteeDetailList)){
                detailDtoList = BeanUtil.copyToList(guaranteeDetailList, FundCreditGuaranteeDetailDto.class);
            }
            creditService.creditCreateOrUpdate(fundCredit ,detailDtoList);
        }
        financingOccupyNoRecyclable();
        financingOccupyRecyclable();
//        throw new MithrasException("执行完毕");
    }

    /**
     * 需要在结构变更前调用该方法初始化
     */
    @Transactional(rollbackFor = Throwable.class)
    public void financingOccupyNoRecyclable(){
        // 非循环授信占用
        Map<String, BigDecimal> initMapOrigin = new HashMap<>();
        initMapOrigin.put("DK202304140030-06",new BigDecimal("14062500"));
        initMapOrigin.put("DK202304140030-03",new BigDecimal("0"));
        initMapOrigin.put("DK202304140030-02",new BigDecimal("0"));
        initMapOrigin.put("DK202304140030-05",new BigDecimal("36250000"));
        initMapOrigin.put("DK202304140030-01",new BigDecimal("0"));
        initMapOrigin.put("DK202304140030-04",new BigDecimal("9839083"));
        initMapOrigin.put("DK202304140030-07",new BigDecimal("47687500"));
        initMapOrigin.put("DK202304140030-08",new BigDecimal("54500000"));
        initMapOrigin.put("DK202304140030-09",new BigDecimal("26530000"));
        initMapOrigin.put("DK202403050052-12",new BigDecimal("260840000"));
        initMapOrigin.put("DK202403050052-13",new BigDecimal("61580000"));
        initMapOrigin.put("DK202403050052-14",new BigDecimal("93750000"));
        initMapOrigin.put("DK202403050052-15",new BigDecimal("131020000"));
        initMapOrigin.put("DK202304140030-10",new BigDecimal("87500000"));
        initMapOrigin.put("DK202304140030-11",new BigDecimal("89000000"));
        Map<String, Long> initMap = initMapOrigin.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> entry.getValue().multiply(new BigDecimal("10000")).longValue()));
        // 融资基本信息
        Map<String, FundFinancingBaseInfo> financingBaseInfoMap = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().in(FundFinancingBaseInfo::getFinancingCode, initMap.keySet()))
                .stream().collect(Collectors.toMap(FundFinancingBaseInfo::getFinancingCode, Function.identity()));
        List<Long> financingIdList = financingBaseInfoMap.values().stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
        Map<Long, List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByFinancingId(financingIdList);
        Map<Long, FundFinancingPlan> planMap = financingPlanService.getMapByFinancingIds(financingIdList);

        Set<Long> set = new HashSet<>();
        for (Map.Entry<String, FundFinancingBaseInfo> entry : financingBaseInfoMap.entrySet()) {
            FundFinancingBaseInfo financingBaseInfo = entry.getValue();
            Long financingId = financingBaseInfo.getId();
            BigDecimal guaranteeRate = BigDecimal.ZERO;
            FundFinancingPlan financingPlan = planMap.get(financingId);
            if(financingPlan.getGuaranteeInfo() != null){
                List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSON.parseArray(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
                BigDecimal guaranteeAmountSum = BigDecimal.valueOf(guaranteeInfoList.stream().mapToLong(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).sum());
                guaranteeRate = guaranteeAmountSum.divide(BigDecimal.valueOf(financingPlan.getFinancingAmount()), 10, RoundingMode.HALF_UP);
            }
            FundFinancingCreditRef ref = refMap.get(financingId).get(0);
            Long occupyAmount = initMap.get(entry.getKey());
            CreditLimitOccupyBO occupyBO = new CreditLimitOccupyBO();
            occupyBO.setBizTargetKey(String.valueOf(financingId));
            occupyBO.setGrantSubjectKey(String.valueOf(ref.getOrganizationId()));
            occupyBO.setBizSourceKey(String.valueOf(ref.getCreditId()));
            occupyBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            occupyBO.setAmount(occupyAmount);
            occupyBO.setGuaranteeAmount(BigDecimal.valueOf(occupyAmount).multiply(guaranteeRate).longValue());
            occupyBO.setHappenDate(LocalDate.now());
            try {
                creditLimitManagerService.occupy(occupyBO);
            }catch (Exception e){
                log.error("初始化失败的非循环授信:{},{}",e,ref.getCreditId());
            }
        }
        log.info("非循环错误授信");
        set.forEach(System.out::println);


    }

    @Transactional(rollbackFor = Throwable.class)
    public void financingOccupyRecyclable(){
        // 循环授信占用
        List<FundCredit> creditList = creditService.list(Wrappers.<FundCredit>lambdaQuery()
                .eq(FundCredit::getRecyclable, YesOrNoNumberEnum.YES.getCode())
                .eq(FundCredit::getEffective, YesOrNoNumberEnum.YES.getCode())
                .ge(FundCredit::getEffectiveDateTo, LocalDate.now()));
        Map<Long, List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByCreditId(creditList.stream().map(FundCredit::getId).collect(Collectors.toList()));
        refMap = refMap.values().stream().flatMap(Collection::stream).collect(Collectors.groupingBy(FundFinancingCreditRef::getFinancingId));
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.listByIds(refMap.keySet());
        Map<Long, FundFinancingPlan> planMap = financingPlanService.getMapByFinancingIds(refMap.keySet());
        Map<Long, Long> remainingAmountMap = fundReceiptRepayBaseInfoService.queryRemainingAmount(refMap.keySet(), FinancingTypeEnum.INDIRECT);
        Set<Long> set = new HashSet<>();
        for (FundFinancingBaseInfo financingBaseInfo : financingBaseInfoList) {
            if(Objects.equals(FundFinancingProcessStatus.NEW_UN_SUBMIT.name(), financingBaseInfo.getApprovalStatus())){
                // 新建未提交的不占用
                continue;
            }
            Long financingId = financingBaseInfo.getId();
            FundFinancingCreditRef ref = refMap.get(financingId).get(0);
            BigDecimal guaranteeRate = BigDecimal.ZERO;
            FundFinancingPlan financingPlan = planMap.get(financingId);
            if(financingPlan.getGuaranteeInfo() != null){
                List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSON.parseArray(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
                BigDecimal guaranteeAmountSum = BigDecimal.valueOf(guaranteeInfoList.stream().mapToLong(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).sum());
                guaranteeRate = guaranteeAmountSum.divide(BigDecimal.valueOf(financingPlan.getFinancingAmount()), 10, RoundingMode.HALF_UP);
            }
            CreditLimitOccupyBO occupyBO = new CreditLimitOccupyBO();
            occupyBO.setBizTargetKey(String.valueOf(financingId));
            occupyBO.setGrantSubjectKey(String.valueOf(ref.getOrganizationId()));
            occupyBO.setBizSourceKey(String.valueOf(ref.getCreditId()));
            occupyBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            // 占用金额
            Long occupyAmount = Arrays.asList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()).contains(financingBaseInfo.getFinancingStatus())
                    ? remainingAmountMap.getOrDefault(financingId, 0L) : financingBaseInfo.getFinancingAmount();
            occupyBO.setAmount(occupyAmount);
            // 占用金额*担保比例
            occupyBO.setGuaranteeAmount(BigDecimal.valueOf(occupyAmount).multiply(guaranteeRate).longValue());
            occupyBO.setHappenDate(LocalDate.now());
            try {
//                if(!Arrays.asList(30L,15L,35L,24L,13L,20L).contains(ref.getCreditId())) {
//                if(Arrays.asList(20L).contains(ref.getCreditId()) && !Objects.equals(ref.getFinancingId(), 371L)) {
                    creditLimitManagerService.occupy(occupyBO);
//                }
            }catch (Exception e){
                log.error("初始化失败的循环授信:{},{}",e,occupyBO.getBizSourceKey());
                set.add(ref.getCreditId());
            }

        }
        log.info("循环错误授信");
        set.forEach(System.out::println);
//        throw new MithrasException("执行完成");
    }

    @XxlJob("initFundTableTest")
    @Transactional(rollbackFor = Throwable.class)
    public void initFundTableTest(){
        log.info("定时任务可以正常调用");
    }


    @XxlJob("initFundTable")
    @Transactional(rollbackFor = Throwable.class)
    public void initFundTable(){

        List<FundFinancingBaseInfo> needUpdateList = new ArrayList<>();
        // 担保额度
        Map<Long, Long> guaranteeRemainingMap = guaranteeInfoService.remainingGuaranteeLimit(guaranteeAgencyService.list().stream().map(FundGuaranteeAgency::getId).collect(Collectors.toSet()));
        List<FundCredit> fundCreditList = creditService.list(Wrappers.<FundCredit>lambdaQuery().eq(FundCredit::getEffective, 1).ge(FundCredit::getEffectiveDateTo, LocalDate.now()));
        // 授信额度
        Map<Long, CreditLimitDetailBO> creditLimitDetailMap = creditService.queryLimitDetailBatch(fundCreditList, false);

        // 主表基本信息
        List<FundFinancingBaseInfo> baseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().ne(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.SYNDICATIONS.name()));
        // 融资方案信息
        Map<Long, FundFinancingPlan> planMap = financingPlanService.list(Wrappers.<FundFinancingPlan>lambdaQuery().in(FundFinancingPlan::getFinancingId, baseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(FundFinancingPlan::getFinancingId, Function.identity()));
        // 融资授信关联信息
        Map<Long, List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByFinancingId(baseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList()));
        Map<Long, String> orgIdNameMap = fundOrganizationService.getNamesByIds(refMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()));

        Map<Long, List<FundFinancingPlan.GuaranteeInfo>> planGuaranteeInfoMap = new HashMap<>();
        // 主表担保信息字段处理
        for (FundFinancingBaseInfo baseInfo : baseInfoList) {
            if(Objects.nonNull(baseInfo.getGuaranteeInfo())){
                List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfos = JSON.parseArray(baseInfo.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
                List<FundFinancingCreditRef> financingCreditRefList = refMap.get(baseInfo.getId());
                if(CollectionUtil.isEmpty(financingCreditRefList)){
                    throw new MithrasException("获取机构信息异常，financingId:" + baseInfo.getId());
                }
                Long orgId = financingCreditRefList.get(0).getOrganizationId();
                Long creditId = Optional.ofNullable(financingCreditRefList).map(m -> m.get(0).getCreditId()).orElse(null);
                CreditLimitDetailBO creditLimit = creditLimitDetailMap.getOrDefault(creditId, new CreditLimitDetailBO());
                List<FundFinancingPlan.GuaranteeInfo> planGuaranteeInfoList = new ArrayList<>();
                for (FundFinancingBaseInfo.GuaranteeInfo guaranteeInfo : guaranteeInfos) {
                    guaranteeInfo.setOrganizationId(orgId);
                    guaranteeInfo.setOrganizationName(orgIdNameMap.get(orgId));
                    // 补充机构信息转为融资方案字段
                    FundFinancingPlan.GuaranteeInfo planGuaranteeInfo = BeanUtil.copyProperties(guaranteeInfo, FundFinancingPlan.GuaranteeInfo.class);
                    planGuaranteeInfoList.add(planGuaranteeInfo);
                    // 基本信息补充 授信担保金额，担保剩余额度
                    Long creditRemainingGuarantee = Optional.ofNullable(creditLimit.getGuaranteeLimit()).orElse(0L) - Optional.ofNullable(creditLimit.getOccupyGuaranteeLimit()).orElse(0L);
                    guaranteeInfo.setGuaranteeAmount(creditRemainingGuarantee);
                    guaranteeInfo.setRemainingGuaranteeAmount(guaranteeRemainingMap.get(guaranteeInfo.getGuaranteeAgencyId()));
                }
                baseInfo.setGuaranteeInfo(JSON.toJSONString(guaranteeInfos));
                needUpdateList.add(baseInfo);
                // 生成融资方案担保数据
                planGuaranteeInfoMap.put(baseInfo.getId(), planGuaranteeInfoList);
            }
        }

        financingBaseInfoService.updateBatchById(needUpdateList);

        Map<Long, FundFinancingBaseInfo> baseInfoMap = baseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, bigBear -> bigBear));
        List<FundFinancingPlan> financingPlanList = fundFinancingPlanService.list(Wrappers.<FundFinancingPlan>lambdaQuery()
                .in(FundFinancingPlan::getFinancingId, baseInfoMap.keySet()));

        for (FundFinancingPlan financingPlan : financingPlanList) {
            FundFinancingBaseInfo baseInfo = baseInfoMap.get(financingPlan.getFinancingId());
            List<FundFinancingCreditRef> financingCreditRefList = refMap.get(baseInfo.getId());
            if(CollectionUtil.isEmpty(financingCreditRefList)){
                throw new MithrasException("获取机构信息异常，financingId:" + baseInfo.getId());
            }
            Long orgId = financingCreditRefList.get(0).getOrganizationId();
            if(Objects.nonNull(financingPlan.getGuaranteeAmountInfo())){
                List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfos = JSON.parseArray(financingPlan.getGuaranteeAmountInfo(), FundFinancingPlan.GuaranteeAmountInfo.class);
                for (FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo : guaranteeAmountInfos) {
                    guaranteeAmountInfo.setOrganizationId(orgId);
                    guaranteeAmountInfo.setOrganizationName(orgIdNameMap.get(orgId));
                }
                financingPlan.setGuaranteeAmountInfo(JSON.toJSONString(guaranteeAmountInfos));
            }
            List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = planGuaranteeInfoMap.get(financingPlan.getFinancingId());
            if(CollectionUtil.isNotEmpty(guaranteeInfoList)){
                financingPlan.setGuaranteeInfo(JSON.toJSONString(guaranteeInfoList));
            }
        }
        fundFinancingPlanService.updateBatchById(financingPlanList);
//        throw new MithrasException("执行完毕");


    }


}
