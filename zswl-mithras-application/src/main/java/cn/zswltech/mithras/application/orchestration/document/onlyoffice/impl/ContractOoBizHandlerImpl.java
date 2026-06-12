package cn.zswltech.mithras.application.orchestration.document.onlyoffice.impl;

import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.onlyoffice.DocDetailRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractExtraFileTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.document.onlyoffice.OoBizHandler;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 合同
 *
 * @author wangchuanhao
 * @date 2022/8/23 5:02 PM
 */
@Component
public class ContractOoBizHandlerImpl implements OoBizHandler {

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public void handle(DocDetailRSP docDetailRSP, MaterialsList materialsList) {
        // 合同模块的文件 只有 发起人 可在 合同生效状态-且不在变更流程中的时候可以打印和下载文件
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(materialsList.getBelongId());
        if (Objects.isNull(contractBaseInfo)) {
            // 没关联到合同 不操作
            return;
        }

        // 打印 下载 权限
        if (!canDownload(materialsList, contractBaseInfo)) {
            // 不是业务发起人 不允许 打印 下载
            docDetailRSP.getDocument().getPermissions().setDownload(false);
            docDetailRSP.getDocument().getPermissions().setPrint(false);
        }
        List<String> jobList = sysUserService.queryUserJobList(AccountUtil.getLoginInfo().getId());
        // 编辑权限 只有在创建、变更流程中 法务经理节点 才能编辑
        ProcessResp processResp = findRelatedProcess(contractBaseInfo.getId(), Arrays.asList(ProcessModelTypeEnum.ContractCreateFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name()));
        // 编辑权限 在合同正常结清流程中 运营管理岗、运营管理部负责人节点 才能编辑
        ProcessResp processResp2 = findRelatedProcess(contractBaseInfo.getId(), Arrays.asList(ProcessModelTypeEnum.ContractNormalSettleFlow.name()));
        if (Objects.nonNull(processResp)
                // 法务经理审批节点
                && "userTask_lawManager".equals(processResp.getCurTaskActivityIds())
                // 当前登陆用户是审批人
                && Stream.of(processResp.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0
                // 当前登陆用户是法务经理
                && jobList.contains(JobEnum.legalmanager.name())
        ) {
        } else if (Objects.nonNull(processResp)
                // 当前登陆用户是审批人
                && Stream.of(processResp.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0
                // 当前登陆用户是运营管理
                && jobList.contains(JobEnum.yunYingGuanLi.name())) {

        } else if (Objects.nonNull(processResp)
                // 当前登陆用户是审批人
                && Stream.of(processResp.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0
                // 当前登陆用户是运营管理（复核）
                && jobList.contains(JobEnum.yunYingGuanLiReview.name())) {

            //决议文件权限控制
        } else if ((ContractTypeEnum.RESOLUTION_FILE.name().equals(materialsList.getMaterialsType())
                || ContractTypeEnum.OTHER_CONTRACT.name().equals(materialsList.getMaterialsType()))
                // 当前登陆用户是项目经理岗位的人
                && ((sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.headofyyglb.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLiReview.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.legalmanager.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.operationManagement.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.loanreviewpost.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name()) ||
                sysUserService.currentUserIsSpecificJob(JobEnum.headoflegalcompliance.name()))
                && (Objects.nonNull(processResp) && Stream.of(processResp.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0))) {
        //合同正常结清所有权转移证书权限控制
        } else if (ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name().equals(materialsList.getMaterialsType())
                &&Objects.nonNull(processResp2)
                // 当前登陆用户是审批人
                && Stream.of(processResp2.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0
                // 当前登陆用户是运营管理
                && jobList.contains(JobEnum.yunYingGuanLi.name())){

        } else if (ContractExtraFileTypeEnum.CONTRACT_SETTLE_OWN.name().equals(materialsList.getMaterialsType())
                &&Objects.nonNull(processResp2)
                // 当前登陆用户是审批人
                && Stream.of(processResp2.getCurAssigneeIds().split(",")).map(Long::valueOf).filter(a -> AccountUtil.getLoginInfo().getId().equals(a)).count() > 0
                // 当前登陆用户是运营管理部负责人
                && jobList.contains(JobEnum.headofyyglb.name())){

        } else {
            docDetailRSP.getDocument().getPermissions().setEdit(false);
            docDetailRSP.getEditorConfig().setMode("view");
        }

    }

    @Override
    public String getBusinessModule() {
        return BusinessModuleEnum.CONTRACT.name();
    }

    private ProcessResp findRelatedProcess(Long contractId, List<String> modelKeyList) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(contractId));
        processPageReq.setModelKeyList(modelKeyList);
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    public boolean canDownload(MaterialsList materialsList, ContractBaseInfo contractBaseInfo) {
        // 为业务发起人
        return Objects.equals(contractBaseInfo.getProjSponsorUserId(), AccountUtil.getLoginInfo().getId())
                // 合同处于生效状态
                && ContractStatus.TAKE_EFFECT.name().equals(contractBaseInfo.getContractStatus())
                // 合同不处于变更流程中
                && findRelatedProcess(contractBaseInfo.getId(), Arrays.asList(ProcessModelTypeEnum.ContractModifyFlow.name())) == null;
    }

    public boolean canDownload(MaterialsList materialsList) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(materialsList.getBelongId());
        if (Objects.isNull(contractBaseInfo)) {
            return true;
        }
        // 为业务发起人
        return Objects.equals(contractBaseInfo.getProjSponsorUserId(), AccountUtil.getLoginInfo().getId())
                // 合同处于生效状态
                && ContractStatus.TAKE_EFFECT.name().equals(contractBaseInfo.getContractStatus())
                // 合同不处于变更流程中
                && findRelatedProcess(contractBaseInfo.getId(), Arrays.asList(ProcessModelTypeEnum.ContractModifyFlow.name())) == null;
    }


}
