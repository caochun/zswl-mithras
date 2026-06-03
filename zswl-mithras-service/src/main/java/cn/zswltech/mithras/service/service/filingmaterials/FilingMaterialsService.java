package cn.zswltech.mithras.service.service.filingmaterials;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.ProcessHistoryReq;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.CommonFileSortWeight;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.filingmaterials.*;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.flow.search.ProcessHistoryRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessTaskExtra;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListListRSP;
import cn.zswltech.mithras.dto.materialsfile.ProjMaterialsListListRSP;
import cn.zswltech.mithras.service.CommonFileSortComparator;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.filingmaterials.domain.constant.FilingMaterialsConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.convert.FileConvert;
import cn.zswltech.mithras.service.convert.flow.FlowProcessConvert;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.customer.domain.enums.app.VisitPhaseStatus;
import cn.zswltech.mithras.customer.domain.enums.app.VisitRecordStatus;
import cn.zswltech.mithras.customer.domain.enums.client.ClientMaterialsDisplayEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.CreditorDebtorTypeEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.*;
import cn.zswltech.mithras.credit.domain.groupcredit.establish.enums.GroupCreditEstablishMaterialsEnum;
import cn.zswltech.mithras.service.enums.lease.LeaseFileTypeEnums;
import cn.zswltech.mithras.payment.domain.enums.LendingMaterialType;
import cn.zswltech.mithras.payment.domain.enums.PaymentTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.InvestigationResultEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.PublicInfoClientTypeEnum;
import cn.zswltech.mithras.payment.domain.enums.pubinfo.PublicInfoFileTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishMaterialsApproveEnum;
import cn.zswltech.mithras.service.enums.projestablish.ProjEstablishMaterialsEnum;
import cn.zswltech.mithras.service.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.FileListProviderFactory;
import cn.zswltech.mithras.service.gendoc.render.BusinessMaterialsInnerOperationRender;
import cn.zswltech.mithras.service.gendoc.render.BusinessMaterialsRender;
import cn.zswltech.mithras.service.gendoc.render.MaterialsApprovalSnapshootRender;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.contract.mapper.contract.*;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.FilingMaterialsMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.mapper.GroupCreditReviewBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.leaseholdproperty.LeaseItemInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractMortgageLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractPledgeLibMapper;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.FilingMaterials;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.review.model.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentPolicyInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoQuery;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.pubinfo.PublicInfoRecord;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentPolicyInfoMapper;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projpricing.ProjPricingBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.email.AbstractSendEmailHandler;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.groupcreditestablish.GroupCreditEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.model.PublicInfoExcelModel;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoQueryService;
import cn.zswltech.mithras.payment.application.pubinfo.PublicInfoRecordService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.FlowUtil;
import cn.zswltech.mithras.service.util.WatermarkUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType.GROUP_CREDIT_REVIEW;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * 项目资料归档
 * @author lllin
 * @date 2025-12-03
 */
