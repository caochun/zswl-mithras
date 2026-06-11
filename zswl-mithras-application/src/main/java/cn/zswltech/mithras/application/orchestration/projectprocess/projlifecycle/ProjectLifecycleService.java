package cn.zswltech.mithras.application.orchestration.projectprocess.projlifecycle;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListREQ;
import cn.zswltech.mithras.dto.afterlease.RentCollectionListRSP;
import cn.zswltech.mithras.dto.contract.price.ContractAocPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractFactoringPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.flow.search.BackToStepTaskListRSP;
import cn.zswltech.mithras.dto.flow.search.TaskListREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projlifecycle.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewAocPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewFactoringPriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.workbench.ClientProjectListRSP;
import cn.zswltech.mithras.contract.convert.contract.ContractPriceConverter;
import cn.zswltech.mithras.projectprocess.convert.projreview.ProjReviewPriceConverter;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractChangeTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractFlowSubModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjLifecycleEventTypeEnum;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjStageEnum;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.foundation.persistence.dto.*;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckExternalQuery;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanBase;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.model.LifecycleProjDO;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.model.ProjLifecycleEvent;
import cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence.ClientProjLifecycleListParam;
import cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence.ProjLifecycleListDTO;
import cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence.ProjLifecycleListSelectParam;
import cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence.ProjLifecycleStatisticParam;
import cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence.ProjLifecycleStatisticsDTO;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.*;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.projlifecycle.mapper.ProjLifecycleEventMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckExternalQueryService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.afterlease.application.impl.RentCollectionIndexServiceImpl;
import cn.zswltech.mithras.application.orchestration.workflow.flow.service.MyTaskService;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishPriceService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.extra.spring.SpringUtil.getBean;


/**
 * @create: 2022-10-24
 **/

@Slf4j
@Service
public class ProjectLifecycleService {

    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;

    @Resource
    private ProjEstablishPriceService projEstablishPriceService;

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ClientMapper clientMapper;

    @Resource
    private UserService userService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Resource
    private FlowProcessApiService flowProcessApiService;

    @Resource
    private ProjLifecycleEventMapper projLifecycleEventMapper;

    @Resource
    private MyTaskService myTaskService;

    @Resource
    private RentCollectionIndexServiceImpl rentCollectionIndexService;

    @Resource
    private AfterLeaseCheckPlanBaseService checkPlanBaseService;

    @Resource
    private AfterLeaseCheckPlanClientService checkPlanClientService;

    @Resource
    private AfterLeaseCheckExternalQueryService checkExternalQueryService;

    @Autowired
    private ContractLeasePriceLibService contractLeasePriceLibService;

    @Autowired
    private ContractAocPriceLibService contractAocPriceLibService;

    @Autowired
    private ContractFactoringPriceLibService contractFactoringPriceLibService;

    @Resource
    private ProjReviewLeasePriceLibService projReviewLeasePriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService projReviewFactoringPriceLibService;
    @Resource
    private ProjReviewAocPriceLibService projReviewAocPriceLibService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;
    @Resource
    private ProjReviewFactoringPriceService projReviewFactoringPriceService;
    @Resource
    private ProjReviewAocPriceService projReviewAocPriceService;
    @Resource
    private ProjReviewPriceConverter projReviewPriceConverter;

    @Resource
    private ContractPriceConverter priceConverter;

    @Deprecated
    public R<ProjStageCountRSP> count() {
        Map<String, List> all = getStagesRecord();
        List<ContractBaseInfo> settleStageContracts = all.get(ProjStageEnum.CONTRACTSETTLE_STAGE.name());
        long count = settleStageContracts.stream().filter(o -> o.getSettleTime() != null && o.getSettleTime().isAfter(getLastMonthLastDay())).count();

        ProjStageCountRSP rsp = new ProjStageCountRSP();
        ProjStageCountRSP.Stage contractSettle = new ProjStageCountRSP.Stage();
        contractSettle.setCount(settleStageContracts.stream().map(ContractBaseInfo::getProjReviewId).distinct().count());
        contractSettle.setMonthCount(count);
        rsp.setContractSettle(contractSettle);

        List<ContractBaseInfo> contractStage = all.get(ProjStageEnum.CONTRACT_STAGE.name());
        Map<Long, List<ContractBaseInfo>> collect = contractStage.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        long count1 = contractStage.stream().filter(o -> o.getCreateTime().isAfter(getLastMonthLastDay())).map(ContractBaseInfo::getProjReviewId).filter(o -> collect.get(o).size() == 1).distinct().count();
        ProjStageCountRSP.Stage contract = new ProjStageCountRSP.Stage();
        contract.setCount(contractStage.stream().filter(o -> o.getProjCode() != null).map(ContractBaseInfo::getProjCode).distinct().count());
        contract.setMonthCount(count1);
        rsp.setContract(contract);

        List<ProjReviewBaseInfo> projreviewStage = all.get(ProjStageEnum.PROJREVIEW_STAGE.name());
        long count2 = projreviewStage.stream().filter(o -> o.getCreateTime().isAfter(getLastMonthLastDay())).map(ProjReviewBaseInfo::getId).distinct().count();
        ProjStageCountRSP.Stage projreview = new ProjStageCountRSP.Stage();
        projreview.setCount(projreviewStage.stream().map(ProjReviewBaseInfo::getId).distinct().count());
        projreview.setMonthCount(count2);
        rsp.setProjreview(projreview);

        List<ProjEstablishBaseInfo> projestablishStage = all.get(ProjStageEnum.PROJESTABLISH_STAGE.name());
        long count3 = projestablishStage.stream().filter(o -> o.getCreateTime().isAfter(getLastMonthLastDay())).count();
        ProjStageCountRSP.Stage projestablish = new ProjStageCountRSP.Stage();
        projestablish.setCount((long) projestablishStage.size());
        projestablish.setMonthCount(count3);
        rsp.setProjestablish(projestablish);


        ProjStageCountRSP.Stage totalProj = new ProjStageCountRSP.Stage();
        totalProj.setCount(contractSettle.getCount() + contract.getCount() + projreview.getCount() + projestablish.getCount());
        totalProj.setMonthCount(count + count1 + count2 + count3);
        rsp.setTotalProj(totalProj);

        return R.ok(rsp);
    }

    public R<ProjStageCountRSP> countNew() {
        ProjStageCountRSP rsp = new ProjStageCountRSP();
        ProjLifecycleStatisticParam dto = new ProjLifecycleStatisticParam();
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        dto.setIsBizUser(isBizUser);
        dto.setDeptIdList(canViewDeptIds);
        dto.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        dto.setAddLimitTime(getLastMonthLastDay());
        ProjLifecycleStatisticsDTO statisticsDTO = projEstablishBaseInfoMapper.lifecycleStatistics(dto);
        // 合同结清
        ProjStageCountRSP.Stage contractSettle = new ProjStageCountRSP.Stage();
        contractSettle.setCount(statisticsDTO.getContractSettleCount());
        contractSettle.setMonthCount(statisticsDTO.getAddContractSettleCount());
        rsp.setContractSettle(contractSettle);
        // 合同
        ProjStageCountRSP.Stage contract = new ProjStageCountRSP.Stage();
        contract.setCount(statisticsDTO.getContractCount());
        contract.setMonthCount(statisticsDTO.getAddContractCount());
        rsp.setContract(contract);
        // 评审
        ProjStageCountRSP.Stage review = new ProjStageCountRSP.Stage();
        review.setCount(statisticsDTO.getReviewCount());
        review.setMonthCount(statisticsDTO.getAddReviewCount());
        rsp.setProjreview(review);
        // 立项
        ProjStageCountRSP.Stage establish = new ProjStageCountRSP.Stage();
        establish.setCount(statisticsDTO.getEstablishCount());
        establish.setMonthCount(statisticsDTO.getAddEstablishCount());
        rsp.setProjestablish(establish);
        // 全量统计
        ProjStageCountRSP.Stage totalProj = new ProjStageCountRSP.Stage();
        totalProj.setCount(statisticsDTO.getTotalCount());
        totalProj.setMonthCount(statisticsDTO.getAddTotalCount());
        rsp.setTotalProj(totalProj);

        return R.ok(rsp);
    }

    private LocalDateTime getLastMonthLastDay() {
        return LocalDateTimeUtil.endOfDay(LocalDateTime.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
    }

    private Map<String, List> getStagesRecord() {
        return getStagesRecord("All_Stages_Record");
    }

    private Map<String, List> getStagesRecord(String projStage) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name()));
        List<ContractBaseInfo> settleContracts = contractBaseInfos.stream().filter(o -> o.getContractStatus().equals(ContractStatus.SETTLE.name())).collect(Collectors.toList());
        Set<Long> settleIds = contractBaseInfos.stream().filter(o -> !o.getContractStatus().equals(ContractStatus.SETTLE.name())).map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
        Set<ContractBaseInfo> settleStageContracts = settleContracts.stream().filter(o -> !settleIds.contains(o.getProjReviewId())).collect(Collectors.toSet());
        Map<String, List> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(settleStageContracts)) {
            map.put(ProjStageEnum.CONTRACTSETTLE_STAGE.name(), new ArrayList<>(settleStageContracts));
        }
        if (ProjStageEnum.CONTRACTSETTLE_STAGE.name().equals(projStage)) {
            return map;
        }
        List<ContractBaseInfo> contractStage = contractBaseInfos.stream().filter(o -> !settleStageContracts.contains(o)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(contractStage)) {
            map.put(ProjStageEnum.CONTRACT_STAGE.name(), contractStage);
        }
        if (ProjStageEnum.CONTRACT_STAGE.name().equals(projStage)) {
            return map;
        }
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()).or()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.NEW.name()));
        Set<Long> reviewIds = contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
        List<ProjReviewBaseInfo> projreviewStage = projReviewBaseInfos.stream().filter(o -> !reviewIds.contains(o.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(projreviewStage)) {
            map.put(ProjStageEnum.PROJREVIEW_STAGE.name(), projreviewStage);
        }
        if (ProjStageEnum.PROJREVIEW_STAGE.name().equals(projStage)) {
            return map;
        }
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name()).or()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.NEW.name()));
        Set<Long> ids = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjEstablishId).collect(Collectors.toSet());
        List<ProjEstablishBaseInfo> projestablishStage = projEstablishBaseInfos.stream().filter(o -> !ids.contains(o.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(projreviewStage)) {
            map.put(ProjStageEnum.PROJESTABLISH_STAGE.name(), projestablishStage);
        }
        if (ProjStageEnum.PROJESTABLISH_STAGE.name().equals(projStage)) {
            return map;
        }

//
        return map;
    }

