package cn.zswltech.mithras.application.orchestration.workflow.flow.listener.endhandler;

import cn.zswltech.mithras.projectprocess.flow.listener.endhandler.ILifecycleProcessor;

import cn.zswltech.mithras.workflow.flow.listener.endhandler.AbstractProcessEndHandler;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.common.ResultCode;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionManualRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdMarginRecordREQ;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentMethod;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractDeductRentInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRetreatInfo;
import cn.zswltech.mithras.margin.mapper.model.MarginBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.event.CollectionAddEvent;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractDeductRentInfoService;
import cn.zswltech.mithras.contract.core.ContractRetreatInfoService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 保证金退抵流程结束
 *
 * @author wangchuanhao
 * @date 2022/11/9 2:34 PM
 */
@Component
@Slf4j
public class ContractDepostProcessEndHandler extends AbstractProcessEndHandler implements ILifecycleProcessor {
    @Resource
    private ContractRetreatInfoService contractRetreatInfoService;
    @Resource
    private ContractDeductRentInfoService contractDeductRentInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private FinancialService financialService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return ProcessModelTypeEnum.MarginFlowAuto.name().equals(endContext.getModelKey()) ||
                ProcessModelTypeEnum.MarginFlowManually.name().equals(endContext.getModelKey());
    }

    @Override
    public void customfillLifcycleEvent(ProjLifecycleEvent endEvent, ProcessEndContext endContext) {

    }

    @Override
    public void handle(ProcessEndContext endContext) {
        log.info("进入ContractProcessEndHandler.handle方法");
        Long id = Long.valueOf(endContext.getBusinessKey());
        ContractRetreatInfo contractRetreatInfo = contractRetreatInfoService.getById(id);
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractRetreatInfo.getContractId());

        /*变更审批状态*/
        boolean processPass = ProcessBusinessStatusEnum.success(endContext.getEndType());
        if (processPass) {
            contractRetreatInfo.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
            contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.RETREAT_PASS.name());
        }else if(ProcessBusinessStatusEnum.CANCEL.getType().equals(endContext.getEndType())){
            contractRetreatInfo.setProcessStatus(ProcessStatus.CANCEL.name());
            contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.RETREAT_CANCEL.name());
        } else {
            contractRetreatInfo.setProcessStatus(ProcessStatus.APPROVAL_REJECT.name());
            contractBaseInfo.setContractProcessStatus(ContractProcessStatusEnum.RETREAT_REJECT.name());
        }
        contractRetreatInfoService.updateById(contractRetreatInfo);
        contractBaseInfoService.updateById(contractBaseInfo);


        if (!ProcessBusinessStatusEnum.success(endContext.getEndType())) { // 审批不通过
            log.info("流程审批未通过，解锁租金收款计划，不执行核销相关逻辑");
            unLockRent(contractRetreatInfo);
            return;
        }

        /*抵扣金额 > 0*/
        if (contractRetreatInfo.getDeductionAmount() > 0L) {
            deductionAmount(contractRetreatInfo);
        }
        /*3.若本次保证金退款金额＞0&该合同下所有租金已全部核销，则立刻给“出纳”角色发起“租金退还代办通知”，每个用户可对自己待办点击“确认”，确认成功后，该条待办从当前用户“待办列表”移除。*/
        /*如果租金还没有核销完，后面需要定时扫描，核销完成后应该生成通知*/
        if (contractRetreatInfo.getReturnedAmount() > 0L) {
            contractDeductRentInfoService.checkRentAndSend(contractRetreatInfo);
        }

        unLockRent(contractRetreatInfo);
    }


    /**
     * 抵扣:
     */
    private void deductionAmount(ContractRetreatInfo contractRetreatInfo) {
        // 抵扣金额
        Long amount = contractRetreatInfo.getDeductionAmount();
        LocalDate recycleManagerDate = null;  // 计划应收日期

        /*按照ID从小到大，查询到所有配置好需要抵扣的期次，并放入List*/
        LambdaQueryWrapper<ContractDeductRentInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ContractDeductRentInfo::getRetreatInfoId, contractRetreatInfo.getId());
        wrapper.orderByAsc(ContractDeductRentInfo::getId);
        List<ContractDeductRentInfo> contractDeductRentInfoList = contractDeductRentInfoService.list(wrapper);
        List<String> codeList = contractDeductRentInfoList.stream().map(ContractDeductRentInfo::getCode).collect(Collectors.toList());

        /*查询到收款计划*/
        LambdaQueryWrapper<CollectionBaseInfo> collectionWrapper = Wrappers.lambdaQuery();
        collectionWrapper.eq(CollectionBaseInfo::getContractId, contractRetreatInfo.getContractId());
        collectionWrapper.in(CollectionBaseInfo::getCode, codeList);
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectionWrapper);

        /*按照 codeList 的顺序排序  数据库中in无法按照特定顺序排序 这边内存加工*/
        Map<String, CollectionBaseInfo> codeToInfoMap = collectionBaseInfoList.stream()
                .collect(Collectors.toMap(CollectionBaseInfo::getCode, Function.identity()));
        List<CollectionBaseInfo> sortedList = codeList.stream()
                .map(codeToInfoMap::get)
                .filter(Objects::nonNull) // 防止 codeList 中有 code 没查到数据
                .collect(Collectors.toList());

        /*按照重排后的顺序抵扣，先本后息*/
        for (CollectionBaseInfo collectionBaseInfo : sortedList) {
            recycleManagerDate = collectionBaseInfo.getPlanCollectionDate();
            Long collectionAmount = LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()); // 实收金额
            Long needPrincipal = LongUtil.null2zero(collectionBaseInfo.getPrincipal()); // 应收本金

            Long needCollectionAmount = collectionBaseInfo.getPlanCollectionAmount() - collectionAmount; //剩余应收金额（本金+利息）
            if (amount < needCollectionAmount) { // 不足核销本期  先本后息
                Long principal = 0L; // 本金
                Long interest = 0L; // 利息
                if (amount < needPrincipal) {
                    principal = amount;
                    amount = 0L;
                } else {
                    principal = needPrincipal;
                    amount = amount - needPrincipal;
                }

                if (amount > 0L) { // 部分核销，如何有剩余保证金必定不足核销利息
                    interest = amount;
                }

                collectionManualRecord(collectionBaseInfo, principal, interest);
                amount = 0L;
            } else { // 本期核销完毕
                amount = amount - needCollectionAmount;
                collectionManualRecord(collectionBaseInfo, needPrincipal, LongUtil.null2zero(collectionBaseInfo.getInterest()));
            }

            if (amount == 0L) {
                break;
            }
        }

        /* 回收 生成应收计划*/
        if (contractRetreatInfo.getRecyclingFlag() != null && "1".equals(contractRetreatInfo.getRecyclingFlag())) {
            // 应收金额=保证金抵扣金额，计划应收日=最早一期未全部核销的租金计划应收日，若计划应收日＞系统当前时间，则核销状态=未到期，计划应收日≤系统当前时间，则核销状态=未核销；
            //创建保证金收款
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .eq(PaymentBaseInfo::getContractId, contractRetreatInfo.getContractId())
                    .in(PaymentBaseInfo::getPaymentStatus, ListUtil.toList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()))
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
//                CollectionFlowCenterBusinessCollectionManualRecordREQ req = new CollectionFlowCenterBusinessCollectionManualRecordREQ();
                CollectionAddEvent collectionAddEvent = new CollectionAddEvent(paymentBaseInfo.getPaymentCode(), paymentBaseInfo.getContractId(), CashFlowItemEnum.EARNEST_MONEY,
                        contractRetreatInfo.getDeductionAmount(), recycleManagerDate, "MARGIN_RECYCLE");
                ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
            }
        }

        /*校验抵扣后保证金是否归0  且没有推送标记  然后推送苍穹*/
