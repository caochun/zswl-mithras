package cn.zswltech.mithras.dashboard.interfaces;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardPerformanceApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dashboard.application.DashboardPerformanceApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/6/25/19:41
 * @description
 */
@Slf4j
@RestController
public class DashboardPerformanceController implements DashboardPerformanceApi {

    @Resource
    private DashboardPerformanceApplicationService performanceService;

    @Override
    public R<DeptPerformanceRSP> deptPerformance() {
        try {
            return R.ok(performanceService.deptPerformance());
        } catch (InterruptedException ex) {
            log.error("业务工作台-我的业绩-错误", ex);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("业务工作台-我的业绩-错误", e);
        }
        return R.fail("业务工作台-我的业绩-数据异常，请稍后重试");
    }

    @Override
    public R<List<PersonalPerformanceRSP>> personalPerformance() {
        return R.ok(performanceService.personalPerformance());
    }

    @Override
    public R<KpiDeptShipSortPerformanceRSP> deptShipSortPerformance() {
        return R.ok(performanceService.deptShipSortPerformance());
    }

    @Override
    public R<List<DeptInSortPerformanceRSP>> deptInSortPerformance() {
        return R.ok(performanceService.deptInSortPerformance());
    }
}
