package cn.zswltech.mithras.application.orchestration.adapter.dashboard;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.flow.core.dao.NodeBackRecordMapper;
import cn.zswltech.flow.core.dao.OperateRecordMapper;
import cn.zswltech.flow.core.domain.entity.NodeBackRecord;
import cn.zswltech.flow.core.domain.entity.OperateRecord;
import cn.zswltech.mithras.dashboard.application.port.DashboardNodeBackRecordSnapshot;
import cn.zswltech.mithras.dashboard.application.port.DashboardOperateRecordPort;
import cn.zswltech.mithras.dashboard.application.port.DashboardOperateRecordSnapshot;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DashboardOperateRecordPortAdapter implements DashboardOperateRecordPort {

    @Resource
    private OperateRecordMapper operateRecordMapper;
    @Resource
    private NodeBackRecordMapper nodeBackRecordMapper;

    @Override
    public List<DashboardOperateRecordSnapshot> listOperateRecords(Collection<String> processInstanceIds) {
        if (CollectionUtil.isEmpty(processInstanceIds)) {
            return Collections.emptyList();
        }
        Example example = new Example(OperateRecord.class);
        example.createCriteria().andIn("processInstanceId", processInstanceIds);
        return operateRecordMapper.selectByCondition(example).stream()
                .map(this::toOperateRecordSnapshot)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardNodeBackRecordSnapshot> listNodeBackRecords(Collection<String> processInstanceIds) {
        if (CollectionUtil.isEmpty(processInstanceIds)) {
            return Collections.emptyList();
        }
        Example example = new Example(NodeBackRecord.class);
        example.createCriteria().andIn("processInstanceId", processInstanceIds);
        example.orderBy("gmtCreate").desc();
        return nodeBackRecordMapper.selectByCondition(example).stream()
                .map(this::toNodeBackRecordSnapshot)
                .collect(Collectors.toList());
    }

    private DashboardOperateRecordSnapshot toOperateRecordSnapshot(OperateRecord record) {
        DashboardOperateRecordSnapshot snapshot = new DashboardOperateRecordSnapshot();
        snapshot.setProcessDefinitionId(record.getProcessDefinitionId());
        snapshot.setProcessInstanceId(record.getProcessInstanceId());
        snapshot.setType(record.getType());
        snapshot.setNote(record.getNote());
        snapshot.setHandlerId(record.getHandlerId());
        snapshot.setTaskActivityId(record.getTaskActivityId());
        snapshot.setOperateTime(record.getGmtCreate());
        return snapshot;
    }

    private DashboardNodeBackRecordSnapshot toNodeBackRecordSnapshot(NodeBackRecord record) {
        DashboardNodeBackRecordSnapshot snapshot = new DashboardNodeBackRecordSnapshot();
        snapshot.setProcessInstanceId(record.getProcessInstanceId());
        snapshot.setJumpToSourceFlag(record.getJumpToSourceFlag());
        return snapshot;
    }
}
