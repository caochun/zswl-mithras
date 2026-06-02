package cn.zswltech.mithras.riskcontrol.eventbus;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/15 11:33
 */
public enum OperationEnum {
    /**
     * 付款核销
     */
    PAYMENT_VERIFICATION,
    /**
     * 收款核销
     */
    COLLECTION_VERIFICATION,
    /**
     * 合同生效
     */
    CONTRACT_EFFECTIVE;
}
