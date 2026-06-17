package cn.zswltech.mithras.finance.monthly.application.port;

public interface FundsDailyCostJobPort {

    void fundsDailyCostMainFinishJob(String param);

    void fundsDailyCostInit(String param);
}
