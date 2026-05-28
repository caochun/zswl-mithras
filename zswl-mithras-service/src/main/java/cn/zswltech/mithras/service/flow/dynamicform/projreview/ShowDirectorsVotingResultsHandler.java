package cn.zswltech.mithras.service.flow.dynamicform.projreview;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.domain.resp.VoteResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.VoteFormRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.util.StreamUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表单处理器
 *
 * @author wangchuanhao
 * @date 2022/8/8 11:44 AM
 */
@Component
public class ShowDirectorsVotingResultsHandler implements DynamicFormHandler {

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        // 纯展示 不做处理
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        VoteFormRSP formRSP = new VoteFormRSP();
        List<VoteFormRSP.VoteRSP> voteRSPList = processApiService.queryVoteResult(rsp.getProcessInstanceId(), "userTask_directorVote")
                .stream()
                // 一人一票 去重
                .sorted(Comparator.comparing(VoteResp::getVoteTime).reversed())
                .filter(StreamUtil.distinctByKey(VoteResp::getHandlerId))

                .map(r -> VoteFormRSP.VoteRSP.builder()
                        .handlerId(Optional.ofNullable(r.getHandlerId()).map(Long::valueOf).orElse(null))
                        .message(r.getMessage())
                        .voteTime(LocalDateTimeUtil.of(r.getVoteTime()))
                        .type(r.getType())
                        .typeName(r.getTypeName())
                        .taskId(r.getTaskId())
                        .build())
                .collect(Collectors.toList());
        // 填充名字
        if (CollectionUtils.isNotEmpty(voteRSPList)) {
            Map<Long, String> userNameMap = id2NameService.sysUserId2Name(voteRSPList.stream().map(VoteFormRSP.VoteRSP::getHandlerId).filter(Objects::nonNull).collect(Collectors.toSet()));
            voteRSPList.forEach(e -> e.setHandlerName(userNameMap.get(e.getHandlerId())));
        }
        formRSP.setVoteRSPList(voteRSPList);
        rsp.getDynamicFormData().put(getType().name(), formRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.projReview_showDirectorsVotingResults;
    }

}
