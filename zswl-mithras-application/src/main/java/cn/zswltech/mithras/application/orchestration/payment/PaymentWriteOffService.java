package cn.zswltech.mithras.application.orchestration.payment;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentListRsp;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffDetailReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffDetailRsp;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffListReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffListRsp;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.foundation.constant.FinancialConstants;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.payment.application.convert.PaymentConvert;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.message.enums.MessageUrlEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.application.PaymentWriteOffHistoryService;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.third.financialshare.enums.CQBusinessTypeENUM;
import cn.zswltech.mithras.third.financialshare.enums.CQPaymentMethodENUM;
import cn.zswltech.mithras.third.financialshare.enums.CQPaymentTypeENUM;
import cn.zswltech.mithras.third.financialshare.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentWriteOffHistory;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 17:27
 */
@Service
@Slf4j
public class PaymentWriteOffService {
    @Resource
    private PaymentBaseInfoService baseInfoService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private PaymentConvert paymentConvert;
    @Resource
    private PaymentWriteOffHistoryService historyService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientService clientService;

    public PageR<PaymentWriteOffListRsp> list(PaymentWriteOffListReq req) {
        PaymentListReq paymentListReq = paymentConvert.writeOffListReqToPaymentListReq(req);
        paymentListReq.setOrderFieldName("apply_payment_date");
        paymentListReq.setOrder("desc");
        paymentListReq.setPaymentStatusList(Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()));
        PageR<PaymentListRsp> page = baseInfoService.list(paymentListReq);
        if (ObjectUtil.isEmpty(page)) {
            return null;
        }
        List<Long> clientIds = new ArrayList<>();
        List<Long> deptIds = new ArrayList<>();
        List<PaymentWriteOffListRsp> rspData = new ArrayList<>();
        for (PaymentListRsp paymentListRsp : page.getList()) {
            clientIds.add(paymentListRsp.getClientId());
            deptIds.add(paymentListRsp.getBizDeptId());
            PaymentWriteOffListRsp tmp = paymentConvert.paymentListRspToWriteOffListRsp(paymentListRsp);
            ArrayList<Long> payments = new ArrayList<>();
            payments.add(tmp.getPaymentId());
            tmp.setBizDeptId(paymentListRsp.getBizDeptId());
            tmp.setPaidAmount(actualDetailService.calculatePaidAmount(payments));
            if (null == paymentListRsp.getApplyPaymentAmount()) {
                tmp.setRemainingAmount(0L);
            } else {
                tmp.setRemainingAmount(tmp.getApplyPaymentAmount()-tmp.getPaidAmount());
            }
            rspData.add(tmp);
        }
        Set<Long> contractIds = page.getList().stream().map(PaymentListRsp::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractBaseInfoMap;
        if (CollectionUtil.isNotEmpty(contractIds)) {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(contractIds);
            contractBaseInfoMap = contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
        } else {
            contractBaseInfoMap = Collections.emptyMap();
        }
        Map<Long, String> clientNames = id2NameService.clientId2Name(clientIds);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);
        for (PaymentWriteOffListRsp rspDatum : rspData) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMap.get(rspDatum.getContractId());
            if (Objects.nonNull(contractBaseInfo)) {
                rspDatum.setProjectBizType(contractBaseInfo.getBizType());
                rspDatum.setLeaseType(contractBaseInfo.getLeaseType());
            }
            rspDatum.setClientName(clientNames.get(rspDatum.getClientId()));
            rspDatum.setBizDeptName(deptId2Name.get(rspDatum.getBizDeptId()));
        }
        return PageR.of(page, rspData);
    }

    public PaymentWriteOffDetailRsp detail(PaymentWriteOffDetailReq req) {
        PaymentDetailRsp detail = baseInfoService.detail(req.getPaymentId());
        return paymentConvert.paymentDetailToWriteoffDetail(detail);
    }

    /**
     * 付款核销按钮
     *
     * @param paymentId
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized void writeOff(Long paymentId) {
        // 1. 业务校验
        List<PaymentActualDetail> actualDetails = actualDetailService.getBaseMapper()
                .selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, paymentId));
        int w = 0;
        for (PaymentActualDetail actualDetail : actualDetails) {
            if(WriteOffStatus.WRITTEN_OFF.name().equals(actualDetail.getWriteOffStatus())){
                w++;
            }
        }
        if(w == 0){
            throw new MithrasException("没有已核销的付款明细，无法核销付款申请");
        }
        // 2.业务校验后更新，检查核销人数量
        PaymentWriteOffHistory history = new PaymentWriteOffHistory();
        PaymentBaseInfo payment = baseInfoService.getById(paymentId);
        String writeOffUserIdsJson = payment.getWriteOffUserIds();
        List<Long> writeOffUserIds = JSON.parseObject(writeOffUserIdsJson, new TypeReference<List<Long>>() {
        });
        if (ObjectUtil.isNotEmpty(writeOffUserIds)) {
            if (2 <= writeOffUserIds.size()) {
                throw new MithrasException("付款已完成核销");
            } else {
                // 已有1人核销的情况
                if (AccountUtil.getLoginInfo().getId().equals(writeOffUserIds.get(0))) {
                    throw new MithrasException("您已核销");
                } else {
                    // 更新到"已核销"状态
                    payment.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
                    //payment.setWrittenOffDate(LocalDate.now());
                    writeOffUserIds.add(AccountUtil.getLoginInfo().getId());
                    history.setDataStatus(payment.getWriteOffStatus());
                }
            }
        } else {
            // 暂无人核销的情况
            writeOffUserIds = new ArrayList<>();
            writeOffUserIds.add(AccountUtil.getLoginInfo().getId());
            history.setDataStatus(payment.getWriteOffStatus());
        }
        payment.setWriteOffUserIds(JSON.toJSONString(writeOffUserIds));
        baseInfoService.updateById(payment);
        if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(payment.getWriteOffStatus())){
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(payment.getContractId());
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统通知");
            messageAddREQ.setMessageType(MessageTypeEnum.PAYMENT.name());
            String clientName = id2NameService.clientId2Name(Collections.singleton(payment.getClientId())).get(payment.getClientId());
            messageAddREQ.setRelation(clientName+"的"+contractBaseInfo.getContractCode());
            messageAddREQ.setContent(payment.getPaymentCode());
            messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.PAYMENT_COMPLETE.pcUrl,payment.getId()));
            Set<Long> to = new HashSet<>();
            if (contractBaseInfo.getProjSponsorUserId() != null){
                to.add(contractBaseInfo.getProjSponsorUserId());
            }
            if (contractBaseInfo.getBizDeptLeaderId() != null){
                to.add(contractBaseInfo.getBizDeptLeaderId());
            }
            messageAddREQ.setTo(new ArrayList<>(to));
            messageAddREQ.setNeedOa(true);
            messageAddREQ.setNoticeSource(NoticeSourceENUM.PAYMENT.name());
            messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
        }
        // 记录
        history.setOperation("核销");
        history.setPaymentid(payment.getId());
        history.setDataStatus(payment.getWriteOffStatus());
        history.setOperateDataCode(payment.getPaymentCode());
        history.setOperatePersonId(AccountUtil.getLoginInfo().getId());
        history.setOperateTime(LocalDateTime.now());
        history.setOperatePersonName(id2NameService.sysUserId2NameSingle(history.getOperatePersonId()));
        history.setOperateDataType("付款申请");
        historyService.save(history);
    }

    /**
     * 苍穹核销
     *
     * @param paymentId
     */
    @Transactional(rollbackFor = Throwable.class)
    public synchronized void financialWriteOff(Long paymentId, String collectionCode, Long paidInAmount, LocalDate paidInDate) {
        PaymentBaseInfo payment = baseInfoService.getById(paymentId);
        SpringContextHolder.getBean(PaymentWriteOffService.class).modifyPaymentStatus(paymentId);
        PaymentWriteOffHistory history = new PaymentWriteOffHistory();
        //根据核销日期+核销金额，把反结算和原始结算记录关联，剔除这些成对的记录后，取最晚的核销日期
        List<PaymentActualDetail> paymentActualDetails = actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, paymentId)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                .orderByDesc(PaymentActualDetail::getPaidInDate));
        if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(payment.getWriteOffStatus())){
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(payment.getContractId());
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统通知");
            messageAddREQ.setMessageType(MessageTypeEnum.PAYMENT.name());
            String clientName = id2NameService.clientId2Name(Collections.singleton(payment.getClientId())).get(payment.getClientId());
            messageAddREQ.setRelation(clientName+"的"+contractBaseInfo.getContractCode());
            messageAddREQ.setContent(payment.getPaymentCode());
            messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.PAYMENT_COMPLETE.pcUrl,payment.getId()));
            Set<Long> to = new HashSet<>();
            if (contractBaseInfo.getProjSponsorUserId() != null){
                to.add(contractBaseInfo.getProjSponsorUserId());
            }
            if (contractBaseInfo.getBizDeptLeaderId() != null){
                to.add(contractBaseInfo.getBizDeptLeaderId());
            }
            messageAddREQ.setTo(new ArrayList<>(to));
            messageAddREQ.setNeedOa(true);
            messageAddREQ.setNoticeSource(NoticeSourceENUM.PAYMENT.name());
            messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
        }

        if (isNotNull(collectionCode)){
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getCode, collectionCode)
                    .gt(CollectionBaseInfo::getPhase, 0)
                    .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                            CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())));
            if (!collectionBaseInfoList.isEmpty()) {
                Set<Long> userIds = new HashSet<>();
                CollectionBaseInfo info = collectionBaseInfoList.get(0);
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(payment.getContractId());
                //法律合规部
                Set<Long> flhgbSet = sysUserService.getUserByDeptCode("FLHGB_ZCBQ").stream().map(UserDO::getId).collect(Collectors.toSet());
                //财务部
                Set<Long> jhcwbSet = sysUserService.getUserByDeptCode("JHCWB").stream().map(UserDO::getId).collect(Collectors.toSet());
                //资金部
                Set<Long> zjglbSet = sysUserService.getUserByDeptCode("ZJGLB").stream().map(UserDO::getId).collect(Collectors.toSet());
                userIds.addAll(flhgbSet);
                userIds.addAll(jhcwbSet);
                userIds.addAll(zjglbSet);
                if (isNotNull(contractBaseInfo.getProjSponsorUserId())) {
                    userIds.add(contractBaseInfo.getProjSponsorUserId());
                }
                if (isNotNull(contractBaseInfo.getProjCosponsorUserIds())) {
                    List<Long> cosponsorUserIds = JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), Long.class);
                    userIds.addAll(cosponsorUserIds);
                }
                if (isNotNull(contractBaseInfo.getBizDeptLeaderId())) {
                    userIds.add(contractBaseInfo.getBizDeptLeaderId());
                }
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setMessageType(MessageTypeEnum.RENT_RECEIVED.name());
                String clientName = id2NameService.clientId2Name(Collections.singleton(payment.getClientId())).get(payment.getClientId());
                BigDecimal cashFlowAmountB = LongUtil.tenThousand2Dollar(String.valueOf(info.getCashFlowAmount())).setScale(2, RoundingMode.HALF_UP);
                BigDecimal paidInAmountB = LongUtil.tenThousand2Dollar(String.valueOf(paidInAmount)).setScale(2, RoundingMode.HALF_UP);
                messageAddREQ.setRelation(String.format("%s-%s-第%s期租金%s元于%s到账%s元，请知悉!",
                        clientName, contractBaseInfo.getContractCode(), info.getPhase(), cashFlowAmountB, paidInDate, paidInAmountB));
                messageAddREQ.setContent(info.getCode());
                messageAddREQ.setPcurl(StringUtils.format("/cpm/collectionWriteOff/detail/%s", info.getId()));
                messageAddREQ.setTo(new ArrayList<>(userIds));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setNoticeSource(NoticeSourceENUM.RENT_RECEIVED.name());
                messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
            }
        }
        // 记录
        history.setOperation("核销");
        history.setPaymentid(payment.getId());
        history.setOperateDataCode(payment.getPaymentCode());
        //history.setOperatePersonId(AccountUtil.getLoginInfo().getId());
        history.setOperateTime(LocalDateTime.now());
        history.setOperatePersonName(id2NameService.sysUserId2NameSingle(history.getOperatePersonId()));
        history.setOperateDataType("付款申请");
        historyService.save(history);
        //付款结束，通知苍穹付款
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                //给苍穹推送应收单，收款单 这里推送的是没有银行流水的情况，做兼容
                List<PaymentActualDetail> otherPayment = paymentActualDetails.stream().filter(e -> ObjectUtil.isEmpty(e.getBankDetailNo())).collect(Collectors.toList());
                if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(payment.getWriteOffStatus()) && CollectionUtil.isNotEmpty(otherPayment)) {
                    List<CQ2PaymentVO> cq2PaymentVOS = new ArrayList<>();
                    log.info("financialWriteOff WRITTEN_OFF {}", otherPayment);
                    otherPayment.forEach(detail -> {
                        CQ2PaymentVO cq2PaymentVO = buildPayment(detail.getId());
                        if(ObjectUtil.isNotEmpty(cq2PaymentVO)) {
                            cq2PaymentVOS.add(cq2PaymentVO);
                        }
                    });
                    //付款单
                    financialManagerServiceImpl2.cq2PaymentExec(SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(payment.getContractId()), cq2PaymentVOS);
                }
            }
        });
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyPaymentStatus(Long paymentId) {
        // 1. 计算已核销金额
        Long amount = actualDetailService.calculatePaidAmount(Collections.singletonList(paymentId));

        // 2.更新核销状态
        PaymentBaseInfo payment = baseInfoService.getById(paymentId);
        PaymentWriteOffStatus paymentWriteOffStatus;
        if (amount <= 0) {
            paymentWriteOffStatus = PaymentWriteOffStatus.NO_PAID;
        } else if (amount < payment.getApplyPaymentAmount()) {
            paymentWriteOffStatus = PaymentWriteOffStatus.PART_WRITTEN_OFF;
        } else if (ObjectUtil.equals(amount, payment.getApplyPaymentAmount())) {
            paymentWriteOffStatus = PaymentWriteOffStatus.WRITTEN_OFF;
            // 核销完毕需要将超期天数重置为默认值
            payment.setBeyondDays(PaymentBaseInfo.DEFAULT_BEYOND_DAYS);
        } else {
            throw new MithrasException("核销大于申请金额");
        }
        log.info("payment financialWriteOff all = {} ,计算已核销金额 = {}, ", payment.getApplyPaymentAmount(), amount);
        payment.setWriteOffStatus(paymentWriteOffStatus.name());
        //根据核销日期+核销金额，把反结算和原始结算记录关联，剔除这些成对的记录后，取最晚的核销日期
        List<PaymentActualDetail> paymentActualDetails = actualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, paymentId)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                .orderByDesc(PaymentActualDetail::getPaidInDate));

        if (CollectionUtil.isNotEmpty(paymentActualDetails)) {
            //<时间-金额, 次数>
            Map<LocalDate, Map<Long, Integer>> map = new HashMap<>();
            Map<Long, Integer> twoMap;
            for (PaymentActualDetail base : paymentActualDetails) {
                if (LongUtil.null2zero(base.getPaidInAmount()) >= 0) {
                    twoMap = map.getOrDefault(base.getPaidInDate(), new HashMap<>());
                    twoMap.put(base.getPaidInAmount(), twoMap.getOrDefault(base.getPaidInAmount(), 0) + 1);
                    map.put(base.getPaidInDate(), twoMap);
                } else {
                    twoMap = map.getOrDefault(base.getPaidInDate(), new HashMap<>());
                    twoMap.put(Math.abs(base.getPaidInAmount()), twoMap.getOrDefault(Math.abs(base.getPaidInAmount()), 0) - 1);
                    map.put(base.getPaidInDate(), twoMap);
                }
            }
            LocalDate localDate = null;
            for (Map.Entry<LocalDate, Map<Long, Integer>> entry : map.entrySet()) {
                LocalDate key = entry.getKey();
                Map<Long, Integer> value = entry.getValue();
                for (Integer v : value.values()) {
                    if (v > 0) {
                        if (ObjectUtil.isNull(localDate) || key.isAfter(localDate)) {
                            localDate = key;
                        }
                    }
                }
            }
            payment.setPaidInDate(localDate);
        }
        baseInfoService.updateById(payment);
    }

    public CQ2PaymentVO buildPayment(Long paymentActualId){
        PaymentActualDetail paymentActualDetail = actualDetailService.getById(paymentActualId);
        if (ObjectUtil.isEmpty(paymentActualDetail)) {
            return null;
        }
        PaymentBaseInfo baseInfo = baseInfoService.getById(paymentActualDetail.getPaymentId());
        if (ObjectUtil.isEmpty(baseInfo)) {
            return null;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            log.error("付款单合同数据为空 {}, {}", baseInfo, paymentActualDetail);
            return null;
        }
        //过滤银企直联数据
        if (ObjectUtil.equals(paymentActualDetail.getPaymentMethod(), CQPaymentMethodENUM.JSFS15.getDisplay())) {
            log.info("付款单为银企直联数据核销完毕不在处理 {}, {}", baseInfo, paymentActualDetail);
            return null;
        }
        //提供客户（租金往来方）名称
        ContractTenantry contractTenantry = SpringContextHolder.getBean(ContractTenantryService.class).getOne(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, baseInfo.getContractId())
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .last(StringUtil.mysqlLimitOne()));
        Client client = null;
        if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
             client = clientService.getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
        }
        if (ObjectUtil.isEmpty(client)) {
            client = clientService.getById(contractBaseInfo.getClientId());
        }
        if (ObjectUtil.isEmpty(client)) {
            log.error("付款单客户数据为空 {}, {}", baseInfo, paymentActualDetail);
            return null;
        }
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = null;
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();//051售后回租
            } else if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.FINANCE_LEASING.getCode();//048融资租赁
            }
        }
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setCico_payzh_number(paymentActualDetail.getOurAccountNumber());
        //vo.setPayzh_bank_name(paymentActualDetail.getOurAccountBank());
        vo.setApplydate(paymentActualDetail.getPaidInDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        vo.setCico_srcbillno(String.join("-", baseInfo.getPaymentCode(), UUIDUtil.genUuid()));
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_JR001.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(paymentActualDetail.getPaidInAmount()))));
        entry.setE_asstact_name(id2NameService.clientId2NameSingle(baseInfo.getClientId()));
        entry.setCico_pay_bank_number_number(paymentActualDetail.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(paymentActualDetail.getOurAccountBank());
        entry.setE_settlementtype_number(paymentActualDetail.getPaymentMethod());
        //entry.setCico_accountname(paymentActualDetail.getOppositeAccountName());
        entry.setE_asstact_name(client.getClientName());
        entry.setE_asstact(client.getClientCode());
        entry.setCico_uniquecode(paymentActualDetail.getBankDetailNo());
        //20251217付款核销推送-设置业务类型
        entry.setCico_businesstype_number(leaseTypeCode);
        vo.setEntry(CollectionUtil.toList(entry));
        vo.setCico_paynum_rby(paymentActualDetail.getBankDetailNo());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), id2NameService.clientId2NameSingle(baseInfo.getClientId()),
                    Optional.of(LeaseType.valueOf(contractBaseInfo.getLeaseType())).map(LeaseType::display).orElse(null)));
        } else {
            vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), id2NameService.clientId2NameSingle(baseInfo.getClientId()),
                    Optional.of(ProjectBizType.valueOf(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(null)));
        }
        OrgDO orgDO = SpringContextHolder.getBean(OrgDOMapper.class).selectByPrimaryKey(contractBaseInfo.getBizDeptId());
        vo.setCico_dept_number(ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId()));
        //项目端付款申请
        vo.setSource(ExceptionSourceENUM.BUSINESS_FLOW.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(baseInfo.getPaymentCode());
        return vo;
    }

    /*public CQ2PaymentVO.CQ2PaymentVOEntry buildPaymentBody(CQ2PaymentVO vo, PaymentActualDetail paymentActualDetail, Client client){
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_JR001.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(paymentActualDetail.getPaidInAmount()))));
        entry.setE_asstact_name(client.getClientName());
        entry.setCico_pay_bank_number_number(paymentActualDetail.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(paymentActualDetail.getOurAccountBank());
        entry.setE_settlementtype_number(paymentActualDetail.getPaymentMethod());
        entry.setE_asstact_name(client.getClientName());
        entry.setE_asstact(client.getClientCode());
        return entry;
    }*/

    //这里构建核销前需要发送的付款
    public CQ2PaymentVO buildPaymentOther(PaymentBaseInfo baseInfo, PaymentActualDetailUnconfirmed paymentActualDetail){
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            log.error("付款单合同数据为空 {}, {}", baseInfo, paymentActualDetail);
            return null;
        }
        //过滤银企直联数据
        if (!ObjectUtil.equals(paymentActualDetail.getPaymentMethod(), CQPaymentMethodENUM.JSFS15.getDisplay())) {
            log.info("付款单为银企直联数据核销完毕不在处理 {}, {}", baseInfo, paymentActualDetail);
            return null;
        }
        //提供客户（租金往来方）名称
        ContractTenantry contractTenantry = SpringContextHolder.getBean(ContractTenantryService.class).getOne(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, baseInfo.getContractId())
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .last(StringUtil.mysqlLimitOne()));
        Client client = null;
        if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
            client = clientService.getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
        }
        if (ObjectUtil.isEmpty(client)) {
            client = clientService.getById(contractBaseInfo.getClientId());
        }
        if (ObjectUtil.isEmpty(client)) {
            log.error("付款单客户数据为空 {}, {}", baseInfo, paymentActualDetail);
            return null;
        }
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setCico_payzh_number(paymentActualDetail.getOurAccountNumber());
        //vo.setPayzh_bank_name(paymentActualDetail.getOurAccountBank());
        vo.setApplydate(paymentActualDetail.getPaidInDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        vo.setCico_srcbillno(String.join("-", baseInfo.getPaymentCode(), UUIDUtil.genUuid()));
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = null;
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();//051售后回租
            } else if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.FINANCE_LEASING.getCode();//048融资租赁
            }
        }
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK01_JR001.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(paymentActualDetail.getPaidInAmount()))));
        entry.setE_asstact_name(id2NameService.clientId2NameSingle(baseInfo.getClientId()));
        entry.setCico_pay_bank_number_number(paymentActualDetail.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(paymentActualDetail.getOurAccountBank());
        //设置业务类型
        entry.setE_settlementtype_number(paymentActualDetail.getPaymentMethod());
        //entry.setCico_accountname(paymentActualDetail.getOppositeAccountName());
        entry.setE_asstact_name(client.getClientName());
        entry.setE_asstact(client.getClientCode());
        entry.setCico_businesstype_number(leaseTypeCode);
        //entry.setCico_uniquecode(paymentActualDetail.getBankDetailNo());
        vo.setEntry(CollectionUtil.toList(entry));
        //vo.setCico_paynum_rby(paymentActualDetail.getBankDetailNo());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), id2NameService.clientId2NameSingle(baseInfo.getClientId()),
                    Optional.of(LeaseType.valueOf(contractBaseInfo.getLeaseType())).map(LeaseType::display).orElse(null)));
        } else {
            vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), id2NameService.clientId2NameSingle(baseInfo.getClientId()),
                    Optional.of(ProjectBizType.valueOf(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(null)));
        }
        //项目端付款申请
        vo.setSource(ExceptionSourceENUM.BUSINESS_FLOW.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(String.join("-", baseInfo.getPaymentCode(), paymentActualDetail.getSeqCode()));
        return vo;
    }

    /**
     * 撤销核销按钮
     *
     * @param paymentId
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized void undoWriteOff(Long paymentId) {
        PaymentWriteOffHistory history = new PaymentWriteOffHistory();
        // 2.业务校验后更新，检查核销人数量
        PaymentBaseInfo payment = baseInfoService.getById(paymentId);
        String writeOffUserIdsJson = payment.getWriteOffUserIds();
        List<Long> writeOffUserIds = JSON.parseObject(writeOffUserIdsJson, new TypeReference<List<Long>>() {
        });
        if (ObjectUtil.isEmpty(writeOffUserIds)) {
            throw new MithrasException("暂无核销无需撤销");
        }
        if (writeOffUserIds.size() >= 2) {
            throw new MithrasException("双人核销后无法撤销");
        }
        payment.setWriteOffUserIds(null);
        baseInfoService.updateById(payment);
        // 记录
        history.setOperation("撤销");
        history.setPaymentid(payment.getId());
        history.setOperateDataCode(payment.getPaymentCode());
        history.setOperatePersonId(AccountUtil.getLoginInfo().getId());
        history.setOperateTime(LocalDateTime.now());
        history.setOperatePersonName(id2NameService.sysUserId2NameSingle(history.getOperatePersonId()));
        history.setOperateDataType("付款申请");
        history.setDataStatus(payment.getWriteOffStatus());
        historyService.save(history);
    }
}