@Slf4j
@Service("FilingMaterialsService")
public class FilingMaterialsService extends AbstractFilingMaterialsService<FilingMaterialsMapper, FilingMaterials> {
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;
    @Resource
    private BusinessMaterialsRender businessMaterialsRender;
    @Resource
    private BusinessMaterialsInnerOperationRender innerOperationRender;
    @Resource
    private MaterialsApprovalSnapshootRender materialsApprovalSnapshootRender;
    @Resource
    private GroupCreditReviewBaseInfoMapper groupCreditReviewBaseInfoMapper;
    @Resource
    private GroupCreditEstablishBaseInfoMapper groupCreditEstablishBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private ProjPricingBaseInfoMapper projPricingBaseInfoMapper;
    @Resource
    private LeaseItemInfoMapper leaseItemInfoMapper;
    @Resource
    private FlowProcessConvert flowProcessConvert;
    @Resource
    private List<AbstractSendEmailHandler> abstractSendEmailHandlers;
    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PublicInfoRecordService publicInfoRecordService;
    @Resource
    private PublicInfoQueryService publicInfoQueryService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    protected FileConvert fileConvert;
    @Resource
    private FileListProviderFactory fileListProviderFactory;
    @Resource
    ContractMortgageLibMapper contractMortgageLibMapper;
    @Resource
    ContractPledgeLibMapper contractPledgeLibMapper;
    @Resource
    ContractGuarantorLibMapper contractGuarantorLibMapper;
    @Resource
    CommonVersionMapper commonVersionMapper;
    @Resource
    ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    MaterialsListLibService materialsListLibService;
    @Resource
    LeaseItemInfoService leaseItemInfoService;
    @Resource
    VisitRecordMapper visitRecordMapper;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private HistoryService historyService;

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    /*提交审批*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String startProcess(FilingBaseREQ filingBaseREQ) {
        FilingMaterials filingMaterials = this.getById(filingBaseREQ.getId());
        Assert.notNull(filingMaterials, () -> MithrasException.newException("记录不存在"));
        if (!Objects.equals(FilingMaterialsProcessStatusEnum.UN_SUBMIT.name(), filingMaterials.getApproveStatus())) {
            throw new MithrasException("流程已提交审批，不可重复提交");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(filingMaterials.getContractId());
        /*初始化流程发起对象*/
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.FilingMaterialsApplyFlow.name());
        startProcessReq.setStartUserId(String.valueOf(contractBaseInfo.getProjSponsorUserId())); //项目主办
        startProcessReq.setBusinessKey(String.valueOf(filingBaseREQ.getId()));
        startProcessReq.setProcessInstanceName(String.format("%s-项目资料归档", contractBaseInfo.getContractCode()));
        String currentUserId = String.valueOf(contractBaseInfo.getProjSponsorUserId());
        /*发起人、发起部门*/
        startProcessReq.setStartUserId(currentUserId);
        startProcessReq.setStartUserDeptId(String.valueOf(contractBaseInfo.getBizDeptId()));
        Map<String, Object> varMap = new HashMap<>(1);
        /*初审岗 默认葛晓青*/
        List<String> initApproveUser = this.getInitApproveUser();
        List<String> reviewJobUser = this.getReviewJobUser();
        varMap.put("manager",Collections.singletonList(currentUserId));
        varMap.put("initialReview", initApproveUser);
        /*复审岗*/
        varMap.put("review", reviewJobUser);
        startProcessReq.setVariables(varMap);
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, filingMaterials.getClientId());
        /*更新申请表*/
        filingMaterials.setFlowId(processInstanceId);
        filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.UNDER_APPROVAL.name());
        this.updateById(filingMaterials);
        return processInstanceId;
    }


    /**
     * 初始化待办信息
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void initCommonProcessPrepare(Long contractId, String filingType, String initiationMethod) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        CommonVersion commonVersion = getLastVersion(contractBaseInfo.getId(),BusinessModuleEnum.CONTRACT.name());
        if (Objects.isNull(commonVersion)) {
            log.error("项目资料归档生成失败,获取合同项下最新审批通过的版本号失败");
            return;
        }
        Long clientId = contractBaseInfo.getClientId();
        /*1、初始化待办信息*/
        FilingMaterials filingMaterials = FilingMaterials.initFilingMaterials(clientId, contractId, contractBaseInfo.getProjCode(),
                filingType, initiationMethod, commonVersion.getVersion(),contractId,FilingMaterialsConstants.OBJECT_TYPE_PROJECT);
        filingMaterials.setDeptId(contractBaseInfo.getBizDeptId());
        filingMaterials.setUserId(contractBaseInfo.getProjSponsorUserId());
        this.save(filingMaterials);
        /*2、拷贝资料*/
        this.filingMaterialCopyFile(contractBaseInfo, filingMaterials.getId(), commonVersion.getVersion());
        /*3、直接发起流程*/
        this.startProcess(new FilingBaseREQ(filingMaterials.getId()));
    }

    /*流程结束*/
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processEnd(Long id, Integer endType, String processInstanceId) {
        FilingMaterials filingMaterials = this.getById(id);
        /*更新审批状态*/
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        // 更新流程状态
        FilingMaterialsProcessStatusEnum processState;
        if (processPass) {
            processState = FilingMaterialsProcessStatusEnum.APPROVAL_PASS;
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType) ? FilingMaterialsProcessStatusEnum.CANCEL : FilingMaterialsProcessStatusEnum.APPROVAL_REJECT;
        }
        filingMaterials.setApproveStatus(processState.name());
        filingMaterials.setApproveDate(LocalDateTime.now());
        //【“档案管理岗（复核）”审批通过日期－流程推送至项目主办日期】（仅计算工作日）+1
        filingMaterials.setDuration(DateUtil.safeCountWorkdayNumber(filingMaterials.getStartDate(), filingMaterials.getApproveDate()) + 1);
        // 档案管理初审耗时（工作日） = SUM【“档案管理岗（初审）”提交日期－流程到达“档案管理岗（初审）”日期】（仅计算工作日）+1
        filingMaterials.setMaterialsManagerReviewDuration(calculateNodeTotalWorkDayDuration(filingMaterials.getFlowId(), FilingMaterialsConstants.TASK_NODE_CODE_2) + 1);
        // 档案管理复核耗时（工作日） = SUM【“档案管理岗（复核）”提交日期－流程到达“档案管理岗（复核）”日期】（仅计算工作日）+1
        filingMaterials.setMaterialsManagerReReviewDuration(calculateNodeTotalWorkDayDuration(filingMaterials.getFlowId(), FilingMaterialsConstants.TASK_NODE_CODE_3) + 1);
        this.updateById(filingMaterials);
        /*审批通过抄送运营管理部负责人*/
        Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.YYGLB, FlowAssistService.YYGLB_DESC, businesshead.name());
        if (processPass && Objects.nonNull(yyglbDeptLeader)) {
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(filingMaterials.getContractId());
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(processInstanceId);
            req.setCcUserIdList(Arrays.asList(yyglbDeptLeader));
            req.setMessage(String.format("【%s】-项目资料归档已审批通过", contractBaseInfo.getContractCode()));
            getBean(ExecutionService.class).cc(req);
        }
    }

    /**
     * 更新首岗提交时间及退回时间
     *
     * @param businessKey
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateFilingMaterialsReturnDate(String businessKey, LocalDate firstCommitDate, LocalDate returnDate) {
        LambdaUpdateWrapper<FilingMaterials> updateWrapper = Wrappers.lambdaUpdate();
//        updateWrapper.set(FilingMaterials::getFirstCommitDate, firstCommitDate);
        updateWrapper.set(FilingMaterials::getReturnDate, returnDate);
        updateWrapper.eq(FilingMaterials::getId, Integer.parseInt(businessKey));
        this.update(updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFilingLedgeField(String taskActId,Long id,String message){
        FilingMaterials filingMaterials = this.getById(id);
        Assert.notNull(filingMaterials, () -> MithrasException.newException("记录不存在"));
        Integer count ;
        String text = "第【%s】次退回:【%s】";
        String messageResult;
        String oldReason;
        if (StrUtil.equals(FilingMaterialsConstants.TASK_NODE_CODE_2, taskActId)) {
            //初审节点
            count = Optional.ofNullable(filingMaterials.getArchivePreReviewRejectCount()).orElse(0);
            count++;
            filingMaterials.setArchivePreReviewRejectCount(count);
            messageResult = String.format(text, count, message);
            oldReason = filingMaterials.getArchivePreReviewRejectReasonSum();
            filingMaterials.setArchivePreReviewRejectReasonSum(StrUtil.isEmpty(oldReason) ? messageResult : oldReason +";"+ messageResult);
        } else if (StrUtil.equals(FilingMaterialsConstants.TASK_NODE_CODE_3, taskActId)) {
            //复核节点
            count = Optional.ofNullable(filingMaterials.getArchiveReviewRejectCount()).orElse(0);
            count++;
            filingMaterials.setArchiveReviewRejectCount(count);
            messageResult = String.format(text, count, message);
            oldReason = filingMaterials.getArchiveReviewRejectReasonSum();
            filingMaterials.setArchiveReviewRejectReasonSum(StrUtil.isEmpty(oldReason) ? messageResult : oldReason +";" + messageResult);
        } else {
            return;
        }
        this.updateById(filingMaterials);
    }

    /**
     * 获取对应模板下的最新审批通过的版本
     * @param mainId 关联id
     * @param module 关联模块
     * @return
     */
    public CommonVersion getLastVersion(Long mainId,String module){
        LambdaQueryWrapper<CommonVersion> query = Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, module)
                .eq(CommonVersion::getType, VersionTypeEnum.APPROVAL.getType())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1");
        return commonVersionMapper.selectOne(query);
    }

    /**
     * 计算指定节点总耗时（仅工作日，天）：跨天才计数，同一天不计
     *
     * @param processInstanceId 流程实例 ID
     * @param activityId        节点ID（BPMN中定义的id）
     * @return 该节点所有执行实例的总工作日耗时
     */
    public long calculateNodeTotalWorkDayDuration(String processInstanceId, String activityId) {
        // 1. 参数校验
        if (processInstanceId == null || processInstanceId.trim().isEmpty()) {
            throw new MithrasException("流程实例ID 不能为空");
        }
        if (activityId == null || activityId.trim().isEmpty()) {
            throw new MithrasException("节点ID 不能为空");
        }

        // 2. 查询指定流程实例+节点的所有已完成活动实例
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityId(activityId)
                .finished() // 仅查已完成的节点（有开始/结束时间）
                .list();

        if (activityInstances.isEmpty()) {
            return 0L; // 无执行记录，返回0
        }

        // 3. 累加所有实例的工作日耗时（核心逻辑）
        long totalWorkDays = 0L;
        for (HistoricActivityInstance instance : activityInstances) {
            Date startTime = instance.getStartTime();
            Date endTime = instance.getEndTime();

            // 防御性判断：避免空指针
            if (startTime == null || endTime == null) {
                continue;
            }

            // 3.1 将Date转换为LocalDate（忽略时分秒，仅保留日期）
            LocalDate startLocalDate = convertToLocalDate(startTime);
            LocalDate endLocalDate = convertToLocalDate(endTime);

            // 3.2 仅跨天时计算工作日，同一天则计为0
            if (!startLocalDate.isEqual(endLocalDate)) {
                // 调用项目内置方法计算两个日期间的工作日数
                long workDays = baseDataSpecialDateService.calculateWorkDays(startLocalDate, endLocalDate);
                // 确保耗时为非负数（避免方法返回异常值）
                totalWorkDays += Math.max(workDays, 0);
            }
            // 同一天：不执行任何操作，耗时计为0
        }
        return totalWorkDays;
    }

    /**
     * 辅助方法：将Flowable的Date转换为LocalDate（指定时区，忽略时分秒）
     *
     * @param date Flowable返回的时间（startTime/endTime）
     * @return 对应的LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // Date -> Instant -> ZonedDateTime -> LocalDate（自动忽略时分秒）
        return date.toInstant()
                .atZone(ZONE_ID)
                .toLocalDate();
    }

    /**
     * 资料拷贝
     * @param contractBaseInfo 合同信息
     * @param id 申请
     * @param ctrVersion 当前时点合同审批通过的版本
     */
    private void filingMaterialCopyFile(ContractBaseInfo contractBaseInfo, Long id, String ctrVersion) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        /*获取项目评审信息*/
        ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(contractBaseInfo.getProjReviewId());
        /*2.1基础资料*/
        copyBasicInformation(contractBaseInfo, projReviewBaseInfo, id, ctrVersion);
        /*2.2内部操作资料*/
        copyInnerOperation(contractBaseInfo, projReviewBaseInfo, id);
        /*2.3合同资料*/
        copyPaymentInfo(contractBaseInfo, id);
        /*2.4租赁物资料*/
        copyLeaseholdFile(contractBaseInfo, id);
        /*2.5抵质押资料*/
        copyCollateralization(contractBaseInfo, id);
        stopWatch.stop();
        log.info("项目资料归档推送待办资料拷贝 end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
    }

    /**
     * 通过项目评审id获取项目评审信息
     * @param projReviewId 项目评审id
     * @return ProjReviewBaseInfo
     */
    public ProjReviewBaseInfo getProjReviewBaseInfo(Long projReviewId) {
        return SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(projReviewId);
    }

    /**
     * 拷贝当前项目评审版本的交易主体资料及按照当前合同版本下的交易主体生成交易主体对应的基础资料清单
     * @param contractBaseInfo 合同信息
     * @param projReviewBaseInfo 项目评审信息
     * @param filingMaterialsId 项目资料归档表id
     * @param version 当前时点审批通过合同版本id
     */
    private void copyBasicInformation(ContractBaseInfo contractBaseInfo, ProjReviewBaseInfo projReviewBaseInfo, Long filingMaterialsId, String ctrVersion) {
        /*查询项目评审下承租人、担保人资料*/
        List<MaterialsList> fileList = materialsListService.list(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW_CLIENT.name())
                        .eq(MaterialsList::getBelongId, projReviewBaseInfo.getId()));
        if (CollUtil.isNotEmpty(fileList)) {
            this.copyProjectFile(fileList, filingMaterialsId, BusinessModuleEnum.BUSINESS_REVIEW_CLIENT.name(), null);
        }
        /*生成运营终审材料，按交易结构选择不同的模板*/
        FilingBaseResDTO filingBaseResDTO = initOperationBasicInformation(filingMaterialsId, contractBaseInfo.getId(), ctrVersion);
        //归档资料-自然人拷贝参考资料下的资料
        Set<Long> indIds = filingBaseResDTO.getIndIds();
        if(CollUtil.isNotEmpty(fileList) && CollUtil.isNotEmpty(indIds)){
            List<MaterialsList> indFileList = fileList.stream().filter(e -> Objects.nonNull(e.getSourceBusinessKey())
                    && indIds.contains(Long.valueOf(e.getSourceBusinessKey()))).collect(toList());
            if (CollUtil.isNotEmpty(indFileList)) {
                indFileList.stream().forEach(e -> {
                    if (CharSequenceUtil.equals(e.getMaterialsType(), FilingMaterialsConstants.BASIC_INFORMATION)) {
                        e.setMaterialsType(FilingMaterialsConstants.IND_BASIC_INFORMATION);
                    }
                    if (CharSequenceUtil.equals(e.getMaterialsType(), FilingMaterialsConstants.OTHERS)) {
                        e.setMaterialsType(FilingMaterialsConstants.OTHER);
                    }
                });
                this.copyProjectFile(indFileList, filingMaterialsId, BusinessModuleEnum.FILING_BUSINESS_CLIENT.name(), null);
            }
        }
    }

    /**
     * 拷贝内部操作资料
     * @param contractBaseInfo 合同信息
     * @param projReviewBaseInfo 项目评审信息
     * @param id 申请id
     */
    private void copyInnerOperation(ContractBaseInfo contractBaseInfo, ProjReviewBaseInfo projReviewBaseInfo, Long id) {
        HashMap<String, List<MaterialsList>> stringListHashMap = new HashMap<>();
        /*立项资料*/
        getEstablishMaterials(stringListHashMap, projReviewBaseInfo);
        /*定价资料*/
        getProjectPricingMaterials(stringListHashMap, projReviewBaseInfo);
        /*项目评审资料*/
        getProjectReviewMaterials(stringListHashMap, projReviewBaseInfo);
        /*租赁物资料*/
        getProjectLeasehold(stringListHashMap, projReviewBaseInfo);
        /*放款审核资料*/
        getPaymentMaterials(stringListHashMap, contractBaseInfo);
        /*拷贝资料*/
        for (Map.Entry<String, List<MaterialsList>> entry : stringListHashMap.entrySet()) {
            List<MaterialsList> fileList = entry.getValue();
            this.copyProjectFile(fileList, id, entry.getKey(), null);
        }
        /*公开信息查询资料*/
        generatePaymentPublicInformationMaterials(contractBaseInfo, id);
        /*流程审批截图*/
        generateFlowApproveImg(contractBaseInfo, projReviewBaseInfo, id);
        /*生成运维终审-基础资料*/
        initInnerOperationBasicInformation(contractBaseInfo.getId(), id, BusinessMaterialsDocNameEnum.BUSINESS_INNER_OPERATION);
    }

    /**
     * 拷贝立项资料
     * @param stringListHashMap
     * @param projReviewBaseInfo
     */
    private void getEstablishMaterials(HashMap<String, List<MaterialsList>> stringListHashMap, ProjReviewBaseInfo projReviewBaseInfo) {
        /*1、合同项下关联的项目为集团用信项目时获取关联的授信立项流程,*/
        String relationDataType = projReviewBaseInfo.getRelationDataType();
        /*集团授信评审id*/
        Long groupCreditReviewId = projReviewBaseInfo.getGroupCreditReviewId();
        Long projEstablishId = projReviewBaseInfo.getProjEstablishId();
        List<MaterialsList> fileList = null;
        if (GROUP_CREDIT_REVIEW.name().equals(relationDataType) && Objects.nonNull(groupCreditReviewId)) {
            /*通过授信评审id获取授信立项信息*/
            GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(groupCreditReviewId);
            //授信立项
            GroupCreditEstablishBaseInfo groupCreditEstablishBaseInfo = SpringUtil.getBean(GroupCreditEstablishBaseInfoService.class).getById(baseInfo.getGroupCreditEstablishId());
            if (Objects.nonNull(groupCreditEstablishBaseInfo)) {
                List<String> materialEnumList = GroupCreditEstablishMaterialsEnum.listAll();
                materialEnumList.removeIf(e -> CharSequenceUtil.equals(e, GroupCreditEstablishMaterialsEnum.PROJ_INFORMATION.name()));
                fileList = getMaterialsListByVersion(groupCreditEstablishBaseInfo.getId(), new ArrayList<>(materialEnumList),
                        BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name());
            }

        } else if (Objects.nonNull(projEstablishId)) {
            /*非集团授信项目查项目立项流程*/
            ProjEstablishBaseInfo projEstablishBaseInfo = SpringUtil.getBean(ProjEstablishBaseInfoService.class).getById(projReviewBaseInfo.getProjEstablishId());
            if (Objects.nonNull(projEstablishBaseInfo)) {
                List<String> mergeList = new ArrayList<>(ProjEstablishMaterialsEnum.listAll());
                mergeList.addAll(ProjEstablishMaterialsApproveEnum.listAll());
                //排除业务申请书、征信授权书
                mergeList.removeIf(e -> CharSequenceUtil.equalsAny(e, ProjEstablishMaterialsEnum.PROJ_INFORMATION.name(), ProjEstablishMaterialsEnum.PROJ_CREDIT_LETTER.name()));
                fileList = getMaterialsListByVersion(projEstablishBaseInfo.getId(), mergeList, BusinessModuleEnum.PROJ_ESTABLISH.name());
            }
        }
        if (CollUtil.isNotEmpty(fileList)) {
            stringListHashMap.put(BusinessModuleEnum.BUSINESS_PROJ_ESTABLISH.name(), fileList);
        }
    }

    /**
     * 拷贝项目定价资料
     * @param stringListHashMap
     * @param projReviewBaseInfo
     */
    private void getProjectPricingMaterials(HashMap<String, List<MaterialsList>> stringListHashMap, ProjReviewBaseInfo projReviewBaseInfo) {
        ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(projReviewBaseInfo.getId());
        if (Objects.nonNull(projPricingBaseInfo)) {
            List<MaterialsList> fileList = getMaterialsListByVersion(projPricingBaseInfo.getId(), Arrays.asList(ProjPricingMaterialsEnum.BUSINESS_PRICING_APPROVAL_FORM.name()),
                    BusinessModuleEnum.PROJ_PRICING.name());
            if (CollUtil.isNotEmpty(fileList)) {
                stringListHashMap.put(BusinessModuleEnum.BUSINESS_PROJ_PRICING.name(), fileList);
            }
        }
    }

    /**
     * 拷贝项目评审资料
     * @param stringListHashMap
     * @param projReviewBaseInfo
     */
    private void getProjectReviewMaterials(HashMap<String, List<MaterialsList>> stringListHashMap, ProjReviewBaseInfo projReviewBaseInfo) {
        List<MaterialsList> fileList = getMaterialsListByVersion(projReviewBaseInfo.getId(), new ArrayList<>(ProjReviewMaterialsEnum.listAll()),
                BusinessModuleEnum.PROJ_REVIEW.name());
        List<VisitRecord> visitRecordList = visitRecordMapper.selectList(
                Wrappers.<VisitRecord>lambdaQuery()
                        .in(VisitRecord::getProjCode, projReviewBaseInfo.getProjCode())
                        .eq(VisitRecord::getStatus, VisitRecordStatus.PASSED.name())
                        .eq(VisitRecord::getDeleted, 0)
                        .orderByDesc(VisitRecord::getCheckInDate));
        if (!visitRecordList.isEmpty()) {
            Set<Long> visitIds = visitRecordList.stream().map(VisitRecord::getId).collect(Collectors.toSet());
            List<MaterialsList> dataList = materialsListService.getBaseMapper().selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, "VISIT_RECORD")
                    .in(MaterialsList::getBelongId, visitIds)
                    .eq(MaterialsList::getMaterialsType, VisitPhaseStatus.ON_SITE_DUE_DILIGENCE.name())
            );
            for (VisitRecord visitRecord : visitRecordList) {
                for (MaterialsList materialsList : dataList) {
                    if (visitRecord.getId().equals(materialsList.getBelongId())) {
                        materialsList.setLocation(visitRecord.getCheckInLocation());
                    }
                }
            }
            fileList.addAll(dataList);
        }
        if (CollUtil.isNotEmpty(fileList)) {
            stringListHashMap.put(BusinessModuleEnum.BUSINESS_PROJ_REVIEW.name(), fileList);
        }
    }

    /**
     * 拷贝租赁物资料
     * @param stringListHashMap
     * @param projReviewBaseInfo
     */
    private void getProjectLeasehold(HashMap<String, List<MaterialsList>> stringListHashMap, ProjReviewBaseInfo projReviewBaseInfo) {
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getNewestOne(projReviewBaseInfo.getId());
        if (Objects.nonNull(leaseItemInfo)) {
            List<MaterialsList> fileList = getMaterialsListByBelongIds(Arrays.asList(leaseItemInfo.getId()), Arrays.asList(LeaseFileTypeEnums.OPERATION_MANAGER_REVIEW_SUBMISSION.name()),
                    BusinessModuleEnum.LEASE_DATA_LIST.name());
            if (CollUtil.isNotEmpty(fileList)) {
                stringListHashMap.put(BusinessModuleEnum.BUSINESS_LEASE_DATA_LIST.name(), fileList);
            }
        }

    }

    /**
     * 拷贝放款审核资料
     * @param stringListHashMap
     * @param contractBaseInfo
     */
    private void getPaymentMaterials(HashMap<String, List<MaterialsList>> stringListHashMap, ContractBaseInfo contractBaseInfo) {
        List<PaymentBaseInfo> paymentBaseInfoList = getPassPaymentBaseInfoList(contractBaseInfo);
        Assert.notEmpty(paymentBaseInfoList, () -> MithrasException.newException("未找到付款审批通过流程"));
        List<Long> ids = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(toList());
        List<MaterialsList> fileList = getMaterialsListByBelongIds(ids, Arrays.asList(PaymentTypeEnum.LOAN_REVIEW.name()),
                BusinessModuleEnum.PAYMENT.name());
        if (CollUtil.isNotEmpty(fileList)) {
            stringListHashMap.put(BusinessModuleEnum.BUSINESS_PAYMENT_LOAN_APPROVAL.name(), fileList);
        }
    }

    private void generatePaymentPublicInformationMaterials(ContractBaseInfo contractBaseInfo, Long id){
        /*生成zip,上传文件服务器*/
        List<PaymentBaseInfo> paymentBaseInfoList = getPassPaymentBaseInfoList(contractBaseInfo);
        Assert.notEmpty(paymentBaseInfoList, () -> MithrasException.newException("未找到付款审批通过流程"));
        paymentBaseInfoList.forEach(paymentBaseInfo -> getPaymentPublicInformationMaterials(paymentBaseInfo,contractBaseInfo,id));
    }

    /**
     * 下载付款申请流程中的公开信息查询资料
     * @param contractBaseInfo 合同信息
     * @param id 申请id
     */
    public void getPaymentPublicInformationMaterials(PaymentBaseInfo paymentBaseInfo,ContractBaseInfo contractBaseInfo, Long id) {
        /*生成zip,上传文件服务器*/
        // 查找基本信息
        List<PublicInfoQuery> publicInfoQueryList = publicInfoQueryService.list(Wrappers.<PublicInfoQuery>lambdaQuery().eq(PublicInfoQuery::getPaymentId, paymentBaseInfo.getId()));
        if (CollUtil.isEmpty(publicInfoQueryList)) {
            return;
        }
        // 找到所有行记录
        List<PublicInfoRecord> publicInfoRecordList = publicInfoRecordService.list(Wrappers.<PublicInfoRecord>lambdaQuery().in(PublicInfoRecord::getPublicInfoQueryId, publicInfoQueryList.stream().map(PublicInfoQuery::getId).collect(Collectors.toList())));
        if (CollUtil.isEmpty(publicInfoRecordList)) {
            return;
        }
        // 收集所有可能用到的clientId
        List<Long> clientIdList = publicInfoQueryList.stream().map(PublicInfoQuery::getClientId).distinct().collect(Collectors.toList());
        List<Long> userIdList = new LinkedList<>();
        userIdList.addAll(publicInfoQueryList.stream().map(PublicInfoQuery::getConfirmedBy).collect(Collectors.toList()));
        userIdList.addAll(publicInfoQueryList.stream().map(PublicInfoQuery::getCreateBy).collect(Collectors.toList()));
        userIdList.addAll(publicInfoRecordList.stream().map(PublicInfoRecord::getUpdateBy).collect(Collectors.toList()));
        Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(clientIdList);
        Map<Long, String> userId2NameMap = id2NameService.sysUserId2Name(userIdList);
        // 将行记录分组
        Map<Long, Map<String, PublicInfoRecord>> recordGroupMap = new HashMap<>();
        Map<Long, Map<Long, PublicInfoRecord>> recordIdMap = new HashMap<>();
        Map<Long, List<PublicInfoRecord>> recordMap = publicInfoRecordList.stream().collect(Collectors.groupingBy(PublicInfoRecord::getPublicInfoQueryId));
        recordMap.forEach((key, value) -> {
            recordGroupMap.put(key, value.stream().collect(Collectors.toMap(PublicInfoRecord::getConfigKey, Function.identity(), (v1, v2) -> v1)));
            recordIdMap.put(key, value.stream().collect(Collectors.toMap(PublicInfoRecord::getId, Function.identity(), (v1, v2) -> v1)));
        });
        //下载
        String zipName = paymentBaseInfo.getPaymentCode() + "-公开信息-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN)) + ".zip";
        String localFilePath = "/tmp" + zipName;
        try (OutputStream zipOs = FileUtil.getOutputStream(localFilePath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(zipOs, StandardCharsets.UTF_8)) {
            Map<Long, List<PublicInfoQuery>> listMap = publicInfoQueryList.stream().collect(Collectors.groupingBy(PublicInfoQuery::getClientId));
            // 定义一个root路径, 单个用户的所有时间段的文件全部放在这里面
            String path = "公开信息报告";
            for (Map.Entry<Long, List<PublicInfoQuery>> entry : listMap.entrySet()) {
                Long clientId = entry.getKey();
                List<PublicInfoQuery> queries = entry.getValue();
                String clientTypeDisplay = Optional.ofNullable(PublicInfoClientTypeEnum.find(queries.get(0).getClientType())).map(PublicInfoClientTypeEnum::getDisplay).orElse("未知类型");
                String rootPath = path + File.separator + Optional.ofNullable(clientId2NameMap.get(clientId)).map(obj -> obj + "-" + clientTypeDisplay).orElse("未知用户");
                for (PublicInfoQuery query : queries) {
                    // 创建子文件路径，界面报表放在该路径下 /${用户名-用户类型}/开始时间-结束时间
                    String intervalPath = rootPath + File.separator + query.getQueryFrom().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN)) +
                            "~" + query.getQueryTo().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN));
                    // 现在已经来到了单个表格的地方， 先构建Excel文件
                    Map<String, PublicInfoRecord> stringPublicInfoRecordMap = recordGroupMap.get(query.getId());
                    PublicInfoExcelModel publicInfoExcelModel = buildExcel(query, userId2NameMap, clientId2NameMap, stringPublicInfoRecordMap);
                    // 根据不同的客户类型选择不同的导出模版
                    PublicInfoClientTypeEnum clientTypeEnum = PublicInfoClientTypeEnum.find(query.getClientType());
                    if (Objects.isNull(clientTypeEnum)) {
                        throw MithrasException.newException("公开信息导出模版不存在");
                    }
                    InputStream templateFile;
                    switch (clientTypeEnum) {
                        case TENANTRY: {
                            templateFile = this.getClass().getResourceAsStream("/doc/公开信息查询报告-承租人.xlsx");
                            break;
                        }
                        case GUARANTOR: {
                            templateFile = this.getClass().getResourceAsStream("/doc/公开信息查询报告-担保人.xlsx");
                            break;
                        }
                        default:
                            throw MithrasException.newException("公开信息导出模版不存在");
                    }
                    try (ByteArrayOutputStream excelOut = new ByteArrayOutputStream();
                         InputStream finalTemplateFile = templateFile) {
                        EasyExcelFactory.write(excelOut)
                                .withTemplate(finalTemplateFile)
                                .sheet(0)
                                .doFill(publicInfoExcelModel);

                        // 写入Excel到ZIP
                        zipOutputStream.putNextEntry(new ZipEntry(intervalPath + File.separator + "公开信息查询报告.xlsx"));
                        excelOut.writeTo(zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                    if (CollUtil.isEmpty(stringPublicInfoRecordMap)) {
                        continue;
                    }
                    // 将minio上的文件下载下来打包, 找到所有的文件记录并根据记录ID分组
                    Map<Long, Map<String, List<FileListRSP>>> fileListMap = publicInfoQueryService.queryFileList(stringPublicInfoRecordMap);
                    // 遍历文件记录
                    for (Map.Entry<Long, Map<String, List<FileListRSP>>> fileEntry : fileListMap.entrySet()) {
                        Long recordId = fileEntry.getKey();
                        PublicInfoRecord publicInfoRecord = recordIdMap.get(query.getId()).get(recordId);
                        Assert.notNull(publicInfoRecord, "公开信息记录不存在");
                        Map<String, List<FileListRSP>> map = fileEntry.getValue();
                        for (Map.Entry<String, List<FileListRSP>> fileListEntry : map.entrySet()) {
                            // 这一层是小格子
                            List<FileListRSP> fileList = fileListEntry.getValue();
                            String currentPath;
                            if (Objects.equals(fileListEntry.getKey(), publicInfoRecord.getConfigKey() + "_YY")) {
                                currentPath = intervalPath + File.separator +
                                        Optional.ofNullable(PublicInfoFileTypeEnum.find(publicInfoRecord.getConfigKey() + "_YY"))
                                                .map(PublicInfoFileTypeEnum::getDisplay).orElse("") + File.separator + "运营经办";
                            } else {
                                currentPath = intervalPath + File.separator +
                                        Optional.ofNullable(PublicInfoFileTypeEnum.find(publicInfoRecord.getConfigKey() + "_XMJL"))
                                                .map(PublicInfoFileTypeEnum::getDisplay).orElse("") + File.separator + "项目经理";
                            }
                            // 遍历文件列表，下载文件，并写入到zip中
                            for (int i = 0; i < fileList.size(); i++) {
                                // 这里可能文件太大，单个流是放不下的所以每个小框的单独开一个流，放到一个列表里面，最后再写到zip
                                FileListRSP fileListRsp = fileList.get(i);
                                try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
                                    // 下载文件
                                    ossClient.downLoad(arrayOutputStream, fileListRsp.getOssFilename());
                                    zipOutputStream.putNextEntry(new ZipEntry(currentPath + File.separator + i + "-" + fileListRsp.getFilename()));
                                    arrayOutputStream.writeTo(zipOutputStream);
                                    zipOutputStream.closeEntry();
                                }
                            }
                        }
                    }
                }
            }
            zipOutputStream.finish();
            zipOutputStream.flush();
            try (InputStream zipIn = FileUtil.getInputStream(localFilePath)) {
                materialsListService.add(
                        zipIn,
                        zipName,
                        id,
                        FilingDirectoryEnum.PUBLIC_INFORMATION_QUERY.name(),
                        "",
                        BusinessModuleEnum.BUSINESS_PUBLIC_INFORMATION_QUERY.name(),
                        YesOrNoNumberEnum.YES
                );
            }

        } catch (IOException e) {
            log.error("导出公开信息异常", e);
            throw MithrasException.newException(e.getMessage());
        } finally {
            if (FileUtil.exist(localFilePath)) {
                boolean deleteSuccess = FileUtil.del(localFilePath);
                if (!deleteSuccess) {
                    log.warn("临时ZIP文件删除失败：{}", localFilePath);
                }
            }

        }
    }

    private PublicInfoExcelModel buildExcel(PublicInfoQuery query, Map<Long, String> userId2NameMap,
                                            Map<Long, String> clientId2NameMap,
                                            Map<String, PublicInfoRecord> stringPublicInfoRecordMap) {
        // 构建模版填充实体类
        PublicInfoExcelModel excelModel = PublicInfoExcelModel.builder()
                .confirmedName(userId2NameMap.get(query.getConfirmedBy()))
                .tenantryName(clientId2NameMap.get(query.getClientId()))
                .queryFrom(query.getQueryFrom().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .queryTo(query.getQueryTo().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)))
                .confirmedTime(Optional.ofNullable(query.getConfirmedTime()).map(o -> o.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))).orElse(null))
                .build();
        // 遍历使用反射填充数据，因为比较通用
        stringPublicInfoRecordMap.forEach((configKey, publicInfoRecord) -> {
            // 根据configKey 找到对应的枚举，然后填充数据
            ReflectUtil.setFieldValue(excelModel, configKey + "_ENUM", Optional.ofNullable(InvestigationResultEnum.find(publicInfoRecord.getInvestigationType())).map(InvestigationResultEnum::getDisplay).orElse(null));
            ReflectUtil.setFieldValue(excelModel, configKey + "_YY", publicInfoRecord.getInvestigationExplain());
            ReflectUtil.setFieldValue(excelModel, configKey + "_XMJL", publicInfoRecord.getProjectmanagerExplain());
        });
        return excelModel;
    }

    /**
     * 获取流程审批历史生成审批快照.pdf
     * @param contractBaseInfo
     * @param projReviewBaseInfo
     * @param id
     */
    private void generateFlowApproveImg(ContractBaseInfo contractBaseInfo, ProjReviewBaseInfo projReviewBaseInfo, Long id) {
        List<ProcessResp> projectApprovedProcess = getProjectApprovedProcess(contractBaseInfo, projReviewBaseInfo);
        /*生成的审批历史按业务阶段排序*/
        Map<String, Integer> sortMap = DirConditionKeyEnum.getSortMap();
        projectApprovedProcess.sort(Comparator.comparing((ProcessResp p) -> sortMap.getOrDefault(p.getModelKey(), 0))
                .thenComparing(ProcessResp::getModelKey)
                .thenComparing(ProcessResp::getEndTime, Comparator.nullsFirst(Comparator.naturalOrder())));
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        for (ProcessResp processResp : projectApprovedProcess) {
            String processInstanceId = processResp.getProcessInstanceId();
            String modelName = FlowUtil.convertModelName(processResp.getModelKey());
            /*获取审批历史*/
            List<ProcessHistoryRSP> processHis = super.getProcessHis(processInstanceId);
            stringObjectHashMap.put(FilingMaterialsConstants.PROCESS_INSTANCE_ID, processInstanceId);
            stringObjectHashMap.put(FilingMaterialsConstants.MODEL_NAME, modelName);
            stringObjectHashMap.put(FilingMaterialsConstants.LIST, processHis);
            /*填充模板*/
            generateApprovalSnapshot(stringObjectHashMap, id);
        }
    }


    /**
     * 获取合同项下审批通过的各业务阶段
     * @param contractBaseInfo 合同信息
     * @param projReviewBaseInfo 项目评审信息
     * @return
     */
    public List<ProcessResp> getProjectApprovedProcess(ContractBaseInfo contractBaseInfo, ProjReviewBaseInfo projReviewBaseInfo) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setSortType(1);
        processPageReq.setPageSize(999);
        /*一键通过、审批通过*/
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.PASS_ALL.getType(), ProcessBusinessStatusEnum.PASS.getType()));
        List<ProcessResp> contents = new ArrayList<>();
        Long groupCreditReviewId = projReviewBaseInfo.getGroupCreditReviewId();
        /*授信立项流程单独查询*/
        if (Objects.nonNull(groupCreditReviewId)) {
            GroupCreditReviewBaseInfo baseInfo = groupCreditReviewBaseInfoMapper.selectById(groupCreditReviewId);
            processPageReq.setBusinessKey(String.valueOf(groupCreditReviewId));
            processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.GroupCreditReviewModifyFlow.name(), ProcessModelTypeEnum.GroupCreditReviewCreateFlow.name()));
            contents.addAll(taskApiService.queryProcess(processPageReq).getContents());
            processPageReq.setBusinessKey(String.valueOf(baseInfo.getGroupCreditEstablishId()));
            processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.GroupCreditEstablishCreateFlow.name(), ProcessModelTypeEnum.GroupCreditEstablishModifyFlow.name()));
            contents.addAll(taskApiService.queryProcess(processPageReq).getContents());
        }
        /*合同*/
        processPageReq.setBusinessKey(String.valueOf(contractBaseInfo.getId()));
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ContractModifyFlow.name(), ProcessModelTypeEnum.ContractCreateFlow.name()));
        contents.addAll(taskApiService.queryProcess(processPageReq).getContents());
        /*付款申请*/
        processPageReq.setBusinessKey(null);
        List<PaymentBaseInfo> paymentBaseInfoList = getPassPaymentBaseInfoList(contractBaseInfo);
        if(CollUtil.isNotEmpty(paymentBaseInfoList)){
            List<String> ids = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).map(String::valueOf).collect(toList());
            processPageReq.setBusinessKeyList(ids);
            processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.PaymentCreateFlow.name()));
            contents.addAll(taskApiService.queryProcess(processPageReq).getContents());
        }
        /*项目定价、项目立项、项目评审、租赁物*/
        processPageReq.setModelKeyList(Arrays.asList(ProcessModelTypeEnum.ProjReviewPricingModifyApprovalFlow.name(),
                ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name(),
                ProcessModelTypeEnum.ProjEstablishCreateFlow.name(), ProcessModelTypeEnum.ProjEstablishModifyFlow.name(),
                ProcessModelTypeEnum.ProjReviewCreateFlow.name(), ProcessModelTypeEnum.ProjReviewModifyFlow.name(),
                ProcessModelTypeEnum.LeaseModifyFlow.name(), ProcessModelTypeEnum.LeaseCreateFlow.name()));
        processPageReq.setBusinessKeyList(null);
        ProcessTaskExtra processTaskExtra = new ProcessTaskExtra();
        processTaskExtra.setProjCode(projReviewBaseInfo.getProjCode());
        processPageReq.setQueryExtraCondition(bizProcessDataService.extraQueryCondition(processTaskExtra, "t1"));
        contents.addAll(taskApiService.queryProcess(processPageReq).getContents());
        /*流程下获取最新审批通过的数据*/

        Map<String, List<ProcessResp>> map = contents.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        ProcessResp::getModelKey,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String modelKey = entry.getKey();
                            List<ProcessResp> respList = entry.getValue();
                            /*多个付款申请则保留*/
                            if (ProcessModelTypeEnum.PaymentCreateFlow.name().equals(modelKey)) {
                                return respList;
                            } else {
                                return respList.isEmpty()
                                        ? Collections.emptyList()
                                        : Collections.singletonList(respList.get(0));
                            }
                        },
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
        return map.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * 拷贝合同资料
     *
     * @param contractBaseInfo
     * @param filingMaterialsId
     */
    private void copyPaymentInfo(ContractBaseInfo contractBaseInfo, Long filingMaterialsId) {
        List<PaymentBaseInfo> paymentBaseInfoList = getPassPaymentBaseInfoList(contractBaseInfo);
        Assert.notEmpty(paymentBaseInfoList, () -> MithrasException.newException("未找到付款审批通过流程"));
        List<Long> ids = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(toList());
        List<MaterialsList> fileList = getMaterialsListByBelongIds(ids, Arrays.asList(LendingMaterialType.SIGN_PHOTO_VIDEO.name()),
                BusinessModuleEnum.PAYMENT.name());
        if (CollUtil.isNotEmpty(fileList)) {
            this.copyProjectFile(fileList, filingMaterialsId, BusinessModuleEnum.BUSINESS_PAYMENT.name(), null);
            //归档资料拷贝合同签署照片和视频
            this.copyProjectFile(fileList, filingMaterialsId, BusinessModuleEnum.FILING_BUSINESS_PAYMENT.name(), null);
        }
        /*生成运维终审-基础资料*/
        initOperationNotBasicInformation(contractBaseInfo.getId(), filingMaterialsId, BusinessMaterialsDocNameEnum.BUSINESS_CONTRACT, BusinessModuleEnum.FILING_BUSINESS_PAYMENT.name());
    }

    /**
     * 拷贝租赁物资料
     * @param contractBaseInfo
     * @param filingMaterialsId
     */
    private void copyLeaseholdFile(ContractBaseInfo contractBaseInfo, Long filingMaterialsId) {
        List<PaymentBaseInfo> paymentBaseInfoList = getPassPaymentBaseInfoList(contractBaseInfo);
        Assert.notEmpty(paymentBaseInfoList, () -> MithrasException.newException("未找到付款审批通过流程"));
        List<Long> ids = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(toList());
        List<MaterialsList> fileList = getMaterialsListByBelongIds(ids,
                Arrays.asList(LendingMaterialType.PUBLICITY_INFORMATION.name(), LendingMaterialType.LEASE_RELATED.name()),
                BusinessModuleEnum.PAYMENT.name());
        if (CollUtil.isNotEmpty(fileList)) {
            this.copyProjectFile(fileList, filingMaterialsId, BusinessModuleEnum.BUSINESS_LEASEHOLD.name(), null);
            this.copyProjectFile(fileList, filingMaterialsId, BusinessModuleEnum.FILING_BUSINESS_LEASEHOLD.name(), null);
        }
        /*拷贝付款申请流程中的保单附件*/
        List<PaymentPolicyInfo> paymentPolicyInfos = SpringUtil.getBean(PaymentPolicyInfoMapper.class).selectList(
                Wrappers.<PaymentPolicyInfo>lambdaQuery()
                        .in(PaymentPolicyInfo::getPaymentId, ids));
        if (CollUtil.isNotEmpty(paymentPolicyInfos)) {
            List<Long> policyIds = paymentPolicyInfos.stream().map(PaymentPolicyInfo::getId).collect(Collectors.toList());
            List<MaterialsList> policyFileList = getMaterialsListByBelongIds(policyIds, Arrays.asList("POLICY"), "PAYMENTPOLICY");
            if (CollUtil.isNotEmpty(policyFileList)) {
                this.copyProjectFile(policyFileList, filingMaterialsId, BusinessModuleEnum.BUSINESS_LEASEHOLD_POLICY.name(), null);
                this.copyProjectFile(policyFileList, filingMaterialsId, BusinessModuleEnum.FILING_BUSINESS_LEASEHOLD.name(), null);
            }
        }
        /*生成运维终审-基础资料*/
        initOperationNotBasicInformation(contractBaseInfo.getId(), filingMaterialsId,
                BusinessMaterialsDocNameEnum.BUSINESS_LEASEHOLD, BusinessModuleEnum.FILING_BUSINESS_LEASEHOLD.name());
    }

    /**
     * 抵质押资料
     * @param contractBaseInfo 合同信息
     * @param id 申请id
     */
    private void copyCollateralization(ContractBaseInfo contractBaseInfo, Long id) {
        CommonVersion commonVersion = getLastVersion(contractBaseInfo.getId(),BusinessModuleEnum.CONTRACT.name());
        String ctrVersion = commonVersion.getVersion();
        /*是否存在最近一次审批通过的合同创建/合同其他变更流程内“抵押措施/质押措施”内存在担保*/
        List<ContractMortgageLib> contractMortgages = SpringUtil.getBean(ContractMortgageLibMapper.class).selectList(Wrappers.<ContractMortgageLib>lambdaQuery()
                .eq(ContractMortgageLib::getContractId, contractBaseInfo.getId())
                .eq(ContractMortgageLib::getVersion,ctrVersion));
        List<ContractPledgeLib> contractPledgeList = SpringUtil.getBean(ContractPledgeLibMapper.class).selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                .eq(ContractPledgeLib::getContractId, contractBaseInfo.getId())
                .eq(ContractPledgeLib::getVersion,ctrVersion));
        int flag = YesOrNoNumberEnum.YES.getCode();
        if (CollectionUtils.isEmpty(contractPledgeList) && CollectionUtils.isEmpty(contractMortgages)) {
            flag = YesOrNoNumberEnum.NO.getCode();
        }
        /*更新是否存在抵质押措施作为担保*/
        LambdaUpdateWrapper<FilingMaterials> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(FilingMaterials::getGuaranteeFlag, flag);
        updateWrapper.eq(FilingMaterials::getId, id);
        this.update(updateWrapper);

        if (Objects.equals(YesOrNoNumberEnum.YES.getCode(), flag)) {
            initOperationNotBasicInformation(contractBaseInfo.getId(), id, BusinessMaterialsDocNameEnum.BUSINESS_COLLATERALIZATION,
                    BusinessModuleEnum.FILING_BUSINESS_COLLATERALIZATION.name());
        }
    }

    /**
     * 获取最新审批通过的付款申请流程
     * @param contractBaseInfo
     * @return
     */
    private List<PaymentBaseInfo> getPassPaymentBaseInfoList(ContractBaseInfo contractBaseInfo) {
        return SpringUtil.getBean(PaymentBaseInfoMapper.class).selectList(
                Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .eq(PaymentBaseInfo::getContractId, contractBaseInfo.getId())
                        .eq(PaymentBaseInfo::getPaymentProcessStatus, ProcessStatus.APPROVAL_PASS.name())
                        .orderByDesc(PaymentBaseInfo::getId)
        );
    }


    /**
     * 获取对应阶段的最新审批通过的版本号,通过版本号查询对应版本下的资料清单
     * @param belongId 所属id
     * @param materialsList 资料项
     * @param businessType 业务阶段
     * @return
     */
    private List<MaterialsList> getMaterialsListByVersion(Long belongId, List<String> materialsList, String businessType) {
        log.info("项目资料归档查询流程中各项目阶段当前最新审批通过的资料,belongId:{},businessType:{}", belongId,businessType);
        CommonVersion commonVersion = getLastVersion(belongId,businessType);
        if(Objects.isNull(commonVersion)){
            log.info("项目资料归档查询对应阶段businessType:{},belongId:{}版本失败",businessType,belongId);
            return Collections.emptyList();
        }
        log.info("项目资料归档查询流程中的资料,最新审批通过版本:{}",commonVersion.getVersion());
        List<MaterialsListLib> fileLibList = materialsListLibService.list(
                Wrappers.<MaterialsListLib>lambdaQuery()
                        .eq(MaterialsListLib::getBusinessType, businessType)
                        .in(MaterialsListLib::getMaterialsType, materialsList)
                        .eq(MaterialsListLib::getBelongId, belongId)
                        .eq(MaterialsListLib::getVersion, commonVersion.getVersion()));
        return BeanUtil.copyToList(fileLibList, MaterialsList.class);

    }

    /**
     * 直接查询资料主表
     * @param belongIds
     * @param materialsList
     * @param businessType
     * @return
     */
    private List<MaterialsList> getMaterialsListByBelongIds(List<Long> belongIds, List<String> materialsList, String businessType) {
        return materialsListService.list(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, businessType)
                        .in(MaterialsList::getMaterialsType, materialsList)
                        .in(MaterialsList::getBelongId, belongIds));
    }


    /**
     * 初始化运营终审资料-基础资料
     * @param targetId
     */
    private FilingBaseResDTO initOperationBasicInformation(Long targetId, Long contractId, String ctrVersion) {
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, Object> resHashMap = new HashMap<>();
        hashMap.put(FilingMaterialsConstants.CONTRACT_ID, contractId);
        Set<Long> lesseeIds = new HashSet<>();
        Set<Long> entIds = new HashSet<>();
        Set<Long> indIds = new HashSet<>();
        /*基本信息-归档资料交易主体取合同下审批通过的交易主体*/
        this.generateContractActualSubject(contractId, ctrVersion, lesseeIds, entIds, indIds);
        /*承租人-资料清单取承租人*/
        lesseeIds.stream().forEach(e -> {
            hashMap.put(FilingMaterialsConstants.CLIENT_ID, e);
            hashMap.put(FilingMaterialsConstants.TEMPLATE_TYPE, BusinessMaterialsDocNameEnum.BUSINESS_LESSEE_BASIC);
            generateBasicInformation(hashMap, targetId, e,
                    FilingMaterialsConstants.BASIC_INFORMATION, BusinessModuleEnum.FILING_BUSINESS_CLIENT.name());
        });
        /*法人资料清单*/
        entIds.stream().forEach(e -> {
            hashMap.put(FilingMaterialsConstants.CLIENT_ID, e);
            hashMap.put(FilingMaterialsConstants.TEMPLATE_TYPE, BusinessMaterialsDocNameEnum.BUSINESS_ENT_GUARANTOR_BASIC);
            generateBasicInformation(hashMap, targetId, e,
                    FilingMaterialsConstants.BASIC_INFORMATION, BusinessModuleEnum.FILING_BUSINESS_CLIENT.name());
        });
        /*自然人资料清单*/
        indIds.stream().forEach(e -> {
            hashMap.put(FilingMaterialsConstants.CLIENT_ID, e);
            hashMap.put(FilingMaterialsConstants.TEMPLATE_TYPE, BusinessMaterialsDocNameEnum.BUSINESS_IND_GUARANTOR_BASIC);
            generateBasicInformation(hashMap, targetId, e,
                    FilingMaterialsConstants.BASIC_INFORMATION, BusinessModuleEnum.FILING_BUSINESS_CLIENT.name());
        });
        FilingBaseResDTO filingBaseResDTO = new FilingBaseResDTO(lesseeIds, entIds, indIds);
        return filingBaseResDTO;
    }

    private void initOperationNotBasicInformation(Long contractId, Long targetId, BusinessMaterialsDocNameEnum materialsDocNameEnum, String businessType) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(FilingMaterialsConstants.CONTRACT_ID, contractId);
        hashMap.put(FilingMaterialsConstants.TEMPLATE_TYPE, materialsDocNameEnum);
        generateBasicInformation(hashMap, targetId, null, FilingMaterialsConstants.BASIC_INFORMATION, businessType);
    }

    /**
     * 生成内部操作资料清单
     * @param contractId
     * @param targetId
     * @param materialsDocNameEnum
     */
    public void initInnerOperationBasicInformation(Long contractId, Long targetId, BusinessMaterialsDocNameEnum materialsDocNameEnum) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(FilingMaterialsConstants.CONTRACT_ID, contractId);
        hashMap.put(FilingMaterialsConstants.TEMPLATE_TYPE, materialsDocNameEnum);
        Map<String, List<SelectRSP>> operationsDirDict = getOperationsDirDict(FilingMaterialsFilingTypeEnum.BUSINESS_MATERIALS.name(), targetId);
        if (!operationsDirDict.containsKey(BusinessModuleEnum.FILING_BUSINESS_INNER_OPERATION.name())) {
            throw new MithrasException("初始化内部操作资料-业务类型不存在");
        }
        hashMap.put(FilingMaterialsConstants.INNER_TABLE_LIST, operationsDirDict.get(BusinessModuleEnum.FILING_BUSINESS_INNER_OPERATION.name()));
        generateBasicInformation(hashMap, targetId, null, FilingMaterialsConstants.BASIC_INFORMATION, BusinessModuleEnum.FILING_BUSINESS_INNER_OPERATION.name());
    }

    private void generateBasicInformation(HashMap hashMap, Long belongId, Long clientId, String materialsType, String businessType) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String fileName;
            if (hashMap.containsKey(FilingMaterialsConstants.INNER_TABLE_LIST)) {
                fileName = innerOperationRender.render(os, hashMap);
            } else {
                fileName = businessMaterialsRender.render(os, hashMap);
            }
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalStateException("合同渲染生成的文件名不能为空");
            }
            // 4. 生成输入流（基于渲染后的字节数组）
            materialsListService.add(
                    new ByteArrayInputStream(os.toByteArray()),
                    fileName,
                    belongId,
                    materialsType,
                    "",
                    businessType,
                    YesOrNoNumberEnum.YES,
                    Objects.isNull(clientId) ? null : String.valueOf(clientId),
                    null,
                    null
            );
        } catch (IOException e) {
            log.error("渲染业务资料失败:" + e.getMessage());
            throw new RuntimeException("渲染业务资料失败，IO异常", e);
        } catch (IllegalArgumentException e) {
            log.error("渲染业务资料失败:" + e.getMessage());
            throw new IllegalArgumentException("渲染业务资料失败，渲染参数非法", e);
        } catch (Exception e) {
            log.error("渲染业务资料失败:" + e.getMessage());
            throw new RuntimeException("渲染业务资料未知错误", e);
        }
    }

    public void generateBasicTemplate(OutputStream os ,FilingTemplateDownloasREQ filingTemplateDownloasREQ,BusinessMaterialsDocNameEnum materialsDocNameEnum ){
        FilingMaterials filingMaterials = this.getById(filingTemplateDownloasREQ.getId());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(FilingMaterialsConstants.CONTRACT_ID, filingMaterials.getContractId());
        hashMap.put(FilingMaterialsConstants.TEMPLATE_TYPE, materialsDocNameEnum);
        hashMap.put(FilingMaterialsConstants.CLIENT_ID, filingTemplateDownloasREQ.getClientId());
        hashMap.put(FilingMaterialsConstants.GENERATE_MANAGE_FLAG, YesOrNoNumberEnum.NO.getCode());

        if (BusinessMaterialsDocNameEnum.BUSINESS_INNER_OPERATION.name().equals(materialsDocNameEnum.name())) {
            Map<String, List<SelectRSP>> operationsDirDict = getOperationsDirDict(FilingMaterialsFilingTypeEnum.BUSINESS_MATERIALS.name(), filingTemplateDownloasREQ.getId());
            if (!operationsDirDict.containsKey(BusinessModuleEnum.FILING_BUSINESS_INNER_OPERATION.name())) {
                throw new MithrasException("初始化内部操作资料-业务类型不存在");
            }
            hashMap.put(FilingMaterialsConstants.INNER_TABLE_LIST, operationsDirDict.get(BusinessModuleEnum.FILING_BUSINESS_INNER_OPERATION.name()));
        }
        try {
            if (hashMap.containsKey(FilingMaterialsConstants.INNER_TABLE_LIST)) {
                innerOperationRender.render(os, hashMap);
            } else {
                businessMaterialsRender.render(os, hashMap);
            }
        } catch (Exception e) {
            log.error("资料清单模板下载失败:{}", e.getMessage());
            throw new MithrasException("资料清单模板下载失败!");
        }
    }

    /**
     * 审批快照生成
     * @param hashMap
     * @param belongId
     */
    private void generateApprovalSnapshot(HashMap hashMap, Long belongId) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            String fileName = materialsApprovalSnapshootRender.render(os, hashMap);
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalStateException("合同渲染生成的文件名不能为空");
            }
            byte[] renderData = os.toByteArray();
            if (renderData.length == 0) {
                log.warn("审批快照渲染结果为空，无法转换为PDF");
                return;
            }
            /*生成的docx转成pdf*/
            ByteArrayOutputStream pdfOutputStream = WatermarkUtil.doc2Pdf(new ByteArrayInputStream(os.toByteArray()));
            fileName = fileName.substring(0, fileName.lastIndexOf(".") + 1) + FileTypeEnum.PDF.getExName();
            /*上传文件服务器*/
            materialsListService.add(
                    new ByteArrayInputStream(pdfOutputStream.toByteArray()),
                    fileName,
                    belongId,
                    FilingDirectoryEnum.APPROVE_SNAPSHOT.name(),
                    "",
                    BusinessModuleEnum.BUSINESS_FLOW_SNAPSHOT.name(),
                    YesOrNoNumberEnum.YES
            );
        } catch (IOException e) {
            log.error("渲染业务资料失败:" + e.getMessage());
            throw new RuntimeException("渲染业务资料失败，IO异常", e);
        } catch (IllegalArgumentException e) {
            log.error("渲染业务资料失败:" + e.getMessage());
            throw new IllegalArgumentException("渲染业务资料失败，渲染参数非法", e);
        } catch (Exception e) {
            log.error("渲染业务资料失败:" + e.getMessage());
            throw new RuntimeException("渲染业务资料未知错误", e);
        }
    }

    /**
     * 资料拷贝处理方法
     * @param sourceList
     * @param targetId
     * @param businessType
     * @param newMaterialsType
     */
    public void copyProjectFile(List<MaterialsList> sourceList, Long targetId, String businessType, String newMaterialsType) {
        List<MaterialsList> targetList = new LinkedList<>();
        boolean empty = CharSequenceUtil.isEmpty(newMaterialsType);
        sourceList.forEach(item -> {
            String materialsType = empty ? item.getMaterialsType() : newMaterialsType;
            MaterialsList newMaterial = new MaterialsList();
            newMaterial.setBelongId(targetId);
            newMaterial.setBusinessType(businessType);
            newMaterial.setMaterialsType(materialsType);
            if (empty) {
                newMaterial.setMaterialSubType(item.getMaterialSubType());
            }
            newMaterial.setOssFilename(item.getOssFilename());
            newMaterial.setSuffix(item.getSuffix());
            newMaterial.setFilename(item.getFilename());
            newMaterial.setFilePath(item.getFilePath());
            newMaterial.setSystemGenerate(item.getSystemGenerate());
            newMaterial.setSourceBusinessKey(item.getSourceBusinessKey());
            if (empty) {
                newMaterial.setCreateBy(item.getCreateBy());
                newMaterial.setCreateTime(item.getCreateTime());
                newMaterial.setUpdateBy(item.getUpdateBy());
                newMaterial.setUpdateTime(item.getUpdateTime());
            }
            targetList.add(newMaterial);
        });
        if (CollUtil.isNotEmpty(targetList)) {
            materialsListService.saveBatch(targetList);
        }
    }

    /**
     * 邮件发送
     */
    public void sendEmailFilingMaterials(String id) {
        LambdaQueryWrapper<FilingMaterials> query = Wrappers.<FilingMaterials>lambdaQuery()
                .in(FilingMaterials::getApproveStatus, Arrays.asList(ProcessStatus.UNDER_APPROVAL.name(), ProcessStatus.UN_SUBMIT.name()))
                .eq(FilingMaterials::getObjectType,FilingMaterialsConstants.OBJECT_TYPE_PROJECT);
        if (CharSequenceUtil.isNotEmpty(id)) {
            query.eq(FilingMaterials::getId, Long.parseLong(id));
        }
        List<FilingMaterials> filingMaterials = baseMapper.selectList(query);
        if (CollUtil.isEmpty(filingMaterials)) {
            log.info("项目资料归档逾期/催收邮件-无在途项目资料归档流程");
            return;
        }
        /*已推送待办未提交审批*/
        List<FilingMaterials> todoFilingMaterials = filingMaterials.stream().filter(item -> Objects.equals(ProcessStatus.UNDER_APPROVAL.name(),item.getApproveStatus()) && Objects.isNull(item.getFirstCommitDate())).collect(Collectors.toList());
        /*被退回*/
        List<FilingMaterials> returnFilingMaterials = filingMaterials.stream().filter(item -> Objects.nonNull(item.getReturnDate()) && Objects.equals(ProcessStatus.UNDER_APPROVAL.name(),item.getApproveStatus())).collect(Collectors.toList());
        /*待办催办/逾期*/
        sendEmailTodoFilingMaterials(todoFilingMaterials);
        /*退回提交催办/逾期*/
        sendEmailReturnFilingMaterials(returnFilingMaterials);
    }

    /**
     * 待办催办/逾期
     *
     * @param todoFilingMaterials
     */
    private void sendEmailTodoFilingMaterials(List<FilingMaterials> todoFilingMaterials) {
        if (CollUtil.isEmpty(todoFilingMaterials)) {
            return;
        }
        LocalDate now = LocalDate.now();
        /*超过38个工作日*/
        List<FilingMaterials> followUpList = todoFilingMaterials.stream().filter(item -> Objects.nonNull(item.getStartDate()) && DateUtil.countWorkdayNumber(item.getStartDate().toLocalDate(), now) == 39).collect(Collectors.toList());
        /*超过45个工作日*/
        List<FilingMaterials> overdueList = todoFilingMaterials.stream().filter(item -> Objects.nonNull(item.getStartDate()) && DateUtil.countWorkdayNumber(item.getStartDate().toLocalDate(), now) == 46).collect(Collectors.toList());
        String dueDateStr = LocalDateTimeUtil.format(now, DatePattern.CHINESE_DATE_PATTERN);
        if (CollUtil.isNotEmpty(followUpList)) {
            /*到期日*/
            dueDateStr = LocalDateTimeUtil.format(DateUtil.getNextWorkdayAfterDays(now, 5), DatePattern.CHINESE_DATE_PATTERN);
            sendEmail(getContractBaseInfoList(followUpList), dueDateStr, EmailType.FOLLOW_UP_REMIND_EMAIL);
        }
        if (CollUtil.isNotEmpty(overdueList)) {
            sendEmail(getContractBaseInfoList(overdueList), dueDateStr, EmailType.SUPPLEMENT_FOLLOW_UP_REMIND_EMAIL);
            //更新是否归档超期
            overdueList.stream().forEach(e ->e.setFilingOverdue( YesOrNoNumberEnum.YES.getCode()));
            this.updateBatchById(overdueList);
        }

    }

    /**
     * 退回提交催办/逾期
     *
     * @param returnFilingMaterials
     */
    private void sendEmailReturnFilingMaterials(List<FilingMaterials> returnFilingMaterials) {
        if (CollUtil.isEmpty(returnFilingMaterials)) {
            return;
        }
        LocalDate now = LocalDate.now();
        /*超过7个工作日*/
        List<FilingMaterials> followUpSupplementList = returnFilingMaterials.stream().filter(item -> DateUtil.countWorkdayNumber(item.getReturnDate(), now) == 8).collect(Collectors.toList());
        /*超过10个工作日*/
        List<FilingMaterials> overdueSupplementList = returnFilingMaterials.stream().filter(item -> DateUtil.countWorkdayNumber(item.getReturnDate(), now) == 11).collect(Collectors.toList());
        String dueDateStr = LocalDateTimeUtil.format(now, DatePattern.CHINESE_DATE_PATTERN);
        if (CollUtil.isNotEmpty(followUpSupplementList)) {
            /*到期日*/
            dueDateStr = LocalDateTimeUtil.format(DateUtil.getNextWorkdayAfterDays(now, 1), DatePattern.CHINESE_DATE_PATTERN);
            sendEmail(getContractBaseInfoList(followUpSupplementList), dueDateStr, EmailType.OVERDUE_REMIND_EMAIL);
        }
        if (CollUtil.isNotEmpty(overdueSupplementList)) {
            sendEmail(getContractBaseInfoList(overdueSupplementList), dueDateStr, EmailType.SUPPLEMENT_OVERDUE_REMIND_EMAIL);
            //是否补充材料超期
            overdueSupplementList.stream().forEach(e ->e.setSupplementFilingOverdue(YesOrNoNumberEnum.YES.getCode()));
            this.updateBatchById(overdueSupplementList);
        }
    }

    private void sendEmail(List<ContractBaseInfo> contractBaseInfoList, String dueDateStr, EmailType emailType) {
        boolean supplementFollowUp = EmailType.SUPPLEMENT_FOLLOW_UP_REMIND_EMAIL.equals(emailType);
        String config = supplementFollowUp ? FilingMaterialsConstants.FILING_FLOW_SUPPLEMENT_FOLLOW_UP_EMAIL_USER : FilingMaterialsConstants.FILING_FLOW_EMAIL_USER;
        SystemConfigDO systemConfigDO = systemConfigService.getConfig(config).getData();
        if (Objects.isNull(systemConfigDO)) {
            log.error("项目资料归档流程邮件默认抄送人未配置");
            return;
        }
        List<Long> userIdList = Optional.ofNullable(JSON.parseArray(systemConfigDO.getConfigValue(), Long.class)).orElse(new ArrayList<>());
        if (CollUtil.isEmpty(userIdList)) {
            log.error("项目资料归档流程邮件默认抄送人未配置");
            return;
        }
        String contractCodeStr;
        Map<Long, List<ContractBaseInfo>> contractBySponsorUserMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjSponsorUserId));
        /*同一主办人合并发送*/
        for (Map.Entry<Long, List<ContractBaseInfo>> entry : contractBySponsorUserMap.entrySet()) {
            List<ContractBaseInfo> contractBaseInfos = entry.getValue();
            Long userId = entry.getKey();
            contractCodeStr = String.join("、", contractBaseInfos.stream().map(ContractBaseInfo::getContractCode).collect(Collectors.toList()));
            /*协办人*/
            List<Long> sysUserIds = new ArrayList<>();
            contractBaseInfos.stream().forEach(item -> {
                if (CharSequenceUtil.isNotEmpty(item.getProjCosponsorUserIds())) {
                    sysUserIds.addAll(JSONUtil.toList(item.getProjCosponsorUserIds(), Long.class));
                }
            });
            /*业务部门负责人*/
            sysUserIds.add(contractBaseInfos.get(0).getBizDeptLeaderId());
            sysUserIds.addAll(userIdList);
            if (supplementFollowUp) {
                /*分管领导*/
                sysUserIds.add(contractBaseInfos.get(0).getBizDivisionLeaderId());
            }
            /*收件人*/
            HashSet toSet = new HashSet<>();
            /*抄送人*/
            HashSet ccSet = new HashSet<>();
            toSet.add(userId);
            ccSet.addAll(sysUserIds);

            FilingEmailDTO filingEmailDTO = new FilingEmailDTO();
            filingEmailDTO.setContractCodeStr(contractCodeStr);
            filingEmailDTO.setDueDate(dueDateStr);
            for (AbstractSendEmailHandler abstractSendEmailHandler : abstractSendEmailHandlers) {
                if (abstractSendEmailHandler.needHandle(emailType)) {
                    abstractSendEmailHandler.newSendEmail(toSet, ccSet, null, filingEmailDTO);
                }
            }
        }
    }

    /**
     * 根据合同id获取合同信息
     * @param filingMaterials
     * @return
     */
    private List<ContractBaseInfo> getContractBaseInfoList(List<FilingMaterials> filingMaterials) {
        List<Long> contractIds = filingMaterials.stream().map(FilingMaterials::getContractId).collect(Collectors.toList());
        return SpringUtil.getBean(ContractBaseInfoMapper.class).selectList(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getId, contractIds)
                        .orderByDesc(ContractBaseInfo::getId));
    }

    /**
     * 获取归档资料配置的一级目录
     *
     * @param fillingType
     * @param id
     * @return
     */
    public Map<String, List<SelectRSP>> getOperationsDirDict(String fillingType, Long id) {
        FilingMaterials filingMaterials = this.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        /*模板生成的资料项*/
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS = baseMapper.queryDocxFilingMaterialsConfig(fillingType);
        return getBusinessDirDict(filingMaterialsConfigDTOS,filingMaterials);
    }

    /**
     * 获取页面展示的资料项
     * @param fillingType
     * @param id
     * @return
     */
    public Map<String, List<SelectRSP>> getPageOperationsDirDict(String fillingType, Long id) {
        FilingMaterials filingMaterials = this.getById(id);
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        /*查询表中配置好的页面展示的一级目录*/
        List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS = baseMapper.queryFilingMaterialsConfig(fillingType);
        return getBusinessDirDict(filingMaterialsConfigDTOS,filingMaterials);
    }

    public Map<String, List<SelectRSP>> getBusinessDirDict(List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS, FilingMaterials filingMaterials) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(filingMaterials.getContractId());
        ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(contractBaseInfo.getProjReviewId());
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("该流程找不到对应的合同信息"));
        Assert.notNull(projReviewBaseInfo, () -> MithrasException.newException("该流程找不到对应的项目信息"));
        List<SelectRSP> selectRSPList;
        Map<String, List<SelectRSP>> map = new HashMap<>();
        //校验目录下是否存在文件
        Map<String, List<MaterialsList>> listMap = this.checkExistFile(filingMaterialsConfigDTOS, filingMaterials.getId());
        /*通过businessType分组*/
        Map<String, List<FilingMaterialsConfigDTO>> groupByBusiness = filingMaterialsConfigDTOS.stream().collect(Collectors.groupingBy(FilingMaterialsConfigDTO::getBusinessType));
        /*获取合同下各审批通过的业务流程*/
        List<ProcessResp> projectApprovedProcess = this.getProjectApprovedProcess(contractBaseInfo, projReviewBaseInfo);
        /*根据流程key分组*/
        Map<String, List<ProcessResp>> groupByModelKeyMap = projectApprovedProcess.stream().collect(Collectors.groupingBy(ProcessResp::getModelKey));
        for (Map.Entry<String, List<FilingMaterialsConfigDTO>> entry : groupByBusiness.entrySet()) {
            selectRSPList = new ArrayList<>();
            /*一级目录下根据定义好的sortcode排序*/
            List<FilingMaterialsConfigDTO> materialsConfigDTOS = entry.getValue().stream().sorted(Comparator.comparing(FilingMaterialsConfigDTO::getSortCode)).collect(Collectors.toList());
            for (FilingMaterialsConfigDTO filingMaterialsConfigDTO : materialsConfigDTOS) {
                String conditionKey = filingMaterialsConfigDTO.getConditionKey();
                /*该目录是否需拼接流程审批号条件字段*/
                if (CharSequenceUtil.isEmpty(conditionKey)) {
                    selectRSPList.add(new SelectRSP(filingMaterialsConfigDTO.getDirName(), filingMaterialsConfigDTO.getDirCode(), 1));
                } else {
                    /*需关联,则获取对应枚举映射*/
                    DirConditionKeyEnum conditionEnum = DirConditionKeyEnum.getDirConditionKeyByConditionKey(conditionKey);
                    Assert.notNull(conditionEnum, () -> MithrasException.newException(conditionKey + "找不到对应的枚举项"));
                    String queryType = conditionEnum.getQueryType();
                    /*根据流程key匹配,存在多个审批通过的流程,则拼接*/
                    if (conditionEnum.name().startsWith(FilingMaterialsConstants.APPROVAL) && CharSequenceUtil.isNotBlank(queryType)
                            && groupByModelKeyMap.containsKey(queryType)) {
                        List<ProcessResp> processResps = groupByModelKeyMap.get(queryType);
                        String processInstanceId = processResps.stream().sorted(Comparator.comparing(ProcessResp::getEndTime)).map(ProcessResp::getProcessInstanceId).collect(Collectors.joining("、"));
                        selectRSPList.add(new SelectRSP(filingMaterialsConfigDTO.getDirName() + "-" + processInstanceId, filingMaterialsConfigDTO.getDirCode(), 1));
                        continue;
                    }
                    //目录下存在附件时该目录展示
                    if (FilingMaterialsConstants.FILE_CHECK.equals(conditionEnum.name())
                            && listMap.containsKey(filingMaterialsConfigDTO.getDirCode())) {
                        selectRSPList.add(new SelectRSP(filingMaterialsConfigDTO.getDirName(), filingMaterialsConfigDTO.getDirCode(), 1));
                    }
                }
            }
            map.put(entry.getKey(), selectRSPList);
        }
        return map;
    }

    /**
     * 需校验文件是否存在的目录
     * @param filingMaterialsConfigDTOS
     * @param id
     * @return
     */
    private Map<String,List<MaterialsList>> checkExistFile(List<FilingMaterialsConfigDTO> filingMaterialsConfigDTOS,Long id){
        List<String> fileCheckDirCode = filingMaterialsConfigDTOS.stream().filter(e -> Objects.nonNull(e.getConditionKey()) && CharSequenceUtil.equals(e.getConditionKey(), FilingMaterialsConstants.FILE_CHECK)).map(FilingMaterialsConfigDTO::getDirCode).collect(toList());
        if (CollUtil.isEmpty(fileCheckDirCode)) {
            return new HashMap<>();
        }
        List<MaterialsList> materialsLists = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .in(MaterialsList::getBusinessType, FilingMaterialsBusinessTypeEnum.getBusinessTypeAll())
                .in(MaterialsList::getMaterialsType,fileCheckDirCode)
                .eq(MaterialsList::getBelongId, id));
        if(CollUtil.isEmpty(materialsLists)){
            return new HashMap<>();
        }
        return materialsLists.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
    }

    /**
     * 获取基本信息数据
     * @param req
     * @return
     */
    public FilingProjMaterialsListListRSP getCustomerReferenceMaterials(@Valid FilingMaterialsQueryREQ req) {
        FilingMaterials filingMaterials = this.getById(req.getId());
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (!CharSequenceUtil.equals(FilingDirectoryEnum.BASIC_MATERIALS.getCode(), req.getTabCode())) {
            throw new MithrasException("不支持该类型调用！");
        }
        String businessType;
        boolean referenceMaterialsFlag = StrUtil.equals(req.getModuleCode(), FilingMaterialsModuleCodeEnum.REFERENCE_MATERIALS.name());
        if (referenceMaterialsFlag) {
            /*查询该模块下所有的businessType*/
            businessType = BusinessModuleEnum.BUSINESS_REVIEW_CLIENT.name();
        } else {
            businessType = BusinessModuleEnum.FILING_BUSINESS_CLIENT.name();
        }
        Set<Long> clientIds = new HashSet<>();
        Map<Long, String> typeName = new HashMap<>();
        List<MaterialsList> materialsLists = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBusinessType, businessType)
                .eq(MaterialsList::getBelongId, filingMaterials.getId())
                .orderByDesc(MaterialsList::getUpdateTime));
        if (referenceMaterialsFlag) {
            /*获取项目评审中的交易主体*/
            clientIds = getProjReviewClient(filingMaterials.getContractId(), clientIds, typeName, materialsLists);
        } else {
            /*归档资料获取合同下的交易主体*/
            clientIds = getContractActualSubject(filingMaterials.getContractId(), filingMaterials.getContractVersion(), clientIds, typeName);
        }
        List<ProjMaterialsListListRSP> clientMaterialList = materialsListService.toProjMaterialsListListRSP(clientIds, materialsLists, typeName, req.getId(),
                referenceMaterialsFlag ? BusinessModuleEnum.BUSINESS_REVIEW_CLIENT.name() : BusinessModuleEnum.FILING_BUSINESS_CLIENT.name());
        List<ProjMaterialsListListRSP> result = clientMaterialList.stream().sorted(Comparator.comparing(r -> ClientMaterialsDisplayEnum.ofWithDefault(r.getClientTypeName()).order)).collect(toList());
        List<FilingProjMaterialsDetailRSP> copyList = BeanUtil.copyToList(result, FilingProjMaterialsDetailRSP.class);

        if (!referenceMaterialsFlag) {
            /*基础资料-归档后置处理*/
            customerOperationAfterHandle(copyList);
        }
        /*构造反参对象*/
        FilingProjMaterialsListListRSP filingProjMaterialsListListRSP = new FilingProjMaterialsListListRSP();
        filingProjMaterialsListListRSP.setTabCode(req.getTabCode());
        filingProjMaterialsListListRSP.setModuleCode(req.getModuleCode());
        filingProjMaterialsListListRSP.setProjMaterialsListListRSP(copyList);
        return filingProjMaterialsListListRSP;
    }

    public Set<Long> getProjReviewClient(Long contractId, Set<Long> clientIds, Map<Long, String> typeName, List<MaterialsList> materialsLists) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        /*承租人分组*/
        ProjReviewBaseInfo reviewBaseInfo = this.getProjReviewBaseInfo(contractBaseInfo.getProjReviewId());
        if (ProjectBizType.BL.name().equals(reviewBaseInfo.getBizType()) || ProjectBizType.ZR.name().equals(reviewBaseInfo.getBizType())) {
            getFieldFromJson(reviewBaseInfo.getCreditorInfo(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
        } else {
            getFieldFromJson(reviewBaseInfo.getLesseeInfo(), ClientMaterialsDisplayEnum.LESSEEINFO.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getGuaranteeInfo(), ClientMaterialsDisplayEnum.GUARANTEEINFO.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getPledgorInfo(), ClientMaterialsDisplayEnum.PLEDGORINFO.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getMortgagorInfo(), ClientMaterialsDisplayEnum.MORTGAGORINFO.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getCreditorInfo(), ClientMaterialsDisplayEnum.CREDITORCLIENTID.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
            getFieldFromJson(reviewBaseInfo.getDebtorInfo(), ClientMaterialsDisplayEnum.DEBTORINFO.display, FilingMaterialsConstants.CLIENT_ID, clientIds, typeName);
        }

        /*参考资料不存在资料的交易主体不展示*/
        Set<Long> sourceKeySet = materialsLists.stream().map(MaterialsList::getSourceBusinessKey).filter(Objects::nonNull).map(Long::valueOf).collect(toSet());
        return clientIds.stream().filter(sourceKeySet::contains).collect(Collectors.toSet());

    }

    /**
     * 基础资料-归档后置处理
     *
     * @param copyList
     */
    private void customerOperationAfterHandle(List<FilingProjMaterialsDetailRSP> copyList) {
        copyList.stream().filter(Objects::nonNull).forEach(item -> {
            /*后端定义好每个交易主体需展示的归档资料一级目录常量类,前端直接获取该字段进行匹配*/
            if (Objects.equals(item.getClientTypeName(), ClientMaterialsDisplayEnum.LESSEEINFO.display)) {
                item.setCustDirEnum(BusinessModuleEnum.FILING_BUSINESS_LESSEE_CLIENT.name());
            } else if (Objects.equals(item.getClientType(), ClientType.CORPORATION.name())) {
                item.setCustDirEnum(BusinessModuleEnum.FILING_BUSINESS_ENT_CLIENT.name());
            } else {
                item.setCustDirEnum(BusinessModuleEnum.FILING_BUSINESS_IND_CLIENT.name());
            }
            /*生成的资料清单上传人、上传时间页面展示为空*/
            if (CollUtil.isNotEmpty(item.getBusinessMaterialList())) {
                item.getBusinessMaterialList().stream().filter(Objects::nonNull).forEach(e -> {
                    if (Objects.equals(e.getMaterialName(), FilingMaterialsConstants.BASIC_INFORMATION)
                            && CollUtil.isNotEmpty(e.getMaterialList())) {
                        e.getMaterialList().stream()
                                .filter(Objects::nonNull)
                                .filter(t -> Objects.nonNull(t.getSystemGenerate()) && YesOrNoNumberEnum.YES.getCode().equals(t.getSystemGenerate()))
                                .forEach(sub -> {
                                            sub.setCreateByName(null);
                                            sub.setCreateBy(null);
                                            sub.setCreateTimestamp(-1);
                                        }
                                );
                    }
                });
            }
            /*资料附件降序排序*/
            List<MaterialsListListRSP.MaterialGroup> businessMaterialList = item.getBusinessMaterialList();
            if (CollUtil.isNotEmpty(businessMaterialList)) {
                for (MaterialsListListRSP.MaterialGroup group : businessMaterialList) {
                    if (Objects.isNull(group)) {
                        continue;
                    }
                    List<MaterialsListListRSP.MaterialGroup.UploadItem> materialList = group.getMaterialList();
                    if (CollUtil.isNotEmpty(materialList)) {
                        descSortByCreateTime(materialList);
                    }
                }
            }
        });
    }

    /**
     * 非基础资料获取信息方法
     * @param req
     * @return
     */
    public FilingMaterialsQueryRSP getNonCustomerReferenceMaterials(FilingMaterialsQueryREQ req) {
        List<FilingMaterialsGroupQueryRSP> filingMaterialsGroupQueryRSPList = new ArrayList<>();
        String moduleCode = req.getModuleCode();
        String tabCode = req.getTabCode();
        if (Objects.equals(tabCode, FilingDirectoryEnum.BASIC_MATERIALS.name())) {
            throw new MithrasException("该接口不支持基本信息页请求");
        }
        boolean referenceMaterialsFlag = CharSequenceUtil.equals(req.getModuleCode(), FilingMaterialsModuleCodeEnum.REFERENCE_MATERIALS.name());
        if (referenceMaterialsFlag) {
            /*获取对应tab下参考资料*/
            List<FilingDirectoryEnum> childrenByParentCode = FilingDirectoryEnum.getChildrenByParentCode(tabCode, moduleCode);
            /*根据业务类型分组*/
            Map<String, List<FilingDirectoryEnum>> groupByBusinessTypeMap = childrenByParentCode.stream()
                    .collect(Collectors.groupingBy(
                            FilingDirectoryEnum::getBusinessType,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            FilingMaterialsGroupQueryRSP groupQueryRSP;
            for (Map.Entry<String, List<FilingDirectoryEnum>> entry : groupByBusinessTypeMap.entrySet()) {
                String key = entry.getKey();
                List<FilingDirectoryEnum> value = entry.getValue();
                List<String> materialsTypeList = value.stream().map(FilingDirectoryEnum::getCode).collect(toList());
                List<MaterialsList> materialsListList = materialsListService.list(key, materialsTypeList, Collections.singletonList(req.getId()));
                if (ObjectUtils.isEmpty(materialsListList)) {
                    continue;
                }

                /*初始化*/
                groupQueryRSP = new FilingMaterialsGroupQueryRSP();
                groupQueryRSP.setGroupCode(value.get(0).getGroupCode());
                groupQueryRSP.setGroupName(value.get(0).getGroupName());
                groupQueryRSP.setBusinessType(key);
                filingMaterialsGroupQueryRSPList.add(groupQueryRSP);

                List<FileListRSP> rspList = materialsListList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
                fileConvert.fillName(rspList);
                rspList.sort(new CommonFileSortComparator());
                createFileAuditHandle(rspList);
                /*无层级结构的数据直接返回*/
                if (Objects.isNull(value.get(0).getLevel())) {
                    groupQueryRSP.setNonLevelfileListR(rspList);
                    continue;
                }
                List<List<FileListRSP>> groupRspList = new ArrayList<>(rspList.stream()
                        .collect(Collectors.groupingBy(FileListRSP::getMaterialsType))
                        .values());
                // 分组排序
                sortGroup(groupRspList, value, value.get(0).getRelBusinessType());
                List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
                for (List<FileListRSP> groupRsp : groupRspList) {
                    List<FileListRSP> sortList = groupRsp.stream().sorted(Comparator.comparing(FileListRSP::getCreateTime)).collect(toList());
                    resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), sortList));
                }
                groupQueryRSP.setLevelFileList(resList);
            }
        } else {
            FilingMaterialsBusinessTypeEnum businessTypeEnum = FilingMaterialsBusinessTypeEnum.of(req.getTabCode());
            FilingMaterialsGroupQueryRSP groupQueryRSP;
            /*初始化*/
            groupQueryRSP = new FilingMaterialsGroupQueryRSP();
            assert businessTypeEnum != null;
            groupQueryRSP.setGroupCode(businessTypeEnum.getGroupCode());
            groupQueryRSP.setGroupName("归档资料");
            groupQueryRSP.setBusinessType(businessTypeEnum.getCode());
            filingMaterialsGroupQueryRSPList.add(groupQueryRSP);
            List<MaterialsList> materialsListList = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, businessTypeEnum.name())
                    .eq(MaterialsList::getBelongId, req.getId())
                    .orderByDesc(MaterialsList::getUpdateTime));
            if (!ObjectUtils.isEmpty(materialsListList)) {
                List<FileListRSP> rspList = materialsListList.stream().map(fileConvert::entity2RSP).collect(Collectors.toList());
                fileConvert.fillName(rspList);
                /*降序排序*/
                descSortByCreateTime(rspList);
                /*上传时间、上传人处理*/
                createFileAuditHandle(rspList);
                List<List<FileListRSP>> groupRspList = new ArrayList<>(rspList.stream()
                        .collect(Collectors.groupingBy(FileListRSP::getMaterialsType))
                        .values());
                List<Pair<String, List<FileListRSP>>> resList = new ArrayList<>();
                for (List<FileListRSP> groupRsp : groupRspList) {
                    resList.add(new Pair<>(groupRsp.get(0).getMaterialsType(), groupRsp));
                }
                groupQueryRSP.setLevelFileList(resList);
            }
        }
        FilingMaterialsQueryRSP filingMaterialsQueryRSP = new FilingMaterialsQueryRSP();
        filingMaterialsQueryRSP.setTabCode(tabCode);
        filingMaterialsQueryRSP.setModuleCode(moduleCode);
        filingMaterialsQueryRSP.setFilingMaterialsGroupQueryRSPList(filingMaterialsGroupQueryRSPList);
        return filingMaterialsQueryRSP;
    }



    /**
     * 参考资料中系统生成的资料将上传人等字段前端展示为空,代码处理,不对表字段清空
     * @param rspList
     */
    private void createFileAuditHandle(List<FileListRSP> rspList) {
        List<String> materialsTypeList = Arrays.asList(FilingDirectoryEnum.PUBLIC_INFORMATION_QUERY.name(),
                FilingDirectoryEnum.APPROVE_SNAPSHOT.name(), FilingMaterialsConstants.BASIC_INFORMATION);
        rspList.stream().filter(Objects::nonNull)
                .filter(e -> Objects.nonNull(e.getMaterialsType()) && materialsTypeList.contains(e.getMaterialsType()))
                .filter(e -> Objects.nonNull(e.getSystemGenerate()) && YesOrNoNumberEnum.YES.getCode().equals(e.getSystemGenerate()))
                .forEach(e -> {
                    e.setCreateByName(null);
                    e.setCreateBy(null);
                    e.setCreateTime(null);

                });

    }

    /**
     * 创建时间降序
     *
     * @param rspList
     */
    private void descSortByCreateTime(List<? extends CommonFileSortWeight> rspList) {
        Collections.sort(rspList, (Comparator<CommonFileSortWeight>) (o1, o2) -> {
            int weight1 = o1.calculateKeyWeight();
            int weight2 = o2.calculateKeyWeight();
            if (weight1 != weight2) {
                return weight1 - weight2;
            }
            // 如果按照名称排序权重一致则按照创建时间排序
            return Long.compare(o2.createTimestamp(), o1.createTimestamp());
        });
    }


    /**
     * 文件分组排序
     *
     * @param groupRspList
     */
    protected void sortGroup(List<List<FileListRSP>> groupRspList, List<FilingDirectoryEnum> filingDirectoryEnumList, String relBusinessType) {
        if (CharSequenceUtil.isNotEmpty(relBusinessType)) {
            AbstractFileListProvider fileListProvider = fileListProviderFactory.getProvider(relBusinessType);
            if (Objects.nonNull(fileListProvider)) {
                fileListProvider.externalSortGroup(groupRspList);
                return;
            }
        }
        Map<String, Integer> type2SortMap = filingDirectoryEnumList.stream()
                .collect(Collectors.toMap(
                        FilingDirectoryEnum::getCode,
                        FilingDirectoryEnum::getSort,
                        (oldVal, newVal) -> oldVal
                ));

        Collections.sort(groupRspList, (list1, list2) -> {
            Integer sort1 = getSortForNestedList(list1, type2SortMap);
            Integer sort2 = getSortForNestedList(list2, type2SortMap);
            return sort1.compareTo(sort2);
        });
    }

    /**
     * 辅助方法：获取内层List<B>对应的sort值（处理空列表、无匹配type）
     *
     * @param innerList    内层List<B>
     * @param type2SortMap type→sort映射
     * @return 排序值（空列表/无匹配type设为Integer.MAX_VALUE，排最后）
     */
    private static Integer getSortForNestedList(List<FileListRSP> innerList, Map<String, Integer> type2SortMap) {
        if (innerList == null || innerList.isEmpty()) {
            return Integer.MAX_VALUE;
        }
        String materialsType = innerList.get(0).getMaterialsType();
        return type2SortMap.getOrDefault(materialsType, Integer.MAX_VALUE);
    }


    private void getFieldFromJson(String jsonArray, String fieldName, String
            field, Set<Long> set, Map<Long, String> typeName) {
        if (Strings.isNotEmpty(jsonArray)) {
            JSONArray json = JSONUtil.parseArray(jsonArray);
            for (int i = 0; i < json.size(); i++) {
                Object clientId = json.getJSONObject(i).get(field);
                if (clientId != null) {
                    Long l = Long.parseLong(String.valueOf(clientId));
                    if (set.contains(l)) {
                        typeName.put(l, typeName.get(l) + "、" + fieldName);
                    } else {
                        typeName.put(l, fieldName);
                    }
                    set.add(l);
                }
            }
        }
    }

    public Set<Long> getContractActualSubject(Long contractId, String version, Set<Long> clientIds, Map<Long, String> typeName) {
        List<ContractMortgageLib> contractMortgages = contractMortgageLibMapper.selectList(Wrappers.<ContractMortgageLib>lambdaQuery()
                .eq(ContractMortgageLib::getContractId, contractId)
                .eq(ContractMortgageLib::getVersion, version));
        if (CollectionUtils.isNotEmpty(contractMortgages)) {
            List<Long> ids = contractMortgages.stream().map(ContractMortgageLib::getMortgageIds).filter(StringUtils::isNotBlank).map(s -> JSON.parseArray(s, Long.class)).flatMap(List::stream)
                    .collect(Collectors.toList());
            getClientList(ids, ClientMaterialsDisplayEnum.MORTGAGORINFO.display, clientIds, typeName);
        }
        List<ContractPledgeLib> contractPledgeList = contractPledgeLibMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                .eq(ContractPledgeLib::getContractId, contractId)
                .eq(ContractPledgeLib::getVersion, version));
        if (CollectionUtils.isNotEmpty(contractPledgeList)) {
            List<Long> ids = contractPledgeList.stream().map(ContractPledgeLib::getPledgeIds).filter(StringUtils::isNotBlank).map(s -> JSON.parseArray(s, Long.class)).flatMap(List::stream)
                    .collect(Collectors.toList());
            getClientList(ids, ClientMaterialsDisplayEnum.PLEDGORINFO.display, clientIds, typeName);
        }
        List<ContractGuarantorLib> contractGuarantors = contractGuarantorLibMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                .eq(ContractGuarantorLib::getContractId, contractId)
                .eq(ContractGuarantorLib::getVersion, version));
        if (CollectionUtils.isNotEmpty(contractGuarantors)) {
            List<Long> ids = contractGuarantors.stream().map(ContractGuarantorLib::getGuarantorIds).filter(StringUtils::isNotBlank).map(s -> JSON.parseArray(s, Long.class)).flatMap(List::stream)
                    .collect(Collectors.toList());
            getClientList(ids, ClientMaterialsDisplayEnum.GUARANTEEINFO.display, clientIds, typeName);
        }
        List<ContractTenantryLib> contractTenantries = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantryLib::getVersion, version));
        if (CollectionUtils.isNotEmpty(contractTenantries)) {
            Map<String, List<ContractTenantryLib>> groupByLesseeType = contractTenantries.stream().collect(Collectors.groupingBy(ContractTenantryLib::getLesseeType));
            for (Map.Entry<String, List<ContractTenantryLib>> entry : groupByLesseeType.entrySet()) {
                List<Long> ids = entry.getValue().stream().map(ContractTenantryLib::getLesseeId).collect(toList());
                String key = entry.getKey();
                String fieldName;
                if (Objects.equals(CreditorDebtorTypeEnum.CREDITOR.name(), key)) {
                    fieldName = ClientMaterialsDisplayEnum.CREDITORCLIENTID.display;
                } else if (Objects.equals(CreditorDebtorTypeEnum.DEBTOR.name(), key)) {
                    fieldName = ClientMaterialsDisplayEnum.DEBTORINFO.display;
                } else {
                    fieldName = ClientMaterialsDisplayEnum.LESSEEINFO.display;
                }
                getClientList(ids, fieldName, clientIds, typeName);
            }
        }
        return clientIds;
    }

    public void generateContractActualSubject(Long contractId, String ctrVersion, Set<Long> lesseeIds, Set<Long> entIds, Set<Long> indIds) {
        List<ContractMortgageLib> contractMortgages = contractMortgageLibMapper.selectList(Wrappers.<ContractMortgageLib>lambdaQuery()
                .eq(ContractMortgageLib::getContractId, contractId)
                .eq(ContractMortgageLib::getVersion, ctrVersion));
        for (ContractMortgageLib contractMortgageLib : contractMortgages) {
            List<Long> ids = JSON.parseArray(contractMortgageLib.getMortgageIds(), Long.class);
            if (Objects.equals(ClientType.CORPORATION.name(), contractMortgageLib.getMortgageType())) {
                entIds.addAll(ids);
            } else {
                indIds.addAll(ids);
            }
        }

        List<ContractPledgeLib> contractPledgeList = contractPledgeLibMapper.selectList(Wrappers.<ContractPledgeLib>lambdaQuery()
                .eq(ContractPledgeLib::getContractId, contractId)
                .eq(ContractPledgeLib::getVersion, ctrVersion));
        for (ContractPledgeLib contractPledgeLib : contractPledgeList) {
            List<Long> ids = JSON.parseArray(contractPledgeLib.getPledgeIds(), Long.class);
            if (Objects.equals(ClientType.CORPORATION.name(), contractPledgeLib.getPledgeType())) {
                entIds.addAll(ids);
            } else {
                indIds.addAll(ids);
            }
        }

        List<ContractGuarantorLib> contractGuarantors = contractGuarantorLibMapper.selectList(Wrappers.<ContractGuarantorLib>lambdaQuery()
                .eq(ContractGuarantorLib::getContractId, contractId)
                .eq(ContractGuarantorLib::getVersion, ctrVersion));
        for (ContractGuarantorLib contractGuarantorLib : contractGuarantors) {
            List<Long> ids = JSON.parseArray(contractGuarantorLib.getGuarantorIds(), Long.class);
            if (Objects.equals(ClientType.CORPORATION.name(), contractGuarantorLib.getGuarantorType())) {
                entIds.addAll(ids);
            } else {
                indIds.addAll(ids);
            }
        }

        List<ContractTenantryLib> contractTenantries = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantryLib::getVersion, ctrVersion));
        if (CollectionUtils.isNotEmpty(contractTenantries)) {
            List<Long> ids = contractTenantries.stream().filter(Objects::nonNull).map(ContractTenantryLib::getLesseeId).collect(toList());
            lesseeIds.addAll(ids);
        }
    }

    private void getClientList(List<Long> clientIds, String fieldName, Set<Long> set, Map<Long, String> typeName) {
        if (CollUtil.isNotEmpty(clientIds)) {
            for (Long clientId : clientIds) {
                if (clientId != null) {
                    if (set.contains(clientId)) {
                        typeName.put(clientId, typeName.get(clientId) + "、" + fieldName);
                    } else {
                        typeName.put(clientId, fieldName);
                    }
                    set.add(clientId);
                }
            }
        }
    }

}
