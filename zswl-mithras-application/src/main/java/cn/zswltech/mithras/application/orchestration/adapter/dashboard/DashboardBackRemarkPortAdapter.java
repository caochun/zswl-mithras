package cn.zswltech.mithras.application.orchestration.adapter.dashboard;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.mithras.dashboard.application.port.DashboardBackRemarkPort;
import cn.zswltech.mithras.workflow.persistence.mapper.flow.ToDoOperateRecordMapper;
import cn.zswltech.mithras.workflow.persistence.model.flow.OperateRecord;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DashboardBackRemarkPortAdapter implements DashboardBackRemarkPort {

    @Resource
    private ToDoOperateRecordMapper toDoOperateRecordMapper;

    @Override
    public Map<String, String> listBackRemarkByProcessInstanceIdsAndHandlerIds(Collection<String> processInstanceIds, Collection<String> handlerIds) {
        if (CollectionUtil.isEmpty(processInstanceIds) || CollectionUtil.isEmpty(handlerIds)) {
            return Collections.emptyMap();
        }
        List<OperateRecord> operateRecordList = toDoOperateRecordMapper.selectList(Wrappers.<OperateRecord>lambdaQuery()
                .in(OperateRecord::getProcessInstanceId, processInstanceIds)
                .in(OperateRecord::getType, ListUtil.of(CommentTypeEnum.BH.name(), CommentTypeEnum.BHFQR.name(), CommentTypeEnum.BHFQR_ZJDW.name()))
                .in(OperateRecord::getHandlerId, handlerIds));
        return operateRecordList.stream()
                .filter(item -> StrUtil.isNotBlank(item.getNote()))
                .collect(Collectors.groupingBy(OperateRecord::getProcessInstanceId,
                        Collectors.mapping(item -> cleanNote(item.getNote()),
                                Collectors.collectingAndThen(Collectors.toList(), notes -> CharSequenceUtil.join("\n", notes)))));
    }

    private String cleanNote(String note) {
        return note.replace("<div>", "").replace("</div>", "").replace("&nbsp;", "\n");
    }
}
