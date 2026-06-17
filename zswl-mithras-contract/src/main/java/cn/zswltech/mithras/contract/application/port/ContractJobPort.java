package cn.zswltech.mithras.contract.application.port;

import com.xxl.job.core.biz.model.ReturnT;

public interface ContractJobPort {

    void tryAutoStartRentJob();

    ReturnT<String> contractStartRentRemindJobHandler(String param);
}
