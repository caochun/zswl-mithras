package cn.zswltech.mithras.application.orchestration.fund.financing;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswl.notice.message.impl.WebSocketServer;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.mithras.dto.fund.financing.pledge.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.application.orchestration.job.NextMonthRentNotify;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.message.model.MessageModel;
import cn.zswltech.mithras.message.model.PopUpNotificationBody;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractPrice;
import cn.zswltech.mithras.fund.persistence.model.FundCredit;
import cn.zswltech.mithras.fund.persistence.model.financing.*;
import cn.zswltech.mithras.workflow.persistence.model.RentCollectionMonthDetail;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.fund.application.financing.model.FundPledgeSupervisedBO;
import cn.zswltech.mithras.application.orchestration.collection.CollectionService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractPriceService;
import cn.zswltech.mithras.application.orchestration.fund.FundCreditService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingPledgeInfoLibService;
import cn.zswltech.mithras.fund.versioning.financing.handler.impl.FundFinancingPledgeInfoLibHandler;
import cn.zswltech.mithras.workflow.process.prepare.RentCollectionMonthDetailService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FundFinancingPledgeInfoService extends ServiceImpl<FundFinancingPledgeInfoMapper, FundFinancingPledgeInfo> {
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService financingPlanService;
    @Resource
    private FundFinancingPledgeInfoLibService financingPledgeInfoLibService;
    @Resource
    private FundFinancingPledgeInfoLibHandler financingPledgeInfoLibHandler;
    @Resource
    private FundFinancingRepayActualService financingRepayActualService;
    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private FundFinancingPayAccountService fundFinancingPayAccountService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private UserService userService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private NextMonthRentNotify nextMonthRentNotify;
    @Resource
    private RentCollectionMonthDetailService rentCollectionMonthDetailService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;
    @Resource
    private FundFinancingPledgeInfoMapper financingPledgeInfoMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        FundFinancingPledgeInfo info = this.getById(id);
        Assert.notNull(info, () -> MithrasException.newException("质押明细数据不存在"));
        this.removeById(id);
        // 统计剩余的质押明细数据
        int count = this.countByFinancingId(info.getFinancingId());
        if (count > 0) {
            fundFinancingBaseInfoService.updateHasPledge(info.getFinancingId(), YesOrNoNumberEnum.YES);
        } else {
            fundFinancingBaseInfoService.updateHasPledge(info.getFinancingId(), YesOrNoNumberEnum.NO);
        }
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        //FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoService.getById(info.getFinancingId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        fundFinancingBaseInfoService.tryUpdateChangeOther(info.getFinancingId());
    }

    public int countByFinancingId(Long financingId) {
        LambdaQueryWrapper<FundFinancingPledgeInfo> query = Wrappers.lambdaQuery();
        query.in(FundFinancingPledgeInfo::getFinancingId, financingId).eq(FundFinancingPledgeInfo::getIsPledge, 1);
        List<FundFinancingPledgeInfo> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        } else {
            return list.size();
        }
    }

    public Map<Long, List<FundFinancingPledgeInfo>> getMapByFinancingIds(Collection<Long> financingIds) {
        LambdaQueryWrapper<FundFinancingPledgeInfo> query = Wrappers.lambdaQuery();
        query.in(FundFinancingPledgeInfo::getFinancingId, financingIds).eq(FundFinancingPledgeInfo::getIsPledge, 1);
        List<FundFinancingPledgeInfo> result = this.list(query);
        return result.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
    }

    public List<FundFinancingPledgeInfo> list(Long financingId) {
        LambdaQueryWrapper<FundFinancingPledgeInfo> query = Wrappers.lambdaQuery();
        query.eq(FundFinancingPledgeInfo::getFinancingId, financingId);
        return this.list(query);
    }

    //筛选项：所选业务部门下，合同管理模块，合同状态为“起租”的项目
    public List<FundFinancingPledgeProjListRSP> projList(FundFinancingPledgeProjListREQ req) {
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getBizDeptId, req.getBizDeptId())
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()));
        if (ObjectUtil.isEmpty(projReviewBaseInfos)) {
            return null;
        }
        Map<Long, String> projIdMap = projReviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjName));
        List<ContractBaseInfo> contractBaseInfos = getRentContract(projIdMap.keySet());
        if (ObjectUtil.isEmpty(contractBaseInfos)) {
            return null;
        }
        Set<Long> clientSet = contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toSet());
        Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(clientSet);

        if (StringUtils.isNotBlank(req.getProjName())) {
            return clientId2NameMap.entrySet().stream()
                    .filter(f -> f.getValue().contains(req.getProjName()))
                    .map(item -> FundFinancingPledgeProjListRSP.builder().projName(item.getValue()).projReviewId(item.getKey()).build())
                    .collect(Collectors.toList());
        } else {
            return clientId2NameMap.entrySet().stream()
                    .map(item -> FundFinancingPledgeProjListRSP.builder().projName(item.getValue()).projReviewId(item.getKey()).build())
                    .collect(Collectors.toList());
        }

    }

    public List<FundFinancingPledgeContractListRSP> contractList(FundFinancingPledgeContractListREQ req) {
        List<ContractBaseInfo> rentContract = getRentContractByClientIds(Collections.singleton(req.getProjId()));
        List<FundFinancingPledgeContractListRSP> rsps = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(rentContract)) {
            FundFinancingPledgeContractListRSP rsp;
            List<Long> contractIds = rentContract.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            Map<Long, Long> longLongMap = collectionService.listContractAmountByContractIds(contractIds, Boolean.FALSE, Boolean.FALSE);
            Map<Long, LocalDateTime> lastRentDateByContractIds = collectionService.getLastRentDateByContractIds(contractIds);
            for (ContractBaseInfo baseInfo : rentContract) {
                rsp = new FundFinancingPledgeContractListRSP();
                rsp.setContractId(baseInfo.getId());
                rsp.setContractCode(baseInfo.getContractCode());
                rsp.setActualLeaseDate(baseInfo.getActualLeaseDate());
                rsp.setSettleTime(ObjectUtil.isNotEmpty(baseInfo.getSettleTime()) ? baseInfo.getSettleTime() : lastRentDateByContractIds.get(baseInfo.getId()));
                rsp.setBizType(baseInfo.getBizType());
                rsp.setApplyCreditAmount(baseInfo.getApplyCreditAmount());
                rsp.setRemainingUnpaidPrincipal(longLongMap.get(baseInfo.getId()));
                rsps.add(rsp);
            }
        }
        return rsps;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void create(FundFinancingPledgeCreateREQ req) {
        // 单个合同只能被同一个融资，质押一次
        List<FundFinancingPledgeInfo> list = financingPledgeInfoMapper.selectList(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                .eq(FundFinancingPledgeInfo::getContractId, req.getContractId())
                .eq(FundFinancingPledgeInfo::getFinancingId, req.getFinancingId())
        );
        if (CollectionUtil.isNotEmpty(list)) {
            // 说明已经存在了，不能二次添加
            throw MithrasException.newException("该合同已经存于在当前融资，不能重复添加");
        }
        checkReq(req.getFinancingId(), req.getContractId(), req.getIsPledge(), req.getIsSupervise());

        FundFinancingPledgeInfo info = BeanUtil.copyProperties(req, FundFinancingPledgeInfo.class);
        if (ObjectUtil.isNull(req.getRemainingUnpaidPrincipal())) {
            Map<Long, Long> longLongMap = collectionService.listContractAmountByContractIds(Collections.singletonList(req.getContractId()), Boolean.FALSE, Boolean.FALSE);
            info.setRemainingUnpaidPrincipal(LongUtil.null2zero(longLongMap.get(req.getContractId())));
        }
        FundFinancingBaseInfo baseInfo = fundFinancingBaseInfoService.getById(req.getFinancingId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FundFinancingPledgeInfo oldPledgeInfo = baseMapper.selectOne(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                .eq(FundFinancingPledgeInfo::getFinancingId, req.getFinancingId())
                .orderByDesc(FundFinancingPledgeInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        String pledgeCode = null;
        if (ObjectUtil.isNotEmpty(oldPledgeInfo)) {
            pledgeCode = oldPledgeInfo.getPledgeCode();
        }
        List<FundFinancingCreditRef> refList = financingCreditRefService.queryByFinancingId(baseInfo.getId());
        if (CollectionUtil.isEmpty(refList)) {
            throw new MithrasException("需先关联机构");
        }
        FundCredit fundCredit = fundCreditService.getById(refList.get(0).getCreditId());
        info.setPledgeCode(getPledgeCode(fundCredit.getCreditCode(), pledgeCode));
        if (ObjectUtil.isEmpty(req.getContractEndDate())) {
            Map<Long, LocalDateTime> lastRentDateByContractIds = collectionService.getLastRentDateByContractIds(Collections.singletonList(req.getContractId()));
            info.setContractEndDate(ObjectUtil.isEmpty(lastRentDateByContractIds.get(req.getContractId())) ? null : LocalDate.from(lastRentDateByContractIds.get(req.getContractId())));
        }
        this.baseMapper.insert(info);
        YesOrNoNumberEnum hasPledge = info.getIsPledge() != null && info.getIsPledge() ? YesOrNoNumberEnum.YES : YesOrNoNumberEnum.NO;
        // 更新基础数据中是否有质押标识
        fundFinancingBaseInfoService.updateHasPledge(info.getFinancingId(), hasPledge);
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        fundFinancingBaseInfoService.tryUpdateChangeOther(info.getFinancingId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(FundFinancingPledgeModifyREQ req) {
        checkReq(req.getFinancingId(), req.getContractId(), req.getIsPledge(), req.getIsSupervise());

        FundFinancingPledgeInfo info = BeanUtil.copyProperties(req, FundFinancingPledgeInfo.class);
        this.updateById(info);
        FundFinancingPledgeInfo baseInfo = this.getById(info.getId());
        // 如果不是新建则说明是其他类型的变更，需同步变更流程状态
        fundFinancingBaseInfoService.tryUpdateChangeOther(baseInfo.getFinancingId());
    }

    public List<FundFinancingPledgeListRSP> list(FundFinancingPledgeListREQ req) {
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos;
        if (StrUtil.isBlank(req.getVersion())) {
            fundFinancingPledgeInfos = this.list(req.getFinancingId());
        } else {
            List<FundFinancingPledgeInfoLib> libList = financingPledgeInfoLibService.listByFinancingIdVersion(req.getFinancingId(), req.getVersion());
            fundFinancingPledgeInfos = libList.stream().map(financingPledgeInfoLibHandler::actualLib2Entity).collect(Collectors.toList());
        }
        if (ObjectUtil.isEmpty(fundFinancingPledgeInfos)) {
            return Collections.emptyList();
        }
        return this.convertToRSPList(fundFinancingPledgeInfos);
    }

    //可动态查询均动态
    public List<FundFinancingPledgeListRSP> convertToRSPList(List<FundFinancingPledgeInfo> fundFinancingPledgeInfos) {
        List<FundFinancingPledgeListRSP> rsps = BeanUtil.copyToList(fundFinancingPledgeInfos, FundFinancingPledgeListRSP.class);
        List<Long> contractIds = rsps.stream().map(FundFinancingPledgeListRSP::getContractId).collect(Collectors.toList());
        Map<Long, ContractBaseInfo> contractBaseInfoMap = contractBaseInfoService.listByIds(contractIds).stream().collect(Collectors.toMap(ContractBaseInfo::getId,
                e -> e, (a, b) -> a));
        Map<Long, Long> longLongMap = collectionService.listContractAmountByContractIds(contractIds, Boolean.FALSE, Boolean.FALSE);
        Map<Long, LocalDateTime> lastRentDateByContractIds = collectionService.getLastRentDateByContractIds(contractIds);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(contractBaseInfoMap.values().stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
        //合同金额
        Map<Long, Long> contractAmountMap = contractPriceService.queryNewestPrice(new HashSet<>(contractIds)).stream().collect(Collectors.toMap(ContractPrice::getContractId,
                ContractPrice::getContractAmount, (a, b) -> a));
        rsps.forEach(base -> {
            base.setRemainingUnpaidPrincipal(LongUtil.null2zero(longLongMap.get(base.getContractId())));
            base.setContractAmount(contractAmountMap.get(base.getContractId()));
            ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(base.getContractId());
            if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
                /*Map<Long, String> clientNameMap = id2NameService.clientId2Name(Collections.singletonList(contractBaseInfo.getClientId()));
                // 此处projName实际为clientName
                base.setProjName(clientNameMap.get(contractBaseInfo.getClientId()));*/
                base.setContractStartDate(contractBaseInfo.getActualLeaseDate());
                base.setClientId(contractBaseInfo.getClientId());
                base.setClientName(clientId2Name.get(base.getClientId()));
                LocalDateTime dateTime = ObjectUtil.isNotEmpty(contractBaseInfo.getSettleTime()) ? contractBaseInfo.getSettleTime() :
                        lastRentDateByContractIds.get(contractBaseInfo.getId());
                if (ObjectUtil.isNotEmpty(dateTime)) {
                    base.setContractEndDate(dateTime.toLocalDate());
                }
            }
        });

        return rsps;
    }

    public FundFinancingPledgeDetailRSP detail(FundFinancingPledgeDetailREQ req) {
        FundFinancingPledgeInfo pledgeInfo = this.getById(req.getPledgeId());
        if (ObjectUtil.isNull(pledgeInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        Map<Long, Long> longLongMap = collectionService.listContractAmountByContractIds(Collections.singletonList(pledgeInfo.getContractId()), Boolean.FALSE, Boolean.FALSE);
        pledgeInfo.setRemainingUnpaidPrincipal(LongUtil.null2zero(longLongMap.get(pledgeInfo.getContractId())));
        FundFinancingPledgeDetailRSP fundFinancingPledgeDetailRSP = BeanUtil.copyProperties(pledgeInfo, FundFinancingPledgeDetailRSP.class);
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(pledgeInfo.getContractId());
        if (ObjectUtil.isNotEmpty(contractBaseInfo)) {
            fundFinancingPledgeDetailRSP.setClientId(contractBaseInfo.getClientId());
            fundFinancingPledgeDetailRSP.setClientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()));
        }
        fundFinancingPledgeDetailRSP.setBizDeptName(id2NameService.deptId2NameSingle(fundFinancingPledgeDetailRSP.getBizDeptId()));
        return fundFinancingPledgeDetailRSP;
    }

    private List<ContractBaseInfo> getRentContract(Set<Long> projIdSet) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name()))
                .in(ContractBaseInfo::getProjReviewId, projIdSet));
    }

    private List<ContractBaseInfo> getRentContractByClientIds(Set<Long> clientIds) {
        return contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name()))
                .in(ContractBaseInfo::getClientId, clientIds));
    }

    private String getPledgeCode(String creditCode, String oldCode) {
        String code;
        if (oldCode != null && oldCode.length() > 2) {
            code = oldCode.substring(oldCode.length() - 2);
        } else {
            code = "00";
        }
        DecimalFormat format = new DecimalFormat("00");
        String formatCode = format.format(Long.parseLong(code) + 1);
        return String.format("%s%s%s", creditCode, "ZY", formatCode);
    }

    public List<FundFinancingPledgeInfo> findContractPledgeList(Long contractId) {
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = this.list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().eq(FundFinancingPledgeInfo::getContractId, contractId));
        if (CollectionUtil.isEmpty(fundFinancingPledgeInfoList)) {
            return null;
        }
        // 批量查询融资的实际还款计划
        /*Set<Long> financingIds = fundFinancingPledgeInfoList.stream().map(FundFinancingPledgeInfo::getFinancingId).collect(Collectors.toSet());

        LambdaQueryWrapper<FundFinancingRepayActual> query = Wrappers.lambdaQuery();
        query.in(FundFinancingRepayActual::getFinancingId, financingIds);
        List<FundFinancingRepayActual> repayActualList = financingRepayActualService.list(query);
        if (CollectionUtil.isEmpty(repayActualList)) {
            return fundFinancingPledgeInfoList;
        }
        // 分组聚合
        Map<Long, List<FundFinancingRepayActual>> repayActualMap = repayActualList.stream().collect(Collectors.groupingBy(FundFinancingRepayActual::getFinancingId));
        */
        // 找到最后一期的时间，如果早于当前时间认为融资已经到期，剔除到期融资
