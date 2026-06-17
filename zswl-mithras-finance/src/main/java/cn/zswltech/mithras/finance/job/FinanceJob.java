package cn.zswltech.mithras.finance.job;

import cn.zswltech.mithras.finance.application.port.FinanceJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FinanceJob {

    @Resource
    private FinanceJobPort financeJobService;

    /**
     * 维护所有未反核销的收付款
     **/
    @XxlJob("cancelWriteRecordAll")
    public void cancelWriteRecordAll() {
        financeJobService.cancelWriteRecordAll();
    }

    /**
     * 检查是否有上月逾期情况，无则发起
     **/
    @XxlJob("fianceOverdueMaintenance")
    public void fianceOverdueMaintenance() {
        financeJobService.fianceOverdueMaintenance();
    }
}
