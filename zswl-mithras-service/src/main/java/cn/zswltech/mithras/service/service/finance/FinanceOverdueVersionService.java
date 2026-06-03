package cn.zswltech.mithras.service.service.finance;
import cn.zswltech.mithras.workflow.domain.enums.ProcessVarEnum;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueVersionSubmitREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverduePlanStatueEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueRecordStatueEnum;
import cn.zswltech.mithras.service.enums.financeoverdue.OverdueRecordTypeEnum;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueIntegration;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueSettlement;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueVersionRelation;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.finance.FinanceOverdueVersionManagerService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description 逾期报送计划表
* @author vico
* @date 2025-09-15
*/
@Service
public class FinanceOverdueVersionService {

    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private FinanceOverdueReportBaseService financeOverdueReportBaseService;
    @Resource
    private FinanceOverdueVersionRelationService financeOverdueVersionRelationService;
    @Resource
    private FinanceOverdueIntegrationService financeOverdueIntegrationService;
    @Resource
    private FinanceOverdueSettlementService financeOverdueSettlementService;
    @Resource
    private FinanceOverdueVersionManagerService financeOverdueVersionManagerService;

    public void submit(FinanceOverdueVersionSubmitREQ req) {
        FinanceOverdueReportBase reportBase = financeOverdueReportBaseService.getById(req.getReportId());
        if (ObjectUtil.isEmpty(reportBase)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (OverduePlanStatueEnum.CLOSE.name().equals(reportBase.getReportStatus())){
            throw new MithrasException("已关闭的计划不能提交审批");
        }
        //检查
        if (CollectionUtil.isEmpty(req.getIntegrationRecordIds()) && ObjectUtil.isEmpty(req.getSettlementRecordIds())) {
            financeOverdueIntegrationService.submitCheck(req);
            financeOverdueSettlementService.submitCheck(req);
        } else if (CollectionUtil.isNotEmpty(req.getIntegrationRecordIds()) && ObjectUtil.isEmpty(req.getSettlementRecordIds())){
            financeOverdueIntegrationService.submitCheck(req);
        } else if (CollectionUtil.isEmpty(req.getIntegrationRecordIds()) && ObjectUtil.isNotEmpty(req.getSettlementRecordIds())) {
            financeOverdueSettlementService.submitCheck(req);
        } else {
            financeOverdueIntegrationService.submitCheck(req);
            financeOverdueSettlementService.submitCheck(req);
        }
        // 生成流程实例
        StartProcessReq startProcessReq = buildCommonStartProcessReq(reportBase);
        startProcessReq.setModelKey(ProcessModelTypeEnum.FinanceOverdue.name());
        //startProcessReq.setSubModule(ContractFlowSubModuleEnum.CREATE_ALL.name());
        String processInstanceId = processApiService.start(startProcessReq);
        List<FinanceOverdueIntegration> overdueIntegrations = null;
        List<FinanceOverdueSettlement> overdueSettlements = null;
        // 保存流程与勾选id的关系
        List<FinanceOverdueVersionRelation> addRelation = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(req.getIntegrationRecordIds())) {
            overdueIntegrations = financeOverdueIntegrationService.listByIds(req.getIntegrationRecordIds());
        } else if (ObjectUtil.isEmpty(req.getSettlementRecordIds())) {
            overdueIntegrations = financeOverdueIntegrationService.listByReportId(req.getReportId());
        }
        if (CollectionUtil.isNotEmpty(req.getSettlementRecordIds())) {
            overdueSettlements = financeOverdueSettlementService.listByIds(req.getSettlementRecordIds());
        } else if (ObjectUtil.isEmpty(req.getIntegrationRecordIds())) {
            overdueSettlements = financeOverdueSettlementService.listByReportId(req.getReportId());
        }

        if (ObjectUtil.isNotEmpty(overdueIntegrations)) {
            overdueIntegrations.forEach(e -> {
                FinanceOverdueVersionRelation relation = new FinanceOverdueVersionRelation();
                relation.setRecordId(e.getId());
                relation.setRecordType(OverdueRecordTypeEnum.INTEGRATION.name());
                relation.setProcessInstanceId(processInstanceId);
                addRelation.add(relation);
            });
        }

        if (ObjectUtil.isNotEmpty(overdueSettlements)) {
            overdueSettlements.forEach(e -> {
                FinanceOverdueVersionRelation relation = new FinanceOverdueVersionRelation();
                relation.setRecordId(e.getId());
                relation.setRecordType(OverdueRecordTypeEnum.SETTLEMENT.name());
                relation.setProcessInstanceId(processInstanceId);
                addRelation.add(relation);
            });
        }

        if (!addRelation.isEmpty()) {
            financeOverdueVersionRelationService.saveBatch(addRelation);
        }
        //更新状态
        if (CollectionUtil.isNotEmpty(overdueIntegrations)) {
            overdueIntegrations.forEach(e -> {
                if(StrUtil.equalsAny(e.getApprovalStatus(), ProjProcessState.NEW_APPROVAL_PASS.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.CHANGING_UN_SUBMIT.name())) {
                    e.setApprovalStatus(ProjProcessState.CHANGING_UNDER_APPROVAL.name());
                } else {
                    e.setApprovalStatus(ProjProcessState.NEW_UNDER_APPROVAL.name());
                }
            });
            financeOverdueIntegrationService.updateBatchById(overdueIntegrations);
        }

        if (CollectionUtil.isNotEmpty(overdueSettlements)) {
            overdueSettlements.forEach(e -> {
                if(StrUtil.equalsAny(e.getApprovalStatus(), ProjProcessState.NEW_APPROVAL_PASS.name(), ProjProcessState.CHANGING_APPROVAL_PASS.name(), ProjProcessState.CHANGING_UN_SUBMIT.name())) {
                    e.setApprovalStatus(ProjProcessState.CHANGING_UNDER_APPROVAL.name());
                } else {
                    e.setApprovalStatus(ProjProcessState.NEW_UNDER_APPROVAL.name());
                }
            });
            financeOverdueSettlementService.updateBatchById(overdueSettlements);
        }
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return
     */
    private StartProcessReq buildCommonStartProcessReq(FinanceOverdueReportBase reportBase) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(reportBase.getId()));
        startProcessReq.setProcessInstanceName(String.join("-", ProcessModelTypeEnum.FinanceOverdue.getDisplay(), reportBase.getPlanDate().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN))));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        //startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        //增加法律合规部负责人
        /*startProcessReq.setVariables(MapUtil.of(
                Pair.of("flhgbDeptLeader", Objects.isNull(flhgbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(flhgbLeader))),
                Pair.of("yyglbbusinesshead", Objects.isNull(yyglbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(yyglbLeader))),
                Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
                Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
                Pair.of(ProcessVarEnum.projReviewNeedBoradApprove.name(), projReviewNeedBoardApproveFlag),
                // 总经理节点审批人需要计算 项目评审需要董事会审批 ? 无需审批人 : 分管领导
                Pair.of("generalManagerNodeApprover", projReviewNeedBoardApproveFlag ? new ArrayList<>() : Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ? ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>())
        ));*/
        return startProcessReq;
    }

    /**
     * 流程结束 处理流程状态 并把数据抄到lib表
     *
     * @param overdueBaseId
     * @param endType
     */
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long overdueBaseId, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        //变更状态
        List<Long> integrationIds = financeOverdueVersionRelationService.listByProcessInstanceId(processInstanceId, OverdueRecordTypeEnum.INTEGRATION.name())
                .stream().map(FinanceOverdueVersionRelation::getRecordId).collect(Collectors.toList());
        financeOverdueIntegrationService.updateStatus(overdueBaseId, integrationIds, processPass ? ProjProcessState.APPROVAL_PASS.name() : ProjProcessState.APPROVAL_REJECT.name(), OverdueRecordStatueEnum.NOT_REPORT.name());
        List<Long> settlementIds = financeOverdueVersionRelationService.listByProcessInstanceId(processInstanceId, OverdueRecordTypeEnum.SETTLEMENT.name())
                .stream().map(FinanceOverdueVersionRelation::getRecordId).collect(Collectors.toList());
        financeOverdueSettlementService.updateStatus(overdueBaseId, settlementIds, processPass ? ProjProcessState.APPROVAL_PASS.name() : ProjProcessState.APPROVAL_REJECT.name(), OverdueRecordStatueEnum.NOT_REPORT.name());
        // 通不通过都生成版本
        int versionType = processPass ? VersionTypeConstants.NORMAL : VersionTypeConstants.INVALID;
        financeOverdueVersionManagerService.recordVersion(overdueBaseId, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, versionType, null);
        if (!processPass) {
            // 审批拒绝 回滚数据 不回滚
            //financeOverdueVersionManagerService.reset(overdueBaseId);
        }
    }

}