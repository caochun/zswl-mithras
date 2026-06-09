package cn.zswltech.mithras.contract.application.process.prepare;

public interface ContractBizProcessDataPort {

    void recordBizData(String processInstanceId, Long clientId);
}
