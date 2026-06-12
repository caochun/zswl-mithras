package cn.zswltech.mithras.application.orchestration.adapter.margin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.CollectionRecordInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.margin.persistence.mapper.MarginRecordInfoMapper;
import cn.zswltech.mithras.margin.application.port.MarginRecordSupportPort;
import cn.zswltech.mithras.margin.application.port.model.MarginCollectionRecordInfo;
import cn.zswltech.mithras.margin.application.port.model.MarginRefundPaymentInfo;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonViewSubAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.application.orchestration.listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.capital.FinanceFlowRecordService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.third.financialshare.enums.CQBusinessTypeENUM;
import cn.zswltech.mithras.third.financialshare.enums.CQPaymentTypeENUM;
import cn.zswltech.mithras.third.financialshare.enums.ExceptionSourceENUM;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

@Component
public class MarginRecordSupportPortAdapter implements MarginRecordSupportPort {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private CollectionAddEventListener collectionAddEventListener;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private CommonViewMainAuthCheckerNew commonViewMainAuthChecker;
    @Resource
    private CommonViewSubAuthCheckerNew commonViewSubAuthChecker;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public void checkMarginView(Long marginId) {
        commonViewMainAuthChecker.check(BusinessModuleEnum.MARGIN, null, marginId, new Object[0]);
    }

    @Override
    public void checkMarginRecordView(Long marginRecordId) {
        commonViewSubAuthChecker.check(BusinessModuleEnum.MARGIN, MarginRecordInfoMapper.class, marginRecordId, new Object[0]);
    }

    @Override
    public void checkCollectionView(Long collectionId) {
        commonViewMainAuthChecker.check(BusinessModuleEnum.COLLECTION, null, collectionId, new Object[0]);
    }

