package cn.zswltech.mithras.workflow.mapper;

import cn.zswltech.mithras.workflow.model.ProcAttentionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 流程关注记录
 *
 * @author wangchuanhao
 * @date 2022/12/9 12:42 PM
 */
public interface ProcAttentionRecordMapper extends BaseMapper<ProcAttentionRecord> {

    /**
     * 记录数据
     * @param procAttentionRecord
     * @return
     */
    int insertOrUpdate(@Param("record") ProcAttentionRecord procAttentionRecord);

}