//    private List<Long> getStagesIds(String projStage){
//        Map<String,List> stagesCodes;
//        if (ProjStageEnum.CONTRACTSETTLE_STAGE.name().equals(projStage)){
//            stagesCodes = getStagesRecord(projStage);
//            List<ContractBaseInfo> list = stagesCodes.get(projStage);
//            List<Long> ids = list.stream().map(ContractBaseInfo::getProjReviewId).distinct().collect(Collectors.toList());
//            List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, ids));
//            return projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjEstablishId).distinct().collect(Collectors.toList());
//        }
//        if (ProjStageEnum.CONTRACT_STAGE.name().equals(projStage)){
//            stagesCodes = getStagesRecord(projStage);
//            List<ContractBaseInfo> list = stagesCodes.get(projStage);
//            List<Long> ids = list.stream().map(ContractBaseInfo::getProjReviewId).distinct().collect(Collectors.toList());
//            List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, ids));
//            return projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getProjEstablishId).distinct().collect(Collectors.toList());
//        }
//        if (ProjStageEnum.PROJREVIEW_STAGE.name().equals(projStage)){
//            stagesCodes = getStagesRecord(projStage);
//            List<ProjReviewBaseInfo> list = stagesCodes.get(projStage);
//            return list.stream().map(ProjReviewBaseInfo::getProjEstablishId).distinct().collect(Collectors.toList());
//        }
//        if (ProjStageEnum.PROJESTABLISH_STAGE.name().equals(projStage)){
//            stagesCodes = getStagesRecord(projStage);
//            List<ProjEstablishBaseInfo> list = stagesCodes.get(projStage);
//            return list.stream().map(ProjEstablishBaseInfo::getId).distinct().collect(Collectors.toList());
//        }
//        return null;
//    }

    public String getProjStageById(Long establishId, Long reviewId) {
        if (null == reviewId) {
            return ProjStageEnum.PROJESTABLISH_STAGE.name();
        }
        LifecycleProjDO lifecycleProj = projLifecycleEventMapper.getProjSettle(reviewId);
        if (lifecycleProj == null) {
            return ProjStageEnum.PROJREVIEW_STAGE.name();
        }
        if (lifecycleProj.getSettled() == 0) {
            return ProjStageEnum.CONTRACT_STAGE.name();
        }
        if (lifecycleProj.getSettled() == 1) {
            return ProjStageEnum.CONTRACTSETTLE_STAGE.name();
        }
        return null;
    }

    public PageR<ProjectLifecycleListRSP> list(ProjectLifecycleListREQ req) {
        log.info("ProjectLifecycleService list start req = {}", req);
        StopWatch sw = new StopWatch();
        sw.start();
        ProjLifecycleListSelectParam param = new ProjLifecycleListSelectParam();
        BeanUtil.copyProperties(req, param);
        param.setCreateFrom(req.getProjestablishFrom());
        param.setCreateTo(req.getProjestablishTo());
        List<Long> canViewDeptIds = sysUserService.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        if (isBizUser && canViewDeptIds.isEmpty()) {
            //防止sql in报错
            canViewDeptIds.add(Long.MIN_VALUE);
        }
        param.setIsBizUser(isBizUser);
        param.setDeptIdList(canViewDeptIds);
        param.setCurrentUserId(AccountUtil.getLoginInfo().getId());
        Page<ProjLifecycleListDTO> baseInfoPage = projEstablishBaseInfoMapper.lifecycleList(new Page<>(req.getPage(), req.getPageSize()), param);
        List<ProjectLifecycleListRSP> rsps = projEstablishList2RspList(baseInfoPage.getRecords(), req.getProjStage());

        sw.stop();
        log.info("ProjectLifecycleService list end use time = {}ms", sw.getLastTaskTimeMillis());
        return PageR.of(rsps, baseInfoPage.getTotal(),
                baseInfoPage.getPages(),
                baseInfoPage.getCurrent(),
                baseInfoPage.getSize());
    }


    public PageR<ClientProjectListRSP> workbenchClientList(ClientProjLifecycleListParam param) {
        Page<ProjLifecycleListDTO> baseInfoPage = projEstablishBaseInfoMapper.clientLifecycleList(new Page<>(param.getPage(), param.getPageSize()), param);
        List<ClientProjectListRSP> rsps = isNotEmpty(baseInfoPage.getRecords()) ? projEstablishList2clientRspList(baseInfoPage.getRecords()) : ListUtil.empty();
        return PageR.of(rsps, baseInfoPage.getTotal(),
                baseInfoPage.getPages(),
                baseInfoPage.getCurrent(),
                baseInfoPage.getSize());
    }

    public List<ClientProjectListRSP> projEstablishList2clientRspList(List<ProjLifecycleListDTO> records) {
        List<ClientProjectListRSP> rsps = new LinkedList<>();
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<Long> contractStageIds = new ArrayList<>();
        List<Long> reviewStageIds = new ArrayList<>();

        Map<String, List<Long>> stageIds = new HashMap<>();
        stageIds.put(ProjStageEnum.CONTRACT_STAGE.name(), new ArrayList<>());
        stageIds.put(ProjStageEnum.PROJESTABLISH_STAGE.name(), new ArrayList<>());
        stageIds.put(ProjStageEnum.PROJREVIEW_STAGE.name(), new ArrayList<>());
        for (ProjLifecycleListDTO baseInfo : records) {
            sysUserIds.add(baseInfo.getProjSponsorUserId());
            clientIds.add(baseInfo.getClientId());
            deptIds.add(baseInfo.getBizDeptId());
            if (ProjStageEnum.CONTRACT_STAGE.name().equals(baseInfo.getProjStage()) || ProjStageEnum.CONTRACTSETTLE_STAGE.name().equals(baseInfo.getProjStage())) {
                contractStageIds.add(baseInfo.getProjReviewId());
                stageIds.get(ProjStageEnum.CONTRACT_STAGE.name()).add(baseInfo.getId());
            }
            if (!ProjStageEnum.PROJESTABLISH_STAGE.name().equals(baseInfo.getProjStage())) {
                reviewStageIds.add(baseInfo.getProjReviewId());
            }
            if (ProjStageEnum.PROJESTABLISH_STAGE.name().equals(baseInfo.getProjStage())) {
                stageIds.get(ProjStageEnum.PROJESTABLISH_STAGE.name()).add(baseInfo.getId());
            }
            if (ProjStageEnum.PROJREVIEW_STAGE.name().equals(baseInfo.getProjStage())) {
                stageIds.get(ProjStageEnum.PROJREVIEW_STAGE.name()).add(baseInfo.getId());
            }
        }
        Map<Long, List<ContractBaseInfo>> reviewMap = new HashMap<>();
        Map<Long, ContractPriceDetailRSP> contractPriceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(contractStageIds)) {
            List<ContractBaseInfo> infos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjReviewId, contractStageIds)
                    .notIn(ContractBaseInfo::getContractStatus, Arrays.asList("CLOSED", "INVALID")));
            List<Long> contractIds = infos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            reviewMap = infos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
            contractPriceMap = getContractPriceMap(contractIds);
        }
        Map<Long, ProjReviewPriceDetailRSP> reviewPriceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(reviewStageIds)) {
            reviewPriceMap = getReviewPriceMap(reviewStageIds, true);
        }
        Map<Long, Client> clientMap = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIds)
            ).stream().collect(Collectors.toMap(Client::getId, o -> o));
        }
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        for (ProjLifecycleListDTO baseInfo : records) {
            ClientProjectListRSP rsp = new ClientProjectListRSP();
            rsp.setProjectId(baseInfo.getId());
            rsp.setKey(baseInfo.getKey());
//            rsp.setProjStage(projStage == null ? getStageName(stagesCodes, baseInfo.getId(), baseInfo.getDataType()) : projStage);
            rsp.setProjStage(baseInfo.getProjStage());
            rsp.setProjectName(baseInfo.getProjName());
            rsp.setBizDeptName(deptMap.get(baseInfo.getBizDeptId()));
            rsp.setProjSponsorUserId(baseInfo.getProjSponsorUserId());
            rsp.setProjSponsorUserName(sysUserMap.get(baseInfo.getProjSponsorUserId()));
            rsp.setClientId(baseInfo.getClientId());
            Client client = clientMap.get(baseInfo.getClientId());
            if (client != null) {
                rsp.setClientName(client.getClientName());
                rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                rsp.setClientType(client.getClientType());
            }
            rsp.setBizType(getDisplayBizType(baseInfo.getBizType(), baseInfo.getLeaseTypes()));
            rsp.setDataType(baseInfo.getDataType());
            if (ProjStageEnum.CONTRACT_STAGE.name().equals(baseInfo.getProjStage()) || ProjStageEnum.CONTRACTSETTLE_STAGE.name().equals(baseInfo.getProjStage())) {
                List<ContractBaseInfo> contractBaseInfos = reviewMap.get(baseInfo.getProjReviewId());
                if (CollUtil.isNotEmpty(contractBaseInfos)) {
                    for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                        ContractPriceDetailRSP priceDetailRSP = contractPriceMap.get(contractBaseInfo.getId());
                        Long contractAmount = priceDetailRSP != null ? LongUtil.null2zero(priceDetailRSP.getApplyCreditAmount()) : 0L;
                        rsp.setContractAmount(LongUtil.null2zero(rsp.getContractAmount()) + LongUtil.null2zero(contractAmount));
                    }
                }
            }
            if (baseInfo.getProjReviewId() != null) {
                ProjReviewPriceDetailRSP priceDetailRSP = reviewPriceMap.get(baseInfo.getProjReviewId());
                rsp.setApplyCreditAmount(priceDetailRSP != null ? LongUtil.null2zero(priceDetailRSP.getApplyCreditAmount()) : 0);
            }
            rsps.add(rsp);
        }
        return rsps;
    }

    public List<ProjectLifecycleListRSP> projEstablishList2RspList(List<ProjLifecycleListDTO> records, String projStage) {
        List<ProjectLifecycleListRSP> rsps = new LinkedList<>();
//        Map<String, List> stagesCodes = null;
//        if (projStage == null) {
//            stagesCodes = getStagesRecord();
//        }
        Set<Long> clientIds = new HashSet<>();
        Set<Long> sysUserIds = new HashSet<>();
        Set<Long> deptIds = new HashSet<>();
        List<Long> contractStageIds = new ArrayList<>();
        List<Long> reviewStageIds = new ArrayList<>();

        Map<String, List<Long>> stageIds = new HashMap<>();
        stageIds.put(ProjStageEnum.CONTRACT_STAGE.name(), new ArrayList<>());
        stageIds.put(ProjStageEnum.PROJESTABLISH_STAGE.name(), new ArrayList<>());
        stageIds.put(ProjStageEnum.PROJREVIEW_STAGE.name(), new ArrayList<>());
        for (ProjLifecycleListDTO baseInfo : records) {
            sysUserIds.add(baseInfo.getProjSponsorUserId());
            clientIds.add(baseInfo.getClientId());
            deptIds.add(baseInfo.getBizDeptId());
            if (ProjStageEnum.CONTRACT_STAGE.name().equals(baseInfo.getProjStage()) || ProjStageEnum.CONTRACTSETTLE_STAGE.name().equals(baseInfo.getProjStage())) {
                contractStageIds.add(baseInfo.getProjReviewId());
                stageIds.get(ProjStageEnum.CONTRACT_STAGE.name()).add(baseInfo.getId());
            }
            if (!ProjStageEnum.PROJESTABLISH_STAGE.name().equals(baseInfo.getProjStage())) {
                reviewStageIds.add(baseInfo.getProjReviewId());
            }
            if (ProjStageEnum.PROJESTABLISH_STAGE.name().equals(baseInfo.getProjStage())) {
                stageIds.get(ProjStageEnum.PROJESTABLISH_STAGE.name()).add(baseInfo.getId());
            }
            if (ProjStageEnum.PROJREVIEW_STAGE.name().equals(baseInfo.getProjStage())) {
                stageIds.get(ProjStageEnum.PROJREVIEW_STAGE.name()).add(baseInfo.getId());
            }
        }
        Map<Long, List<ContractBaseInfo>> reviewMap = new HashMap<>();
        Map<Long, ContractPriceDetailRSP> contractPriceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(contractStageIds)) {
            List<ContractBaseInfo> infos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjReviewId, contractStageIds)
                    .notIn(ContractBaseInfo::getContractStatus, Arrays.asList("CLOSED", "INVALID")));
            List<Long> contractIds = infos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            reviewMap = infos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
            contractPriceMap = getContractPriceMap(contractIds);
        }
        Map<Long, ProjReviewPriceDetailRSP> reviewPriceMap = new HashMap<>();
        if (CollUtil.isNotEmpty(reviewStageIds)) {
            reviewPriceMap = getReviewPriceMap(reviewStageIds, true);
        }
        Map<Long, Client> clientMap = new HashMap<>();
        if (isNotEmpty(clientIds)) {
            clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, clientIds)
            ).stream().collect(Collectors.toMap(Client::getId, o -> o));
        }
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
        Map<Long, ProcessResp> latestProcessMap = getLatestProcessMap(stageIds);
        for (ProjLifecycleListDTO baseInfo : records) {
            ProjectLifecycleListRSP rsp = new ProjectLifecycleListRSP();
//            rsp.setProjectId(baseInfo.getId());
//            rsp.setKey(baseInfo.getKey());
//            rsp.setProjStage(projStage == null ? getStageName(stagesCodes, baseInfo.getId(), baseInfo.getDataType()) : projStage);
            rsp.setProjStage(baseInfo.getProjStage());
            rsp.setProjectName(baseInfo.getProjName());
            rsp.setProjestablishTime(baseInfo.getCreateTime().toLocalDate());
            rsp.setBizDeptName(deptMap.get(baseInfo.getBizDeptId()));
            rsp.setProjSponsorUserId(baseInfo.getProjSponsorUserId());
            rsp.setProjSponsorUserName(sysUserMap.get(baseInfo.getProjSponsorUserId()));
            rsp.setClientId(baseInfo.getClientId());
            Client client = clientMap.get(baseInfo.getClientId());
            if (client != null) {
                rsp.setClientType(client.getClientType());
                rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                rsp.setClientName(client.getClientName());
            }
            rsp.setBizType(getDisplayBizType(baseInfo.getBizType(), baseInfo.getLeaseTypes()));
            rsp.setDataType(baseInfo.getDataType());
            /*if (ProjStageEnum.CONTRACT_STAGE.name().equals(baseInfo.getProjStage()) || ProjStageEnum.CONTRACTSETTLE_STAGE.name().equals(baseInfo.getProjStage())) {
                List<ContractBaseInfo> contractBaseInfos = reviewMap.get(baseInfo.getProjReviewId());
                if (CollUtil.isNotEmpty(contractBaseInfos)) {
                    for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                        ContractPriceDetailRSP priceDetailRSP = contractPriceMap.get(contractBaseInfo.getId());
                        Long contractAmount = priceDetailRSP != null?LongUtil.null2zero(priceDetailRSP.getApplyCreditAmount()) : 0L;
                        rsp.setContractAmount(LongUtil.null2zero(rsp.getContractAmount()) + LongUtil.null2zero(contractAmount));
                    }
                }
            }*/
            if (baseInfo.getProjReviewId() != null) {
                ProjReviewPriceDetailRSP priceDetailRSP = reviewPriceMap.get(baseInfo.getProjReviewId());
                rsp.setApplyCreditAmount(priceDetailRSP != null ? LongUtil.null2zero(priceDetailRSP.getApplyCreditAmount()) : 0);
            }
            ProcessResp processResp = latestProcessMap.get(baseInfo.getId());
            if (processResp != null) {
                rsp.setProcessType(processResp.getModelName());
                if (processResp.getProcessStatus() == 2 || processResp.getProcessStatus() == 6) {
                    rsp.setApplyTime(processResp.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    rsp.setCurrentNode(getCurNode(processResp.getLastOperatorId()));
                } else if (processResp.getProcessStatus() == 1) {
                    rsp.setCurrentNode(getCurNode(processResp.getCurAssigneeIds()));
                }
            }
            rsps.add(rsp);
        }
        return rsps;
    }

    private Map<Long, ProcessResp> getLatestProcessMap(Map<String, List<Long>> stageIds) {
        Map<Long, ProcessResp> latestMap = new HashMap<>();
        if (CollUtil.isNotEmpty(stageIds.get(ProjStageEnum.PROJESTABLISH_STAGE.name()))) {
            List<ProcessResp> latestProcess = getLatestProcessByIds(BusinessModuleEnum.PROJ_ESTABLISH.getModelKeyList(), stageIds.get(ProjStageEnum.PROJESTABLISH_STAGE.name()).stream().map(Object::toString).collect(Collectors.toList()));
            if (CollUtil.isNotEmpty(latestProcess)) {
                Map<String, List<ProcessResp>> map = latestProcess.stream().collect(Collectors.groupingBy(ProcessResp::getBusinessKey));
                for (String key : map.keySet()) {
                    latestMap.put(Long.valueOf(key), getLatest(map.get(key)));
                }
            }
        }
        if (CollUtil.isNotEmpty(stageIds.get(ProjStageEnum.PROJREVIEW_STAGE.name()))) {
            List<ProjReviewBaseInfo> reviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getProjEstablishId, stageIds.get(ProjStageEnum.PROJREVIEW_STAGE.name())));
            Map<Long, Long> idmap = reviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId));
            List<ProcessResp> latestProcess = getLatestProcessByIds(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                    ProcessModelTypeEnum.ProjReviewModifyFlow.name()), idmap.keySet().stream().map(Object::toString).collect(Collectors.toList()));
            if (CollUtil.isNotEmpty(latestProcess)) {
                Map<String, List<ProcessResp>> map = latestProcess.stream().collect(Collectors.groupingBy(ProcessResp::getBusinessKey));
                for (String key : map.keySet()) {
                    latestMap.put(idmap.get(Long.valueOf(key)), getLatest(map.get(key)));
                }
            }
        }
        if (CollUtil.isNotEmpty(stageIds.get(ProjStageEnum.CONTRACTSETTLE_STAGE.name())) || CollUtil.isNotEmpty(stageIds.get(ProjStageEnum.CONTRACT_STAGE.name()))) {
            List<Long> ids = new ArrayList<>();
            if (CollUtil.isNotEmpty(stageIds.get(ProjStageEnum.CONTRACTSETTLE_STAGE.name()))) {
                ids.addAll(stageIds.get(ProjStageEnum.CONTRACTSETTLE_STAGE.name()));
            }
            if (CollUtil.isNotEmpty(stageIds.get(ProjStageEnum.CONTRACT_STAGE.name()))) {
                ids.addAll(stageIds.get(ProjStageEnum.CONTRACT_STAGE.name()));
            }
            List<ProjReviewBaseInfo> reviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getProjEstablishId, ids));
            if (CollUtil.isNotEmpty(reviewBaseInfos)) {
                Map<Long, Long> idmap = reviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId));
                List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getProjReviewId, idmap.keySet()));
                if (CollUtil.isEmpty(contractBaseInfos)) {
                    return latestMap;
                }
                List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                Map<Long, Long> cidmap = new HashMap<>();
                for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                    cidmap.put(contractBaseInfo.getId(), contractBaseInfo.getProjReviewId());
                }
                Map<Long, List<ProcessResp>> map = new HashMap<>();
                List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds));
                if (CollUtil.isNotEmpty(paymentBaseInfos)) {
                    List<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
                    Map<Long, Long> pidmap = new HashMap<>();
                    for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
                        pidmap.put(paymentBaseInfo.getId(), paymentBaseInfo.getContractId());
                    }
                    List<ProcessResp> latestProcess2 = getLatestProcessByIds(BusinessModuleEnum.PAYMENT.getModelKeyList(), paymentIds.stream().map(Object::toString).collect(Collectors.toList()));
                    if (CollUtil.isNotEmpty(latestProcess2)) {
                        for (ProcessResp processResp : latestProcess2) {
                            Long projId = idmap.get(cidmap.get(pidmap.get(Long.valueOf(processResp.getBusinessKey()))));
                            if (!map.containsKey(projId)) {
                                map.put(projId, new ArrayList<>());
                            }
                            map.get(projId).add(processResp);
                        }
                    }
                }
                List<String> processModelTypes = ListUtil.toList(ProcessModelTypeEnum.ContractLPRChangeFlow.name(), ProcessModelTypeEnum.ContractExtensionFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name(),
                        ProcessModelTypeEnum.ContractEarlyRepayFlow.name(), ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name());
                List<ProcessResp> latestProcess1 = getLatestProcessByIds(processModelTypes, contractIds.stream().map(Object::toString).collect(Collectors.toList()));
                if (CollUtil.isNotEmpty(latestProcess1)) {
                    for (ProcessResp processResp : latestProcess1) {
                        Long projId = idmap.get(cidmap.get(Long.valueOf(processResp.getBusinessKey())));
                        if (!map.containsKey(projId)) {
                            map.put(projId, new ArrayList<>());
                        }
                        map.get(projId).add(processResp);
                    }
                }
                for (Long key : map.keySet()) {
                    latestMap.put(key, getLatest(map.get(key)));
                }
            }
        }
        return latestMap;
    }

    public List<ProcessResp> getLatestProcessByIds(List<String> modelKeyList, List<String> businessKeys) {
        ProcessPageReq processReq = new ProcessPageReq();
        processReq.setModelKeyList(modelKeyList);
        processReq.setBusinessKeyList(businessKeys);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processReq);

        if (processRespPage.getSize() > 0) {
            return processRespPage.getContents();
        }
        return null;
    }

    //获取最新的审批记录，如果有审批中的审批流取审批中的最新提交审批中，反之取最新提交审批流。
    private ProcessResp getLatest(List<ProcessResp> contents) {
        Map<Integer, List<ProcessResp>> processMap = contents.stream().filter(o -> o.getProcessStatus() == 1 || o.getProcessStatus() == 2 || o.getProcessStatus() == 6).collect(Collectors.groupingBy(ProcessResp::getProcessStatus));
        ProcessResp processResp = null;
        if (CollUtil.isNotEmpty(processMap.get(ProcessBusinessStatusEnum.RUNNING.getType()))) {
            Optional<ProcessResp> max = processMap.get(ProcessBusinessStatusEnum.RUNNING.getType()).stream().max(Comparator.comparing(ProcessResp::getStartTime));
            if (max.isPresent()) {
                processResp = max.get();
            }
        } else {
            List<ProcessResp> processResps = new ArrayList<>();
            if (CollUtil.isNotEmpty(processMap.get(ProcessBusinessStatusEnum.PASS_ALL.getType()))) {
                processResps.addAll(processMap.get(ProcessBusinessStatusEnum.PASS_ALL.getType()));
            }
            if (CollUtil.isNotEmpty(processMap.get(ProcessBusinessStatusEnum.PASS.getType()))) {
                processResps.addAll(processMap.get(ProcessBusinessStatusEnum.PASS.getType()));
            }
            Optional<ProcessResp> max = processResps.stream().max(Comparator.comparing(ProcessResp::getStartTime));
            if (max.isPresent()) {
                processResp = max.get();
            }
        }
        return processResp;
    }

    public Map<Long, ContractPriceDetailRSP> getContractPriceMap(Collection<Long> ids) {
        Set<Long> contractIds = new HashSet<>(ids);
        Map<Long, ContractPriceDetailRSP> priceMap = new HashMap<>();
        Map<Long, ContractLeasePrice> leasePricesMap = new HashMap<>();
        Map<Long, ContractFactoringPrice> factoringPricesMap = new HashMap<>();
        Map<Long, ContractAocPrice> aocPricesMap = new HashMap<>();
        List<ContractLeasePriceLib> leasePrices = contractLeasePriceLibService.queryNewestLib(contractIds);
        if (CollUtil.isNotEmpty(leasePrices)) {
            leasePricesMap = leasePrices.stream().collect(Collectors.toMap(ContractLeasePrice::getContractId, o -> o));
        }
        List<ContractFactoringPriceLib> factoringPrices = contractFactoringPriceLibService.queryNewestLib(contractIds);
        if (CollUtil.isNotEmpty(leasePrices)) {
            factoringPricesMap = factoringPrices.stream().collect(Collectors.toMap(ContractFactoringPrice::getContractId, o -> o));
        }
        List<ContractAocPriceLib> aocPrices = contractAocPriceLibService.queryNewestLib(contractIds);
        if (CollUtil.isNotEmpty(leasePrices)) {
            aocPricesMap = aocPrices.stream().collect(Collectors.toMap(ContractAocPrice::getContractId, o -> o));
        }
        for (Long id : ids) {
            ContractPriceDetailRSP res = new ContractPriceDetailRSP();
            // 处理租赁
            res.setLeasePriceModifyRSP(priceConverter.entityToLeaseRsp(leasePricesMap.get(id)));
            // 处理保理
            ContractFactoringPriceDetailRSP contractFactoringPriceDetailRSP = priceConverter.entityToFactoringRsp(factoringPricesMap.get(id));
            res.setFactoringPriceRSP(contractFactoringPriceDetailRSP);
            // 处理债权转让
            ContractAocPriceDetailRSP contractAocPriceDetailRSP = priceConverter.entityToAocRsp(aocPricesMap.get(id));
            res.setAocPriceRSP(contractAocPriceDetailRSP);
            priceMap.put(id, res);
        }
        return priceMap;
    }

    public Map<Long, ProjReviewPriceDetailRSP> getReviewPriceMap(List<Long> ids, boolean version) {
        Set<Long> reviewIds = new HashSet<>(ids);
        Map<Long, ProjReviewPriceDetailRSP> priceMap = new HashMap<>();
        Map<Long, ProjReviewLeasePrice> leasePricesMap = new HashMap<>();
        Map<Long, ProjReviewFactoringPrice> factoringPricesMap = new HashMap<>();
        Map<Long, ProjReviewAocPrice> aocPricesMap = new HashMap<>();
        if (version) {
            List<ProjReviewLeasePriceLib> leasePrices = projReviewLeasePriceLibService.listNewestByProjReviewIds(reviewIds);
            if (CollUtil.isNotEmpty(leasePrices)) {
                leasePricesMap = leasePrices.stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, o -> o));
            }
            List<ProjReviewFactoringPriceLib> factoringPrices = projReviewFactoringPriceLibService.listNewestByProjReviewIds(reviewIds);
            if (CollUtil.isNotEmpty(leasePrices)) {
                factoringPricesMap = factoringPrices.stream().collect(Collectors.toMap(ProjReviewFactoringPrice::getProjectId, o -> o));
            }
            List<ProjReviewAocPriceLib> aocPrices = projReviewAocPriceLibService.listNewestByProjReviewIds(reviewIds);
            if (CollUtil.isNotEmpty(leasePrices)) {
                aocPricesMap = aocPrices.stream().collect(Collectors.toMap(ProjReviewAocPrice::getProjectId, o -> o));
            }
        } else {
            List<ProjReviewLeasePrice> leasePrices = projReviewLeasePriceService.list(Wrappers.<ProjReviewLeasePrice>lambdaQuery().in(ProjReviewLeasePrice::getProjectId, ids));
            if (CollUtil.isNotEmpty(leasePrices)) {
                leasePricesMap = leasePrices.stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, o -> o));
            }
            List<ProjReviewFactoringPrice> factoringPrices = projReviewFactoringPriceService.list(Wrappers.<ProjReviewFactoringPrice>lambdaQuery().in(ProjReviewFactoringPrice::getProjectId, ids));
            if (CollUtil.isNotEmpty(leasePrices)) {
                factoringPricesMap = factoringPrices.stream().collect(Collectors.toMap(ProjReviewFactoringPrice::getProjectId, o -> o));
            }
            List<ProjReviewAocPrice> aocPrices = projReviewAocPriceService.list(Wrappers.<ProjReviewAocPrice>lambdaQuery().in(ProjReviewAocPrice::getProjectId, ids));
            if (CollUtil.isNotEmpty(leasePrices)) {
                aocPricesMap = aocPrices.stream().collect(Collectors.toMap(ProjReviewAocPrice::getProjectId, o -> o));
            }
        }
        for (Long id : ids) {
            ProjReviewPriceDetailRSP res = new ProjReviewPriceDetailRSP();
            // 处理租赁
            res.setLeasePriceDetailRSP(projReviewPriceConverter.entityToLeaseRsp(leasePricesMap.get(id)));
            // 处理保理
            ProjReviewFactoringPriceRSP projReviewFactoringPriceRSP = projReviewPriceConverter.entityToFactoringRsp(factoringPricesMap.get(id));
            res.setFactoringPriceDetailRSP(projReviewFactoringPriceRSP);
            // 处理债权转让
            ProjReviewAocPriceRSP projReviewAocPriceRSP = projReviewPriceConverter.entityToAocRsp(aocPricesMap.get(id));
            res.setAocPriceDetailRSP(projReviewAocPriceRSP);
            priceMap.put(id, res);
        }
        return priceMap;
    }

    private String getDisplayBizType(String bizType, String leaseType) {
        String leaseTypeName = null;
        if (leaseType != null) {
            List<String> list = JSON.parseArray(leaseType, String.class);
            leaseTypeName = list.stream().map(s -> Optional.ofNullable(s).map(LeaseType::of).map(LeaseType::display).orElse("")).collect(Collectors.joining(","));
        }
        if (ProjectBizType.of(bizType) == null) {
            return leaseTypeName;
        } else if (leaseTypeName == null) {
            return Optional.ofNullable(bizType).map(ProjectBizType::of).map(p -> p.display).orElse("");
        } else {
            return Optional.ofNullable(bizType).map(ProjectBizType::of).map(p -> p.display).orElse("") + "-" + leaseTypeName;
        }
    }

    public R<ProjectLifecycleDetailRSP> detail(ProjectLifecycleDetailREQ req) {
        ProjectLifecycleDetailRSP rsp = new ProjectLifecycleDetailRSP();
        String projStage;
        if (null != req.getReviewId()) {
            ProjReviewBaseInfo baseInfo = projReviewBaseInfoMapper.selectById(req.getReviewId());
            rsp.setProjectId(baseInfo.getId());
            rsp.setProjName(baseInfo.getProjName());
            rsp.setProjCode(baseInfo.getProjCode());
            rsp.setBizType(getDisplayBizType(baseInfo.getBizType(), baseInfo.getLeaseTypes()));
            rsp.setProjSponsorUserId(baseInfo.getProjSponsorUserId());
            projStage = getProjStageById(req.getEstablishId(), req.getReviewId());
        } else {
            ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(req.getEstablishId());
            rsp.setProjectId(baseInfo.getId());
            rsp.setProjName(baseInfo.getProjName());
            rsp.setProjCode(baseInfo.getProjCode());
            rsp.setBizType(getDisplayBizType(baseInfo.getBizType(), baseInfo.getLeaseTypes()));
            rsp.setProjSponsorUserId(baseInfo.getProjSponsorUserId());
            projStage = getProjStageById(req.getEstablishId(), req.getReviewId());
        }
        Response<UserVO> userInfo = userService.getUserInfoById(rsp.getProjSponsorUserId());
        if (userInfo.getSuccess()) {
            UserVO data = userInfo.getData();
            rsp.setProjSponsorUserName(data.getUserName());
            //todo 岗位
//            rsp.setProjSponsorDeptName(data.getOrgRolesName().get(0).getOrgName());
            rsp.setProjSponsorDeptName(data.getJobsName().get(0).getOrgName());
        }

        if (ProjStageEnum.PROJESTABLISH_STAGE.name().equals(projStage)) {
            return R.ok(rsp);
        } else {
            ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(req.getReviewId());
            if (Objects.equals(projReviewBaseInfo.getProjReviewStatus(), RecordStatus.TAKE_EFFECT.name())) {
                List<ProjectLifecycleDetailRSP.ClientInfo> clientInfos;
                String creditorInfo = projReviewBaseInfo.getCreditorInfo();
                clientInfos = getClientInfo(creditorInfo, "债权人");
                String debtorInfo = projReviewBaseInfo.getDebtorInfo();
                clientInfos.addAll(getClientInfo(debtorInfo, "债务人"));
                String lesseeInfo = projReviewBaseInfo.getLesseeInfo();
                clientInfos.addAll(getClientInfo(lesseeInfo, "承租人"));
                String guaranteeInfo = projReviewBaseInfo.getGuaranteeInfo();
                clientInfos.addAll(getClientInfo(guaranteeInfo, "担保人"));
                String mortgagorInfo = projReviewBaseInfo.getMortgagorInfo();
                clientInfos.addAll(getClientInfo(mortgagorInfo, "抵押人"));
                String pledgorInfo = projReviewBaseInfo.getPledgorInfo();
                clientInfos.addAll(getClientInfo(pledgorInfo, "质押人"));
                rsp.setClientInfos(clientInfos);
            }
            return R.ok(rsp);
        }
    }

    private ProjReviewBaseInfo getProjReviewBaseInfo(Long reviewId) {
        ProjReviewBaseInfo projReviewBaseInfo;
        projReviewBaseInfo = projReviewBaseInfoMapper.selectOne(Wrappers.<ProjReviewBaseInfo>lambdaQuery().and(o ->
                        o.eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()).or()
                                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.NEW.name()))
                .eq(ProjReviewBaseInfo::getId, reviewId).last("LIMIT 1"));
        return projReviewBaseInfo;
    }

    public List<ProjectLifecycleDetailRSP.ClientInfo> getClientInfo(String jsonArray, String clientCategory) {
        List<ProjectLifecycleDetailRSP.ClientInfo> clientInfos = new LinkedList<>();
        if (Strings.isNotEmpty(jsonArray)) {
            JSONArray json = JSONUtil.parseArray(jsonArray);
            for (int i = 0; i < json.size(); i++) {
                ProjectLifecycleDetailRSP.ClientInfo clientInfo = new ProjectLifecycleDetailRSP.ClientInfo();
                Long clientId = json.getJSONObject(i).getLong("clientId");
                Object clientName = json.getJSONObject(i).get("clientName");
                Object clientType = json.getJSONObject(i).get("clientType");
                clientInfo.setClientId(clientId);
                clientInfo.setClientName((String) clientName);
                clientInfo.setClientType(Optional.ofNullable((String) clientType).map(ClientType::of).map(ClientType::display).orElse(""));
                clientInfo.setClientCategory(clientCategory);
                clientInfos.add(clientInfo);
            }
        }
        return clientInfos;
    }

    public R<ProjectLifecycleProjestablishCardRSP> projestablishCard(ProjectLifecycleCardREQ req) {
        if (null == req.getEstablishId()) {
            return R.ok();
        }
        ProjEstablishBaseInfo baseInfo = projEstablishBaseInfoMapper.selectById(req.getEstablishId());
        ProjEstablishPriceDetailRSP detail = projEstablishPriceService.detail(req.getEstablishId());
        ProjectLifecycleProjestablishCardRSP rsp = new ProjectLifecycleProjestablishCardRSP();
        rsp.setProjectId(baseInfo.getId());
        if (detail.getFactoringPriceRSP() != null) {
            rsp.setApplyCreditAmount(detail.getFactoringPriceRSP().getApplyCreditAmount());
        } else if (detail.getLeasePriceRSP() != null) {
            rsp.setApplyCreditAmount(detail.getLeasePriceRSP().getApplyCreditAmount());
        } else if (detail.getAocPriceRSP() != null) {
            rsp.setApplyCreditAmount(detail.getAocPriceRSP().getApplyCreditAmount());
        }
        rsp.setApprovalType(baseInfo.getApprovalType());
        rsp.setCreateTime(baseInfo.getCreateTime().toLocalDate());
        ProcessResp processResp = getLatestProcess(BusinessModuleEnum.PROJ_ESTABLISH.getModelKeyList(), baseInfo.getId().toString());
        if (processResp != null) {
            rsp.setProcessType(processResp.getModelName());
            if (processResp.getProcessStatus() == 2 || processResp.getProcessStatus() == 6) {
                rsp.setApproveTime(processResp.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else if (processResp.getProcessStatus() == 1) {
                rsp.setCurrentNode(getCurNode(processResp.getCurAssigneeIds()));
                TaskListREQ taskListREQ = new TaskListREQ();
                taskListREQ.setProcessInstanceId(processResp.getProcessInstanceId());
                PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR = myTaskService.myProcessBackToStepList(taskListREQ);
                if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR.getList()) && backToStepTaskListRSPPageR.getList().get(0).getBackUserId().toString().equals(processResp.getStartUserId())) {
                    rsp.setProcessStatus("退回");
                }
            } else {
                rsp.setCurrentNode(getCurNode(processResp.getLastOperatorId()));
            }
            if (StrUtil.isEmpty(rsp.getProcessStatus())) {
                rsp.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(processResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
            }
        } else {
            // 导入的历史数据不存在流程，并且状态为新建审批通过
            if (ProjProcessState.NEW_APPROVAL_PASS.name().equals(baseInfo.getProjEstablishProcessStatus())) {
                rsp.setProcessStatus(ProcessStatus.APPROVAL_PASS.display);
            } else {
                rsp.setProcessStatus(ProcessStatus.UN_SUBMIT.display);
            }
        }
        return R.ok(rsp);
    }

    public R<ProjectLifecycleProjreviewCardRSP> projreviewCard(ProjectLifecycleCardREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(req.getReviewId());
        if (projReviewBaseInfo == null) {
            return R.ok();
        }
        ProjectLifecycleProjreviewCardRSP rsp = new ProjectLifecycleProjreviewCardRSP();
        ProjectLifecycleProjreviewCardRSP.CardRSP projreview = new ProjectLifecycleProjreviewCardRSP.CardRSP();
        ProjectLifecycleProjreviewCardRSP.CardRSP projReviewPricing = new ProjectLifecycleProjreviewCardRSP.CardRSP();
        projReviewPricing.setProjreviewId(projReviewBaseInfo.getId());
        projReviewPricing.setApplyCreditAmount(projReviewBaseInfo.getDeclaredAmount());
        projReviewPricing.setCreateTime(projReviewBaseInfo.getCreateTime().toLocalDate());
        ProjReviewPriceDetailRSP detail = getBean(ProjReviewPriceService.class).detail(projReviewBaseInfo.getId());
        if (isNotNull(detail)) {
            projReviewPricing.setRateType(detail.getRateType());
            projReviewPricing.setRatePercent(detail.getRatePercent());
        }

        ProcessResp pricingprocessResp = getLatestProcess(Collections.singletonList(ProcessModelTypeEnum.ProjReviewPricingApprovalFlow.name()), projReviewBaseInfo.getId().toString());
        if (pricingprocessResp == null) {
            setprojreviewFlowRsp(pricingprocessResp, projReviewPricing);
        } else {
            projreview.setProjreviewId(projReviewBaseInfo.getId());
            projreview.setApplyCreditAmount(projReviewBaseInfo.getDeclaredAmount());
            projreview.setCreateTime(projReviewBaseInfo.getCreateTime().toLocalDate());
            if (isNotNull(detail)) {
                projreview.setLeaseMonthCount(detail.getMonthCount());
                projreview.setPlanStart(detail.getPlanStartDate());
                if (isNotNull(detail.getMonthCount()) && isNotNull(detail.getPlanStartDate())) {
                    projreview.setPlanEnd(detail.getPlanStartDate().plusMonths(detail.getMonthCount()).minusDays(1));
                }
            }
            ProcessResp processResp = getLatestProcess(Arrays.asList(ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                    ProcessModelTypeEnum.ProjReviewModifyFlow.name()), projReviewBaseInfo.getId().toString());
            setprojreviewFlowRsp(processResp, projreview);
            setprojreviewFlowRsp(pricingprocessResp, projReviewPricing);
        }
        rsp.setProjreview(projreview);
        rsp.setProjReviewPricing(projReviewPricing);
        return R.ok(rsp);
    }

    private void setprojreviewFlowRsp(ProcessResp processResp, ProjectLifecycleProjreviewCardRSP.CardRSP rsp) {
        if (processResp != null) {
            rsp.setProcessType(processResp.getModelName());
            if (processResp.getProcessStatus() == 2 || processResp.getProcessStatus() == 6) {
                rsp.setApproveTime(processResp.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                rsp.setConditional(flowProcessApiService.queryExecutionTimes(processResp.getProcessInstanceId(), CommentTypeEnum.ZL_PR_CONDITION_AGREE) > 0);
            } else if (processResp.getProcessStatus() == 1) {
                rsp.setCurrentNode(getCurNode(processResp.getCurAssigneeIds()));
                TaskListREQ taskListREQ = new TaskListREQ();
                taskListREQ.setProcessInstanceId(processResp.getProcessInstanceId());
                PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR = myTaskService.myProcessBackToStepList(taskListREQ);
                if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR.getList()) && backToStepTaskListRSPPageR.getList().get(0).getBackUserId().toString().equals(processResp.getStartUserId())) {
                    rsp.setProcessStatus("退回");
                }
            } else {
                rsp.setCurrentNode(getCurNode(processResp.getLastOperatorId()));
            }
            if (StrUtil.isEmpty(rsp.getProcessStatus())) {
                rsp.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(processResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
            }
        } else {
            ProjReviewBaseInfo review = projReviewBaseInfoMapper.selectById(rsp.getProjreviewId());
            if (ProjProcessState.NEW_APPROVAL_PASS.name().equals(review.getProjReviewProcessStatus())) {
                rsp.setProcessStatus(ProcessStatus.APPROVAL_PASS.display);
            } else {
                rsp.setProcessStatus(ProcessStatus.UN_SUBMIT.display);
            }
        }
    }

    public ProcessResp getLatestProcess(List<String> modelKeyList, String businessKey) {
        ProcessPageReq processReq = new ProcessPageReq();
        processReq.setModelKeyList(modelKeyList);
        processReq.setBusinessKey(businessKey);
        processReq.setSortType(1);
        processReq.setPageSize(1);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = flowTaskApiService.queryProcess(processReq);
        if (processRespPage.getSize() > 0) {
            return processRespPage.getContents().get(0);
        }
        return null;
    }

    private String getCurNode(String curAssigneeIds) {
        if (StringUtils.isBlank(curAssigneeIds)) {
            return null;
        }
        Set<Long> sysUserIds = new HashSet<>();
        sysUserIds.addAll(Stream.of(curAssigneeIds.split(",")).map(Long::valueOf).collect(Collectors.toList()));
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        return Stream.of(curAssigneeIds.split(","))
                .map(Long::valueOf)
                .map(sysUserMap::get)
                .collect(Collectors.joining(","));
    }


    @Deprecated
    public R<List<ProjectLifecycleContractCardRSP>> contractCard(ProjectLifecycleCardREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(req.getReviewId());
        if (projReviewBaseInfo == null) {
            return R.ok();
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().
                ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                .eq(ContractBaseInfo::getProjReviewId, projReviewBaseInfo.getId()));
        if (CollectionUtil.isEmpty(contractBaseInfos)) {
            return R.ok();
        }
        List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds));
        Map<Long, List<PaymentBaseInfo>> contractPayments = paymentBaseInfos.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIds).eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
        Map<Long, List<CollectionBaseInfo>> paymentCollections = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getPaymentId));
        List<ProjectLifecycleContractCardRSP> rsps = new LinkedList<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            ProjectLifecycleContractCardRSP rsp = new ProjectLifecycleContractCardRSP();
            rsp.setContractId(contractBaseInfo.getId());
            rsp.setContractCode(contractBaseInfo.getContractCode());
            rsp.setBizType(Optional.ofNullable(contractBaseInfo.getLeaseType()).map(LeaseType::of).map(p -> p.display).orElse(""));
            rsp.setCreditAmount(contractBaseInfo.getApplyCreditAmount());
            rsp.setCreateTime(contractBaseInfo.getCreateTime().toLocalDate());
            rsp.setContractStatus(Optional.ofNullable(contractBaseInfo.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
            ProcessResp processResp = getLatestProcess(BusinessModuleEnum.CONTRACT.getModelKeyList(), contractBaseInfo.getId().toString());
            if (processResp != null) {
                rsp.setProcessType(processResp.getModelName());
                if (processResp.getProcessStatus() == 2 || processResp.getProcessStatus() == 6) {
                    rsp.setApproveTime(processResp.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else if (processResp.getProcessStatus() == 1) {
                    rsp.setCurrentNode(getCurNode(processResp.getCurAssigneeIds()));
                    TaskListREQ taskListREQ = new TaskListREQ();
                    taskListREQ.setProcessInstanceId(processResp.getProcessInstanceId());
                    PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR = myTaskService.myProcessBackToStepList(taskListREQ);
                    if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR.getList()) && backToStepTaskListRSPPageR.getList().get(0).getBackUserId().toString().equals(processResp.getStartUserId())) {
                        rsp.setProcessStatus("退回");
                    }
                } else {
                    rsp.setCurrentNode(getCurNode(processResp.getLastOperatorId()));
                }
                if (StrUtil.isEmpty(rsp.getProcessStatus())) {
                    rsp.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(processResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
                }
                List<String> processModelTypes = ListUtil.toList(ProcessModelTypeEnum.ContractLPRChangeFlow.name(), ProcessModelTypeEnum.ContractExtensionFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name(),
                        ProcessModelTypeEnum.ContractEarlyRepayFlow.name(), ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name());
                ProcessResp updateProcessResp = getLatestProcess(processModelTypes, contractBaseInfo.getId().toString());
                if (updateProcessResp != null && (updateProcessResp.getProcessStatus() == 2 || updateProcessResp.getProcessStatus() == 6)) {
                    if (ContractFlowSubModuleEnum.MODIFY_ALL.name().equals(updateProcessResp.getSubModule())) {
                        rsp.setUpdateType("其他");
                    } else {
                        rsp.setUpdateType(Optional.ofNullable(updateProcessResp.getSubModule()).map(ContractChangeTypeEnum::of).map(ContractChangeTypeEnum::display).orElse(""));
                    }
                }
            } else {
                if (contractIsPassStatus(contractBaseInfo.getContractProcessStatus())) {
                    rsp.setProcessStatus(ProcessStatus.APPROVAL_PASS.display);
                } else {
                    rsp.setProcessStatus(ProcessStatus.UN_SUBMIT.display);
                }
            }

            if (CollectionUtil.isNotEmpty(contractPayments) && CollectionUtil.isNotEmpty(contractPayments.get(contractBaseInfo.getId()))) {
                List<ProjectLifecycleContractCardRSP.PaymentCardRSP> paymentCardRSPS = new LinkedList<>();
                List<PaymentBaseInfo> paymentBaseInfoList = contractPayments.get(contractBaseInfo.getId());
                for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                    ProjectLifecycleContractCardRSP.PaymentCardRSP paymentCardRSP = new ProjectLifecycleContractCardRSP.PaymentCardRSP();
                   /* paymentCardRSP.setPaymentId(paymentBaseInfo.getId());
                    paymentCardRSP.setPaymentCode(paymentBaseInfo.getPaymentCode());*/
                    paymentCardRSP.setWriteOffStatus(paymentBaseInfo.getWriteOffStatus());
                    paymentCardRSP.setApplyPaymentAmount(paymentBaseInfo.getApplyPaymentAmount());
                    ProcessResp paymentProcessResp = getLatestProcess(BusinessModuleEnum.PAYMENT.getModelKeyList(), paymentBaseInfo.getId().toString());
                    if (paymentProcessResp != null) {
                        paymentCardRSP.setProcessType(paymentProcessResp.getModelName());
                        if (paymentProcessResp.getProcessStatus() == 1) {
                            paymentCardRSP.setCurrentNode(getCurNode(paymentProcessResp.getCurAssigneeIds()));
                            TaskListREQ taskListREQ2 = new TaskListREQ();
                            taskListREQ2.setProcessInstanceId(paymentProcessResp.getProcessInstanceId());
                            PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR2 = myTaskService.myProcessBackToStepList(taskListREQ2);
                            if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR2.getList()) && backToStepTaskListRSPPageR2.getList().get(0).getBackUserId().toString().equals(paymentProcessResp.getStartUserId())) {
                                paymentCardRSP.setProcessStatus("退回");
                            }
                        } else {
                            paymentCardRSP.setCurrentNode(getCurNode(paymentProcessResp.getLastOperatorId()));
                        }
                        if (StrUtil.isEmpty(paymentCardRSP.getProcessStatus())) {
                            paymentCardRSP.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(paymentProcessResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
                        }
                    } else {
                        paymentCardRSP.setProcessStatus(ProcessStatus.UN_SUBMIT.name());
                    }
                    List<CollectionBaseInfo> collections = paymentCollections.get(paymentBaseInfo.getId());
                    if (CollectionUtil.isNotEmpty(collections)) {
                        long receivedPrincipal = 0, receivedInterest = 0, receivedPenaltyInterest = 0, penaltyInterest = 0, receivedAmount = 0, amount = 0;
                        int writeoffs = 0;
                        for (CollectionBaseInfo info : collections) {
                            if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(info.getWriteOffStatus())) {
                                writeoffs++;
                                receivedAmount = receivedAmount + LongUtil.null2zero(info.getCollectionAmount());
                                receivedPrincipal = receivedPrincipal + LongUtil.null2zero(info.getCollectionPrincipal());
                                receivedInterest = receivedInterest + LongUtil.null2zero(info.getCollectionInterest());
                                receivedPenaltyInterest = receivedPenaltyInterest + LongUtil.null2zero(info.getCollectionPenaltyInterest());
                            }
                            penaltyInterest = penaltyInterest + LongUtil.null2zero(info.getPenaltyInterest());
                            amount = amount + LongUtil.null2zero(info.getPrincipal()) + LongUtil.null2zero(info.getInterest()) + LongUtil.null2zero(info.getPenaltyInterest());
                        }
                        ProjectLifecycleContractCardRSP.CollectionCardRSP collectionCardRSP = new ProjectLifecycleContractCardRSP.CollectionCardRSP();
                        collectionCardRSP.setContractId(contractBaseInfo.getId());
                        collectionCardRSP.setCollectionPhase(writeoffs);
                        collectionCardRSP.setTotalPhase(collections.size());
                        collectionCardRSP.setCollectionRate(BigDecimal.valueOf(receivedAmount).divide(BigDecimal.valueOf(amount), 2, RoundingMode.HALF_UP).floatValue());
                        collectionCardRSP.setPenaltyInterest(penaltyInterest);
                        collectionCardRSP.setReceivedPenaltyInterest(receivedPenaltyInterest);
                        collectionCardRSP.setReceivedAmount(receivedAmount);
                        collectionCardRSP.setReceivedPrincipal(receivedPrincipal);
                        collectionCardRSP.setTotalAmount(amount);
                        collectionCardRSP.setReceivedInterest(receivedInterest);
                        collectionCardRSP.setRentActualCode(paymentBaseInfo.getPaymentCode());
                        collectionCardRSP.setContractStatus(Optional.ofNullable(contractBaseInfo.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
                        collectionCardRSP.setProjectCode(contractBaseInfo.getProjCode());
                        paymentCardRSP.setCollectionCard(collectionCardRSP);
                    }
                    paymentCardRSPS.add(paymentCardRSP);
                }
                rsp.setPaymentCard(paymentCardRSPS);
            }
            rsps.add(rsp);
        }
        return R.ok(rsps);
    }


    //借据维度统计收付款
    public R<List<ProjectLifecycleContractCardRSP>> contractCard2(ProjectLifecycleCardREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(req.getReviewId());
        if (projReviewBaseInfo == null) {
            return R.ok();
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().
                ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                .eq(ContractBaseInfo::getProjReviewId, projReviewBaseInfo.getId()));
        if (CollectionUtil.isEmpty(contractBaseInfos)) {
            return R.ok();
        }
        List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getContractId, contractIds)
                .ne(PaymentBaseInfo::getPaymentStatus, PaymentStatusEnum.CLOSED.name()));
        //<ContractId, <ReceiptId, List<PaymentBaseInfo>
        Map<Long, Map<Long, List<PaymentBaseInfo>>> contractPayments = new HashMap<>();
        paymentBaseInfos.forEach(paymentBaseInfo -> {
            Map<Long, List<PaymentBaseInfo>> paymentBaseInfoMap = contractPayments.get(paymentBaseInfo.getContractId());
            if (ObjectUtil.isEmpty(paymentBaseInfoMap)) {
                paymentBaseInfoMap = new HashMap<>();
                contractPayments.put(paymentBaseInfo.getContractId(), paymentBaseInfoMap);
            }
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMap.get(paymentBaseInfo.getReceiptIdFinal());
            if (ObjectUtil.isEmpty(paymentBaseInfoList)) {
                paymentBaseInfoList = new ArrayList<>();
                paymentBaseInfoMap.put(paymentBaseInfo.getReceiptIdFinal(), paymentBaseInfoList);
            }
            paymentBaseInfoList.add(paymentBaseInfo);
        });
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getContractId, contractIds).eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
        //map<ReceiptId, List<CollectionBaseInfo>>
        Map<Long, List<CollectionBaseInfo>> receiptCollections = new HashMap<>();
        collectionBaseInfos.forEach(baseInfo -> {
            List<CollectionBaseInfo> collectionBaseInfos1 = receiptCollections.get(baseInfo.getReceiptId());
            if (ObjectUtil.isEmpty(collectionBaseInfos1)) {
                collectionBaseInfos1 = new ArrayList<>();
                receiptCollections.put(baseInfo.getReceiptId(), collectionBaseInfos1);
            }
            collectionBaseInfos1.add(baseInfo);
        });
        List<ProjectLifecycleContractCardRSP> rsps = new LinkedList<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
            ProjectLifecycleContractCardRSP rsp = new ProjectLifecycleContractCardRSP();
            rsp.setContractId(contractBaseInfo.getId());
            rsp.setContractCode(contractBaseInfo.getContractCode());
            rsp.setBizType(Optional.ofNullable(contractBaseInfo.getLeaseType()).map(LeaseType::of).map(p -> p.display).orElse(""));
            rsp.setCreditAmount(contractBaseInfo.getApplyCreditAmount());
            rsp.setCreateTime(contractBaseInfo.getCreateTime().toLocalDate());
            rsp.setContractStatus(Optional.ofNullable(contractBaseInfo.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
            ProcessResp processResp = getLatestProcess(BusinessModuleEnum.CONTRACT.getModelKeyList(), contractBaseInfo.getId().toString());
            if (processResp != null) {
                rsp.setProcessType(processResp.getModelName());
                if (processResp.getProcessStatus() == 2 || processResp.getProcessStatus() == 6) {
                    rsp.setApproveTime(processResp.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else if (processResp.getProcessStatus() == 1) {
                    rsp.setCurrentNode(getCurNode(processResp.getCurAssigneeIds()));
                    TaskListREQ taskListREQ = new TaskListREQ();
                    taskListREQ.setProcessInstanceId(processResp.getProcessInstanceId());
                    PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR = myTaskService.myProcessBackToStepList(taskListREQ);
                    if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR.getList()) && backToStepTaskListRSPPageR.getList().get(0).getBackUserId().toString().equals(processResp.getStartUserId())) {
                        rsp.setProcessStatus("退回");
                    }
                } else {
                    rsp.setCurrentNode(getCurNode(processResp.getLastOperatorId()));
                }
                if (StrUtil.isEmpty(rsp.getProcessStatus())) {
                    rsp.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(processResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
                }
                List<String> processModelTypes = ListUtil.toList(ProcessModelTypeEnum.ContractLPRChangeFlow.name(), ProcessModelTypeEnum.ContractExtensionFlow.name(), ProcessModelTypeEnum.ContractModifyFlow.name(),
                        ProcessModelTypeEnum.ContractEarlyRepayFlow.name(), ProcessModelTypeEnum.ContractChangeRepayPlanFlow.name());
                ProcessResp updateProcessResp = getLatestProcess(processModelTypes, contractBaseInfo.getId().toString());
                if (updateProcessResp != null && (updateProcessResp.getProcessStatus() == 2 || updateProcessResp.getProcessStatus() == 6)) {
                    if (ContractFlowSubModuleEnum.MODIFY_ALL.name().equals(updateProcessResp.getSubModule())) {
                        rsp.setUpdateType("其他");
                    } else {
                        rsp.setUpdateType(Optional.ofNullable(updateProcessResp.getSubModule()).map(ContractChangeTypeEnum::of).map(ContractChangeTypeEnum::display).orElse(""));
                    }
                }
            } else {
                if (contractIsPassStatus(contractBaseInfo.getContractProcessStatus())) {
                    rsp.setProcessStatus(ProcessStatus.APPROVAL_PASS.display);
                } else {
                    rsp.setProcessStatus(ProcessStatus.UN_SUBMIT.display);
                }
            }

            if (CollectionUtil.isNotEmpty(contractPayments) && CollectionUtil.isNotEmpty(contractPayments.get(contractBaseInfo.getId()))) {
                List<ProjectLifecycleContractCardRSP.PaymentCardRSP> paymentCardRSPS = new LinkedList<>();
                //Map<Long, List<ProjectLifecycleContractCardRSP.PaymentCardRSP>> paymentCardRSPS = new HashMap<>();
                //汇总各付款详情
                //<ReceiptId, list>
                Map<Long, List<PaymentBaseInfo>> paymentBaseInfoMap = contractPayments.get(contractBaseInfo.getId());
                for (Long receiptId : paymentBaseInfoMap.keySet()) {
                    List<PaymentBaseInfo> paymentBaseInfosList = paymentBaseInfoMap.get(receiptId);
                    if (ObjectUtil.isEmpty(paymentBaseInfosList)) {
                        continue;
                    }
                    ProjectLifecycleContractCardRSP.PaymentCardRSP paymentCardRSP = new ProjectLifecycleContractCardRSP.PaymentCardRSP();
                    paymentCardRSP.setReceiptId(paymentBaseInfosList.get(0).getReceiptId());
                    paymentCardRSP.setReceiptCode(paymentBaseInfosList.get(0).getReceiptCode());
                    //处理借据下多笔付款
                    for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfosList) {
                        paymentCardRSP.setApplyPaymentAmount(LongUtil.null2zero(paymentCardRSP.getApplyPaymentAmount()) + LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount()));
                        //获取核销状态
                        if (ObjectUtil.isEmpty(paymentCardRSP.getWriteOffStatus())) {
                            paymentCardRSP.setWriteOffStatus(paymentBaseInfo.getWriteOffStatus());
                        } else {
                            Integer weight = getPaymentStatusWeight(paymentCardRSP.getWriteOffStatus()) + getPaymentStatusWeight(paymentBaseInfo.getWriteOffStatus());
                            if (weight == 0) {
                                paymentCardRSP.setWriteOffStatus(PaymentWriteOffStatus.NO_PAID.name());
                            } else if (weight == 4) {
                                paymentCardRSP.setWriteOffStatus(PaymentWriteOffStatus.WRITTEN_OFF.name());
                            } else {
                                paymentCardRSP.setWriteOffStatus(PaymentWriteOffStatus.PART_WRITTEN_OFF.name());
                            }
                        }
                        //一对一时查询付款状态
                        if (paymentBaseInfosList.size() < 2) {
                            ProcessResp paymentProcessResp = getLatestProcess(BusinessModuleEnum.PAYMENT.getModelKeyList(), paymentBaseInfo.getId().toString());
                            if (paymentProcessResp != null) {
                                paymentCardRSP.setProcessType(paymentProcessResp.getModelName());
                                if (paymentProcessResp.getProcessStatus() == 1) {
                                    paymentCardRSP.setCurrentNode(getCurNode(paymentProcessResp.getCurAssigneeIds()));
                                    TaskListREQ taskListREQ2 = new TaskListREQ();
                                    taskListREQ2.setProcessInstanceId(paymentProcessResp.getProcessInstanceId());
                                    PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR2 = myTaskService.myProcessBackToStepList(taskListREQ2);
                                    if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR2.getList()) && backToStepTaskListRSPPageR2.getList().get(0).getBackUserId().toString().equals(paymentProcessResp.getStartUserId())) {
                                        paymentCardRSP.setProcessStatus("退回");
                                    }
                                } else {
                                    paymentCardRSP.setCurrentNode(getCurNode(paymentProcessResp.getLastOperatorId()));
                                }
                                if (StrUtil.isEmpty(paymentCardRSP.getProcessStatus())) {
                                    paymentCardRSP.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(paymentProcessResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
                                }
                            } else {
                                paymentCardRSP.setProcessStatus(ProcessStatus.UN_SUBMIT.name());
                            }
                        }
                        List<CollectionBaseInfo> collections = receiptCollections.get(paymentBaseInfo.getReceiptId());
                        if (CollectionUtil.isNotEmpty(collections)) {
                            long receivedPrincipal = 0, receivedInterest = 0, receivedPenaltyInterest = 0, penaltyInterest = 0, receivedAmount = 0, amount = 0;
                            int writeoffs = 0;
                            for (CollectionBaseInfo info : collections) {
                                if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(info.getWriteOffStatus())) {
                                    writeoffs++;
                                    receivedAmount = receivedAmount + LongUtil.null2zero(info.getCollectionAmount());
                                    receivedPrincipal = receivedPrincipal + LongUtil.null2zero(info.getCollectionPrincipal());
                                    receivedInterest = receivedInterest + LongUtil.null2zero(info.getCollectionInterest());
                                    receivedPenaltyInterest = receivedPenaltyInterest + LongUtil.null2zero(info.getCollectionPenaltyInterest());
                                }
                                penaltyInterest = penaltyInterest + LongUtil.null2zero(info.getPenaltyInterest());
                                amount = amount + LongUtil.null2zero(info.getPrincipal()) + LongUtil.null2zero(info.getInterest()) + LongUtil.null2zero(info.getPenaltyInterest());
                            }
                            ProjectLifecycleContractCardRSP.CollectionCardRSP collectionCardRSP = paymentCardRSP.getCollectionCard();
                            if (ObjectUtils.isEmpty(collectionCardRSP)) {
                                collectionCardRSP = new ProjectLifecycleContractCardRSP.CollectionCardRSP();
                                collectionCardRSP.setContractId(contractBaseInfo.getId());
                                collectionCardRSP.setRentActualCode(paymentBaseInfo.getPaymentCode());
                                collectionCardRSP.setContractStatus(Optional.ofNullable(contractBaseInfo.getContractStatus()).map(ContractStatus::of).map(c -> c.display).orElse(""));
                                collectionCardRSP.setProjectCode(contractBaseInfo.getProjCode());
                                paymentCardRSP.setCollectionCard(collectionCardRSP);
                                collectionCardRSP.setCollectionPhase(LongUtil.null2zero(collectionCardRSP.getCollectionPhase()) + writeoffs);
                                collectionCardRSP.setTotalPhase(LongUtil.null2zero(collectionCardRSP.getTotalPhase()) + collections.size());
                                collectionCardRSP.setPenaltyInterest(LongUtil.null2zero(collectionCardRSP.getPenaltyInterest()) + LongUtil.null2zero(penaltyInterest));
                                collectionCardRSP.setReceivedPenaltyInterest(LongUtil.null2zero(collectionCardRSP.getReceivedPenaltyInterest()) + LongUtil.null2zero(receivedPenaltyInterest));
                                collectionCardRSP.setReceivedAmount(LongUtil.null2zero(collectionCardRSP.getReceivedAmount()) + LongUtil.null2zero(receivedAmount));
                                collectionCardRSP.setReceivedPrincipal(LongUtil.null2zero(collectionCardRSP.getReceivedPrincipal()) + LongUtil.null2zero(receivedPrincipal));
                                collectionCardRSP.setTotalAmount(LongUtil.null2zero(collectionCardRSP.getTotalAmount()) + LongUtil.null2zero(amount));
                                collectionCardRSP.setReceivedInterest(LongUtil.null2zero(collectionCardRSP.getReceivedInterest()) + LongUtil.null2zero(receivedInterest));
                                collectionCardRSP.setCollectionRate(BigDecimal.valueOf(receivedAmount).divide(BigDecimal.valueOf(amount), 2, RoundingMode.HALF_UP).floatValue());
                            }

                        }
                    }
                    paymentCardRSPS.add(paymentCardRSP);

                }

                rsp.setPaymentCard(paymentCardRSPS);
            }
            rsps.add(rsp);
        }
        return R.ok(rsps);
    }

    /**
     * 合并付款状态
     **/
    private Integer getPaymentStatusWeight(String status) {
        Integer i = 0;
        if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(status)) {
            i = i + 2;
        } else if (PaymentWriteOffStatus.PART_WRITTEN_OFF.name().equals(status)) {
            i++;
        }
        return i;
    }

    private boolean contractIsPassStatus(String contractProcessStatus) {
        if (ObjectUtil.isEmpty(contractProcessStatus)) {
            return false;
        }
        ContractProcessStatusEnum processStatusEnum = ContractProcessStatusEnum.of(contractProcessStatus);
        return ContractProcessStatusEnum.NEW_PASS.equals(processStatusEnum) ||
                ContractProcessStatusEnum.START_RENT_PASS.equals(processStatusEnum) ||
                ContractProcessStatusEnum.SETTLE_PASS.equals(processStatusEnum);
    }

    public R<List<RentCollectionListRSP>> rentCollectionListCard(ProjectLifecycleCardREQ req) {
        ProjReviewBaseInfo projReviewBaseInfo = getProjReviewBaseInfo(req.getReviewId());
        if (projReviewBaseInfo == null) {
            return R.ok();
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().
                ne(ContractBaseInfo::getContractStatus, ContractStatus.CLOSED.name())
                .ne(ContractBaseInfo::getContractStatus, ContractStatus.INVALID.name())
                .eq(ContractBaseInfo::getProjReviewId, projReviewBaseInfo.getId()));
        if (CollectionUtil.isEmpty(contractBaseInfos)) {
            return R.ok();
        }
        RentCollectionListREQ indexReq = new RentCollectionListREQ();
        indexReq.setContractIds(contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        indexReq.setPageSize(Integer.MAX_VALUE);
        PageR<RentCollectionListRSP> rentCollectionListRSPPageR = rentCollectionIndexService.indexList(indexReq);
        return R.ok(rentCollectionListRSPPageR.getList());
    }

    public ProjectLifecycleAfterLeaseCheckCardRSP afterLeaseCheckCard(ProjectLifecycleCardREQ req) {
        if (req.getReviewId() == null) {
            return new ProjectLifecycleAfterLeaseCheckCardRSP();
        }
        Assert.notNull(req.getEstablishId(), () -> MithrasException.newException("立项id不能为空"));
        Assert.notNull(req.getReviewId(), () -> MithrasException.newException("评审id不能为空"));
        // 根据不同类型确定项目评审id
        Long projectReviewId = req.getReviewId();
        ProjectLifecycleAfterLeaseCheckCardRSP rsp = new ProjectLifecycleAfterLeaseCheckCardRSP();
        if (Objects.isNull(projectReviewId)) {
            // 没有评审id的话返回空
            rsp.setCheckPlanList(Collections.emptyList());
            rsp.setExternalQueryList(Collections.emptyList());
            return rsp;
        }
        // 处理检查计划
        rsp.setCheckPlanList(this.listCheckPlanInfoList(projectReviewId));
        // 处理外部查询
        rsp.setExternalQueryList(this.listExternalQueryInfoList(projectReviewId));
        return rsp;
    }

    public PageR<ProjectLifecycleMilestoneRSP> milestone(ProjectLifecycleEventREQ req) {
        //
        req.setProjectId(req.getEstablishId());
        Long reviewId = req.getReviewId();
        if (null != reviewId) {
            ProjReviewBaseInfo reviewBaseInfo = projReviewBaseInfoMapper.selectById(reviewId);
            if (reviewBaseInfo.getGroupCreditReviewId() != null) {
                req.setDataType("GROUP_CREDIT_REVIEW");
            } else {
                req.setDataType("PROJ_ESTABLISH");
            }
        }
        //
        Page<ProjLifecycleEvent> projLifecycleEventPage = projLifecycleEventMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<ProjLifecycleEvent>lambdaQuery().eq(ProjLifecycleEvent::getProjId, req.getProjectId())
                        .eq(ProjLifecycleEvent::getProjType, req.getDataType())
                        .eq(req.getEventType() != null, ProjLifecycleEvent::getEventType, req.getEventType())
                        .ge(req.getEventTimeFrom() != null, ProjLifecycleEvent::getEventTime, req.getEventTimeFrom()).le(req.getEventTimeTo() != null, ProjLifecycleEvent::getEventTime, req.getEventTimeTo()));
        List<ProjectLifecycleMilestoneRSP> rsps = projLifecycleEventList2RspList(projLifecycleEventPage.getRecords());
        return PageR.of(rsps, projLifecycleEventPage.getTotal(),
                projLifecycleEventPage.getPages(),
                projLifecycleEventPage.getCurrent(),
                projLifecycleEventPage.getSize());
    }

    public List<ProjectLifecycleMilestoneRSP> projLifecycleEventList2RspList(List<ProjLifecycleEvent> records) {
        List<ProjectLifecycleMilestoneRSP> rsps = new LinkedList<>();
        Set<Long> sysUserIds = records.stream().map(ProjLifecycleEvent::getOperator).collect(Collectors.toSet());
        Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
        for (ProjLifecycleEvent event : records) {
            ProjectLifecycleMilestoneRSP rsp = new ProjectLifecycleMilestoneRSP();
            rsp.setId(event.getId());
            rsp.setEvent(event.getEvent());
            rsp.setEventTime(event.getEventTime());
            rsp.setEventdesc(event.getEventdesc());
            rsp.setEventType(Optional.ofNullable(event.getEventType()).map(ProjLifecycleEventTypeEnum::of).map(c -> c.display).orElse(""));
            rsp.setOperator(sysUserMap.get(event.getOperator()));
            rsps.add(rsp);
        }
        return rsps;
    }

    private List<ProjectLifecycleAfterLeaseCheckCardRSP.CheckPlanInfo> listCheckPlanInfoList(Long projectReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projectReviewId);
        if (Objects.isNull(projReviewBaseInfo) || Objects.isNull(projReviewBaseInfo.getClientId())) {
            return Collections.emptyList();
        }
        List<NewAfterLeaseCheckPlanClient> checkPlanClientList = checkPlanClientService.listByClientIds(Collections.singletonList(projReviewBaseInfo.getClientId()));
        if (CollectionUtil.isEmpty(checkPlanClientList)) {
            return Collections.emptyList();
        }
        // 只取计划中本客户是需要检查的
        Map<Long, NewAfterLeaseCheckPlanClient> map = checkPlanClientList.stream()
                .filter(item -> Objects.equals(item.getIsCheck(), YesOrNoNumberEnum.YES.getCode()))
                .collect(Collectors.toMap(NewAfterLeaseCheckPlanClient::getPlanId, e -> e));
        if (CollectionUtil.isEmpty(map)) {
            return Collections.emptyList();
        }
        List<NewAfterLeaseCheckPlanBase> checkPlanBaseList = checkPlanBaseService.listByIds(map.keySet());
        // 只取检查中或者计划完结的
        List<NewAfterLeaseCheckPlanBase> targetPlanList = checkPlanBaseList.stream().filter(item -> {
            boolean checking = Objects.equals(item.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.CHECKING.name());
            boolean finish = Objects.equals(item.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.FINISH.name());
            return checking || finish;
        }).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(targetPlanList)) {
            return Collections.emptyList();
        }
        return targetPlanList.stream().map(item -> {
            ProjectLifecycleAfterLeaseCheckCardRSP.CheckPlanInfo checkPlanInfo = new ProjectLifecycleAfterLeaseCheckCardRSP.CheckPlanInfo();
            checkPlanInfo.setCheckPlanId(item.getId());
            NewAfterLeaseCheckPlanClient checkPlanClient = map.get(item.getId());
            if (Objects.nonNull(checkPlanClient)) {
                checkPlanInfo.setCheckPlanClientId(checkPlanClient.getId());
                if (!Objects.equals(item.getPlanStatus(), AfterLeaseCheckPlanStatusEnum.FINISH.name())) {
                    checkPlanInfo.setCanJump(Boolean.TRUE);
                } else {
                    if (Objects.equals(checkPlanClient.getApprovalStatus(), ProcessStatus.APPROVAL_PASS.name())) {
                        checkPlanInfo.setCanJump(Boolean.TRUE);
                    } else {
                        checkPlanInfo.setCanJump(Boolean.FALSE);
                    }
                }
                ProcessResp processResp = getLatestProcess(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.getModelKeyList(), checkPlanClient.getId().toString());
                this.fillProcessInfo(checkPlanInfo, processResp);
            } else {
                log.warn("没有找到对应的检查客户记录，跳转标志置为FALSE[planId: {}]", item.getId());
                checkPlanInfo.setCanJump(Boolean.FALSE);
            }
            checkPlanInfo.setPlanName(item.getPlanName());
            checkPlanInfo.setPlanStatus(item.getPlanStatus());
            checkPlanInfo.setCheckProjectApprovalStatus(checkPlanClient.getApprovalStatus());
            // 拼接检查时间
            checkPlanInfo.setPlanTime(checkPlanBaseService.formatPlanTime(item));
            return checkPlanInfo;
        }).collect(Collectors.toList());
    }

    private List<ProjectLifecycleAfterLeaseCheckCardRSP.ExternalQueryInfo> listExternalQueryInfoList(Long projectReviewId) {
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projectReviewId);
        if (Objects.isNull(projReviewBaseInfo) || Objects.isNull(projReviewBaseInfo.getClientId())) {
            return Collections.emptyList();
        }
        List<NewAfterLeaseCheckExternalQuery> checkExternalQueryList = checkExternalQueryService.listByClientId(projReviewBaseInfo.getClientId());
        if (CollectionUtil.isEmpty(checkExternalQueryList)) {
            return Collections.emptyList();
        }
        return checkExternalQueryList.stream().map(item -> {
            ProjectLifecycleAfterLeaseCheckCardRSP.ExternalQueryInfo externalQueryInfo = new ProjectLifecycleAfterLeaseCheckCardRSP.ExternalQueryInfo();
            externalQueryInfo.setQueryId(item.getId());
            externalQueryInfo.setQueryMonth(item.getInspectionMonth().getYear() + "年" + item.getInspectionMonth().getMonthValue() + "月");
            ProcessResp processResp = getLatestProcess(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_EXTERNAL_QUERY.getModelKeyList(), item.getId().toString());
            this.fillProcessInfo(externalQueryInfo, processResp);
            return externalQueryInfo;
        }).collect(Collectors.toList());
    }

    private void fillProcessInfo(AbstractProcessInfoRSP processInfoRSP, ProcessResp processResp) {
        if (processResp != null) {
            processInfoRSP.setProcessType(processResp.getModelName());
            if (processResp.getProcessStatus() == 2 || processResp.getProcessStatus() == 6) {
                processInfoRSP.setApproveTime(processResp.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else if (processResp.getProcessStatus() == 1) {
                processInfoRSP.setCurrentNode(getCurNode(processResp.getCurAssigneeIds()));
                TaskListREQ taskListREQ = new TaskListREQ();
                taskListREQ.setProcessInstanceId(processResp.getProcessInstanceId());
                PageR<BackToStepTaskListRSP> backToStepTaskListRSPPageR = myTaskService.myProcessBackToStepList(taskListREQ);
                if (CollectionUtil.isNotEmpty(backToStepTaskListRSPPageR.getList()) && backToStepTaskListRSPPageR.getList().get(0).getBackUserId().toString().equals(processResp.getStartUserId())) {
                    processInfoRSP.setProcessStatus("退回");
                }
            } else {
                processInfoRSP.setCurrentNode(getCurNode(processResp.getLastOperatorId()));
            }
            if (StrUtil.isEmpty(processInfoRSP.getProcessStatus())) {
                processInfoRSP.setProcessStatus(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(processResp.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
            }
        } else {
            processInfoRSP.setProcessStatus(ProcessStatus.UN_SUBMIT.display);
        }
    }
}
