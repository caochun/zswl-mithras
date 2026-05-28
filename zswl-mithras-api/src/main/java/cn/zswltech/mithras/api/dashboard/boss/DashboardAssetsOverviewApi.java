package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.BalanceOverviewRSP;
import cn.zswltech.mithras.dto.dashboard.boss.AssetsOverviewDetailListRSP;
import cn.zswltech.mithras.dto.dashboard.boss.LoanOverviewRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/14:41
 * @description
 */
@Api(tags = "管理工作台-资产总览接口")
@RequestMapping(path = "/dashboard")
public interface DashboardAssetsOverviewApi {
    @PostMapping(path = "/assets/balance/overview")
    @ApiOperation(value = "资产总览-余额总览")
    R<BalanceOverviewRSP> getBalanceOverview();

    @PostMapping(path = "/assets/loan/overview")
    @ApiOperation(value = "资产总览-投放总览")
    R<LoanOverviewRSP> getLoanOverview();

    @PostMapping(path = "/assets/detail/byprovince")
    @ApiOperation(value = "资产和投放明细-按省份")
    R<List<AssetsOverviewDetailListRSP>> getDetailByProvince();

    @PostMapping(path = "/assets/detail/byprovince/export")
    @ApiOperation(value = "资产和投放明细-按省份-导出")
    void exportDetailByProvince();

    @PostMapping(path = "/assets/detail/byarea")
    @ApiOperation(value = "资产和投放明细-按经济圈")
    R<List<AssetsOverviewDetailListRSP>> getDetailByArea();
}
