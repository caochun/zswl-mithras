package cn.zswltech.mithras.service.service.assetclassify;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.dao.TaskMapper;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.StringUtil;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.delayed.RedisDelayedQueueListener;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @description: 超时消息监听
 * @author: luyujie
 * @date: 2026/1/6 10:44
 */
@Component
@Slf4j
public class AssetManagementReviewExpirationListener implements RedisDelayedQueueListener<String> {

    @Resource
    private ExecutionService executionService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private TaskMapper taskMapper;

    @Override
    public void invoke(String processInstanceId) {
        log.info("===" + processInstanceId + "=== ");
        // 查询流程状态
        ProcessResp processResp = flowTaskApiService.queryProcessById(processInstanceId);
        // 不是running状态，直接返回
        if (!Objects.equals(processResp.getProcessStatus(), ProcessBusinessStatusEnum.RUNNING.getType())) {
            return;
        }
        //查询任务ID
        TaskSystemPageReq taskReq = new TaskSystemPageReq();
        taskReq.setIsRunning(1);
        taskReq.setProcessInstanceId(processInstanceId);
        taskReq.setSortType(1);
        List<TaskResp> taskRespList = taskMapper.querySystemTask(taskReq);
        if(ObjectUtil.isNotEmpty(taskRespList) && StringUtil.isNotEmpty(taskRespList.get(0).getTaskActivityId()) && "assetManagementReview".equals(taskRespList.get(0).getTaskActivityId())){
            // 资产管理岗提交一岗
            executionService.currentNodeAutoCommit(taskRespList.get(0), String.valueOf(GlobalConstants.READONLY_ID), "超过24小时自动通过");
        }
    }
}
