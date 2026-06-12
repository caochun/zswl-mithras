package cn.zswltech.mithras.application.orchestration.third.financial.impl;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.builder.EqualsBuilder;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.common.ResultCode;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.dto.third.financial.InnerCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdMarginRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.payment.application.convert.PaymentConvert;
import cn.zswltech.mithras.application.orchestration.enums.*;
import cn.zswltech.mithras.capital.enums.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.collection.enums.BillTypeEnum;
import cn.zswltech.mithras.margin.enums.MarginWriteOffStatusEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.capital.persistence.mapper.writeoff.FinanceFlowWriteOffDetailMapper;
import cn.zswltech.mithras.capital.persistence.model.writeoff.FinanceFlowWriteOffDetail;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.persistence.model.MarginRecordInfo;
import cn.zswltech.mithras.margin.persistence.model.WarrantyBaseInfo;
import cn.zswltech.mithras.margin.persistence.model.WarrantyRecordInfo;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.application.orchestration.collection.BillManagementService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.margin.service.MarginRecordService;
import cn.zswltech.mithras.margin.service.WarrantyBaseInfoService;
import cn.zswltech.mithras.margin.service.WarrantyRecordService;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentWriteOffService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.payment.event.PaymentWriteOffEvent;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getApplicationContext;
import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @ClassName FinancialService
 * @Description
 * @Author jackerhe
 * @Date 2022/10/17 7:22 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class FinancialServiceImpl implements FinancialService {

    public static final String CWXT = "财务系统";

    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private PaymentConvert paymentConvert;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private PaymentWriteOffService paymentWriteOffService;
    @Resource
    private MarginRecordService marginRecordService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private UserService userService;
    @Resource
    private BillManagementService billManagementService;
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FinanceFlowWriteOffDetailMapper financeFlowWriteOffDetailMapper;
    @Resource
    private WarrantyBaseInfoService warrantyBaseInfoService;
    @Resource
    private WarrantyRecordService warrantyRecordService;

    /**
     * 苍穹系统付款核销
     **/
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<String> paymentRecode(ThirdPaymentDetailREQ req) {
        log.info("FinancialService paymentRecode param {}", req);
        if (!req.isReqNeedHandle()) {
            log.info("接收到付款核销通知，标记为无需处理，忽略不处理");
            return R.ok("接收到付款核销通知，标记为无需处理，忽略不处理");
        }
        if (paymentActualDetailService.count(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getFlowId, req.getPknumber())) > 0) {
            return R.fail("付款核销:该流水号" + req.getPknumber() + "已核销，此请求为重复操作");
        }
        StopWatch sw = new StopWatch();
        sw.start();
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getPaymentCode, req.getCollectionCode()));
        // 查询合同
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
        PaymentActualDetail paymentActualDetail = paymentConvert.third2PaymentActual(req);
        paymentActualDetail.setOperationDate(LocalDate.now());
        paymentActualDetail.setContractId(paymentBaseInfo.getContractId());
        if (req.isReqFromCq()) {
            paymentActualDetail.setInfoSource(CWXT);
            paymentActualDetail.setWriteOffType(WriteOffTypeEnum.AUTO_RECORD.name());
        } else {
            paymentActualDetail.setInfoSource(StrUtil.isNotBlank(req.getInfoSource()) ? req.getInfoSource() : "未知");
            paymentActualDetail.setWriteOffType(WriteOffTypeEnum.MANUAL_RECORD.name());
        }
        paymentActualDetail.setPaidInAmount(LongUtil.other2Long(req.getPaidInAmount().toString()));
        paymentActualDetail.setPaymentId(paymentBaseInfo.getId());
        paymentActualDetail.setBankDetailNo(req.getBankDetailNo());
        paymentActualDetail.setFinanceFlowId(req.getFinanceFlowId());
        paymentActualDetail.setWriteOffStatus(WriteOffStatus.WRITTEN_OFF.name());
        paymentActualDetail.setClientId(paymentBaseInfo.getClientId());
        int actualDetailCount = paymentActualDetailService.count(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, paymentBaseInfo.getId()));
        paymentActualDetail.setSeqCode(String.format("%02d", actualDetailCount + 1));
        paymentActualDetailService.save(paymentActualDetail);
        //维护流水记录
        addPaymentToFinanceFlowDetail(FinanceFlowDetailTableEnum.PAYMENT_ACTUAL_DETAIL.name(), paymentActualDetail);
        req.setRecordId(paymentActualDetail.getId());
        //维护反核销关系
        paymentActualDetailService.cancelWriteRecord(paymentActualDetail);
        //每收到一笔核销更新付款状态
        paymentWriteOffService.financialWriteOff(paymentBaseInfo.getId(), req.getCollectionCode(), paymentActualDetail.getPaidInAmount(), req.getPaidInDate());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ThreadPoolUtil.getCommonPool().execute(() -> {
//                    // 尝试确定对应付款的FTP
//                    try {
//                        paymentBaseInfoService.tryInitFtpCost(paymentBaseInfo.getId());
//                    } catch (Exception e) {
//                        log.error("初始化付款申请FTP成本发生异常[paymentId: {}]", paymentBaseInfo.getId(), e);
//                    }
//                    // 尝试更新直租合同对应FTP考核信息的生效日期
//                    if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
//                        try {
//                            List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentId(paymentBaseInfo.getId());
//                            if (CollectionUtil.isNotEmpty(paymentActualDetailList)) {
//                                paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
//                                PaymentActualDetail earliest = paymentActualDetailList.get(0);
//                                SpringUtil.getBean(FtpAssessmentInfoService.class).refreshEffectDate(paymentBaseInfo.getId(), paymentBaseInfo.getReceiptId(), earliest.getPaidInDate());
//                            }
//                        } catch (Exception e) {
//                            log.error("尝试更新直租合同对应FTP价格考核表的生效日期发生异常[合同编号:{}]", contractBaseInfo.getContractCode(), e);
//                        }
//                    }
                    // 尝试初始化默认票据信息
                    try {
                        if (Objects.equals(paymentActualDetail.getPaymentMethod(), GlobalConstants.CQ_PAYMENT_METHOD_PJ) && Objects.nonNull(paymentActualDetail.getPaidInAmount()) && paymentActualDetail.getPaidInAmount() > 0 && ObjectUtil.equals(paymentActualDetail.getInfoSource(), CWXT)) {
                            billManagementService.initAfterCqNotify(paymentActualDetail.getId(), BillTypeEnum.PAYMENT.name());
                        }
                    } catch (Exception e) {
                        log.error("付款核销增加默认票据信息异常[paymentActualDetail:{}]", JSONUtil.toJsonStr(paymentActualDetail), e);
                    }
                    // 尝试初始化项目分配信息
                    try {
                        // TODO 判断是否首次 历史数据导入完成后可拿掉该判断 create内部保障数据唯一
                        if (actualDetailCount == 0) {
                            kpiProjectDistributionService.create(paymentBaseInfo.getContractId(), true);
                            //客户首次投放，需加入租后检查计划中
                            //SpringContextHolder.getBean(AfterLeaseCheckPlanBaseService.class).clientFirstCheck(paymentBaseInfo.getClientId());
                        }
                    } catch (Exception e) {
                        log.error("初始化项目分配信息发生异常[contractId:{}]", paymentBaseInfo.getContractId(), e);
                    }
                });
                // 发送指标速算事件
                MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
                metricComputeEventBus.post(metricComputeEvent);
            }
        });
        sw.stop();
        getApplicationContext().publishEvent(new PaymentWriteOffEvent(this, paymentActualDetail));
        //票据通知财务 --来自苍穹的才通知
        if (ObjectUtil.equals(GlobalConstants.CQ_PAYMENT_METHOD_PJ, req.getPaymentMethod()) && req.isReqFromCq()) {
            String relation = "%s %s 存在票据付款，";
//            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(paymentBaseInfo.getContractId());
            noticeFinancialPersonnel(MessageUrlEnum.BILL_PAY, String.format(relation, ObjectUtil.isNotEmpty(contractBaseInfo) ?
                            contractBaseInfo.getProjName() : null, paymentBaseInfo.getPaymentCode()), paymentBaseInfo.getId(),
                    paymentBaseInfo.getPaymentCode());
        }
        log.info("FinancialService paymentRecode param end use time {}ms", sw.getLastTaskTimeMillis());
        return R.ok("付款核销:" + req.getPknumber() + "核销成功");
    }

    private void addPaymentToFinanceFlowDetail(String tableName, PaymentActualDetail paymentActualDetail) {
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setMainId(paymentActualDetail.getId());
        financeFlowWriteOffDetail.setRecordMainTable(tableName);
        financeFlowWriteOffDetail.setFinanceFlowId(paymentActualDetail.getFinanceFlowId());
        financeFlowWriteOffDetail.setBankDetailNo(paymentActualDetail.getBankDetailNo());
        financeFlowWriteOffDetailMapper.insert(financeFlowWriteOffDetail);
    }

    /**
     * 苍穹系统收款核销
     **/
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<String> collectionRecode(ThirdCollectionRecordREQ req) {//收款核销
        log.info("FinancialService collectionRecode param {}", req);
        CashFlowItemEnum cashFlowItemEnum = Optional.ofNullable(CashFlowItemEnum.of(req.getCashFlowItem())).orElseThrow(() -> new MithrasException("无此现金流项目"));
        //验证是否存在
        if (collectionRecordInfoService.count(Wrappers.<CollectionRecordInfo>lambdaQuery()
                .eq(CollectionRecordInfo::getFlowId, req.getPknumber())) > 0) {
            return R.fail("收款核销:此流水" + req.getPknumber() + "已核销，本次请求为重复申请");
        }
        //增加校验
        preCheck(req);
        StopWatch sw = new StopWatch();
        sw.start();
        CollectionRecordInfo collectionRecordInfo = collectionRecordInfoService.financialAdd(req);
        req.setRecordId(collectionRecordInfo.getId());
        //建立反核销关系
        collectionRecordInfoService.cancelWriteRecord(collectionRecordInfo);
        //维护核销状态
        collectionRecordInfoService.financialWriteOff(collectionRecordInfo.getCollectionId(), collectionRecordInfo.getId(), String.valueOf(collectionRecordInfo.getSortId()), req.getCashFlowItem(), req.getCollectionCode(), LongUtil.other2Long(req.getCollectionAmount().toString()), req.getCollectionDate());
        if (ObjectUtil.equals(cashFlowItemEnum.display, CashFlowItemEnum.EARNEST_MONEY.display)) {//保证金还需同步至保证金管理模块
            CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCode, req.getCollectionCode())
                    .last(StringUtil.mysqlLimitOne()));
            MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(Wrappers.<MarginBaseInfo>lambdaQuery()
                    .eq(MarginBaseInfo::getContractId, collectionBaseInfo.getContractId())
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNull(marginBaseInfo)) {
                log.error("FinancialServiceImpl collectionRecode MarginBaseInfo not find marginBaseInfo by contractId {}", collectionBaseInfo.getContractId());
                return null;
            }
            MarginRecordInfo marginRecordInfo = BeanUtil.copyProperties(req, MarginRecordInfo.class);
            marginRecordInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
            marginRecordInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
            marginRecordInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
            marginRecordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
            marginRecordInfo.setSourceFlag(0);
            marginRecordInfo.setCollectionId(collectionBaseInfo.getId());
            //何老师说保证金不用记录银行流水ID
