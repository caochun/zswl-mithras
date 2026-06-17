package cn.zswltech.mithras.application.orchestration.adapter.dashboard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.zswltech.mithras.dashboard.application.port.DashboardProcessExtraPort;
import cn.zswltech.mithras.dashboard.application.port.DashboardProcessExtraSnapshot;
import cn.zswltech.mithras.workflow.persistence.mapper.flow.FlowQueryExtraMapper;
import cn.zswltech.mithras.workflow.persistence.model.flow.BizProcessData;
import cn.zswltech.mithras.workflow.persistence.model.flow.FlowQueryExtra;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DashboardProcessExtraPortAdapter implements DashboardProcessExtraPort {

    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private FlowQueryExtraMapper flowQueryExtraMapper;

    @Override
    public Map<String, DashboardProcessExtraSnapshot> listByProcessInstanceIds(Collection<String> processInstanceIds) {
        if (CollectionUtils.isEmpty(processInstanceIds)) {
            return Collections.emptyMap();
        }
        Map<String, Long> clientProcessIdMap = bizProcessDataService.getBaseMapper().selectList(Wrappers.<BizProcessData>lambdaQuery()
                        .in(BizProcessData::getProcessInstanceId, processInstanceIds))
                .stream()
                .filter(b -> Objects.nonNull(b.getClientId()))
                .collect(Collectors.toMap(BizProcessData::getProcessInstanceId, BizProcessData::getClientId, (k1, k2) -> k1));

        List<FlowQueryExtra> extraList = flowQueryExtraMapper.selectList(Wrappers.<FlowQueryExtra>lambdaQuery()
                .in(FlowQueryExtra::getInstanceId, processInstanceIds));
        Map<String, FlowQueryExtra> extraMap = extraList.stream()
                .collect(Collectors.toMap(FlowQueryExtra::getInstanceId, Function.identity(), (k1, k2) -> k1));
        if (processInstanceIds.size() > 200) {
            logDuplicatedFlowQueryExtra(extraList);
        }

        return processInstanceIds.stream()
                .collect(Collectors.toMap(Function.identity(), processInstanceId -> {
                    DashboardProcessExtraSnapshot snapshot = new DashboardProcessExtraSnapshot();
                    snapshot.setProcessInstanceId(processInstanceId);
                    snapshot.setClientId(clientProcessIdMap.get(processInstanceId));
                    FlowQueryExtra extra = extraMap.get(processInstanceId);
                    if (Objects.nonNull(extra)) {
                        snapshot.setProjName(extra.getProjName());
                        snapshot.setProjCode(extra.getProjCode());
                        snapshot.setContractCode(extra.getContractCode());
                    }
                    return snapshot;
                }, (k1, k2) -> k1));
    }

    private void logDuplicatedFlowQueryExtra(List<FlowQueryExtra> extraList) {
        ExecutorService executorService = ThreadUtil.newSingleExecutor();
        executorService.execute(() -> {
            List<List<FlowQueryExtra>> duplicated = extraList.stream()
                    .collect(Collectors.groupingBy(FlowQueryExtra::getInstanceId))
                    .values()
                    .stream()
                    .filter(a -> a.size() > 1)
                    .collect(Collectors.toList());
            if (CollUtil.isNotEmpty(duplicated)) {
                log.error("流程列表导出全量数据，存在 instanceId 重复，数据=》{}", duplicated);
            }
        });
    }
}
