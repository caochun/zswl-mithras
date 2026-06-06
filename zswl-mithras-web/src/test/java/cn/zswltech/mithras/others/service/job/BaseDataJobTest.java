package cn.zswltech.mithras.others.service.job;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.basedata.job.BaseDataJob;
import cn.zswltech.mithras.others.service.ApplicationTest;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/12/5
 * @description
 */
public class BaseDataJobTest extends ApplicationTest {
    @Resource
    private BaseDataJob baseDataJob;

    @Test
    public void exchangeRateTodoJobTest() {
        baseDataJob.generateExchangeRateTodo();
    }

    @Test
    public void specialDataRemindJobTest() {
        ReturnT<String> returnT = baseDataJob.specialDataRemindJob();
        System.out.println("任务执行结果: " + JSONUtil.toJsonStr(returnT));
    }

    @Test
    public void lprRemindJobTest() {
        ReturnT<String> returnT = baseDataJob.lprRemindJob();
        System.out.println("任务执行结果: " + JSONUtil.toJsonStr(returnT));
    }
}
