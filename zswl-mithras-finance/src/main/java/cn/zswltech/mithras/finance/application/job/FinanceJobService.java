package cn.zswltech.mithras.finance.application.job;

public interface FinanceJobService {

    void calculateProjectProfit(String jobParam);

    void cancelWriteRecordAll();

    void fianceOverdueMaintenance();
}
