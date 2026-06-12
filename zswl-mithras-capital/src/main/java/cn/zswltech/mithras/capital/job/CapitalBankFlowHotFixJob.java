package cn.zswltech.mithras.capital.job;

import cn.zswltech.mithras.capital.job.port.CapitalBankFlowHotFixJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @date 2024/10/28 17:15
 */
@Slf4j
@Component
public class CapitalBankFlowHotFixJob {

    @Resource
    private CapitalBankFlowHotFixJobService capitalBankFlowHotFixJobService;

    @XxlJob("financeFlowRecordJob")
    public void financeFlowRecordJob() {
        capitalBankFlowHotFixJobService.financeFlowRecordJob();
    }
}
