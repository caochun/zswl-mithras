package cn.zswltech.mithras.workflow.persistence.mapper;

import cn.zswltech.mithras.workflow.persistence.model.FlowQueryExtra;
import cn.zswltech.mithras.workflow.persistence.model.FlowQueryExtraMissing;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yibin
 */
public interface FlowQueryExtraMapper extends BaseMapper<FlowQueryExtra> {
    List<FlowQueryExtraMissing> selectMissingFlow(Page<FlowQueryExtraMissing> page, @Param("flowKeys") List<String> flowKeys);
}
