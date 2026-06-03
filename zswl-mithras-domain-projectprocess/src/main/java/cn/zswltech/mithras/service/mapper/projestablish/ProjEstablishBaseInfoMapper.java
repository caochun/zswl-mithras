package cn.zswltech.mithras.service.mapper.projestablish;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.service.mapper.dto.*;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projlifecycle.mapper.dto.ClientProjLifecycleListParam;
import cn.zswltech.mithras.projlifecycle.mapper.dto.ProjLifecycleListDTO;
import cn.zswltech.mithras.projlifecycle.mapper.dto.ProjLifecycleListSelectParam;
import cn.zswltech.mithras.projlifecycle.mapper.dto.ProjLifecycleStatisticParam;
import cn.zswltech.mithras.projlifecycle.mapper.dto.ProjLifecycleStatisticsDTO;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
public interface ProjEstablishBaseInfoMapper extends CustomBaseMapper<ProjEstablishBaseInfo> {

    Page<ProjEstablishBaseInfo> myList(Page<ProjEstablishBaseInfo> page,
                                       @Param("dto") ProjEstablishListSelectDTO selectDTO);

    Page<ProjLifecycleListDTO> lifecycleList(Page<ProjEstablishBaseInfo> page,
                                             @Param("dto") ProjLifecycleListSelectParam selectDTO);

    Page<ProjLifecycleListDTO> clientLifecycleList(Page<ProjEstablishBaseInfo> page,
                                             @Param("dto") ClientProjLifecycleListParam dto);

    ProjLifecycleStatisticsDTO lifecycleStatistics(@Param("dto") ProjLifecycleStatisticParam statisticParam);

    int clientRelatedProjEstablishCount(Long clientId);

    int clientRelatedProjEstablishLibCount(Long clientId);

    ProjEstablishBaseInfo listProjByAuthRole(Long clientId);

    @Select("select count(id) as `value`, biz_dept_id as `key` from proj_establish_base_info where proj_establish_status = 'TAKE_EFFECT' and create_time >= #{startTime} and create_time <= #{endTime} group by biz_dept_id")
    List<Pair<Long, Long>> countEffectProjectGroupByDept(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
