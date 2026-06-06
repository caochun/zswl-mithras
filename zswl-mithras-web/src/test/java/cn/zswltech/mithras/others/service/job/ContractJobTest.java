package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.job.ContractJob;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/14
 * @description
 */
public class ContractJobTest extends ApplicationTest {
    @Resource
    private ContractJob contractJob;

    @Test
    public void autoStartRentJobTest() {
        contractJob.tryAutoStartRentJob();
    }

    @Test
    public void contractStartRentRemindJobHandlerTest() {
        ReturnT<String> result = contractJob.contractStartRentRemindJobHandler(null);
        System.out.println("执行结果: " + result);
    }
}
