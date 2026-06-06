package cn.zswltech.mithras.basedata.application.job;

import com.xxl.job.core.biz.model.ReturnT;

public interface BaseDataJobService {

    void generateExchangeRateTodo(String jobParam);

    ReturnT<String> specialDataRemindJob();

    ReturnT<String> lprRemindJob();
}
