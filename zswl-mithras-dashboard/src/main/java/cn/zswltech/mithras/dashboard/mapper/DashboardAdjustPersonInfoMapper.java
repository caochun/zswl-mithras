package cn.zswltech.mithras.dashboard.mapper;

import cn.zswltech.mithras.dashboard.mapper.model.DashboardAdjustPersonInfo;
import cn.zswltech.mithras.dashboard.mapper.model.DashboardAdjustPersonLatestQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 工作台-人力调整明细表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-07-29
 */
public interface DashboardAdjustPersonInfoMapper extends BaseMapper<DashboardAdjustPersonInfo> {

    List<DashboardAdjustPersonInfo> getLatest(DashboardAdjustPersonLatestQuery query);
}
