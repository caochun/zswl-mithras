package cn.zswltech.mithras.api.managereport;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.managereport.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@Api(tags = "管理报表相关接口")
public interface ManageReportApi {
    @ApiOperation("业务运行分析-明细列表")
    @PostMapping(path = "/managereport/yewuyunyingfenxi/detail")
    R<PageR<YeWuYunXingFenXiDetailRSP>> queryYeWuYunXingFenXiDetailWithPage(@RequestBody @Valid YeWuYunXingFenXiDetailREQ req);

    @ApiOperation("业务运行分析-统计列表")
    @PostMapping(path = "/managereport/yewuyunyingfenxi/statistic")
    R<List<YeWuYunXingFenXiStatisticRSP>> statisticYeWuYunXingFenXi(@RequestBody @Valid YeWuYunXingFenXiStatisticREQ req);

    @ApiOperation("运营待办-明细列表")
    @PostMapping(path = "/managereport/yunyingdaiban/detail")
    R<List<YunYingDaiBanDetailRSP>> queryYunYingDaiBanDetail(@RequestBody @Valid YunYingDaiBanDetailREQ req);

    @ApiOperation("运营待办-统计列表")
    @PostMapping(path = "/managereport/yunyingdaiban/statistic")
    R<List<YunYingDaiBanStatisticRSP>> statisticYunYingDaiBan(@RequestBody @Valid YunYingDaiBanStatisticREQ req);

    @ApiOperation("合同时效监控表-明细列表")
    @PostMapping(path = "/managereport/hetongshixiao/detail")
    R<List<HeTongShiXiaoDetailRSP>> queryHeTongShiXiaoDetail(@RequestBody @Valid HeTongShiXiaoDetailREQ req);

    @ApiOperation("合同时效监控表-统计列表")
    @PostMapping(path = "/managereport/hetongshixiao/statistic")
    R<List<HeTongShiXiaoStatisticRSP>> statisticHeTongShiXiao(@RequestBody @Valid HeTongShiXiaoStatisticREQ req);
}
