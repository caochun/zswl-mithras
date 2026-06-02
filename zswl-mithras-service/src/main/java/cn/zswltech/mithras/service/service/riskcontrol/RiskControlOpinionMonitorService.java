package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskDataSourceEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskOpinionWarnCordType;
import cn.zswltech.mithras.riskcontrol.common.RiskRelationTypeEnum;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ProjClientRole;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.BizProcessDataService;
import cn.zswltech.mithras.service.service.FlowAssistService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.ClientVwSyncService;
import cn.zswltech.mithras.service.service.client.ProjClientRoleService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.service.constant.ResultMsg.RECORD_NOT_EXIST;
import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.service.enums.ProcessModelTypeEnum.*;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author
 * @description 舆情管理
 * @date 2023-03-09
 */
@Slf4j
@Service
public class RiskControlOpinionMonitorService extends ServiceImpl<RiskControlOpinionMonitorMapper, RiskControlOpinionMonitor> {

    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;
    @Resource
    private ClientService clientService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ClientVwSyncService clientVwSyncService;
    @Resource
    private RiskControlOpinionVersionService riskControlOpinionVersionService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(RiskControlOpinionMonitorAddREQ req) {
        RiskControlOpinionMonitor info = BeanUtil.copyProperties(req, RiskControlOpinionMonitor.class);
        riskControlOpinionMonitorMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(RiskControlOpinionMonitorModifyREQ req) {
        RiskControlOpinionMonitor originalInfo = riskControlOpinionMonitorMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        RiskControlOpinionMonitor info = BeanUtil.copyProperties(req, RiskControlOpinionMonitor.class);
        riskControlOpinionMonitorMapper.updateById(info);
    }

    @Resource
    private SysUserService sysUserService;

    public Page<RiskControlOpinionMonitor> list(RiskControlOpinionMonitorListREQ req, Long accountId) {
        LambdaQueryWrapper<RiskControlOpinionMonitor> wrapper = Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                .eq(isNotNull(req.getId()), RiskControlOpinionMonitor::getId, req.getId())
                .like(isNotEmpty(req.getChiName()), RiskControlOpinionMonitor::getChiName, req.getChiName())
                .like(isNotEmpty(req.getTitle()), RiskControlOpinionMonitor::getTitle, req.getTitle())
                .eq(isNotEmpty(req.getCreditCode()), RiskControlOpinionMonitor::getCreditCode, req.getCreditCode())
                .eq(isNotNull(req.getWarnLevel()), RiskControlOpinionMonitor::getWarnLevel, req.getWarnLevel())
                .eq(isNotNull(req.getHandleStatus()), RiskControlOpinionMonitor::getHandleStatus, req.getHandleStatus())
                .eq(isNotNull(req.getWarnStar()), RiskControlOpinionMonitor::getWarnStar, req.getWarnStar());

        // 新增：处理"未处理"状态且创建人非当前用户的记录
        wrapper.and(w -> w.ne(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.UNSUBMITTED.name())
                .or(e -> e.eq(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.UNSUBMITTED.name())
                        .eq(RiskControlOpinionMonitor::getCreateBy, accountId)));
        if (sysUserService.currentUserIsSpecificJob(JobEnum.assetmanagement.name()) && sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name())) {
            // 测试环境特殊性，两个角色都有，不加权限
            ;
        } else {
            this.fillUscCodesByJob(wrapper, false);
        }
        if (req.getClientId() != null) {
            Client byId = clientService.getById(req.getClientId());
            wrapper.eq(isNotNull(byId), RiskControlOpinionMonitor::getChiName, byId.getClientName());
        }
        if (isNotNull(req.getPublishDateFrom())) {
            wrapper.ge(RiskControlOpinionMonitor::getInfoPublDate, LocalDateTime.of(req.getPublishDateFrom(), LocalTime.MIN));
        }
        if (isNotNull(req.getPublishDateTo())) {
            wrapper.lt(RiskControlOpinionMonitor::getInfoPublDate, LocalDateTime.of(req.getPublishDateTo(), LocalTime.MAX));
        }
        wrapper.orderByDesc(RiskControlOpinionMonitor::getInfoPublDate);
        return riskControlOpinionMonitorMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), wrapper);
    }

    public void subCheck(Long id) {
        RiskControlOpinionMonitor riskControlWarnMonitor = riskControlOpinionMonitorMapper.selectById(id);
        if(ObjectUtil.isEmpty(riskControlWarnMonitor)){
            throw new MithrasException(RECORD_NOT_EXIST);
        }
        if(ObjectUtil.isEmpty(riskControlWarnMonitor.getHandleResult()) || ObjectUtil.isEmpty(riskControlWarnMonitor.getAdvisement())) {
            throw new MithrasException("请在详细信息页面右上角点击编辑按钮完成“是否处置”和“处置意见”的填写");
        }
    }

    public RiskWarnMonitorStatisticsRSP statisticsOpinion(RiskWarnMonitorStatisticsRSP rsp) {
        if (ObjectUtil.isEmpty(rsp)) {
            rsp = new RiskWarnMonitorStatisticsRSP();
        }
        // 之前是全量客户
        //rsp.setTotalMonitorClient(clientMapper.selectCount(Wrappers.lambdaQuery()));
        // 现在预警监控客户数同舆情一样是 查询client_vw视图
        rsp.setTotalMonitorClient(clientVwSyncService.count(Wrappers.lambdaQuery()));
        //今日舆情数
        if(ObjectUtil.isEmpty(rsp.getOpinionCard())) {
            rsp.setOpinionCard(new ArrayList<>());
        }
        rsp.getOpinionCard().add(RiskWarnMonitorStatisticsRSP.RiskWarnMonitorStatisticsBody.builder()
                .cardCode(RiskOpinionWarnCordType.OPINION.name())
                .cardCodeName(RiskOpinionWarnCordType.OPINION.display())
                .amount(riskControlOpinionMonitorMapper.countDealing())
                .todayAdd(riskControlOpinionMonitorMapper.countNewOp())
                .todayClose(riskControlOpinionMonitorMapper.countCloseOp())
                .build());
        return rsp;
    }

    //统计每天审批中的数据
    public List<RiskWarnMonitorQuantityChangeRSP> quantityChange(LocalDate began) {
        if(ObjectUtil.isEmpty(began)) {
            began = LocalDate.now().minusDays(4);
        }
        //查询流程中数据
        ProcessPageReq flowReq = new ProcessPageReq();
        flowReq.setModelKeyList(ListUtil.toList(ProcessModelTypeEnum.RiskControlOpinionHandleFlow.name(), ProcessModelTypeEnum.RiskControlOpinionHandleAfterLaunchFlow.name(), ProcessModelTypeEnum.RiskControlOpinionHandleCloseFlow.name(),
                ProcessModelTypeEnum.RiskControlOpinionHandleAfterLaunchCloseFlow.name(), ProcessModelTypeEnum.RiskControlNotPaymentFlow.name(), ProcessModelTypeEnum.RiskControlPaymentFlow.name()));
        flowReq.setSortType(1);
        flowReq.setPageIndex(1);
        flowReq.setPageSize(5000);
        //flowReq.setProcessCreateTimeFrom(Date.from(began.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        cn.zswltech.flow.core.util.Page<ProcessResp> flowRespPage = taskApiService.queryProcess(flowReq);
        if(ObjectUtil.isEmpty(flowRespPage) || ObjectUtil.isEmpty(flowRespPage.getContents())) {
            return null;
        }
        //计算每天数量
        RiskWarnMonitorQuantityChangeRSP rsp = new RiskWarnMonitorQuantityChangeRSP();
        rsp.setCardCode(RiskOpinionWarnCordType.OPINION.name());
        rsp.setCardCodeName(RiskOpinionWarnCordType.OPINION.display());
        Map<LocalDate, Integer> cardDetail = new HashMap<>();
        for (ProcessResp flow : flowRespPage.getContents()) {
            totalNumEveryDay(cardDetail, flow.getStartTime(), flow.getEndTime(), began);
        }
        rsp.setCardDetail(cardDetail);
        return CollectionUtil.toList(rsp);
    }

    private void totalNumEveryDay( Map<LocalDate, Integer> cardDetail, Date startTime, Date endTime, LocalDate began) {
        if (ObjectUtil.isEmpty(startTime)) {
            return;
        }
        LocalDate startLocalDate = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate;
        if (ObjectUtil.isEmpty(endTime)) {
            endLocalDate = LocalDate.now();
        } else {
            endLocalDate = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        for (LocalDate temp = startLocalDate; !temp.isAfter(endLocalDate); ) {
            if (!temp.isBefore(began)) {
                Integer orDefault = cardDetail.getOrDefault(temp, 0);
                cardDetail.put(temp, ++orDefault);
            }
            temp = temp.plusDays(1);
        }
    }

    public Page<RiskControlOpinionMonitor> opinionList(RiskWarnMonitorOpinionListREQ req) {
        LambdaQueryWrapper<RiskControlOpinionMonitor> wrapper = Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                .eq(isNotNull(req.getId()), RiskControlOpinionMonitor::getId, req.getId())
                .in(ObjectUtil.isNotEmpty(req.getIds()), RiskControlOpinionMonitor::getId, req.getIds())
                .like(isNotEmpty(req.getChiName()), RiskControlOpinionMonitor::getChiName, req.getChiName())
                .eq(isNotNull(req.getHandleStatus()), RiskControlOpinionMonitor::getHandleStatus, req.getHandleStatus());
        return riskControlOpinionMonitorMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), wrapper);
    }

    public void fillUscCodesByJob(LambdaQueryWrapper<RiskControlOpinionMonitor> wrapper, boolean isNotifyList) {
        List<PaymentActualDetail> qw = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        Set<Long> contractIds = qw.stream().map(PaymentActualDetail::getContractId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(contractIds)) {
            return;
        }

        Set<Long> launchedClientIds = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
                .in(ProjClientRole::getMainId, contractIds)
                .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name())
        ).stream().map(ProjClientRole::getClientId).collect(Collectors.toSet());

        Set<String> uscCodes = clientService.list(Wrappers.<Client>lambdaQuery()
                .in(Client::getId, launchedClientIds)
        ).stream().map(Client::getUscCode).filter(Objects::nonNull).collect(Collectors.toSet());
        // 根据当前登陆人的岗位判断展示的内容
        boolean isAssetManagement = sysUserService.currentUserIsSpecificJob(JobEnum.assetmanagement.name());
        boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name());
        // 既不是资产管理也不是风控经理则无需在通知列表展示，填一个不可能的值即可 -_-///
        if (!isAssetManagement && !isRiskManager && isNotifyList) {
            wrapper.eq(RiskControlOpinionMonitor::getCreditCode, "impossible");
            return;
        }
        if (isAssetManagement) {
            wrapper.in(RiskControlOpinionMonitor::getCreditCode, uscCodes);
        }
        if (isRiskManager) {
            wrapper.notIn(RiskControlOpinionMonitor::getCreditCode, uscCodes);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(RiskControlOpinionMonitorRemoveREQ req) {
        RiskControlOpinionMonitor originalInfo = riskControlOpinionMonitorMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        riskControlOpinionMonitorMapper.deleteById(req.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void handle(Long id, String advisement) {
        riskControlOpinionMonitorMapper.updateById(new RiskControlOpinionMonitor().setId(id)
                .setHandleStatus(RiskControlOpinionHandleStatus.HANDLE_ING.name())
                .setHandleResult(YesOrNoNumberEnum.YES.getCode())
                .setAdvisement(advisement));
        //
        RiskControlOpinionMonitor opinion = riskControlOpinionMonitorMapper.selectById(id);
        Client client = getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery().eq(Client::getUscCode, opinion.getCreditCode()));
        err(isNull(client), "此舆情的统一社会信用代码对应的客户未找到");

        //发起审批流
        StartProcessReq startProcessReq = buildCommonStartProcessReq(opinion, client);

        // 判断该客户关联的合同是否已存在已放款的
        Set<Long> contractIds = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
                        .eq(ProjClientRole::getClientId, client.getId())
                        .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()))
                .stream()
                .map(ProjClientRole::getMainId).collect(Collectors.toSet());
        List<PaymentActualDetail> paymentActualDetails = new ArrayList<>();
        if (isNotEmpty(contractIds)) {
            paymentActualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .in(PaymentActualDetail::getContractId, contractIds)
                    .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        }
        if (CollUtil.isEmpty(paymentActualDetails)) {
            boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name());
            if (!isRiskManager) {
                throw new MithrasException("仅<风控经理>可处理");
            }
            startProcessReq.setModelKey(RiskControlOpinionHandleFlow.name());
        } else {
            boolean isAssetManagement = sysUserService.currentUserIsSpecificJob(JobEnum.assetmanagement.name());
            if (!isAssetManagement) {
                throw new MithrasException("仅<资产管理>可处理");
            }
            startProcessReq.setModelKey(RiskControlOpinionHandleAfterLaunchFlow.name());
        }
        // 这里需要统一根据是否存在风险敞口来决定走什么分支
