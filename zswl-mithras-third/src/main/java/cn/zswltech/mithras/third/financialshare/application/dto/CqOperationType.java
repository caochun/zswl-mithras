package cn.zswltech.mithras.third.financialshare.application.dto;

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
