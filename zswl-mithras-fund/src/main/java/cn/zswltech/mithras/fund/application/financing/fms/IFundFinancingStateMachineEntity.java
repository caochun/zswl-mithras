package cn.zswltech.mithras.fund.application.financing.fms;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/9/5 14:15
 */
public interface IFundFinancingStateMachineEntity {

    Long getId();

    void setId(Long id);

    String getProcessStatus();

    void setProcessStatus(String processState);

    String getRecordStatus();

    void setRecordStatus(String recordStatus);

    String getSecondRecordStatus();

    void setSecondRecordStatus(String recordStatus);

    String getProcessStatusFieldName();

    String getRecordStatusFieldName();

    String getSecondRecordStatusFieldName();

}
