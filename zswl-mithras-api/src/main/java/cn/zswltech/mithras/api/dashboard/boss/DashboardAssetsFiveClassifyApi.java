package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.AssetsFiveClassifyListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/17:47
 * @description
 */
@Api(tags = "管理工作台-资产五级分类接口")
@RequestMapping(path = "/dashboard")
public interface DashboardAssetsFiveClassifyApi {

    @PostMapping(path = "/assetfiveclassify/statistics")
    @ApiOperation(value = "资产五级分类统计表")
    R<List<AssetsFiveClassifyListRSP>> list();
}
