package cn.zswltech.mithras.workflow.flow.util;

import cn.zswltech.flow.core.domain.req.SetTaskApproverReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.workflow.flow.constant.FlowConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/31
 * @description
 */
public class FlowUtil {
    /**
     * 校验流程节点是否处于发起人节点
     *
     * @param processResp 流程节点数据
     */
    public static boolean isStartUserNode(ProcessResp processResp) {
//        String startUserTask = Arrays.stream(processResp.getCurTaskActivityIds().split(","))
//                .filter(FlowConstants.START_USER_TASK::equals)
//                .findFirst()
//                .orElse(null);
//        return Objects.nonNull(startUserTask);
        return isSpecificNode(processResp, FlowConstants.START_USER_TASK);
    }

    public static boolean isSpecificNode(ProcessResp processResp, String activityId) {
        String currentUserTask = Arrays.stream(processResp.getCurTaskActivityIds().split(","))
                .filter(activityId::equals)
                .findFirst()
                .orElse(null);
        return Objects.nonNull(currentUserTask);
    }

    /**
     * 构建设置审批流用户的参数
     *
     * @param userIdList
     * @param processInstanceId
     * @param activityId
     * @return
     */
    public static SetTaskApproverReq buildSetApproverReq(List<Long> userIdList, String processInstanceId, String activityId) {
        SetTaskApproverReq setTaskApproverReq = new SetTaskApproverReq();
        setTaskApproverReq.setProcessInstanceId(processInstanceId);
        setTaskApproverReq.setActivityId(activityId);
        setTaskApproverReq.setApproverIdList(CollectionUtils.isEmpty(userIdList) ? new ArrayList<>() : userIdList.stream().map(String::valueOf).collect(Collectors.toList()));
        return setTaskApproverReq;
    }

    /**
     * 模型名称处理
     *
     * @param modelKey
     * @return
     */
    public static String convertModelName(String modelKey) {
        return Optional.ofNullable(ProcessModelTypeEnum.getByName(modelKey)).map(ProcessModelTypeEnum::getDisplay).orElse("");
    }

}
