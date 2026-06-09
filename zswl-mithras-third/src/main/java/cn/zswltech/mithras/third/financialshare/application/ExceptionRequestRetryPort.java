package cn.zswltech.mithras.third.financialshare.application;

public interface ExceptionRequestRetryPort {
    void retryCollection(String reqData);

    void retryPayment(String reqData);
}
