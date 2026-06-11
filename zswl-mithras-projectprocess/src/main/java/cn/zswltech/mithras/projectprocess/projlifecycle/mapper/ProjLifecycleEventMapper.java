package cn.zswltech.mithras.projectprocess.projlifecycle.mapper;

import cn.zswltech.mithras.dto.projlifecycle.ProjectLifecycleListREQ;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.LifecycleProjDO;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.projlifecycle.model.ProjLifecycleListDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @create: 2022-10-26
 **/
public interface ProjLifecycleEventMapper extends BaseMapper<ProjLifecycleEvent> {

    void insertList(@Param("lists") List<ProjLifecycleEvent> lists);

    Page<ProjLifecycleListDO> mainList(Page<ProjLifecycleListDO> page, @Param("req") ProjectLifecycleListREQ req);

    LifecycleProjDO getProjSettle(@Param(value = "reviewId") Long reviewId);
}
