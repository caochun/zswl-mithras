package cn.zswltech.mithras.afterlease.enums;

public enum AfterLeaseProcessEndResult {
    PASS,
    REJECT,
    CANCEL,
    OTHER;

    public boolean pass() {
        return PASS.equals(this);
    }

    public boolean reject() {
        return REJECT.equals(this);
    }

    public boolean cancel() {
        return CANCEL.equals(this);
    }
}
