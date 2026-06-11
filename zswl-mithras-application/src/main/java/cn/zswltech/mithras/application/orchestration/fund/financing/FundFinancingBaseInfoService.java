package cn.zswltech.mithras.application.orchestration.fund.financing;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.dto.fund.FundCreditListRSP;
import cn.zswltech.mithras.dto.fund.financing.SingleFinancingIdREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.fund.financing.baseinfo.FundFinancingCalcRemainingGuaranteeAmountREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingChangeSubTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.model.FundCredit;
import cn.zswltech.mithras.fund.mapper.model.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.fund.mapper.model.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.mapper.model.financing.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.credit.creditlimit.service.CreditBusinessRefService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.fund.application.*;
import cn.zswltech.mithras.application.orchestration.fund.FundCreditService;
import cn.zswltech.mithras.application.orchestration.fund.FundGuaranteeAgencyService;
import cn.zswltech.mithras.application.orchestration.fund.FundGuaranteeInfoService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.fund.application.lib.financing.FundFinancingBaseInfoLibService;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingBaseInfoLibHandler;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

@Slf4j
@Service
public class FundFinancingBaseInfoService extends ServiceImpl<FundFinancingBaseInfoMapper, FundFinancingBaseInfo> {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundFinancingBaseInfoLibService financingBaseInfoLibService;
    @Resource
    private FundFinancingBaseInfoLibHandler financingBaseInfoLibHandler;
    @Resource
    private FundGuaranteeAgencyService fundGuaranteeAgencyService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FundGuaranteeInfoService fundGuaranteeInfoService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private CreditBusinessRefService creditBusinessRefService;
    @Resource
    private FundCreditGuaranteeDetailService fundCreditGuaranteeDetailService;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FundFinancingFeeDetailService financingFeeDetailService;

