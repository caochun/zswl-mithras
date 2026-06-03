package cn.zswltech.mithras.kpi.mapper;

import cn.zswltech.mithras.kpi.mapper.model.PerformanceMainInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author yangxiong
* @description 针对表【performance_main_info(业绩信息主表)】的数据库操作Mapper
* @createDate 2024-07-01 10:17:54
* @Entity cn.zswltech.mithras.kpi.mapper.model.PerformanceMainInfo
*/
public interface PerformanceMainInfoMapper extends BaseMapper<PerformanceMainInfo> {

    List<Long> getDeptHead(@Param("userId") Long userId);
}




