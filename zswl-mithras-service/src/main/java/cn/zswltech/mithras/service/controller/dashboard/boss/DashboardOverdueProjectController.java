package cn.zswltech.mithras.service.controller.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardOverdueProjectApi;
import cn.zswltech.mithras.dto.dashboard.boss.OverdueProjectListRSP;
import cn.zswltech.mithras.service.service.dashboard.boss.DashboardOverdueProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/19:23
 * @description
 */
@Slf4j
@RestController
public class DashboardOverdueProjectController implements DashboardOverdueProjectApi {
    @Resource
    private DashboardOverdueProjectService dashboardOverdueProjectService;

    @Override
    public R<List<OverdueProjectListRSP>> list() {
        return R.ok(dashboardOverdueProjectService.listOverdueProject());
    }
}
