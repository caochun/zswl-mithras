package cn.zswltech.mithras.api.dashboard.boss;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author bigbear
 * @date 2024/10/22 11:43
 * @description
 */
@Api(tags = "管理工作台-部门业绩排名接口")
@RequestMapping(path = "/dashboard")
public interface DeptPerformanceSortApi {
}
