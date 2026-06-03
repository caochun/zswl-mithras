package cn.zswltech.mithras.service.service.third.financial.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.enums.FinancialChangeStateENUM;
import cn.zswltech.mithras.third.enums.FinancialPaymentCodeENUM;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractVersionService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.third.financial.FinancialManagerService;
import cn.zswltech.mithras.service.service.third.financial.FinancialService;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQBillPaymentREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveRentREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQWithdrawREQ;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.resp.FinancialCommonRSP;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName FinancialManagerServiceImpl
 * @Description
 * @Author jackerhe
 * @Date 2022/11/1 4:30 下午
 * @Version 1.0
 **/
@Service
@Slf4j
@Deprecated
public class FinancialManagerServiceImpl implements FinancialManagerService {

    private static final String RECEIVE_TYPE = "A";
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private ContractVersionService contractVersionService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private CollectionService collectionService;
    @Autowired
    @Lazy
    private FinancialService financialService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractRentActualService contractRentActualService;

    @Value("${cq.enable:true}")
    private Boolean cqEnable;

    @Override
    @SneakyThrows
    //@Async
    public void cqReceiveExec(List<CollectionBaseInfo> reqs, ProcessModelTypeEnum processModelTypeEnum) {
        try {
            if (ObjectUtil.isEmpty(reqs) || reqs.size() == 0 || !cqEnable) {
                log.info("FinancialManagerServiceImpl cqReceiveExec not send cqEnable {}", cqEnable);
                return;
            }
            log.info("FinancialManagerServiceImpl cqReceiveExec CashFlowItem {} , param : {}", reqs.get(0).getCashFlowItem(), reqs);
            //每次只会传一个合同的收付款信息
            List<CQReceiveREQ> cqReceiveREQS = new ArrayList<>();
            Set<Long> contractIds = reqs.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
            if (contractIds.size() != 1) {
                throw new MithrasException("期望一个合同，但是找个多个或未找到合同ID，合同ID为" + contractIds);
            }
            //业务确认推负数金额给苍穹，产生应收待付的单子
            reqs.forEach(collectionBaseInfo -> {
                if (ObjectUtil.equals(CashFlowItemEnum.EARNEST_MONEY.name(), collectionBaseInfo.getCashFlowItem())) {
                    collectionBaseInfo.setPlanCollectionAmount(Math.negateExact(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount())));
                }
            });
            ContractBaseInfoDetailRSP contractBaseInfo = contractBaseInfoService.editionDetail(new ContractBaseInfoDetailREQ(reqs.get(0).getContractId()));
            OrgDO orgDO = orgDOMapper.selectByPrimaryKey(contractBaseInfo.getBizDeptId());
            String orgCode = ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId());
            ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getId());
            //客户查询客户编号
            Client client = clientService.getById(main.getLesseeId());
            String customer = ObjectUtil.isNull(client) ? null : client.getClientCode();
            ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractBaseInfo.getId());
            if (ObjectUtil.equals(CashFlowItemEnum.RENT.name(), reqs.get(0).getCashFlowItem())) {
                //已推送租金需取消
                ContractPriceDetailRSP detail = contractPriceService.detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
                List<CQReceiveRentREQ> cqReceiveRentREQS = rentHandle(contractBaseInfo.getId(), orgCode,
                        customer, contractBaseInfo.getProjSponsorUserName(), contractBaseInfo.getContractCode(),
                        NumberUtil.div(LongUtil.tenThousand2Dollar(NumberUtil.add(detail.getLprPercent(), detail.getLprAddPercent()).toString()).toString(), "100", 10, RoundingMode.HALF_UP),
                        FinancialChangeStateENUM.changeCqStatus(processModelTypeEnum, ObjectUtil.isNull(contractSettlePlan) ? null :
                                contractSettlePlan.getIsEarnestDeduction()), processModelTypeEnum);
                if (ObjectUtil.isEmpty(cqReceiveRentREQS)) {
                    return;
                }

                PlatformApiHandler<List<CQReceiveRentREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_RENT_RECEIVE);
                //同步
                platformApiHandler.execute(cqReceiveRentREQS);
            } else {
                //其他类型
                //税率不是我方传输
                reqs.forEach(collectionBaseInfo -> cqReceiveREQS.add(buildCQReceiveREQ(collectionBaseInfo.getCode(), collectionBaseInfo, orgCode, customer, contractBaseInfo.getProjSponsorUserName(), contractBaseInfo.getContractCode(), collectionBaseInfo.getPlanCollectionAmount())));
                PlatformApiHandler<List<CQReceiveREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_RECEIVE);
                //同步
                platformApiHandler.execute(cqReceiveREQS);
            }
            //todo 保证金退款
            log.info("FinancialManagerServiceImpl cqReceiveExec over CashFlowItem {} ", reqs.get(0).getCashFlowItem());
