package cn.zswltech.mithras.service.controller.dashboard;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardProjectInfoApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.dashboard.DashboardProjectInfoService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;


/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Slf4j
@RestController
public class DashboardProjectInfoController implements DashboardProjectInfoApi {
    @Resource
    private DashboardProjectInfoService dashboardProjectInfoService;

    @Override
    public R<List<DashboardProjectInfoStatisticsRSP>> statisticsList(DashboardProjectInfoStatisticsListREQ req) {
        return R.ok(dashboardProjectInfoService.statisticsList(req));
    }

    @Override
    public R<SettleInThreeMonSumRSP> settleInThreeMonthList(@Valid DashboardProjectInfoSettleInThreeMonthREQ req) {
        return R.ok(dashboardProjectInfoService.listSettleInThreeMonth(req));
    }

    @Override
    public R<OverDueSumRSP> overdueList(@Valid DashboardProjectInfoOverdueREQ req) {
        return R.ok(dashboardProjectInfoService.listOverdue(req));
    }

    @Override
    public R<RentThisMonthSumRSP> rentThisMonthList(@Valid DashboardProjectInfoRentThisMonthREQ req) {
        return R.ok(dashboardProjectInfoService.listRentThisMonth(req));
    }

    @Override
    public R<List<DashboardProjectFinanceStatisticsRSP>> statisticsRentInfoList() {
        try {
            return R.ok(dashboardProjectInfoService.financeStatisticsList());
        } catch (Exception e) {
            log.error("业务工作台-项目视图-项目情况-统计发生未知异常", e);
            throw new MithrasException("系统繁忙，请稍后再试");
        }
    }

    @Override
    public R<Map<String, Object>> listProjectPayNoSettle(@Valid DashboardProjectPayNoSettleREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardProjectPayNoSettleRSP> dashboardProjectPayNoSettleRSPS = dashboardProjectInfoService.listPayNoSettle(req);
        map.put(RECORDS, dashboardProjectPayNoSettleRSPS);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardProjectPayNoSettleRSPS, new DashboardProjectPayNoSettleRSP()));
        } catch (Exception e) {
            log.warn("DashboardProjectInfoController listProjectPayNoSettle count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> listProvision(@Valid DashboardProjectProvisionREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardProjectProvisionRSP> dashboardProjectProvisionRSPS = dashboardProjectInfoService.listProvision(req);
        map.put(RECORDS, dashboardProjectProvisionRSPS);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardProjectProvisionRSPS, new DashboardProjectProvisionRSP()));
        } catch (Exception e) {
            log.warn("DashboardProjectInfoController listProvision count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> listPledge(@Valid DashboardProjectPledgeREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardProjectPledgeRSP> rspList = dashboardProjectInfoService.listPledge(req);
        map.put(RECORDS, rspList);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(rspList, new DashboardProjectPledgeRSP()));
        } catch (Exception e) {
            log.warn("DashboardProjectInfoController listProvision count error ", e);
        }
        return R.ok(map);
    }
}