//        Map<Long, Long> clientStockRiskExposureMap = clientService.clientStockRiskExposureMap(Collections.singletonList(client.getId()));
//        Long riskExposure = clientStockRiskExposureMap.get(client.getId());
//        if (Objects.isNull(riskExposure) || riskExposure <= 0) {
//            boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name());
//            if (!isRiskManager) {
//                throw new MithrasException("仅<风控经理>可处理");
//            }
//            startProcessReq.setModelKey(RiskControlOpinionHandleFlow.name());
//        } else {
//            boolean isAssetManagement = sysUserService.currentUserIsSpecificJob(JobEnum.assetmanagement.name());
//            if (!isAssetManagement) {
//                throw new MithrasException("仅<资产管理>可处理");
//            }
//            startProcessReq.setModelKey(RiskControlOpinionHandleAfterLaunchFlow.name());
//        }
        String processInstanceId = getBean(FlowProcessApiService.class).start(startProcessReq);
        SpringContextHolder.getBean(BizProcessDataService.class).recordBizData(processInstanceId, client.getId());
    }

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    private StartProcessReq buildCommonStartProcessReq(RiskControlOpinionMonitor opinion, Client client) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(opinion.getId()));
        startProcessReq.setProcessInstanceName(String.format("【%s】舆情处置流程-%s", client.getClientName(), opinion.getId()));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        Long belongSponsorId = client.getBelongSponsorId();
        // 找到客户关联的合同或者评审中，投放金额最大的对应的项目主办
        if (Objects.isNull(belongSponsorId)) {
            belongSponsorId = this.ensureMaxAmountSponsor(client.getId());
        }
        err(isNull(belongSponsorId), "无法确定审批流中的项目主办");
        Long deptId = null;
        List<OrgDO> deptList = getBean(SysUserService.class).getSpecificUserDeptList(belongSponsorId);
        Optional<OrgDO> first = deptList.stream().filter(e -> OrgConstants.BUSINESS_DEPT == e.getType()).findFirst();
        if (first.isPresent()) {
            //首选业务部门
            deptId = first.get().getId();
        }
