package cn.zswltech.mithras.application.orchestration.kpi;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.dto.message.MessageUrlEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.*;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.message.convert.MessageConver;
import cn.zswltech.mithras.kpi.convert.KpiProjectDistributionConvert;
import cn.zswltech.mithras.kpi.application.distribution.KpiProjectDistributionDeptWeightService;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import cn.zswltech.mithras.customer.enums.client.ClientTransferStatus;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.message.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.message.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.kpi.dto.persistence.KpiProjectDistributionQuery;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.kpi.mapper.lib.KpiProjectDistributionWeightLibMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.customer.model.client.ClientTransfer;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.kpi.model.*;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.kpi.bo.KpiProjectDistributionBO;
import cn.zswltech.mithras.application.orchestration.client.ClientTransferService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRentEstimateService;
import cn.zswltech.mithras.kpi.distribution.versioning.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.kpi.distribution.versioning.KpiProjectDistributionLibVersionService;
import cn.zswltech.mithras.kpi.distribution.versioning.KpiProjectDistributionWeightLibService;
import cn.zswltech.mithras.kpi.distribution.versioning.handler.impl.KpiProjectDistributionWeightLibHandler;
import cn.zswltech.mithras.message.service.MessageService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionService extends ServiceImpl<KpiProjectDistributionMapper, KpiProjectDistribution> {
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;
    @Resource
    private KpiProjectDistributionWeightService kpiProjectDistributionWeightService;
    @Resource
    private KpiProjectDistributionDeptWeightService kpiProjectDistributionDeptWeightService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractRentEstimateService contractRentEstimateService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private KpiProjectDistributionBaseInfoLibService kpiProjectDistributionBaseInfoLibService;
    @Resource
    private KpiProjectDistributionWeightLibService kpiProjectDistributionWeightLibService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private OrgService orgService;
    @Resource
    private UserService userService;
    @Resource
    private KpiProjectDistributionLibVersionService kpiProjectDistributionLibVersionService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private KpiProjectDistributionWeightLibMapper libMapper;
    @Resource
    private KpiProjectDistributionWeightLibHandler handler;


    public void init() {
        // 找到所有4月份及之后有产生实际付款的合同
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.ge(PaymentActualDetail::getPaidInDate, LocalDate.of(2023, 4, 1));
        query.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.list(query);
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return;
        }
        Set<Long> paymentIds = paymentActualDetailList.stream().map(PaymentActualDetail::getPaymentId).collect(Collectors.toSet());
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByIds(paymentIds);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return;
        }
        Set<Long> contractIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet());
        for (Long contractId : contractIds) {
            SpringUtil.getBean(KpiProjectDistributionService.class).create(contractId, false);
        }
    }

    public void importHistoryData(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read(1, 243);
        if (CollectionUtil.isEmpty(rows)) {
            log.info("没有需导入的数据");
            return;
        }
        // 备用数据
        List<OrgDO> orgDOList = orgService.selectAll();
        Map<String, Long> orgMap = orgDOList.stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId));
        List<UserDO> userDOList = userService.selectAll();
        Map<String, Long> userMap = userDOList.stream().collect(Collectors.toMap(UserDO::getUserName, UserDO::getId));

        for (List<Object> row : rows) {
            String contractCode = null;
            try {
                contractCode = Optional.ofNullable(row.get(2)).map(Object::toString).orElse(null);
                if (StrUtil.isBlank(contractCode)) {
                    continue;
                }
                log.info("开始处理{}的项目分配表", contractCode);
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getOne(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractCode, contractCode));
                if (Objects.isNull(contractBaseInfo)) {
                    log.info("没有找到合同编号为{}的合同信息", contractCode);
                    continue;
                }
                // 校验是否存在
                KpiProjectDistribution exist = this.getOneByContractId(contractBaseInfo.getId());
                if (Objects.nonNull(exist)) {
                    log.info("{}的项目分配表数据已存在，无需重复生成", contractCode);
                    continue;
                }
                transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        // 主表数据
                        Long projectDistributionId = this.saveProjectDistribution(contractBaseInfo.getId());
                        // 基本信息表
                        this.saveProjectDistributionBaseInfo(row, projectDistributionId, contractBaseInfo, orgMap, userMap);
                        // 分配信息表
                        this.saveProjectDistributionWeight(row, projectDistributionId, orgMap, userMap);
                        // 生成版本
                        kpiProjectDistributionLibVersionService.recordVersion(projectDistributionId, VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
                    } catch (Exception e) {
                        transactionStatus.setRollbackOnly();
                        log.error("保存项目分配表数据发生异常[{}]", contractBaseInfo.getContractCode(), e);
                    }
                });

            } catch (Exception e) {
                log.error("处理{}的项目分配表发生异常", contractCode, e);
            } finally {
                log.info("结束处理{}的项目分配表", contractCode);
            }
        }
    }

    public KpiProjectDistribution getOneByContractId(Long contractId) {
        LambdaQueryWrapper<KpiProjectDistribution> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistribution::getContractId, contractId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void submit(Long projectDistributionId) {
        KpiProjectDistribution projectDistribution = this.getById(projectDistributionId);
        if (Objects.isNull(projectDistribution)) {
            throw new MithrasException("项目分配信息不存在");
        }

        // 已经存在流程中的数据不可以再次提交
        if (Objects.equals(projectDistribution.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
            throw new MithrasException("项目分配信息正在审批中，请稍后再试");
        }

        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(projectDistribution.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        //客户移交流程中 不允许提交
        if (clientTransferService.count(Wrappers.<ClientTransfer>lambdaQuery()
                .eq(ClientTransfer::getClientId, contractBaseInfo.getClientId())
                .eq(ClientTransfer::getTransferStatus, ClientTransferStatus.to_be_approved.name())) > 0) {
            throw new MithrasException("客户移交流程中，请先完成客户移交流程审批");
        }
        // 基本信息
        KpiProjectDistributionBaseInfo baseInfo = kpiProjectDistributionBaseInfoService.getOneByProjectDistributionId(projectDistributionId);
        // 项目分配比重
        List<KpiProjectDistributionWeight> weightList = kpiProjectDistributionWeightService.listByProjectDistributionId(projectDistributionId);
        // 部门的分配信息校验
        List<KpiProjectDistributionDeptWeightInfo> deptWeightInfos = SpringUtil.getBean(KpiProjectDistributionDeptWeightService.class).queryList(projectDistributionId);
        if (CollectionUtil.isEmpty(deptWeightInfos)) {
            throw new MithrasException("请维护部门分配信息");
        }
        // 前置校验
        this.checkBeforeSubmit(baseInfo, weightList);
        // 保存审批相关人员
        List<String> sponsorUserIds = new LinkedList<>();
        List<String> cosponsorUserIds = new LinkedList<>();
        List<String> otherDeptRecommend = new LinkedList<>();
        List<String> leaderIds = new LinkedList<>();
        List<String> businessHeaderIds = new LinkedList<>();
        List<String> teamLeaderIds = new LinkedList<>();
        // 团队长
        if (Objects.nonNull(baseInfo.getTeamLeaderId())) {
            teamLeaderIds.add(String.valueOf(baseInfo.getTeamLeaderId()));
        }
        for (KpiProjectDistributionWeight weight : weightList) {
            if (StrUtil.isBlank(weight.getWeightTarget())) {
                continue;
            }
            if (Objects.equals(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name(), weight.getWeightType())) {
                sponsorUserIds.add(weight.getWeightTarget());
            } else if (Objects.equals(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name(), weight.getWeightType())) {
                cosponsorUserIds.add(weight.getWeightTarget());
            } else if (Objects.equals(KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name(), weight.getWeightType())) {
                otherDeptRecommend.add(weight.getWeightTarget());
            }
        }

        // 拿出所有的部门，再根据部门id获取部门负责人和分管领导
        List<Long> deptIds = deptWeightInfos.stream().map(KpiProjectDistributionDeptWeightInfo::getWeightTarget)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(deptIds)) {
            throw new MithrasException("请维护部门分配信息");
        }

        for (Long deptId : deptIds) {
            // 利润归属部门负责人
            List<UserDO> businessHeaderList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.businesshead.name());
            if (CollectionUtil.isNotEmpty(businessHeaderList)) {
                for (UserDO userDO : businessHeaderList) {
                    businessHeaderIds.add(String.valueOf(userDO.getId()));
                }
            }

            // 分管领导
            List<UserDO> leaderList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.leaderincharge.name());
            if (CollectionUtil.isNotEmpty(leaderList)) {
                for (UserDO userDO : leaderList) {
                    leaderIds.add(String.valueOf(userDO.getId()));
                }
            }
        }

        // 获取部门负责人和分管领导 去重
        leaderIds = leaderIds.stream().distinct().collect(Collectors.toList());
        businessHeaderIds = businessHeaderIds.stream().distinct().collect(Collectors.toList());

        // 审批流参数
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("sponsorUser", sponsorUserIds);
        varMap.put("cosponsorUser", cosponsorUserIds);
        varMap.put("otherDeptRecommend", otherDeptRecommend);
        varMap.put("leaderincharge", leaderIds);
        varMap.put("teamLeader", teamLeaderIds);
        varMap.put("bizDeptLeader", businessHeaderIds);
        // 变更审批状态
        projectDistribution.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        this.updateById(projectDistribution);
        // 生成审批流
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (Objects.isNull(currentUserId)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        String currentUserName = sysUserService.getUserName(currentUserId);
        StartProcessReq startProcessReq = new StartProcessReq();
        ProcessModelTypeEnum processModelTypeEnum = Objects.equals(projectDistribution.getDistributionStatus(), YesOrNoNumberEnum.NO.getCode()) ? ProcessModelTypeEnum.KpiProjectDistributionCreateFlow : ProcessModelTypeEnum.KpiProjectDistributionModifyFlow;
        startProcessReq.setModelKey(processModelTypeEnum.name());
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(projectDistributionId));
        if (processModelTypeEnum == ProcessModelTypeEnum.KpiProjectDistributionCreateFlow) {
            startProcessReq.setProcessInstanceName(String.format("%s发起的关于【%s】合同的绩效考核项目分配创建流程", currentUserName, contractBaseInfo.getContractCode()));
        } else {
            startProcessReq.setProcessInstanceName(String.format("%s发起的关于【%s】合同的绩效考核项目分配变更流程", currentUserName, contractBaseInfo.getContractCode()));
        }
        startProcessReq.setStartUserId(String.valueOf(currentUserId));
        startProcessReq.setStartUserDeptId(String.valueOf(contractBaseInfo.getBizDeptId()));
        flowProcessApiService.start(startProcessReq);
    }

    public void checkBeforeSubmit(Long projectDistributionId) {
        KpiProjectDistributionBaseInfo baseInfo = kpiProjectDistributionBaseInfoService.getOneByProjectDistributionId(projectDistributionId);
        List<KpiProjectDistributionWeight> weightList = kpiProjectDistributionWeightService.listByProjectDistributionId(projectDistributionId);
        this.checkBeforeSubmit(baseInfo, weightList);
    }

    public void checkBeforeSubmit(KpiProjectDistributionBaseInfo baseInfo, List<KpiProjectDistributionWeight> weightList) {
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("基本信息不能为空");
        }
        if (Objects.isNull(baseInfo.getTeamLeaderId())) {
            throw new MithrasException("团队长不能为空");
        }
        if (StrUtil.isBlank(baseInfo.getRemark())) {
            throw new MithrasException("项目交接备注不能为空");
        }
        if (CollectionUtil.isEmpty(weightList)) {
            throw new MithrasException("分配比重不能为空");
        }
        int sum = weightList.stream().filter(e -> Objects.nonNull(e.getWeightValue())).mapToInt(KpiProjectDistributionWeight::getWeightValue).sum();
        if (sum != 1000000) {
            throw new MithrasException("分配比重加总必须等于100%");
        }
        int sponsorWeight = 0;
        int cosponsorWeight = 0;
        int businessDeptWeight = 0;
        for (KpiProjectDistributionWeight weight : weightList) {
            if (KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name().equalsIgnoreCase(weight.getWeightType())
                    && weight.getWeightValue() != null) {
                sponsorWeight += weight.getWeightValue();
            }
            if (KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name().equalsIgnoreCase(weight.getWeightType())
                    && weight.getWeightValue() != null) {
                cosponsorWeight += weight.getWeightValue();
            }
            if (KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equalsIgnoreCase(weight.getWeightType())
                    && weight.getWeightValue() != null) {
                businessDeptWeight += weight.getWeightValue();
            }
        }
        if (ObjectUtil.isEmpty(baseInfo.getSuppleDescribe())) {
            if (sponsorWeight > 0 && sponsorWeight < 500000) {
                throw new MithrasException("分配比重主办的比例需大于等于50%,请提供文字说明（必填）和材料说明（非必填）");
            }
            if (cosponsorWeight > 300000) {
                throw new MithrasException("分配比重协办的比例需小于等于30%,请提供文字说明（必填）和材料说明（非必填）");
            }
            if (businessDeptWeight > 100000) {
                throw new MithrasException("分配比重部门池的比例需小于等于10%,请提供文字说明（必填）和材料说明（非必填）");
            }
        }

    }

    @Transactional(rollbackFor = Throwable.class)
    public Long create(Long contractId, boolean notify) {
        KpiProjectDistribution exist = this.getOneByContractId(contractId);
        if (Objects.nonNull(exist)) {
            log.info("对应合同的项目分配记录已经存在，无需重复生成[contractId:{}]", contractId);
            return exist.getId();
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        // 保存主数据
        KpiProjectDistribution kpiProjectDistribution = new KpiProjectDistribution();
        kpiProjectDistribution.setContractId(contractId);
        kpiProjectDistribution.setDistributionStatus(YesOrNoNumberEnum.NO.getCode());
        kpiProjectDistribution.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
        this.save(kpiProjectDistribution);
        Long projectDistributionId = kpiProjectDistribution.getId();
        // 保存基本信息
        KpiProjectDistributionBaseInfo baseInfo = this.buildBaseInfo(projectDistributionId, contractBaseInfo);
        kpiProjectDistributionBaseInfoService.save(baseInfo);
        // 保存分配比重
        List<KpiProjectDistributionWeight> weightList = this.buildWeightList(projectDistributionId, contractBaseInfo);
        kpiProjectDistributionWeightService.saveBatch(weightList);
        // 生成合同主办对应的业务部门分润信息，100%
        KpiProjectDistributionDeptWeight deptWeight = new KpiProjectDistributionDeptWeight();
        deptWeight.setProjectDistributionId(projectDistributionId);
        // 不直接拿分配表的
        Long projSponsorUserId = contractBaseInfo.getProjSponsorUserId();
        OrgDO deptByUserId = sysUserService.getBizDeptByUserId(projSponsorUserId);
        if (Objects.isNull(deptByUserId)) {
            throw new MithrasException("合同主办对应的业务部门为空");
        }
        deptWeight.setWeightTarget(deptByUserId.getId());
        deptWeight.setWeightValue(1000000);
        SpringUtil.getBean(KpiProjectDistributionDeptWeightService.class).save(deptWeight);
        // 发送通知
        if (Objects.nonNull(baseInfo.getProfitBelongDeptId()) && notify) {
            ThreadPoolUtil.getCommonPool().execute(() -> {
//                List<UserDO> list = sysUserService.listSpecificOrgJobUser(baseInfo.getProfitBelongDeptId(), JobEnum.businesshead.name());
                if (Objects.nonNull(contractBaseInfo.getProjSponsorUserId())) {
                    // 发送通知
                    MessageAddREQ messageAddREQ = new MessageAddREQ();
                    messageAddREQ.setFrom("系统通知");
                    messageAddREQ.setTo(Collections.singletonList(contractBaseInfo.getProjSponsorUserId()));
                    messageAddREQ.setContent("绩效考核-项目分配表");
                    messageAddREQ.setFlowid(String.valueOf(projectDistributionId));
                    messageAddREQ.setRelation(String.format("<%s><%s>已投放，请及时填写项目分配表", baseInfo.getProjName(), baseInfo.getContractCode()));
                    messageAddREQ.setNoticeSource(NoticeSourceENUM.KPI_PROJECT_ALLOCATION.name());
                    messageAddREQ.setMessageType(MessageTypeEnum.KPI_PROJECT_ALLOCATION_REMIND.name());
                    messageAddREQ.setPcurl(MessageUrlEnum.KPI_PROJECT_ALLOCATION_LIST.pcUrl);
                    messageAddREQ.setAppurl(MessageUrlEnum.KPI_PROJECT_ALLOCATION_LIST.appUrl);
                    messageAddREQ.setBusinessId(String.valueOf(projectDistributionId));
                    messageService.sendMessage(messageConver.reqToMessage(messageAddREQ));
                }
            });
        }
        return baseInfo.getId();
    }

    public PageR<KpiProjectDistributionListRSP> pageList(KpiProjectDistributionListREQ req) {
        KpiProjectDistributionQuery dbQuery = this.buildQuery(req);
        // 获取项目集合
        int total = this.getBaseMapper().myPageListCount(dbQuery);
        if (total == 0) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        List<KpiProjectDistributionBO> dataList = this.getBaseMapper().myPageList(dbQuery);
        Set<Long> projectDistributionIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        Set<Long> bizDeptIds = new HashSet<>();
        for (KpiProjectDistributionBO bo : dataList) {
            projectDistributionIds.add(bo.getId());
            userIds.add(bo.getSponsorUserId());
            bizDeptIds.add(bo.getContractBelongDeptId());
        }

        // 获取列表信息最新生效的流程信息，3种流程皆可
        ProcessPageReq listReq = new ProcessPageReq();
        listReq.setPageIndex(1);
        listReq.setPageSize(Integer.MAX_VALUE);
        listReq.setModelKeyList(ListUtil.of(ProcessModelTypeEnum.KpiProjectDistributionCreateFlow.name(),
                ProcessModelTypeEnum.KpiProjectDistributionModifyFlow.name(), ProcessModelTypeEnum.KpiProjectDistributionTransferFlow.name()));
        listReq.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        listReq.setBusinessKeyList(dataList.stream().map(KpiProjectDistributionBO::getId).map(String::valueOf).collect(Collectors.toList()));
        List<ProcessResp> processList = SpringUtil.getBean(FlowTaskApiService.class).queryProcess(listReq).getContents();

        Map<Long, List<ProcessResp>> map = new HashMap<>();
        if (CollUtil.isNotEmpty(processList)) {
            map = processList.stream().collect(Collectors.groupingBy(e -> Long.valueOf(e.getBusinessKey())));
        }
        // 分配比重中的id也添加到对应的set中统一获取名称
        List<KpiProjectDistributionWeight> dbWeightList = kpiProjectDistributionWeightService.listByProjectDistributionIds(projectDistributionIds);
        if (CollectionUtil.isNotEmpty(dbWeightList)) {
            for (KpiProjectDistributionWeight kpdw : dbWeightList) {
                if (StrUtil.isBlank(kpdw.getWeightTarget())) {
                    continue;
                }
                if (KpiProjectWeightTypeEnum.BUSINESS_DEPT.name().equals(kpdw.getWeightType())) {
                    bizDeptIds.add(Long.valueOf(kpdw.getWeightTarget()));
                } else {
                    userIds.add(Long.valueOf(kpdw.getWeightTarget()));
                }
            }
        }
        Map<Long, List<KpiProjectDistributionWeight>> weightMap = dbWeightList.stream().collect(Collectors.groupingBy(KpiProjectDistributionWeight::getProjectDistributionId));
        // 部门分润比
        List<KpiProjectDistributionDeptWeight> dbDeptWeightList = kpiProjectDistributionDeptWeightService.listByProjectDistributionIds(projectDistributionIds);
        Map<Long, List<KpiProjectDistributionDeptWeight>> deptWeightMap = dbDeptWeightList.stream().collect(Collectors.groupingBy(KpiProjectDistributionDeptWeight::getProjectDistributionId));
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(bizDeptIds);
        List<KpiProjectDistributionListRSP> resultList = new LinkedList<>();
        for (KpiProjectDistributionBO bo : dataList) {
            KpiProjectDistributionListRSP rsp = new KpiProjectDistributionListRSP();
            rsp.setId(bo.getId());
            rsp.setContractCode(bo.getContractCode());
            rsp.setContractId(bo.getContractId());
            // 这个地方的取值逻辑先跟详情页保持一致
            rsp.setProjSource(kpiProjectDistributionBaseInfoService.ensureProjSource(bo.getProjReviewSource()).display());
            rsp.setProjClassify(bo.getProjClassify());
            rsp.setProjectDistributionId(bo.getProjectDistributionId());
            rsp.setDistributionStatus(bo.getDistributionStatus());
            rsp.setProjName(bo.getProjName());
            if (Objects.nonNull(bo.getContractStartDate())) {
                rsp.setContractStartDate(LocalDateTimeUtil.format(bo.getContractStartDate(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setBelongDeptId(bo.getContractBelongDeptId());
            rsp.setBelongDeptName(deptNameMap.get(rsp.getBelongDeptId()));
            rsp.setSponsorUserId(bo.getSponsorUserId());
            rsp.setSponsorUserName(userNameMap.get(rsp.getSponsorUserId()));
            rsp.setDistributionStatus(bo.getDistributionStatus());
            rsp.setApprovalStatus(bo.getApprovalStatus());
            List<KpiProjectDistributionWeight> weightList = weightMap.get(bo.getId());
            if (CollectionUtil.isNotEmpty(weightList)) {
                List<KpiProjectDistributionWeightInfo> weightInfoList = new ArrayList<>(weightList.size());
                for (KpiProjectDistributionWeight distributionWeight : weightList) {
                    weightInfoList.add(KpiProjectDistributionConvert.toKpiProjectDistributionWeightInfo(distributionWeight, userNameMap, deptNameMap));
                }
                rsp.setWeightInfoList(weightInfoList);
            }
            List<KpiProjectDistributionDeptWeight> deptWeightList = deptWeightMap.get(bo.getId());
            if (CollectionUtil.isNotEmpty(deptWeightList)) {
                List<KpiProjectDistributionDeptWeightInfo> deptWeightInfoList = new ArrayList<>(deptWeightList.size());
                for (KpiProjectDistributionDeptWeight deptWeight : deptWeightList) {
                    KpiProjectDistributionDeptWeightInfo deptWeightInfo = BeanUtil.copyProperties(deptWeight, KpiProjectDistributionDeptWeightInfo.class);
                    deptWeightInfo.setWeightTargetName(deptNameMap.get(deptWeight.getWeightTarget()));
                    deptWeightInfoList.add(deptWeightInfo);
                }
                rsp.setDeptWeightInfoList(deptWeightInfoList);
            }
            if (Objects.nonNull(bo.getEffectYear()) && Objects.nonNull(bo.getEffectMonth()) && Objects.equals(bo.getDistributionStatus(), YesOrNoNumberEnum.YES.getCode())) {
                LocalDate localDate = LocalDate.of(bo.getEffectYear(), bo.getEffectMonth(), 1);
                rsp.setEffectMonth(LocalDateTimeUtil.format(localDate, DatePattern.NORM_MONTH_PATTERN));
            }


            List<ProcessResp> processListList = map.get(bo.getId());
            if (CollectionUtil.isNotEmpty(processListList)) {
                processListList.sort(Comparator.comparing(ProcessResp::getEndTime).reversed());
                ProcessResp lastestProcess = processListList.get(0);
                rsp.setProcessId(lastestProcess.getProcessInstanceId());
                rsp.setProcessType(Optional.ofNullable(ProcessModelTypeEnum.getByName(lastestProcess.getModelKey())).map(ProcessModelTypeEnum::getDisplay).orElse(""));
                rsp.setLastApproveTime(LocalDateTimeUtil.of(lastestProcess.getEndTime()));
            }
            resultList.add(rsp);
        }
        return PageR.of(resultList, total, req.getPage(), req.getPageSize());
    }

    public List<KpiProjectDistributionPrevRSP> prev(KpiProjectDistributionPrevREQ req) {
        // 找到上个版本
        CommonVersion lastVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, req.getProjectDistributionId())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION.name())
                .lt(StringUtils.isNotBlank(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1")
        );
        if (Objects.isNull(lastVersion)) {
            return new ArrayList<>();
        }
        List<KpiProjectDistributionWeightLib> baseInfoLibList = libMapper.selectList(Wrappers.<KpiProjectDistributionWeightLib>lambdaQuery()
                .eq(KpiProjectDistributionWeightLib::getProjectDistributionId, lastVersion.getMainId())
                .eq(KpiProjectDistributionWeightLib::getVersion, lastVersion.getVersion())
        );
        List<KpiProjectDistributionWeightInfo> res = new ArrayList<>();
        for (KpiProjectDistributionWeightLib weightLib : baseInfoLibList) {
            KpiProjectDistributionWeightInfo weightInfo = handler.actualLib2Rsp(weightLib);
            res.add(weightInfo);
        }
        List<KpiProjectDistributionPrevRSP> rsp = new ArrayList<>();
        for (KpiProjectDistributionWeightInfo weightInfo : res) {
            KpiProjectDistributionPrevRSP prevRSP = BeanUtil.copyProperties(weightInfo, KpiProjectDistributionPrevRSP.class);
            rsp.add(prevRSP);
        }
        return rsp;
    }


    public List<KpiProjectDistributionHistoryRSP> history(KpiProjectDistributionHistoryREQ req) {
        // 基本信息版本数据
        List<KpiProjectDistributionBaseInfoLib> baseInfoLibList = kpiProjectDistributionBaseInfoLibService.listEffectByProjectDistributionId(req.getProjectDistributionId());
        if (CollectionUtil.isEmpty(baseInfoLibList)) {
            return Collections.emptyList();
        }
        // 分配比重版本数据
        List<KpiProjectDistributionWeightLib> weightLibList = kpiProjectDistributionWeightLibService.listEffectByProjectDistributionId(req.getProjectDistributionId());
        // 获取id-name的map
        Set<Long> deptIds = new HashSet<>();
        Set<Long> userIds = new HashSet<>();
        if (CollectionUtil.isNotEmpty(weightLibList)) {
            for (KpiProjectDistributionWeightLib weightLib : weightLibList) {
                if (StrUtil.isBlank(weightLib.getWeightTarget())) {
                    continue;
                }
                if (Objects.equals(weightLib.getWeightType(), KpiProjectWeightTypeEnum.BUSINESS_DEPT.name())) {
                    deptIds.add(Long.valueOf(weightLib.getWeightTarget()));
                } else {
                    userIds.add(Long.valueOf(weightLib.getWeightTarget()));
                }
            }
        }
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIds);
        // 按照版本号分组
        Map<String, KpiProjectDistributionBaseInfoLib> baseInfoLibMap = baseInfoLibList.stream().collect(Collectors.toMap(KpiProjectDistributionBaseInfoLib::getVersion, e -> e));
        Map<String, List<KpiProjectDistributionWeightLib>> weightLibMap = weightLibList.stream().collect(Collectors.groupingBy(KpiProjectDistributionWeightLib::getVersion));
        // 返回参数封装
        List<KpiProjectDistributionHistoryRSP> result = new LinkedList<>();
        for (Map.Entry<String, KpiProjectDistributionBaseInfoLib> entry : baseInfoLibMap.entrySet()) {
            String version = entry.getKey();
            KpiProjectDistributionBaseInfoLib baseInfoLib = entry.getValue();
            List<KpiProjectDistributionWeightLib> weightLibs = weightLibMap.get(version);
            result.add(KpiProjectDistributionConvert.toKpiProjectDistributionHistoryRSP(baseInfoLib, weightLibs, userNameMap, deptNameMap));
        }
        // 加工一下返回参数（排序、比对数据以确定是否需要标红）
        this.decorateHistory(result);
        return result;
    }

    private void decorateHistory(List<KpiProjectDistributionHistoryRSP> rsp) {
        if (CollectionUtil.isEmpty(rsp) || rsp.size() == 1) {
            return;
        }
        rsp.sort(Comparator.comparing(KpiProjectDistributionHistoryRSP::getVersion));
        for (int i = 1; i < rsp.size(); i++) {
            KpiProjectDistributionHistoryRSP rspEarly = rsp.get(i - 1);
            KpiProjectDistributionHistoryRSP rspLater = rsp.get(i);
            List<KpiProjectDistributionWeightInfoWithTag> weightListEarly = rspEarly.getWeightInfoWithTagList();
            weightListEarly.sort(Comparator.comparing(item -> item.getWeightType() + "-" + item.getWeightTarget()));
            List<KpiProjectDistributionWeightInfoWithTag> weightListLater = rspLater.getWeightInfoWithTagList();
            weightListLater.sort(Comparator.comparing(item -> item.getWeightType() + "-" + item.getWeightTarget()));
            Map<Long, KpiProjectDistributionWeightInfoWithTag> weightMapEarly = weightListEarly.stream().collect(Collectors.toMap(KpiProjectDistributionWeightInfo::getId, e -> e));
            Map<Long, KpiProjectDistributionWeightInfoWithTag> weightMapLater = weightListLater.stream().collect(Collectors.toMap(KpiProjectDistributionWeightInfo::getId, e -> e));
            for (Map.Entry<Long, KpiProjectDistributionWeightInfoWithTag> entry : weightMapLater.entrySet()) {
                KpiProjectDistributionWeightInfoWithTag later = entry.getValue();
                KpiProjectDistributionWeightInfoWithTag early = weightMapEarly.get(entry.getKey());
                if (Objects.isNull(early)) {
                    // 说明是新增的部门/人员
                    later.setWeightTargetNameRed(Boolean.TRUE);
                    later.setWeightValueRed(Boolean.TRUE);
                } else {
                    // 已存在的部门/人员需比对具体的分配占比
                    if (!Objects.equals(early.getWeightValue(), later.getWeightValue())) {
                        later.setWeightValueRed(Boolean.TRUE);
                    }
                }
            }
        }
    }

    private KpiProjectDistributionQuery buildQuery(KpiProjectDistributionListREQ req) {
        KpiProjectDistributionQuery query = new KpiProjectDistributionQuery();
        query.setStart((req.getPage() - 1) * req.getPageSize());
        query.setSize(req.getPageSize());
        query.setContractCode(req.getContractCode());
        query.setProjName(req.getProjName());
        query.setDistributionStatus(req.getDistributionStatus());
        query.setSponsorUserId(req.getSponsorUserId());
        query.setApprovalStatus(req.getApprovalStatus());
        query.setDeptId(req.getBizDeptId());
        query.setContractIds(req.getContractIds());
        if (Objects.nonNull(req.getBizDeptIdWeight()) || Objects.nonNull(req.getSponsorUserIdWeight()) || Objects.nonNull(req.getProjCosponsorUserIdWeight())) {
            query.setWeightFlag(1);
        }
        query.setBizDeptIdWeight(req.getBizDeptIdWeight());
        query.setSponsorUserIdWeight(req.getSponsorUserIdWeight());
        query.setProjCosponsorUserIdWeight(req.getProjCosponsorUserIdWeight());
        // 以下是权限条件
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        if (CollectionUtil.isNotEmpty(canViewDeptIds)) {
            query.setCurrentUserBizDeptId(canViewDeptIds);
        } else {
            // 区分项目经理和非业务部门人员
            if (sysUserService.currentUserIsSpecificJob(JobEnum.projmanager.name())) {
                query.setCurrentUserId(AccountUtil.getLoginInfo().getId());
                if (Objects.nonNull(query.getCurrentUserId())) {
                    query.setCurrentUserIdStr(String.valueOf(query.getCurrentUserId()));
                }
            }
        }
        return query;
    }

    private KpiProjectDistributionBaseInfo buildBaseInfo(Long projectDistributionId, ContractBaseInfo contractBaseInfo) {
        KpiProjectDistributionBaseInfo baseInfo = new KpiProjectDistributionBaseInfo();
        baseInfo.setProjectDistributionId(projectDistributionId);
        baseInfo.setContractCode(contractBaseInfo.getContractCode());
        baseInfo.setProjName(contractBaseInfo.getProjName());
        // 合同开始时间 --- 取最早的实际投放时间
        PaymentActualDetail paymentActualDetail = paymentActualDetailService.getEarliestPayment(contractBaseInfo.getId());
        if (Objects.nonNull(paymentActualDetail) && Objects.nonNull(paymentActualDetail.getPaidInDate())) {
            baseInfo.setContractStartDate(paymentActualDetail.getPaidInDate());
            baseInfo.setEffectYear(paymentActualDetail.getPaidInDate().getYear());
            baseInfo.setEffectMonth(paymentActualDetail.getPaidInDate().getMonthValue());
        }
        // 合同结束时间 --- 取概算租金表最后一期时间
        ContractRentEstimate contractRentEstimate = contractRentEstimateService.getLastOne(contractBaseInfo.getId());
        if (Objects.nonNull(contractRentEstimate)) {
            baseInfo.setContractEndDate(contractRentEstimate.getCashFlowDate());
        }
        baseInfo.setProfitBelongDeptId(contractBaseInfo.getBizDeptId());
        baseInfo.setChangeReason("创建");
        return baseInfo;
    }

    private List<KpiProjectDistributionWeight> buildWeightList(Long projectDistributionId, ContractBaseInfo contractBaseInfo) {
        List<KpiProjectDistributionWeight> list = new LinkedList<>();
        if (Objects.nonNull(contractBaseInfo.getBizDeptId())) {
            KpiProjectDistributionWeight distributionWeight = new KpiProjectDistributionWeight();
            distributionWeight.setProjectDistributionId(projectDistributionId);
            distributionWeight.setWeightType(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name());
            distributionWeight.setWeightTarget(String.valueOf(contractBaseInfo.getBizDeptId()));
            list.add(distributionWeight);
        }
        if (Objects.nonNull(contractBaseInfo.getProjSponsorUserId())) {
            KpiProjectDistributionWeight distributionWeight = new KpiProjectDistributionWeight();
            distributionWeight.setProjectDistributionId(projectDistributionId);
            distributionWeight.setWeightType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
            distributionWeight.setWeightTarget(String.valueOf(contractBaseInfo.getProjSponsorUserId()));
            list.add(distributionWeight);
        }
        if (StrUtil.isNotBlank(contractBaseInfo.getProjCosponsorUserIds())) {
            List<Long> cosponsorUserIds = JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), Long.class);
            for (Long id : cosponsorUserIds) {
                KpiProjectDistributionWeight distributionWeight = new KpiProjectDistributionWeight();
                distributionWeight.setProjectDistributionId(projectDistributionId);
                distributionWeight.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                distributionWeight.setWeightTarget(String.valueOf(id));
                list.add(distributionWeight);
            }
        }
        // 新建的时候默认添加一个跨部门推荐人
        KpiProjectDistributionWeight distributionWeight = new KpiProjectDistributionWeight();
        distributionWeight.setProjectDistributionId(projectDistributionId);
        distributionWeight.setWeightType(KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name());
        list.add(distributionWeight);
        return list;
    }

    private Long saveProjectDistribution(Long contractId) {
        KpiProjectDistribution kpiProjectDistribution = new KpiProjectDistribution();
        kpiProjectDistribution.setContractId(contractId);
        kpiProjectDistribution.setDistributionStatus(YesOrNoNumberEnum.YES.getCode());
        kpiProjectDistribution.setApprovalStatus(ProcessStatus.APPROVAL_PASS.name());
        this.save(kpiProjectDistribution);
        return kpiProjectDistribution.getId();
    }

    private void saveProjectDistributionBaseInfo(List<Object> row, Long projectDistributionId, ContractBaseInfo contractBaseInfo, Map<String, Long> orgMap, Map<String, Long> userMap) {
        String profitBelongDeptName = Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null);
        String startRentDate = Optional.ofNullable(row.get(14)).map(Object::toString).orElse(null);
        String projSource = Optional.ofNullable(row.get(3)).map(Object::toString).orElse(null);
        String projClassify = Optional.ofNullable(row.get(16)).map(Object::toString).orElse(null);
        if (StrUtil.isBlank(startRentDate)) {
            throw new MithrasException("起租日期为空");
        }
        LocalDate contractStartDate = LocalDateTimeUtil.parse(startRentDate, DatePattern.NORM_DATETIME_PATTERN).toLocalDate();
        Long profitBelongDeptId = orgMap.get(profitBelongDeptName);
        if (Objects.isNull(profitBelongDeptId)) {
            throw new MithrasException("利润归属部门id为空");
        }
        KpiProjectDistributionBaseInfo baseInfo = new KpiProjectDistributionBaseInfo();
        baseInfo.setProjectDistributionId(projectDistributionId);
        baseInfo.setChangeReason("历史数据导入");
        baseInfo.setContractCode(contractBaseInfo.getContractCode());
        baseInfo.setProfitBelongDeptId(profitBelongDeptId);
        baseInfo.setProjName(contractBaseInfo.getProjName());
        if (Objects.equals(projSource, "新增")) {
            baseInfo.setProjSource(KpiProjectSourceDistributionEnum.NEW.name());
        } else {
            baseInfo.setProjSource(KpiProjectSourceDistributionEnum.HISTORY.name());
        }
        if (Objects.equals(projClassify, "产业类")) {
            baseInfo.setProjClassify(KpiProjectClassifyEnum.INDUSTRY.name());
        } else if (Objects.equals(projClassify, "公共事业类")) {
            baseInfo.setProjClassify(KpiProjectClassifyEnum.PUBLIC.name());
        } else {
            baseInfo.setProjClassify(KpiProjectClassifyEnum.FACTORY.name());
        }
        baseInfo.setEffectYear(contractStartDate.getYear());
        baseInfo.setEffectMonth(contractStartDate.getMonthValue());
        baseInfo.setContractStartDate(contractStartDate);
        // 合同结束时间 --- 取概算租金表最后一期时间
        ContractRentEstimate contractRentEstimate = contractRentEstimateService.getLastOne(contractBaseInfo.getId());
        if (Objects.nonNull(contractRentEstimate)) {
            baseInfo.setContractEndDate(contractRentEstimate.getCashFlowDate());
        }
        kpiProjectDistributionBaseInfoService.save(baseInfo);
    }

    private void saveProjectDistributionWeight(List<Object> row, Long projectDistributionId, Map<String, Long> orgMap, Map<String, Long> userMap) {
        String sponsorUser1 = Optional.ofNullable(row.get(4)).map(Object::toString).orElse(null);
        String sponsorUser1Weight = Optional.ofNullable(row.get(5)).map(Object::toString).orElse(null);
        String sponsorUser2 = Optional.ofNullable(row.get(6)).map(Object::toString).orElse(null);
        String sponsorUser2Weight = Optional.ofNullable(row.get(7)).map(Object::toString).orElse(null);
        String cosponsorUser1 = Optional.ofNullable(row.get(8)).map(Object::toString).orElse(null);
        String cosponsorUser1Weight = Optional.ofNullable(row.get(9)).map(Object::toString).orElse(null);
        String cosponsorUser2 = Optional.ofNullable(row.get(10)).map(Object::toString).orElse(null);
        String cosponsorUser2Weight = Optional.ofNullable(row.get(11)).map(Object::toString).orElse(null);
        String deptName = Optional.ofNullable(row.get(12)).map(Object::toString).orElse(null);
        String deptWeight = Optional.ofNullable(row.get(13)).map(Object::toString).orElse(null);
        List<KpiProjectDistributionWeight> weightList = new LinkedList<>();
        if (StrUtil.isNotBlank(sponsorUser1) && StrUtil.isNotBlank(sponsorUser1Weight)) {
            Long userId = userMap.get(sponsorUser1);
            if (Objects.nonNull(userId)) {
                KpiProjectDistributionWeight weight = new KpiProjectDistributionWeight();
                weight.setProjectDistributionId(projectDistributionId);
                weight.setWeightType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
                weight.setWeightTarget(String.valueOf(userId));
                weight.setWeightValue(Integer.parseInt(sponsorUser1Weight) * 10000);
                weightList.add(weight);
            }
        }
        if (StrUtil.isNotBlank(sponsorUser2) && StrUtil.isNotBlank(sponsorUser2Weight)) {
            Long userId = userMap.get(sponsorUser2);
            if (Objects.nonNull(userId)) {
                KpiProjectDistributionWeight weight = new KpiProjectDistributionWeight();
                weight.setProjectDistributionId(projectDistributionId);
                weight.setWeightType(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name());
                weight.setWeightTarget(String.valueOf(userId));
                weight.setWeightValue(Integer.parseInt(sponsorUser2Weight) * 10000);
                weightList.add(weight);
            }
        }
        if (StrUtil.isNotBlank(cosponsorUser1) && StrUtil.isNotBlank(cosponsorUser1Weight)) {
            Long userId = userMap.get(cosponsorUser1);
            if (Objects.nonNull(userId)) {
                KpiProjectDistributionWeight weight = new KpiProjectDistributionWeight();
                weight.setProjectDistributionId(projectDistributionId);
                weight.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                weight.setWeightTarget(String.valueOf(userId));
                weight.setWeightValue(Integer.parseInt(cosponsorUser1Weight) * 10000);
                weightList.add(weight);
            }
        }
        if (StrUtil.isNotBlank(cosponsorUser2) && StrUtil.isNotBlank(cosponsorUser2Weight)) {
            Long userId = userMap.get(cosponsorUser2);
            if (Objects.nonNull(userId)) {
                KpiProjectDistributionWeight weight = new KpiProjectDistributionWeight();
                weight.setProjectDistributionId(projectDistributionId);
                weight.setWeightType(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name());
                weight.setWeightTarget(String.valueOf(userId));
                weight.setWeightValue(Integer.parseInt(cosponsorUser2Weight) * 10000);
                weightList.add(weight);
            }
        }
        if (StrUtil.isNotBlank(deptName) && StrUtil.isNotBlank(deptWeight)) {
            Long deptId = orgMap.get(deptName);
            if (Objects.nonNull(deptId)) {
                KpiProjectDistributionWeight weight = new KpiProjectDistributionWeight();
                weight.setProjectDistributionId(projectDistributionId);
                weight.setWeightType(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name());
                weight.setWeightTarget(String.valueOf(deptId));
                weight.setWeightValue(Integer.parseInt(deptWeight) * 10000);
                weightList.add(weight);
            }
        }
        if (CollectionUtil.isNotEmpty(weightList)) {
            kpiProjectDistributionWeightService.saveBatch(weightList);
        }
    }

    /**
     * 绩效管理项目分配表导出
     *
     * @param req 过滤参数
     * @throws IOException 异常
     */
    public void export(KpiProjectDistributionListREQ req) throws IOException {
        //设置到导出数据大小
        req.setPage(1);
        req.setPageSize(5000);
        List<KpiProjectDistributionListRSP> list = this.pageList(req).getList();

        if (CollUtil.isEmpty(list)) {
            throw new MithrasException("没有可以导出的数据");
        }

        String downloadFileName = URLEncoder.encode("项目分配表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(getHead());
        //合并单元格
        w.merge(0, 0, 11, 14, "人员分润比-" + KpiProjectWeightTypeEnum.BUSINESS_DEPT.display(), true);
        w.merge(0, 0, 15, 18, "人员分润比-" + KpiProjectWeightTypeEnum.PROJECT_SPONSOR.display(), true);
        w.merge(0, 0, 19, 22, "人员分润比-" + KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.display(), true);
        w.merge(0, 0, 23, 26, "人员分润比-" + KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.display(), true);
        w.merge(0, 0, 27, 30, "部门分润比", true);

        for (List<String> strings : getBodyList(list)) {
            w.writeRow(strings);
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @NotNull
    private List<List<String>> getBodyList(List<KpiProjectDistributionListRSP> list) {
        List<List<String>> bodyList = new ArrayList<>();
        for (KpiProjectDistributionListRSP dto : list) {
            List<String> body = new ArrayList<>();
            body.add(dto.getContractCode());
            body.add(Objects.requireNonNull(ProcessStatus.of(dto.getApprovalStatus())).display);
            body.add(dto.getProjName());
            body.add(dto.getContractStartDate());
            body.add(dto.getBelongDeptName());
            body.add(dto.getSponsorUserName());
            body.add(dto.getEffectMonth());
            body.add(dto.getProcessType());
            body.add(Objects.nonNull(dto.getProcessId()) ? dto.getProcessId() :  "");
            body.add(dto.getProjSource());
            body.add(Optional.ofNullable(dto.getLastApproveTime()).map(e ->e.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).orElse(""));
            List<KpiProjectDistributionWeightInfo> weightInfoList = dto.getWeightInfoList();
            if (CollUtil.isNotEmpty(weightInfoList)) {
                Map<String, List<KpiProjectDistributionWeightInfo>> infoMap = weightInfoList.stream().collect(Collectors.groupingBy(KpiProjectDistributionWeightInfo::getWeightType));
                //业务部门
                fillData(body, infoMap.get(KpiProjectWeightTypeEnum.BUSINESS_DEPT.name()));
                //项目主办
                fillData(body, infoMap.get(KpiProjectWeightTypeEnum.PROJECT_SPONSOR.name()));
                //项目协办
                fillData(body, infoMap.get(KpiProjectWeightTypeEnum.PROJECT_COSPONSOR.name()));
                //跨部门推荐人
                fillData(body, infoMap.get(KpiProjectWeightTypeEnum.OTHER_DEPT_RECOMMEND.name()));
            }
            if (CollUtil.isNotEmpty(dto.getDeptWeightInfoList())) {
                // 按照实际沟通情况，分润部门最多2个,目前导出Excel的表头也只做了2个，此处最多取2个
                int total = Math.min(dto.getDeptWeightInfoList().size(), 2);
                for (int i = 0; i < total; i++) {
                    KpiProjectDistributionDeptWeightInfo deptWeightInfo = dto.getDeptWeightInfoList().get(i);
                    body.add(deptWeightInfo.getWeightTargetName());
                    // 分润比正常都是整数
                    body.add(BigDecimal.valueOf(deptWeightInfo.getWeightValue()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString() + "%");
                }
                if (total == 1) {
                    // 补全空单元格
                    addString(body);
                }
            }
            bodyList.add(body);
        }
        return bodyList;
    }

    private void fillData(List<String> body, List<KpiProjectDistributionWeightInfo> list) {
        //1、没有数据
        if (CollUtil.isEmpty(list)) {
            addString(body);
            addString(body);
            return;
        }
        //2、两条数据，这里暂时只处理两条数据
        if (list.size() > 1) {
            for (int i = 0; i < 2; i++) {
                KpiProjectDistributionWeightInfo one = list.get(i);
                handler(body, one);
            }
        } else {
            //3、只有一条数据
            handler(body, list.get(0));
            addString(body);
        }

    }

    private void handler(List<String> body, KpiProjectDistributionWeightInfo weightInfo) {
        if (Objects.nonNull(weightInfo.getWeightTargetName())) {
            body.add(weightInfo.getWeightTargetName());
            if (Objects.nonNull(weightInfo.getWeightValue())) {
                body.add(BigDecimal.valueOf(weightInfo.getWeightValue()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString() + "%");
            } else {
                body.add("-");
            }
        } else {
            addString(body);
        }
    }

    private void addString(List<String> list) {
        list.add("");
        list.add("");
    }

    @NotNull
    private List<String> getHead() {
        List<String> head = new ArrayList<>();
        head.add("合同编号");
        head.add("审批状态");
        head.add("项目名称");
        head.add("投放日期");
        head.add("业务部门");
        head.add("项目主办");
        head.add("生效月份");
        head.add("流程类型");
        head.add("流程ID");
        head.add("项目来源");
        head.add("最后审批通过时间");
        for (int i = 0; i < 16; i++) {
            head.add("人员分润比");
        }
        for (int i = 0; i < 4; i++) {
            head.add("部门分润比");
        }
        return head;
    }
}
