package cn.zswltech.mithras.service.service.third.financial.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailException;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.monthly.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.service.enums.third.CQPaymentMethodENUM;
import cn.zswltech.mithras.service.enums.third.ExceptionSourceENUM;
import cn.zswltech.mithras.service.enums.third.FinancialDevUrlENUM;
import cn.zswltech.mithras.service.mapper.model.ExceptionRequestInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.margin.MarginRecordInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.third.SyncCqRecord;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.ExceptionRequestRecordService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.margin.MarginRecordService;
import cn.zswltech.mithras.service.service.monthly.MonthlyManagementBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.third.financial.req.*;
import cn.zswltech.mithras.service.service.third.financial.resp.CQ2WithdrawRSP;
import cn.zswltech.mithras.service.service.third.financial.resp.FinancialBaseRSP;
import cn.zswltech.mithras.service.service.third.financial.resp.FinancialCommonRSP;
import cn.zswltech.mithras.service.service.third.financial.vo.*;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DatePattern.NORM_DATE_PATTERN;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getProperty;
import static cn.zswltech.mithras.service.service.third.financial.vo.CqOperationType.ADD;
import static cn.zswltech.mithras.service.service.third.financial.vo.CqOperationType.MODIFY;
import static cn.zswltech.mithras.service.util.LongUtil.null2zero;
import static cn.zswltech.mithras.service.util.LongUtil.tenThousand2Dollar;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * @author yibin
 */
@Slf4j
@Service
public class FinancialManagerServiceImpl2 {

    @Resource
    private SyncCqRecordService syncCqRecordService;

    @Resource
    private PlatformApiHandleFactory platformApiHandleFactory;

    @Resource
    private AppAuthConfig appAuthConfig;

    @Resource
    private ExceptionRequestRecordService exceptionRequestInfoService;

    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private MonthlyManagementBaseInfoService monthlyManagementBaseInfoService;