//        else if (!deptList.isEmpty()) {
//            deptId = deptList.get(0).getId();
//        }
        err(isNull(deptId), "项目主办的业务部门不存在");
        startProcessReq.setStartUserDeptId(deptId.toString());
        Long finalDeptId = deptId;
        Long originDeptLeader = getBean(SysUserService.class).getUserIdByOrgJob(finalDeptId, JobEnum.businesshead.name());
        Long originDivisionLeader = getBean(SysUserService.class).getUserIdByOrgJob(finalDeptId, JobEnum.leaderincharge.name());
        //FLHGB_ZCBQ，法律合规部
        Long flhgbLeader = SpringUtil.getBean(FlowAssistService.class).deptLeader(FlowAssistService.FLHGB, FlowAssistService.FLHGB_DESC, businesshead.name());
//        //查看该舆情客户相关的项目主办（包括合同，审批）
//        Client client = clientService.getOne(Wrappers.<Client>lambdaQuery().eq(Client::getUscCode, opinion.getCreditCode()));
//        Map<String, List<ProjClientRole>> moduleGroup = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
//                        .eq(ProjClientRole::getClientId, client.getId())).stream()
//                .collect(Collectors.groupingBy(ProjClientRole::getModuleType));
//        Set<String> projectSponsors = new HashSet<>();
//        moduleGroup.forEach((moduleType, projClientRoles) -> {
//            if (BusinessModuleEnum.CONTRACT.name().equals(moduleType)) {
//                contractBaseInfoService.listByIds(projClientRoles.stream().map(ProjClientRole::getMainId).collect(Collectors.toSet())).forEach(e -> projectSponsors.add(String.valueOf(e.getProjSponsorUserId())));
//
//            } else if (BusinessModuleEnum.PROJ_REVIEW.name().equals(moduleType)) {
//                projReviewBaseInfoService.listByIds(projClientRoles.stream().map(ProjClientRole::getMainId).collect(Collectors.toSet())).forEach(e -> projectSponsors.add(String.valueOf(e.getProjSponsorUserId())));
//            }
//        });
        //补充风控经理信息
        //风控经理 先按部门查询，部门没有查所有
        List<String> riskControlManagerIds = SpringContextHolder.getBean(SysUserService.class).getRiskManagerIdsOrderByDeptId().get(finalDeptId);
        if (CollectionUtil.isEmpty(riskControlManagerIds)) {
            riskControlManagerIds = sysUserService.getAllRiskControlManagerIds().stream().map(String::valueOf).collect(Collectors.toList());
        }
        startProcessReq.setVariables(MapUtil.of(
                Pair.of("projectSponsors", ListUtil.toList(String.valueOf(belongSponsorId))),
                Pair.of("warnLevel", isNull(opinion.getWarnLevel()) ? 1 : opinion.getWarnLevel()),
                Pair.of("deptLeader", Objects.isNull(originDeptLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(originDeptLeader))),
                Pair.of("divisionLeader", Objects.isNull(originDivisionLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(originDivisionLeader))),
                Pair.of("riskControlManager", riskControlManagerIds),
                Pair.of("flhgbDeptLeader", Objects.isNull(flhgbLeader) ? new ArrayList<>() : ListUtil.toList(String.valueOf(flhgbLeader)))
        ));
        return startProcessReq;
    }

    public Long ensureMaxAmountSponsor(Long clientId) {
        Map<String, List<ProjClientRole>> moduleGroup = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
                        .eq(ProjClientRole::getClientId, clientId)).stream()
                .collect(Collectors.groupingBy(ProjClientRole::getModuleType));
        // 优先找合同
        List<ProjClientRole> contractProjClientRoleList = moduleGroup.get(BusinessModuleEnum.CONTRACT.name());
        if (CollectionUtil.isNotEmpty(contractProjClientRoleList)) {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.listByIds(contractProjClientRoleList.stream().map(ProjClientRole::getMainId).collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(contractBaseInfoList)) {
                contractBaseInfoList.sort(Comparator.comparing(ContractBaseInfo::getApplyCreditAmount));
                return contractBaseInfoList.get(contractBaseInfoList.size() - 1).getProjSponsorUserId();
            }
        }
        // 没有合同找评审
        List<ProjClientRole> projReviewProjClientRoleList = moduleGroup.get(BusinessModuleEnum.PROJ_REVIEW.name());
        if (CollectionUtil.isNotEmpty(projReviewProjClientRoleList)) {
            List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.listByIds(projReviewProjClientRoleList.stream().map(ProjClientRole::getMainId).collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(projReviewBaseInfoList)) {
                projReviewBaseInfoList.sort(Comparator.comparing(ProjReviewBaseInfo::getDeclaredAmount));
                return projReviewBaseInfoList.get(projReviewBaseInfoList.size() - 1).getProjSponsorUserId();
            }
        }
        return null;
    }

    @Resource
    private FlowTaskApiService flowTaskApiService;

    public String getProcessInstanceId(Long id) {
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Arrays.asList(RiskControlOpinionHandleAfterLaunchFlow.name(), RiskControlOpinionHandleFlow.name()));
        processPageReq.setBusinessKey(String.valueOf(id));
        processPageReq.setSortType(1);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processPageReq);
        if (isEmpty(processRespPage.getContents())) {
            throw new MithrasException("该舆情处置不存在审批流程");
        }
        return processRespPage.getContents().get(0).getProcessInstanceId();
    }

    public int countByClientId(Long clientId) {
        // 获取客户的统一社会信用编码
        Client client = clientService.getById(clientId);
        if (Objects.isNull(client) || Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            return 0;
        }
        if (StrUtil.isBlank(client.getUscCode())) {
            return 0;
        }
        // 通过社会统一信用代码查询舆情
        LambdaQueryWrapper<RiskControlOpinionMonitor> query = Wrappers.lambdaQuery();
        query.eq(RiskControlOpinionMonitor::getCreditCode, client.getUscCode());
        query.in(RiskControlOpinionMonitor::getHandleStatus, Arrays.asList(RiskControlOpinionHandleStatus.PEND_HANDLE.name(), RiskControlOpinionHandleStatus.HANDLE_ING.name(), RiskControlOpinionHandleStatus.REJECTED.name()));
        return this.count(query);
    }

    public Map<String, Integer> countByClientIdList(Collection<Long> clientIds) {
        if (CollUtil.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        // 获取客户的统一社会信用编码
        List<Client> clients = clientService.listByIds(clientIds);
        clients = clients.stream().filter(e -> !Objects.equals(e.getClientType(), ClientType.NORMAL.name()) && StrUtil.isNotBlank(e.getUscCode()))
                .collect(Collectors.toList());
        // 通过社会统一信用代码查询舆情
        LambdaQueryWrapper<RiskControlOpinionMonitor> query = Wrappers.lambdaQuery();
        query.in(RiskControlOpinionMonitor::getCreditCode, clients.stream().map(Client::getUscCode).collect(Collectors.toList()));
        query.in(RiskControlOpinionMonitor::getHandleStatus, Arrays.asList(RiskControlOpinionHandleStatus.PEND_HANDLE.name(), RiskControlOpinionHandleStatus.HANDLE_ING.name(), RiskControlOpinionHandleStatus.REJECTED.name()));
        List<RiskControlOpinionMonitor> list = this.list(query);
        return list.stream().collect(Collectors.groupingBy(RiskControlOpinionMonitor::getChiName, Collectors.summingInt(e -> 1)));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void close(OpinionMonitorCloseREQ req) {
        // 首先找到当前待关闭舆情对应客户的实时风险敞口
        RiskControlOpinionMonitor riskControlOpinionMonitor = riskControlOpinionMonitorMapper.selectById(req.getId());
        err(isNull(riskControlOpinionMonitor), ResultMsg.RECORD_NOT_EXIST);
        // 更新状态为处理中
        riskControlOpinionMonitorMapper.update(null, Wrappers.lambdaUpdate(riskControlOpinionMonitor)
                .set(RiskControlOpinionMonitor::getHandleStatus, RiskControlOpinionHandleStatus.HANDLE_ING.name())
                .set(RiskControlOpinionMonitor::getAdvisement, req.getCloseReason())
                .set(RiskControlOpinionMonitor::getHandleResult, YesOrNoNumberEnum.NO.getCode())
                .eq(RiskControlOpinionMonitor::getId, riskControlOpinionMonitor.getId()));
        // 根据客户信用编码找到客户
        Client client = clientService.getOne(Wrappers.<Client>lambdaQuery()
                .eq(Client::getUscCode, riskControlOpinionMonitor.getCreditCode())
                .last(StringUtil.mysqlLimitOne()));
        err(isNull(client), String.format("客户【%s】%s", riskControlOpinionMonitor.getChiName(), ResultMsg.RECORD_NOT_EXIST));
//        Map<Long, Long> clientStockRiskExposureMap = clientService.clientStockRiskExposureMap(Collections.singletonList(client.getId()));
//        Long riskExposure = clientStockRiskExposureMap.get(client.getId());
        // 判断该客户关联的合同是否已存在已放款的
        Set<Long> contractIds = projClientRoleService.list(Wrappers.<ProjClientRole>lambdaQuery()
                        .eq(ProjClientRole::getClientId, client.getId())
                        .eq(ProjClientRole::getModuleType, BusinessModuleEnum.CONTRACT.name()))
                .stream()
                .map(ProjClientRole::getMainId).collect(Collectors.toSet());
        List<PaymentActualDetail> paymentActualDetails = new ArrayList<>();
        if (isNotEmpty(contractIds)) {
            paymentActualDetails = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .in(PaymentActualDetail::getContractId, contractIds)
                    .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        }
        StartProcessReq startProcessReq = buildCommonCloseProcessReq(riskControlOpinionMonitor);
        if (CollUtil.isEmpty(paymentActualDetails)) {
            boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name());
            if (!isRiskManager) {
                throw new MithrasException("仅<风控经理>可处理");
            }
            startProcessReq.setBusinessKey(String.valueOf(riskControlOpinionMonitor.getId()));
            startProcessReq.setProcessInstanceName("舆情处置（未放款）关闭审批");
            startProcessReq.setModelKey(RiskControlOpinionHandleCloseFlow.name());
        } else {
            boolean isAssetManagement = sysUserService.currentUserIsSpecificJob(JobEnum.assetmanagement.name());
            if (!isAssetManagement) {
                throw new MithrasException("仅<资产管理>可处理");
            }
            startProcessReq.setBusinessKey(String.valueOf(riskControlOpinionMonitor.getId()));
            startProcessReq.setProcessInstanceName("舆情处置（已放款）关闭审批");
            startProcessReq.setModelKey(RiskControlOpinionHandleAfterLaunchCloseFlow.name());
        }
//        if (isNull(riskExposure) || riskExposure <= 0) {
//            // 不存在敞口， 走风控相关的关闭流程
//            // 校验当前用户是不是风控经理
//            boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name());
//            if (!isRiskManager) {
//                throw new MithrasException("仅<风控经理>可处理");
//            }
//            startProcessReq.setModelKey(RiskControlOpinionHandleCloseFlow.name());
//        } else {
//            // 存在敞口，走资金相关的关闭流程
//            // 校验当前用户是不是资金经理
//            boolean isRiskManager = sysUserService.currentUserIsSpecificJob(JobEnum.assetmanagement.name());
//            if (!isRiskManager) {
//                throw new MithrasException("仅<资产管理>可处理");
//            }
//            startProcessReq.setModelKey(RiskControlOpinionHandleAfterLaunchCloseFlow.name());
//        }
        String processInstanceId = getBean(FlowProcessApiService.class).start(startProcessReq);
        SpringContextHolder.getBean(BizProcessDataService.class).recordBizData(processInstanceId, client.getId());
    }

    private StartProcessReq buildCommonCloseProcessReq(RiskControlOpinionMonitor opinion) {
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(opinion.getId()));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
        return startProcessReq;
    }


    // 保存手工录入舆情数据
    @Transactional(rollbackFor = Throwable.class)
    public void saveOpinion(RiskControlOpinionManualDetailREQ req){
        saveManualInfo(req,RiskControlOpinionHandleStatus.UNSUBMITTED.name());
    }

    // 提交手工录入舆情数据-走审批流
    @Transactional(rollbackFor = Throwable.class)
    public void submitOpinion(RiskControlOpinionManualDetailREQ req){
        RiskControlOpinionMonitor info = saveManualInfo(req,RiskControlOpinionHandleStatus.PEND_HANDLE.name());
        // 走审批流程
        if (Objects.equals(info.getHandleResult(), YesOrNoNumberEnum.YES.getCode())){
            // 走关闭流程
            OpinionMonitorCloseREQ param = new OpinionMonitorCloseREQ();
            param.setId(info.getId());
            param.setCloseReason(info.getAdvisement());
            this.close(param);
        }else {
            // 走舆情处置流程
            this.handle(info.getId(), info.getAdvisement());
        }

    }

    // 删除手工录入舆情数据
    @Transactional(rollbackFor = Throwable.class)
    public void deleteOpinion(SinglePkREQ req){
        RiskControlOpinionMonitor originalInfo = riskControlOpinionMonitorMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        riskControlOpinionMonitorMapper.deleteById(req.getId());
    }

    // 保存手工舆情信息
    private RiskControlOpinionMonitor saveManualInfo(RiskControlOpinionManualDetailREQ req,String handleStatus){
        RiskControlOpinionMonitor info = BeanUtil.copyProperties(req, RiskControlOpinionMonitor.class);
        info.setChiName(req.getClientName());
        info.setInfoPublDate(convertDate(req.getInfoPublDate()));
        // 根据关联关系类型设置关联关系描述
        if (StrUtil.isNotEmpty(req.getRelationType())){
            info.setRelationTypeName(Objects.requireNonNull(RiskRelationTypeEnum.findByName(req.getRelationType())).display());
        }
        info.setHandleStatus(handleStatus);
        info.setDataSource(RiskDataSourceEnum.MANUAL.name());
        // 保存或修改舆情数据
        this.saveOrUpdate(info);
        return info;
    }

    private LocalDateTime convertDate(String infoPublDate){
        // 解析日期字符串
        LocalDate date = LocalDate.parse(infoPublDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 转换为 LocalDateTime
        return date.atStartOfDay();
    }

    // 与数据库数据作对比并去重
    public List<RiskControlOpinionMonitor> existingDate(List<RiskControlOpinionMonitor> list){
        List<RiskControlOpinionMonitor> results = new ArrayList<>();
        // 1. 查询数据库中已存在的记录
        Set<String> existingKeys = getExistingKeysFromDatabase(list);
        log.info("数据库中已存在的数据量: {}", existingKeys.size());

        // 2. 过滤掉已存在的数据
        results = filterExistingData(list, existingKeys);
        log.info("需要插入的数据量: {}", results.size());

        if (results.isEmpty()) {
            log.info("所有数据在数据库中已存在，无需插入");
            return results;
        }

        return results;
    }

    /**
     * 查询数据库中已存在的记录
     */
    private Set<String> getExistingKeysFromDatabase(List<RiskControlOpinionMonitor> data) {
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptySet();
        }

        // 准备查询参数
        List<Map<String, Object>> params = new ArrayList<>();
        for (RiskControlOpinionMonitor item : data) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("title", item.getTitle());
            paramMap.put("creditCode", item.getCreditCode());
            paramMap.put("infoPublDate", item.getInfoPublDate());
            params.add(paramMap);
        }

        // 查询数据库中已存在的记录
        List<RiskControlOpinionMonitor> existingRecords = this.baseMapper.selectExistingRecords(params);

        // 转换为唯一键集合
        return existingRecords.stream()
                .map(RiskControlOpinionMonitor::getUniqueKey)
                .collect(Collectors.toSet());
    }

    /**
     * 过滤掉已存在的数据
     */
    private List<RiskControlOpinionMonitor> filterExistingData(
            List<RiskControlOpinionMonitor> allData,
            Set<String> existingKeys) {

        if (CollectionUtils.isEmpty(allData) || CollectionUtils.isEmpty(existingKeys)) {
            return allData;
        }

        List<RiskControlOpinionMonitor> filteredData = new ArrayList<>();

        for (RiskControlOpinionMonitor item : allData) {
            String uniqueKey = item.getUniqueKey();
            if (!existingKeys.contains(uniqueKey)) {
                filteredData.add(item);
            }
        }

        return filteredData;
    }

}