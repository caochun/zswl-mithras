package cn.zswltech.mithras.projectprocess.mapper.lib.projestablish;

import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePriceLib;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @author zhaozhengkang
 * @description 租赁报价方案表
 * @date 2022-07-20
 */
public interface ProjEstablishLeasePriceLibMapper extends BaseMapper<ProjEstablishLeasePriceLib> {
    @Select("<script>" +
            "SELECT p.* FROM proj_establish_lease_price_lib p " +
            "INNER JOIN (SELECT main_id AS id, MAX(version) AS version FROM common_version t1 " +
            "  WHERE t1.module = 'PROJ_ESTABLISH' " +
            "  AND t1.version_type = 1 " +
            "  GROUP BY main_id) m ON p.proj_establish_id = m.id AND p.version = m.version " +
            "WHERE p.proj_establish_id IN " +
            "       <foreach collection=\"projEstablishIds\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">" +
            "            #{item}" +
            "       </foreach>" +
            "</script>")
    List<ProjEstablishLeasePriceLib> listNewestByProjEstablishIds(@Param("projEstablishIds") Set<Long> projEstablishIds);
}
