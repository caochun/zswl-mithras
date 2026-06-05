package cn.zswltech.mithras.service.controller.dashboard;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardClientOverviewApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.service.dashboard.DashboardClientOverviewService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;

/**
 * @ClassName DashboardClientOverviewController
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/6/18 4:43 下午
 * @Version 1.0
 **/
@RestController
@Slf4j
public class DashboardClientOverviewController implements DashboardClientOverviewApi {

    @Resource
    private DashboardClientOverviewService dashboardClientOverviewService;


    private static final Set<String> OVERDUE_SET;
    static {
        OVERDUE_SET = new HashSet<>();
        OVERDUE_SET.add("overdueAmount");
        OVERDUE_SET.add("penaltyInterestRate");
        OVERDUE_SET.add("penaltyInterestAmount");
        OVERDUE_SET.add("penaltyInterestReductionAmount");

    }


    @Override
    public R<List<DashboardClientOverviewStatisticsRSP>> statisticsList() {
        try {
            return R.ok(dashboardClientOverviewService.statisticsList());
        } catch (Exception e) {
            log.error("业务工作台-客户视图-客户一览-统计错误", e);
        }
        return R.fail("客户视图异常，请稍后重试");
    }

    @Override
    public R<Map<String, Object>> allPageList(@Valid DashboardClientOverviewAllREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardClientOverviewAllRSP> dashboardClientOverviewAllRSPPageR = dashboardClientOverviewService.allPageList(req);
        //DashboardClientOverviewAllREQ allREQ = new DashboardClientOverviewAllREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardClientOverviewAllRSPPageR);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardClientOverviewService.allPageList(req).getList(), new DashboardClientOverviewAllRSP()));
        } catch (Exception e) {
            log.warn("DashboardClientOverviewController allPageList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> survivalPageList(@Valid DashboardClientOverviewSurvivalREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardClientOverviewSurvivalRSP> dashboardClientOverviewSurvivalRSPPageR = dashboardClientOverviewService.survivalPageList(req);
        //DashboardClientOverviewSurvivalREQ allREQ = new DashboardClientOverviewSurvivalREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardClientOverviewSurvivalRSPPageR);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardClientOverviewService.survivalPageList(req).getList(), new DashboardClientOverviewSurvivalRSP()));
        } catch (Exception e) {
            log.warn("DashboardClientOverviewController survivalPageList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> settleInThreeMonthPageList(@Valid DashboardClientOverviewSettleInThreeMonthREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardClientOverviewSettleInThreeMonthRSP> dashboardClientOverviewSettleInThreeMonthRSPPageR = dashboardClientOverviewService.settleInThreeMonthPageList(req);
        //DashboardClientOverviewSettleInThreeMonthREQ allREQ = new DashboardClientOverviewSettleInThreeMonthREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardClientOverviewSettleInThreeMonthRSPPageR);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardClientOverviewService.settleInThreeMonthPageList(req).getList(), new DashboardClientOverviewSettleInThreeMonthRSP()));
        } catch (Exception e) {
            log.warn("DashboardClientOverviewController settleInThreeMonthPageList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> overduePageList(@Valid DashboardClientOverviewOverdueREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardClientOverviewOverdueRSP> dashboardClientOverviewOverdueRSPPageR = dashboardClientOverviewService.overduePageList(req);
        //DashboardClientOverviewOverdueREQ allREQ = new DashboardClientOverviewOverdueREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardClientOverviewOverdueRSPPageR);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countString(dashboardClientOverviewService.overduePageList(req).getList(), OVERDUE_SET, new DashboardClientOverviewOverdueRSP()));
        } catch (Exception e) {
            log.warn("DashboardClientOverviewController overduePageList count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> settledPageList(@Valid DashboardClientOverviewSettledREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardClientOverviewSettledRSP> dashboardClientOverviewSettledRSPPageR = dashboardClientOverviewService.settledPageList(req);
        //DashboardClientOverviewSettledREQ allREQ = new DashboardClientOverviewSettledREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardClientOverviewSettledRSPPageR);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardClientOverviewService.settledPageList(req).getList(), new DashboardClientOverviewSettledRSP()));
        } catch (Exception e) {
            log.warn("DashboardClientOverviewController settledPageList count error ", e);
        }
        return R.ok(map);
    }
}
