package cn.zswltech.mithras.capital.job.port;

public interface CQFinanceJobService {

    void sendWriteOffNotice();

    void fullFlowRecord();

    void fullBRFlowRecord();
}
