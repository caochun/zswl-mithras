package cn.zswltech.mithras.service.service.financeprofitdistribution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptWeightInfo;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.*;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.ProcessState;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.financeprofitdistribution.FinanceProjectDistributionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.financeprofitdistribution.FinanceProjectDistributionDeptLaunchWeight;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.mapper.model.financeprofitdistribution.FinanceProjectDistribution;
import cn.zswltech.mithras.service.mapper.model.financeprofitdistribution.FinanceProjectDistributionDeptWeight;
import cn.zswltech.mithras.service.mapper.process.prepare.CommonProcessPrepareMapper;
import cn.zswltech.mithras.service.mapper.financeprojectdistribution.FinanceProjectDistributionMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientTransferService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractRentEstimateService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.enums.CommonProcessPrepareStatus.COMMITTED;

/**
 * @author lllin
 * @date 2025-12-19
 * @description
 */
@Slf4j
@Service
public class FinanceProjectDistributionService extends ServiceImpl<FinanceProjectDistributionMapper, FinanceProjectDistribution> {
    @Resource
    private FinanceProjectDistributionBaseInfoService financeProjectDistributionBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractRentEstimateService contractRentEstimateService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
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
    private HttpServletResponse response;
    @Resource
    private ClientTransferService clientTransferService;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    CommonProcessPrepareMapper commonProcessPrepareMapper;
    @Resource
    ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private BizProcessDataService bizProcessDataService;