//        LocalDate now = LocalDate.now();
//        for (Map.Entry<Long, List<FundFinancingRepayActual>> entry : repayActualMap.entrySet()) {
//            List<FundFinancingRepayActual> list = entry.getValue();
//            list.sort(Comparator.comparing(FundFinancingRepayActual::getPhase));
//            FundFinancingRepayActual last = list.get(list.size() - 1);
//            if (now.isAfter(last.getRepayDate())) {
//                fundFinancingPledgeInfoList.removeIf(item -> Objects.equals(item.getFinancingId(), entry.getKey()));
//            }
//        }
        if (CollectionUtil.isNotEmpty(fundFinancingPledgeInfoList)) {
            // 过滤状态
            List<FundFinancingBaseInfo> financingBaseInfoList = fundFinancingBaseInfoService.listByIds(fundFinancingPledgeInfoList.stream().map(FundFinancingPledgeInfo::getFinancingId).collect(Collectors.toList()));
            Map<Long, FundFinancingBaseInfo> financingBaseInfoMap = financingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));
            List<String> pledgeCondition = Arrays.asList(FundFinancingStatusEnum.NEW.name(), FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name());
            fundFinancingPledgeInfoList.removeIf(item -> !pledgeCondition.contains(Optional.ofNullable(financingBaseInfoMap.get(item.getFinancingId())).map(FundFinancingBaseInfo::getFinancingStatus).orElse(null)));
        }
        return fundFinancingPledgeInfoList;
    }

    public String buildTip(Long financingId) {
        FundFinancingBaseInfo financingBaseInfo = fundFinancingBaseInfoService.getById(financingId);
        FundFinancingPlan financingPlan = financingPlanService.getOneByFinancingId(financingId);
        return String.format("该合同已在编号为%s的融资中被质押", financingBaseInfo.getFinancingCode());
    }

    public String buildSuperviseTip(Long financingId) {
        FundFinancingBaseInfo financingBaseInfo = fundFinancingBaseInfoService.getById(financingId);
        return String.format("该合同已在编号为%s的融资中被监管", financingBaseInfo.getFinancingCode());
    }

    /**
     * 融资生效时发送弹窗消息
     *
     * @param financingId
     */
    public void sendPopUpMsg(Long financingId) {
        List<FundFinancingPledgeInfo> pledgeInfos = list(
                Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                        .eq(FundFinancingPledgeInfo::getFinancingId, financingId));

        for (FundFinancingPledgeInfo pledgeInfo : pledgeInfos) {
            // 查询还款账户
            List<FundFinancingPayAccount> accounts = fundFinancingPayAccountService.list(Wrappers.<FundFinancingPayAccount>lambdaQuery()
                    .eq(FundFinancingPayAccount::getFinancingId, pledgeInfo.getFinancingId()));
            String accountInfo;
            if (CollectionUtil.isEmpty(accounts)) {
                accountInfo = "融资中未维护收款账户";
            } else {
                FundFinancingPayAccount account = accounts.get(0);
                accountInfo = account.getAccountBank() + account.getAccountNumber();
            }
            // 查询合同最近一期的租金收款id
            CollectionBaseInfo currentIssue = collectionService.getCurrentIssue(pledgeInfo.getContractId());
            ContractBaseInfo contract = contractBaseInfoService.getById(pledgeInfo.getContractId());
            Long toUserId = contract.getProjSponsorUserId();
            MessageModel messageModel = new MessageModel();
            messageModel.setNeedOa(false);
            messageModel.setFrom("系统通知");
            messageModel.setTo(ListUtil.toList(toUserId));
            PopUpNotificationBody notificationBody = new PopUpNotificationBody();
            notificationBody.setContent(String.format("%s项目已被质押，自%s起还款账户为%s",
                    contract.getProjName(),
                    DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN),
                    accountInfo));
            notificationBody.setTitle("融资质押提醒");
            Map<String, Object> attach = new HashMap<>();
            attach.put("popUpType", "FINANCE_PLEDGE_ADD");
            if (ObjectUtil.isNotEmpty(currentIssue)) {
                attach.put("collectionId", currentIssue.getId());
            }
            notificationBody.setAttachment(attach);
            messageModel.setMsgId(pledgeInfo.getContractId());
            messageModel.setBodie(notificationBody);
            messageModel.setToTel(ListUtil.toList(userService.getRealPhone(toUserId)));
            //直接使用
            WebSocketServer.sendAsyncInfo(messageConver.buildPopUpNotification(messageModel));
        }
    }

    private void checkReq(Long financingId, Long contractId, boolean isPledge, boolean isSupervise) {
        if (isPledge) {
            // 校验是否已经质押给其他融资
            List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = this.findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(fundFinancingPledgeInfoList)) {
                Optional<FundFinancingPledgeInfo> any = fundFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsPledge(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(this.buildTip(any.get().getFinancingId()));
                }
            }
            // 校验是否已经质押给直接融资
            List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = directFinancingPledgeInfoService.findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(directFinancingPledgeInfoList)) {
                Optional<FundDirectFinancingPledgeInfo> any = directFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsPledge(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(directFinancingPledgeInfoService.buildTip(any.get().getFinancingId()));
                }

            }
        }
        // 校验是否已经监管
        if (isSupervise) {
            List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = this.findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(fundFinancingPledgeInfoList)) {
                Optional<FundFinancingPledgeInfo> any = fundFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsSupervise(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(this.buildSuperviseTip(any.get().getFinancingId()));
                }
            }

            List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = directFinancingPledgeInfoService.findContractPledgeList(contractId);
            if (!ObjectUtils.isEmpty(directFinancingPledgeInfoList)) {
                Optional<FundDirectFinancingPledgeInfo> any = directFinancingPledgeInfoList.stream().filter(item -> !item.getFinancingId().equals(financingId))
                        .filter(item -> Objects.equals(item.getIsSupervise(), Boolean.TRUE)).findAny();
                if(any.isPresent()){
                    throw MithrasException.newException(directFinancingPledgeInfoService.buildSuperviseTip(any.get().getFinancingId()));
                }
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void checkPledgeChange(Long financingId) {
        List<FundFinancingPledgeInfoLib> pledgeInfoLibList = financingPledgeInfoLibService.list(Wrappers.<FundFinancingPledgeInfoLib>lambdaQuery().eq(FundFinancingPledgeInfoLib::getFinancingId, financingId).eq(FundFinancingPledgeInfoLib::getIsSupervise, Boolean.TRUE));
        List<FundFinancingPledgeInfo> pledgeInfoList = list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().eq(FundFinancingPledgeInfo::getFinancingId, financingId).eq(FundFinancingPledgeInfo::getIsSupervise, Boolean.TRUE));
        if (CollectionUtil.isEmpty(pledgeInfoLibList) || CollectionUtil.isEmpty(pledgeInfoList)) {
            return;
        }
        // 拿到最新版本
        FundFinancingPledgeInfoLib pledgeInfoLastLib = pledgeInfoLibList.stream().max(Comparator.comparing(FundFinancingPledgeInfoLib::getVersion)).get();
        List<FundFinancingPledgeInfoLib> pledgeInfoLibLastList = pledgeInfoLibList.stream().filter(m -> Objects.equals(m.getVersion(), pledgeInfoLastLib.getVersion())).collect(Collectors.toList());

        Map<Long, FundFinancingPledgeInfoLib> pledgeInfoLibLastMap = pledgeInfoLibLastList.stream().collect(Collectors.toMap(FundFinancingPledgeInfoLib::getContractId, Function.identity()));
        Map<Long, String> contractIdMap = pledgeInfoList.stream().filter(item -> {
            FundFinancingPledgeInfoLib pledgeInfoLib = pledgeInfoLibLastMap.get(item.getContractId());
            return !Objects.equals(item.getAccountNumber(), Optional.ofNullable(pledgeInfoLib).map(FundFinancingPledgeInfoLib::getAccountNumber).orElse(null));
        }).collect(Collectors.toMap(FundFinancingPledgeInfo::getContractId, FundFinancingPledgeInfo::getContractCode));
        if (CollectionUtil.isNotEmpty(contractIdMap)) {
            // 检查关联资产端的合同在结清当月是否已发送还款通知书，如果已发送需触发该合同的还款通知书流程
            LocalDate now = LocalDate.now();
            List<RentCollectionMonthDetail> collectionMonthDetailList = rentCollectionMonthDetailService.list(Wrappers.<RentCollectionMonthDetail>lambdaQuery()
                    .in(RentCollectionMonthDetail::getContractCode, contractIdMap.values())
                    .eq(RentCollectionMonthDetail::getYear, now.getYear())
                    .eq(RentCollectionMonthDetail::getMonth, now.getMonthValue()));
            if (CollectionUtil.isEmpty(collectionMonthDetailList)) {
                return;
            }
            List<String> needSendContractCode = collectionMonthDetailList.stream().map(RentCollectionMonthDetail::getContractCode).collect(Collectors.toList());
            List<Long> needSendContractIdList = contractIdMap.entrySet().stream().filter(entry ->
                    needSendContractCode.contains(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
            // 租金支付通知书没有BusinessKey，无法通过流程查询到该合同是否发送过还款通知书。调整逻辑
//            List<Long> needSendContractIdList = contractIdMap.entrySet().stream().filter(entry -> {
//                entry.getValue()
//                LocalDate lastDay = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
//                ProcessPageReq processPageReq = new ProcessPageReq();
//                processPageReq.setPageIndex(1);
//                processPageReq.setPageSize(1);
//                processPageReq.setBusinessKey(valueOf(entry.get));
//                processPageReq.setModelKeyList(toList(ProcessModelTypeEnum.RentPaymentNotifyFlow.name()));
//                processPageReq.setProcessCreateTimeFrom(Date.from(Instant.from(lastDay.atStartOfDay(ZoneId.systemDefault()))));
//                return CollectionUtil.isNotEmpty(taskApiService.queryProcess(processPageReq).getContents());
//            }).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(needSendContractIdList)) {
                nextMonthRentNotify.runJob(String.valueOf(LocalDate.now().getMonthValue()), needSendContractIdList, Boolean.TRUE);
            }
        }
    }

    public FundPledgeSupervisedBO getSuperviseAccountNum(String contractCode) {
        if (StringUtils.isBlank(contractCode)) {
            return null;
        }
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getContractCode, contractCode));
        if (contractBaseInfoList.isEmpty()) {
            return null;
        }
        List<FundPledgeSupervisedBO> fundPledgeSupervisedBos = financingPledgeInfoMapper.getFundSupervisedBo(contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        if (!fundPledgeSupervisedBos.isEmpty()) {
            return fundPledgeSupervisedBos.get(0);
        }
        return null;
    }

    public List<FundFinancingContractInfoListRSP> contractSearch(FundFinancingContractInfoListREQ req) {
        List<ContractBaseInfo> contractBaseInfos = null;
        /*直融和间融新增关联合同明细的时候   如果合同被锁定，需要排除*/
        if("1".equals(req.getRemoveLockFlag())) {
            contractBaseInfos = contractBaseInfoService.contractSearchList(req.getContractCode());
        }else{ // 防止其他地方用到 兜底
            contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .like(CharSequenceUtil.isNotBlank(req.getContractCode()), ContractBaseInfo::getContractCode, req.getContractCode())
                    .in(ContractBaseInfo::getContractStatus, ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name()));
        }
        if (CollUtil.isEmpty(contractBaseInfos)) {
            return Collections.emptyList();
        }

        List<FundFinancingContractInfoListRSP> rspList = new ArrayList<>();
        List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        Map<Long, Long> longLongMap = collectionService.listContractAmountByContractIds(contractIds, Boolean.FALSE, Boolean.FALSE);
        Map<Long, LocalDateTime> lastRentDateByContractIds = collectionService.getLastRentDateByContractIds(contractIds);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toList()));
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(contractBaseInfos.stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toList()));
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            FundFinancingContractInfoListRSP rsp = new FundFinancingContractInfoListRSP();
            rsp.setContractCode(contractBaseInfo.getContractCode());
            rsp.setClientId(contractBaseInfo.getClientId());
            rsp.setClientName(clientId2Name.get(contractBaseInfo.getClientId()));
            rsp.setProjReviewId(contractBaseInfo.getProjReviewId());
            rsp.setProjName(contractBaseInfo.getProjName());
            rsp.setBizDeptId(contractBaseInfo.getBizDeptId());
            rsp.setBizDeptName(deptId2Name.get(contractBaseInfo.getBizDeptId()));
            rsp.setContractId(contractBaseInfo.getId());
            rsp.setBizType(contractBaseInfo.getBizType());
            rsp.setActualLeaseDate(contractBaseInfo.getActualLeaseDate());
            rsp.setSettleTime(ObjectUtil.isNotEmpty(contractBaseInfo.getSettleTime()) ? contractBaseInfo.getSettleTime() : lastRentDateByContractIds.get(contractBaseInfo.getId()));
            rsp.setApplyCreditAmount(contractBaseInfo.getApplyCreditAmount());
            rsp.setRemainingUnpaidPrincipal(longLongMap.get(contractBaseInfo.getId()));
            rspList.add(rsp);
        }
        return rspList;
    }

    public Map<Long, List<FundFinancingPledgeInfo>> getMapByFinancings(Set<Long> financings) {
        if (CollectionUtil.isEmpty(financings)) {
            return MapUtil.empty();
        }
        return list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                .in(FundFinancingPledgeInfo::getFinancingId, financings)).stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
    }
}
