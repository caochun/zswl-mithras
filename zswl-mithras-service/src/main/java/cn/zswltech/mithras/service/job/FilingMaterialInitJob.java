package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowExecutionApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.execution.ExecutionProcessBaseReq;
import cn.zswltech.flow.core.domain.req.execution.ExecutionRandomReturnReq;
import cn.zswltech.flow.core.domain.req.task.TaskSystemPageReq;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.service.impl.FlowCacheService;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.workflow.domain.enums.CommonProcessPrepareStatus;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.service.enums.filingmaterials.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.mapper.model.filingmaterials.FilingMaterials;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.filingmaterials.AfterFilingMaterialsService;
import cn.zswltech.mithras.service.service.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.service.service.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author linlili
 */
@Component
@Slf4j
public class FilingMaterialInitJob {
    @Resource
    private AfterFilingMaterialsService afterFilingMaterialsService;
    @Resource
    private FundFilingMaterialsService fundFilingMaterialsService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private FlowExecutionApiService executionApiService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowCacheService flowCacheService;
    @Resource
    private CommonProcessPrepareService commonProcessPrepareService;
    @Resource
    private FilingMaterialsService filingMaterialsService;

    /**
     * 租后资料归档邮件发送
     */
    @XxlJob("afterFilingMaterialInitJob")
    public void afterFilingMaterialInitJob() {
        try {
            log.info("afterFilingMaterialInitJob 开始扫描");
            //审批状态在2026.01.01至上线期间更新为“计划完结审批通过
            List<NewAfterLeaseCheckPlanClient> newAfterLeaseCheckPlanClients = afterFilingMaterialsService.query2026ApprovePassPlanClient();
            if (CollUtil.isEmpty(newAfterLeaseCheckPlanClients)) {
                return;
            }
            for (NewAfterLeaseCheckPlanClient client : newAfterLeaseCheckPlanClients) {
                try {
                    afterFilingMaterialsService.initFilingMaterials(client.getId(),null);
                } catch (Exception e) {
                    log.error("afterFilingMaterialInitJob 初始化失败：" + client.getId());
                }
            }
        } catch (Exception e) {
            log.error("租后资料归档流程发起初始化任务失败:{}", e.getMessage());
        }
    }