    public List<FundFinancingBaseInfo> listFinancialSystemTodoList(LocalDate targetDate) {
        List<FundFinancingBaseInfo> list1 = this.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                .notIn(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name())
        );
        // 还要算上当月结清的融资
        List<FundFinancingBaseInfo> list2 = this.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.SETTLE.name())
                .notIn(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name())
                .ge(FundFinancingBaseInfo::getActualExpireDate, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), 1))
                .le(FundFinancingBaseInfo::getActualExpireDate, LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth()))
        );
        List<FundFinancingBaseInfo> result = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(list1)) {
            result.addAll(list1);
        }
        if (CollectionUtil.isNotEmpty(list2)) {
            result.addAll(list2);
        }
        return result;
    }

    public Long calcRemainingGuaranteeAmount(FundFinancingCalcRemainingGuaranteeAmountREQ req) {
        FundFinancingBaseInfo financingBaseInfo = this.getById(req.getFinancingId());
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        Long total = fundGuaranteeAgencyService.getTotalGuaranteeLimit(req.getGuaranteeAgencyId()).orElse(0L);
        Long used = fundGuaranteeAgencyService.getUsedGuaranteeLimit(Collections.singletonList(req.getGuaranteeAgencyId())).get(req.getGuaranteeAgencyId());
        if (Objects.isNull(used)) {
            used = 0L;
        }
        return total - used - req.getGuaranteeAmount();
    }

    public void updateFinancingAmount(Long id, Long financingAmount) {
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(id);
        toUpdate.setFinancingAmount(financingAmount);
        this.updateById(toUpdate);
    }

    public void updateHasPledge(Long id, YesOrNoNumberEnum hasPledge) {
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(id);
        toUpdate.setHasPledgeInfo(hasPledge.getCode());
        this.updateById(toUpdate);
    }

    /**
     * 仅限非银团业务使用
     * @param creditCode
     * @return
     */
    public List<FundFinancingBaseInfo> listByCreditCode(String creditCode) {
        FundCredit fundCredit = fundCreditService.getOne(Wrappers.<FundCredit>lambdaQuery()
                .eq(FundCredit::getCreditCode, creditCode));
        if(Objects.nonNull(fundCredit)){
            List<FundFinancingCreditRef> refList = financingCreditRefService.queryByCreditId(fundCredit.getId());
            List<Long> financingIdList = refList.stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList());
            return this.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                    .in(CollectionUtil.isNotEmpty(financingIdList) ,FundFinancingBaseInfo::getId, financingIdList));
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void syndicationModify(FundFinancingBaseInfoModifyREQ req) {
        FundFinancingBaseInfo exist = this.getById(req.getId());
        Assert.notNull(exist, () -> MithrasException.newException("基本信息不存在"));
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        validOrgInfo(exist, req.getOrganizationInfoList());
        List<Long> organizationIdList = new ArrayList<>();
        List<String> organizationNameList = new ArrayList<>();
        Map<Long, Long> orgCreditIdMap = new HashMap<>();
        List<FundFinancingBaseInfo.OrganizationInfo> organizationInfoList = new ArrayList<>();
        for (FundFinancingBaseInfoModifyREQ.organizationInfoREQ info : req.getOrganizationInfoList()) {
            List<FundCreditListRSP.FundCreditList> fundCreditLists = fundCreditService.listEffectByOrgId(info.getOrganizationId(), null);
            Assert.isTrue(Optional.ofNullable(fundCreditLists).map(m -> m.get(0).getRemainingTotalLimit()).orElse(0L) >= info.getOrganizationAmount(),
                    () -> MithrasException.newException("融资机构:【" + info.getOrganizationName()+ "】,录入值不得大于可用额度"));;
            organizationIdList.add(info.getOrganizationId());
            organizationNameList.add(info.getOrganizationName());
            FundFinancingBaseInfo.OrganizationInfo organizationInfo = BeanUtil.copyProperties(info, FundFinancingBaseInfo.OrganizationInfo.class);
            organizationInfoList.add(organizationInfo);
            orgCreditIdMap.put(info.getOrganizationId(), fundCreditLists.get(0).getId());
        }
        toUpdate.setOrganizationInfo(JSON.toJSONString(organizationInfoList));
        long count = orgCreditIdMap.entrySet().stream().map(Map.Entry::getValue).filter(Objects::nonNull).count();
        if(count > 0){
            financingCreditRefService.removeRefByFinancingId(exist.getId());
        }
        for (Map.Entry<Long, Long> entry : orgCreditIdMap.entrySet()) {
            if(entry.getValue() != null){
                financingCreditRefService.saveRef(exist.getId(), entry.getValue(), entry.getKey());
            }
        }


//        toUpdate.setOrganizationIds(JSON.toJSONString(organizationIdList));
//        toUpdate.setOrganizationNames(JSON.toJSONString(organizationNameList));

        toUpdate.setId(req.getId());
        toUpdate.setTimeLimitType(req.getTimeLimitType());
        toUpdate.setBusinessType(req.getBusinessType());
        List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeInfoRspList = this.calculateGuaranteeInfo(organizationIdList);
        toUpdate.setGuaranteeInfo(JSON.toJSONString(guaranteeInfoRspList));

        toUpdate.setFundsPurpose(req.getFundsPurpose());
        toUpdate.setRemark(req.getRemark());
        toUpdate.setInitialInterestReceivedOnce(req.getInitialInterestReceivedOnce());
        this.getBaseMapper().updateAnnotationIncludeNullById(toUpdate);
        this.tryUpdateChangeOther(req.getId());
        // 尝试变更融资方案表
        List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList = null;
        if (StrUtil.isNotBlank(toUpdate.getGuaranteeInfo())) {
            List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(toUpdate.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
            guaranteeAmountInfoList = guaranteeInfoList.stream().map(item -> {
                FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo = new FundFinancingPlan.GuaranteeAmountInfo();
                guaranteeAmountInfo.setOrganizationId(item.getOrganizationId());
                guaranteeAmountInfo.setOrganizationName(item.getOrganizationName());
                guaranteeAmountInfo.setGuaranteeAgencyId(item.getGuaranteeAgencyId());
                guaranteeAmountInfo.setGuaranteeAgencyName(item.getGuaranteeAgencyName());
                return guaranteeAmountInfo;
            }).collect(Collectors.toList());
        }
        financingPlanService.tryModifyGuaranteeAmount(req.getId(), guaranteeAmountInfoList, req.getOrganizationInfoList().stream().mapToLong(FundFinancingBaseInfoModifyREQ.organizationInfoREQ::getOrganizationAmount).sum());
    }

    private void validOrgInfo(FundFinancingBaseInfo baseInfo, List<FundFinancingBaseInfoModifyREQ.organizationInfoREQ> organizationInfoList) {
        if(CollectionUtil.isEmpty(organizationInfoList)) {
            throw MithrasException.newException("融资机构为必填项");
        }
        FundFinancingPlan financingPlan = financingPlanService.getOneByFinancingId(baseInfo.getId());
        List<FundFinancingFeeDetail> feeDetailList = financingFeeDetailService.list(Wrappers.<FundFinancingFeeDetail>lambdaQuery().eq(FundFinancingFeeDetail::getFinancingId, baseInfo.getId()));
        // 费用项已引用的机构id
        List<Long> feeContainsOrgId = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(feeDetailList)){
            feeContainsOrgId = feeDetailList.stream().map(FundFinancingFeeDetail::getOrganizationId).collect(Collectors.toList());
        }
        // 担保已引用的机构id
        List<Long> guaranteeContainsOrgId = new ArrayList<>();
        if(financingPlan.getGuaranteeInfo() != null) {
            List<FundFinancingPlan.GuaranteeInfo> planGuaranteeInfoList = JSON.parseArray(financingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
            guaranteeContainsOrgId = planGuaranteeInfoList.stream().map(FundFinancingPlan.GuaranteeInfo::getOrganizationId).collect(Collectors.toList());
        }
        // 编辑后的机构id
        List<Long> orgIdList = organizationInfoList.stream().map(FundFinancingBaseInfoModifyREQ.organizationInfoREQ::getOrganizationId).collect(Collectors.toList());
        Assert.isTrue(orgIdList.containsAll(feeContainsOrgId), () -> MithrasException.newException("已被费用项选用的机构不可删除"));
        Assert.isTrue(orgIdList.containsAll(guaranteeContainsOrgId), () -> MithrasException.newException("已被指定担保的机构不可删除"));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundFinancingBaseInfoModifyREQ req) {
        FundFinancingBaseInfo exist = this.getById(req.getId());
        Assert.notNull(exist, () -> MithrasException.newException("基本信息不存在"));
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
//        List<Long> ref = creditBusinessRefService.getByTargetId(exist.getId(), CreditLimitBizTypeEnum.FUND);
//        Assert.notNull(CollectionUtil.isNotEmpty(ref), () -> MithrasException.newException("授信信息不存在"));
        List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByFinancingId(exist.getId());
        List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeInfoRspList = calculateGuaranteeInfo(financingCreditRefList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList()));
        toUpdate.setId(req.getId());
        toUpdate.setGuaranteeInfo(JSON.toJSONString(guaranteeInfoRspList));
        toUpdate.setBusinessType(req.getBusinessType());
        toUpdate.setTimeLimitType(req.getTimeLimitType());
        toUpdate.setFundsPurpose(req.getFundsPurpose());
        toUpdate.setRemark(req.getRemark());
        toUpdate.setInitialInterestReceivedOnce(req.getInitialInterestReceivedOnce());
        this.getBaseMapper().updateAnnotationIncludeNullById(toUpdate);
        this.tryUpdateChangeOther(req.getId());
        // 尝试变更融资方案表
        List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList = null;
        if (StrUtil.isNotBlank(toUpdate.getGuaranteeInfo())) {
            List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(toUpdate.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
            guaranteeAmountInfoList = guaranteeInfoList.stream().map(item -> {
                FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo = new FundFinancingPlan.GuaranteeAmountInfo();
                guaranteeAmountInfo.setOrganizationId(item.getOrganizationId());
                guaranteeAmountInfo.setOrganizationName(item.getOrganizationName());
                guaranteeAmountInfo.setGuaranteeAgencyId(item.getGuaranteeAgencyId());
                guaranteeAmountInfo.setGuaranteeAgencyName(item.getGuaranteeAgencyName());
                return guaranteeAmountInfo;
            }).collect(Collectors.toList());
        }
        financingPlanService.tryModifyGuaranteeAmount(req.getId(), guaranteeAmountInfoList, null);
    }

    private List<FundFinancingBaseInfo.GuaranteeInfo> buildGuaranteeInfo(List<FundFinancingBaseInfoModifyREQ.GuaranteeInfoREQ> guaranteeInfoReqList, FundFinancingBaseInfo exist) {
        List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(guaranteeInfoReqList)) {
            // 已有担保信息
            Map<Long, FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoMap;
            if (StrUtil.isNotBlank(exist.getGuaranteeInfo())) {
                List<FundFinancingBaseInfo.GuaranteeInfo> list = JSONUtil.toList(exist.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
                guaranteeInfoMap = list.stream().collect(Collectors.toMap(FundFinancingBaseInfo.GuaranteeInfo::getGuaranteeAgencyId, e -> e));
            } else {
                guaranteeInfoMap = Collections.emptyMap();
            }

            Set<Long> guaranteeAgencyIds = guaranteeInfoReqList.stream().map(FundFinancingBaseInfoModifyREQ.GuaranteeInfoREQ::getGuaranteeAgencyId).collect(Collectors.toSet());
            Map<Long, Long> remainingGuaranteeLimitMap = fundGuaranteeInfoService.remainingGuaranteeLimit(guaranteeAgencyIds);
            // 查询担保机构名称
            List<FundGuaranteeAgency> list = fundGuaranteeAgencyService.listByIds(guaranteeAgencyIds);
            Map<Long, FundGuaranteeAgency> map = list.stream().collect(Collectors.toMap(FundGuaranteeAgency::getId, e -> e));
            guaranteeInfoList = guaranteeInfoReqList.stream().map(item -> {
                long remaining;
                if (Objects.equals(exist.getFinancingStatus(), FundFinancingStatusEnum.CARRY_INTEREST.name())) {
                    FundFinancingBaseInfo.GuaranteeInfo oldInfo = guaranteeInfoMap.get(item.getGuaranteeAgencyId());
                    long old;
                    if (Objects.isNull(oldInfo)) {
                        old = 0L;
                    } else {
                        old = Optional.ofNullable(oldInfo.getGuaranteeAmount()).orElse(0L);
                    }
                    remaining = remainingGuaranteeLimitMap.get(item.getGuaranteeAgencyId()) + old - item.getGuaranteeAmount();
                } else {
                    remaining = remainingGuaranteeLimitMap.get(item.getGuaranteeAgencyId()) - item.getGuaranteeAmount();
                }
                Assert.isTrue(remaining >= 0, () -> MithrasException.newException("剩余担保额度不能小于0"));
                FundFinancingBaseInfo.GuaranteeInfo guaranteeInfo = new FundFinancingBaseInfo.GuaranteeInfo();
                guaranteeInfo.setGuaranteeAgencyId(item.getGuaranteeAgencyId());
                guaranteeInfo.setGuaranteeAmount(item.getGuaranteeAmount());
                // 剩余担保额度拷贝一份（剩余担保额度只在生效的时候固化一次，即版本表里都应该是生效那时的，详情页会动态变化）
                if (Objects.nonNull(guaranteeInfoMap.get(item.getGuaranteeAgencyId()))) {
                    guaranteeInfo.setRemainingGuaranteeAmount(guaranteeInfoMap.get(item.getGuaranteeAgencyId()).getRemainingGuaranteeAmount());
                }
                FundGuaranteeAgency dbData = map.get(item.getGuaranteeAgencyId());
                if (Objects.nonNull(dbData)) {
                    guaranteeInfo.setGuaranteeAgencyName(dbData.getGuaranteeAgencyName());
                }
                return guaranteeInfo;
            }).collect(Collectors.toList());
        }
        return guaranteeInfoList;
    }

    public void tryUpdateChangeOther(Long financingId) {
        FundFinancingBaseInfo baseInfo = this.getById(financingId);
        if (ObjectUtil.isEmpty(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_ALREADY_EXIST);
        }
        if (Objects.equals(baseInfo.getFinancingStatus(), FundFinancingStatusEnum.NEW.name())) {
            return;
        }
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(financingId);
        toUpdate.setProcessStatus(FundFinancingProcessStatus.CHANGING_UN_SUBMIT.name());
        toUpdate.setChangeSubType(FundFinancingChangeSubTypeEnum.CHANGE_OTHER.name());
        this.updateById(toUpdate);
    }

    public FundFinancingBaseInfoDetailRSP detail(SingleFinancingIdREQ req) {
        FundFinancingBaseInfo financingBaseInfo;
        if (StrUtil.isBlank(req.getVersion())) {
            financingBaseInfo = this.getById(req.getFinancingId());
            Assert.notNull(financingBaseInfo, () -> MithrasException.newException("基本信息不存在"));
            // 编辑区的剩余授信额度和剩余担保额度需要动态变化
            return this.convertToDetailRSP(financingBaseInfo, false);
        } else {
            FundFinancingBaseInfoLib fundFinancingBaseInfoLib = financingBaseInfoLibService.getOneByOriginIdVersion(req.getFinancingId(), req.getVersion());
            Assert.notNull(fundFinancingBaseInfoLib, () -> MithrasException.newException("对应版本的基本信息数据不存在"));
            financingBaseInfo = financingBaseInfoLibHandler.actualLib2Entity(fundFinancingBaseInfoLib);
            return this.convertToDetailRSP(financingBaseInfo, true);
        }
    }

    public FundFinancingBaseInfoDetailRSP convertToDetailRSP(FundFinancingBaseInfo financingBaseInfo, boolean isHistory) {
        FundFinancingBaseInfoDetailRSP rsp = new FundFinancingBaseInfoDetailRSP();
        BeanUtil.copyProperties(financingBaseInfo, rsp);
        List<FundOrganization> organizationList = fundOrganizationService.getByFinancingId(financingBaseInfo.getId());
        rsp.setOrganizationId(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
        rsp.setOrganizationName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));

        List<FundFinancingBaseInfoDetailRSP.OrganizationInfo> organizationInfoList = JSON.parseArray(financingBaseInfo.getOrganizationInfo(), FundFinancingBaseInfoDetailRSP.OrganizationInfo.class);
        List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeInfoRspList = JSON.parseArray(financingBaseInfo.getGuaranteeInfo(), FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP.class);

        // 编辑区
        List<String> editStatus = Arrays.asList(FundFinancingProcessStatus.NEW_UN_SUBMIT.name(), FundFinancingProcessStatus.CANCEL_NEW.name(), FundFinancingProcessStatus.NEW_APPROVAL_REJECT.name());
        if(!isHistory && editStatus.contains(financingBaseInfo.getApprovalStatus())) {
            if (financingBaseInfo.getBusinessType().equals(FundFinancingBizTypeEnum.SYNDICATIONS.name())) {
                if(CollectionUtil.isNotEmpty(organizationInfoList)) {
                    Map<Long, Pair<Long, Long>> creditLimitPairMap = fundCreditService.queryCreditLimitBatch(organizationInfoList.stream().map(FundFinancingBaseInfoDetailRSP.OrganizationInfo::getOrganizationId).collect(Collectors.toList()));
                    for (FundFinancingBaseInfoDetailRSP.OrganizationInfo organizationInfo : organizationInfoList) {
                        Pair<Long, Long> creditLimitLongPair = creditLimitPairMap.getOrDefault(organizationInfo.getOrganizationId(), Pair.of(0L, 0L));
                        organizationInfo.setRemainingCreditAmount(creditLimitLongPair.getValue());
                    }
                }
            }else {
                Pair<Long, Long> remainingCreditLimit = this.getRemainingCreditLimit(financingBaseInfo);
                rsp.setRemainingCreditLimit(remainingCreditLimit.getValue());
                rsp.setTotalCreditLimit(remainingCreditLimit.getKey());
            }
            if(CollectionUtil.isNotEmpty(guaranteeInfoRspList)) {
                guaranteeInfoRspList = this.calculateGuaranteeInfo(guaranteeInfoRspList.stream().map(FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP::getOrganizationId).collect(Collectors.toList()));
            }
        }
        Set<Long> userIds = new HashSet<>();
        userIds.add(financingBaseInfo.getFundManagerId());
        userIds.add(financingBaseInfo.getBizHeaderId());
        userIds.add(financingBaseInfo.getLeaderId());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        rsp.setFundManagerName(userNameMap.get(financingBaseInfo.getFundManagerId()));
        rsp.setBizHeaderName(userNameMap.get(financingBaseInfo.getBizHeaderId()));
        rsp.setLeaderName(userNameMap.get(financingBaseInfo.getLeaderId()));
        String deptName = id2NameService.deptId2NameSingle(financingBaseInfo.getDeptId());
        rsp.setDeptName(deptName);
        // 处理担保信息
        rsp.setGuaranteeInfoList(guaranteeInfoRspList);
        rsp.setOrganizationInfoList(organizationInfoList);

//        if (StrUtil.isNotBlank(financingBaseInfo.getGuaranteeInfo())) {
//            List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(financingBaseInfo.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
//            List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeInfoRSPList = new ArrayList<>(guaranteeInfoList.size());
//            for (FundFinancingBaseInfo.GuaranteeInfo guaranteeInfo : guaranteeInfoList) {
//                FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP guaranteeInfoRSP = new FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP();
//                guaranteeInfoRSP.setGuaranteeAgencyId(guaranteeInfo.getGuaranteeAgencyId());
//                guaranteeInfoRSP.setGuaranteeAgencyName(guaranteeInfo.getGuaranteeAgencyName());
//                guaranteeInfoRSP.setGuaranteeAmount(guaranteeInfo.getGuaranteeAmount());
//                if (isHistory) {
//                    guaranteeInfoRSP.setRemainingAmount(guaranteeInfo.getRemainingGuaranteeAmount());
//                } else {
//                    Long total = fundGuaranteeAgencyService.getTotalGuaranteeLimit(guaranteeInfo.getGuaranteeAgencyId()).orElse(0L);
//                    Long used = fundGuaranteeAgencyService.getUsedGuaranteeLimit(Collections.singletonList(guaranteeInfo.getGuaranteeAgencyId())).get(guaranteeInfo.getGuaranteeAgencyId());
//                    if (Objects.isNull(used)) {
//                        used = 0L;
//                    }
//                    if (Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.CARRY_INTEREST.name())) {
//                        guaranteeInfoRSP.setRemainingAmount(total - used);
//                    } else {
//                        guaranteeInfoRSP.setRemainingAmount(total - used - LongUtil.null2zero(guaranteeInfoRSP.getGuaranteeAmount()));
//                    }
////                    // 实时计算
////                    if (Objects.isNull(map)) {
////                        map = fundGuaranteeInfoService.remainingGuaranteeLimit(guaranteeInfoList.stream().map(FundFinancingBaseInfo.GuaranteeInfo::getGuaranteeAgencyId).collect(Collectors.toSet()), Objects.equals(fundCredit.getRecyclable(), YesOrNoNumberEnum.YES.getCode()));
////                    }
////                    if (Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.CARRY_INTEREST.name())) {
////                        guaranteeInfoRSP.setRemainingAmount(map.get(guaranteeInfo.getGuaranteeAgencyId()));
////                    } else {
////                        guaranteeInfoRSP.setRemainingAmount(map.get(guaranteeInfo.getGuaranteeAgencyId()) - guaranteeInfo.getGuaranteeAmount());
////                    }
//                }
//                guaranteeInfoRSPList.add(guaranteeInfoRSP);
//            }
//            rsp.setGuaranteeInfoList(guaranteeInfoRSPList);
//        }
        if (Objects.nonNull(financingBaseInfo.getPlanLoanDate())) {
            rsp.setPlanLoanDate(LocalDateTimeUtil.format(financingBaseInfo.getPlanLoanDate(), DatePattern.NORM_DATE_PATTERN));
        }
        if (Objects.nonNull(financingBaseInfo.getActualLoanDate())) {
            rsp.setActualLoanDate(LocalDateTimeUtil.format(financingBaseInfo.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
        }
        return rsp;
    }

//    public List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> calculateGuaranteeInfo(Long financingId) {
//        Map<Long, Map<Long, List<CreditBusinessRef>>> refMap = creditBusinessRefService.getByTargetId(financingId, CreditLimitBizTypeEnum.FUND);
//        // 先取生效的授信
//        List<Long> effectCreditId = fundCreditService.getEffectCreditId();
//        List<Long> creditIdList = refMap.values().stream().map(Map::keySet).flatMap(Collection::stream).filter(effectCreditId::contains).collect(Collectors.toList());
//        Map<Long, List<FundCreditGuaranteeDetail>> creditIdMap = fundCreditGuaranteeDetailService.getByCreditIdList(creditIdList);
//        List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeInfoRspList = new ArrayList<>();
//
//        Set<Long> guaranteeAgencyIdList = creditIdMap.values().stream().flatMap(Collection::stream).map(FundCreditGuaranteeDetail::getGuaranteeAgencyId).collect(Collectors.toSet());
//        Map<Long, String> agencyNameMap = fundGuaranteeAgencyService.getNameByIds(guaranteeAgencyIdList);
//        Map<Long, String> organizationNameMap = fundOrganizationService.getNamesByIds(refMap.keySet());
//
//        for (Long organizationId : refMap.keySet()) {
//            Map<Long, List<CreditBusinessRef>> map = refMap.get(organizationId);
//            for (Long creditId : map.keySet()) {
//                // 某个机构下的生效授信
//                List<FundCreditGuaranteeDetail> guaranteeDetails = creditIdMap.get(creditId);
//                // 遍历授信下的担保信息,取到授信下各个担保额度
//                for (FundCreditGuaranteeDetail guaranteeDetail : guaranteeDetails) {
//                    FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP guaranteeInfoRsp = new FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP();
//                    guaranteeInfoRsp.setOrganizationId(organizationId);
//                    guaranteeInfoRsp.setOrganizationName(organizationNameMap.get(organizationId));
//                    guaranteeInfoRsp.setGuaranteeAgencyId(guaranteeDetail.getGuaranteeAgencyId());
//                    guaranteeInfoRsp.setGuaranteeAgencyName(agencyNameMap.get(guaranteeDetail.getGuaranteeAgencyId()));
//                    guaranteeInfoRsp.setGuaranteeAmount(guaranteeDetail.getGuaranteeAmount());
//
//                    guaranteeInfoRsp.setRemainingAmount(0L);
//                    guaranteeInfoRspList.add(guaranteeInfoRsp);
//
//                }
//            }
//        }
//        return guaranteeInfoRspList;
//    }


    public List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> calculateGuaranteeInfo(Collection<Long> organizationIdList) {
        // 取授信
        Map<Long, List<FundFinancingCreditRef>> refMap = financingCreditRefService.queryBatchByOrgId(organizationIdList);
        List<Long> creditIdList = refMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getCreditId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(creditIdList)) {
            return Collections.emptyList();
        }
        Map<Long, List<FundCredit>> creditMap = Optional.ofNullable(
                fundCreditService.listByIds(creditIdList).stream().collect(Collectors.groupingBy(FundCredit::getOrganizationId))
        ).orElse(Collections.emptyMap());

        Map<Long, List<FundCreditGuaranteeDetail>> creditIdMap = fundCreditGuaranteeDetailService.getByCreditIdList(creditIdList);
        List<FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP> guaranteeInfoRspList = new ArrayList<>();
        Set<Long> guaranteeAgencyIdList = creditIdMap.values().stream().flatMap(Collection::stream).map(FundCreditGuaranteeDetail::getGuaranteeAgencyId).collect(Collectors.toSet());
        Map<Long, Long> remainingLimitMap = fundGuaranteeInfoService.remainingGuaranteeLimit(guaranteeAgencyIdList);
        Map<Long, String> agencyNameMap = fundGuaranteeAgencyService.getNameByIds(guaranteeAgencyIdList);
        Map<Long, String> organizationNameMap = fundOrganizationService.getNamesByIds(refMap.keySet());

        Map<Long, CreditLimitDetailBO> creditLimitDetailMap = fundCreditService.queryLimitDetailBatch(creditMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), false);
        for (Long organizationId : refMap.keySet()) {
            List<FundCredit> fundCreditList = creditMap.get(organizationId);
            for (FundCredit fundCredit : fundCreditList) {
                // 某个机构下的生效授信
                List<FundCreditGuaranteeDetail> guaranteeDetails = creditIdMap.get(fundCredit.getId());
                CreditLimitDetailBO creditLimitDetail = creditLimitDetailMap.get(fundCredit.getId());
                // 遍历授信下的担保信息,取到授信下各个担保额度
                if(CollectionUtil.isEmpty(guaranteeDetails)){
                    continue;
                }
                for (FundCreditGuaranteeDetail guaranteeDetail : guaranteeDetails) {
                    FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP guaranteeInfoRsp = new FundFinancingBaseInfoDetailRSP.GuaranteeInfoRSP();
                    guaranteeInfoRsp.setOrganizationId(organizationId);
                    guaranteeInfoRsp.setOrganizationName(organizationNameMap.get(organizationId));
                    guaranteeInfoRsp.setGuaranteeAgencyId(guaranteeDetail.getGuaranteeAgencyId());
                    guaranteeInfoRsp.setGuaranteeAgencyName(agencyNameMap.get(guaranteeDetail.getGuaranteeAgencyId()));
                    if(creditLimitDetail != null && creditLimitDetail.getGuaranteeLimit() != null && creditLimitDetail.getOccupyGuaranteeLimit() != null) {
                        guaranteeInfoRsp.setGuaranteeAmount(creditLimitDetail.getGuaranteeLimit() - creditLimitDetail.getOccupyGuaranteeLimit());
                    }
                    guaranteeInfoRsp.setRemainingGuaranteeAmount(remainingLimitMap.get(guaranteeDetail.getGuaranteeAgencyId()));
                    guaranteeInfoRspList.add(guaranteeInfoRsp);
                }
            }
        }
        return guaranteeInfoRspList;
    }


    public void modifyPlanLoanDate(Long financingId, LocalDate planLoanDate) {
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(financingId);
        toUpdate.setPlanLoanDate(planLoanDate);
        this.updateById(toUpdate);
    }

    public void modifyActualLoanDate(Long financingId, LocalDate actualLoanDate) {
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(financingId);
        toUpdate.setActualLoanDate(actualLoanDate);
        this.updateById(toUpdate);
    }

    public void modifyActualExpireDate(Long financingId, LocalDate actualExpireDate) {
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(financingId);
        toUpdate.setActualExpireDate(actualExpireDate);
        this.updateById(toUpdate);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void fixDynamicData(FundFinancingBaseInfo baseInfo) {
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(baseInfo.getId());
        if (StrUtil.isNotBlank(baseInfo.getGuaranteeInfo())) {
            List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(baseInfo.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
            Set<Long> agencyIds = guaranteeInfoList.stream().map(FundFinancingBaseInfo.GuaranteeInfo::getGuaranteeAgencyId).collect(Collectors.toSet());
            Map<Long, Long> map = fundGuaranteeInfoService.remainingGuaranteeLimit(agencyIds);
            for (FundFinancingBaseInfo.GuaranteeInfo guaranteeInfo : guaranteeInfoList) {
                Long remainingAmount = map.get(guaranteeInfo.getGuaranteeAgencyId());
                if (Objects.equals(baseInfo.getFinancingStatus(), FundFinancingStatusEnum.CARRY_INTEREST.name())) {
                    guaranteeInfo.setRemainingGuaranteeAmount(remainingAmount);
                } else {
                    guaranteeInfo.setRemainingGuaranteeAmount(LongUtil.null2zero(remainingAmount) - LongUtil.null2zero(guaranteeInfo.getGuaranteeAmount()));
                }
            }
            toUpdate.setGuaranteeInfo(JSONUtil.toJsonStr(guaranteeInfoList));
        }
        // 固化剩余授信额度
        if(Objects.equals(baseInfo.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())){
            List<FundFinancingBaseInfo.OrganizationInfo> organizationInfoList = JSON.parseArray(baseInfo.getOrganizationInfo(), FundFinancingBaseInfo.OrganizationInfo.class);
            Map<Long, Pair<Long, Long>> creditLimitPairMap = fundCreditService.queryCreditLimitBatch(organizationInfoList.stream().map(FundFinancingBaseInfo.OrganizationInfo::getOrganizationId).collect(Collectors.toList()));
            for (FundFinancingBaseInfo.OrganizationInfo organizationInfo : organizationInfoList) {
                Pair<Long, Long> creditLimitLongPair = creditLimitPairMap.getOrDefault(organizationInfo.getOrganizationId(), Pair.of(0L, 0L));
                organizationInfo.setRemainingCreditAmount(creditLimitLongPair.getValue());
            }
            toUpdate.setOrganizationInfo(JSON.toJSONString(organizationInfoList));
        }else {
            Pair<Long, Long> remainingCreditLimit = this.getRemainingCreditLimit(baseInfo);
            toUpdate.setRemainingCreditLimit(remainingCreditLimit.getValue());
            toUpdate.setTotalCreditLimit(remainingCreditLimit.getKey());
        }
        this.updateById(toUpdate);
    }

    /**
     * 仅限非银团的合同使用
     * @param baseInfo
     * @return
     */
    public Pair<Long, Long> getRemainingCreditLimit(FundFinancingBaseInfo baseInfo) {
        List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByFinancingId(baseInfo.getId());
        if(CollectionUtil.isEmpty(financingCreditRefList)){
            throw new MithrasException("未找到生效授信");
        }
        FundCredit fundCredit = fundCreditService.getById(financingCreditRefList.get(0).getCreditId());
        CreditLimitDetailBO creditLimitDetail = fundCreditService.queryLimitDetail(fundCredit, false);
        if(creditLimitDetail != null) {
            return Pair.of(creditLimitDetail.getTotalLimit(), creditLimitDetail.getTotalLimit() - creditLimitDetail.getOccupyTotalLimit());
        }
//        long usedLimit = 0L;
//        Map<Long, FundCreditService.LimitDto> map = fundCreditService.calculateEveryLimit(fundCredit , baseInfo.getId());
//        FundCreditService.LimitDto limitDto = map.get(baseInfo.getCreditId());
//        if (Objects.nonNull(limitDto)) {
//            usedLimit = limitDto.getTotalUsedLimit(fundCredit.getRecyclable());
//        }
//        // 剩余可用 = 自己占用的 + 除自己之外剩余的
//        if (baseInfo.getFinancingStatus().equals(FundFinancingStatusEnum.CARRY_INTEREST.name())) {
//            return fundCredit.getTotalCreditLimit() - usedLimit + baseInfo.getFinancingAmount();
//        } else {
//            return fundCredit.getTotalCreditLimit() - usedLimit;
//        }
//        baseInfo.getOrganizationIds();
//        fundCreditService.queryLimitDetail()


        return Pair.of(0L ,0L);

    }

    public ProcessResp findRelatedProcess(Long financingId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(financingId));
        processPageReq.setModelKeyList(BusinessModuleEnum.FUND_FINANCING.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public String getFileName(Long id){
        FundFinancingBaseInfo financingBaseInfo = this.getById(id);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        //间融：融资编号-机构简称
        List<FundOrganization> organizationList = getBean(FundOrganizationService.class).getByFinancingId(financingBaseInfo.getId());
        String abbreviation = organizationList.stream().map(FundOrganization::getAbbreviation).collect(Collectors.joining("、"));
        return financingBaseInfo.getFinancingCode() + "-" + abbreviation;
    }

}
