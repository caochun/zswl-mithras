package cn.zswltech.mithras.kpi.mapper;

import cn.zswltech.mithras.kpi.dto.persistence.EclExecuteRecordParam;
import cn.zswltech.mithras.kpi.model.EclExecuteRecordLib;
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