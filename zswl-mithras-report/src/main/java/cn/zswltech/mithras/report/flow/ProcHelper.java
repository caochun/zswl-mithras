package cn.zswltech.mithras.report.flow;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.constant.FlowConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 流程辅助
 *
 * @author wangchuanhao
 * @date 2023/1/12 10:38 AM
 */
@Service
public class ProcHelper {

    @Resource
    private FlowTaskApiService taskApiService;

    public Integer findProcessStatus(String procBusinessKey) {
        ProcessPageReq req = new ProcessPageReq();
        req.setPageSize(1);
        req.setBusinessKey(procBusinessKey);
        ProcessResp processResp = taskApiService.queryProcess(req).getContents().stream().findFirst().orElse(null);
        return Optional.ofNullable(processResp).map(ProcessResp::getProcessStatus).orElse(null);
    }

    /**
     * 判断流程是否已结束
     * @param procBusinessKey
     * @return
     */
    public boolean judgeProcessEndWithCheck(String procBusinessKey) {
        Integer processStatus = findProcessStatus(procBusinessKey);
        if (Objects.isNull(processStatus)) {
            throw new MithrasException("流程不存在");
        }
        return !Objects.equals(ProcessBusinessStatusEnum.RUNNING.getType(), processStatus);
    }

    /**
     * 寻找该模块进行中的流程
     * @return
     */
    public ProcessResp findRelatedProcess() {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.CreditReportFlow.name()));
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    /**
     * 是否可编辑数据 不在流程中 或 在发起人节点
     * @return
     */
    public boolean canEditData() {
        ProcessResp processResp = findRelatedProcess();
        return Objects.isNull(processResp)
                || (FlowConstants.START_USER_TASK.equals(processResp.getCurTaskActivityIds()) && Objects.equals(Long.valueOf(processResp.getStartUserId()), AccountUtil.getLoginInfo().getId()));
    }

}
