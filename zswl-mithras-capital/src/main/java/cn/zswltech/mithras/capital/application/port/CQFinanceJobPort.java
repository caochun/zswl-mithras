package cn.zswltech.mithras.capital.application.port;

public interface CQFinanceJobPort {

    void sendWriteOffNotice();

    void fullFlowRecord();

    void fullBRFlowRecord();
}
