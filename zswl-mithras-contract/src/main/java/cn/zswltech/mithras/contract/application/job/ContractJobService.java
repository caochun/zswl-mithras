package cn.zswltech.mithras.contract.application.job;

import com.xxl.job.core.biz.model.ReturnT;

public interface ContractJobService {

    void tryAutoStartRentJob();

    ReturnT<String> contractStartRentRemindJobHandler(String param);
}
