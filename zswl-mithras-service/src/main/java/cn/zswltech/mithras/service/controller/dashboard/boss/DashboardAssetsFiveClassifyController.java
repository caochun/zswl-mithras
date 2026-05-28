package cn.zswltech.mithras.service.controller.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.DashboardAssetsFiveClassifyApi;
import cn.zswltech.mithras.dto.dashboard.boss.AssetsFiveClassifyListRSP;
import cn.zswltech.mithras.service.service.dashboard.boss.DashboardAssetsFiveClassifyService;
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
public class DashboardAssetsFiveClassifyController implements DashboardAssetsFiveClassifyApi {

    @Resource
    private DashboardAssetsFiveClassifyService dashboardAssetsFiveClassifyService;

    @Override
    public R<List<AssetsFiveClassifyListRSP>> list() {
        return R.ok(dashboardAssetsFiveClassifyService.list());
    }
}
