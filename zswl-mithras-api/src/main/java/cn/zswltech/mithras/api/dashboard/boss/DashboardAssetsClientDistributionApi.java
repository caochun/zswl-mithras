package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionAssetsIndustryListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionClientDepartmentListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.DistributionClientStatisticsListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/16:29
 * @description
 */
@Api(tags = "管理工作台-资产及客户分布")
@RequestMapping(path = "/dashboard")
public interface DashboardAssetsClientDistributionApi {

    @PostMapping(path = "/distribution/assetsindustry")
    @ApiOperation(value = "资产行业分布")
    R<List<DistributionAssetsIndustryListRSP>> getDistributionAssetsIndustryList();

    @PostMapping(path = "/distribution/clientdepartment")
    @ApiOperation(value = "资产客户部门分布")
    R<List<DistributionClientDepartmentListRSP>> getDistributionClientDepartmentList();

    @PostMapping(path = "/distribution/clientstatistics")
    @ApiOperation(value = "资产客户统计")
    R<List<DistributionClientStatisticsListRSP>> getDistributionClientStatisticsList();
}
