package cn.zswltech.mithras.workflow.job;

import cn.zswltech.mithras.workflow.process.prepare.job.BillOverdueJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/10/27
 * @description
 */
@Slf4j
@Component
public class BillOverdueJob {

    @Resource
    private BillOverdueJobService billOverdueJobService;

    @XxlJob("billOverdueDraftGenerateTodoJob")
    public void billOverdueDraftGenerateTodoJob() {
        billOverdueJobService.billOverdueDraftGenerateTodoJob();
    }
}
