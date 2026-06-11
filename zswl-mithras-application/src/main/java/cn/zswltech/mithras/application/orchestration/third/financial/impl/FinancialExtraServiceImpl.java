package cn.zswltech.mithras.application.orchestration.third.financial.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.common.ResultCode;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.third.financialshare.application.FinancialExtraService;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2023/12/15
 * @description
 */
@Slf4j
@Service
public class FinancialExtraServiceImpl implements FinancialExtraService {
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FinancialService financialService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<String> paymentRecodeExtra(ThirdPaymentDetailREQ req) {
        log.info("付款记录明细入参{}", JSONUtil.toJsonStr(req));
        R<String> result = R.fail();
        if (!req.isReqNeedHandle()) {
            log.info("接收到付款核销通知，标记为无需处理，忽略不处理");
            return R.ok("接收到付款核销通知，标记为无需处理，忽略不处理");
        }
        long maxMount = 0L;
        //有投放应该确认是否有需要自动核销的
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getPaymentCode, req.getCollectionCode()));
        if (ObjectUtil.isEmpty(paymentBaseInfo)) {
            log.error("FinancialService paymentRecode error : {}", req);
            return R.fail("未查询到此付款编号");
        }
        if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
            List<CollectionBaseInfo> list = new ArrayList<>();
//            maxMount += LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount());
//            List<PaymentActualDetailUnconfirmed> planList = SpringUtil.getBean(PaymentActualDetailUnconfirmedService.class).listByPaymentId(paymentBaseInfo.getId());
            List<PaymentActualDetail> actualList = SpringUtil.getBean(PaymentActualDetailService.class).listByPaymentId(paymentBaseInfo.getId());
//            long totalPlan = Optional.ofNullable(planList).map(e -> e.stream().filter(i -> Objects.nonNull(i.getPaidInAmount())).mapToLong(PaymentActualDetailUnconfirmed::getPaidInAmount).sum()).orElse(0L);
            long totalActual = Optional.ofNullable(actualList).map(e -> e.stream().filter(i -> Objects.nonNull(i.getPaidInAmount())).mapToLong(PaymentActualDetail::getPaidInAmount).sum()).orElse(0L);
            maxMount = paymentBaseInfo.getApplyPaymentAmount() - totalActual;
            //尝试自动核销首期租金，质保金
            if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getDownPaymentType()) && ObjectUtil.isEmpty(actualList)) {
                //不包括的首期租金自动核销
                list.addAll(collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.FIRST_RENT.name())
                        .eq(CollectionBaseInfo::getPaymentId, paymentBaseInfo.getId())
                        .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())));
                maxMount -= LongUtil.null2zero(paymentBaseInfo.getDownPayment());
            }
            //尝试自动核销质保金
            if (YesOrNoNumberEnum.NO.getCode().equals(paymentBaseInfo.getRetentionMoneyType()) && ObjectUtil.isEmpty(actualList)) {
                //包括的质保金自动核销
                list.addAll(collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RETENTION_MONEY.name())
                        .eq(CollectionBaseInfo::getPaymentId, paymentBaseInfo.getId())
                        .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())));
                maxMount -= LongUtil.null2zero(paymentBaseInfo.getRetentionMoney());
            }
            if(req.getPaidInAmount().compareTo(LongUtil.tenThousand2Dollar(Long.toString(maxMount))) > 0){
                return R.fail("核销失败：最多可核销" + LongUtil.tenThousand2Dollar(String.valueOf(maxMount)) + "元");
            }
            //核销
            result = financialService.paymentRecode(req);
            if (ObjectUtil.isNotEmpty(list)) {
                list.forEach(base -> {
                    //核销收款
                    ThirdCollectionRecordREQ thirdReq = new ThirdCollectionRecordREQ();
                    thirdReq.setCollectionCode(base.getCode());
                    thirdReq.setCollectionType(GlobalConstants.AUTO_DEDUCTION_PAY_METHOD);
                    thirdReq.setCollectionDate(ObjectUtil.isNull(req.getPaidInDate()) ? base.getPlanCollectionDate() : req.getPaidInDate());
                    thirdReq.setCollectionAmount(NumberUtil.add(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getPlanCollectionAmount(),
                            base.getCollectionAmount()).toString()), thirdReq.getPenaltyInterest()));
                    thirdReq.setPrincipal(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getPrincipal(), LongUtil.null2zero(base.getCollectionPrincipal())).toString()));
                    thirdReq.setInterest(LongUtil.tenThousand2Dollar(NumberUtil.sub(base.getInterest(), LongUtil.null2zero(base.getCollectionInterest())).toString()));
                    thirdReq.setPenaltyInterest(LongUtil.tenThousand2Dollar(NumberUtil.sub(LongUtil.null2zero(base.getPenaltyInterest()), LongUtil.null2zero(LongUtil.null2zero(base.getCollectionPenaltyInterest()) + LongUtil.null2zero(base.getPenaltyInterestDeductionAmount()))).toString()));
                    thirdReq.setCashFlowItem(base.getCashFlowItem());
                    //加上罚息
                    thirdReq.setPknumber(IdUtil.fastSimpleUUID());
                    R<String> collectionRecode = financialService.collectionRecode(thirdReq);
                    if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
                        log.error("核销失败，原因：{}", collectionRecode.getMsg());
                        throw new MithrasException(collectionRecode.getMsg());
                    }
                    //核销付款
                    ThirdPaymentDetailREQ paymentDetailREQ = new ThirdPaymentDetailREQ();
                    paymentDetailREQ.setCollectionCode(req.getCollectionCode());
                    paymentDetailREQ.setPaymentMethod(GlobalConstants.AUTO_DEDUCTION_PAY_METHOD);
                    paymentDetailREQ.setPaidInAmount(thirdReq.getCollectionAmount());
                    paymentDetailREQ.setPaidInDate(req.getPaidInDate());
                    paymentDetailREQ.setOurAccountName(req.getOurAccountName());
                    paymentDetailREQ.setOurAccountBank(req.getOurAccountBank());
                    paymentDetailREQ.setOurAccountNumber(req.getOurAccountNumber());
                    paymentDetailREQ.setOppositeAccountBank(req.getOppositeAccountBank());
                    paymentDetailREQ.setOppositeAccountName(req.getOppositeAccountName());
                    paymentDetailREQ.setOppositeAccountNumber(req.getOppositeAccountNumber());
                    paymentDetailREQ.setPknumber(IdUtil.fastSimpleUUID());
                    financialService.paymentRecode(paymentDetailREQ);
                });
            }
        }
        return result;
    }
}
