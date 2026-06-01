package cn.zswltech.mithras.service.service.third.financial.vo;

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
