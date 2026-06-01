package cn.zswltech.mithras.service.mapper.kpi;

import cn.zswltech.mithras.service.mapper.dto.kpi.EclExecuteRecordParam;
import cn.zswltech.mithras.service.mapper.model.kpi.EclExecuteRecordLib;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 资产减值记录表
* @author vico
* @date 2025-09-28
*/
public interface EclExecuteRecordLibMapper extends BaseMapper<EclExecuteRecordLib> {
    List<EclExecuteRecordLib> listCompare(@Param("param") EclExecuteRecordParam param);

}