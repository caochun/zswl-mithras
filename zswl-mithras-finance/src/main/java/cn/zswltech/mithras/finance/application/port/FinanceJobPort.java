package cn.zswltech.mithras.finance.application.port;

public interface FinanceJobPort {

    void calculateProjectProfit(String jobParam);

    void cancelWriteRecordAll();

    void fianceOverdueMaintenance();
}
