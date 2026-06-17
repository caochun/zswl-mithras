package cn.zswltech.mithras.capital.job;

import cn.zswltech.mithras.capital.application.port.ManualWriteOffReleaseBankFlowJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @date 2024/10/12 10:45
 */
@Slf4j
@Component
public class ManualWriteOffReleaseBankFlowJob {

    @Resource
    private ManualWriteOffReleaseBankFlowJobPort manualWriteOffReleaseBankFlowJobPort;

    @XxlJob(value = "manualWriteOffReleaseBankFlow")
    public void releaseBankFlow() {
        manualWriteOffReleaseBankFlowJobPort.releaseBankFlow();
    }
}
