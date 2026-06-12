package cn.zswltech.mithras.capital.job;

import cn.zswltech.mithras.capital.job.service.FinanceAutoWriteOffJobService;
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
    private FinanceAutoWriteOffJobService financeAutoWriteOffJobService;

    @XxlJob(value = "financeFlowAutoWriteOffJob")
    public void financeFlowAutoWriteOffJob() {
        financeAutoWriteOffJobService.financeFlowAutoWriteOff();
    }
}
