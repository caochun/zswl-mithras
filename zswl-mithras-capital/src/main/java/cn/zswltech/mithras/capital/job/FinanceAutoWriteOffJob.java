package cn.zswltech.mithras.capital.job;

import cn.zswltech.mithras.capital.application.port.FinanceAutoWriteOffJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yangxiong
 * @date 2024/6/5/16:37
 * @description
 */
@Component
@Slf4j
public class FinanceAutoWriteOffJob {

    @Resource
    private FinanceAutoWriteOffJobPort financeAutoWriteOffJobPort;

    @XxlJob(value = "financeFlowAutoWriteOffJob")
    public void financeFlowAutoWriteOffJob() {
        financeAutoWriteOffJobPort.financeFlowAutoWriteOff();
    }
}
