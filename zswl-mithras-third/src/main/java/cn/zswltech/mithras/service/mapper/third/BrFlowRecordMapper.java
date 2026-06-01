package cn.zswltech.mithras.service.mapper.third;
import cn.zswltech.mithras.service.mapper.model.third.BrFlowRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @description 保融流水表
* @author vico
* @date 2024-06-17
*/
public interface BrFlowRecordMapper extends BaseMapper<BrFlowRecord> {

    int syncCQFlow();

}