//            marginRecordInfo.setFinanceFlowId(req.getFinanceFlowId());
//            marginRecordInfo.setBankDetailNo(req.getBankDetailNo());
            marginRecordInfo.setCollectionAmount(LongUtil.other2Long(req.getCollectionAmount().toString()));
            marginRecordInfo.setMarginId(marginBaseInfo.getId());
            marginRecordService.financialAdd(marginRecordInfo);
            //触发保证金管理是否核销完毕
            marginRecordService.financialWriteOff(marginRecordInfo, req.getCollectionCode(), LongUtil.other2Long(req.getCollectionAmount().toString()), req.getCollectionDate());
//            //设置流水核销明细
//            addMarginToFinanceFlowDetail(FinanceFlowDetailTableEnum.MARGIN_RECORD_INFO.name(), marginRecordInfo);
        }

        /*质保金核销*/
        if (ObjectUtil.equals(cashFlowItemEnum.display, CashFlowItemEnum.RETENTION_MONEY.display)) {
            warrantyWriteOff(req);
        }
        //票据通知财务
        if (ObjectUtil.equals(GlobalConstants.CQ_PAYMENT_METHOD_PJ, req.getCollectionType()) && CWXT.equals(req.getDataSource())) {
            String relation = "%s %s 存在票据收款，";
            CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getById(collectionRecordInfo.getCollectionId());
            if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(collectionBaseInfo.getContractId());
                noticeFinancialPersonnel(MessageUrlEnum.BILL_COLLECTION, String.format(relation, ObjectUtil.isNotEmpty(contractBaseInfo) ?
                                contractBaseInfo.getProjName() : null, collectionBaseInfo.getCode()), collectionBaseInfo.getId(),
                        collectionBaseInfo.getCode());
            }
        }
        //这里添加到延时队列里
        sw.stop();
        log.info("FinancialService collectionRecode end use time {}ms", sw.getLastTaskTimeMillis());
        return R.ok("收款核销:核销成功");
    }

    /*质保金核销  借鉴保证金核销*/
    private void warrantyWriteOff(ThirdCollectionRecordREQ req) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCode, req.getCollectionCode())
                .last(StringUtil.mysqlLimitOne()));
        WarrantyBaseInfo warrantyBaseInfo = warrantyBaseInfoService.getOne(Wrappers.<WarrantyBaseInfo>lambdaQuery()
                .eq(WarrantyBaseInfo::getContractId, collectionBaseInfo.getContractId())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(warrantyBaseInfo)) {
            log.error("FinancialServiceImpl collectionRecode MarginBaseInfo not find marginBaseInfo by contractId {}", collectionBaseInfo.getContractId());
        }
        WarrantyRecordInfo warrantyRecordInfo = BeanUtil.copyProperties(req, WarrantyRecordInfo.class);
        warrantyRecordInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
        warrantyRecordInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
        warrantyRecordInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
        warrantyRecordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        warrantyRecordInfo.setSourceFlag(0);
        warrantyRecordInfo.setCollectionId(collectionBaseInfo.getId());

        warrantyRecordInfo.setCollectionAmount(LongUtil.other2Long(req.getCollectionAmount().toString()));
        warrantyRecordInfo.setWarrantyId(warrantyBaseInfo.getId());
        warrantyRecordService.financialAdd(warrantyRecordInfo);
        //触发质保金管理是否核销完毕
        warrantyRecordService.financialWriteOff(warrantyRecordInfo, req.getCollectionCode(), LongUtil.other2Long(req.getCollectionAmount().toString()), req.getCollectionDate());
    }

    private void addMarginToFinanceFlowDetail(String tableName, MarginRecordInfo marginRecordInfo) {
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setBankDetailNo(marginRecordInfo.getBankDetailNo());
        financeFlowWriteOffDetail.setMainId(marginRecordInfo.getId());
        financeFlowWriteOffDetail.setRecordMainTable(tableName);
        financeFlowWriteOffDetail.setFinanceFlowId(marginRecordInfo.getFinanceFlowId());
        financeFlowWriteOffDetailMapper.insert(financeFlowWriteOffDetail);
    }

    private void addMarginToFinanceFlowDetail(String tableName, WarrantyRecordInfo warrantyRecordInfo) {
        FinanceFlowWriteOffDetail financeFlowWriteOffDetail = new FinanceFlowWriteOffDetail();
        financeFlowWriteOffDetail.setBankDetailNo(warrantyRecordInfo.getBankDetailNo());
        financeFlowWriteOffDetail.setMainId(warrantyRecordInfo.getId());
        financeFlowWriteOffDetail.setRecordMainTable(tableName);
        financeFlowWriteOffDetail.setFinanceFlowId(warrantyRecordInfo.getFinanceFlowId());
        financeFlowWriteOffDetailMapper.insert(financeFlowWriteOffDetail);
    }

    private void preCheck(ThirdCollectionRecordREQ req) {
        //当前期项
        String collectionCode = req.getCollectionCode();
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCode, collectionCode)
                .last(StringUtil.mysqlLimitOne()));

        if (ObjectUtil.isNull(collectionBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        if (!Objects.equals(collectionBaseInfo.getCashFlowItem(), CashFlowItemEnum.RENT.name())) {
            // 忽略非租金类型
            return;
        }
        if (collectionBaseInfo.getPhase() == 0) {
            // 忽略0期租金（一般是首期利息）
            return;
        }

        String receiptCode = collectionBaseInfo.getReceiptCode();
        if (StrUtil.isBlank(receiptCode)) {
            receiptCode = collectionCode.substring(0, collectionCode.lastIndexOf("-"));
        }
        //最后一期租金
        CollectionBaseInfo lastPhaseRent = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, collectionBaseInfo.getContractId())
                .eq(CollectionBaseInfo::getReceiptCode, receiptCode)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .orderByDesc(CollectionBaseInfo::getPhase)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(lastPhaseRent)) {
            receiptCode = collectionCode.substring(0, collectionCode.indexOf("-")) + "-HZ";
            // 如果找不到，兼容一下历史直租数据的问题(现金流编号的前缀和借据编号不一致)
            lastPhaseRent = collectionBaseInfoService.getOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, collectionBaseInfo.getContractId())
                    .eq(CollectionBaseInfo::getReceiptCode, receiptCode)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .orderByDesc(CollectionBaseInfo::getPhase)
                    .last(StringUtil.mysqlLimitOne()));
        }

        if (lastPhaseRent == null) {
            return;
        }
        Boolean aBoolean = new EqualsBuilder().append(lastPhaseRent.getCode(), collectionBaseInfo.getCode())
                .append(lastPhaseRent.getCashFlowItem(), collectionBaseInfo.getCashFlowItem())
                .append(lastPhaseRent.getPhase(), collectionBaseInfo.getPhase())
                .append(lastPhaseRent.getPlanCollectionDate(), collectionBaseInfo.getPlanCollectionDate())
                .build();

        if (Boolean.FALSE.equals(aBoolean)) {
            return;
        }

        //查询是否存在提前结清通过流程
        ProcessPageReq page = new ProcessPageReq();
        page.setModelKey(ProcessModelTypeEnum.ContractEarlySettleFlow.name());
        page.setBusinessKey(String.valueOf(collectionBaseInfo.getContractId()));
