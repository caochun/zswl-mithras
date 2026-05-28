package cn.zswltech.mithras.service.mapper.kpi;

import cn.zswltech.mithras.service.mapper.model.kpi.EclExecuteRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 资产减值记录表
* @author vico
* @date 2025-09-28
*/
public interface EclExecuteRecordMapper extends BaseMapper<EclExecuteRecord> {

    List<EclExecuteRecord> getLastList(@Param("contractCodes") List<String> contractCodes);

}