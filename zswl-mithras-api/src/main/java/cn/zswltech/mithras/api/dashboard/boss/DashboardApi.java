package cn.zswltech.mithras.api.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.dashboard.boss.DashboardListRSP;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/5/18
 * @description
 */
@Api(tags = "管理工作台")
@RequestMapping(path = "/dashboard")
public interface DashboardApi {
    @PostMapping(path = "/list")
    R<List<DashboardListRSP>> list();
}
