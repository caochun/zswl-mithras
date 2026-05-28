package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.MonthCollectStatisticsListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/22 11:41
 * @description
 */
@Api(tags = "管理工作台-业务月度收入接口")
@RequestMapping(path = "/dashboard")
public interface BusinessMonthCollectApi {

    @PostMapping(path = "/monthcollect/statistics")
    @ApiOperation(value = "业务月度收入统计")
    R<List<MonthCollectStatisticsListRSP>> getMonthCollectStatisticsList(@RequestBody @Valid MonthCollectStatisticsListREQ req);
}
