package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.collection.job.CollectionJob;
import cn.zswltech.mithras.fund.job.FundFinancingJob;
import cn.zswltech.mithras.fund.job.FundOrganizationJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
public class CollectionJobTest extends ApplicationTest {
    @Resource
    private CollectionJob collectionJob;

    @Resource
    private FundFinancingJob fundFinancingJob;
    @Resource
    private FundOrganizationJob fundOrganizationJob;

    @Test
    public void penaltyInterestJobHandlerTest() {
        fundFinancingJob.autoAdjustRate();
    }

    @Test
    public void fundOrganizationJobTest() {
        fundOrganizationJob.FundOrganizationCode();
    }


}
