package cn.zswltech.mithras.third.service.financial.vo;

/**
 * @author yibin
 */
public enum CqOperationType {
    ADD(0), MODIFY(1);

    public final Integer type;

    CqOperationType(Integer type) {
        this.type = type;
    }
}
