package cn.zswltech.mithras.service.mapper.afterlease;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseAssetStrategyREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAssetStrategyRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckRSP;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanBaseLib;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardClientAfterLeaseCheckQuery;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2022/11/8
 * @description
 */
public interface NewAfterLeaseCheckPlanBaseMapper extends CustomBaseMapper<NewAfterLeaseCheckPlanBase> {
    Page<AfterLeaseAssetStrategyRSP> afterLeaseAssetStrategy(Page<AfterLeaseAssetStrategyRSP> page, @Param("dto") AfterLeaseAssetStrategyREQ request);

    Page<DashboardClientAfterLeaseCheckRSP> pageList(Page<DashboardClientAfterLeaseCheckRSP> objectPage, @Param("req") DashboardClientAfterLeaseCheckQuery req);

    Page<AfterLeaseAssetStrategyRSP> afterLeaseAssetStrategys(Page<AfterLeaseAssetStrategyRSP> page, @Param("dto") AfterLeaseAssetStrategyREQ request);

    //获取项目经理名称
    List<AfterLeaseAssetStrategyRSP> getSponsorName();

    //查询全量部门名称
    List<AfterLeaseAssetStrategyRSP> queryAllDeptName();

    //获取五级分类
    List<AfterLeaseAssetStrategyRSP> getClassifyResult();
}
