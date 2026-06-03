package cn.zswltech.mithras.service.service.leaseholdproperty.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractFlowSubModuleEnum;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseItemInfoService;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseReviewService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;

/**
 * @ClassName LeaseReviewServiceImpl
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/9/26 1:52 下午
 * @Version 1.0
 **/
@Service
public class LeaseReviewServiceImpl implements LeaseReviewService {

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private BizProcessDataService bizProcessDataService;
    @Resource
    private SysUserService sysUserService;
    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;


    //创建，变更，本质都是创建，这里使用同一套 rocessModelTypeEnum.LeaseCreateFlow.name()
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void effect(Long leaseItemInfoId, ProcessModelTypeEnum processModelTypeEnum) {
        if(ObjectUtil.isEmpty(processModelTypeEnum)){
            throw new MithrasException("流程类型为空");
        }
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(leaseItemInfoId);
        if(ObjectUtil.isEmpty(leaseItemInfo)) {
            throw new MithrasException("租赁基本信息为空");
        }
        // 校验
        // 生成流程实例
        StartProcessReq startProcessReq = buildCommonStartProcessReq(leaseItemInfo, processModelTypeEnum);
        startProcessReq.setModelKey(processModelTypeEnum.name());
        startProcessReq.setSubModule(ContractFlowSubModuleEnum.CREATE_ALL.name());
        String processInstanceId = processApiService.start(startProcessReq);
        bizProcessDataService.recordBizData(processInstanceId, leaseItemInfo.getClientId());
        // 合同流程状态变更为新建审批中
        leaseItemInfo.setApprovalStatus(ProcessStatus.UNDER_APPROVAL.name());
        leaseItemInfo.setFlowId(processInstanceId);
        leaseItemInfoService.updateById(leaseItemInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void processEnd(Long leaseItemInfoId, Integer endType) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        LeaseItemInfo leaseItemInfo = leaseItemInfoService.getById(leaseItemInfoId);
        if(processPass) {
            leaseItemInfo.setApprovalStatus(ProcessStatus.APPROVAL_PASS.name());
        } else {
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                leaseItemInfo.setApprovalStatus(ProcessStatus.CANCELED.name());
            } else {
                leaseItemInfo.setApprovalStatus(ProcessStatus.APPROVAL_REJECT.name());
            }
        }
        leaseItemInfoService.updateById(leaseItemInfo);
        //回调填充合同信息
       contractBaseInfoService.modifyLeaseItemByProjReviewId(leaseItemInfo.getProjReviewId(), leaseItemInfo.getLeaseItemTypes());
    }

    @Override
    public void effectAndCreate(Long projReviewId, String flowId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        //项目存在审批中数据时不允许再次发起 产品说不在拦截，不管原流程
        /*if (leaseItemInfoService.count(Wrappers.<LeaseItemInfo>lambdaQuery()
                .eq(LeaseItemInfo::getProjReviewId, projReviewBaseInId())
                .eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.UNDER_APPROVAL.name())) > 0) {
            throw new MithrasException("该项目下已存在审批中租赁物流程");
        }*/
        //保存基本信息
        LeaseItemInfo leaseItemInfo = new LeaseItemInfo();
        leaseItemInfo.setProjReviewId(projReviewBaseInfo.getId());
        leaseItemInfo.setProjName(projReviewBaseInfo.getProjName());
        leaseItemInfo.setClientId(projReviewBaseInfo.getClientId());
        leaseItemInfo.setBizDeptId(projReviewBaseInfo.getBizDeptId());
        leaseItemInfo.setRelevanceFlowId(flowId);
        leaseItemInfo.setProjSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
        leaseItemInfo.setProjCosponsorUserIds(projReviewBaseInfo.getProjCosponsorUserIds());
        leaseItemInfo.setApprovalStatus(ProcessStatus.UN_SUBMIT.name());
        leaseItemInfoService.save(leaseItemInfo);
        //有创建就发变更
        ProcessModelTypeEnum processModelTypeEnum;
        if (leaseItemInfoService.count(Wrappers.<LeaseItemInfo>lambdaQuery()
                .eq(LeaseItemInfo::getProjReviewId, projReviewId)
                .eq(LeaseItemInfo::getApprovalStatus, ProcessStatus.APPROVAL_PASS.name())) > 0) {
            processModelTypeEnum = ProcessModelTypeEnum.LeaseModifyFlow;
        } else {
            processModelTypeEnum = ProcessModelTypeEnum.LeaseCreateFlow;
        }
        SpringContextHolder.getBean(LeaseReviewService.class).effect(leaseItemInfo.getId(), processModelTypeEnum);
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return
     */
    private StartProcessReq buildCommonStartProcessReq(LeaseItemInfo leaseItemInfo, ProcessModelTypeEnum processModelTypeEnum) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(leaseItemInfo.getId()));
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(leaseItemInfo.getProjReviewId());
        if (processModelTypeEnum == ProcessModelTypeEnum.LeaseCreateFlow) {
            startProcessReq.setProcessInstanceName(String.format("【%s】租赁物创建流程", projReviewBaseInfo.getProjName()));
        } else {
            startProcessReq.setProcessInstanceName(String.format("【%s】租赁物变更流程", projReviewBaseInfo.getProjName()));
        }
        startProcessReq.setStartUserId(String.valueOf(projReviewBaseInfo.getProjSponsorUserId()));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(projReviewBaseInfo.getBizDeptId()).map(String::valueOf).orElse(null));
        //运营经理
        List<Long> operationManagement = sysUserService.jobUsers(Collections.singleton("operationManagement"));
        //增加运营部领导
        Long yyglbDeptLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.YYGLB, FlowAssistService.YYGLB_DESC, businesshead.name());
        //法律合规部负责人
        Long flhgbHead = sysUserService.getUserIdByOrgJob(sysUserService.getOrgIdByCode("FLHGB_ZCBQ"), deptLeaderJob);
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projManager", Objects.isNull(projReviewBaseInfo.getProjSponsorUserId()) ? new ArrayList<>() : ListUtil.toList(String.valueOf(projReviewBaseInfo.getProjSponsorUserId()))),
                Pair.of("operationManagement", Objects.isNull(operationManagement) ? new ArrayList<>() : ListUtil.toList(String.valueOf(operationManagement))),// 运营经理
                Pair.of("yyglbDeptLeader", Objects.isNull(yyglbDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(yyglbDeptLeader))),
                Pair.of("legalManagerUser", Objects.nonNull(projReviewBaseInfo.getLegalManagerUserId()) ? ListUtil.toList(String.valueOf(projReviewBaseInfo.getLegalManagerUserId())) : new ArrayList<>()),
                Pair.of("legalManagerManagement",  Objects.isNull(flhgbHead) ? new ArrayList<>() : ListUtil.toList(String.valueOf(flhgbHead)))));
        return startProcessReq;
    }
}