//        marginDeductBackRecord(contractRetreatInfo);  //
    }

    /**
     * 保证金推送苍穹
     */
    private void marginDeductBackRecord(ContractRetreatInfo contractRetreatInfo) {
        /*保证金余额实时取自合同保证金*/
        LambdaQueryWrapper<MarginBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MarginBaseInfo::getContractId, contractRetreatInfo.getContractId());
        wrapper.last(StringUtil.mysqlLimitOne());
        MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(wrapper);

        if (marginBaseInfo.getCollectionAmount() <= 0L && !"1".equals(contractRetreatInfo.getRecyclingFlag())) {
            /*保证金余额归0， 生成付款单   推送苍穹*/
            marginBackRecord(contractRetreatInfo);
        }
    }

    /**
     * 保证金抵扣
     */
    private void marginBackRecord(ContractRetreatInfo contractRetreatInfo) {
        //核销保证金 --抵扣未做关联租金的操作，这里可能是核销只核销对应期项，防止管理任务多次操作,且保证金目前没有票据
        // REFUND_MARGIN(保证金退款), REFUND_MARGIN_DEDUCT(保证金抵扣)
        String collectionType = "REFUND_MARGIN_DEDUCT";
        Long collectionAmount = contractRetreatInfo.getDeductionAmount();

        LambdaQueryWrapper<MarginBaseInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(MarginBaseInfo::getContractId, contractRetreatInfo.getContractId());
        wrapper.last(StringUtil.mysqlLimitOne());
        MarginBaseInfo marginBaseInfo = marginBaseInfoService.getOne(wrapper);

        ThirdMarginRecordREQ thirdMarginRecordREQ = new ThirdMarginRecordREQ();
        thirdMarginRecordREQ.setCollectionType(collectionType);
        thirdMarginRecordREQ.setCollectionCode(marginBaseInfo.getMarginCode());
        thirdMarginRecordREQ.setCollectionDate(LocalDate.now());
        thirdMarginRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(collectionAmount));
        thirdMarginRecordREQ.setPknumber(getPkNumber());
        thirdMarginRecordREQ.setProcessPassFlag(true);
        thirdMarginRecordREQ.setDataSource(WriteOffTypeEnum.AUTO_RECORD.display());
        R<String> collectionRecode = financialService.backRecord(thirdMarginRecordREQ);
        if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
            log.error("核销失败，原因：{}", collectionRecode.getMsg());
            throw new MithrasException(collectionRecode.getMsg());
        }
    }

    private String getPkNumber() {
        return String.join("-", "ZSZL", UUIDUtil.genUuid());
    }


    /**
     * 复用 收款手工核销 保证金抵扣逻辑  (会生成租金收款流水)
     *
     * @param collectionBaseInfo 收款计划
     * @param actualPrincipal    实收本金
     * @param actualInterest     实收利息
     */
    private void collectionManualRecord(CollectionBaseInfo collectionBaseInfo, Long actualPrincipal, Long actualInterest) {
        CollectionFlowCenterBusinessCollectionManualRecordREQ req = new CollectionFlowCenterBusinessCollectionManualRecordREQ();
        req.setCollectionId(collectionBaseInfo.getId().toString());
        req.setCollectionCode(collectionBaseInfo.getCode());
        req.setCashFlowItem(collectionBaseInfo.getCashFlowItem());
        req.setCollectionType(PaymentMethod.REFUND_MARGIN_DEDUCT.display());
        req.setCollectionDate(collectionBaseInfo.getPlanCollectionDate());
        req.setPrincipal(actualPrincipal);
        req.setInterest(actualInterest);
        req.setCollectionAmount(actualPrincipal + actualInterest);


//        SpringUtil.getBean(CollectionFlowCenterService.class).collectionManualRecord(req, true);

        //校验是否超额
        ThirdCollectionRecordREQ thirdCollectionRecordREQ = new ThirdCollectionRecordREQ();
        if (LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) < (LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) + LongUtil.null2zero(req.getPrincipal()) + LongUtil.null2zero(req.getInterest()))) {
            throw new MithrasException("本次核销将导致 累计核销⾦额⼤于计划⾦额，操作失败！");
        }
        if (CashFlowItemEnum.RENT.name().equals(req.getCashFlowItem())) {
            req.setCollectionAmount(LongUtil.null2zero(req.getPrincipal()) + LongUtil.null2zero(req.getInterest()) + LongUtil.null2zero(req.getPenaltyInterest()));
        }

        //默认抵扣自己，传人可抵扣其他合同
        Long deductionContractId = collectionBaseInfo.getContractId();
        if (ObjectUtil.isNotEmpty(req.getDeductionContractId())) {
            deductionContractId = req.getDeductionContractId();
        }
        //检验保证金是否足够抵扣
        MarginBaseInfo marginBaseInfo1 = marginBaseInfoService.getOne(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, deductionContractId));
        //保证今余额不足不能被核销
        if (Objects.isNull(marginBaseInfo1) || req.getCollectionAmount() > marginBaseInfo1.getCollectionAmount()) {
            throw new MithrasException("保证金余额不足，暂时无法核销！");
        }
        //核销保证金
        ThirdMarginRecordREQ thirdMarginRecordREQ = new ThirdMarginRecordREQ();
        thirdMarginRecordREQ.setCollectionType(PaymentMethod.REFUND_MARGIN_DEDUCT.name());
        thirdMarginRecordREQ.setCollectionCode(marginBaseInfo1.getMarginCode());
        thirdMarginRecordREQ.setCollectionDate(req.getCollectionDate());
        thirdMarginRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(req.getCollectionAmount())));
        thirdMarginRecordREQ.setPknumber(getPkNumber());
        thirdMarginRecordREQ.setDataSource(WriteOffTypeEnum.AUTO_RECORD.display()); // 自动核销
        thirdMarginRecordREQ.setCollectionId(req.getCollectionId());
        thirdMarginRecordREQ.setRentCollectionCode(collectionBaseInfo.getCode());
        R<String> collectionRecode = financialService.backRecord(thirdMarginRecordREQ);
        if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
            log.error("核销失败，原因：{}", collectionRecode.getMsg());
            throw new MithrasException(collectionRecode.getMsg());
        }
        //
        thirdCollectionRecordREQ.setDeductionMarginBaseId(marginBaseInfo1.getId());
        thirdCollectionRecordREQ.setDeductionMarginBaseCode(marginBaseInfo1.getMarginCode());

        thirdCollectionRecordREQ.setPknumber(getPkNumber());
        thirdCollectionRecordREQ.setDataSource(WriteOffTypeEnum.AUTO_RECORD.display()); // 自动核销
        thirdCollectionRecordREQ.setCollectionCode(collectionBaseInfo.getCode());
        thirdCollectionRecordREQ.setCollectionType(req.getCollectionType());
        thirdCollectionRecordREQ.setCollectionDate(req.getCollectionDate());
        thirdCollectionRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(req.getCollectionAmount())));
        thirdCollectionRecordREQ.setCashFlowItem(req.getCashFlowItem());
        thirdCollectionRecordREQ.setInvoiceFlag(YesOrNoNumberEnum.YES.getCode());
        thirdCollectionRecordREQ.setPrincipal(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(req.getPrincipal()))));
        thirdCollectionRecordREQ.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(req.getInterest()))));
        thirdCollectionRecordREQ.setPenaltyInterest(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(req.getPenaltyInterest()))));
        //核销流水
        R<String> result = financialService.collectionRecode(thirdCollectionRecordREQ);
        if (ResultCode.FAILURE.getCode() == result.getCode()) {
            log.error("核销失败，原因：{}", result.getMsg());
            throw new MithrasException(result.getMsg());
        }

    }

    /**
     * 租金应收计划进行解锁
     */
    private void unLockRent(ContractRetreatInfo contractRetreatInfo) {
        /*查询到所有配置好需要抵扣的期次*/
        LambdaQueryWrapper<ContractDeductRentInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ContractDeductRentInfo::getRetreatInfoId, contractRetreatInfo.getId());
        List<ContractDeductRentInfo> contractDeductRentInfoList = contractDeductRentInfoService.list(wrapper);
        List<String> codeList = contractDeductRentInfoList.stream().map(ContractDeductRentInfo::getCode).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(codeList)) {// 无抵扣明细，直接返回，无需解锁
            return;
        }
        /*查询到收款计划 并倒序抵扣*/
        LambdaQueryWrapper<CollectionBaseInfo> collectionWrapper = Wrappers.lambdaQuery();
        collectionWrapper.eq(CollectionBaseInfo::getContractId, contractRetreatInfo.getContractId());
        collectionWrapper.in(CollectionBaseInfo::getCode, codeList);
        collectionWrapper.orderByDesc(CollectionBaseInfo::getPhase);
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(collectionWrapper);
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            collectionBaseInfo.setRetreatLock("0");
        }
        collectionBaseInfoService.updateBatchById(collectionBaseInfoList);
    }
}
