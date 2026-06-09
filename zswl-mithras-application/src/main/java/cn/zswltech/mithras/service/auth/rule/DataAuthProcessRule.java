package cn.zswltech.mithras.service.auth.rule;


import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsBusinessTypeEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.workflow.application.flow.util.FlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限校验 判断是否在流程中
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:56 PM
 */
@Slf4j
@Component
public class DataAuthProcessRule {
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FileService fileService;

    /**
     * 判断是否在流程中
     * @param mainId
     * @return
     */
    public void check(DataAuthBusinessModule businessModule, Long mainId) {
        if (businessModule.equals(BusinessModuleEnum.CONTARCT_DEPOSIT)) {
            return;
        }
        if (businessModule.equals(BusinessModuleEnum.CONTRACT_TEXT_MANAGE) || businessModule.equals(BusinessModuleEnum.VISIT_RECORD)) {
            return;
        }
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            // 流程为空 放过
            return;
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (BusinessModuleEnum.CONTRACT.equals(businessModule) && Objects.nonNull(loginInfo)) {
            Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
            if (allOperateUserJob.containsKey(loginInfo.getId())) {
                return;
            }
        }
        if (BusinessModuleEnum.CREDIT_REPORT_SELECT.equals(businessModule) && Objects.nonNull(loginInfo)) {
            Map<Long, List<String>> creditsearcherJob = fileService.getAllOperateUserJob(JobEnum.creditsearcher);
            Map<Long, List<String>> headerJob = fileService.getAllOperateUserJob(JobEnum.headofyyglb);
            if (creditsearcherJob.containsKey(loginInfo.getId()) || headerJob.containsKey(loginInfo.getId())) {
                return;
            }
        }

        if(FilingMaterialsBusinessTypeEnum.getBusinessTypeAll().contains(businessModule.name())
                || Objects.equals(BusinessModuleEnum.FUND_FILING.name(),businessModule.name())
                || Objects.equals(BusinessModuleEnum.AFTER_LEASING_FILING.name(),businessModule.name())
                || Objects.equals(BusinessModuleEnum.OTHER_FILING.name(),businessModule.name())){
            return;
        }
        boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
        if (!isStartUserNode) {
            throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
        }
    }


    public void appCheck(DataAuthBusinessModule businessModule, Long mainId, Long createdBy) {
        if (businessModule.equals(BusinessModuleEnum.CONTRACT_TEXT_MANAGE) || businessModule.equals(BusinessModuleEnum.VISIT_RECORD)) {
            return;
        }
        ProcessPageReq req = new ProcessPageReq();
        req.setBusinessKey(String.valueOf(mainId));
        req.setPageIndex(1);
        req.setPageSize(1);
        req.setModelKeyList(businessModule.getModelKeyList());
        req.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                .stream().findFirst().orElse(null);
        if (Objects.isNull(processResp)) {
            // 流程为空 放过
            return;
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if ((createdBy != null && loginInfo == null)) {
            loginInfo = new AccountVO();
            loginInfo.setId(createdBy);
        } else if (createdBy != null && loginInfo != null && !createdBy.equals(loginInfo.getId())) {
            loginInfo.setId(createdBy);
        }
        if (BusinessModuleEnum.CONTRACT.equals(businessModule) && Objects.nonNull(loginInfo)) {
            Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
            if (allOperateUserJob.containsKey(loginInfo.getId())) {
                return;
            }
        }
        if (BusinessModuleEnum.CREDIT_REPORT_SELECT.equals(businessModule) && Objects.nonNull(loginInfo)) {
            Map<Long, List<String>> creditsearcherJob = fileService.getAllOperateUserJob(JobEnum.creditsearcher);
            Map<Long, List<String>> headerJob = fileService.getAllOperateUserJob(JobEnum.headofyyglb);
            if (creditsearcherJob.containsKey(loginInfo.getId()) || headerJob.containsKey(loginInfo.getId())) {
                return;
            }
        }
        boolean isStartUserNode = FlowUtil.isStartUserNode(processResp);
        if (!isStartUserNode) {
            throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
        }
    }
}
