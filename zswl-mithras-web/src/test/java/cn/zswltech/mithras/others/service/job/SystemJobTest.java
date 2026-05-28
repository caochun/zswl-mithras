package cn.zswltech.mithras.others.service.job;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.SystemJob;
import com.xxl.job.core.biz.model.ReturnT;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/27
 * @description
 */
public class SystemJobTest extends ApplicationTest {
    @Resource
    private SystemJob systemJob;

    @Test
    public void createBudgetFinancialFlowTest() {
        systemJob.createBudgetFinancialFlow();
    }

//    @Test
//    public void initHistoryProjectClientMaterialTest() {
//        systemJob.initHistoryProjectClientMaterial();
//    }

    @Test
    public void initFtpInterestDataTest () throws Exception {
        systemJob.initFtpInterestData();
    }

    @Test
    public void refreshDataDictLocalCacheTest() {
        ReturnT<String> result = systemJob.refreshDataDictLocalCache();
        System.out.println("执行结果: " + JSONUtil.toJsonStr(result));
    }

    @Test
    public void createSettleContractLibTest() {
        systemJob.createSettleContractLib();
    }
}
