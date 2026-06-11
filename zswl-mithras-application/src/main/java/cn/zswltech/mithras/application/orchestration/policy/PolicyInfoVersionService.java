package cn.zswltech.mithras.application.orchestration.policy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.policy.PolicyInfoVersionListRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.associationreport.constant.MithrasConstants;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.policy.enums.PolicyApprovalStatusEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.dto.ChangeDTO;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.policy.model.PolicyInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.policy.mapper.PolicyInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.BizProcessDataService;
import cn.zswltech.mithras.workflow.flow.attention.ProcAttentionRecordService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.workflow.flow.port.FlowEndEventProcessor;
import cn.zswltech.mithras.foundation.version.CommonVersionService;
import cn.zswltech.mithras.policy.versioning.handler.PolicyAbstractHandler;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.*;


/**
 * @create: 2023-06-17
 **/

@Slf4j
@Service
public class PolicyInfoVersionService extends CommonVersionService<PolicyInfo> implements FlowEndEventProcessor {
    @Resource
    private List<PolicyAbstractHandler> libHandlerList;

    @Resource
    private PolicyInfoMapper policyInfoMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProcAttentionRecordService procAttentionRecordService;


    @Transactional(rollbackFor = Throwable.class)
    public void effect(@NotNull Long policyId) {
        PolicyInfo baseInfo = policyInfoMapper.selectById(policyId);
        StartProcessReq startProcessReq = new StartProcessReq();
        PolicyInfo info = new PolicyInfo();
        info.setId(policyId);
        // 判断使用创建流程还是修改流程
        if (PolicyApprovalStatusEnum.NEW_UN_SUBMIT.name().equals(baseInfo.getApprovalStatus())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.PolicyCreateFlow.name());
            info.setApprovalStatus(PolicyApprovalStatusEnum.NEW_UNDER_APPROVAL.name());
        } else {
            startProcessReq.setModelKey(ProcessModelTypeEnum.PolicyModifyFlow.name());
            info.setApprovalStatus(PolicyApprovalStatusEnum.CHANGING_UNDER_APPROVAL.name());
        }
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(baseInfo.getProjId());
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(policyId));
        startProcessReq.setProcessInstanceName(projReviewBaseInfo.getProjName()+"保单审批");
        startProcessReq.setStartUserDeptId(Optional.ofNullable(projReviewBaseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, projReviewBaseInfo.getClientId());
        policyInfoMapper.updateById(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void policyStartReminderProcess(PolicyInfo policyInfo) {
        if(ObjectUtil.isEmpty(policyInfo)) {
            return;
        }
        //查询合同
        Long projSponsorUserId = null;
        //headoflegalcompliance yyglbleader
        if (ObjectUtil.isNotEmpty(policyInfo.getContractId())) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(policyInfo.getContractId());
            if(ObjectUtil.isNotEmpty(contractBaseInfo)) {
                projSponsorUserId = contractBaseInfo.getProjSponsorUserId();
            }
        }
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.PolicyReminderFlow.name());
        //ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(policyInfo.getProjId());
        startProcessReq.setStartUserId(String.valueOf(GlobalConstants.READONLY_ID));
        startProcessReq.setBusinessKey(String.valueOf(policyInfo.getId()));
        startProcessReq.setProcessInstanceName(policyInfo.getPolicyCode()+"保单到期提示");
        startProcessReq.setStartUserDeptId(null);
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projSponsorUserId", Objects.isNull(projSponsorUserId) ? new ArrayList<>() : ListUtil.toList(String.valueOf(projSponsorUserId)))
        ));
        String processInstanceId = processApiService.start(startProcessReq);
        //加入关注列表
        //bizProcessDataService.recordBizData(processInstanceId, projReviewBaseInfo == null ? null :projReviewBaseInfo.getClientId());
        policyInfo.setExpirationReminderFlag(YesOrNoNumberEnum.YES.getCode());
        policyInfoMapper.updateById(policyInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void policyStartOverdueReminderProcess(PolicyInfo policyInfo) {
        if(ObjectUtil.isEmpty(policyInfo)) {
            return;
        }
        //查询合同
        Long projSponsorUserId = null;
        Long bizDeptLeader = null;

        //headoflegalcompliance yyglbleader
        if (ObjectUtil.isNotEmpty(policyInfo.getContractId())) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(policyInfo.getContractId());
            if(ObjectUtil.isNotEmpty(contractBaseInfo)) {
                projSponsorUserId = contractBaseInfo.getProjSponsorUserId();
                bizDeptLeader = contractBaseInfo.getBizDeptLeaderId();
            }
        }
        //
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.PolicyOverdueReminderFlow.name());
        //ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(policyInfo.getProjId());
        startProcessReq.setStartUserId(String.valueOf(GlobalConstants.READONLY_ID));
        startProcessReq.setBusinessKey(String.valueOf(policyInfo.getId()));
        startProcessReq.setProcessInstanceName(policyInfo.getPolicyCode()+"续保逾期提示");
        startProcessReq.setStartUserDeptId(null);

        Map variables =new HashMap();
        variables.put("bizDeptLeader", Objects.isNull(bizDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(bizDeptLeader)));
        variables.put("projSponsorUserId", Objects.isNull(projSponsorUserId) ? new ArrayList<>() : ListUtil.toList(String.valueOf(projSponsorUserId)));
        startProcessReq.setVariables(variables);
        String processInstanceId = processApiService.start(startProcessReq);
        //bizProcessDataService.recordBizData(processInstanceId, projReviewBaseInfo == null ? null :projReviewBaseInfo.getClientId());
        policyInfo.setRenewalOverdueFlag(YesOrNoNumberEnum.YES.getCode());
        policyInfoMapper.updateById(policyInfo);
    }



    @Override
    public void customFlushData(PolicyInfo policyInfo, String version, boolean needClearLastFlag, Integer versionType) {
        for (PolicyAbstractHandler libHandler : libHandlerList) {
            libHandler.flushData(version, policyInfo.getId(), needClearLastFlag, versionType);
        }
    }

    @Override
    public void customReset(PolicyInfo policyInfo, CommonVersion commonVersion) {
        // 处理抄表逻辑
        for (PolicyAbstractHandler libHandler : libHandlerList) {
            libHandler.reset(policyInfo.getId(), commonVersion.getVersion());
        }
    }

    @Override
    public CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion) {
        PolicyInfo baseInfo = policyInfoMapper.selectById(newVersion.getMainId());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersionDiffRSP versionDiffRSP = new CommonVersionDiffRSP();
        Map<String, List> oldData = new HashMap<>();
        Map<String, List<Map<String, DiffValue>>> newData = new HashMap<>();
        Map<String, Boolean> moduleChanged = new HashMap<>();
        for (PolicyAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(baseInfo.getId())) {
                CommonVersionDiffBO commonVersionDiffBO = libHandler.libCompareLib(newVersion, oldVersion);
                oldData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getBeforeData());
                newData.put(libHandler.getSubModule().name(), commonVersionDiffBO.getAfterData());
                moduleChanged.put(libHandler.getSubModule().name(), commonVersionDiffBO.getModuleChanged());
            }
        }
        versionDiffRSP.setOldData(oldData);
        versionDiffRSP.setNewData(newData);
        versionDiffRSP.setModuleChanged(moduleChanged);
        return versionDiffRSP;
    }

    @Override
    protected CommonVersionListRSP convertPageRsp(CommonVersion cv, PolicyInfo baseModel, Map<Long, String> userNameMap) {
        PolicyInfoVersionListRSP rsp = BeanUtil.copyProperties(cv, PolicyInfoVersionListRSP.class);
        rsp.setGmtModify(cv.getUpdateTime());
        rsp.setOperatorId(cv.getUpdateBy());
        rsp.setOperatorName(Optional.ofNullable(userNameMap.get(cv.getUpdateBy())).orElse(MithrasConstants.DEFAULT_USER_NAME));
        rsp.setPolicyCode(baseModel.getPolicyCode());
        return rsp;
    }

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.POLICY;
    }



    public ProcessResp findRelatedProcess(Long projEstablishId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projEstablishId));
        processPageReq.setModelKeyList(BusinessModuleEnum.POLICY.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Override
    public ChangeDTO checkActualChange(Long mainId) {
        ChangeDTO changeDTO = new ChangeDTO();
        PolicyInfo baseInfo = policyInfoMapper.selectById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion newestVersion = findNewestVersion(mainId);
        if (Objects.isNull(newestVersion)) {
            // 不存在版本 则判为可以新增版本
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        for (PolicyAbstractHandler libHandler : libHandlerList) {
            if (libHandler.needHandle(mainId)) {
                ChangeDTO moduleChangeDTO = libHandler.checkActualChange(newestVersion);
                if (Boolean.TRUE.equals(moduleChangeDTO.getNeedApprovalChangeFlag())) {
                    // 快速返回
                    changeDTO.setChangeFlag(true);
                    changeDTO.setNeedApprovalChangeFlag(true);
                    return changeDTO;
                } else if (Boolean.TRUE.equals(moduleChangeDTO.getChangeFlag())) {
                    // 还需要找到最坏情况
                    changeDTO.setChangeFlag(true);
                }
            }
        }
        return changeDTO;
    }

    /**
     * 生效数据校验
     */
    public void effectCheck(PolicyInfo info) {
        List<MaterialsList> list = materialsListService.getList(info.getId(), BusinessModuleEnum.POLICY.name(), null, null);
        if (CollUtil.isEmpty(list)){
            throw new MithrasException("资料清单不能为空");
        }
    }

    public void checkRenewalInsurance(Long policyId) {
        PolicyInfo policyInfo = baseMapper.selectById(policyId);
        if (ObjectUtil.isEmpty(policyInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //检查续保保单是否存在
        List<PolicyInfo> baseInfos = policyInfoMapper.selectList(Wrappers.<PolicyInfo>lambdaQuery()
                .eq(PolicyInfo::getParentId, policyId));
        if(ObjectUtil.isNotEmpty(baseInfos)){
            baseInfos.forEach(this::effectCheck);
        }
    }

    @Override
    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        PolicyInfo baseInfo = policyInfoMapper.selectById(id);
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        PolicyInfo info = new PolicyInfo();
        info.setId(id);
        // 记录版本前要先更新状态
        if (processPass) {
            if (PolicyApprovalStatusEnum.NEW_UNDER_APPROVAL.name().equals(baseInfo.getApprovalStatus())) {
                info.setApprovalStatus(PolicyApprovalStatusEnum.NEW_APPROVAL_PASS.name());
            } else {
                info.setApprovalStatus(PolicyApprovalStatusEnum.CHANGING_APPROVAL_PASS.name());
            }
        } else {
            if (PolicyApprovalStatusEnum.NEW_UNDER_APPROVAL.name().equals(baseInfo.getApprovalStatus())) {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    info.setApprovalStatus(PolicyApprovalStatusEnum.CANCEL_NEW.name());
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    info.setApprovalStatus(PolicyApprovalStatusEnum.APPROVAL_REJECT.name());
                }
            } else {
                if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                    info.setApprovalStatus(PolicyApprovalStatusEnum.CANCEL_CHANGE.name());
                }
                if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                    info.setApprovalStatus(PolicyApprovalStatusEnum.APPROVAL_REJECT.name());
                }
            }
        }
        policyInfoMapper.updateById(info);
        // 记录版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType);
    }
}
