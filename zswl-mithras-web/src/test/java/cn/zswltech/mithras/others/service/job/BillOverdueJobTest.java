package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.BillOverdueJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2025/10/27
 * @description
 */
public class BillOverdueJobTest extends ApplicationTest {
    @Resource
    private BillOverdueJob billOverdueJob;

    @Test
    public void billOverdueDraftGenerateTodoJobTest() {
        billOverdueJob.billOverdueDraftGenerateTodoJob();
    }
}
