package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.BusinessTransformFunnelListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/17:17
 * @description
 */
@Api(tags = "管理工作台-业务转化漏斗接口")
@RequestMapping(path = "/dashboard")
public interface DashboardBusinessTransformFunnelApi {

    @PostMapping(path = "/conversion/statistics")
    @ApiOperation(value = "各阶段转化数据")
    R<List<BusinessTransformFunnelListRSP>> list();
}