    /**
     * 根据合同id获取关联的项目利润分配信息
     *
     * @param contractId
     * @return
     */
    public FinanceProjectDistribution getOneByContractId(Long contractId) {
        LambdaQueryWrapper<FinanceProjectDistribution> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectDistribution::getContractId, contractId);
        query.orderByDesc(FinanceProjectDistribution::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public FinanceProjectDistribution getApprovedOneByContractId(Long contractId) {
        LambdaQueryWrapper<FinanceProjectDistribution> query = Wrappers.lambdaQuery();
        query.eq(FinanceProjectDistribution::getContractId, contractId);
        query.eq(FinanceProjectDistribution::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name());
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    /**
     * 提交审批
     *
     * @param projectDistributionId
     */
    @Transactional(rollbackFor = Throwable.class)
    public R<String> submit(Long projectDistributionId, boolean updateFlag) {
        FinanceProjectDistribution financeProjectDistribution = this.getById(projectDistributionId);
        if (Objects.isNull(financeProjectDistribution)) {
            throw new MithrasException("项目利润分配信息不存在");
        }
        // 已经存在流程中的数据不可以再次提交
        if (Objects.equals(financeProjectDistribution.getApprovalStatus(), ProcessStatus.UNDER_APPROVAL.name())) {
            throw new MithrasException("项目分配信息正在审批中，请稍后再试");
        }

        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(financeProjectDistribution.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        // 部门的分配信息校验
        List<FinanceProjectDistributionDeptWeightInfo> deptWeightInfos = SpringUtil.getBean(FinanceProjectDistributionDeptWeightService.class).queryList(projectDistributionId);
        if (CollectionUtil.isEmpty(deptWeightInfos)) {
            throw new MithrasException("请维护部门分配信息");
        }
        /*业务部门负责人*/
        List<String> leaderIds = new LinkedList<>();
        /*分管领导*/
        List<String> businessHeaderIds = new LinkedList<>();

        // 拿出所有的部门，再根据部门id获取部门负责人和分管领导
        List<Long> deptIds = deptWeightInfos.stream().map(FinanceProjectDistributionDeptWeightInfo::getWeightTarget)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(deptIds)) {
            throw new MithrasException("请维护部门分配信息");
        }

        for (Long deptId : deptIds) {
            // 部门负责人
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
        /*项目经理*/
        varMap.put("sponsorUser", Arrays.asList(String.valueOf(contractBaseInfo.getProjSponsorUserId())));
        //部门负责人
        varMap.put("leaderincharge", businessHeaderIds);
        //分管领导
        varMap.put("bizDeptLeader", leaderIds);
        // 变更审批状态
        financeProjectDistribution.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        this.updateById(financeProjectDistribution);
        // 生成审批流
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (Objects.isNull(currentUserId)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }


        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setModelKey(ProcessModelTypeEnum.ProjectProfitSharingFlow.name());
        startProcessReq.setVariables(varMap);
        startProcessReq.setBusinessKey(String.valueOf(projectDistributionId));
        startProcessReq.setProcessInstanceName(String.format("%s-项目利润分配", contractBaseInfo.getContractCode()));

        startProcessReq.setStartUserId(String.valueOf(currentUserId));
        startProcessReq.setStartUserDeptId(String.valueOf(contractBaseInfo.getBizDeptId()));
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, contractBaseInfo.getClientId());
        if (updateFlag) {
            LambdaUpdateWrapper<CommonProcessPrepare> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(CommonProcessPrepare::getStatus, COMMITTED.name());
            updateWrapper.eq(CommonProcessPrepare::getBusinessId, projectDistributionId);
            updateWrapper.eq(CommonProcessPrepare::getProcessType, ProcessModelTypeEnum.ProjectProfitSharingFlow.name());
            SpringUtil.getBean(CommonProcessPrepareService.class).update(updateWrapper);
        }
        return R.ok(processInstanceId);
    }

    /**
     * 合同创建审批通过后初始化待办
     *
     * @param contractId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void initProcessPrepare(Long contractId) {
        FinanceProjectDistribution exist = this.getOneByContractId(contractId);
        if (Objects.nonNull(exist) && !CharSequenceUtil.equalsAny(exist.getApprovalStatus(),
                ProcessStatus.CANCEL.name(),ProcessStatus.APPROVAL_REJECT.name(),ProcessStatus.CANCELED.name())) {
            log.info("对应合同的项目利润分配记录已经存在，无需重复生成[contractId:{}]", contractId);
            return;
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        // 保存主数据
        FinanceProjectDistribution financeProjectDistribution = new FinanceProjectDistribution();
        financeProjectDistribution.setContractId(contractId);
        financeProjectDistribution.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
        this.save(financeProjectDistribution);
        Long projectDistributionId = financeProjectDistribution.getId();
        // 保存基本信息
        FinanceProjectDistributionBaseInfo baseInfo = this.buildBaseInfo(projectDistributionId, contractBaseInfo);
        financeProjectDistributionBaseInfoService.save(baseInfo);

        // 生成合同主办对应的业务部门分润信息，100%
        FinanceProjectDistributionDeptWeight deptWeight = new FinanceProjectDistributionDeptWeight();
        deptWeight.setProjectDistributionId(projectDistributionId);

        // 不直接拿分配表的
        Long projSponsorUserId = contractBaseInfo.getProjSponsorUserId();
        OrgDO deptByUserId = sysUserService.getBizDeptByUserId(projSponsorUserId);
        if (Objects.isNull(deptByUserId)) {
            throw new MithrasException("合同主办对应的业务部门为空");
        }
        deptWeight.setWeightTarget(deptByUserId.getId());
        deptWeight.setWeightValue(1000000);
        /*初始化部门投放比*/
        FinanceProjectDistributionDeptLaunchWeight deptLaunchWeight = new FinanceProjectDistributionDeptLaunchWeight();
        BeanUtils.copyProperties(deptWeight, deptLaunchWeight);
        SpringUtil.getBean(FinanceProjectDistributionDeptWeightService.class).save(deptWeight);
        SpringUtil.getBean(FinanceProjectDistributionDeptLaunchWeightService.class).save(deptLaunchWeight);


        /*生成流程待办*/
        CommonProcessPrepare prepare = CommonProcessPrepare.builder()
                .processType(ProcessModelTypeEnum.ProjectProfitSharingFlow.name())
                .businessId(String.valueOf(projectDistributionId))
                .formName(String.format("%s-项目利润分配", contractBaseInfo.getContractCode()))
                .projName(contractBaseInfo.getProjName())
                .projCode(contractBaseInfo.getProjCode())
                .clientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()))
                .currentAssignee(JSONUtil.toJsonStr(Collections.singletonList(contractBaseInfo.getProjSponsorUserId())))
                .currentNode("项目经理")
                .applyTime(LocalDateTime.now())
                .status(CommonProcessPrepareStatus.PEND_COMMIT.name())
                .build();
        commonProcessPrepareMapper.insert(prepare);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(ProcessEndContext endContext) {
        Integer endType = endContext.getEndType();
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        FinanceProjectDistribution distribution = new FinanceProjectDistribution();
        ProcessStatus processState = null;
        boolean successFlag = false;
        if (processPass) {
            processState = ProcessStatus.APPROVAL_PASS;
            successFlag = true;
        } else {
            processState = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType) ? ProcessStatus.CANCEL: ProcessStatus.APPROVAL_REJECT;
        }
        distribution.setApprovalStatus(processState.name());
        this.update(distribution, Wrappers.<FinanceProjectDistribution>lambdaUpdate().eq(FinanceProjectDistribution::getId, endContext.getBusinessKey()));
        /*抄送财务经理、财务主管。*/
        List<Long> userIds = sysUserService.jobUsers(new HashSet<>(Arrays.asList(JobEnum.financialmanager.name(), JobEnum.financialofficer.name())));
        if (successFlag && CollUtil.isNotEmpty(userIds)) {
            FinanceProjectDistribution financeProjectDistribution = this.getById(endContext.getBusinessKey());
            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(financeProjectDistribution.getContractId());
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            req.setProcessInstanceId(endContext.getProcessInstanceId());
            req.setCcUserIdList(userIds);
            req.setMessage(String.format("【%s】-项目利润分配已审批通过", contractBaseInfo.getContractCode()));
            getBean(ExecutionService.class).cc(req);
        }
    }

    public boolean checkPassProcess(Long contractId) {
        if (Objects.isNull(this.getOneByContractId(contractId))) {
            return true;
        }
        FinanceProjectDistribution financeProjectDistribution = this.getApprovedOneByContractId(contractId);
        return Objects.nonNull(financeProjectDistribution);
    }


    private FinanceProjectDistributionBaseInfo buildBaseInfo(Long projectDistributionId, ContractBaseInfo contractBaseInfo) {
        FinanceProjectDistributionBaseInfo baseInfo = BeanCopyUtils.generatorObject(contractBaseInfo, FinanceProjectDistributionBaseInfo.class);
        baseInfo.setProjectDistributionId(projectDistributionId);
        return baseInfo;
    }
}