    @Value("${mithras.remote.authOrg}")
    private String orgCode;

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() + 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));


    /**
     * 租金表有变化，通知苍穹  这里实际已经不推送了，仅记录日志
     *
     * @param bizInfo        基本信息
     * @param receiptRentMap 租金表变更期限详情
     **/
    public List<CQReceiveRentREQ> adjustReceipts(SyncCqReqBizInfo bizInfo, Map<Long, FinancialCollectionRentVO> receiptRentMap) {
        //苍穹对接开关开关
        if (cqIsNotOpen()) {
            return null;
        }
        List<CQReceiveRentREQ> req = new ArrayList();
        for (Long receiptId : receiptRentMap.keySet()) {
            req.add(adjustSingleReceipt(bizInfo, receiptRentMap.get(receiptId)));
        }
        //--将报送流水记录存储到SyncCqRecord
        PlatformApiHandler<List<CQReceiveRentREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_RENT_RECEIVE);
        //同步
        //--执行http请求到苍穹
        //--将流水记录的状态置为发送成功
        //过滤为null的期项，产生原因 历史期限不在融租易维护，为统一导入，导致取消时未查询到数据
        req = req.stream().filter(ObjectUtil::isNotEmpty).collect(Collectors.toList());
        List<CQReceiveRentREQ> finalReq = req;
        CompletableFuture.runAsync(() ->{
            platformApiHandler.execute(finalReq);
        }, threadPool);
        //清理null
        if (ObjectUtil.isNotEmpty(req)) {
            req.forEach(e -> {
                List<CQReceiveRentREQ.ReceiveRentBody> entry = e.getEntry();
                if (ObjectUtil.isNotEmpty(entry)) {
                    e.setEntry(entry.stream().filter(Objects::nonNull).collect(Collectors.toList()));
                }
            });
        }
        return req;
    }

    /**
     * 通知第三方收款
     **/
    @Deprecated
    public void receiveExec(SyncCqReqBizInfo bizInfo, List<FinancialCollectionVO> financialCollectionVOS){
        //苍穹开关配置是否打开
        if (cqIsNotOpen()) {
            return;
        }
       /* PlatformApiHandler<List<CQReceiveREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_RECEIVE);
        //同步
        CompletableFuture.runAsync(() ->{
            platformApiHandler.execute(receiveSingleReq(bizInfo, financialCollectionVOS));
        }, threadPool);*/

    }

    /**
     * 通知第三方付款
     **/
    @Deprecated
    public void paymentExec(SyncCqReqBizInfo bizInfo, List<FinancialPaymentVO> financialCollectionVOS){
        //苍穹开关配置是否打开
        if (cqIsNotOpen()) {
            return;
        }
      /*  PlatformApiHandler<List<CQBillPaymentREQ>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ_BILL_PAYMENT);
        CompletableFuture.runAsync(() ->{
            platformApiHandler.execute(payment2CqReq(bizInfo, financialCollectionVOS));
        }, threadPool);*/

        log.info("FinancialManagerServiceImpl2 cqPaymentExec over");
    }

    //二期推送应收单
    public void planCollectionExec(List<CQ2PlanCollectionVO> cq2PlanCollectionVOReq){
        if (cqIsNotOpen() || ObjectUtil.isEmpty(cq2PlanCollectionVOReq)) {
            return;
        }
        List<CQ2PlanCollectionVO> cq2PlanCollectionVO = new ArrayList<>();
        cq2PlanCollectionVOReq.forEach(e -> {
            if(ObjectUtil.isNotEmpty(e.getEntry())) {
                cq2PlanCollectionVO.add(e);
            }
        });
        if(cq2PlanCollectionVO.isEmpty()) {
            log.warn("应收单数据异常 {}", cq2PlanCollectionVOReq);
            return;
        }
         //构建基本信息
        cq2PlanCollectionVO.forEach(this::buildPlanCollection);
        PlatformApiHandler<List<CQ2PlanCollectionReq>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_PLAN_COLLECTION);
        CompletableFuture.runAsync(() ->{
            platformApiHandler.execute(planCollection2cqReq(cq2PlanCollectionVO));
        }, threadPool);
        log.info("FinancialManagerServiceImpl2 planCollectionExec over");
    }

    //二期推收款单
    public void collectionExec(List<CQ2CollectionVO> vos){
        if (cqIsNotOpen()) {
            return;
        }
        vos.forEach(this::buildCollection);
        PlatformApiHandler<List<CQ2CollectionReq>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_COLLECTION);
        CompletableFuture.runAsync(() ->{
            platformApiHandler.execute(collection2cqReq(vos));
        }, threadPool);
        log.info("FinancialManagerServiceImpl2 collectionExec over");
    }


    //二期推付款单
    public void cq2PaymentExec(SyncCqReqBizInfo bizInfo, List<CQ2PaymentVO> vos) {
        if (cqIsNotOpen()) {
            return;
        }
        log.info("FinancialManagerServiceImpl2 cq2PaymentExec req {}", vos);
        //补充基本信息
        vos.forEach(vo -> buildPayment(vo, bizInfo));
        PlatformApiHandler<List<CQ2PaymentReq>, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_PAYMENT);
        CompletableFuture.runAsync(() -> {
            platformApiHandler.execute(payment2cqReq(vos));
        }, threadPool);
        log.info("FinancialManagerServiceImpl2 cq2PaymentExec over");
    }

    //二期推记账申请单
    public void cq2AccountApplicationExec(CQ2AccountApplicationVO vo) {
        if (cqIsNotOpen()) {
            return;
        }
        log.info("FinancialManagerServiceImpl2 cq2AccountApplicationExec req {}", vo == null ? "" : JSONUtil.toJsonStr(vo));
        if(ObjectUtil.isEmpty(vo) || ObjectUtil.isEmpty(vo.getTallyentryentity())) {
            return;
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> needBody = vo.getTallyentryentity().stream().filter(e -> e.getCico_amount().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(needBody)) {
            return;
        }
        vo.setTallyentryentity(needBody);
        //过滤为0的变更数据
        this.buildAccountApplicationExec(vo);
        PlatformApiHandler<CQ2AccountApplicationReq, FinancialCommonRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_ACCOUNT_APPLICATION);
        CompletableFuture.runAsync(() -> {
            platformApiHandler.execute(payment2CQ2AccountApplicationReq(vo));
        }, threadPool);
        log.info("FinancialManagerServiceImpl2 cq2AccountApplicationExec over");
    }
    boolean withDraw =  false;
    //二期删除接口
    public CQ2WithdrawRSP cq2Withdraw(CQ2WithdrawVO vo) {
        //应收单也要撤回收款单
        if (ExceptionSourceENUM.BUSINESS_FLOW.name().equals(vo.getSource()) && FinancialDevUrlENUM.CQ2_PLAN_COLLECTION.name().equals(vo.getPlatform())) {
            CollectionRecordInfo collectionRecordInfo = SpringContextHolder.getBean(CollectionRecordInfoService.class).getById(Integer.valueOf(vo.getBusinessKey()));
            ExceptionRequestInfo collectionRequestInfo = exceptionRequestInfoService.getOne(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                    .eq(ExceptionRequestInfo::getBusinessKey, String.valueOf(collectionRecordInfo.getCollectionId()))
                    .eq(ExceptionRequestInfo::getSource, ExceptionSourceENUM.BANK_FLOW.name())
                    .eq(ExceptionRequestInfo::getPlatform, FinancialDevUrlENUM.CQ2_COLLECTION.name())
                    .isNull(ExceptionRequestInfo::getWithdrawFlag)
                    .orderByDesc(ExceptionRequestInfo::getId)
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNotEmpty(collectionRequestInfo)) {
                if (ObjectUtil.equal(collectionRequestInfo.getRetryFlag(), YesOrNoNumberEnum.NO.getCode())) {
                    exceptionRequestInfoService.removeById(collectionRequestInfo.getId());
                } else {
                    CQ2WithdrawReq cq2WithdrawReq = new CQ2WithdrawReq();
                    cq2WithdrawReq.setBillIdentification(Optional.ofNullable(PlatformApiEnum.getWithdrawType(PlatformApiEnum.CQ2_COLLECTION)).orElseThrow(() -> new MailException("暂不支持此类型删除接口")));
                    PlatformApiHandler<CQ2WithdrawReq, CQ2WithdrawRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_WITHDRAW);
                    cq2WithdrawReq.setBillNo(platformApiHandleFactory.getPlatformApiRequestInspector(PlatformApiEnum.CQ2_COLLECTION).getBillNo(collectionRequestInfo.getReqData()));
                    platformApiHandler.execute(cq2WithdrawReq);
                }
            }
        }
        //获取
        ExceptionRequestInfo exceptionRequestInfo = exceptionRequestInfoService.getOne(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                .eq(ExceptionRequestInfo::getBusinessKey, getBusinessKey(vo))
                .eq(ExceptionRequestInfo::getSource, vo.getSource())
                .eq(ExceptionRequestInfo::getPlatform, vo.getPlatform())
                .isNull(ExceptionRequestInfo::getWithdrawFlag)
                .orderByDesc(ExceptionRequestInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        PlatformApiEnum of = Optional.ofNullable(PlatformApiEnum.of(vo.getPlatform())).orElseThrow(() -> new MailException("暂不支持此类型接口"));
        CQ2WithdrawRSP rsp = null;
        if(ObjectUtil.isNotEmpty(exceptionRequestInfo)) {
            CQ2WithdrawReq cq2WithdrawReq = new CQ2WithdrawReq();
            cq2WithdrawReq.setBillIdentification(Optional.ofNullable(PlatformApiEnum.getWithdrawType(of)).orElseThrow(() -> new MailException("暂不支持此类型删除接口")));
            PlatformApiHandler<CQ2WithdrawReq, CQ2WithdrawRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_WITHDRAW);
            cq2WithdrawReq.setBillNo(platformApiHandleFactory.getPlatformApiRequestInspector(of).getBillNo(exceptionRequestInfo.getReqData()));
            //应收同时删除收款
            if (ObjectUtil.equals(vo.getPlatform(), PlatformApiEnum.CQ2_PLAN_COLLECTION.name())) {
                CollectionRecordInfo collectionRecordInfo = collectionRecordInfoService.getById(Long.parseLong(vo.getBusinessKey()));
                if (ObjectUtil.isNotEmpty(collectionRecordInfo)) {
                    ExceptionRequestInfo collectionRequestInfo = exceptionRequestInfoService.getOne(Wrappers.<ExceptionRequestInfo>lambdaQuery()
                            .eq(ExceptionRequestInfo::getBusinessId, collectionRecordInfo.getBankDetailNo())
                            .eq(ExceptionRequestInfo::getSource, ExceptionSourceENUM.BANK_FLOW.name())
                            .eq(ExceptionRequestInfo::getPlatform, PlatformApiEnum.CQ2_COLLECTION.name())
                            .orderByDesc(ExceptionRequestInfo::getId)
                            .last(StringUtil.mysqlLimitOne()));
                    if (ObjectUtil.isNotEmpty(collectionRequestInfo)) {
                        if (ObjectUtil.equal(collectionRequestInfo.getRetryFlag(), YesOrNoNumberEnum.NO.getCode())) {
                            exceptionRequestInfoService.removeById(collectionRequestInfo.getId());
                        } else {
                            CQ2WithdrawReq collectionWithdrawReq = new CQ2WithdrawReq();
                            collectionWithdrawReq.setBillIdentification(PlatformApiEnum.getWithdrawType(PlatformApiEnum.CQ2_COLLECTION));
                            collectionWithdrawReq.setBillNo(platformApiHandleFactory.getPlatformApiRequestInspector(PlatformApiEnum.CQ2_COLLECTION).getBillNo(collectionRequestInfo.getReqData()));
                            platformApiHandler.execute(collectionWithdrawReq);
                        }
                    }
                }
            }
            if (ObjectUtil.equal(exceptionRequestInfo.getRetryFlag(), YesOrNoNumberEnum.NO.getCode())) {
                exceptionRequestInfoService.removeById(exceptionRequestInfo.getId());
                //成功
                rsp = new CQ2WithdrawRSP();
                rsp.setSuccess(true);
                rsp.setStatus(true);
                withDraw = true;
            } else {
                rsp = platformApiHandler.execute(cq2WithdrawReq);
                exceptionRequestInfo.setWithdrawFlag(rsp != null && (ObjectUtil.equals(rsp.getSuccess(), Boolean.TRUE) || ObjectUtil.equals(rsp.getStatus(), Boolean.TRUE)) ? 0 : 1);
                exceptionRequestInfo.setWithdrawFailMessage(rsp == null ? "撤回失败" : rsp.getMessage());
                exceptionRequestInfoService.updateById(exceptionRequestInfo);
                withDraw = rsp != null && ObjectUtil.equals(rsp.getSuccess(), Boolean.TRUE);
            }
        } else {
            withDraw = true;
        }
        //回滚各模块数据
        if (withDraw) {
            switch (of) {
                case CQ2_PAYMENT:
                    //项目端付款核销
                    if (ExceptionSourceENUM.BUSINESS_FLOW.name().equals(vo.getSource())) {
                        SpringContextHolder.getBean(PaymentActualDetailService.class).withdraw(Long.parseLong(vo.getBusinessKey()));
                    } else if (ExceptionSourceENUM.BUSINESS_MARGIN.name().equals(vo.getSource())) {
                        SpringContextHolder.getBean(MarginRecordService.class).withdraw(Long.parseLong(vo.getBusinessKey()));
                    } else if (ExceptionSourceENUM.FINANCE_SIDE.name().equals(vo.getSource()) || ExceptionSourceENUM.FINANCE_SIDE_MARGIN.name().equals(vo.getSource()) || ExceptionSourceENUM.FINANCE_SIDE_EXPENCE.name().equals(vo.getSource())) {
                        SpringContextHolder.getBean(FundReceiptFlowDetailService.class).withdraw(Long.parseLong(vo.getBusinessKey()));
                    }
                    break;
                case CQ2_COLLECTION:
                    if (ExceptionSourceENUM.FINANCE_SIDE.name().equals(vo.getSource())) {
                        SpringContextHolder.getBean(FundReceiptFlowDetailService.class).withdraw(Long.parseLong(vo.getBusinessKey()));
                    }
                    break;
                case CQ2_PLAN_COLLECTION:
                    //应收
                    if (ExceptionSourceENUM.BUSINESS_FLOW.name().equals(vo.getSource())) {
                        collectionRecordInfoService.withdraw(Long.parseLong(vo.getBusinessKey()));
                    }
                    break;
                case CQ2_ACCOUNT_APPLICATION:
                    if (ExceptionSourceENUM.ASSET_SIDE_AIR_ACCOUNT.name().equals(vo.getSource())) {
                        monthlyManagementBaseInfoService.callBackAntiSettlement(MonthlyModuleTypeEnum.AIR, Long.parseLong(vo.getBusinessKey()));
                    } else if (ExceptionSourceENUM.ASSET_SIDE_PR_ACCOUNT.name().equals(vo.getSource())) {
                        monthlyManagementBaseInfoService.callBackAntiSettlement(MonthlyModuleTypeEnum.RP, Long.parseLong(vo.getBusinessKey()));
                    } else if (ExceptionSourceENUM.ASSET_SIDE_COST_STAMP_DUTY.name().equals(vo.getSource())) {
                        monthlyManagementBaseInfoService.callBackAntiSettlement(MonthlyModuleTypeEnum.STAMP_DUTY_PROJ, Long.parseLong(vo.getBusinessKey()));
                    } else if (ExceptionSourceENUM.FINANCE_SIDE_COST_STAMP_DUTY.name().equals(vo.getSource())) {
                        monthlyManagementBaseInfoService.callBackAntiSettlement(MonthlyModuleTypeEnum.STAMP_DUTY_FIN, Long.parseLong(vo.getBusinessKey()));
                    } else if (ExceptionSourceENUM.ASSET_SIDE_COST_DK.name().equals(vo.getSource()) || ExceptionSourceENUM.ASSET_SIDE_COST_ZR.name().equals(vo.getSource())) {
                        monthlyManagementBaseInfoService.callBackAntiSettlement(MonthlyModuleTypeEnum.COST, Long.parseLong(vo.getBusinessKey()));
                    }
                default:
            }
        }
        return rsp;
    }

    public void sendAccountAge(List<CQ2AccountAgeAddREQ> reqs) {
        if (cqIsNotOpen() || ObjectUtil.isEmpty(reqs)) {
            return;
        }
        log.info("FinancialManagerServiceImpl2 sendAccountAge req {}", reqs);
        if (CollectionUtil.isEmpty(reqs)) {
            return;
        }
        //补充基本信息
        PlatformApiHandler<List<CQ2AccountAgeAddREQ>, FinancialBaseRSP> platformApiHandler = platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.CQ2_ACCOUNT_AGE_ADD);
        CompletableFuture.runAsync(() -> {
            platformApiHandler.execute(reqs);
        }, threadPool);
        log.info("FinancialManagerServiceImpl2 cq2PaymentExec over");
    }

    private String getBusinessKey(CQ2WithdrawVO vo) {
        PlatformApiEnum of = Optional.ofNullable(PlatformApiEnum.of(vo.getPlatform())).orElseThrow(() -> new MailException("暂不支持此类型接口"));
        switch (of) {
            case CQ2_PAYMENT:
                //项目端付款核销
                if (ExceptionSourceENUM.BUSINESS_FLOW.name().equals(vo.getSource())) {
                    return Optional.ofNullable(SpringContextHolder.getBean(PaymentActualDetailService.class).getById(Long.parseLong(vo.getBusinessKey()))).map(PaymentActualDetail::getPaymentId).map(String::valueOf).orElse(vo.getBusinessKey());
                } else if (ExceptionSourceENUM.BUSINESS_MARGIN.name().equals(vo.getSource())) {
                    return Optional.ofNullable(SpringContextHolder.getBean(MarginRecordService.class).getById(Long.parseLong(vo.getBusinessKey()))).map(MarginRecordInfo::getMarginId).map(String::valueOf).orElse(vo.getBusinessKey());
                } else if (ExceptionSourceENUM.FINANCE_SIDE.name().equals(vo.getSource()) || ExceptionSourceENUM.FINANCE_SIDE_MARGIN.name().equals(vo.getSource()) || ExceptionSourceENUM.FINANCE_SIDE_EXPENCE.name().equals(vo.getSource())) {
                    return Optional.ofNullable(SpringContextHolder.getBean(FundReceiptFlowDetailService.class).getById(Long.parseLong(vo.getBusinessKey()))).map(FundReceiptFlowDetail::getCashFlowCode).orElse(vo.getBusinessKey());
                }
                return vo.getBusinessKey();
            case CQ2_PLAN_COLLECTION:
                //应收
                if (ExceptionSourceENUM.BUSINESS_FLOW.name().equals(vo.getSource())) {
                    CollectionRecordInfo byId = collectionRecordInfoService.getById(Long.parseLong(vo.getBusinessKey()));
                    return byId == null ? vo.getBusinessKey() : String.valueOf(byId.getCollectionId());
                }
                return vo.getBusinessKey();
            case CQ2_COLLECTION:
                //项目端付款核销
                if (ExceptionSourceENUM.FINANCE_SIDE.name().equals(vo.getSource()) || ExceptionSourceENUM.FINANCE_SIDE_MARGIN.name().equals(vo.getSource()) || ExceptionSourceENUM.FINANCE_SIDE_EXPENCE.name().equals(vo.getSource())) {
                    return Optional.ofNullable(SpringContextHolder.getBean(FundReceiptFlowDetailService.class).getById(Long.parseLong(vo.getBusinessKey()))).map(FundReceiptFlowDetail::getBankFlowNo).orElse(vo.getBusinessKey());
                } else {
                    return vo.getBusinessKey();
                }
            case CQ2_ACCOUNT_APPLICATION:
            default:
                return vo.getBusinessKey();
        }
    }
    private void buildAccountApplicationExec(CQ2AccountApplicationVO vo){
        vo.setTallycompany_number(orgCode);
        vo.setCreator_number(appAuthConfig.getClientNo());
        vo.setCico_system(FinancialConstants.RZY);
        vo.setCompany_number(orgCode);
        //保持一致
        vo.setBizdate(vo.getTallydate());
        vo.setCico_period_number(vo.getCico_period_number().replaceAll("-", ""));
        vo.setCico_sourcebillno(vo.getCico_sourcebillno().substring(0, Math.min(49, vo.getCico_sourcebillno().length() - 1)));
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> details = vo.getTallyentryentity();
        if (details != null) {
            details.forEach(detail -> {
                detail.setE_asstact(orgCode);
                detail.setCico_amount(this.bigDecimalHandle(detail.getCico_amount()));
                detail.setCico_bhsje(this.bigDecimalHandle(detail.getCico_bhsje()));
                detail.setCico_hsje(this.bigDecimalHandle(detail.getCico_hsje()));
                detail.setCico_se(this.bigDecimalHandle(detail.getCico_se()));
                detail.setTallyamount(this.bigDecimalHandle(detail.getTallyamount()));
            });
        }
    }

    private void buildCollection(CQ2CollectionVO vo){
        if(ObjectUtil.isNotEmpty(vo) && ObjectUtil.isNotEmpty(vo.getEntry())){
            vo.setCico_ishavecontr(false);//是否合同收付
            vo.setCico_billtypeid_number("cas_recbill_BT");
            vo.setOrg_number(orgCode);
            vo.setOpenorg(orgCode);
            vo.setCreator_number(appAuthConfig.getClientNo());
            vo.setCurrency_number(FinancialConstants.RMB_NAME);//
            vo.setExratetable_number(FinancialConstants.ERT01);//
            vo.setCico_srcbillno(vo.getCico_srcbillno().substring(0, Math.min(49, vo.getCico_srcbillno().length() - 1)));
            vo.setActrecamt(this.bigDecimalHandle(vo.getActrecamt()));
            vo.setLocalamt(this.bigDecimalHandle(vo.getLocalamt()));
            vo.getEntry().forEach(body -> {
                body.setE_settleorg_number(orgCode);
                body.setE_receivablelocamt(body.getE_receivableamt());
                body.setE_discountamt(this.bigDecimalHandle(body.getE_discountamt()));
                body.setE_receivableamt(this.bigDecimalHandle(body.getE_receivableamt()));
                body.setE_receivablelocamt(this.bigDecimalHandle(body.getE_receivablelocamt()));
                body.setE_actamt(this.bigDecimalHandle(body.getE_actamt()));
            });
        }
    }

    private void buildPlanCollection(CQ2PlanCollectionVO cq2PlanCollectionVO){
        cq2PlanCollectionVO.setRequestId(IdUtil.simpleUUID());
        cq2PlanCollectionVO.setExchangerate(BigDecimal.valueOf(1));
        cq2PlanCollectionVO.setCico_srcsystem(FinancialConstants.RZY);
        cq2PlanCollectionVO.setCico_isinvoice(false);
        cq2PlanCollectionVO.setCreator_number(appAuthConfig.getClientNo());
        cq2PlanCollectionVO.setOrg_number(orgCode);
        cq2PlanCollectionVO.setRecorg_number(orgCode);
        cq2PlanCollectionVO.setCico_applyorg_number(orgCode);
        cq2PlanCollectionVO.setCurrency_number(FinancialConstants.RMB);
        cq2PlanCollectionVO.setBilltype_number(FinancialConstants.AR_FINARBILL_BT_ZB);//单据类型
        cq2PlanCollectionVO.setCico_srcbillno(cq2PlanCollectionVO.getCico_srcbillno().substring(0, Math.min(49, cq2PlanCollectionVO.getCico_srcbillno().length() - 1)));
        //todo 流水号
        if(ObjectUtil.isEmpty(cq2PlanCollectionVO.getCico_paynum_rby())){
            cq2PlanCollectionVO.setCico_paynum_rby(UUIDUtil.genUuid());
        }
        cq2PlanCollectionVO.setExchangerate(this.bigDecimalHandle(cq2PlanCollectionVO.getExchangerate()));
        cq2PlanCollectionVO.setCico_acctagebegining(cq2PlanCollectionVO.getBizdate());
        List<CQ2PlanCollectionVO.CQ2PlanCollectionVOBody> entry = cq2PlanCollectionVO.getEntry();
        if (ObjectUtil.isNotEmpty(entry)) {
            entry.forEach(e -> {
                e.setE_quantity(this.bigDecimalHandle(e.getE_quantity()));
                e.setE_taxrate(this.bigDecimalHandle(e.getE_taxrate()));
                e.setE_unitprice(this.bigDecimalHandle(e.getE_unitprice()));
                e.setE_taxunitprice(this.bigDecimalHandle(e.getE_taxunitprice()));
                e.setE_tax(this.bigDecimalHandle(e.getE_tax()));
                e.setE_recamount(this.bigDecimalHandle(e.getE_recamount()));
            });
        }

    }
    private void buildPayment(CQ2PaymentVO vo, SyncCqReqBizInfo bizInfo){
        vo.setSettleorg_number(orgCode);
        vo.setApplyorg_number(orgCode);
        if(ObjectUtil.isNotEmpty(bizInfo) && ObjectUtil.isNotEmpty(bizInfo.getOrgCode())){
            vo.setCico_dept_number(bizInfo.getOrgCode());
        }
        vo.setPayorg_number(orgCode);
        vo.setCico_ishavecontr(false);//false
        vo.setCico_srcsystem(FinancialConstants.RZY);
        vo.setCreator_number(appAuthConfig.getClientNo());
        vo.setCico_srcbillno(vo.getCico_srcbillno().substring(0, Math.min(49, vo.getCico_srcbillno().length() - 1)));

        if(ObjectUtil.isEmpty(vo.getCico_paynum_rby())){
            vo.setCico_paynum_rby(UUIDUtil.genUuid());
        }
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        List<CQ2PaymentVO.CQ2PaymentVOEntry> entry = vo.getEntry();
        if (ObjectUtil.isNotEmpty(entry)) {
            entry.forEach(body -> {
                body.setCico_settemenorg_number(orgCode);
                if (ObjectUtil.isNotEmpty(body.getE_settlementtype_number())) {
                    body.setE_settlementtype_number(Optional.ofNullable(CQPaymentMethodENUM.ofDisplay(body.getE_settlementtype_number())).map(CQPaymentMethodENUM::name).orElse(CQPaymentMethodENUM.JSFS16.name()));
                    if (ObjectUtil.isEmpty(body.getCico_uniquecode())) {
                        body.setCico_uniquecode(UUIDUtil.genUuid());
                    }
                }
                body.setE_applyamount(this.bigDecimalHandle(body.getE_applyamount()));
            });
        }
    }

    private List<CQ2PlanCollectionReq> planCollection2cqReq(List<CQ2PlanCollectionVO> vos){
        return BeanUtil.copyToList(vos, CQ2PlanCollectionReq.class);
    }
    private List<CQ2CollectionReq> collection2cqReq(List<CQ2CollectionVO> vos){
        return BeanUtil.copyToList(vos, CQ2CollectionReq.class);
    }

    private List<CQ2PaymentReq> payment2cqReq(List<CQ2PaymentVO> vos){
        return BeanUtil.copyToList(vos, CQ2PaymentReq.class);
    }

    private CQ2AccountApplicationReq payment2CQ2AccountApplicationReq(CQ2AccountApplicationVO vo){
        return BeanUtil.copyProperties(vo, CQ2AccountApplicationReq.class);
    }

    private List<CQBillPaymentREQ> payment2CqReq(SyncCqReqBizInfo bizInfo, List<FinancialPaymentVO> financialCollectionVOS){
        //构建付款申请
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        List<CQBillPaymentREQ> payment2CqReqs = new ArrayList<>();
        financialCollectionVOS.forEach(financialPaymentVO -> {
            CQBillPaymentREQ cqBillPaymentREQ = new CQBillPaymentREQ();
            cqBillPaymentREQ.setSourcebillno(financialPaymentVO.getPaymentCode());
            cqBillPaymentREQ.setCreator(bizInfo.getProjSponsorUserPhone());//主办
            cqBillPaymentREQ.setBizdate(financialPaymentVO.getApplyPaymentDate().format(dateTimeFormatter));//计划收款日期
            cqBillPaymentREQ.setSettleorg(bizInfo.getOrgCode());
            //客户名称
            //收款人银行账号
            cqBillPaymentREQ.setCico_contractnumun(financialPaymentVO.getContractCode());
            //添加首期租金信息
            cqBillPaymentREQ.setIsinitialrent(financialPaymentVO.getIsInitialRent());
            cqBillPaymentREQ.setInitialrentamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(financialPaymentVO.getInitialRentAmount()))));
            cqBillPaymentREQ.setCico_warrantyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(financialPaymentVO.getRetentionMoney()))));
            CQBillPaymentREQ.PaymentBody paymentBody = cqBillPaymentREQ.new PaymentBody();
            paymentBody.setE_assacct(bizInfo.getCustomer());
            paymentBody.setE_paymenttype(financialPaymentVO.getPaymentType());
            paymentBody.setE_applyamount(LongUtil.tenThousand2Dollar(financialPaymentVO.getApplyPaymentAmount().toString()));//应收金额
            cqBillPaymentREQ.setEntry(Collections.singletonList(paymentBody));
            payment2CqReqs.add(cqBillPaymentREQ);
        });
       return  payment2CqReqs;
    }

    public List<CQReceiveREQ> receiveSingleReq(SyncCqReqBizInfo bizInfo, List<FinancialCollectionVO> financialCollectionVOS){
        List<CQReceiveREQ> cqReceiveREQS = new ArrayList<>();
        for (FinancialCollectionVO financialCollectionVO : financialCollectionVOS) {
            CQReceiveREQ cqReceiveREQ = new CQReceiveREQ();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
            cqReceiveREQ.setSourcebillno(financialCollectionVO.getCode());
            cqReceiveREQ.setCreator(bizInfo.getProjSponsorUserPhone());//主办
            cqReceiveREQ.setBizdate(financialCollectionVO.getPlanCollectionDate().format(dateTimeFormatter));//计划收款日期
            cqReceiveREQ.setMaturitydate(financialCollectionVO.getMaturityDate());//到期日
            cqReceiveREQ.setOrg(bizInfo.getOrgCode());
            cqReceiveREQ.setAsstact(bizInfo.getCustomer());
            cqReceiveREQ.setCico_contractnumun(financialCollectionVO.getContractCode());
            //租赁业务一个收款编号下只有一条收款记录
            CQReceiveREQ.ReceiveBody receiveBody = cqReceiveREQ.new ReceiveBody();
            receiveBody.setCico_incomeitems(financialCollectionVO.getCashFlowItem());//收入项目
            receiveBody.setE_receivableamt(LongUtil.tenThousand2Dollar(financialCollectionVO.getPlanCollectionAmount().toString()));//应收金额
            cqReceiveREQ.setEntry(ListUtil.toList(receiveBody));
            cqReceiveREQS.add(cqReceiveREQ);
        }
        return cqReceiveREQS;
    }

    public CQReceiveRentREQ adjustSingleReceipt(SyncCqReqBizInfo bizInfo, FinancialCollectionRentVO vo) {
        CQReceiveRentREQ req = new CQReceiveRentREQ();
        req.setSourcebillno(vo.getReceiptCode());//租金表标识
        req.setCreator(bizInfo.getProjSponsorUserPhone());//主办
        req.setBilltype("ar_finarbill_BT_zb");
        req.setSettleorg(bizInfo.getOrgCode());
        req.setCustome(bizInfo.getCustomer());
        req.setContractCode(vo.getContractCode());
        req.setDepartment(bizInfo.getOrgCode());
        //查询合同上次请求数据
        Map<String, SyncCqRecord> syncCqRecordMap = new HashMap<>();
        syncCqRecordService.getLatestByReceiptCode(vo.getReceiptCode()).forEach(rent -> {
            syncCqRecordMap.putIfAbsent(rent.getRentActualCode(), rent);
        });
        //--处理【新增】
        if (isNotEmpty(vo.getAddRentActual())) {
            //先取消，防止已有变更影响
            List<CQReceiveRentREQ.ReceiveRentBody> receiveRentBodies = convert2CqReqBody(vo, vo.getUpdateRentActual(), MODIFY, syncCqRecordMap);
            if(CollectionUtil.isNotEmpty(receiveRentBodies)){
                req.getEntry().addAll(receiveRentBodies);
            }
            req.getEntry().addAll(convert2CqReqBody(vo, vo.getAddRentActual(), ADD, syncCqRecordMap));
        }
        //--处理【更新】,更新被拆成删除和新增
        if (isNotEmpty(vo.getUpdateRentActual())) {
            //先取消
            req.getEntry().addAll(convert2CqReqBody(vo, vo.getUpdateRentActual(), MODIFY, syncCqRecordMap));

            //再新增
            req.getEntry().addAll(convert2CqReqBody(vo, vo.getUpdateRentActual(), ADD, syncCqRecordMap));
        }

        //--处理【删除】
        if (isNotEmpty(vo.getRemoveRentActual())) {
            req.getEntry().addAll(convert2CqReqBody(vo, vo.getRemoveRentActual(), MODIFY, syncCqRecordMap));
        }
        return req;
    }

    private List<CQReceiveRentREQ.ReceiveRentBody> convert2CqReqBody(FinancialCollectionRentVO vo, List<SyncCqReqBody> body, CqOperationType type,
                                                                     Map<String, SyncCqRecord> syncCqRecordMap) {
        if (type == ADD) {
            return body.stream().map(e -> {
                        CQReceiveRentREQ.ReceiveRentBody receiveRentBody = new CQReceiveRentREQ.ReceiveRentBody()
                                .setRentActualid(String.join("-", "RZYRENT", IdUtil.fastSimpleUUID()))
                                //现金流编号
                                .setRentActualCode(e.getCode())
                                .setLeaseRate(vo.getLeaseRate())
                                .setDate(e.getDate().format(ofPattern(NORM_DATE_PATTERN)))
                                .setPhase(e.getPhase())
                                //金额统一缩小一万倍
                                .setRent(tenThousand2Dollar(null2zero(e.getRent()).toString()))
                                .setPrincipal(tenThousand2Dollar(null2zero(e.getPrincipal()).toString()))
                                .setInterest(tenThousand2Dollar(null2zero(e.getInterest()).toString()))
                                .setLastAmount(tenThousand2Dollar(null2zero(e.getRemainingPrincipal()).toString()))
                                .setBilling(type.type)
                                .setChangeState(ObjectUtil.isNotEmpty(vo.getChangeState()) ? vo.getChangeState().getDisplay() : null);
                        if(ObjectUtil.isNotEmpty(syncCqRecordMap.get(receiveRentBody.getRentActualCode()))){
                            SyncCqRecord syncCqRecord = syncCqRecordMap.get(receiveRentBody.getRentActualCode());
                            //计算本次与上次的差值
                            receiveRentBody.setRentdifference(receiveRentBody.getRent().subtract(new BigDecimal(syncCqRecord.getRent())))
                                    .setPrincipaldifference(receiveRentBody.getPrincipal().subtract(new BigDecimal(syncCqRecord.getPrincipal())))
                                    .setInterestdifference(receiveRentBody.getInterest().subtract(new BigDecimal(syncCqRecord.getInterest())));
                        } else {
                            receiveRentBody.setRentdifference(receiveRentBody.getRent())
                                    .setPrincipaldifference(receiveRentBody.getPrincipal())
                                    .setInterestdifference(receiveRentBody.getInterest());
                        }
                        return receiveRentBody;
                    }
            ).collect(Collectors.toList());
        }
        if (type == MODIFY) {
            return body.stream().map(e -> {
                SyncCqRecord record = syncCqRecordMap.get(e.getCode());
                if(ObjectUtil.isEmpty(record)) {
                    return null;
                }
                CQReceiveRentREQ.ReceiveRentBody receiveRentBody = new CQReceiveRentREQ.ReceiveRentBody();
                receiveRentBody.setRentActualid(record.getRecordId());
                receiveRentBody.setRentActualCode(record.getRentActualCode());
                receiveRentBody.setLeaseRate(new BigDecimal(record.getLeaseRate()));
                receiveRentBody.setDate(record.getDate());
                receiveRentBody.setPhase(record.getPhase());
                receiveRentBody.setRent(new BigDecimal(record.getRent()));
                receiveRentBody.setPrincipal(new BigDecimal(record.getPrincipal()));
                receiveRentBody.setInterest(new BigDecimal(record.getInterest()));
                receiveRentBody.setLastAmount(new BigDecimal(record.getLastAmount()));
                receiveRentBody.setBilling(type.type);
                receiveRentBody.setChangeState(ObjectUtil.isNotEmpty(vo.getChangeState())? vo.getChangeState().getDisplay() : null);
                return receiveRentBody;
            }).collect(Collectors.toList());
        }

        log.warn("未能生产ReceiveRentBody列表");
        return ListUtil.empty();
    }
    /**
     *与苍穹交互是否未打开
     * @return true 未打开， false 已打开
     **/
    private Boolean cqIsNotOpen(){
        if (!getProperty("cq.enable", Boolean.class)) {
            log.info("苍穹对接开关未开启，不进行调用");
            return true;
        }
        return false;
    }

    private BigDecimal bigDecimalHandle(BigDecimal bigDecimal) {
        if (ObjectUtil.isEmpty(bigDecimal)) {
            return null;
        }
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
