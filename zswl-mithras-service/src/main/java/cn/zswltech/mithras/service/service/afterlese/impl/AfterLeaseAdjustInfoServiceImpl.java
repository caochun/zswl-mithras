package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.afterlease.AfterLeaseAdjustConvert;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseAdjustEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.projlifecycle.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.service.flow.helper.CalBoardRuleHelper;
import cn.zswltech.mithras.service.mapper.afterlease.AfterLeaseAdjustInfoMapper;
import cn.zswltech.mithras.service.mapper.model.afterlease.AfterLeaseAdjustInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseAdjustInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjContext;
import cn.zswltech.mithras.service.service.projfms.ProjEvent;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projfms.impl.AfterLeaseAdjustMachine;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleEventService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewService;
import cn.zswltech.mithras.service.util.ApprovalTestUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 租后调整信息表
 *
 * @author vico
 * @date 2022-11-08
 */
@Service
public class AfterLeaseAdjustInfoServiceImpl extends ServiceImpl<AfterLeaseAdjustInfoMapper, AfterLeaseAdjustInfo> implements AfterLeaseAdjustInfoService {

    @Resource
    private AfterLeaseAdjustInfoMapper afterLeaseAdjustInfoMapper;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ProjReviewPriceService projReviewPriceService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private AfterLeaseAdjustConvert afterLeaseAdjustConvert;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AfterLeaseAdjustMachine stateMachine;
    @Resource
    private FlowProcessApiService processApiService;
    @Resource
    private CalBoardRuleHelper calBoardRuleHelper;
    @Resource
    private ProjectLifecycleEventService projectLifecycleEventService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ProjReviewService projReviewService;
    @Resource
    private BizProcessDataService bizProcessDataService;


    @Value("${mithras.job.deptLeader}")
    private String deptLeaderJob;
    @Value("${mithras.job.divisionLeader}")
    private String divisionLeaderJob;

