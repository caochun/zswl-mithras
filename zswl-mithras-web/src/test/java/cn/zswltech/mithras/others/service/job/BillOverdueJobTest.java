package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.workflow.application.process.prepare.job.BillOverdueJobService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/10/27
 * @description
 */
public class BillOverdueJobTest extends ApplicationTest {
    @Resource
    private BillOverdueJobService billOverdueJobService;

    @Test
    public void billOverdueDraftGenerateTodoJobTest() {
        billOverdueJobService.billOverdueDraftGenerateTodoJob();
    }
}
