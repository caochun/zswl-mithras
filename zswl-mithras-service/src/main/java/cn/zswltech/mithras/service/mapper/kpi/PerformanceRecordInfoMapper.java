package cn.zswltech.mithras.service.mapper.kpi;

import cn.zswltech.mithras.service.mapper.model.kpi.PerformanceRecordInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.dto.PerformanceTargetQuery;
import cn.zswltech.mithras.service.mapper.model.kpi.dto.PerformanceTargetResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author yangxiong
* @description 针对表【performance_record_info(业绩信息详情表)】的数据库操作Mapper
* @createDate 2024-06-24 14:54:12
* @Entity cn.zswltech.mithras.service.mapper.model.kpi.PerformanceRecordInfo
*/
public interface PerformanceRecordInfoMapper extends BaseMapper<PerformanceRecordInfo> {

    List<PerformanceTargetResult> queryTargetAmount(PerformanceTargetQuery query);

}




