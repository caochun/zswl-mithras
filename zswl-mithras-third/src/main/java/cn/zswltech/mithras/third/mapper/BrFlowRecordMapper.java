package cn.zswltech.mithras.third.mapper;
import cn.zswltech.mithras.third.mapper.model.BrFlowRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @description 保融流水表
* @author vico
* @date 2024-06-17
*/
public interface BrFlowRecordMapper extends BaseMapper<BrFlowRecord> {

    int syncCQFlow();

}