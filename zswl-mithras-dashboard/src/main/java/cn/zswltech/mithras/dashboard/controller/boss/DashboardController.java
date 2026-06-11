package cn.zswltech.mithras.dashboard.controller.boss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardApi;
import cn.zswltech.mithras.dto.dashboard.boss.DashboardListRSP;
import cn.zswltech.mithras.dashboard.model.DashboardAuthorityConfig;
import cn.zswltech.mithras.dashboard.model.DashboardConfig;
import cn.zswltech.mithras.dashboard.application.boss.DashboardAuthorityConfigService;
import cn.zswltech.mithras.dashboard.application.boss.DashboardConfigService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/5/18
 * @description
 */
@RestController
public class DashboardController implements DashboardApi {
    @Resource
    private DashboardConfigService dashboardConfigService;
    @Resource
    private DashboardAuthorityConfigService dashboardAuthorityConfigService;

    @Override
    public R<List<DashboardListRSP>> list() {
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        // 查询当前用户拥有的看板
        List<DashboardAuthorityConfig> dashboardAuthorityConfigList = dashboardAuthorityConfigService.listByUserId(currentUserId);
        if (CollectionUtil.isEmpty(dashboardAuthorityConfigList)) {
            return R.ok(Collections.emptyList());
        }
        // 查询看板具体信息
        Set<String> keys = dashboardAuthorityConfigList.stream().map(DashboardAuthorityConfig::getDashboardKey).collect(Collectors.toSet());
        List<DashboardConfig> dashboardConfigList = dashboardConfigService.listByKeys(keys);
        if (CollectionUtil.isEmpty(dashboardConfigList)) {
            return R.ok(Collections.emptyList());
        }
        // 排序
        dashboardConfigList.sort(Comparator.comparing(DashboardConfig::getOrderNum));
        // 封装返回参数
        String dataDate = LocalDateTimeUtil.format(LocalDate.now().minusDays(1), DatePattern.NORM_DATE_PATTERN);
        List<DashboardListRSP> result = dashboardConfigList.stream().map(e -> {
            DashboardListRSP rsp = new DashboardListRSP();
            rsp.setDashboardKey(e.getDashboardKey());
            rsp.setDashboardDisplay(e.getDashboardDisplay());
            rsp.setDataDate(dataDate);
            return rsp;
        }).collect(Collectors.toList());
        return R.ok(result);
    }
}