    @XxlJob("afterFilingMaterialBankJob")
    public void afterFilingMaterialBankJob() {
        try {
            log.info("afterFilingMaterialBankJob 开始扫描");
            String jobParam = XxlJobHelper.getJobParam();
            if(CharSequenceUtil.isEmpty(jobParam)){
                return;
            }
            List<String> list = new ArrayList<>(Arrays.asList(jobParam.split(",")));
            LambdaQueryWrapper<FilingMaterials> queryWrapper = Wrappers.<FilingMaterials>lambdaQuery()
                    .in(FilingMaterials::getId, list);
            List<FilingMaterials> filingMaterialsList = afterFilingMaterialsService.list(queryWrapper);
            if(CollUtil.isEmpty(filingMaterialsList)){
                return;
            }
            TaskSystemPageReq flowReq = new TaskSystemPageReq();
            flowReq.setIsRunning(1);
            flowReq.setSortType(1);
            ExecutionRandomReturnReq executionRandomReturnReq = new ExecutionRandomReturnReq();
            executionRandomReturnReq.setTaskActivityId("userTask_assetManager");
            executionRandomReturnReq.setJumpToSourceFlag(0);
            executionRandomReturnReq.setHandlerId("3");
            executionRandomReturnReq.setMessage("系统管理员统一退回");

            for (FilingMaterials filingMaterials : filingMaterialsList) {
                try {
                    flowReq.setProcessInstanceId(filingMaterials.getFlowId());
                    flowReq.setModelKey(ProcessModelTypeEnum.AfterFilingMaterialsApplyFlow.name());
                    Page<TaskResp> flowTaskPage = taskApiService.querySystemTask(flowReq);
                    List<TaskResp> contents = flowTaskPage.getContents();
                    List<TaskResp> afterReviewList = contents.stream().filter(e -> Objects.equals(e.getTaskActivityId(), "userTask_afterReview")).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(afterReviewList)) {
                        continue;
                    }
                    executionRandomReturnReq.setProcessInstanceId(filingMaterials.getFlowId());
                    executionRandomReturnReq.setTaskId(afterReviewList.get(0).getTaskId());
                    executionApiService.randomReturn(executionRandomReturnReq);
                } catch (Exception e) {
                    log.error("退回失败");
                }
            }
        } catch (Exception e) {
            log.error("租后资料归档系统退回:{}", e.getMessage());
        }
    }

    /**
     * 资金资料归档邮件发送
     */
    @XxlJob("fundFilingMaterialInitJob")
    public void fundFilingMaterialInitJob() {
        try {
            log.info("fundFilingMaterialInitJob 开始扫描");
            String jobParam = XxlJobHelper.getJobParam();
            //直融
            LambdaQueryWrapper<FundDirectFinancingBaseInfo> directQueryWrapper = Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                    .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name());
            //间融
            LambdaQueryWrapper<FundFinancingBaseInfo> queryWrapper = Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                    .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name());
            if (CharSequenceUtil.isNotBlank(jobParam)) {
                List<String> list = new ArrayList<>(Arrays.asList(jobParam.split(",")));
                directQueryWrapper.in(FundDirectFinancingBaseInfo::getFinancingCode, list);
                queryWrapper.in(FundFinancingBaseInfo::getFinancingCode, list);
            }

            List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = SpringUtil.getBean(FundDirectFinancingBaseInfoMapper.class).selectList(directQueryWrapper);
            List<FundFinancingBaseInfo> financingBaseInfoList = SpringUtil.getBean(FundFinancingBaseInfoMapper.class).selectList(queryWrapper);

            for (FundDirectFinancingBaseInfo baseInfo : directFinancingBaseInfoList) {
                try {
                    fundFilingMaterialsService.initFundCommonProcessPrepare(baseInfo.getId(), baseInfo.getFundManagerId(), baseInfo.getFinancingCode(),
                            FilingMaterialsFilingTypeEnum.FUND_DIRECT_FINANCING.name(), FilingMaterialsInitiationMethodEnum.SYSTEM.name());
                } catch (Exception e) {
                    log.error("直融 初始化失败：" + baseInfo.getFinancingCode());
                }
            }
            for (FundFinancingBaseInfo existBaseInfo : financingBaseInfoList) {
                try {
                    fundFilingMaterialsService.initFundCommonProcessPrepare(existBaseInfo.getId(), existBaseInfo.getFundManagerId(), existBaseInfo.getFinancingCode(),
                            FilingMaterialsFilingTypeEnum.FUND_FINANCING.name(), FilingMaterialsInitiationMethodEnum.SYSTEM.name());
                } catch (Exception e) {
                    log.error("直融 初始化失败：" + existBaseInfo.getFinancingCode());
                }
            }
        } catch (Exception e) {
            log.error("资金资料归档流程发起初始化任务失败:{}", e.getMessage());
        }
    }

    @XxlJob("projectFilingMaterialCloseJob")
    public void projectFilingMaterialCloseJob() {
        try {
            log.info("projectFilingMaterialCloseJob 项目资料归档在途流程关闭开始");
            String jobParam = XxlJobHelper.getJobParam();
            //审批中
            LambdaQueryWrapper<FilingMaterials> queryWrapper = Wrappers.<FilingMaterials>lambdaQuery();
            queryWrapper.eq(FilingMaterials::getApproveStatus, FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
            queryWrapper.eq(FilingMaterials::getFilingType, FilingMaterialsFilingTypeEnum.BUSINESS_MATERIALS.name());
            queryWrapper.isNull(FilingMaterials::getApproveDate);
            queryWrapper.isNotNull(FilingMaterials::getFlowId);

            //待办
            LambdaQueryWrapper<CommonProcessPrepare> query = Wrappers.lambdaQuery();
            query.eq(CommonProcessPrepare::getStatus, CommonProcessPrepareStatus.PEND_COMMIT.name());
            query.eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.FilingMaterialsApplyFlow.name());

            if (CharSequenceUtil.isNotEmpty(jobParam)) {
                List<String> list = new ArrayList<>(Arrays.asList(jobParam.split(",")));
                queryWrapper.in(FilingMaterials::getId, list);
                query.in(CommonProcessPrepare::getBusinessId,list);
            }

            List<FilingMaterials> filingMaterialsList = filingMaterialsService.list(queryWrapper);
            List<CommonProcessPrepare> commonProcessPrepareList = commonProcessPrepareService.list(query);

            ExecutionProcessBaseReq flowReq = new ExecutionProcessBaseReq();
            for (FilingMaterials filingMaterials : filingMaterialsList) {
                try {
                    ProcessInstance processInstance = flowCacheService.queryRunningProcessInstanceWithCheck(filingMaterials.getFlowId());
                    if (Objects.isNull(processInstance)) {
                        continue;
                    }
                    flowReq.setHandlerId(processInstance.getStartUserId());
                    flowReq.setProcessInstanceId(filingMaterials.getFlowId());
                    executionApiService.cancel(flowReq);
                } catch (Exception e) {
                    log.error("关闭失败");
                }
            }
            for (CommonProcessPrepare commonProcessPrepare : commonProcessPrepareList) {
                try {
                    commonProcessPrepareService.discard(commonProcessPrepare.getId());
                } catch (Exception e) {
                    log.error("关闭失败");
                }
            }
        } catch (Exception e) {
            log.error("项目资料关闭流程关闭失败:{}", e.getMessage());
        }
    }

}