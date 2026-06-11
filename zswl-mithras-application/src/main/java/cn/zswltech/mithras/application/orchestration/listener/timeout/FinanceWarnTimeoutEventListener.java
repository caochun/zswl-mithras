package cn.zswltech.mithras.application.orchestration.listener.timeout;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.enums.TimeoutTypeEnum;
import cn.zswltech.mithras.contract.event.timeout.TimeoutNotifyEvent;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.message.client.ding.DingGroups;
import cn.zswltech.mithras.message.client.ding.DingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.Collections;

/**
 * @ClassName TimeoutStartEventListener
 * @Description 收款核销检查
 * @Author jackerhe
 * @Date 2023/4/25 5:36 下午
 * @Version 1.0
 **/
@Component
@Slf4j
public class FinanceWarnTimeoutEventListener implements ApplicationListener<TimeoutNotifyEvent> {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private Id2NameService id2NameService;

    @Value("${spring.profiles.active}")
    protected String active;
    protected static final String PROD = "prod";

    /**
     * 监听起租信息
     **/
    @Override
    public void onApplicationEvent(TimeoutNotifyEvent event) {
        if (!PROD.equals(active)) {
            return;
        }
        try {
            if (TimeoutTypeEnum.COLLECTION_RECORD_WARN.name().equals(event.getType())) {
                log.info("CollectionWarnTimeoutEventListener  onApplicationEvent event {}", event.getKey());
                CollectionBaseInfo collectionBaseInfo = collectionBaseInfoService.getById(Long.valueOf(event.getKey()));
                if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
                    if (LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) < (LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest())) ||
                            LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) < 0 || LongUtil.null2zero(collectionBaseInfo.getPrincipal()) < LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal()) ||
                            LongUtil.null2zero(collectionBaseInfo.getInterest()) < LongUtil.null2zero(collectionBaseInfo.getCollectionInterest())
                    ) {
                        String sb = "收款核销异常:" +
                                "客户" +
                                id2NameService.clientId2NameSingle(collectionBaseInfo.getClientId()) +
                                "下合同为" +
                                collectionBaseInfo.getContractCode() +
                                "的收款编号为" +
                                collectionBaseInfo.getCode() +
                                "的收款金额异常" +
                                "应收金额为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(collectionBaseInfo.getPlanCollectionAmount())).setScale(2, RoundingMode.HALF_UP) +
                                "实收金额为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(collectionBaseInfo.getCollectionAmount())).setScale(2, RoundingMode.HALF_UP) +
                                "应收金额本金为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(collectionBaseInfo.getPrincipal())).setScale(2, RoundingMode.HALF_UP) +
                                "实收金额本金为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(collectionBaseInfo.getCollectionPrincipal())).setScale(2, RoundingMode.HALF_UP) +
                                "应收金额利息为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(collectionBaseInfo.getInterest())).setScale(2, RoundingMode.HALF_UP) +
                                "实收金额利息为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(collectionBaseInfo.getCollectionInterest())).setScale(2, RoundingMode.HALF_UP) +
                                "请及时确认此核销是否需要订正";
                        DingService.ding(sb, DingGroups.CANG_QIONG);
                    }
                }
            } else if (TimeoutTypeEnum.PAYMENT_RECORD_WARN.name().equals(event.getType())) {
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(Long.valueOf(event.getKey()));
                Long sum = paymentActualDetailService.calculatePaidAmount(Collections.singletonList(paymentBaseInfo.getId()));
                if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
                    if (paymentBaseInfo.getApplyPaymentAmount() < sum) {
                        String sb = "付款核销异常:" +
                                "客户" +
                                id2NameService.clientId2NameSingle(paymentBaseInfo.getClientId()) +
                                "下合同为" +
                                paymentBaseInfo.getContractCode() +
                                "的付款编号为" +
                                paymentBaseInfo.getPaymentCode() +
                                "的付款金额异常" +
                                "应付金额为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(paymentBaseInfo.getApplyPaymentAmount())).setScale(2, RoundingMode.HALF_UP) +
                                "实付金额为" +
                                LongUtil.tenThousand2Dollar(String.valueOf(sum)).setScale(2, RoundingMode.HALF_UP) +
                                "请及时确认此核销是否需要订正";
                        DingService.ding(sb, DingGroups.CANG_QIONG);
                    }
                }
            }
        } catch (Exception e) {
            log.info("FinanceWarnTimeoutEventListener error", e);
        }
    }
}
