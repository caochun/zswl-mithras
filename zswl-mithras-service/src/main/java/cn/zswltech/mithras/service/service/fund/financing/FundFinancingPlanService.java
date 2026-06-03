package cn.zswltech.mithras.service.service.fund.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.basedata.BaseDataLprDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingChangeLprREQ;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingChangeLprRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.plan.FundFinancingPlanModifyREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.controller.basedata.BaseDataLprController;
import cn.zswltech.mithras.contract.enums.contract.LPRTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.OrganizationType;
import cn.zswltech.mithras.fund.domain.enums.financing.*;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingPlanMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundCredit;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.fund.application.bo.ComprehensiveFinancingCostBO;
import cn.zswltech.mithras.service.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.fund.application.*;
import cn.zswltech.mithras.fund.application.financing.FundLprAdjustRecordService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.fund.FundGuaranteeAgencyService;
import cn.zswltech.mithras.service.service.fund.FundGuaranteeInfoService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingPlanLibService;
import cn.zswltech.mithras.service.service.lib.fund.financing.handler.impl.FundFinancingPlanLibHandler;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FundFinancingPlanService extends ServiceImpl<FundFinancingPlanMapper, FundFinancingPlan> {
    @Resource
    private FundFinancingPlanLibService financingPlanLibService;
    @Resource
    private FundFinancingPlanLibHandler financingPlanLibHandler;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundGuaranteeAgencyService fundGuaranteeAgencyService;
    @Resource
    private FundFinancingService fundFinancingService;
    @Resource
    private BaseDataLprController baseDataLprController;
    @Resource
    private FundGuaranteeInfoService fundGuaranteeInfoService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundCreditGuaranteeDetailService creditGuaranteeDetailService;
    @Resource
    private FundFinancingFeeDetailService financingFeeDetailService;
    @Resource
    private NewFtpBaseInfoService ftpBaseInfoService;

    public void tryModifyGuaranteeAmount(Long financingId, List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList, Long financingAmount) {
        FundFinancingPlan financingPlan = this.getOne(Wrappers.<FundFinancingPlan>lambdaQuery().eq(FundFinancingPlan::getFinancingId, financingId));
        Assert.notNull(financingPlan, () -> MithrasException.newException("融资方案不存在"));
        Map<Long ,Map<Long, FundFinancingPlan.GuaranteeAmountInfo>> existMap;
        if (StrUtil.isBlank(financingPlan.getGuaranteeAmountInfo())) {
            existMap = Collections.emptyMap();
        } else {
            existMap = guaranteeAmountInfoList.stream().collect(Collectors.groupingBy(FundFinancingPlan.GuaranteeAmountInfo::getOrganizationId,
                    Collectors.toMap(FundFinancingPlan.GuaranteeAmountInfo::getGuaranteeAgencyId, Function.identity())));
        }
        financingPlan.setComprehensiveInterestRate(financingPlan.getComprehensiveInterestRate());
        if (CollectionUtil.isEmpty(guaranteeAmountInfoList)) {
            financingPlan.setGuaranteeAmountInfo(null);
        } else {
            // 如果是已经保存过需要保留历史填写的担保金额
            for (FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo : guaranteeAmountInfoList) {
                FundFinancingPlan.GuaranteeAmountInfo exist = existMap.getOrDefault(guaranteeAmountInfo.getOrganizationId(), new HashMap<>()).get(guaranteeAmountInfo.getGuaranteeAgencyId());
                if (Objects.nonNull(exist) && Objects.equals(exist.getOrganizationId(), guaranteeAmountInfo.getOrganizationId())) {
                    guaranteeAmountInfo.setGuaranteeFeeRate(exist.getGuaranteeFeeRate());
                    guaranteeAmountInfo.setGuaranteeFeeAmount(exist.getGuaranteeFeeAmount());
                } else {
                    guaranteeAmountInfo.setGuaranteeFeeRate(4500);
                    guaranteeAmountInfo.setGuaranteeFeeAmount(this.calculateGuaranteeAmount(financingPlan.getFinancingAmount(), guaranteeAmountInfo.getGuaranteeFeeRate(), financingPlan.getFinancingMonth()));
                }
            }
            financingPlan.setGuaranteeAmountInfo(JSONUtil.toJsonStr(guaranteeAmountInfoList));
        }
        if(financingAmount != null){
            financingPlan.setFinancingAmount(financingAmount);
        }
        this.getBaseMapper().updateAnnotationIncludeNullById(financingPlan);
    }

    public FundFinancingChangeLprRSP getLprData(SingleFinancingIdREQ req) {
        FundFinancingChangeLprRSP rsp = new FundFinancingChangeLprRSP();
        if (StrUtil.isBlank(req.getVersion())) {
            // 没有指定版本则新数据为编辑区数据、老数据为最新生效版本数据
            FundFinancingPlan financingPlan = this.getOneByFinancingId(req.getFinancingId());
            FundFinancingPlanLib financingPlanLib = financingPlanLibHandler.queryLatestDataByOriginId(financingPlan.getId());
            rsp.setNewData(BeanUtil.copyProperties(financingPlan, FundFinancingChangeLprRSP.Data.class));
            if (Objects.nonNull(financingPlanLib)) {
                rsp.setOldData(BeanUtil.copyProperties(financingPlanLib, FundFinancingChangeLprRSP.Data.class));
            }
        } else {
            // 指定版本则新数据为对应版本数据，老数据为比该版本小的最新生效版本数据
            FundFinancingPlanLib newData = financingPlanLibService.getOneByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            FundFinancingPlanLib oldData = financingPlanLibService.getLatestByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            if (Objects.nonNull(newData)) {
                rsp.setNewData(BeanUtil.copyProperties(newData, FundFinancingChangeLprRSP.Data.class));
            }
            if (Objects.nonNull(oldData)) {
                rsp.setOldData(BeanUtil.copyProperties(oldData, FundFinancingChangeLprRSP.Data.class));
            }
        }
        return rsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void changeLpr(FundFinancingChangeLprREQ req) {
        FundFinancingPlan exist = this.getOneByFinancingId(req.getFinancingId());
        Assert.notNull(exist, () -> MithrasException.newException("融资方案不存在"));
        FundFinancingPlan toUpdate = new FundFinancingPlan();
        toUpdate.setId(exist.getId());
        toUpdate.setLprType(req.getLprType());
        toUpdate.setLprRatePercent(req.getLprRatePercent());
        toUpdate.setLprAddPercent(req.getLprAddPercent());
        toUpdate.setInterestRateType(req.getInterestRateType());
        toUpdate.setLprArrangeMode(req.getLprArrangeMode());
        toUpdate.setLprAdjustmentDay(req.getLprAdjustmentDay());
        this.updateById(toUpdate);
        // 变更状态
        FundFinancingBaseInfo baseInfoUpdate = new FundFinancingBaseInfo();
        baseInfoUpdate.setId(req.getFinancingId());
        baseInfoUpdate.setApprovalStatus(FundFinancingProcessStatus.CHANGING_UN_SUBMIT.name());
        baseInfoUpdate.setChangeSubType(FundFinancingChangeSubTypeEnum.CHANGE_LPR.name());
        financingBaseInfoService.updateById(baseInfoUpdate);
    }

    public FundFinancingPlan getOneByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundFinancingPlan> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingPlan::getFinancingId, financingId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public Map<Long, FundFinancingPlan> getMapByFinancingIds(Collection<Long> financingIds) {
        LambdaQueryWrapper<FundFinancingPlan> query = Wrappers.lambdaQuery();
        query.in(FundFinancingPlan::getFinancingId, financingIds);
        List<FundFinancingPlan> list = this.list(query);
        return list.stream().collect(Collectors.toMap(FundFinancingPlan::getFinancingId, e -> e));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundFinancingPlanModifyREQ req) {
        this.checkBeforeModify(req);
        FundFinancingPlan financingPlan = this.getById(req.getId());
        Assert.notNull(financingPlan, () -> MithrasException.newException("融资方案数据不存在"));
        // 校验剩余授信额度
        FundFinancingBaseInfo fundFinancingBaseInfo = financingBaseInfoService.getById(financingPlan.getFinancingId());
        if(!Objects.equals(fundFinancingBaseInfo.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())) {
            fundFinancingService.checkFinancingAmount(financingPlan.getFinancingId(), req.getFinancingAmount());
        }
        FundFinancingPlan toUpdate = new FundFinancingPlan();
        BeanUtil.copyProperties(req, toUpdate);
        toUpdate.setComprehensiveInterestRateCurrent(toUpdate.getComprehensiveInterestRate());
        if (CollectionUtil.isNotEmpty(req.getGuaranteeAmountInfoList())) {
            Set<Long> ids = req.getGuaranteeAmountInfoList().stream().map(FundFinancingPlanModifyREQ.GuaranteeAmountInfoREQ::getGuaranteeAgencyId).collect(Collectors.toSet());
            List<FundGuaranteeAgency> list = fundGuaranteeAgencyService.listByIds(ids);
            Map<Long, FundGuaranteeAgency> map = list.stream().collect(Collectors.toMap(FundGuaranteeAgency::getId, e -> e));
            List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList = req.getGuaranteeAmountInfoList().stream().map(item -> {
                FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo = new FundFinancingPlan.GuaranteeAmountInfo();
                guaranteeAmountInfo.setGuaranteeAgencyId(item.getGuaranteeAgencyId());
                FundGuaranteeAgency dbData = map.get(item.getGuaranteeAgencyId());
                if (Objects.nonNull(dbData)) {
                    guaranteeAmountInfo.setGuaranteeAgencyName(dbData.getGuaranteeAgencyName());
                }
                guaranteeAmountInfo.setOrganizationId(item.getOrganizationId());
                guaranteeAmountInfo.setOrganizationName(item.getOrganizationName());
                guaranteeAmountInfo.setGuaranteeFeeRate(item.getGuaranteeFeeRate());
                guaranteeAmountInfo.setGuaranteeFeeAmount(item.getGuaranteeFeeAmount());
                // 校验担保金额是否正确
                long expectValue = this.calculateGuaranteeAmount(req.getFinancingAmount(), item.getGuaranteeFeeRate(), req.getFinancingMonth());
                Assert.isTrue(expectValue == item.getGuaranteeFeeAmount(), () -> MithrasException.newException(String.format("【%s】的担保费计算结果不正确", guaranteeAmountInfo.getGuaranteeAgencyName())));
                return guaranteeAmountInfo;
            }).collect(Collectors.toList());
            toUpdate.setGuaranteeAmountInfo(JSONUtil.toJsonStr(guaranteeAmountInfoList));
        }

        if (CollectionUtil.isNotEmpty(req.getGuaranteeInfoList())) {
            /**
             * 1. 录入的担保额度需同时小于等于担保主体和授信中的可用担保额度，才可保存成功；
             * 2. 融资金额-担保额度后需小于等于占用授信的信用可用额度，才可保存成功；
             */
            // 授信中的可用担保额度
            List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = new ArrayList<>();
            List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> baseInfoGuaranteeInfoList = JSON.parseArray(fundFinancingBaseInfo.getGuaranteeInfo(), FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP.class);
            Map<Long, List<FundFinancingCreditRef>> refByOrgIdMap = financingCreditRefService.queryBatchByOrgId(baseInfoGuaranteeInfoList.stream().map(FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP::getOrganizationId).collect(Collectors.toList()));
            Map<Long, List<FundCreditGuaranteeDetail>> guaranteeDetailMap = creditGuaranteeDetailService.getByCreditIdList(refByOrgIdMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getCreditId).collect(Collectors.toList()));
            Map<Long, Map<Long, FundFinancingCreditRef>> refMap = refByOrgIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.groupingBy(FundFinancingCreditRef::getOrganizationId,
                    Collectors.toMap(FundFinancingCreditRef::getFinancingId, Function.identity())));
            // 担保主体中的可用担保额度
            Set<Long> agencyIdList = req.getGuaranteeInfoList().stream().map(FundFinancingPlanModifyREQ.GuaranteeInfoREQ::getGuaranteeAgencyId).collect(Collectors.toSet());
            Map<Long, Long> guaranteeInfoMap = fundGuaranteeInfoService.remainingGuaranteeLimit(agencyIdList);

            Long amountSum = 0L;
            for (FundFinancingPlanModifyREQ.GuaranteeInfoREQ item : req.getGuaranteeInfoList()) {
                FundFinancingCreditRef financingCreditRef = refMap.getOrDefault(item.getOrganizationId(), new HashMap<>()).get(financingPlan.getFinancingId());
                List<FundCreditGuaranteeDetail> guaranteeDetails = guaranteeDetailMap.get(financingCreditRef.getCreditId());
                Map<Long, Long> guaranteeAmountMap = guaranteeDetails.stream().collect(Collectors.toMap(FundCreditGuaranteeDetail::getGuaranteeAgencyId, FundCreditGuaranteeDetail::getGuaranteeAmount));
                Long creditRemainingAmount = guaranteeAmountMap.getOrDefault(item.getGuaranteeAgencyId(), 0L);
                Long guaranteeRemainingAmount = guaranteeInfoMap.getOrDefault(item.getGuaranteeAgencyId(), 0L);
                Assert.isTrue(item.getGuaranteeAmount() <= creditRemainingAmount,() -> MithrasException.newException("录入的担保额度需小于等于授信中的可用担保额度【" + item.getGuaranteeAgencyName()+ "】"));
                Assert.isTrue(item.getGuaranteeAmount() <= guaranteeRemainingAmount,() -> MithrasException.newException("录入的担保额度需小于等于担保主体的可用担保额度【" + item.getGuaranteeAgencyName()+ "】"));
                amountSum += item.getGuaranteeAmount();
                FundFinancingPlan.GuaranteeInfo guaranteeInfo = BeanUtil.copyProperties(item, FundFinancingPlan.GuaranteeInfo.class);
                guaranteeInfoList.add(guaranteeInfo);
            }
            // 再校验一下担保主体被不同授信分别引用的情况下的可用额度
            Map<Long, List<FundFinancingPlanModifyREQ.GuaranteeInfoREQ>> collect = req.getGuaranteeInfoList().stream().collect(Collectors.groupingBy(FundFinancingPlanModifyREQ.GuaranteeInfoREQ::getGuaranteeAgencyId));
            for (Map.Entry<Long, List<FundFinancingPlanModifyREQ.GuaranteeInfoREQ>> itemEntry : collect.entrySet()) {
                long sum = itemEntry.getValue().stream().filter(f -> Objects.nonNull(f.getGuaranteeAmount())).mapToLong(FundFinancingPlanModifyREQ.GuaranteeInfoREQ::getGuaranteeAmount).sum();
                Long guaranteeRemainingAmount = guaranteeInfoMap.get(itemEntry.getKey());
                Assert.isTrue(sum <= guaranteeRemainingAmount,() -> MithrasException.newException("录入的担保额度需小于等于担保主体的可用担保额度【" + itemEntry.getValue().get(0).getGuaranteeAgencyName()+ "】"));

            }
            // 校验 融资金额-担保额度后需小于等于占用授信的信用可用额度
            List<FundFinancingCreditRef> refList = financingCreditRefService.queryByFinancingId(fundFinancingBaseInfo.getId());
            Map<Long, Long> organizationAmountMap = new HashMap<>();
            if(Objects.equals(fundFinancingBaseInfo.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())) {
                List<FundFinancingBaseInfo.OrganizationInfo> organizationInfoList = JSON.parseArray(fundFinancingBaseInfo.getOrganizationInfo(), FundFinancingBaseInfo.OrganizationInfo.class);
                organizationAmountMap = organizationInfoList.stream().collect(Collectors.toMap(
                        FundFinancingBaseInfo.OrganizationInfo::getOrganizationId, FundFinancingBaseInfo.OrganizationInfo::getOrganizationAmount, (m1, m2) -> m1));
            }else {
                // 非银团只会关联一笔授信
                FundFinancingCreditRef financingCreditRef = refList.get(0);
                organizationAmountMap.put(financingCreditRef.getOrganizationId(), fundFinancingBaseInfo.getFinancingAmount());
            }
            Map<Long, List<FundFinancingPlanModifyREQ.GuaranteeInfoREQ>> guaranteeInfoReqMap = req.getGuaranteeInfoList().stream().collect(Collectors.groupingBy(FundFinancingPlanModifyREQ.GuaranteeInfoREQ::getOrganizationId));
            Map<Long, FundCredit> fundCreditMap = fundCreditService.listByIds(refList.stream().map(FundFinancingCreditRef::getCreditId).collect(Collectors.toList()))
                    .stream().collect(Collectors.toMap(FundCredit::getOrganizationId, Function.identity()));
            // 每笔授信的额度详情
            Map<Long, CreditLimitDetailBO> limitDetailMap = fundCreditService.queryLimitDetailBatch(fundCreditMap.values(), false);

            for (Map.Entry<Long, List<FundFinancingPlanModifyREQ.GuaranteeInfoREQ>> itemEntry : guaranteeInfoReqMap.entrySet()) {
                CreditLimitDetailBO creditLimitDetailBO = limitDetailMap.get(fundCreditMap.get(itemEntry.getKey()).getId());
                if(Objects.isNull(creditLimitDetailBO)){
                    throw new MithrasException("信用可用额度计算失败");
                }
                Long organizationAmount = organizationAmountMap.get(itemEntry.getKey());
                long organizationGuaranteeAmount = itemEntry.getValue().stream().mapToLong(FundFinancingPlanModifyREQ.GuaranteeInfoREQ::getGuaranteeAmount).sum();
                // 融资金额-担保额度是否小于等于占用授信的信用可用额度
                Assert.isTrue(organizationAmount - organizationGuaranteeAmount <= creditLimitDetailBO.getCreditLimit() - creditLimitDetailBO.getOccupyCreditLimit(), () -> MithrasException.newException("融资金额-担保额度需小于等于占用授信的信用可用额度【" + itemEntry.getValue().get(0).getOrganizationName()+ "】"));
            }

            toUpdate.setGuaranteeInfo(JSONUtil.toJsonStr(guaranteeInfoList));
        }
        //起租前需更新ftp定价
        if (!StrUtil.equalsAny(fundFinancingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name())) {
            toUpdate.setFtpYieldRate(this.getFtpYieldRate(toUpdate.getFinancingMonth()));
        }

        int originLprRate = Optional.ofNullable(financingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(financingPlan.getLprAddPercent()).orElse(0);
        int nowLprRate = Optional.ofNullable(req.getLprRatePercent()).orElse(0) + Optional.ofNullable(req.getLprAddPercent()).orElse(0);
        this.getBaseMapper().updateAnnotationIncludeNullById(toUpdate);
        // 更新冗余字段
        financingBaseInfoService.updateFinancingAmount(financingPlan.getFinancingId(), toUpdate.getFinancingAmount());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        financingBaseInfoService.tryUpdateChangeOther(financingPlan.getFinancingId());
        // 判断是否需要更新综合融资成本
        if(Objects.equals(JSONUtil.toJsonStr(toUpdate.getGuaranteeAmountInfo()), financingPlan.getGuaranteeAmountInfo()) ||
                Objects.equals(req.getEarnestMoneyAmount(), financingPlan.getEarnestMoneyAmount()) ||
                (Objects.isNull(financingPlan.getEarnestMoneyAmount()) && Objects.nonNull(req.getEarnestMoneyAmount())) ||
                nowLprRate != originLprRate ||
                (Objects.isNull(financingPlan.getLprRatePercent()) && Objects.nonNull(req.getLprRatePercent()))) {
            // 担保费率或保证金变更、lpr利率发生变化 -> 重算综合融资成本
            updateFinancingCost(financingPlan.getFinancingId());
        }
    }

    //获取最新的ftp
    private Integer getFtpYieldRate(Integer month) {
        NewFtpMonthlyGuidanceExtDraftDetailRSP lastFtpMonthlyGuidance = ftpBaseInfoService.getLastFtpMonthlyGuidance();
        if (ObjectUtil.isEmpty(lastFtpMonthlyGuidance)) {
            return 0;
        }
        return lastFtpMonthlyGuidance.getFtpYieldRate(month);
    }

    public FundFinancingPlanDetailRSP detail(SingleFinancingIdREQ req) {
        FundFinancingPlan financingPlan;
        if (StrUtil.isBlank(req.getVersion())) {
            financingPlan = this.getOne(Wrappers.<FundFinancingPlan>lambdaQuery().eq(FundFinancingPlan::getFinancingId, req.getFinancingId()));
            Assert.notNull(financingPlan, () -> MithrasException.newException("融资方案数据不存在"));
        } else {
            FundFinancingPlanLib financingPlanLib = financingPlanLibService.getOneByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            Assert.notNull(financingPlanLib, () -> MithrasException.newException("对应版本的数据不存在"));
            financingPlan = financingPlanLibHandler.actualLib2Entity(financingPlanLib);
        }
        return this.convertToDetailRSP(financingPlan);
    }

    public FundFinancingPlanDetailRSP convertToDetailRSP(FundFinancingPlan financingPlan) {
        FundFinancingPlanDetailRSP rsp = new FundFinancingPlanDetailRSP();
        BeanUtil.copyProperties(financingPlan, rsp);
        if (StrUtil.isNotBlank(financingPlan.getGuaranteeAmountInfo())) {
            List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList = JSONUtil.toList(financingPlan.getGuaranteeAmountInfo(), FundFinancingPlan.GuaranteeAmountInfo.class);
            List<FundFinancingPlanDetailRSP.GuaranteeAmountInfoRSP> amountInfoRSPList = BeanUtil.copyToList(guaranteeAmountInfoList, FundFinancingPlanDetailRSP.GuaranteeAmountInfoRSP.class);
            rsp.setGuaranteeAmountInfoList(amountInfoRSPList);
        }
        if(StrUtil.isNotBlank(financingPlan.getGuaranteeInfo())) {
            List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
            rsp.setGuaranteeInfoList(BeanUtil.copyToList(guaranteeInfoList, FundFinancingPlanDetailRSP.GuaranteeInfoRSP.class));
        }
        List<FundFinancingFeeDetail> feeDetailList = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery()
                .eq(FundFinancingFeeDetail::getFinancingId, financingPlan.getFinancingId()));
        if(CollectionUtil.isNotEmpty(feeDetailList)){
            rsp.setTotalFee(feeDetailList.stream().mapToLong(FundFinancingFeeDetail::getAmount).sum());
        }
        rsp.setComprehensiveFinancingCost(financingPlan.getComprehensiveInterestRate());
        return rsp;
    }

    public Long calculateGuaranteeAmount(Long financingAmount, Integer rate, Integer month) {
        if (Objects.isNull(financingAmount) || Objects.isNull(rate) || Objects.isNull(month)) {
            return null;
        }
        BigDecimal financingAmountBD = BigDecimal.valueOf(financingAmount);
        BigDecimal rateBD = BigDecimal.valueOf(rate)
                .divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal monthBD = BigDecimal.valueOf(month);
        BigDecimal guaranteeAmountBD = financingAmountBD.multiply(rateBD).multiply(monthBD).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
        return Util.mithrasLongDecimalTwo(guaranteeAmountBD.longValue());
    }

    private void checkBeforeModify(FundFinancingPlanModifyREQ req) {
        Assert.isTrue(req.getFinancingAmount() > 0, () -> MithrasException.newException("融资金额必须大于0"));
        Assert.isTrue(req.getFinancingMonth() > 0, () -> MithrasException.newException("融资期限必须大于0"));
        Assert.isTrue(req.getRepayTimes() > 0, () -> MithrasException.newException("还款期数必须大于0"));
        Assert.isTrue(req.getInterestAmount() >= 0, () -> MithrasException.newException("预计利息金额必须大于等于0"));
        Assert.isTrue(req.getInterestAmount() < req.getFinancingAmount(), () -> MithrasException.newException("预计利息金额必须小于融资金额"));
//        if (Objects.nonNull(req.getServiceChargeAmount())) {
//            Assert.isTrue(req.getServiceChargeAmount() >= 0, () -> MithrasException.newException("保理手续费必须大于等于0"));
//            Assert.isTrue(req.getServiceChargeAmount() < req.getFinancingAmount(), () -> MithrasException.newException("保理手续费必须小于融资金额"));
//        }
        if (Objects.nonNull(req.getEarnestMoneyAmount())) {
            Assert.isTrue(req.getEarnestMoneyAmount() >= 0, () -> MithrasException.newException("保证金金额必须大于等于0"));
            Assert.isTrue(req.getEarnestMoneyAmount() < req.getFinancingAmount(), () -> MithrasException.newException("保证金金额必须小于融资金额"));
        }
//        if (Objects.nonNull(req.getLicenseAmount())) {
//            Assert.isTrue(req.getLicenseAmount() >= 0, () -> MithrasException.newException("开证许可证费必须大于等于0"));
//            Assert.isTrue(req.getLicenseAmount() < req.getFinancingAmount(), () -> MithrasException.newException("开证许可证费必须小于融资金额"));
//        }
//        if (Objects.nonNull(req.getOtherAmount())) {
//            Assert.isTrue(req.getOtherAmount() >= 0, () -> MithrasException.newException("其他费用必须大于等于0"));
//            Assert.isTrue(req.getOtherAmount() < req.getFinancingAmount(), () -> MithrasException.newException("其他费用必须小于融资金额"));
//        }
    }

    //尝试调整综合借款年利率, 每天查询
    public void autoAdjustRate(String financingCode){
        if (CharSequenceUtil.isEmpty(financingCode)) {
            financingCode = null;
        }
        //查询生效、起息的利率类型为浮动利率的融资方案
        List<FundFinancingPlan> fundFinancingPlans = baseMapper.queryEffectFloatPlan(financingCode);
        if(ObjectUtil.isEmpty(fundFinancingPlans)){
            return;
        }
        Map<Long, FundFinancingPlan> financingPlanMap = fundFinancingPlans.stream().collect(Collectors.toMap(FundFinancingPlan::getFinancingId, e -> e));
        List<FundFinancingBaseInfo> fundFinancingBaseInfos = financingBaseInfoService.listByIds(financingPlanMap.keySet());
        // 去掉实际贷款日期为空的
        fundFinancingBaseInfos.removeIf(e -> Objects.isNull(e.getActualLoanDate()));
        FundFinancingPlan fundFinancingPlan;
        R<BaseDataLprDetailRSP> latestLpr = baseDataLprController.getLatestLpr();
        BaseDataLprDetailRSP lprData = latestLpr.getData();
        long oneLpr = LongUtil.other2Long(lprData.getOneYear());
        long fiveLpr = LongUtil.other2Long(lprData.getFiveYear());
        List<FundFinancingPlan> updatePlan = new ArrayList<>();
        List<FundLprAdjustRecord> addLprAdjustRecordList = new ArrayList<>();
        Map<String, Integer> refreshFundsDailyCostMap = new HashMap<>();
        for(FundFinancingBaseInfo baseInfo : fundFinancingBaseInfos){
            fundFinancingPlan = financingPlanMap.get(baseInfo.getId());
            if(fundFinancingPlan != null){
                if(isChange(fundFinancingPlan.getLprArrangeMode(), fundFinancingPlan.getLprAdjustmentDay(), baseInfo.getActualLoanDate())){
                    log.info(String.format("融资申请编号：%s触发LPR调整",baseInfo.getFinancingCode()));
                    FundLprAdjustRecord fundLprAdjustRecord = new FundLprAdjustRecord();
                    fundLprAdjustRecord.setFinancingId(fundFinancingPlan.getFinancingId());
                    fundLprAdjustRecord.setBeforeLprRatePercent(fundFinancingPlan.getLprRatePercent());
                    if (LPRTypeEnum.ONE_YEAR.name().equals(fundFinancingPlan.getLprType())) {
                        fundFinancingPlan.setLprRatePercent(Math.toIntExact(oneLpr));
                        fundFinancingPlan.setComprehensiveInterestRateCurrent(Math.toIntExact(oneLpr) + fundFinancingPlan.getLprAddPercent());
                    } else {
                        fundFinancingPlan.setLprRatePercent(Math.toIntExact(fiveLpr));
                        fundFinancingPlan.setComprehensiveInterestRateCurrent((Math.toIntExact(fiveLpr) + fundFinancingPlan.getLprAddPercent()));
                    }
                    fundLprAdjustRecord.setAfterLprRatePercent(fundFinancingPlan.getLprRatePercent());
                    LocalDate localDate = LocalDateTimeUtil.parse(lprData.getLprDate(), DatePattern.NORM_DATE_PATTERN).toLocalDate();
                    fundLprAdjustRecord.setLprDate(localDate);
                    addLprAdjustRecordList.add(fundLprAdjustRecord);
                    updatePlan.add(fundFinancingPlan);
                    // 放入应付利息待更新map
                    refreshFundsDailyCostMap.put(baseInfo.getFinancingCode(), fundFinancingPlan.getComprehensiveInterestRateCurrent());
                }
            }
        }
        if(ObjectUtil.isNotEmpty(updatePlan)){
            SpringContextHolder.getBean(FundFinancingPlanService.class).updateBatchById(updatePlan);
            try {
                SpringContextHolder.getBean(FundsDailyCostMainService.class).refreshFinancingRate(refreshFundsDailyCostMap);
            } catch (Exception e) {
                log.error("更新应付利息模块融资合同利率发生异常", e);
            }
        }
        if(ObjectUtil.isNotEmpty(addLprAdjustRecordList)){
            SpringContextHolder.getBean(FundLprAdjustRecordService.class).saveBatch(addLprAdjustRecordList);
        }
    }

    private boolean isChange(String lprArrangeMode, String lprAdjustmentDay, LocalDate actualLoanDate){
        LprArrangeModeEnum lprArrangeModeEnum = LprArrangeModeEnum.of(lprArrangeMode);
        if(lprArrangeModeEnum == null || ObjectUtil.isEmpty(actualLoanDate)){
            return false;
        }
        if(Objects.equals(lprArrangeModeEnum.name(),LprArrangeModeEnum.YEAR.name())){
            return isChangeYear(lprAdjustmentDay);
        } else {
            return isChangeMonth(lprArrangeMode,actualLoanDate);
        }
    }

    public boolean isChangeMonth(String lprArrangeMode, LocalDate actualLoanDate){
        LocalDate now = LocalDate.now();
        /*当前日期小于实际贷款日期*/
        if (now.isBefore(actualLoanDate)) {
            return false;
        }

        int lastDayOfCurrentMonth = now.lengthOfMonth();
        int dayOfMonth = actualLoanDate.getDayOfMonth();
        int currentDay = now.getDayOfMonth();
        /*判断日是否相同*/
        boolean dayIsEqualFlag = dayOfMonth == currentDay || (dayOfMonth > lastDayOfCurrentMonth && currentDay == lastDayOfCurrentMonth);
        if(!dayIsEqualFlag){
            return false;
        }
        LprArrangeModeEnum lprArrangeModeEnum = LprArrangeModeEnum.of(lprArrangeMode);
        assert lprArrangeModeEnum != null;

        int month = 0;
        switch (lprArrangeModeEnum){
            case MONTH_DECEMBER:
                month = 12;
                break;
            case MONTH_SIX:
                month = 6;
                break;
            case MONTH_THREE:
                month = 3;
                break;
            case MONTH_ONE:
                //每1个月
                month = 1;
                break;
            default:
                break;
        }
        if(month == 0){
            log.error("获取对应相隔月份错误！");
            return false;
        }
        // 计算总间隔月数
        int yearDiff = now.getYear() - actualLoanDate.getYear();
        int monthDiff = now.getMonthValue() - actualLoanDate.getMonthValue();
        int totalIntervalMonths = yearDiff * 12 + monthDiff;
        return totalIntervalMonths >= month && totalIntervalMonths % month == 0;
    }

    public boolean isChangeYear(String lprAdjustmentDay){
        if(CharSequenceUtil.isEmpty(lprAdjustmentDay)){
            return false;
        }
        String[] split = lprAdjustmentDay.split("-");
        if(split.length != 2){
            return false;
        }
        int month = Integer.parseInt(split[0]);
        int day = Integer.parseInt(split[1]);

        LocalDate now = LocalDate.now();
        int lastDayOfCurrentMonth = now.lengthOfMonth();
        int currentDay = now.getDayOfMonth();
        if (month == now.getMonthValue()) {
            //符合月份
            return day == currentDay || (day > lastDayOfCurrentMonth && currentDay == lastDayOfCurrentMonth);
        }
        return false;
    }




    /**
     * 更新综合融资成本
     * @param financingId
     */
    public void updateFinancingCost(Long financingId){
        List<FundOrganization> organizationList = organizationService.getByFinancingId(financingId);
        // 是否存在不属于租赁公司的机构
        long count = organizationList.stream().filter(f -> !OrganizationType.ZL.name().equals(f.getOrganizationType())).count();

        ComprehensiveFinancingCostBO bo = new ComprehensiveFinancingCostBO();
        bo.setFinancingType(FinancingTypeEnum.INDIRECT.name());
        // 不为租赁公司的话默认给个银行类型，业务不会受到影响
        bo.setOrganizationType(count == 0 ? OrganizationType.ZL.name() : OrganizationType.BANK.name());
        bo.setFinancingId(financingId);
        Long contractRate = null;
        Long comprehensiveFinancingCost = null;
        try {
            contractRate = receiptRepayBaseInfoService.calculateFundContractRate(bo);
            if(contractRate != null){
                // 综合融资成本 = 合同利率 + (担保费率 * 担保比例)
                BigDecimal resultSum = BigDecimal.ZERO;
                FundFinancingPlan financingPlan = getOneByFinancingId(financingId);
                if(financingPlan.getGuaranteeInfo() != null) {
                    List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSON.parseArray(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
                    List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList = JSON.parseArray(financingPlan.getGuaranteeAmountInfo(), FundFinancingPlan.GuaranteeAmountInfo.class);
                    // 担保费率详情
                    Map<Long, Map<Long, FundFinancingPlan.GuaranteeAmountInfo>> guaranteeAmountMap = guaranteeAmountInfoList.stream().collect(Collectors.groupingBy(FundFinancingPlan.GuaranteeAmountInfo::getOrganizationId,
                            Collectors.toMap(FundFinancingPlan.GuaranteeAmountInfo::getGuaranteeAgencyId, Function.identity())));
                    for (FundFinancingPlan.GuaranteeInfo guaranteeInfo : guaranteeInfoList) {
                        FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo = guaranteeAmountMap.getOrDefault(guaranteeInfo.getOrganizationId(), Collections.emptyMap()).get(guaranteeInfo.getGuaranteeAgencyId());
                        // 担保比例
                        BigDecimal guaranteeRate = BigDecimal.valueOf(guaranteeInfo.getGuaranteeAmount()).divide(BigDecimal.valueOf(financingPlan.getFinancingAmount()), 4, RoundingMode.HALF_UP);
                        BigDecimal result = BigDecimal.valueOf(guaranteeAmountInfo.getGuaranteeFeeRate()).multiply(guaranteeRate);
                        resultSum = resultSum.add(result);
                    }
                }
                comprehensiveFinancingCost = resultSum.longValue() + contractRate;
            }
        }catch (MithrasException me){
            log.error("{}",me);
            throw new MithrasException("计算综合融资成本发生异常：" + me.getMessage());
        }catch (Exception e){
            log.error("{}",e);
            throw new MithrasException("计算综合融资成本发生未知异常");
        }
        update(Wrappers.<FundFinancingPlan>lambdaUpdate()
                .eq(FundFinancingPlan::getFinancingId, financingId)
//                .set(FundFinancingPlan::getContractRate, contractRate)
                .set(FundFinancingPlan::getComprehensiveInterestRate, comprehensiveFinancingCost)
                .set(FundFinancingPlan::getComprehensiveInterestRateCurrent, comprehensiveFinancingCost));
    }

}
