package cn.zswltech.mithras.workflow.flow;

import cn.zswltech.mithras.workflow.persistence.mapper.flow.FlowTaskDurationMapper;
import cn.zswltech.mithras.workflow.persistence.model.flow.FlowTaskDuration;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author luyi
 */
@Service
public class FlowTaskDurationService extends ServiceImpl<FlowTaskDurationMapper, FlowTaskDuration> {

    public FlowTaskDuration getByTaskId(String taskId) {
        return this.getOne(Wrappers.<FlowTaskDuration>lambdaQuery()
                .eq(FlowTaskDuration::getTaskId, taskId)
        );
    }

    public void removeByTaskIds(Collection<String> taskIds) {
        this.remove(Wrappers.<FlowTaskDuration>lambdaQuery()
                .in(FlowTaskDuration::getTaskId, taskIds)
        );
    }
}
