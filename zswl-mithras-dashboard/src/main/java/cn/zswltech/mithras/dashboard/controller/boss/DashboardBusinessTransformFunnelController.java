package cn.zswltech.mithras.dashboard.controller.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardBusinessTransformFunnelApi;
import cn.zswltech.mithras.dto.dashboard.boss.BusinessTransformFunnelListRSP;
import cn.zswltech.mithras.dashboard.application.boss.DashboardBusinessTransformFunnelService;
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
public class DashboardBusinessTransformFunnelController implements DashboardBusinessTransformFunnelApi {
    @Resource
    private DashboardBusinessTransformFunnelService dashboardBusinessTransformFunnelService;

    @Override
    public R<List<BusinessTransformFunnelListRSP>> list() {
        return R.ok(dashboardBusinessTransformFunnelService.listBusinessTransformFunnel());
    }
}