/*
            for (CollectionBaseInfo baseInfo : reqs) {
                if (ObjectUtil.equals(CashFlowItemEnum.FIRST_RENT.name(), baseInfo.getCashFlowItem())) {
                    //如果是不包括的首期租金，要自动核销
                    PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(baseInfo.getPaymentId());
                    if (ObjectUtil.equals(YesOrNoNumberEnum.NO.getCode(), paymentBaseInfo.getDownPaymentType()) && paymentBaseInfo.getDownPayment() > 0) {
                        ThirdCollectionRecordREQ collectionRecordREQ = new ThirdCollectionRecordREQ();
                        collectionRecordREQ.setCollectionCode(baseInfo.getCode());
                        collectionRecordREQ.setCollectionType("收款");
                        collectionRecordREQ.setCollectionDate(LocalDate.now());
                        collectionRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf((LongUtil.null2zero(baseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(baseInfo.getCollectionAmount())))));
                        collectionRecordREQ.setCashFlowItem(CashFlowItemEnum.FIRST_RENT.name());
                        collectionRecordREQ.setInvoiceFlag(YesOrNoNumberEnum.YES.getCode());
                        collectionRecordREQ.setPknumber(baseInfo.getCode());
                        collectionRecordREQ.setOurAccountName("_");
                        collectionRecordREQ.setOurAccountBank("_");
                        collectionRecordREQ.setOurAccountNumber("_");
                        financialService.collectionRecode(collectionRecordREQ);
                    }
                }
            }*/
        } catch (Exception e) {
            log.error("FinancialManagerServiceImpl cqReceiveExec has error", e);
        }
    }

    //保证金退款
    @Override
    @Async
    public void earnestRecord(Long contractId) {
        if (!cqEnable) {
            log.info("FinancialManagerServiceImpl earnestRecord not send cqEnable {}", cqEnable);
            return;
        }
        //保证金退款放于最后一期
        /*MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(Wrappers.<MarginBaseInfo>lambdaQuery()
                .eq(MarginBaseInfo::getContractId, contractId)
                .last(StringUtil.mysqlLimitOne()));
        //合同结清方案
        ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractId);
        if(ObjectUtil.isNull(marginBaseInfo) || ObjectUtil.isNull(contractSettlePlan)){
            return;
        }
        LocalDate applySettleDate;
        if (ContractSettlePlanTypeEnum.SETTLE_IN_ADVANCE.name().equals(contractSettlePlan.getSettleType())) {
            applySettleDate = contractSettlePlan.getApplySettleDate();
        } else {
            applySettleDate = contractSettlePlan.getOriginalDeadline();
        }
        MarginBaseInfoDetailREQ marginBaseInfoDetailREQ = new MarginBaseInfoDetailREQ();
        marginBaseInfoDetailREQ.setId(marginBaseInfo.getId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        MarginBaseInfoRSP marginBaseInfoRSP = marginBaseInfoService.detail(marginBaseInfoDetailREQ);
        //无保证金可退情况
        if(ObjectUtil.isNull(marginBaseInfo) || LongUtil.null2zero(marginBaseInfoRSP.getCanBackAmount()) <= 0){
            return;
        }
        log.info("FinancialManagerServiceImpl earnestRecord execute marginBaseInfo : {} ", marginBaseInfo);
        PlatformApiHandler<List<CQBillPaymentREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_BILL_PAYMENT);
        ContractBaseInfoDetailRSP contractBaseInfo = contractBaseInfoService.editionDetail(new ContractBaseInfoDetailREQ(contractId));
        OrgDO orgDO = orgDOMapper.selectByPrimaryKey(contractBaseInfo.getBizDeptId());
        String orgCode = ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId());
        ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getId());
        //客户查询客户编号
        Client client = clientService.getById(main.getLesseeId());
        String customer = ObjectUtil.isNull(client) ? null : client.getClientCode();
        //构建付款申请
        CQBillPaymentREQ cqPaymentREQ = new CQBillPaymentREQ();
        cqPaymentREQ.setSourcebillno(marginBaseInfo.getMarginCode());
        cqPaymentREQ.setCreator(contractBaseInfo.getProjSponsorUserName());//主办
        cqPaymentREQ.setBizdate(applySettleDate.format(dateTimeFormatter));//计划收款日期
        cqPaymentREQ.setSettleorg(orgCode);
        //客户名称
        //收款人银行账号
        cqPaymentREQ.setCico_contractnumun(contractBaseInfo.getContractCode());
        AtomicReference<CQBillPaymentREQ.PaymentBody> paymentBody = new AtomicReference<>(cqPaymentREQ.new PaymentBody());
        paymentBody.get().setE_assacct(customer);
        //paymentBody.setE_bebank();
        paymentBody.get().setE_applyamount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(marginBaseInfoRSP.getCanBackAmount()).toString()));
        cqPaymentREQ.setEntry(Collections.singletonList(paymentBody.get()));

        FinancialCommonRSP execute = platformApiHandler.execute(Collections.singletonList(cqPaymentREQ));
        log.info("FinancialManagerServiceImpl earnestRecord over execute : {}", execute);*/
    }

    /*private FinancialChangeStateENUM changeCqStatus(ProcessModelTypeEnum processModelTypeEnum, Integer deductio){
        switch (processModelTypeEnum){
            case ContractLPRChangeFlow:
                return FinancialChangeStateENUM.LPR_CHANGE;
            case ContractEarlyRepayFlow:
                return FinancialChangeStateENUM.EARLY_REPAYMENT;
            case ContractChangeRepayPlanFlow:
                return FinancialChangeStateENUM.CHANGE_REPAY_PLAN;
            case ContractExtensionFlow:
                return FinancialChangeStateENUM.EXTENSION;
            case ContractEarlySettleFlow:
                //提前结清抵扣
                if(YesOrNoNumberEnum.YES.getCode().equals(deductio)){
                    return FinancialChangeStateENUM.ContractEarlySettle_Deductio;
                }else {
                    return FinancialChangeStateENUM.ContractEarlySettle;
                }
            case ContractNormalSettleFlow:
                if(YesOrNoNumberEnum.YES.getCode().equals(deductio)){
                    return FinancialChangeStateENUM.ContractNormalSettle;
                }
            default:
                return null;
        }
    }
*/
    @SneakyThrows
    @Override
    public void collectionExec(List<CollectionBaseInfo> reqs) {
        if (ObjectUtil.isEmpty(reqs) || reqs.size() == 0 || !cqEnable) {
            log.info("FinancialManagerServiceImpl collectionExec not send cqEnable {}", cqEnable);
            return;
        }
        log.info("FinancialManagerServiceImpl collectionExec CashFlowItem {} , param : {}", reqs.get(0).getCashFlowItem(), reqs);
        //每次只会传一个合同的收付款信息
        AtomicReference<List<CQReceiveREQ>> cqReceiveREQS = new AtomicReference<>(new ArrayList<>());
        Set<Long> contractIds = reqs.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toSet());
        if (contractIds.size() != 1) {
            throw new MithrasException("期望一个合同，但是找个多个或未找到合同ID，合同ID为" + contractIds);
        }
        ContractBaseInfoDetailRSP contractBaseInfo = contractBaseInfoService.editionDetail(new ContractBaseInfoDetailREQ(contractIds.iterator().next()));
        OrgDO orgDO = orgDOMapper.selectByPrimaryKey(contractBaseInfo.getBizDeptId());
        PlatformApiHandler<List<CQReceiveREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_RECEIVE);
        AtomicReference<List<CollectionBaseInfo>> sendReqs = new AtomicReference<>(new ArrayList<>());
        String orgCode = ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId());
        ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getId());
        //客户查询客户编号
        Client client = clientService.getById(main.getLesseeId());
        String asstact = ObjectUtil.isNull(client) ? null : client.getClientCode();
        reqs.forEach(collectionBaseInfo -> {
            if (LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()) > 0) {
                CQReceiveREQ cqReceiveREQ = buildCQReceiveREQ(String.format("%s-%s-fx", collectionBaseInfo.getCode(), collectionBaseInfo.getPlanPenaltyInterestDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))), collectionBaseInfo, orgCode, asstact, contractBaseInfo.getProjSponsorUserName(), contractBaseInfo.getContractCode(), LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getPenaltyInterestDeductionAmount()));

                cqReceiveREQS.get().add(cqReceiveREQ);
                collectionBaseInfo.setPlanPenaltyInterestDate(LocalDate.now());
                sendReqs.get().add(collectionBaseInfo);
            }
        });
        //同步
        platformApiHandler.execute(cqReceiveREQS.get());
        log.info("FinancialManagerServiceImpl collectionExec over CashFlowItem {} ", reqs.get(0).getCashFlowItem());
    }


    private CQReceiveREQ buildCQReceiveREQ(String sourcebillno, CollectionBaseInfo collectionBaseInfo, String orgCode, String asstact, String projSponsorUserName, String contractCode, Long amount) {
        CQReceiveREQ cqReceiveREQ = new CQReceiveREQ();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        cqReceiveREQ.setSourcebillno(sourcebillno);
        cqReceiveREQ.setCreator(projSponsorUserName);//主办
        cqReceiveREQ.setBizdate(collectionBaseInfo.getPlanCollectionDate().format(dateTimeFormatter));//计划收款日期
        cqReceiveREQ.setOrg(orgCode);
        cqReceiveREQ.setAsstact(asstact);
        cqReceiveREQ.setCico_contractnumun(contractCode);
        //保证金添加到期日
        if(CashFlowItemEnum.EARNEST_MONEY.name().equals(collectionBaseInfo.getCashFlowItem())){
            LocalDate contractExpirationDate = contractRentActualService.getContractRentExpirationDate(collectionBaseInfo.getContractId());
            if(ObjectUtil.isNotEmpty(contractExpirationDate)){
                cqReceiveREQ.setMaturitydate(contractExpirationDate.format(dateTimeFormatter));
            }
        }
        //租赁业务一个收款编号下只有一条收款记录
        CQReceiveREQ.ReceiveBody receiveBody = cqReceiveREQ.new ReceiveBody();
        receiveBody.setCico_incomeitems(collectionBaseInfo.getCashFlowItem());//收入项目
        receiveBody.setE_receivableamt(LongUtil.tenThousand2Dollar(amount.toString()));//应收金额
        //receiveBody.setAxrate(null);
        cqReceiveREQ.setEntry(ListUtil.toList(receiveBody));
        return cqReceiveREQ;
    }

    @Override
    @SneakyThrows
    //@Async
    //付款需调用付款申请接口
    public void cqPaymentExec(Long paymentId) {
        try {
            if (!cqEnable) {
                log.info("FinancialManagerServiceImpl cqPaymentExec not send cqEnable {}", cqEnable);
                return;
            }
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(paymentId);
            if (ObjectUtil.isEmpty(paymentBaseInfo)) {
                throw new MithrasException("付款信息未找到数据");
            }
            log.info("FinancialManagerServiceImpl cqPaymentExec  execute paymentBaseInfoLib : {} ", paymentBaseInfo);
            ContractBaseInfoDetailRSP contractBaseInfo = contractBaseInfoService.editionDetail(new ContractBaseInfoDetailREQ(paymentBaseInfo.getContractId()));
            OrgDO orgDO = orgDOMapper.selectByPrimaryKey(contractBaseInfo.getBizDeptId());
            PlatformApiHandler<List<CQBillPaymentREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_BILL_PAYMENT);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
            String orgCode = ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId());
            ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getId());
            //客户查询客户编号
            Client client = clientService.getById(main.getLesseeId());
            String customer = ObjectUtil.isNull(client) ? null : client.getClientCode();
            //构建付款申请
            CQBillPaymentREQ cqPaymentREQ = new CQBillPaymentREQ();
            cqPaymentREQ.setSourcebillno(paymentBaseInfo.getPaymentCode());
            cqPaymentREQ.setCreator(contractBaseInfo.getProjSponsorUserName());//主办
            cqPaymentREQ.setBizdate(paymentBaseInfo.getApplyPaymentDate().format(dateTimeFormatter));//计划收款日期
            cqPaymentREQ.setSettleorg(orgCode);
            //客户名称
            //收款人银行账号
            cqPaymentREQ.setCico_contractnumun(contractBaseInfo.getContractCode());
            AtomicReference<CQBillPaymentREQ.PaymentBody> paymentBody = new AtomicReference<>(cqPaymentREQ.new PaymentBody());
            paymentBody.get().setE_assacct(customer);
            //paymentBody.setE_bebank();
            paymentBody.get().setE_paymenttype(FinancialPaymentCodeENUM.changePaymentCode(contractBaseInfo.getBizType()).getDisplay());
            paymentBody.get().setE_applyamount(LongUtil.tenThousand2Dollar(paymentBaseInfo.getApplyPaymentAmount().toString()));
            cqPaymentREQ.setIsinitialrent(paymentBaseInfo.getDownPaymentType());
            cqPaymentREQ.setInitialrentamount(LongUtil.tenThousand2Dollar(paymentBaseInfo.getDownPayment().toString()));
            cqPaymentREQ.setEntry(Collections.singletonList(paymentBody.get()));

            platformApiHandler.execute(Collections.singletonList(cqPaymentREQ));
            log.info("FinancialManagerServiceImpl cqPaymentExec over");
        } catch (Exception e) {
            log.error("FinancialManagerServiceImpl cqPaymentExec has error", e);
        }
    }

    //租金表核销完毕不再推送，未核销先调用取消接口
    public List<CQReceiveRentREQ> rentHandle(Long contractId, String orgCode, String customer,
                                              String projSponsorUserName, String contractCode,
                                              BigDecimal rate,
                                              FinancialChangeStateENUM changeStateENUM, ProcessModelTypeEnum processModelTypeEnum) {
        //版本号
        List<CommonVersion> oldVersion = getRentActualVersion(contractId, null);
        CommonVersion lastCommonVersion = contractVersionService.findNewestVersion(contractId);
        String lastVersion = null;
        if (ObjectUtil.isNotEmpty(lastCommonVersion)) {
            lastVersion = lastCommonVersion.getVersion();
        }
        //编辑区本次审批通过数据
        List<ContractRentActual> newRentActuals = contractRentActualLibService.listByContractVersion(contractId, lastVersion);
        newRentActuals = newRentActuals.stream().filter(base -> ObjectUtil.isNotEmpty(base.getCashFlowCode())).collect(Collectors.toList());
        Map<String, CQReceiveRentREQ> cqReceiveRentREQMap = new HashMap<>();
        //备份全量数据
        Map<String, ContractRentActualLib> oldAllMap = new HashMap<>();
        List<CQReceiveRentREQ> cqReceiveRentREQS = new ArrayList<>();
        //判断第几个有效版本
        //Integer sentCount;
        //只有一个版本 为全部新增
        if (ObjectUtil.isEmpty(oldVersion) || ObjectUtil.equals(oldVersion.size(), 1)) {
            for (ContractRentActual rentActual : newRentActuals) {
                buildCQReceiveRentBodyREQ(rentActual, orgCode, customer, projSponsorUserName, contractCode, YesOrNoNumberEnum.NO.getCode(), rate,
                        changeStateENUM, cqReceiveRentREQMap, oldAllMap, String.valueOf(rentActual.getUpdateTime()));
            }
            cqReceiveRentREQS.addAll(cqReceiveRentREQMap.values());
            return cqReceiveRentREQS;
        }
        //查询每个租金表的最近一次推送版本
        Set<Long> receiptIdSet = newRentActuals.stream().map(ContractRentActual::getReceiptId).collect(Collectors.toSet());
        //获取历史租金表
        List<ContractRentActualLib> oldRentActuals = getHistoryActual(contractId, receiptIdSet, lastVersion);
        Map<String, ContractRentActualLib> oldMap = oldRentActuals.stream().filter(base -> ObjectUtil.isNotEmpty(base.getCashFlowCode())).collect(Collectors.toMap(ContractRentActual::getCashFlowCode, v -> v));
        oldAllMap.putAll(oldMap);
        //需要新增
        List<ContractRentActualLib> cancelRents = new ArrayList<>();
        //需要取消
        List<ContractRentActual> addRents = new ArrayList<>();
        for (ContractRentActual newLib : newRentActuals) {
            if (compareRent(newLib, oldMap.get(newLib.getCashFlowCode()))) {
                //有变更需要推送取消+新增
                addRents.add(newLib);
                if (ObjectUtil.isNotEmpty(oldMap.get(newLib.getCashFlowCode()))) {
                    cancelRents.add(oldMap.get(newLib.getCashFlowCode()));
                }
            }
            oldMap.remove(newLib.getCashFlowCode());
        }
        //期数变短，多余期数取消
        if (oldMap.size() > 0) {
            cancelRents.addAll(oldMap.values());
            //减少期数也需要补0新增
            ContractRentActual actual;
            for (ContractRentActualLib oldActual : oldMap.values()) {
                actual = BeanUtil.copyProperties(oldActual, ContractRentActual.class);
                actual.setRent(0L);
                actual.setPrincipal(0L);
                actual.setInterest(0L);
                actual.setRemainingPrincipal(0L);
                actual.setUpdateTime(oldActual.getDataUpdateTime());
                addRents.add(actual);
            }
        }
        //第0期租金-- 唯一ID，合同起租日期，合同剩余本金，当新增借据时传输，用于苍穹技术计提利息
        if(ProcessModelTypeEnum.ContractStartRentFlow.equals(processModelTypeEnum) || ProcessModelTypeEnum.ContractAddNewReceiptFlow.equals(processModelTypeEnum)){
            addZeroRent(addRents);
        }
        //需要取消
        if (ObjectUtil.isNotEmpty(cancelRents)) {
            for (ContractRentActualLib lib : cancelRents) {
                buildCQReceiveRentBodyREQ(lib, orgCode, customer, projSponsorUserName, contractCode, YesOrNoNumberEnum.YES.getCode(), rate,
                        changeStateENUM, cqReceiveRentREQMap, oldAllMap, String.valueOf(lib.getDataUpdateTime()));
            }
        }
        //需要新增
        if (ObjectUtil.isNotEmpty(addRents)) {
            for (ContractRentActual rentActual : addRents) {
                buildCQReceiveRentBodyREQ(rentActual, orgCode, customer, projSponsorUserName, contractCode, YesOrNoNumberEnum.NO.getCode(), rate,
                        changeStateENUM, cqReceiveRentREQMap, oldAllMap, String.valueOf(rentActual.getUpdateTime()));
            }
        } else {
            //正常结清时需要特殊处理 最后一期取消重新推送，并填充保证金变更结果
            if (FinancialChangeStateENUM.ContractNormalSettle.equals(changeStateENUM)) {
                buildCQReceiveRentBodyREQ(newRentActuals.get(newRentActuals.size() - 1), orgCode, customer, projSponsorUserName, contractCode,
                        YesOrNoNumberEnum.YES.getCode(), rate, changeStateENUM, cqReceiveRentREQMap, oldAllMap,
                        newRentActuals.get(newRentActuals.size() - 1).getUpdateTime().toString());
                //新增版本
                buildCQReceiveRentBodyREQ(newRentActuals.get(newRentActuals.size() - 1), orgCode, customer, projSponsorUserName, contractCode,
                        YesOrNoNumberEnum.NO.getCode(), rate, changeStateENUM, cqReceiveRentREQMap, oldAllMap, newRentActuals.get(newRentActuals.size() - 1).getUpdateTime().toString());
                CQReceiveRentREQ req = cqReceiveRentREQMap.get(getPayCode(newRentActuals.get(newRentActuals.size() - 1).getCashFlowCode()));
                CQReceiveRentREQ.ReceiveRentBody receiveRentBody = req.getEntry().get(req.getEntry().size() - 1);
                receiveRentBody.setRentActualid(receiveRentBody.getRentActualid() + "-last");
            }
        }
        cqReceiveRentREQS.addAll(cqReceiveRentREQMap.values());
        return cqReceiveRentREQS;
    }

    private void addZeroRent(List<ContractRentActual> rentActuals){
        if(ObjectUtil.isEmpty(rentActuals)){
            return;
        }
        Set<Long> receiptIds = rentActuals.stream().map(ContractRentActual::getReceiptId).collect(Collectors.toSet());
        if(ObjectUtil.isEmpty(receiptIds)){
            return;
        }

        Long contractId = rentActuals.get(0).getContractId();
        List<Long> rentActualIdSet = rentActuals.stream().map(ContractRentActual::getId).collect(Collectors.toList());
        //付款剩余本金
        Map<String, Long> paymentCodeSum = collectionService.listContractAmountByRentActualIds(rentActualIdSet, false, false);
        Map<Long, String> receiptMap = contractReceiptService.listByIds(receiptIds).stream().collect(Collectors.toMap(ContractReceipt::getId,
                ContractReceipt::getPaymentApplyCode));
        Map<String, Long> payCode2Id = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getPaymentCode, paymentCodeSum.keySet())).stream().collect(Collectors.toMap(PaymentBaseInfo::getPaymentCode,
                PaymentBaseInfo::getId));

        ContractRentActual contractRentActual;
        for(Long id : receiptIds){
            PaymentActualDetail paymentActualDetail = paymentActualDetailService.getOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getPaymentId, payCode2Id.get(receiptMap.get(id)))
                    .orderByAsc(PaymentActualDetail::getPaidInDate)
                    .last(StringUtil.mysqlLimitOne()));
            contractRentActual = new ContractRentActual();
            contractRentActual.setContractId(contractId);
            contractRentActual.setReceiptId(id);
            contractRentActual.setCashFlowPhase(0);
            contractRentActual.setPrincipal(0L);
            contractRentActual.setInterest(0L);
            contractRentActual.setCashFlowDate(ObjectUtil.isNotEmpty(paymentActualDetail) ? paymentActualDetail.getPaidInDate() : LocalDate.now());
            contractRentActual.setRent(0L);
            contractRentActual.setCashFlowCode(receiptMap.get(id) + "-000");
            contractRentActual.setRemainingPrincipal(LongUtil.null2zero(paymentCodeSum.get(receiptMap.get(id))));
            contractRentActual.setUpdateTime(contractRentActual.getCashFlowDate().atStartOfDay());
            rentActuals.add(contractRentActual);
        }
    }

    private void buildCQReceiveRentBodyREQ(ContractRentActual rentActual, String orgCode,
                                           String customer, String projSponsorUserName,
                                           String contractCode,
                                           Integer status, BigDecimal rate,
                                           FinancialChangeStateENUM changeStateENUM, Map<String, CQReceiveRentREQ> cqReceiveRentREQMap,
                                           Map<String, ContractRentActualLib> oldAllMap, String sentCount) {
        CQReceiveRentREQ req = cqReceiveRentREQMap.get(getPayCode(rentActual.getCashFlowCode()));
        if (ObjectUtil.isNull(req)) {
            req = new CQReceiveRentREQ();
            req.setSourcebillno(getPayCode(rentActual.getCashFlowCode()));//租金表标识
            req.setCreator(projSponsorUserName);//主办
            req.setSettleorg(orgCode);
            req.setBilltype("ar_finarbill_BT_zb");
            req.setCustome(customer);
            req.setContractCode(contractCode);
            req.setDepartment(orgCode);
            cqReceiveRentREQMap.put(req.getSourcebillno(), req);
        }
        if (ObjectUtil.isEmpty(req.getEntry())) {
            List<CQReceiveRentREQ.ReceiveRentBody> receiveRentBodies = new ArrayList<>();
            req.setEntry(receiveRentBodies);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        //租赁业务一个收款编号下只有一条收款记录
        CQReceiveRentREQ.ReceiveRentBody receiveBody = new CQReceiveRentREQ.ReceiveRentBody();
        receiveBody.setBilling(status);
        receiveBody.setRentActualid(buildRentCode(rentActual.getCashFlowCode(), rentActual.getCashFlowDate(), rentActual.getRent(), sentCount));
        receiveBody.setRentActualCode(rentActual.getCashFlowCode());
        receiveBody.setLeaseRate(rate);//
        receiveBody.setDate(rentActual.getCashFlowDate().format(dateTimeFormatter));
        receiveBody.setPhase(rentActual.getCashFlowPhase());//期限
        receiveBody.setRent(LongUtil.tenThousand2Dollar(LongUtil.null2zero(rentActual.getRent()).toString()));//缩小一万倍
        receiveBody.setPrincipal(LongUtil.tenThousand2Dollar(LongUtil.null2zero(rentActual.getPrincipal()).toString()));
        receiveBody.setInterest(LongUtil.tenThousand2Dollar(LongUtil.null2zero(rentActual.getInterest()).toString()));
        receiveBody.setLastAmount(LongUtil.tenThousand2Dollar(LongUtil.null2zero(rentActual.getRemainingPrincipal()).toString()));//剩余本金
        if (ObjectUtil.isNotNull(changeStateENUM)) {
            receiveBody.setChangeState(changeStateENUM.getDisplay());
        }
        //新增计算差额
        if (YesOrNoNumberEnum.NO.getCode().equals(status)) {
            ContractRentActual oldContractRentActual = oldAllMap.get(rentActual.getCashFlowCode());
            if (ObjectUtil.isNull(oldContractRentActual)) {
                if (ObjectUtil.isNull(changeStateENUM)) {
                    receiveBody.setRentdifference(BigDecimal.valueOf(0));
                    receiveBody.setPrincipaldifference(BigDecimal.valueOf(0));
                    receiveBody.setInterestdifference(BigDecimal.valueOf(0));
                } else {
                    receiveBody.setRentdifference(receiveBody.getRent());
                    receiveBody.setPrincipaldifference(receiveBody.getPrincipal());
                    receiveBody.setInterestdifference(receiveBody.getInterest());
                }
            } else {
                receiveBody.setRentdifference(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(rentActual.getRent()) - LongUtil.null2zero(oldContractRentActual.getRent()))));
                receiveBody.setPrincipaldifference(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(rentActual.getPrincipal()) - LongUtil.null2zero(oldContractRentActual.getPrincipal()))));
                receiveBody.setInterestdifference(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(rentActual.getInterest()) - LongUtil.null2zero(oldContractRentActual.getInterest()))));
            }
        }

        req.getEntry().add(receiveBody);
    }

    private String getPayCode(String code) {
        if (ObjectUtil.isNull(code) || code.length() < 5) {
            return null;
        }
        return code.substring(0, code.length() - 4);
    }

    private Boolean compareRent(ContractRentActual newBase, ContractRentActualLib oldBase) {
        if (ObjectUtil.isEmpty(oldBase)) {
            return Boolean.TRUE;
        }
        return ObjectUtil.notEqual(newBase.getUpdateTime(), oldBase.getDataUpdateTime());
    }

    private CommonVersion getRentActualVersion(Long mainId) {
        return commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                .orderByDesc(CommonVersion::getVersion)
                .last(StringUtil.mysqlLimitOne()));
    }

    private List<CommonVersion> getRentActualVersion(Long contractId, String version) {
        return commonVersionMapper.selectList(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, contractId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                .lt(ObjectUtil.isNotNull(version), CommonVersion::getVersion, version)
                .orderByDesc(CommonVersion::getVersion));
    }

    //获取历史的传输租金表数据
    private List<ContractRentActualLib> getHistoryActual(Long contractId, Set<Long> receiptIds, String lastVersion) {
        List<String> commonVersions = getRentActualVersion(contractId, lastVersion).stream().map(CommonVersion::getVersion).collect(Collectors.toList());
        List<ContractRentActualLib> rentActualLibs = contractRentActualLibService.listByContractReceiptVersion(contractId, receiptIds, commonVersions);

        Map<String, Map<Long, List<ContractRentActualLib>>> versionRentMap = new HashMap<>();
        Map<Long, List<ContractRentActualLib>> rentActualMap;
        Map<Long, List<ContractRentActualLib>> rentActualMapRSP = new HashMap<>();
        List<ContractRentActualLib> rentActualRSP = new ArrayList<>();
        List<ContractRentActualLib> tempList;
        Map<Long, List<ContractRentActualLib>> newTempMap;
        Map<Long, List<ContractRentActualLib>> oldTempMap;
        Map<String, ContractRentActualLib> oldRentActualMap;
        //构建版本-借据组
        for (ContractRentActualLib lib : rentActualLibs) {
            rentActualMap = versionRentMap.get(lib.getVersion());
            if (ObjectUtil.isEmpty(rentActualMap)) {
                rentActualMap = new HashMap<>();
                versionRentMap.put(lib.getVersion(), rentActualMap);
            }
            tempList = rentActualMap.get(lib.getReceiptId());
            if (ObjectUtil.isEmpty(tempList)) {
                tempList = new ArrayList<>();
                rentActualMap.put(lib.getReceiptId(), tempList);
            }
            tempList.add(lib);
        }
        for (int i = 0; i < commonVersions.size(); i++) {
            String newVersion = commonVersions.get(i);
            if (ObjectUtil.equals((i + 1), commonVersions.size()) || ObjectUtil.isEmpty(versionRentMap.get(commonVersions.get(i + 1)))) {
                //第一个传输版本 未找到变更的填充第一版
                for (Long receipt : receiptIds) {
                    if (ObjectUtil.isEmpty(rentActualMapRSP.get(receipt)) && ObjectUtil.isNotEmpty(versionRentMap.get(newVersion)) && ObjectUtil.isNotEmpty(versionRentMap.get(newVersion).get(receipt))) {
                        rentActualMapRSP.put(receipt, versionRentMap.get(newVersion).get(receipt));
                    }
                }
                break;
            }
            String oldVersion = commonVersions.get(i + 1);
            newTempMap = versionRentMap.get(newVersion);
            oldTempMap = versionRentMap.get(oldVersion);
            for (Long receipt : receiptIds) {
                if (ObjectUtil.isEmpty(rentActualMapRSP.get(receipt)) && ObjectUtil.isNotEmpty(newTempMap.get(receipt))) {
                    //最后一个版本
                    if (ObjectUtil.isEmpty(oldTempMap.get(receipt))) {
                        rentActualMapRSP.put(receipt, newTempMap.get(receipt));
                        continue;
                    }
                    //无此借据，继续查找，直到有变化为止
                    oldRentActualMap = oldTempMap.get(receipt).stream().filter(base -> ObjectUtil.isNotEmpty(base.getCashFlowCode())).collect(Collectors.toMap(ContractRentActual::getCashFlowCode, v -> v));
                    for (ContractRentActualLib rentActualLib : newTempMap.get(receipt)) {
                        if (ObjectUtil.isNull(oldRentActualMap.get(rentActualLib.getCashFlowCode())) || ObjectUtil.notEqual(rentActualLib.getDataUpdateTime(), oldRentActualMap.get(rentActualLib.getCashFlowCode()).getDataUpdateTime())) {
                            rentActualMapRSP.put(receipt, newTempMap.get(receipt));
                            break;
                        }
                    }
                }
            }
        }
        for (List<ContractRentActualLib> actualLibs : rentActualMapRSP.values()) {
            if (ObjectUtil.isNotEmpty(actualLibs)) {
                rentActualRSP.addAll(actualLibs);
            }
        }
        return rentActualRSP;
    }

    //撤回已建立应收单
    //此处租金表已采用其他方式，保留以后有其他类型撤回可使用
    private void withdrawRent(List<ContractRentActualLib> rentActualLibs) {
        if (ObjectUtil.isEmpty(rentActualLibs) || rentActualLibs.size() == 0) {
            return;
        }
        List<String> rentCode = new ArrayList<>();
        List<CQWithdrawREQ> cqWithdrawREQS = new ArrayList<>();
        for (ContractRentActualLib lib : rentActualLibs) {
            if (ObjectUtil.isNotEmpty(lib.getCashFlowCode())) {
                rentCode.add(buildRentCode(lib.getCashFlowCode(), lib.getCashFlowDate(), lib.getRent(), lib.getDataUpdateTime().toString()));
            }
        }
        for (String code : rentCode) {
            CQWithdrawREQ cqWithdrawREQ = new CQWithdrawREQ();
            cqWithdrawREQ.setSourcebillno(code);
            cqWithdrawREQ.setBilltype(RECEIVE_TYPE);
            cqWithdrawREQS.add(cqWithdrawREQ);
        }
        //同步
        PlatformApiHandler<List<CQWithdrawREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_WITHDRAW);
        platformApiHandler.execute(cqWithdrawREQS);
    }

    private String buildRentCode(String code, LocalDate cashFlowData, Long rent, String sentCount) {
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN);
        return String.format("%s-%s-%s-%s", code, cashFlowData, LongUtil.null2zero(rent), sentCount);
    }

}
