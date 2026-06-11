package cn.zswltech.mithras.others.kpi;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.kpi.model.KpiProjectDistribution;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
public class KpiProjectDistributionTest extends ApplicationTest {
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;

    @Test
    public void createTest() {
        kpiProjectDistributionService.create(2278L, true);
    }

    @Test
    public void importHistoryData() {
        kpiProjectDistributionService.importHistoryData(FileUtil.getInputStream("/Users/mockorz/Documents/2023年一季度项目分配.xlsx"));
    }

    @Test
    public void ttt() {
        kpiProjectDistributionService.remove(Wrappers.<KpiProjectDistribution>lambdaQuery().eq(KpiProjectDistribution::getDistributionStatus, 1));
    }
}