    @Override
    public int countUnfinishedCollectionsByContractId(Long contractId) {
        return collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED));
    }

    @Override
    public List<MarginCollectionRecordInfo> listEarnestMoneyCollectionsByContractId(Long contractId) {
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, contractId)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name())
                        .orderByAsc(CollectionBaseInfo::getCode))
                .stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarginCollectionRecordInfo> listCollectionsByIds(Collection<Long> collectionIds) {
        if (ObjectUtil.isEmpty(collectionIds)) {
            return Collections.emptyList();
        }
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getId, collectionIds))
                .stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public MarginCollectionRecordInfo getCollectionById(Long collectionId) {
        return toInfo(collectionBaseInfoMapper.selectById(collectionId));
    }

    @Override
    public MarginCollectionRecordInfo getLastCollectionRecord(Long collectionId) {
        CollectionRecordInfo recordInfo = collectionRecordInfoMapper.selectOne(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getCollectionId, collectionId)
                .orderByDesc(CollectionRecordInfo::getSortId)
                .last("LIMIT 1"));
        return toInfo(recordInfo);
    }

    @Override
    public List<MarginCollectionRecordInfo> listReceivedRentCollections(String collectionCode) {
        return collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .eq(CollectionBaseInfo::getCode, collectionCode)
                        .gt(CollectionBaseInfo::getPhase, 0)
                        .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(
                                CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                                CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())))
                .stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    @Override
    public String displayCollectionWriteOffStatus(String writeOffStatus) {
        return Optional.ofNullable(writeOffStatus).map(CollectionWriteOffStatusEnum::of).map(CollectionWriteOffStatusEnum::display).orElse(null);
    }

    @Override
    public void sendRentReceivedMessage(MarginCollectionRecordInfo info, Long paidInAmount, LocalDate paidInDate) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(info.getContractId());
        Set<Long> userIds = new HashSet<>();
        userIds.addAll(sysUserService.getUserByDeptCode("FLHGB_ZCBQ").stream().map(UserDO::getId).collect(Collectors.toSet()));
        userIds.addAll(sysUserService.getUserByDeptCode("JHCWB").stream().map(UserDO::getId).collect(Collectors.toSet()));
        userIds.addAll(sysUserService.getUserByDeptCode("ZJGLB").stream().map(UserDO::getId).collect(Collectors.toSet()));
        if (isNotNull(contractBaseInfo.getProjSponsorUserId())) {
            userIds.add(contractBaseInfo.getProjSponsorUserId());
        }
        if (isNotNull(contractBaseInfo.getProjCosponsorUserIds())) {
            userIds.addAll(JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), Long.class));
        }
        if (isNotNull(contractBaseInfo.getBizDeptLeaderId())) {
            userIds.add(contractBaseInfo.getBizDeptLeaderId());
        }
        MessageAddREQ messageAddREQ = new MessageAddREQ();
        messageAddREQ.setFrom("系统通知");
        messageAddREQ.setMessageType(MessageTypeEnum.RENT_RECEIVED.name());
        String clientName = id2NameService.clientId2Name(Collections.singleton(info.getClientId())).get(info.getClientId());
        BigDecimal cashFlowAmountB = LongUtil.tenThousand2Dollar(String.valueOf(info.getCashFlowAmount())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal paidInAmountB = LongUtil.tenThousand2Dollar(String.valueOf(paidInAmount)).setScale(2, RoundingMode.HALF_UP);
        messageAddREQ.setRelation(String.format("%s-%s-第%s期租金%s元于%s到账%s元，请知悉",
                clientName, contractBaseInfo.getContractCode(), info.getPhase(), cashFlowAmountB, paidInDate, paidInAmountB));
        messageAddREQ.setContent(info.getCode());
        messageAddREQ.setPcurl(StringUtils.format("/cpm/collectionWriteOff/detail/%s", info.getId()));
        messageAddREQ.setTo(new ArrayList<>(userIds));
        messageAddREQ.setNeedOa(false);
        messageAddREQ.setNoticeSource(NoticeSourceENUM.RENT_RECEIVED.name());
        messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
    }

    @Override
    public boolean canSettleContractAfterMarginCompleted(Long contractId) {
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(contractId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleFlow.name());
        req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (ObjectUtil.isEmpty(processResp)) {
            return true;
        }
        req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
        req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        return ObjectUtil.isNotEmpty(flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null));
    }

    @Override
    public void contractSettle(Long contractId) {
        contractBaseInfoService.contractSettle(contractId);
    }

    @Override
    public void pushMarginRefundPayments(Long contractId, List<MarginRefundPaymentInfo> payments) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            return;
        }
        Client client = clientMapper.selectById(contractBaseInfo.getClientId());
        if (ObjectUtil.isEmpty(client)) {
            return;
        }
        List<CQ2PaymentVO> vos = payments.stream()
                .map(payment -> buildPayment(contractBaseInfo, client, payment))
                .collect(Collectors.toList());
        financialManagerServiceImpl2.cq2PaymentExec(collectionAddEventListener.getBizInfo(contractId), vos);
    }

    @Override
    public void withdrawBankFlow(Long recordId, String recordMainTable, Long amount) {
        financeFlowRecordService.withdrawBankFlow(recordId, recordMainTable, amount);
    }

    private MarginCollectionRecordInfo toInfo(CollectionBaseInfo collectionBaseInfo) {
        if (collectionBaseInfo == null) {
            return null;
        }
        MarginCollectionRecordInfo info = new MarginCollectionRecordInfo();
        info.setId(collectionBaseInfo.getId());
        info.setContractId(collectionBaseInfo.getContractId());
        info.setClientId(collectionBaseInfo.getClientId());
        info.setCode(collectionBaseInfo.getCode());
        info.setContractCode(collectionBaseInfo.getContractCode());
        info.setPhase(collectionBaseInfo.getPhase());
        info.setPaymentCode(collectionBaseInfo.getPaymentCode());
        info.setCollectionDate(collectionBaseInfo.getCollectionDate());
        info.setCollectionAmount(collectionBaseInfo.getCollectionAmount());
        info.setWriteOffStatus(collectionBaseInfo.getWriteOffStatus());
        info.setCashFlowAmount(collectionBaseInfo.getCashFlowAmount());
        info.setCashFlowItem(collectionBaseInfo.getCashFlowItem());
        info.setInterest(collectionBaseInfo.getInterest());
        info.setPenaltyInterest(collectionBaseInfo.getPenaltyInterest());
        info.setPrincipal(collectionBaseInfo.getPrincipal());
        info.setPlanCollectionDate(collectionBaseInfo.getPlanCollectionDate());
        info.setPlanCollectionAmount(collectionBaseInfo.getPlanCollectionAmount());
        info.setCollectionPrincipal(collectionBaseInfo.getCollectionPrincipal());
        info.setCollectionInterest(collectionBaseInfo.getCollectionInterest());
        info.setCollectionPenaltyInterest(collectionBaseInfo.getCollectionPenaltyInterest());
        return info;
    }

    private CQ2PaymentVO buildPayment(ContractBaseInfo contractBaseInfo, Client client, MarginRefundPaymentInfo payment) {
        String leaseTypeCode = null;
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();
            } else if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.FINANCE_LEASING.getCode();
            }
        }
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setCico_payzh_number(payment.getOurAccountNumber());
        vo.setApplydate(payment.getCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        vo.setCico_srcbillno(String.join("-", payment.getMarginCode(), UUIDUtil.genUuid()));
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK05_004.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(payment.getCollectionAmount()))));
        entry.setE_asstact_name(client.getClientName());
        entry.setCico_pay_bank_number_number(payment.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(payment.getOurAccountBank());
        entry.setE_settlementtype_number(payment.getCollectionType());
        entry.setE_asstact(client.getClientCode());
        entry.setCico_businesstype_number(leaseTypeCode);
        vo.setEntry(CollectionUtil.toList(entry));
        vo.setCico_paynum_rby(payment.getBankDetailNo());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), client.getClientName(),
                Optional.of(ProjectBizType.valueOf(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(null)));
        vo.setSource(ExceptionSourceENUM.BUSINESS_MARGIN.name());
        vo.setBusinessKey(String.valueOf(payment.getMarginId()));
        vo.setBusinessTitle(payment.getMarginCode());
        return vo;
    }

    private MarginCollectionRecordInfo toInfo(CollectionRecordInfo recordInfo) {
        if (recordInfo == null) {
            return null;
        }
        MarginCollectionRecordInfo info = new MarginCollectionRecordInfo();
        info.setId(recordInfo.getId());
        info.setOurAccountId(recordInfo.getOurAccountId());
        info.setOurAccountName(recordInfo.getOurAccountName());
        info.setOurAccountNumber(recordInfo.getOurAccountNumber());
        info.setOurAccountBank(recordInfo.getOurAccountBank());
        return info;
    }
}