    private static final String RSP_PROJ_FLOW= "此项目已经在审批流中，请重新选择";

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public AfterLeaseAdjustInfoAddRSP add(AfterLeaseAdjustInfoAddREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoLibService.getOldEdition(req.getProjId());
        if (ObjectUtil.isEmpty(projReviewBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //判断项目是否有流程，包括合同，收付款等
        checkProjReviewFlow(projReviewBaseInfo.getId());
        //判断是否有未提交的
        if(baseMapper.selectCount(Wrappers.<AfterLeaseAdjustInfo>lambdaQuery()
                .eq(AfterLeaseAdjustInfo::getProjId, req.getProjId())
                //.eq(AfterLeaseAdjustInfo::getAfterLeaseAdjustType, req.getAfterLeaseAdjustType())
                .in(AfterLeaseAdjustInfo::getAdjustProcessStatus, ListUtil.toList(ProcessStatus.UN_SUBMIT.name(), ProcessStatus.CANCELED.name(), ProcessStatus.UNDER_APPROVAL.name()))) > 0){
            throw new MithrasException("已存在未提交或审批中的流程！");
        }
        //判读是否有付款核销
        isWriteOff(projReviewBaseInfo.getId());
        ProjReviewPriceDetailRSP priceDetail = projReviewPriceService.oldDetail(projReviewBaseInfo.getId());
        AfterLeaseAdjustInfo afterLeaseAdjustInfo = afterLeaseAdjustConvert.projReviewBase2AfterLeaseAdjust(projReviewBaseInfo);
        OrgDO bizOrgDO = sysUserService.currentUserBizDept();

        //授信金额
        if (ObjectUtil.isNotEmpty(priceDetail.getAocPriceDetailRSP())) {
            afterLeaseAdjustInfo.setApplyCreditAmount(priceDetail.getAocPriceDetailRSP().getApplyCreditAmount());
        }
        if (ObjectUtil.isNotEmpty(priceDetail.getFactoringPriceDetailRSP())) {
            afterLeaseAdjustInfo.setApplyCreditAmount(priceDetail.getFactoringPriceDetailRSP().getApplyCreditAmount());
        }
        if (ObjectUtil.isNotEmpty(priceDetail.getLeasePriceDetailRSP())) {
            afterLeaseAdjustInfo.setApplyCreditAmount(priceDetail.getLeasePriceDetailRSP().getApplyCreditAmount());
        }
        afterLeaseAdjustInfo.setAfterLeaseAdjustType(req.getAfterLeaseAdjustType());
        afterLeaseAdjustInfo.setAdjustProcessStatus(ProcessStatus.UN_SUBMIT.name());
        afterLeaseAdjustInfo.setAdjustStatus(RecordStatus.NEW.name());
        //自动设置项目主办、业务部门，业务部门负责人，业务部门分管领导
        afterLeaseAdjustInfo.setProjSponsorUserId(AccountUtil.getLoginInfo().getId());
        afterLeaseAdjustInfo.setBizDeptId(bizOrgDO.getId());
        afterLeaseAdjustInfo.setBizDeptLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), deptLeaderJob));
        afterLeaseAdjustInfo.setBizDivisionLeaderId(sysUserService.getUserIdByOrgJob(bizOrgDO.getId(), divisionLeaderJob));
        afterLeaseAdjustInfo.setCreateTime(null);
        afterLeaseAdjustInfo.setUpdateBy(null);
        afterLeaseAdjustInfo.setCreateBy(AccountUtil.getLoginInfo().getId());
        afterLeaseAdjustInfo.setUpdateBy(AccountUtil.getLoginInfo().getId());
        afterLeaseAdjustInfoMapper.insert(afterLeaseAdjustInfo);
        return AfterLeaseAdjustInfoAddRSP.builder().adjustId(afterLeaseAdjustInfo.getId()).build();
    }

    private void checkProjReviewFlow(Long projId){
        //项目是否有流程
        if(ObjectUtil.isNotEmpty(projReviewService.findRelatedProcess(projId))){
            throw new MithrasException(RSP_PROJ_FLOW);
        }
        //验证项目下合同有无流程
        List<Long> contractIds = contractBaseInfoService.selectListByProjId(projId).stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(contractIds)){
            return;
        }
        if(ObjectUtil.isNotEmpty(findRelatedProcessesByList(BusinessModuleEnum.CONTRACT, contractIds))){
            throw new MithrasException(RSP_PROJ_FLOW);
        }
        //验证合同下有无收付款流程
        List<Long> paymentIds = paymentBaseInfoService.detailByContractIds(contractIds).stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(paymentIds)){
            return;
        }
        if(ObjectUtil.isNotEmpty(findRelatedProcessesByList(BusinessModuleEnum.PAYMENT, contractIds))){
            throw new MithrasException(RSP_PROJ_FLOW);
        }
    }

    private List<ProcessResp> findRelatedProcessesByList(BusinessModuleEnum businessModuleEnum, List<Long> ids) {
        List<String> businessIds = ids.stream().map(String::valueOf).collect(Collectors.toList());
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(10);
        processPageReq.setBusinessKeyList(businessIds);
        processPageReq.setModelKeyList(businessModuleEnum.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents();
    }

    private void isWriteOff(Long projId) {
        List<Long> contractIds = contractBaseInfoService.selectListByProjId(projId).stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        Map<Long, Pair<Long, Long>> longPairMap = paymentBaseInfoService.calculateCapitalDistributionBatch(contractIds);
        if (ObjectUtil.isEmpty(longPairMap)) {
            throw new MithrasException("该项目未放款，如需变更项目，请至【项目评审】提交变更申请！");
        }
        Long sum = 0L;
        for (Pair<Long, Long> pair : longPairMap.values()) {
            sum += pair.getValue();
        }
        if (ObjectUtil.equals(sum, 0L)) {
            throw new MithrasException("该项目未放款，如需变更项目，请至【项目评审】提交变更申请！");
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(AfterLeaseAdjustInfoModifyREQ req) {
        AfterLeaseAdjustInfo originalInfo = afterLeaseAdjustInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (RecordStatus.CLOSED.name().equals(originalInfo.getAdjustStatus()) || RecordStatus.TAKE_EFFECT.name().equals(originalInfo.getAdjustStatus())) {
            throw new MithrasException("项目调整已生效或关闭，不能提交审核");
        }
        AfterLeaseAdjustInfo info = afterLeaseAdjustConvert.AdjustInfoModifyREQ2LeaseAdjust(req);
        afterLeaseAdjustInfoMapper.updateById(info);
        if(ProcessStatus.CANCELED.name().equals(originalInfo.getAdjustProcessStatus())){
            stateMachine.execute(ProjContext.of(originalInfo, ProjEvent.MODIFY_SAVE, originalInfo.getProcessStatus()));
        }
    }

    @Override
    public Page<AfterLeaseAdjustInfoListRSP> list(AfterLeaseAdjustInfoListREQ req) {

        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        Page<AfterLeaseAdjustInfo> pageData = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<AfterLeaseAdjustInfo>lambdaQuery()
                .apply(ObjectUtil.isNotEmpty(req.getProjCosponsorUserId()), " json_contains(proj_cosponsor_user_ids, CONVERT ({0}, CHAR ))", req.getProjCosponsorUserId())
                .eq(ObjectUtil.isNotNull(req.getId()), AfterLeaseAdjustInfo::getId, req.getId())
                .eq(ObjectUtil.isNotEmpty(req.getClientId()), AfterLeaseAdjustInfo::getClientId, req.getClientId())
                .eq(ObjectUtil.isNotEmpty(req.getBizType()), AfterLeaseAdjustInfo::getBizType, req.getBizType())
                .eq(ObjectUtil.isNotEmpty(req.getBizDeptId()), AfterLeaseAdjustInfo::getBizDeptId, req.getBizDeptId())
                .eq(ObjectUtil.isNotEmpty(req.getProjSponsorUserId()), AfterLeaseAdjustInfo::getProjSponsorUserId, req.getProjSponsorUserId())
                .eq(ObjectUtil.isNotEmpty(req.getAfterLeaseAdjustType()), AfterLeaseAdjustInfo::getAfterLeaseAdjustType, req.getAfterLeaseAdjustType())
                .eq(ObjectUtil.isNotEmpty(req.getAdjustProcessStatus()), AfterLeaseAdjustInfo::getAdjustProcessStatus, req.getAdjustProcessStatus())
                //可看部门+主办+协办
                .and(isBizUser, wrapper -> wrapper.in(ObjectUtil.isNotEmpty(canViewDeptIds), AfterLeaseAdjustInfo::getBizDeptId, canViewDeptIds)
                        .or().eq(AfterLeaseAdjustInfo::getProjSponsorUserId, AccountUtil.getLoginInfo().getId())
                        .or().apply(" json_contains(proj_cosponsor_user_ids, CONVERT ({0}, CHAR ))", AccountUtil.getLoginInfo().getId())
                )
                .like(ObjectUtil.isNotEmpty(req.getProjName()), AfterLeaseAdjustInfo::getProjName, req.getProjName())
                .like(ObjectUtil.isNotEmpty(req.getProjCode()), AfterLeaseAdjustInfo::getProjCode, req.getProjCode())
                .between(ObjectUtil.isNotEmpty(req.getBeganTime()) && ObjectUtil.isNotEmpty(req.getEndTime()), AfterLeaseAdjustInfo::getCreateTime, req.getBeganTime(), req.getEndTime())
                .orderByDesc(AfterLeaseAdjustInfo::getCreateTime)
        );
        List<AfterLeaseAdjustInfoListRSP> resPage = new ArrayList<>(pageData.getRecords().size());
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        for (AfterLeaseAdjustInfo record : pageData.getRecords()) {
            AfterLeaseAdjustInfoListRSP rsp = afterLeaseAdjustConvert.baseInfo2AdjustInfoListRSP(record);
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                sysUserIds.addAll(rsp.getProjCosponsorUserIds());
            }
            sysUserIds.add(rsp.getProjSponsorUserId());
            clientIds.add(record.getClientId());
            deptIds.add(rsp.getBizDeptId());
            resPage.add(rsp);
        }
        Map<Long, String> clientMap = id2NameService.clientId2Name(clientIds);
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (AfterLeaseAdjustInfoListRSP rsp : resPage) {
            rsp.setBizDeptName(deptMap.get(rsp.getBizDeptId()));
            rsp.setProjSponsorUserName(sysUserMap.get(rsp.getProjSponsorUserId()));
            if (CollUtil.isNotEmpty(rsp.getProjCosponsorUserIds())) {
                rsp.setProjCosponsorUserNames(rsp.getProjCosponsorUserIds().stream().map(sysUserMap::get)
                        .collect(Collectors.toList()));
            }
            rsp.setClientName(clientMap.get(rsp.getClientId()));
        }
        return new Page<AfterLeaseAdjustInfoListRSP>()
                .setCurrent(pageData.getCurrent())
                .setRecords(resPage)
                .setSize(pageData.getSize())
                .setTotal(pageData.getTotal());
    }

    @Override
    public AfterLeaseAdjustDetailRSP detail(Long adjustId) {
        AfterLeaseAdjustInfo afterLeaseAdjustInfo = baseMapper.selectById(adjustId);
        AfterLeaseAdjustDetailRSP detailRSP = afterLeaseAdjustConvert.baseInfo2adjustDetailRSP(afterLeaseAdjustInfo);
        if(ObjectUtil.isEmpty(detailRSP)){
            return detailRSP;
        }
        ProjReviewPriceDetailRSP detail = projReviewPriceService.detail(afterLeaseAdjustInfo.getProjId());
        //租赁
        if(ObjectUtil.isNotEmpty(detail.getLeasePriceDetailRSP())){
            detailRSP.setLeaseMonthCount(detail.getLeasePriceDetailRSP().getLeaseMonthCount());
        }else if(ObjectUtil.isNotEmpty(detail.getFactoringPriceDetailRSP())){
            detailRSP.setLeaseMonthCount(detail.getFactoringPriceDetailRSP().getFactoringCreditTerm());
        }else if(ObjectUtil.isNotEmpty(detail.getAocPriceDetailRSP())){
            detailRSP.setLeaseMonthCount(detail.getAocPriceDetailRSP().getAocCreditTerm());
        }
        Set<Long> sysUserIds = new HashSet<>();
        sysUserIds.add(detailRSP.getProjSponsorUserId());
        if (CollUtil.isNotEmpty(detailRSP.getProjCosponsorUserIds())) {
            sysUserIds.addAll(detailRSP.getProjCosponsorUserIds());
        }
        sysUserIds.add(detailRSP.getBizDeptLeaderId());
        sysUserIds.add(detailRSP.getBizDivisionLeaderId());
        sysUserIds.add(detailRSP.getRiskControlManagerId());
        sysUserIds.add(detailRSP.getLegalManagerUserId());
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        detailRSP.setBizDeptName(id2NameService.sysUserId2Name(Collections.singleton(detailRSP.getBizDeptId())).get(detailRSP.getBizDeptId()));
        detailRSP.setProjSponsorUserName(sysUserMap.get(detailRSP.getProjSponsorUserId()));
        if (CollUtil.isNotEmpty(detailRSP.getProjCosponsorUserIds())) {
            detailRSP.setProjCosponsorUserNames(detailRSP.getProjCosponsorUserIds().stream().map(sysUserMap::get).collect(Collectors.toList()));
        }
        detailRSP.setBizDeptLeaderName(sysUserMap.get(detailRSP.getBizDeptLeaderId()));
        detailRSP.setBizDivisionLeaderName(sysUserMap.get(detailRSP.getBizDivisionLeaderId()));
        detailRSP.setRiskControlManagerName(sysUserMap.get(detailRSP.getRiskControlManagerId()));
        detailRSP.setLegalManagerName(sysUserMap.get(detailRSP.getLegalManagerUserId()));
        detailRSP.setBizDeptName(id2NameService.deptId2Name(Collections.singleton(detailRSP.getBizDeptId())).get(detailRSP.getBizDeptId()));
        return detailRSP;
    }

    @Override //查询最近审核通过的最大月数
    public AfterLeaseAdjustInfo adjustBaseLastByprojId(Long projId, String afterLeaseAdjustEnum) {
        return baseMapper.selectOne(Wrappers.<AfterLeaseAdjustInfo>lambdaQuery()
                .eq(AfterLeaseAdjustInfo::getProjId, projId)
                .eq(AfterLeaseAdjustInfo::getAfterLeaseAdjustType, afterLeaseAdjustEnum)
                .eq(AfterLeaseAdjustInfo::getAdjustProcessStatus, ProjProcessState.APPROVAL_PASS.name())
                .orderByDesc(AfterLeaseAdjustInfo::getExtensionmonth)
                .last(StringUtil.mysqlLimitOne())
        );
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void effect(Long adjustId) {
        AfterLeaseAdjustInfo baseInfo = baseMapper.selectById(adjustId);
        if (!Boolean.TRUE.equals(ApprovalTestUtil.getApprovalFlag())) {
            // 一般没有请求头的 都直接生成版本 不影响业务模块测试
            stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));
        } else {
            StartProcessReq startProcessReq = buildCommonStartProcessReq(baseInfo);
            String processInstanceId = processApiService.start(startProcessReq);
            // 记录客户id
            bizProcessDataService.recordBizData(processInstanceId, baseInfo.getClientId());
            String event;
            // 判断使用创建流程还是修改流程
            if (AfterLeaseAdjustEnum.EXTEND.name().equals(baseInfo.getAfterLeaseAdjustType())) {
                event = "租后管理展期审批";
            } else {
                event = "租后管理调整还款计划审批";
            }
            //项目全周期
            projectLifecycleEventService.add(event, ProjLifecycleEventTypeEnum.APPROVAL.name(), "提交审批", baseInfo.getProjId());
            stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.SUBMIT_APPROVAL, baseInfo.getProcessStatus()));
        }
    }

    @Override
    public void adjustCancel(AfterLeaseCancelREQ req) {
        AfterLeaseAdjustInfo baseInfo = baseMapper.selectById(req.getMainId());
        if(ObjectUtil.isEmpty(baseInfo)){
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if(!ProcessStatus.UN_SUBMIT.name().equals(baseInfo.getAdjustProcessStatus())){
            throw new MithrasException("项目不处于未提交状态，无法取消");
        }

        //产品要求删除
        baseMapper.deleteById(req.getMainId());
        //产品改需求不用校验项目下的合同 暂时保留，防止逻辑回退
        /*ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoLibService.getOldEdition(baseInfo.getProjId());
        //清空
        baseInfo.setExtensionmonth(null);
        baseInfo.setAdjustExplain(null);
        baseInfo.setLegalManagerUserId(projReviewBaseInfo.getLegalManagerUserId());
        baseInfo.setRiskControlManagerId(projReviewBaseInfo.getRiskControlManagerId());
        baseMapper.updateById(baseInfo);
        //取消
        stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_WITHDRAW, baseInfo.getProcessStatus()));*/
    }

    /**
     * 构建通用的启动流程参数
     * 还需自己填充 subModule 和 modelKey
     *
     * @return StartProcessReq
     */
    private StartProcessReq buildCommonStartProcessReq(AfterLeaseAdjustInfo baseInfo) {
        Boolean projReviewNeedBoardApproveFlag = Optional.of(calBoardRuleHelper.needBoardApprove(baseInfo.getProjId())).orElse(Boolean.FALSE);
        //初始化参数
        StartProcessReq startProcessReq = new StartProcessReq();
        //展期流程
        if (ObjectUtil.equals(AfterLeaseAdjustEnum.EXTEND.name(), baseInfo.getAfterLeaseAdjustType())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.AfterLeaseExtendFlow.name());
        } else if (ObjectUtil.equals(AfterLeaseAdjustEnum.REPAYMENT.name(), baseInfo.getAfterLeaseAdjustType())) {
            startProcessReq.setModelKey(ProcessModelTypeEnum.AfterLeaseRepaymentFlow.name());
        }else {
            throw new MithrasException("暂不支持此类型");
        }
        startProcessReq.setVariables(MapUtil.of(
            Pair.of("bizDeptLeader", Objects.nonNull(baseInfo.getBizDeptLeaderId()) ?
                    ListUtil.toList(String.valueOf(baseInfo.getBizDeptLeaderId())) : new ArrayList<>()),
            Pair.of("bizDivisionLeader", Objects.nonNull(baseInfo.getBizDivisionLeaderId()) ?
                    ListUtil.toList(String.valueOf(baseInfo.getBizDivisionLeaderId())) : new ArrayList<>()),
            Pair.of("riskControlManager", Objects.nonNull(baseInfo.getRiskControlManagerId()) ?
                    ListUtil.toList(String.valueOf(baseInfo.getRiskControlManagerId())) : new ArrayList<>()),
            Pair.of("legalManagerUser", Objects.nonNull(baseInfo.getLegalManagerUserId()) ?
                    ListUtil.toList(String.valueOf(baseInfo.getLegalManagerUserId())) : new ArrayList<>()),
            //判断上次是否走董事会
            Pair.of("InDirectorFlag", projReviewNeedBoardApproveFlag)
        ));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        startProcessReq.setBusinessKey(String.valueOf(baseInfo.getId()));
        //项目类型
        startProcessReq.setSubModule(baseInfo.getBizType());
        startProcessReq.setProcessInstanceName(baseInfo.getProjName());
        startProcessReq.setCcUserIdList(StringUtils.isBlank(baseInfo.getProjCosponsorUserIds()) ?
                new ArrayList<>() : JSONUtil.parseArray(baseInfo.getProjCosponsorUserIds()).toList(String.class));
        startProcessReq.setStartUserDeptId(Optional.ofNullable(baseInfo.getBizDeptId())
                .map(String::valueOf).orElse(null));
        return startProcessReq;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void processEnd(Long adjustId, Integer endType, Long startUserId, String processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        AfterLeaseAdjustInfo baseInfo = baseMapper.selectById(adjustId);
        if (processPass) {
            // 审批通过 新增版本
            stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.APPROVAL_PASS, baseInfo.getProcessStatus()));
        } else {
            if (ProcessBusinessStatusEnum.CANCEL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_WITHDRAW, baseInfo.getProcessStatus()));
            }
            if (ProcessBusinessStatusEnum.REJECT.getType().equals(endType) || ProcessBusinessStatusEnum.REJECT_ALL.getType().equals(endType)) {
                stateMachine.execute(ProjContext.of(baseInfo, ProjEvent.NEW_REJECT, baseInfo.getProcessStatus()));
            }
        }
    }

    @Override
    public ProcessResp findRelatedProcess(Long projReviewId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setBusinessKey(String.valueOf(projReviewId));
        processPageReq.setModelKeyList(BusinessModuleEnum.ADJUST.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents().stream().findFirst().orElse(null);
    }

    @Override
    public List<ProcessResp> findRelatedProcesses(Long adjustId) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(10);
        processPageReq.setBusinessKey(String.valueOf(adjustId));
        processPageReq.setModelKeyList(BusinessModuleEnum.ADJUST.getModelKeyList());
        processPageReq.setProcessStatusList(Arrays.asList(ProcessBusinessStatusEnum.RUNNING.getType(),
                ProcessBusinessStatusEnum.SUSPEND.getType()));
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processPageReq);
        return processRespPage.getContents();
    }

    @Override
    public void checkDetail(Long id) {
        AfterLeaseAdjustInfo baseInfo = getById(id);
        checkDetail(baseInfo);
    }

    @Override
    public void checkDetail(AfterLeaseAdjustInfo baseInfo) {
        //展期验证
        if(AfterLeaseAdjustEnum.EXTEND.name().equals(baseInfo.getAfterLeaseAdjustType())){
            if (ObjectUtil.isEmpty(baseInfo.getExtensionmonth())) {
                throw new MithrasException("展期月数不能为空。");
            }
        }
        if(ObjectUtil.isEmpty(baseInfo.getLegalManagerUserId()) ||  ObjectUtil.isEmpty(baseInfo.getRiskControlManagerId())) {
            throw new MithrasException("风控经理、法务经理不能为空。");
        }
    }

}