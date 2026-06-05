package cn.zswltech.mithras.service.service.fund.financing;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingChangePreCheckRSP;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListREQ;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListRSP;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingEffectREQ;
import cn.zswltech.mithras.dto.fund.financing.version.FundFinancingSubmitREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyCostREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceExtDraftDetailRSP;
import cn.zswltech.mithras.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.creditlimit.service.CreditLimitManagerService;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.fund.application.convert.financing.FundFinancingConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.fund.domain.enums.financing.*;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.ReceiptRepayState;
import cn.zswltech.mithras.monthly.enums.StampDutyTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBorrowingMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.ProcessModifyRemark;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundCredit;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundCreditGuaranteeDetail;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundGuaranteeAgency;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.*;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.ProcessModifyRemarkService;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.creditlimit.service.bo.CreditLimitOccupyBO;
import cn.zswltech.mithras.service.service.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.fund.application.*;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCollectAccountService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.fund.FundGuaranteeAgencyService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.fms.FundFinancingBaseInfoStateMachine;
import cn.zswltech.mithras.service.service.fund.financing.fms.FundFinancingContext;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.fund.application.lib.financing.*;
import cn.zswltech.mithras.fund.application.lib.financing.handler.impl.FundFinancingRepayActualLibHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.service.service.monthly.MonthlyManageService;
import cn.zswltech.mithras.service.service.monthly.MonthlyStampDutyService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Slf4j
@Service
public class FundFinancingService {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FundCreditService creditService;
    @Resource
    private FundCreditGuaranteeDetailService creditGuaranteeDetailService;
    @Resource
    private FundGuaranteeAgencyService fundGuaranteeAgencyService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundFinancingPledgeInfoService financingPledgeInfoService;
    @Resource
    private FundFinancingRepayActualService financingRepayActualService;
    @Resource
    private FundFinancingRepayEstimateService financingRepayEstimateService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private FundFinancingBaseInfoStateMachine stateMachine;
    @Resource
    private FundFinancingLibVersionService financingLibVersionService;
    @Resource
    private FundFinancingEarlySettlePlanService financingEarlySettlePlanService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundFinancingRepayActualLibHandler financingRepayActualLibHandler;
    @Resource
    private FundFinancingService financingService;
    @Resource
    private FundReceiptRepayBaseInfoMapper fundReceiptRepayBaseInfoMapper;
    @Resource
    private FundReceiptRepayBorrowingMapper fundReceiptRepayBorrowingMapper;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private MonthlyStampDutyService monthlyStampDutyService;
    @Resource
    private CreditLimitManagerService creditLimitManagerService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundFinancingPayAccountService financingPayAccountService;
    @Resource
    private NewFtpBaseInfoService ftpBaseInfoService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FlowTaskApiService taskApiService;

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByFinancingId(Long financingId) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        if (Objects.isNull(financingBaseInfo)) {
            throw new MithrasException("融资不存在");
        }
        if (!Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.CLOSE.name())) {
            throw new MithrasException("已关闭的融资才允许删除");
        }
        // 删除相关表数据
        SpringUtil.getBean(FundFinancingBaseInfoService.class).remove(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getId, financingId));
        SpringUtil.getBean(FundFinancingBaseInfoLibService.class).remove(Wrappers.<FundFinancingBaseInfoLib>lambdaQuery().eq(FundFinancingBaseInfoLib::getOriginId, financingId));
        SpringUtil.getBean(FundFinancingCollectAccountService.class).remove(Wrappers.<FundFinancingCollectAccount>lambdaQuery().eq(FundFinancingCollectAccount::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingCollectAccountLibService.class).remove(Wrappers.<FundFinancingCollectAccountLib>lambdaQuery().eq(FundFinancingCollectAccount::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingEarlySettlePlanService.class).remove(Wrappers.<FundFinancingEarlySettlePlan>lambdaQuery().eq(FundFinancingEarlySettlePlan::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingEarlySettlePlanLibService.class).remove(Wrappers.<FundFinancingEarlySettlePlanLib>lambdaQuery().eq(FundFinancingEarlySettlePlan::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingPayAccountService.class).remove(Wrappers.<FundFinancingPayAccount>lambdaQuery().eq(FundFinancingPayAccount::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingPayAccountLibService.class).remove(Wrappers.<FundFinancingPayAccountLib>lambdaQuery().eq(FundFinancingPayAccount::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingPlanService.class).remove(Wrappers.<FundFinancingPlan>lambdaQuery().eq(FundFinancingPlan::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingPlanLibService.class).remove(Wrappers.<FundFinancingPlanLib>lambdaQuery().eq(FundFinancingPlan::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingPledgeInfoService.class).remove(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().eq(FundFinancingPledgeInfo::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingPledgeInfoLibService.class).remove(Wrappers.<FundFinancingPledgeInfoLib>lambdaQuery().eq(FundFinancingPledgeInfo::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingRepayEstimateService.class).remove(Wrappers.<FundFinancingRepayEstimate>lambdaQuery().eq(FundFinancingRepayEstimate::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingRepayEstimateLibService.class).remove(Wrappers.<FundFinancingRepayEstimateLib>lambdaQuery().eq(FundFinancingRepayEstimate::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingRepayActualService.class).remove(Wrappers.<FundFinancingRepayActual>lambdaQuery().eq(FundFinancingRepayActual::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingRepayActualLibService.class).remove(Wrappers.<FundFinancingRepayActualLib>lambdaQuery().eq(FundFinancingRepayActual::getFinancingId, financingId));
        SpringUtil.getBean(FundFinancingCreditRefService.class).remove(Wrappers.<FundFinancingCreditRef>lambdaQuery().eq(FundFinancingCreditRef::getFinancingId, financingId));
        monthlyStampDutyService.removeStampDuty(financingId, StampDutyTypeEnum.FIN_FIN);
    }

    public void checkFinancingAmount(Long financingId, Long financingAmount) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        long remainingCreditLimit = financingBaseInfoService.getRemainingCreditLimit(financingBaseInfo).getValue();
        // 当前融资金额 + 剩余额度 不超过目标融资金额即可（考虑修改融资金额的场景，要把自己占用的额度也计算进去）
        Assert.isTrue((remainingCreditLimit + financingBaseInfo.getFinancingAmount()) >= financingAmount, () -> MithrasException.newException("融资金额大于剩余总授信额度，请重新输入"));
    }

    public void checkFinancingAmount(FundCredit fundCredit, Long financingAmount) {
        Assert.notNull(fundCredit, () -> MithrasException.newException("授信数据不存在"));
        Long fundCreditId = fundCredit.getId();
        CreditLimitDetailBO creditLimitDetailBO = creditService.queryLimitDetail(fundCredit, false);
//        Map<Long, FundCreditService.LimitDto> map = creditService.calculateEveryLimit(Collections.singletonList(fundCredit));
        long remainingCreditAmount = fundCredit.getTotalCreditLimit();
        if (Objects.nonNull(creditLimitDetailBO)) {
            remainingCreditAmount = creditLimitDetailBO.getTotalLimit() - creditLimitDetailBO.getOccupyTotalLimit();
        }
        Assert.isTrue(remainingCreditAmount >= financingAmount, () -> MithrasException.newException("融资金额大于剩余总授信额度，请重新输入"));
    }

    public FundFinancingChangePreCheckRSP changePreCheck(Long financingId) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        FundFinancingChangePreCheckRSP rsp = new FundFinancingChangePreCheckRSP();
        if (Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.NEW.name())) {
            throw new MithrasException("生效状态才可发起贷后变更");
        }
        if (Objects.equals(financingBaseInfo.getProcessStatus(), FundFinancingProcessStatus.CHANGING_UNDER_APPROVAL.name())) {
            throw new MithrasException("变更审批中，不允许同时发生多个贷后变更");
        }
        if (Objects.equals(financingBaseInfo.getProcessStatus(), FundFinancingProcessStatus.CHANGING_UN_SUBMIT.name())) {
            rsp.setIsChanging(Boolean.TRUE);
            rsp.setChangeSubType(financingBaseInfo.getChangeSubType());
        } else {
            rsp.setIsChanging(Boolean.FALSE);
        }
        return rsp;
    }

    public FundFinancingListRSP pageList(FundFinancingListREQ req) {
        FundFinancingListRSP listrsp = new FundFinancingListRSP();
        Page<FundFinancingBaseInfo> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<FundFinancingBaseInfo> conditionQuery = this.buildQuery(req);
        Page<FundFinancingBaseInfo> dbResult = financingBaseInfoService.page(pageQuery, conditionQuery);
        Page<FundFinancingBaseInfo> dbResultAll = financingBaseInfoService.page(new Page<>(1, Integer.MAX_VALUE), conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            listrsp.setRecords(PageR.empty(req.getPage(), req.getPageSize()));
            return listrsp;
        }
        List<FundFinancingBaseInfo> dbList = dbResult.getRecords();
        List<FundFinancingBaseInfo> dbListAll = dbResultAll.getRecords();
        Set<Long> financingIds = new HashSet<>();
        Set<Long> createUserIds = new HashSet<>();
        for (FundFinancingBaseInfo financingBaseInfo : dbListAll) {
            financingIds.add(financingBaseInfo.getId());
            createUserIds.add(financingBaseInfo.getCreateBy());
        }
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(createUserIds);
        // 批量查询融资方案
        Map<Long, FundFinancingPlan> financingPlanMap = financingPlanService.getMapByFinancingIds(financingIds);
        // 批量查询融资机构
        Map<Long, List<FundFinancingCreditRef>> orgMap = financingCreditRefService.queryBatchByFinancingId(financingIds);
        Set<Long> orgIds = orgMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
        // 批量查询质押明细
        Map<Long, List<FundFinancingPledgeInfo>> financingPledgeMap = financingPledgeInfoService.getMapByFinancingIds(financingIds);
        List<FundReceiptRepayBaseInfo> repayBaseInfos = fundReceiptRepayBaseInfoMapper.selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().eq(FundReceiptRepayBaseInfo::getReceiptRepayState, ReceiptRepayState.NO_WRITE_OFF.name())
                .in(FundReceiptRepayBaseInfo::getFinancingId, financingIds));
        Map<Long, List<FundReceiptRepayBorrowing>> rbMap = new HashMap<>();
        if (CollUtil.isNotEmpty(repayBaseInfos)) {
            List<Long> repayIds = repayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
//            orMap = repayBaseInfos.stream().collect(Collectors.groupingBy(FundReceiptRepayBaseInfo::getFinancingOrgId));
            List<FundReceiptRepayBorrowing> borrowings = fundReceiptRepayBorrowingMapper.selectList(Wrappers.<FundReceiptRepayBorrowing>lambdaQuery().eq(FundReceiptRepayBorrowing::getWriteOffState, CashFlowState.WRITTEN_OFF.name()).in(FundReceiptRepayBorrowing::getReceiptRepayId, repayIds));
            rbMap = borrowings.stream().collect(Collectors.groupingBy(FundReceiptRepayBorrowing::getReceiptRepayId));
        }
//        List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlowList = fundReceiptRepayCashFlowService.list(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery().in(FundReceiptRepayCashFlow::getFinancingId, financingIds)
//                .ne(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name()));
//        List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlowList = fundReceiptRepayCashFlowService.list(
//                Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
//                        .in(FundReceiptRepayCashFlow::getFinancingId, financingIds)
//                        .gt(FundReceiptRepayCashFlow::getRepayDate, LocalDate.now())
//        );
//        // FIXME 由于间融和直融共用收付款的表，且两者会存在相同id，理论上fund_receipt_repay_base_info表中可以通过financing_type来区分
//        // FIXME 但是赶时间修复bug，这里使用融资编号前缀过滤的方法去掉直融的数据，后续修改数据查询方式
//        if (CollectionUtil.isNotEmpty(fundReceiptRepayCashFlowList)) {
//            fundReceiptRepayCashFlowList.removeIf(e -> (StrUtil.isNotBlank(e.getCashFlowCode()) && e.getCashFlowCode().startsWith("ZR")));
//        }
//        Map<Long, List<FundReceiptRepayCashFlow>> fundReceiptRepayCashFlowMap = fundReceiptRepayCashFlowList.stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getFinancingId));
        Map<Long, Long> fundReceiptRepayCashFlowMap = receiptRepayBaseInfoService.queryRemainingAmount(financingIds, FinancingTypeEnum.INDIRECT);
        // 处理返回对象
//        Map<Long, List<FundReceiptRepayBaseInfo>> finalOrMap = orMap;
        Map<Long, List<FundReceiptRepayBorrowing>> finalRbMap = rbMap;
        List<FundFinancingListRSP.FundFinancingList> rspList = dbList.stream().map(item -> {
            FundFinancingListRSP.FundFinancingList rsp = FundFinancingConvert.toFundFinancingList(item, financingPlanMap.get(item.getId()), financingPledgeMap.get(item.getId()), orgMap.get(item.getId()));
            // 填充创建人
            rsp.setCreateUserId(item.getCreateBy());
            rsp.setCreateUserName(userNameMap.get(item.getCreateBy()));
            rsp.setLastPrincipal(fundReceiptRepayCashFlowMap.get(item.getId()));
            if(CollectionUtil.isNotEmpty(rsp.getOrganizationId())){
                rsp.setOrganizationName(rsp.getOrganizationId().stream().map(orgIdNameMap::get).collect(Collectors.toList()));
            }
            return rsp;
        }).collect(Collectors.toList());
        listrsp.setRecords(PageR.of(rspList, dbResult.getTotal(), req.getPage(), req.getPageSize()));
        FundFinancingListRSP.Sum sum = new FundFinancingListRSP.Sum();
        BigDecimal financingCostSum = BigDecimal.ZERO;
        BigDecimal interestRate = BigDecimal.ZERO;
        for (FundFinancingBaseInfo item : dbListAll) {
            FundFinancingListRSP.FundFinancingList rsp = FundFinancingConvert.toFundFinancingList(item, financingPlanMap.get(item.getId()), financingPledgeMap.get(item.getId()), orgMap.get(item.getId()));
            rsp.setLastPrincipal(fundReceiptRepayCashFlowMap.get(item.getId()));
            sum.setFinancingAmount(LongUtil.null2zero(sum.getFinancingAmount()) + LongUtil.null2zero(rsp.getFinancingAmount()));
            sum.setLastPrincipal(LongUtil.null2zero(sum.getLastPrincipal()) + LongUtil.null2zero(rsp.getLastPrincipal()));
            sum.setGuaranteeFinancingAmount(LongUtil.null2zero(sum.getGuaranteeFinancingAmount()) + LongUtil.null2zero(rsp.getGuaranteeFinancingAmount()));
            sum.setCreditFinancingAmount(LongUtil.null2zero(sum.getCreditFinancingAmount()) + LongUtil.null2zero(rsp.getCreditFinancingAmount()));
            interestRate = interestRate.add(new BigDecimal(Optional.ofNullable(rsp.getInterestRate()).orElse(0)));
            // 综合融资成本合计: sum（剩余本金*综合融资成本）/剩余本金之和
            if(rsp.getComprehensiveFinancingCost() != null) {
                financingCostSum = financingCostSum.add(new BigDecimal(Optional.ofNullable(rsp.getLastPrincipal()).orElse(0L)).multiply(new BigDecimal(rsp.getComprehensiveFinancingCost())));
            }
        }
        if (!financingCostSum.equals(BigDecimal.ZERO) && sum.getLastPrincipal() != 0) {
            sum.setComprehensiveFinancingCost(financingCostSum.divide(new BigDecimal(sum.getLastPrincipal()), 0, RoundingMode.HALF_UP).intValue());
        } else {
            sum.setComprehensiveFinancingCost(0);
        }
        if(CollectionUtil.isNotEmpty(rspList)){
            sum.setInterestRate(interestRate.divide(BigDecimal.valueOf(dbResult.getTotal()), 10, RoundingMode.HALF_UP).intValue());
        }else{
            sum.setInterestRate(0);
        }
        listrsp.setSum(sum);
        return listrsp;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long create(Long fundCreditId, Long financingAmount, String businessType) {
        // 查询授信信息
        FundCredit fundCredit = creditService.getById(fundCreditId);
        Assert.notNull(fundCredit, () -> MithrasException.newException("授信不存在"));
        boolean effectiveDataNonNull = Objects.nonNull(fundCredit.getEffectiveDateFrom()) && Objects.nonNull(fundCredit.getEffectiveDateTo());
        Assert.isTrue(effectiveDataNonNull, () -> MithrasException.newException("授信有效时间缺失"));
        LocalDate now = LocalDate.now();
        boolean creditEffective = now.isAfter(fundCredit.getEffectiveDateFrom()) || now.isBefore(fundCredit.getEffectiveDateTo());
        Assert.isTrue(creditEffective, () -> MithrasException.newException("授信已过期"));
        // 校验金额
        this.checkFinancingAmount(fundCredit, financingAmount);
        // 创建基本信息
        FundFinancingBaseInfo financingBaseInfo = this.createBaseInfo(fundCredit, financingAmount, businessType);
        // 创建融资方案
        this.createPlan(financingBaseInfo);
        financingCreditRefService.removeRefByFinancingId(financingBaseInfo.getId());
        financingCreditRefService.saveRef(financingBaseInfo.getId() ,fundCreditId, fundCredit.getOrganizationId());
        return financingBaseInfo.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long createSyndication(Long financingAmount) {
        // 创建基本信息
        FundFinancingBaseInfo financingBaseInfo = this.createBaseInfoSyndication(financingAmount);
        // 创建融资方案
        FundFinancingPlan financingPlan = new FundFinancingPlan();
        financingPlan.setFinancingId(financingBaseInfo.getId());
        financingPlanService.save(financingPlan);
        return financingBaseInfo.getId();
    }

    public void close(Long financingId) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        // 新建生效状态下的才允许作废
        if (!Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.NEW.name())
                && !Objects.equals(financingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.EFFECT.name())) {
            throw new MithrasException("只有新建生效状态才允许作废");
        }
        // 新建审批中不允许作废
        if (Objects.equals(financingBaseInfo.getProcessStatus(), FundFinancingProcessStatus.NEW_UNDER_APPROVAL.name())
                && Objects.equals(financingBaseInfo.getProcessStatus(),FundFinancingProcessStatus.CHANGING_UNDER_APPROVAL)) {
            throw new MithrasException("处于流程中，不允许作废");
        }
        FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
        toUpdate.setId(financingId);
        toUpdate.setFinancingStatus(FundFinancingStatusEnum.CLOSE.name());
        financingBaseInfoService.updateById(toUpdate);
        monthlyStampDutyService.removeStampDuty(financingId, StampDutyTypeEnum.FIN_FIN);
        //释放授信的占用额度释放
        creditService.release(financingBaseInfo.getId(), financingBaseInfo.getFinancingAmount());
    }

    @Transactional(rollbackFor = Throwable.class)
    public String effect(FundFinancingSubmitREQ req) {
        // 前置校验 通过返回基本信息对象
        FundFinancingBaseInfo baseInfo = this.checkBeforeEffect(req.getId());
        List<FundOrganization> organizationList = organizationService.getByFinancingId(req.getId());
        // 固化动态数据
        financingBaseInfoService.fixDynamicData(baseInfo);
        if (Objects.equals(baseInfo.getFinancingStatus(), FundFinancingStatusEnum.NEW.name())) {
            // 是否在流程中
            boolean isInProcess = FundFinancingProcessStatus.isInProcess(baseInfo.getProcessStatus());
            if (isInProcess) {
                throw new MithrasException("已提交审批，请勿重复提交");
            }
            // 调整状态
            FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.SUBMIT_APPROVAL, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
            stateMachine.execute(context, null);
            // 生成审批流

            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setModelKey(ProcessModelTypeEnum.FundFinancingCreateFlow.name());
            startProcessReq.setBusinessKey(String.valueOf(req.getId()));
            startProcessReq.setProcessInstanceName(String.format("%s的%s万元%s", organizationList.get(0).getOrganizationName(), Util.toWanYuan(baseInfo.getFinancingAmount()), ProcessModelTypeEnum.FundFinancingCreateFlow.getDisplay()));
            startProcessReq.setSubModule(FundFinancingFlowSubModuleEnum.CREATE_ALL.name());
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            startProcessReq.setStartUserId(String.valueOf(currentUserId));
            List<OrgDO> orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
            if (CollectionUtil.isNotEmpty(orgList)) {
                startProcessReq.setStartUserDeptId(String.valueOf(orgList.get(0).getId()));
            }
            String start = flowProcessApiService.start(startProcessReq);
            occupyCredit(baseInfo, null);
            return start;
        } else if (Objects.equals(baseInfo.getFinancingStatus(), FundFinancingStatusEnum.EFFECT.name())) {
            // 是否在流程中
            boolean isInProcess = FundFinancingProcessStatus.isInProcess(baseInfo.getProcessStatus());
            if (isInProcess) {
                throw new MithrasException("已提交审批，请勿重复提交");
            }
            // 检查数据是否变动
            ChangeDTO changeDTO = financingLibVersionService.checkActualChange(req.getId());
            if (!Boolean.TRUE.equals(changeDTO.getChangeFlag())) {
                throw new MithrasException("数据未变动，无需提交数据");
            }

            // checkOnly=true 则直接返回
            if (req.getOnlyCheck()) {
                return null;
            }
            //校验变更说明，并插入变更说明
            req.getRemarkAddREQ().check();
            getBean(ProcessModifyRemarkService.class).saveOrUpdateByKey(copyProperties(req.getRemarkAddREQ(), ProcessModifyRemark.class));

            // 调整状态
            FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.SUBMIT_APPROVAL, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
            stateMachine.execute(context, null);
            // 生成审批流
            StartProcessReq startProcessReq = new StartProcessReq();
            startProcessReq.setModelKey(ProcessModelTypeEnum.FundFinancingModifyFlow.name());
            startProcessReq.setBusinessKey(String.valueOf(req.getId()));
            startProcessReq.setProcessInstanceName(String.format("%s的%s万元%s", organizationList.get(0).getOrganizationName(), Util.toWanYuan(baseInfo.getFinancingAmount()), ProcessModelTypeEnum.FundFinancingModifyFlow.getDisplay()));
            startProcessReq.setSubModule(FundFinancingFlowSubModuleEnum.MODIFY_ALL.name());
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            startProcessReq.setStartUserId(String.valueOf(currentUserId));
            List<OrgDO> orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
            if (CollectionUtil.isNotEmpty(orgList)) {
                startProcessReq.setStartUserDeptId(String.valueOf(orgList.get(0).getId()));
            }
            String start = flowProcessApiService.start(startProcessReq);
            occupyCredit(baseInfo, null);
            return start;
        } else {
            Assert.isTrue(Objects.equals(baseInfo.getProcessStatus(), FundFinancingProcessStatus.CHANGING_UN_SUBMIT.name()), () -> MithrasException.newException("数据未变动"));
            // 调整状态
            FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.CONFIRM, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
            stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_OTHER.name());
            // 校验是否修改了被监管合同的还款账户，如修改-检查关联资产端的合同在结清当月是否已发送还款通知书，如果已发送需触发该合同的还款通知书流程
            financingPledgeInfoService.checkPledgeChange(baseInfo.getId());
            // 生成版本
            financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
            // 通知收付款模块尝试更新还款表
            receiptRepayBaseInfoService.processFinancingEffectEnd(req.getId());
            return null;
        }
    }

    /**
     * 占用额度
     * @param baseInfo
     * @param occupyTotalAmount 占用总额度，可以为空（占用融资金额）
     */
    public void occupyCredit(FundFinancingBaseInfo baseInfo, Long occupyTotalAmount) {
        FundFinancingPlan fundFinancingPlan = financingPlanService.getOne(Wrappers.<FundFinancingPlan>lambdaQuery()
                .eq(FundFinancingPlan::getFinancingId, baseInfo.getId()));
        List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByFinancingId(baseInfo.getId());
        List<Long> orgIdList = financingCreditRefList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList());
        Map<Long, FundCredit> effectCreditByOrgIds = creditService.getEffectCreditByOrgIds(orgIdList);
        List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList = JSON.parseArray(fundFinancingPlan.getGuaranteeInfo(), FundFinancingPlan.GuaranteeInfo.class);
        Map<Long, List<FundFinancingPlan.GuaranteeInfo>> guaranteeMap = Optional.ofNullable(guaranteeInfoList).map(m -> m.stream()
                .collect(Collectors.groupingBy(FundFinancingPlan.GuaranteeInfo::getOrganizationId))).orElse(Collections.emptyMap());
        if(orgIdList.size() > 1) {
            List<FundFinancingBaseInfo.OrganizationInfo> organizationInfoList = JSON.parseArray(baseInfo.getOrganizationInfo(), FundFinancingBaseInfo.OrganizationInfo.class);
            Map<Long, Long> financingAmountMap = Optional.ofNullable(organizationInfoList).map(m -> m.stream()
                    .collect(Collectors.toMap(FundFinancingBaseInfo.OrganizationInfo::getOrganizationId, FundFinancingBaseInfo.OrganizationInfo::getOrganizationAmount))).orElse(Collections.emptyMap());
            for (Long orgId : orgIdList) {
                CreditLimitOccupyBO occupyBO = new CreditLimitOccupyBO();
                FundCredit fundCredit = effectCreditByOrgIds.get(orgId);
                occupyBO.setBizTargetKey(String.valueOf(baseInfo.getId()));
                occupyBO.setGrantSubjectKey(String.valueOf(orgId));
                occupyBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
                occupyBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
                List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList1 = guaranteeMap.getOrDefault(orgId, Collections.emptyList());
                if(occupyTotalAmount != null){
                    BigDecimal amountRate = BigDecimal.valueOf(financingAmountMap.get(orgId)).divide(BigDecimal.valueOf(baseInfo.getFinancingAmount()), 10, RoundingMode.HALF_UP);
                    occupyBO.setAmount(amountRate.multiply(BigDecimal.valueOf(occupyTotalAmount)).longValue());
                    long guaranteeAmount = guaranteeInfoList1.stream().mapToLong(m -> Optional.ofNullable(m).map(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).orElse(0L)).sum();
                    if(guaranteeAmount != 0) {
                        BigDecimal guaranteeRate = BigDecimal.valueOf(guaranteeAmount).divide(BigDecimal.valueOf(financingAmountMap.get(orgId)), 10, RoundingMode.HALF_UP);
                        occupyBO.setGuaranteeAmount(guaranteeRate.multiply(BigDecimal.valueOf(occupyTotalAmount)).longValue());
                    }else {
                        occupyBO.setGuaranteeAmount(0L);
                    }
                }else {
                    occupyBO.setAmount(financingAmountMap.get(orgId));
                    occupyBO.setGuaranteeAmount(guaranteeInfoList1.stream().mapToLong(m -> Optional.ofNullable(m).map(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).orElse(0L)).sum());
                }

                occupyBO.setHappenDate(LocalDate.now());
                creditLimitManagerService.occupy(occupyBO);
            }
        }else{
            Long orgId = orgIdList.get(0);
            FundCredit fundCredit = effectCreditByOrgIds.get(orgId);
            CreditLimitOccupyBO occupyBO = new CreditLimitOccupyBO();
            occupyBO.setBizTargetKey(String.valueOf(baseInfo.getId()));
            occupyBO.setGrantSubjectKey(String.valueOf(orgId));
            occupyBO.setBizSourceKey(String.valueOf(fundCredit.getId()));
            occupyBO.setBizType(CreditLimitBizTypeEnum.FUND.name());
            List<FundFinancingPlan.GuaranteeInfo> guaranteeInfoList1 = guaranteeMap.getOrDefault(orgId, Collections.emptyList());
            if(occupyTotalAmount != null){
                occupyBO.setAmount(occupyTotalAmount);
                long guaranteeAmount = guaranteeInfoList1.stream().mapToLong(m -> Optional.ofNullable(m).map(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).orElse(0L)).sum();
                if(guaranteeAmount != 0) {
                    BigDecimal guaranteeRate = BigDecimal.valueOf(guaranteeAmount).divide(BigDecimal.valueOf(baseInfo.getFinancingAmount()), 10, RoundingMode.HALF_UP);
                    occupyBO.setGuaranteeAmount(guaranteeRate.multiply(BigDecimal.valueOf(occupyTotalAmount)).longValue());
                }else {
                    occupyBO.setGuaranteeAmount(0L);
                }
            }else {
                occupyBO.setAmount(baseInfo.getFinancingAmount());
                occupyBO.setGuaranteeAmount(guaranteeInfoList1.stream().mapToLong(m -> Optional.ofNullable(m).map(FundFinancingPlan.GuaranteeInfo::getGuaranteeAmount).orElse(0L)).sum());
            }
            occupyBO.setHappenDate(LocalDate.now());
            creditLimitManagerService.occupy(occupyBO);

        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void carryInterest(FundFinancingEffectREQ req) {
        //校验是否可以起息
        FundFinancingBaseInfo existBaseInfo = this.checkCarryInterest(req);
        FundFinancingBaseInfo baseInfo = new FundFinancingBaseInfo();
        baseInfo.setId(req.getFinancingId());
        baseInfo.setRepayDay(req.getRepaymentDate());
        baseInfo.setActualLoanDate(req.getActualLoanDate());
        // 修改状态
        //baseInfo.setFinancingStatus(FundFinancingStatusEnum.CARRY_INTEREST.name());
        financingBaseInfoService.updateById(baseInfo);
        // 查询融资机构
        String orgName = existBaseInfo.getOrganizationInfo();
        List<FundFinancingCreditRef> orgList = financingCreditRefService.queryByFinancingId(req.getFinancingId());
        if(CollectionUtil.isNotEmpty(orgList)){
            String orgNewName = "";
            Set<Long> orgIds = orgList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
            Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
            for(FundFinancingCreditRef fundFinancingCreditRef : orgList){
                orgNewName += orgIdNameMap.get(fundFinancingCreditRef.getOrganizationId())+",";
            }
            orgNewName = orgNewName.substring(0, orgNewName.length() - 1);
            orgName = orgNewName;
        }
        //流程发起
        startCarryInterest(req.getFinancingId(),orgName,ProcessModelTypeEnum.IndirectFinancingCarryInterestFlow.name());
//        // 固化动态数据
////        financingBaseInfoService.fixDynamicData(existBaseInfo);
//        // 生成数据版本
//        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
//        // 通知收付款模块尝试更新还款表
//        receiptRepayBaseInfoService.processFinancingEffectEnd(req.getFinancingId());
//        // 向对应的资金经理推送归档的待办
//        startFileProcessFinancing(existBaseInfo);
//        //计算ftp
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                try {
//                    SpringContextHolder.getBean(FtpIncomeBaseInfoService.class).createOrUpdate(baseInfo.getId(), FinancingTypeEnum.INDIRECT.name(), null);
//                } catch (Exception e) {
//                    log.error("计算间融ftp收益失败 financingCode = {}, financingId = {}", baseInfo.getFinancingCode(), baseInfo.getId(), e);
//                }
//            }
//        });
    }

    //发起融资起息审批流程
    @Transactional(rollbackFor = Throwable.class)
    public void startCarryInterest(Long businessKey,String name,String modelKey) {
        //流程发起
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(modelKey);
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(businessKey));
        startProcessReq.setProcessInstanceName(String.format("【%s】融资起息审批流程",name));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(sysUserService.getUserDept()).map(OrgDO::getId).map(Objects::toString).orElse(""));
        //资金核销岗位
        SysUserService userService = SpringContextHolder.getBean(SysUserService.class);
        Set<Long> userIds = userService.getUserIdsByRole("ZJGLB-ZJHXG");
        List<String> fundWriteOffList = new LinkedList<>();
        for(Long userId : userIds){
            fundWriteOffList.add(String.valueOf(userId));
        }
        // 设置启动参数
        Map<String, Object> initParamMap = new HashMap<>();
        //资金核销岗
        initParamMap.put("fundWriteOff", fundWriteOffList);
        startProcessReq.setVariables(initParamMap);
        processApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void confirmChangeLpr(Long financingId) {
        FundFinancingBaseInfo baseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(baseInfo, () -> MithrasException.newException("融资数据不存在"));
        Assert.notNull(baseInfo.getRepayDay(), () -> MithrasException.newException("还款日不能为空"));
        Assert.notNull(baseInfo.getActualLoanDate(), () -> MithrasException.newException("实际贷款日期不能为空"));
        Assert.isTrue(this.checkRepayActualIsChange(baseInfo), () -> MithrasException.newException("实际还款表未发生变化"));
        Assert.isTrue(Objects.equals(baseInfo.getApprovalStatus(), FundFinancingProcessStatus.CHANGING_UN_SUBMIT.name()), () -> MithrasException.newException("数据未变动"));
        Assert.isTrue(Objects.equals(baseInfo.getChangeSubType(), FundFinancingChangeSubTypeEnum.CHANGE_LPR.name()), () -> MithrasException.newException("变更类型不符合当前操作类型"));
        List<FundFinancingRepayActual> repayActualList = financingRepayActualService.listByFinancingId(financingId);
        Assert.notEmpty(repayActualList, () -> MithrasException.newException("实际还款表不能为空"));
        repayActualList.sort(Comparator.comparing(FundFinancingRepayActual::getPhase));
        FundFinancingRepayActual first = repayActualList.get(0);
        Assert.isTrue(baseInfo.getActualLoanDate().isEqual(first.getRepayDate()), () -> MithrasException.newException("实际贷款日期必须和实际还款表第1行日期一致"));
        // 固化动态数据
//        financingBaseInfoService.fixDynamicData(baseInfo);
        // 变更状态
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.CONFIRM, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_LPR.name());
        // 生成数据版本
        financingLibVersionService.recordVersion(baseInfo.getMainId(), VersionTypeEnum.EFFECT, AccountUtil.getLoginInfo().getId(), null, VersionTypeConstants.NORMAL);
        // 通知收付款模块尝试更新还款表
        receiptRepayBaseInfoService.processFinancingEffectEnd(financingId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public String submitEarlySettle(Long financingId) {
        FundFinancingBaseInfo baseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(baseInfo, () -> MithrasException.newException("融资数据不存在"));
        FundFinancingEarlySettlePlan earlySettlePlan = financingEarlySettlePlanService.getOneByFinancingId(financingId);
        Assert.notNull(earlySettlePlan, () -> MithrasException.newException("还款方案必填项未填写"));
        Assert.isTrue(this.checkRepayActualIsChange(baseInfo), () -> MithrasException.newException("实际还款表未发生变化"));
        // 是否在流程中
        boolean isInProcess = FundFinancingProcessStatus.isInProcess(baseInfo.getProcessStatus());
        if (isInProcess) {
            throw new MithrasException("已提交审批，请勿重复提交");
        }
        // 状态变更
        FundFinancingContext<FundFinancingBaseInfo> context = FundFinancingContext.of(baseInfo, FundFinancingEvent.SUBMIT_APPROVAL, FundFinancingProcessStatus.of(baseInfo.getApprovalStatus()));
        stateMachine.execute(context, FundFinancingChangeSubTypeEnum.CHANGE_EARLY_SETTLE.name());
        List<FundOrganization> organizationList = organizationService.getByFinancingId(financingId);
        // 生成审批流
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.FundFinancingEarlySettleFlow.name());
        startProcessReq.setBusinessKey(String.valueOf(financingId));
        startProcessReq.setProcessInstanceName(String.format("%s的%s万元%s", organizationList.get(0).getOrganizationName(), Util.toWanYuan(baseInfo.getFinancingAmount()), ProcessModelTypeEnum.FundFinancingEarlySettleFlow.getDisplay()));
        startProcessReq.setSubModule(FundFinancingFlowSubModuleEnum.EARLY_SETTLE.name());
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        startProcessReq.setStartUserId(String.valueOf(currentUserId));
        List<OrgDO> orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
        if (CollectionUtil.isNotEmpty(orgList)) {
            startProcessReq.setStartUserDeptId(String.valueOf(orgList.get(0).getId()));
        }
        return flowProcessApiService.start(startProcessReq);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void notifyFinancingSettle(Long financingId, boolean isDirect) {
        log.info("收付款通知可结清[financingId = {}]", financingId);
        Assert.notNull(financingId, () -> MithrasException.newException("融资id不能为空"));
        if (isDirect) {
            // do nothing
        } else {
            FundFinancingBaseInfo toUpdate = new FundFinancingBaseInfo();
            toUpdate.setId(financingId);
            toUpdate.setFinancingStatus(FundFinancingStatusEnum.SETTLE.name());
            financingBaseInfoService.updateById(toUpdate);
            // 生成数据版本
            financingLibVersionService.recordVersion(financingId, VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
        }
    }

    private FundFinancingBaseInfo checkBeforeEffect(Long financingId) {
        // 基本信息
        FundFinancingBaseInfo baseInfo = financingBaseInfoService.getById(financingId);
        Assert.notNull(baseInfo, () -> MithrasException.newException("基本信息不存在"));
        if(!Objects.equals(baseInfo.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())) {
            Assert.notNull(baseInfo.getTotalCreditLimit(), () -> MithrasException.newException("基本信息-总授信额度不能为空"));
        }
        Assert.notBlank(baseInfo.getTimeLimitType(), () -> MithrasException.newException("基本信息-融资期限类型不能为空"));
        Assert.notBlank(baseInfo.getBusinessType(), () -> MithrasException.newException("基本信息-业务类型不能为空"));
        Assert.notBlank(baseInfo.getFundsPurpose(), () -> MithrasException.newException("资金用途不能为空"));
        Assert.notNull(baseInfo.getFundManagerId(), () -> MithrasException.newException("资金经理不能为空"));
        Assert.notNull(baseInfo.getDeptId(), () -> MithrasException.newException("所属部门不能为空"));
        Assert.notNull(baseInfo.getBizHeaderId(), () -> MithrasException.newException("部门负责人不能为空"));
        Assert.notNull(baseInfo.getLeaderId(), () -> MithrasException.newException("分管领导不能为空"));
        Assert.notNull(baseInfo.getPlanLoanDate(), () -> MithrasException.newException("计划贷款日期不能为空"));
        // 融资方案
        FundFinancingPlan plan = financingPlanService.getOneByFinancingId(financingId);
        Assert.notNull(plan, () -> MithrasException.newException("融资方案不存在"));
        Assert.notNull(plan.getFinancingAmount(), () -> MithrasException.newException("融资金额不能为空"));
        Assert.notNull(plan.getFinancingMonth(), () -> MithrasException.newException("融资期限不能为空"));
        Assert.notNull(plan.getRepayTimes(), () -> MithrasException.newException("还款期数不能为空"));
        Assert.notBlank(plan.getRepayFrequency(), () -> MithrasException.newException("还款频率不能为空"));
        Assert.notBlank(plan.getRepayWay(), () -> MithrasException.newException("还款方式不能为空"));
        Assert.notNull(plan.getInterestAmount(), () -> MithrasException.newException("预计利息金额不能为空"));
        Assert.notNull(plan.getInterestRateType(), () -> MithrasException.newException("利率类型不能为空"));
        Assert.notNull(plan.getLprType(), () -> MithrasException.newException("LPR品种不能为空"));
        Assert.notNull(plan.getLprRatePercent(), () -> MithrasException.newException("LPR利率不能为空"));
        Assert.notNull(plan.getLprAddPercent(), () -> MithrasException.newException("LPR加点不能为空"));
        // 还款概算表
        List<FundFinancingRepayEstimate> repayEstimateList = financingRepayEstimateService.listByFinancingId(financingId);
        Assert.notEmpty(repayEstimateList, () -> MithrasException.newException("还款概算表不能为空"));
        // 融资金额校验
        if(!Objects.equals(baseInfo.getBusinessType(), FundFinancingBizTypeEnum.SYNDICATIONS.name())) {
            financingService.checkFinancingAmount(financingId, plan.getFinancingAmount());
        }
        // 还本账户校验
        List<FundFinancingPayAccount> payAccountList = financingPayAccountService.list(Wrappers.<FundFinancingPayAccount>lambdaQuery().eq(FundFinancingPayAccount::getFinancingId, financingId));
        if(CollectionUtil.isEmpty(payAccountList)) {
            throw new MithrasException("请维护我司还款账户信息后提交");
        }
        List<String> accountCategoryList = payAccountList.stream().map(FundFinancingPayAccount::getAccountCategory).collect(Collectors.toList());
        if(accountCategoryList.contains(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name())){

        }else if(accountCategoryList.contains(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL.name()) && accountCategoryList.contains(FundFinancingAccountTypeEnum.REPAY_INTEREST.name())){

        }else {
            throw new MithrasException("请维护我司还款账户信息后提交");
        }
        return baseInfo;
    }

    private FundFinancingBaseInfo checkCarryInterest(FundFinancingEffectREQ req) {
        FundFinancingBaseInfo baseInfo = financingBaseInfoService.getById(req.getFinancingId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<FundFinancingRepayActual> list = financingRepayActualService.list(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                .eq(FundFinancingRepayActual::getFinancingId, req.getFinancingId())
                .orderByAsc(FundFinancingRepayActual::getPhase));
        if (CollectionUtil.isEmpty(list)) {
            throw new MithrasException("请导入实际还款计划表");
        }
        list.forEach(actual -> {
            if (ObjectUtil.isNull(actual.getCashFlowCode())) {
                throw new MithrasException("现金流编号为空，请重新导入还款计划");
            }
        });
        Assert.isFalse(ObjectUtil.equals(FundFinancingStatusEnum.CARRY_INTEREST.name(), baseInfo.getFinancingStatus()),
                () -> MithrasException.newException("已起息，请勿重复起息"));
        if (ObjectUtil.notEqual(FundFinancingStatusEnum.EFFECT.name(), baseInfo.getFinancingStatus())) {
            throw new MithrasException("不符合融资生效状态");
        }
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(String.valueOf(req.getFinancingId()));
        processPageReq.setModelKey(ProcessModelTypeEnum.IndirectFinancingCarryInterestFlow.name());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        if (Objects.nonNull(processRespPage) && CollectionUtil.isNotEmpty(processRespPage.getContents())) {
            throw new MithrasException("已有起息流程在审批中");
        }
//        // 校验填写的实际贷款日期和还款表第一期是否一致
//        FundFinancingRepayActual first = list.get(0);
//        Assert.isTrue(req.getActualLoanDate().isEqual(first.getRepayDate()), () -> MithrasException.newException("填写的实际贷款时间和实际还款表第一行时间不一致"));
        return baseInfo;
    }

    private LambdaQueryWrapper<FundFinancingBaseInfo> buildQuery(FundFinancingListREQ req) {
        List<FundFinancingCreditRef> financingCreditRefList = financingCreditRefService.queryByOrgId(req.getOrganizationId());

        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionUtil.isNotEmpty(financingCreditRefList), FundFinancingBaseInfo::getId, financingCreditRefList.stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList()));
        query.lt(Objects.nonNull(req.getOrganizationId()) && CollectionUtil.isEmpty(financingCreditRefList), FundFinancingBaseInfo::getId, 0);
        query.ge(Objects.nonNull(req.getFinancingAmountFrom()), FundFinancingBaseInfo::getFinancingAmount, req.getFinancingAmountFrom());
        query.le(Objects.nonNull(req.getFinancingAmountTo()), FundFinancingBaseInfo::getFinancingAmount, req.getFinancingAmountTo());
        query.in(CollectionUtil.isNotEmpty(req.getFinancingStatus()), FundFinancingBaseInfo::getFinancingStatus, req.getFinancingStatus());
        query.eq(Objects.nonNull(req.getMoneyManagerId()), FundFinancingBaseInfo::getFundManagerId, req.getMoneyManagerId());
        query.eq(Objects.nonNull(req.getHasPledgeInfo()), FundFinancingBaseInfo::getHasPledgeInfo, req.getHasPledgeInfo());
        query.in(Objects.nonNull(req.getBusinessTypeList()), FundFinancingBaseInfo::getBusinessType, req.getBusinessTypeList());
        if (StrUtil.isNotBlank(req.getActualLoanDateFrom())) {
            query.ge(FundFinancingBaseInfo::getActualLoanDate, LocalDateTimeUtil.parseDate(req.getActualLoanDateFrom(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getActualLoanDateTo())) {
            query.le(FundFinancingBaseInfo::getActualLoanDate, LocalDateTimeUtil.parseDate(req.getActualLoanDateTo(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getActualExpireDateFrom())) {
            query.ge(FundFinancingBaseInfo::getActualExpireDate, LocalDateTimeUtil.parseDate(req.getActualExpireDateFrom(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getActualExpireDateTo())) {
            query.le(FundFinancingBaseInfo::getActualExpireDate, LocalDateTimeUtil.parseDate(req.getActualExpireDateTo(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getCreateTimeFrom())) {
            query.ge(BaseModel::getCreateTime, LocalDateTimeUtil.parseDate(req.getCreateTimeFrom(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getCreateTimeTo())) {
            query.le(BaseModel::getCreateTime, DateUtil.endOfDay(LocalDateTimeUtil.parseDate(req.getCreateTimeTo(), DatePattern.NORM_DATE_PATTERN)));
        }
        if (StrUtil.isNotBlank(req.getUpdateTimeFrom())) {
            query.ge(BaseModel::getUpdateTime, LocalDateTimeUtil.parseDate(req.getUpdateTimeFrom(), DatePattern.NORM_DATE_PATTERN));
        }
        if (StrUtil.isNotBlank(req.getUpdateTimeTo())) {
            query.le(BaseModel::getUpdateTime, DateUtil.endOfDay(LocalDateTimeUtil.parseDate(req.getUpdateTimeTo(), DatePattern.NORM_DATE_PATTERN)));
        }
        query.like(StrUtil.isNotBlank(req.getFinancingCode()), FundFinancingBaseInfo::getFinancingCode, req.getFinancingCode());
        query.orderByDesc(BaseModel::getUpdateTime);
        return query;
    }

    private FundFinancingBaseInfo createBaseInfo(FundCredit fundCredit, Long financingAmount, String businessType) {
        // 查询融资机构
        FundOrganization organization = organizationService.getById(fundCredit.getOrganizationId());
        Assert.notNull(organization, () -> MithrasException.newException("融资机构不存在"));
        FundFinancingBaseInfo financingBaseInfo = new FundFinancingBaseInfo();
        financingBaseInfo.setBusinessType(businessType);
        financingBaseInfo.setFinancingStatus(FundFinancingStatusEnum.NEW.name());
        financingBaseInfo.setApprovalStatus(FundFinancingProcessStatus.NEW_UN_SUBMIT.name());
        financingBaseInfo.setFinancingAmount(financingAmount);
//        financingBaseInfo.setCreditId(fundCredit.getId());
//        financingBaseInfo.setCreditCode(fundCredit.getCreditCode());
        // 查询同授信编号下已有的融资
        List<FundFinancingBaseInfo> existList = financingBaseInfoService.listByCreditCode(fundCredit.getCreditCode());
        int seq;
        if (CollectionUtil.isEmpty(existList)) {
            seq = 1;
        } else {
            existList.sort(Comparator.comparing(FundFinancingBaseInfo::getSequence));
            seq = existList.get(existList.size() - 1).getSequence() + 1;
        }
//        financingBaseInfo.setCreditCode(fundCredit.getCreditCode());
        financingBaseInfo.setSequence(seq);
        // 融资编号 = 授信编号 + "-" + 两位序列号
        financingBaseInfo.setFinancingCode(String.format("%s-%02d", fundCredit.getCreditCode(), seq));
        financingBaseInfo.setTotalCreditLimit(fundCredit.getTotalCreditLimit());
        // 如果授信中有担保方则自动带出，并按照融资金额/授信金额的比例同比计算担保金额
        List<FundCreditGuaranteeDetail> creditGuaranteeDetailList = creditGuaranteeDetailService.getByCreditId(fundCredit.getId());
        if (CollectionUtil.isNotEmpty(creditGuaranteeDetailList)) {
            Set<Long> ids = creditGuaranteeDetailList.stream().map(FundCreditGuaranteeDetail::getGuaranteeAgencyId).collect(Collectors.toSet());
            List<FundGuaranteeAgency> fundGuaranteeAgencyList = fundGuaranteeAgencyService.listByIds(ids);
            Map<Long, FundGuaranteeAgency> fundGuaranteeAgencyMap = fundGuaranteeAgencyList.stream().collect(Collectors.toMap(FundGuaranteeAgency::getId, e -> e));
            List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = new ArrayList<>(creditGuaranteeDetailList.size());
            for (FundCreditGuaranteeDetail creditGuaranteeDetail : creditGuaranteeDetailList) {
                FundFinancingBaseInfo.GuaranteeInfo guaranteeInfo = new FundFinancingBaseInfo.GuaranteeInfo();
                guaranteeInfo.setGuaranteeAgencyId(creditGuaranteeDetail.getGuaranteeAgencyId());
                FundGuaranteeAgency fundGuaranteeAgency = fundGuaranteeAgencyMap.get(creditGuaranteeDetail.getGuaranteeAgencyId());
                if (Objects.nonNull(fundGuaranteeAgency)) {
                    guaranteeInfo.setGuaranteeAgencyName(fundGuaranteeAgency.getGuaranteeAgencyName());
                }
                // 等比例计算担保金额
                BigDecimal guaranteeAmount = BigDecimal.valueOf(creditGuaranteeDetail.getGuaranteeAmount())
                        .divide(BigDecimal.valueOf(fundCredit.getTotalCreditLimit()), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(financingAmount));
                // 金额数字合法化后存入
                guaranteeInfo.setGuaranteeAmount(Util.mithrasLongDecimalTwo(guaranteeAmount.longValue()));
                guaranteeInfoList.add(guaranteeInfo);
            }
            financingBaseInfo.setGuaranteeInfo(JSONUtil.toJsonStr(guaranteeInfoList));
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        financingBaseInfo.setFundManagerId(currentUserId);
        List<OrgDO> orgList;
        orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
        if (CollectionUtil.isEmpty(orgList)) {
            // 资金经理没有找到的话再找一下资金资金经理
            orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.deepmoneymanager.name());
        }
        Assert.notEmpty(orgList, () -> MithrasException.newException("没有找到当前用户作为资金经理所在部门"));
        Long deptId = orgList.get(0).getId();
        financingBaseInfo.setDeptId(deptId);
//        // 写死鲁桂欣
//        financingBaseInfo.setBizHeaderId(65L);
        List<UserDO> headList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.moneymanagerhead.name());
        if (CollectionUtil.isEmpty(headList)) {
            throw new MithrasException("没有找到<资金业务负责人>岗位对应的人员信息");
        }
        financingBaseInfo.setBizHeaderId(headList.get(0).getId());
        List<Long> leaderIds = sysUserService.queryJobUserIds(JobEnum.financialdirector.name());
        if (CollectionUtil.isNotEmpty(leaderIds)) {
            financingBaseInfo.setLeaderId(leaderIds.get(0));
        }
        financingBaseInfoService.save(financingBaseInfo);
        financingCreditRefService.removeRefByFinancingId(financingBaseInfo.getId());
        financingCreditRefService.saveRef(financingBaseInfo.getId(), fundCredit.getId(), organization.getId());
        return financingBaseInfo;
    }

    private FundFinancingBaseInfo createBaseInfoSyndication(Long financingAmount) {
        // 查询融资机构
        FundFinancingBaseInfo financingBaseInfo = new FundFinancingBaseInfo();
        financingBaseInfo.setBusinessType(FundFinancingBizTypeEnum.SYNDICATIONS.name());
        financingBaseInfo.setFinancingStatus(FundFinancingStatusEnum.NEW.name());
        financingBaseInfo.setApprovalStatus(FundFinancingProcessStatus.NEW_UN_SUBMIT.name());
        financingBaseInfo.setFinancingAmount(financingAmount);
        // 银团不指定授信无需序列号，为了不影响原逻辑 将银团的序列号设置为0
        financingBaseInfo.setSequence(0);
        // 非银团融资编号 = 授信编号 + "-" + 两位序列号 ， 银团融资编号 = 根据融资业务类型为银团的数量拟定一个新的编号 + "-" + 00
        int syndicationCount = financingBaseInfoService.count(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getBusinessType, FundFinancingBizTypeEnum.SYNDICATIONS.name()));
        String lastCreditCode = "DK" + DateUtil.dateString(LocalDate.now(), DateUtil.DATE_PATTERN) + String.format("%04d", syndicationCount + 1);
        financingBaseInfo.setFinancingCode(String.format("%s-%02d", lastCreditCode, 0));
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        financingBaseInfo.setFundManagerId(currentUserId);
        List<OrgDO> orgList;
        orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.moneymanager.name());
        if (CollectionUtil.isEmpty(orgList)) {
            // 资金经理没有找到的话再找一下资金资金经理
            orgList = sysUserService.listOrgByJob(currentUserId, JobEnum.deepmoneymanager.name());
        }
        Assert.notEmpty(orgList, () -> MithrasException.newException("没有找到当前用户作为资金经理所在部门"));
        Long deptId = orgList.get(0).getId();
        financingBaseInfo.setDeptId(deptId);
        List<UserDO> headList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.moneymanagerhead.name());
        if (CollectionUtil.isEmpty(headList)) {
            throw new MithrasException("没有找到<资金业务负责人>岗位对应的人员信息");
        }
        financingBaseInfo.setBizHeaderId(headList.get(0).getId());
        List<Long> leaderIds = sysUserService.queryJobUserIds(JobEnum.financialdirector.name());
        if (CollectionUtil.isNotEmpty(leaderIds)) {
            financingBaseInfo.setLeaderId(leaderIds.get(0));
        }
        financingBaseInfoService.save(financingBaseInfo);
        return financingBaseInfo;
    }

    private void createPlan(FundFinancingBaseInfo financingBaseInfo) {
        FundFinancingPlan financingPlan = new FundFinancingPlan();
        financingPlan.setFinancingId(financingBaseInfo.getId());
        financingPlan.setFinancingAmount(financingBaseInfo.getFinancingAmount());
        if (StrUtil.isNotBlank(financingBaseInfo.getGuaranteeInfo())) {
            List<FundFinancingBaseInfo.GuaranteeInfo> guaranteeInfoList = JSONUtil.toList(financingBaseInfo.getGuaranteeInfo(), FundFinancingBaseInfo.GuaranteeInfo.class);
            List<FundFinancingPlan.GuaranteeAmountInfo> guaranteeAmountInfoList = guaranteeInfoList.stream().map(item -> {
                FundFinancingPlan.GuaranteeAmountInfo guaranteeAmountInfo = new FundFinancingPlan.GuaranteeAmountInfo();
                guaranteeAmountInfo.setGuaranteeAgencyId(item.getGuaranteeAgencyId());
                guaranteeAmountInfo.setGuaranteeAgencyName(item.getGuaranteeAgencyName());
                guaranteeAmountInfo.setGuaranteeFeeRate(4500);
                return guaranteeAmountInfo;
            }).collect(Collectors.toList());
            financingPlan.setGuaranteeAmountInfo(JSONUtil.toJsonStr(guaranteeAmountInfoList));
        }
        financingPlan.setFtpYieldRate(this.getFtpYieldRate(financingPlan.getFinancingMonth()));
        financingPlanService.save(financingPlan);
    }

    //获取最新的ftp
    private Integer getFtpYieldRate(Integer month) {
        NewFtpMonthlyGuidanceExtDraftDetailRSP lastFtpMonthlyGuidance = ftpBaseInfoService.getLastFtpMonthlyGuidance();
        if (ObjectUtil.isEmpty(lastFtpMonthlyGuidance)) {
            return 0;
        }
        return lastFtpMonthlyGuidance.getFtpYieldRate(month);
    }

    private boolean checkRepayActualIsChange(FundFinancingBaseInfo financingBaseInfo) {
        CommonVersion commonVersion = financingLibVersionService.findNewestVersion(financingBaseInfo.getMainId());
        ChangeDTO changeDTO = financingRepayActualLibHandler.checkActualChange(commonVersion);
        return changeDTO.getChangeFlag();
    }

    public void startFileProcessFinancing(FundFinancingBaseInfo financingBaseInfo){
        List<FundOrganization> organizationList = organizationService.getByFinancingId(financingBaseInfo.getId());
//        String fundManagerName = id2NameService.sysUserId2NameSingle(financingBaseInfo.getFundManagerId());
        CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                .processType(ProcessModelTypeEnum.FinancingRecordFlow.name())
                .businessId(String.valueOf(financingBaseInfo.getId()))
                .formName(String.format("%s的%s万元融资档案归档", organizationList.get(0).getOrganizationName(),
                        Util.toWanYuan(financingBaseInfo.getFinancingAmount())))
                .projName(null)
                .clientName(organizationList.get(0).getOrganizationName())
                .currentAssignee(JSON.toJSONString(Collections.singletonList(financingBaseInfo.getFundManagerId())))
                .currentNode("档案归档")
                .applyTime(LocalDateTime.now())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareMapper.insert(prepare);
    }

    @XxlJob("FinancingFloatRateAdjust")
    public void financingFloatRateAdjustTask(){
        List<String> rateTypeList = Arrays.asList(LprArrangeModeEnum.YEAR.name());
        List<FundFinancingPlan> planList = financingPlanService.list(Wrappers.<FundFinancingPlan>lambdaQuery().in(FundFinancingPlan::getLprArrangeMode, rateTypeList));
        if(CollectionUtil.isEmpty(planList)){
            return;
        }
        Map<Long, FundFinancingBaseInfo> baseInfoMap = financingBaseInfoService.listByIds(planList.stream().map(FundFinancingPlan::getFinancingId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));

        for (FundFinancingPlan financingPlan : planList) {
            FundFinancingBaseInfo financingBaseInfo = baseInfoMap.get(financingPlan.getFinancingId());
            LocalDate actualLoanDate = financingBaseInfo.getActualLoanDate();
            if(actualLoanDate == null){
                continue;
            }
            LprArrangeModeEnum modeEnum = LprArrangeModeEnum.of(financingPlan.getLprArrangeMode());
            if(modeEnum == null){
                continue;
            }
            LocalDate latestDate = LocalDate.now();
            while (true) {
                switch (modeEnum) {
                    case YEAR:
                        latestDate = latestDate.minusYears(1);
                        break;
                    default:
                        break;
                }
                if (actualLoanDate.isEqual(latestDate)) {
                    startFinancingFloatRateAdjust(financingBaseInfo);
                    break;
                }
                if(actualLoanDate.isAfter(latestDate)){
                    break;
                }
            }

        }

    }

    public void startFinancingFloatRateAdjust(FundFinancingBaseInfo financingBaseInfo){
        List<FundOrganization> organizationList = organizationService.getByFinancingId(financingBaseInfo.getId());
        String fundManagerName = id2NameService.sysUserId2NameSingle(financingBaseInfo.getFundManagerId());
        CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                .processType(ProcessModelTypeEnum.FinancingFloatRateAdjustFlow.name())
                .businessId(String.valueOf(financingBaseInfo.getId()))
                .formName(String.format("%s的%s融资合同浮动利率调整", organizationList.get(0).getOrganizationName(),
                        Util.toWanYuan(financingBaseInfo.getFinancingAmount())))
                .projName(null)
                .clientName(fundManagerName)
                .currentAssignee(JSON.toJSONString(Collections.singletonList(financingBaseInfo.getFundManagerId())))
                .currentNode("融资合同浮动利率调整")
                .applyTime(LocalDateTime.now())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareMapper.insert(prepare);
    }

}
