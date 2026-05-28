package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.OverdueProjectListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/15/18:00
 * @description
 */
@Api(tags = "管理工作台-逾期项目信息接口")
@RequestMapping(path = "/dashboard")
public interface DashboardOverdueProjectApi {

    @PostMapping(path = "/overdueproject/list")
    @ApiOperation(value = "逾期项目信息")
    R<List<OverdueProjectListRSP>> list();
 }
