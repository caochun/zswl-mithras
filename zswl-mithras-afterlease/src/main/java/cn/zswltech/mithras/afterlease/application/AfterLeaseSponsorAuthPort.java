package cn.zswltech.mithras.afterlease.application;

public interface AfterLeaseSponsorAuthPort {
    void check(String businessModule, Long businessId);
}
