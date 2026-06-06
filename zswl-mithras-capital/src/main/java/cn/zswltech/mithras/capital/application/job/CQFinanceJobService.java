package cn.zswltech.mithras.capital.application.job;

public interface CQFinanceJobService {

    void sendWriteOffNotice();

    void fullFlowRecord();

    void fullBRFlowRecord();
}
