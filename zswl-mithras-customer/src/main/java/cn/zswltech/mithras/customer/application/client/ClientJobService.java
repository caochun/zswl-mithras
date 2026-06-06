package cn.zswltech.mithras.customer.application.client;

public interface ClientJobService {

    void clientAuthTypeModify();

    void releaseClient(String jobParam);

    void supplementClientCode(String clientName);
}
