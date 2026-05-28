package cn.zswltech.mithras.service.mapper.lib.projestablish;

import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-20
 */
public interface ProjEstablishBaseInfoLibMapper extends BaseMapper<ProjEstablishBaseInfoLib> {


    List<ProjEstablishBaseInfoLib> firstCreateVersion(@Param("startDate") LocalDate start,
                                                      @Param("endDate") LocalDate end);

    @Select("SELECT p.* FROM proj_establish_base_info_lib p " +
            "INNER JOIN (SELECT main_id AS id, MAX(version) AS version FROM common_version t1 " +
            "WHERE t1.module = 'PROJ_ESTABLISH' AND t1.version_type = 1 " +
            "GROUP BY main_id) m ON p.origin_id = m.id AND p.version = m.version")
    List<ProjEstablishBaseInfoLib> allNewstEffectVersion();
}
