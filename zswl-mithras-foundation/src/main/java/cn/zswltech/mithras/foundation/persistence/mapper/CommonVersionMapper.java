package cn.zswltech.mithras.foundation.persistence.mapper;

import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author luyi
 */
public interface CommonVersionMapper extends BaseMapper<CommonVersion> {
    @Select("SELECT a.main_id,a.version,a.create_time from common_version a " +
            " INNER JOIN (SELECT main_id, module, MIN(version) AS version, MIN(create_time) AS create_time FROM common_version " +
            "  WHERE module = #{module} AND version_type = 1 GROUP BY main_id ) m " +
            " ON m.main_id = a.main_id AND m.version = a.version AND m.create_time >= #{createTimeStart} AND a.module = m.module")
    List<CommonVersion> firstCreateVersion(@Param("module") String module,
                                           @Param("createTimeStart") LocalDateTime createTimeStart);
}