//        List<ProcessResp> settle = taskApiService.queryProcess(page).getContents();
        List<ProcessResp> settle = Optional.ofNullable(taskApiService.queryProcess(page)).map(Page::getContents).orElse(Collections.emptyList());

        //这里只需要拦截已经通过的提前结清流程
        settle = settle.stream().filter(a -> 2 == a.getProcessStatus() || 6 == a.getProcessStatus())
                .collect(Collectors.toList());

        //查询是否存在结清确认流程
        ProcessPageReq pageReq = new ProcessPageReq();
        pageReq.setModelKey(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
        pageReq.setBusinessKey(String.valueOf(collectionBaseInfo.getContractId()));
//        List<ProcessResp> settleConfirm = taskApiService.queryProcess(pageReq).getContents();
        List<ProcessResp> settleConfirm = Optional.ofNullable(taskApiService.queryProcess(pageReq)).map(Page::getContents).orElse(Collections.emptyList());

        if (CollUtil.isEmpty(settle)) {
            //正常结清或者是正在走提前结清都不处理
            return;
        }

        List<ProcessResp> effectList = settleConfirm.stream().filter(a -> 2 == a.getProcessStatus() || 6 == a.getProcessStatus())
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(settle) && CollUtil.isNotEmpty(effectList)) {
            //由于正常业务不会核销对不上的数据，暂时先这样处理
            return;
        }
        throw new MithrasException("请提醒业务员在【我发起的-待发起】提交结清确认流程！否则无法保证该期项的应收信息准确无误。");
    }

    /**
     * 苍穹系统退款
     **/
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<String> backRecord(ThirdMarginRecordREQ req) {
        log.info("FinancialService backRecord param {}", req);
        if (marginRecordService.count(Wrappers.<MarginRecordInfo>lambdaQuery()
                .eq(MarginRecordInfo::getFlowId, req.getPknumber())) > 0) {
            return R.fail("退款核销，此流水" + req.getPknumber() + "已核销，本次请求为重复申请");
        }
        StopWatch sw = new StopWatch();
        sw.start();

        MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(Wrappers.<MarginBaseInfo>lambdaQuery()
                .eq(MarginBaseInfo::getMarginCode, req.getCollectionCode())
                .last(StringUtil.mysqlLimitOne()));


        if (ObjectUtil.isEmpty(marginBaseInfo)) {
            return R.fail("保证金管理:无此退款记录");
        }

        MarginRecordInfo marginRecordInfo = BeanUtil.copyProperties(req, MarginRecordInfo.class);
        marginRecordInfo.setRecordType(RecordTypeEnum.REFUND.name());
        marginRecordInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
        marginRecordInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
        marginRecordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        marginRecordInfo.setSourceFlag(0);
        //marginRecordInfo.setCollectionId(collectionBaseInfo.getId());
        marginRecordInfo.setMarginId(marginBaseInfo.getId());
        marginRecordInfo.setFlowId(req.getPknumber());
        marginRecordInfo.setCollectionDate(req.getCollectionDate());
        //x10000
        marginRecordInfo.setCollectionAmount(LongUtil.other2Long(req.getCollectionAmount().toString()));
        marginRecordInfo.setOtherAccountName(req.getOppositeAccountName());
        marginRecordInfo.setOtherAccountNumber(req.getOppositeAccountNumber());
        marginRecordInfo.setBankDetailNo(req.getBankDetailNo());
        marginRecordInfo.setFinanceFlowId(req.getFinanceFlowId());
        marginRecordInfo.setOtherAccountBank(req.getOppositeAccountBank());
        marginRecordService.financialAdd(marginRecordInfo);
        //触发保证金管理是否核销完毕
        marginRecordService.financialWriteOff(marginRecordInfo, req.getCollectionCode(), marginRecordInfo.getCollectionAmount(), req.getCollectionDate());
        addMarginToFinanceFlowDetail(FinanceFlowDetailTableEnum.MARGIN_RECORD_INFO.name(), marginRecordInfo);
        sw.stop();
        log.info("FinancialService backRecord end use time {}ms", sw.getLastTaskTimeMillis());
        return R.ok("保证金管理:退款成功");
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<String> backRecordWarranty(ThirdMarginRecordREQ req) {
        log.info("FinancialService backRecord param {}", req);
        if (marginRecordService.count(Wrappers.<MarginRecordInfo>lambdaQuery()
                .eq(MarginRecordInfo::getFlowId, req.getPknumber())) > 0) {
            return R.fail("退款核销，此流水" + req.getPknumber() + "已核销，本次请求为重复申请");
        }
        StopWatch sw = new StopWatch();
        sw.start();

        WarrantyBaseInfo warrantyBaseInfo = warrantyBaseInfoService.getOne(Wrappers.<WarrantyBaseInfo>lambdaQuery()
                .eq(WarrantyBaseInfo::getWarrantyCode, req.getCollectionCode())
                .last(StringUtil.mysqlLimitOne()));


        if (ObjectUtil.isEmpty(warrantyBaseInfo)) {
            return R.fail("质保金管理:无此退款记录");
        }

        WarrantyRecordInfo warrantyRecordInfo = BeanUtil.copyProperties(req, WarrantyRecordInfo.class);
        warrantyRecordInfo.setRecordType(RecordTypeEnum.REFUND.name());
        warrantyRecordInfo.setWriteOff(MarginWriteOffStatusEnum.WRITTEN_OFF.name());
        warrantyRecordInfo.setReview(MarginWriteOffStatusEnum.REVIEWED.name());
        warrantyRecordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        warrantyRecordInfo.setSourceFlag(0);
        //warrantyRecordInfo.setCollectionId(collectionBaseInfo.getId());
        warrantyRecordInfo.setWarrantyId(warrantyBaseInfo.getId());
        warrantyRecordInfo.setFlowId(req.getPknumber());
        warrantyRecordInfo.setCollectionDate(req.getCollectionDate());
        //x10000
        warrantyRecordInfo.setCollectionAmount(LongUtil.other2Long(req.getCollectionAmount().toString()));
        warrantyRecordInfo.setOtherAccountName(req.getOppositeAccountName());
        warrantyRecordInfo.setOtherAccountNumber(req.getOppositeAccountNumber());
        warrantyRecordInfo.setBankDetailNo(req.getBankDetailNo());
        warrantyRecordInfo.setFinanceFlowId(req.getFinanceFlowId());
        warrantyRecordInfo.setOtherAccountBank(req.getOppositeAccountBank());
        warrantyRecordService.financialAdd(warrantyRecordInfo);
        //触发质保金管理是否核销完毕
        warrantyRecordService.financialWriteOff(warrantyRecordInfo, req.getCollectionCode(), warrantyRecordInfo.getCollectionAmount(), req.getCollectionDate());
        addMarginToFinanceFlowDetail(FinanceFlowDetailTableEnum.WARRANTY_RECORD_INFO.name(), warrantyRecordInfo);
        sw.stop();
        log.info("FinancialService backRecord end use time {}ms", sw.getLastTaskTimeMillis());
        return R.ok("质保金管理:退款成功");
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void innerRecord(InnerCollectionRecordREQ req) {
        List<CollectionBaseInfo> list = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, req.getContractId())
                .eq(ObjectUtil.isNotEmpty(req.getCashFlowItem()), CollectionBaseInfo::getCashFlowItem, req.getCashFlowItem())
                .ne(CollectionBaseInfo::getWriteOffStatus, MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED));
        //todo he 此处提供给测试使用，不做限制，生产需去除
        ThirdCollectionRecordREQ thirdReq;
        for (CollectionBaseInfo base : list) {
            thirdReq = new ThirdCollectionRecordREQ();
            thirdReq.setCollectionCode(base.getCode());
            thirdReq.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.name());
            thirdReq.setCollectionDate(ObjectUtil.isNull(req.getCollectionDate()) ? LocalDate.now() : req.getCollectionDate());
            thirdReq.setCollectionAmount(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getPlanCollectionAmount(), base.getCollectionAmount()).toString()));
            thirdReq.setPrincipal(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getPrincipal(), LongUtil.null2zero(base.getCollectionPrincipal())).toString()));
            thirdReq.setInterest(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getInterest(), LongUtil.null2zero(base.getCollectionInterest())).toString()));
            thirdReq.setPenaltyInterest(LongUtil.tenThousand2Dollar(NumberUtil.sub(LongUtil.null2zero(base.getPenaltyInterest()), LongUtil.null2zero(LongUtil.null2zero(base.getCollectionPenaltyInterest()) + LongUtil.null2zero(base.getPenaltyInterestDeductionAmount()))).toString()));
            thirdReq.setCashFlowItem(base.getCashFlowItem());
            //加上罚息
            thirdReq.setCollectionAmount(NumberUtil.add(thirdReq.getCollectionAmount(), thirdReq.getPenaltyInterest()));
            R<String> collectionRecode = collectionRecode(thirdReq);
            if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
                log.error("自动核销失败，原因：{}", collectionRecode.getMsg());
                throw new MithrasException(collectionRecode.getMsg());
            }
        }
    }

    private void noticeFinancialPersonnel(MessageUrlEnum messageUrlEnum, String relation, Long mainId, String code) {
        //通知除财务总监外的财务人员 financialmanager caiwu
        List<UserDO> usersByjobcod = userService.getUsersByjobcod(JobEnum.financialmanager.name());
        if (ObjectUtil.isNotEmpty(usersByjobcod)) {
            MessageAddREQ messageAddREQ = new MessageAddREQ();
            messageAddREQ.setFrom("系统提醒");
            messageAddREQ.setFlowid(String.valueOf(mainId));
            messageAddREQ.setRelation(relation);
            messageAddREQ.setNeedOa(false);
            messageAddREQ.setContent(code);
            messageAddREQ.setNoticeSource("系统提醒");
            messageAddREQ.setMessageType(MessageTypeEnum.BILL.name());
            messageAddREQ.setPcurl(StringUtils.format(messageUrlEnum.pcUrl, mainId));
            messageAddREQ.setAppurl(messageUrlEnum.appUrl);
            messageAddREQ.setTo(usersByjobcod.stream().map(UserDO::getId).collect(Collectors.toList()));
            messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
        }
    }

}
