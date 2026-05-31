package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.dashboard.DashboardOperationTermEnum;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.dashboard.DashboardDueDiligenceInfoMapper;
import cn.zswltech.mithras.service.mapper.dashboard.DashboardOperationTimeMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardDueDiligenceInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardCorpCommerceInfoLibDto;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.util.DashboardOperationUtil;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ClassName DashboardOperationService
 * @Description TODO
 * @Author zhouning
 * @Date 2024/7/24 5:05 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class DashboardOperationTimeService {

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private GuanYuanOperationService dashboardOperationService;
    @Resource
    private OrgService orgService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private DashboardDueDiligenceInfoMapper dashboardDueDiligenceInfoMapper;
    @Resource
    private DashboardOperationTimeMapper dashboardOperationTimeMapper;

    private static final String BIZ_DIVISION_LEADER = "userTask_bizDivisionLeader";
    private static final String JURY_VOTE = "userTask_juryVote";
    private static final String JURY_SECRETARY = "userTask_jurySecretaryCollect";
    private static final String JURY_MEETING = "userTask_juryMeetingCollect";

    public List<DashboardOperationTimeStatisticsRSP> timeList(DashboardOperationTimeStatisticsREQ req) {
        StopWatch st = new StopWatch("时效统计");
        st.start("查询流程，立项");
        if (ObjectUtil.isEmpty(req.getQueryDateFrom())) {
            req.setQueryDateFrom(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        }
        List<String> riskControlIndustryClassify = null;
        if (StringUtils.isNotBlank(req.getType())) {
            riskControlIndustryClassify = DashboardOperationUtil.getRiskControlIndustryClassify(req);
        }
        Page<ProcessResp> processRespPage = getProjEstablishEffect(req.getQueryDateFrom());
        Map<Long, ProcessResp> projEstablishId2ProcessMap = filterProcessResp(processRespPage, req).stream().collect(Collectors.toMap(e -> Long.valueOf(e.getBusinessKey()), e -> e, (a, b) -> a));
        st.stop();

        List<ProjEstablishBaseInfo> projEstablishBaseInfoAllList = new ArrayList<>();
        if (!projEstablishId2ProcessMap.isEmpty()) {
            projEstablishBaseInfoAllList = projEstablishBaseInfoService.list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .in(ProjEstablishBaseInfo::getId, projEstablishId2ProcessMap.keySet())
            );
        }

        Set<Long> projEstablishBaseInfoIdset = new HashSet<>();
        Map<Long, List<ProjEstablishBaseInfo>> groupProjEstaByClientId = new HashMap<>();
        if (projEstablishBaseInfoAllList != null && !projEstablishBaseInfoAllList.isEmpty()) {
            projEstablishBaseInfoIdset = projEstablishBaseInfoAllList.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toSet());
            groupProjEstaByClientId = projEstablishBaseInfoAllList.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getClientId));
        }

        List<ProjEstablishBaseInfo> projEstablishBaseInfoList= new ArrayList<>();
        if (!projEstablishBaseInfoIdset.isEmpty()) {
            DashboardCorpCommerceInfoLibDto dto = new DashboardCorpCommerceInfoLibDto();
            dto.setInRiskControlIndustryClassify(riskControlIndustryClassify);
            dto.setInClientIds(projEstablishBaseInfoIdset);
            List<CorpCommerceInfoLib> corpCommerceInfoLibs = dashboardOperationTimeMapper.listNewestCommerceInfo(dto);
            for (Map.Entry<Long, List<ProjEstablishBaseInfo>> entry : groupProjEstaByClientId.entrySet()) {
                Long clientId = entry.getKey();
                for (CorpCommerceInfoLib corpCommerceInfoLib : corpCommerceInfoLibs) {
                    if (clientId.equals(corpCommerceInfoLib.getClientId())) {
                        projEstablishBaseInfoList.addAll(groupProjEstaByClientId.get(clientId));
                    }
                }
            }
        }

        Set<Long> idset = new HashSet<>();
        List<ProjReviewBaseInfo> reviewBaseInfos = new ArrayList<>();
        if (!projEstablishBaseInfoList.isEmpty()) {
            idset = projEstablishBaseInfoList.stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toSet());
            reviewBaseInfos = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getProjEstablishId, idset));
        }

        Map<Long, Long> idmap = new HashMap<>();
        Page<ProcessResp> reviewBaseProcessPage = new Page<>();
        Map<Long, ProcessResp> reviewBaseId2ProcessMap = new HashMap<>();
        if (!reviewBaseInfos.isEmpty()) {
            idmap = reviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, ProjReviewBaseInfo::getProjEstablishId));
            reviewBaseProcessPage = getReviewBaseProcessByIds(idmap.keySet().stream().map(Object::toString).collect(Collectors.toList()));
            reviewBaseId2ProcessMap = filterProcessResp(reviewBaseProcessPage, req).stream().collect(Collectors.toMap(e -> Long.valueOf(e.getBusinessKey()), e -> e, (a, b) -> a));;
        }

        Map<String, List<ProcessHistoryResp>> reviewBaseProcessHistoryMap = new HashMap<>();
        if (reviewBaseProcessPage != null && !reviewBaseProcessPage.getContents().isEmpty()) {
            reviewBaseProcessHistoryMap = dashboardOperationService.processHistoryList(reviewBaseProcessPage.getContents().stream().map(ProcessResp::getProcessInstanceId).collect(Collectors.toSet()));
        }

        List<ContractBaseInfo> contractBaseInfos = new ArrayList<>();
        if (!idmap.isEmpty()) {
            contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(
                    ContractBaseInfo::getProjReviewId, idmap.keySet()));
        }

        List<PaymentBaseInfo> paymentBaseInfos = new ArrayList<>();
        if (!contractBaseInfos.isEmpty()) {
            List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            paymentBaseInfos = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getContractId, contractIds));
        }

        List<PaymentActualDetail> paymentActualDetailList = new ArrayList<>();
        if (!paymentBaseInfos.isEmpty()) {
            List<Long> paymentIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
            paymentActualDetailList = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .in(PaymentActualDetail::getPaymentId, paymentIds)
                    .orderByAsc(PaymentActualDetail::getPaidInDate)
            );
        }

        List<DashboardDueDiligenceInfo> dashboardDueDiligenceInfoList = dashboardDueDiligenceInfoMapper.selectList(Wrappers.<DashboardDueDiligenceInfo>lambdaQuery()
                .ge(DashboardDueDiligenceInfo::getDueDiligenceTime, req.getQueryDateFrom())
                .le(DashboardDueDiligenceInfo::getDueDiligenceTime, req.getQueryDateTo())
                .in(StringUtils.isNotBlank(req.getType()), DashboardDueDiligenceInfo::getBusinessGroup, req.getType())
        );
        List<DashboardOperationTimeStatisticsRSP> res = new ArrayList<>();
        for (OrgDO orgDO : sysUserService.listBizDeptSort(req.getType())) {
            if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
            if(CollectionUtils.isNotEmpty(req.getBizDeptIdList()) && !req.getBizDeptIdList().contains(orgDO.getId())){
                continue;
            }
            long bizDeptId = orgDO.getId();
            String bizDeptName = orgDO.getName();
            DashboardOperationTimeStatisticsRSP dashboard = new DashboardOperationTimeStatisticsRSP();
            dashboard.setBizDeptId(bizDeptId);
            dashboard.setBizDeptName(bizDeptName);
            res.add(dashboard);
            List<ProjEstablishBaseInfo> tempProjEstaList = new ArrayList<>();
            List<ProjReviewBaseInfo> tempProjReviewList = new ArrayList<>();
            List<ContractBaseInfo> tempContractBaseList = new ArrayList<>();
            List<ProcessResp> tempProcessRespList = new ArrayList<>();
            List<PaymentBaseInfo> tempPaymentBaseList = new ArrayList<>();
            List<PaymentActualDetail> tempPaymentActualList = new ArrayList<>();

            for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
                if (bizDeptId == projEstablishBaseInfo.getBizDeptId()) {
                    tempProjEstaList.add(projEstablishBaseInfo);
                }
            }

            for (ProjEstablishBaseInfo projEstablishBaseInfo : tempProjEstaList) {
                ProcessResp processResp = projEstablishId2ProcessMap.get(projEstablishBaseInfo.getId());
                tempProcessRespList.add(processResp);
                for (ProjReviewBaseInfo projReviewBaseInfo : reviewBaseInfos) {
                    if (projEstablishBaseInfo.getId().equals(projReviewBaseInfo.getProjEstablishId())) {
                        tempProjReviewList.add(projReviewBaseInfo);
                    }
                }
            }
            for (ContractBaseInfo contractBaseInfo : contractBaseInfos) {
                for (ProjReviewBaseInfo projReviewBaseInfo : tempProjReviewList) {
                    if (contractBaseInfo.getProjReviewId().equals(projReviewBaseInfo.getId())) {
                        tempContractBaseList.add(contractBaseInfo);
                    }
                }
            }
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
                for (ContractBaseInfo contractBaseInfo : tempContractBaseList) {
                    if (paymentBaseInfo.getContractId().equals(contractBaseInfo.getId())) {
                        tempPaymentBaseList.add(paymentBaseInfo);
                    }
                }
            }
            for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
                for (PaymentBaseInfo paymentBaseInfo : tempPaymentBaseList) {
                    if (paymentActualDetail.getPaymentId().equals(paymentBaseInfo.getId())) {
                        tempPaymentActualList.add(paymentActualDetail);
                    }
                }
            }

            //立项
            Long totalWorkMinutes = 0l;
            int count = 0;
            Long paymentActualMinutes = 0l;
            int paidCount = 0;
            for (ProcessResp processResp : tempProcessRespList) {
                LocalDateTime applyTime = processResp.getStartTime() == null ? null : processResp.getStartTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                LocalDateTime endTime = processResp.getEndTime() == null ? null : processResp.getEndTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                Long workMinutes = DateUtil.countWorkdayHouse(applyTime, endTime);
                if (ObjectUtil.isNotNull(workMinutes)) {
                    totalWorkMinutes += workMinutes;
                    count += 1;
                }
                long projEstablishBaseId = Long.valueOf(processResp.getBusinessKey());
                List<ProjReviewBaseInfo> calProjReviewList = new ArrayList<>();
                for (ProjReviewBaseInfo projReviewBaseInfo : tempProjReviewList) {
                    if (projEstablishBaseId == projReviewBaseInfo.getProjEstablishId()) {
                        calProjReviewList.add(projReviewBaseInfo);
                    }
                }

                List<ContractBaseInfo> calContractBaseList = new ArrayList<>();
                for (ContractBaseInfo contractBaseInfo : tempContractBaseList) {
                    for (ProjReviewBaseInfo projReviewBaseInfo : calProjReviewList) {
                        if (contractBaseInfo.getProjReviewId().equals(projReviewBaseInfo.getId())) {
                            calContractBaseList.add(contractBaseInfo);
                        }
                    }
                }

                List<PaymentBaseInfo> calPaymentBaseInfoList = new ArrayList<>();
                for (PaymentBaseInfo paymentBaseInfo : tempPaymentBaseList) {
                    for (ContractBaseInfo contractBaseInfo : calContractBaseList) {
                        if (paymentBaseInfo.getContractId().equals(contractBaseInfo.getId())) {
                            calPaymentBaseInfoList.add(paymentBaseInfo);
                        }
                    }
                }

                List<PaymentActualDetail> calPaymentActualDetailList = new ArrayList<>();
                for (PaymentActualDetail paymentActualDetail : tempPaymentActualList) {
                    for (PaymentBaseInfo paymentBaseInfo : calPaymentBaseInfoList) {
                        if (paymentActualDetail.getPaymentId().equals(paymentBaseInfo.getId())) {
                            calPaymentActualDetailList.add(paymentActualDetail);
                        }
                    }
                }

                List<PaymentActualDetail> paymentActualDetails = calPaymentActualDetailList.stream()
                        .sorted(Comparator.comparing(PaymentActualDetail::getPaidInDate)).collect(Collectors.toList());
                if (!paymentActualDetails.isEmpty()) {
                    LocalDateTime firstPaidInDate = paymentActualDetails.get(0).getPaidInDate() == null ? null : paymentActualDetails.get(0).getPaidInDate()
                            .atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
                    Long paidMinutes = DateUtil.countWorkdayHouse(applyTime, firstPaidInDate);
                    if (ObjectUtil.isNotNull(paidMinutes)) {
                        paymentActualMinutes += paidMinutes;
                        paidCount += 1;
                    }
                }
            }
            //立项均耗
            if (totalWorkMinutes > 0) {
                BigDecimal days = BigDecimal.valueOf(totalWorkMinutes).divide(BigDecimal.valueOf(24*60L*count), 2, RoundingMode.HALF_UP);
                dashboard.setProjEstaTotalTime(new ValueUnitDTO(days.toString(),"天"));
            } else {
                dashboard.setProjEstaTotalTime(new ValueUnitDTO("0","天"));
            }
            //立项投放均耗
            if (paymentActualMinutes > 0) {
                BigDecimal days = BigDecimal.valueOf(paymentActualMinutes).divide(BigDecimal.valueOf(24*60L*paidCount), 2, RoundingMode.HALF_UP);
                dashboard.setProjEstaPaidInTotalTime(new ValueUnitDTO(days.toString(),"天"));
            } else {
                dashboard.setProjEstaPaidInTotalTime(new ValueUnitDTO("0","天"));
            }

            //评审
            long reviewPaymentMinutes = 0l;
            long totalReviewProcessMinutes = 0;
            long totalJuryProcessMinutes = 0;
            int reviewCount = 0;
            int totalReviewProcessCount = 0;
            int totalJuryProcessCount = 0;
            List<ProcessResp> tempProjReviewRespList = new ArrayList<>();
            for (ProjReviewBaseInfo projReviewBaseInfo : tempProjReviewList) {
                if (reviewBaseId2ProcessMap.containsKey(projReviewBaseInfo.getId())) {
                    tempProjReviewRespList.add(reviewBaseId2ProcessMap.get(projReviewBaseInfo.getId()));
                }
            }
            for (ProcessResp processResp : tempProjReviewRespList) {
                LocalDateTime applyTime = processResp.getStartTime() == null ? null : processResp.getStartTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                long projReviewId = Long.valueOf(processResp.getBusinessKey());
                List<ProcessHistoryResp> historys = reviewBaseProcessHistoryMap.get(processResp.getProcessInstanceId());
                LocalDateTime arriveDate = null;
                LocalDateTime bizDivisionLeaderDate = null;
                LocalDateTime juryVoteDate = null;
                LocalDateTime jurySecretaryDate = null;
                LocalDateTime juryMeetingDate = null;
                if (ObjectUtil.isNotEmpty(historys)) {
                    for (ProcessHistoryResp history : historys) {
                        if (arriveDate == null) {
                            arriveDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            continue;
                        }
                        Long tempMinutes = DateUtil.countWorkdayHouse(arriveDate, history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                        BigDecimal addDay = null;
                        if (tempMinutes != null && tempMinutes > 0) {
                            addDay = BigDecimal.valueOf(tempMinutes).divide(BigDecimal.valueOf(24*60L), 2, RoundingMode.HALF_UP);
                        }
                        //业务分管领导
                        if (ObjectUtil.equals(BIZ_DIVISION_LEADER, history.getTaskActivityId())) {
                            bizDivisionLeaderDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        } else if (ObjectUtil.equals(JURY_VOTE, history.getTaskActivityId())) {
                            juryVoteDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        } else if (ObjectUtil.equals(JURY_SECRETARY, history.getTaskActivityId())) {
                            jurySecretaryDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        } else if (ObjectUtil.equals(JURY_MEETING, history.getTaskActivityId())) {
                            juryMeetingDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        }
                        if (ObjectUtil.isNotEmpty(history.getOperateTime())) {
                            arriveDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        }
                    }
                    if (ObjectUtil.isNotNull(bizDivisionLeaderDate) && ObjectUtil.isNotNull(juryVoteDate)) {
                        Long tempMinutes = DateUtil.countWorkdayHouse(bizDivisionLeaderDate, juryVoteDate);
                        if (tempMinutes != null) {
                            totalReviewProcessMinutes += tempMinutes;
                            totalReviewProcessCount += 1;
                        }
                    }
                    if (ObjectUtil.isNotNull(jurySecretaryDate) && ObjectUtil.isNotNull(juryMeetingDate)) {
                        Long tempMinutes = DateUtil.countWorkdayHouse(jurySecretaryDate, juryMeetingDate);
                        if (tempMinutes != null) {
                            totalJuryProcessMinutes += tempMinutes;
                            totalJuryProcessCount += 1;
                        }
                    }
                }
                List<ContractBaseInfo> calContractBaseList = new ArrayList<>();
                for (ContractBaseInfo contractBaseInfo : tempContractBaseList) {
                    if (contractBaseInfo.getProjReviewId().equals(projReviewId)) {
                        calContractBaseList.add(contractBaseInfo);
                    }
                }

                List<PaymentBaseInfo> calPaymentBaseInfoList = new ArrayList<>();
                for (PaymentBaseInfo paymentBaseInfo : tempPaymentBaseList) {
                    for (ContractBaseInfo contractBaseInfo : calContractBaseList) {
                        if (paymentBaseInfo.getContractId().equals(contractBaseInfo.getId())) {
                            calPaymentBaseInfoList.add(paymentBaseInfo);
                        }
                    }
                }

                List<PaymentActualDetail> calPaymentActualDetailList = new ArrayList<>();
                for (PaymentActualDetail paymentActualDetail : tempPaymentActualList) {
                    for (PaymentBaseInfo paymentBaseInfo : calPaymentBaseInfoList) {
                        if (paymentActualDetail.getPaymentId().equals(paymentBaseInfo.getId())) {
                            calPaymentActualDetailList.add(paymentActualDetail);
                        }
                    }
                }

                List<PaymentActualDetail> paymentActualDetails = calPaymentActualDetailList.stream()
                        .sorted(Comparator.comparing(PaymentActualDetail::getPaidInDate)).collect(Collectors.toList());
                if (!paymentActualDetails.isEmpty()) {
                    LocalDateTime firstPaidInDate = paymentActualDetails.get(0).getPaidInDate() == null ? null : paymentActualDetails.get(0).getPaidInDate()
                            .atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
                    Long paidMinutes = DateUtil.countWorkdayHouse(applyTime, firstPaidInDate);
                    if (paidMinutes != null) {
                        reviewPaymentMinutes += paidMinutes;
                        reviewCount += 1;
                    }
                }
            }
            //评审投放均耗
            if (reviewPaymentMinutes > 0) {
                BigDecimal days = BigDecimal.valueOf(reviewPaymentMinutes).divide(BigDecimal.valueOf(24*60L*reviewCount), 2, RoundingMode.HALF_UP);
                dashboard.setReviewPaidInTotalTime(new ValueUnitDTO(days.toString(),"天"));
            } else {
                dashboard.setReviewPaidInTotalTime(new ValueUnitDTO("0","天"));
            }
            //评审均耗
            if (totalReviewProcessMinutes > 0) {
                BigDecimal days = BigDecimal.valueOf(totalReviewProcessMinutes).divide(BigDecimal.valueOf(24*60L*totalReviewProcessCount), 2, RoundingMode.HALF_UP);
                dashboard.setReviewTotalTime(new ValueUnitDTO(days.toString(),"天"));
            } else {
                dashboard.setReviewTotalTime(new ValueUnitDTO("0","天"));
            }
            //纪要均耗
            if (totalJuryProcessMinutes > 0) {
                BigDecimal days = BigDecimal.valueOf(totalJuryProcessMinutes).divide(BigDecimal.valueOf(24*60L*totalJuryProcessCount), 2, RoundingMode.HALF_UP);
                dashboard.setSummaryTotalTime(new ValueUnitDTO(days.toString(),"天"));
            } else {
                dashboard.setSummaryTotalTime(new ValueUnitDTO("0","天"));
            }
            //尽调
            long dueDiligenceInfoTotalTime = 0l;
            int dueDiligenceInfoCount = 0;
            for (DashboardDueDiligenceInfo dashboardDueDiligenceInfo : dashboardDueDiligenceInfoList) {
                if (bizDeptId == dashboardDueDiligenceInfo.getDeptId()) {
                    Long dueDiligenceInfoMinutes = DateUtil.countWorkdayHouse(dashboardDueDiligenceInfo.getDueDiligenceTime(), dashboardDueDiligenceInfo.getDueDiligenceReportTime());
                    if (dueDiligenceInfoMinutes != null) {
                        dueDiligenceInfoTotalTime += dueDiligenceInfoMinutes;
                        dueDiligenceInfoCount++;
                    }
                }
            }
            if (dueDiligenceInfoTotalTime > 0) {
                BigDecimal days = BigDecimal.valueOf(dueDiligenceInfoTotalTime).divide(BigDecimal.valueOf(24*60L*dueDiligenceInfoCount), 2, RoundingMode.HALF_UP);
                dashboard.setDueDiligenceTotalTime(new ValueUnitDTO(days.toString(),"天"));
            } else {
                dashboard.setDueDiligenceTotalTime(new ValueUnitDTO("0","天"));
            }
        }
        return res;
    }

    public List<DashboardOperationTimePercentageRSP> timeTerm(DashboardOperationTimeStatisticsREQ req) {
        List<DashboardOperationTimePercentageRSP> resp = new ArrayList<>();
        //当期平均
        DashboardOperationTimeStatisticsREQ reqA = new DashboardOperationTimeStatisticsREQ();
        BeanUtil.copyProperties(req, reqA);

        //去年同期
        DashboardOperationTimeStatisticsREQ reqB = new DashboardOperationTimeStatisticsREQ();
        reqB.setType(req.getType());
        LocalDate sameFromMonthLastYear = req.getQueryDateFrom().minus(1, ChronoUnit.YEARS)
                .withDayOfMonth(req.getQueryDateFrom().getDayOfMonth());
        LocalDate sameToMonthLastYear = req.getQueryDateTo().minus(1, ChronoUnit.YEARS)
                .withDayOfMonth(req.getQueryDateTo().getDayOfMonth());
        reqB.setQueryDateFrom(sameFromMonthLastYear);
        reqB.setQueryDateTo(sameToMonthLastYear);

        //本年平均
        DashboardOperationTimeStatisticsREQ reqC = new DashboardOperationTimeStatisticsREQ();
        LocalDate beginTime = req.getQueryDateFrom().with(TemporalAdjusters.firstDayOfYear());
        LocalDate endTime = req.getQueryDateTo().with(TemporalAdjusters.lastDayOfYear());
        reqC.setType(req.getType());
        reqC.setQueryDateFrom(beginTime);
        reqC.setQueryDateTo(endTime);

        //去年平均
        DashboardOperationTimeStatisticsREQ reqD = new DashboardOperationTimeStatisticsREQ();
        LocalDate firstDayOfLastYear = req.getQueryDateFrom().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfLastYear = req.getQueryDateTo().minusYears(1).with(TemporalAdjusters.lastDayOfYear());
        reqD.setType(req.getType());
        reqD.setQueryDateFrom(firstDayOfLastYear);
        reqD.setQueryDateTo(lastDayOfLastYear);

        List<Map<String, List<DashboardOperationTimeStatisticsRSP>>> result = new ArrayList<>();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
        List<Callable<Map<String, List<DashboardOperationTimeStatisticsRSP>>>> tasks = new ArrayList<>();
        //当期平均
        tasks.add(new DashboardOperationTimeCallerTask(reqA, DashboardOperationTermEnum.CURRENT_TERM.name()));
        //去年同期
        tasks.add(new DashboardOperationTimeCallerTask(reqB, DashboardOperationTermEnum.LAST_TERM.name()));
        //本年平均
        tasks.add(new DashboardOperationTimeCallerTask(reqC, DashboardOperationTermEnum.CURRENT_YEAR.name()));
        //去年平均
        tasks.add(new DashboardOperationTimeCallerTask(reqD, DashboardOperationTermEnum.LAST_YEAR.name()));
        try {
            List<Future<Map<String, List<DashboardOperationTimeStatisticsRSP>>>> results = fixedThreadPool.invokeAll(tasks);
            for (Future<Map<String, List<DashboardOperationTimeStatisticsRSP>>> res : results) {
                result.add(res.get());
            }
            fixedThreadPool.shutdown();
            while (true){
                if (fixedThreadPool.isTerminated()){
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("待办的error", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        if (ObjectUtil.isEmpty(result)) {
            return Collections.emptyList();
        }
        for (Map<String, List<DashboardOperationTimeStatisticsRSP>> map : result) {
            if (map.containsKey(DashboardOperationTermEnum.CURRENT_TERM.name())) {
                DashboardOperationTimePercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.CURRENT_TERM.name(), map);
                resp.add(percentageRSP);
            } else if (map.containsKey(DashboardOperationTermEnum.LAST_TERM.name())) {
                DashboardOperationTimePercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.LAST_TERM.name(), map);
                resp.add(percentageRSP);
            } else if (map.containsKey(DashboardOperationTermEnum.CURRENT_YEAR.name())) {
                DashboardOperationTimePercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.CURRENT_YEAR.name(), map);
                resp.add(percentageRSP);
            } else if (map.containsKey(DashboardOperationTermEnum.LAST_YEAR.name())) {
                DashboardOperationTimePercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.LAST_YEAR.name(), map);
                resp.add(percentageRSP);
            }
        }
        return resp;
    }

    public List<DashboardOperationTimeStatisticsRSP> detailList(DashboardOperationTimeListREQ req) {
        DashboardOperationTimeStatisticsREQ query = buildQuery(req);
        if(CollectionUtils.isNotEmpty(req.getBizDeptIdList())) {
            query.setBizDeptIdList(req.getBizDeptIdList());
        }
        List<DashboardOperationTimeStatisticsRSP> timeStatisticsRSPS = timeList(query);
        return timeStatisticsRSPS;
    }

    private DashboardOperationTimeStatisticsREQ buildQuery(DashboardOperationTimeListREQ req){
        DashboardOperationTimeStatisticsREQ query = BeanUtil.copyProperties(req, DashboardOperationTimeStatisticsREQ.class);
        if(DashboardOperationBaseREQ.publicType.equals(req.getType())) {
            List<OrgDO> orgDOList = sysUserService.listBizDeptSort(req.getType());
            List<Long> orgIdList = orgDOList.stream().filter(f -> Arrays.asList("浙江业务部", "公用事业业务部").contains(f.getName()))
                    .map(OrgDO::getId).collect(Collectors.toList());
            query.setBizDeptIdList((orgIdList));
        }
        return query;
    }

    private DashboardOperationTimePercentageRSP generateKey(String key, Map<String, List<DashboardOperationTimeStatisticsRSP>> map) {
        DashboardOperationTimePercentageRSP percentageRSP = new DashboardOperationTimePercentageRSP();
        double projEstaTotalTime = 0.00;
        double dueDiligenceTotalTime = 0.00;
        double reviewTotalTime = 0.00;
        double summaryTotalTime = 0.00;
        double projEstaPaidInTotalTime = 0.00;
        double reviewPaidInTotalTime = 0.00;
        int projEstaTotalCount = 0;
        int dueDiligenceTotalCount = 0;
        int reviewTotalCount = 0;
        int summaryTotalCount = 0;
        int projEstaPaidInTotalCount = 0;
        int reviewPaidInTotalCount = 0;
        List<DashboardOperationTimeStatisticsRSP> list = map.get(key);
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardOperationTimeStatisticsRSP dash : list) {
                if (ObjectUtil.isNotNull(dash.getProjEstaTotalTime())) {
                    ValueUnitDTO dto = dash.getProjEstaTotalTime();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    projEstaTotalTime += doubleValue;
                    projEstaTotalCount++;
                }
                if (ObjectUtil.isNotNull(dash.getDueDiligenceTotalTime())) {
                    ValueUnitDTO dto = dash.getDueDiligenceTotalTime();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    dueDiligenceTotalTime += doubleValue;
                    dueDiligenceTotalCount++;
                }
                if (ObjectUtil.isNotNull(dash.getReviewTotalTime())) {
                    ValueUnitDTO dto = dash.getReviewTotalTime();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    reviewTotalTime += doubleValue;
                    reviewTotalCount++;
                }
                if (ObjectUtil.isNotNull(dash.getSummaryTotalTime())) {
                    ValueUnitDTO dto = dash.getSummaryTotalTime();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    summaryTotalTime += doubleValue;
                    summaryTotalCount++;
                }
                if (ObjectUtil.isNotNull(dash.getProjEstaPaidInTotalTime())) {
                    ValueUnitDTO dto = dash.getProjEstaPaidInTotalTime();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    projEstaPaidInTotalTime += doubleValue;
                    projEstaPaidInTotalCount++;
                }
                if (ObjectUtil.isNotNull(dash.getReviewPaidInTotalTime())) {
                    ValueUnitDTO dto = dash.getReviewPaidInTotalTime();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    reviewPaidInTotalTime += doubleValue;
                    reviewPaidInTotalCount++;
                }
            }
        }
        percentageRSP.setTermName(key);
        if (projEstaTotalTime > 0) {
            BigDecimal days = BigDecimal.valueOf(projEstaTotalTime).divide(BigDecimal.valueOf(projEstaTotalCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setProjEstaTotalTime(new ValueUnitDTO(days.toString(),"天"));
        } else {
            percentageRSP.setProjEstaTotalTime(new ValueUnitDTO("0","天"));
        }

        if (dueDiligenceTotalTime > 0) {
            BigDecimal days = BigDecimal.valueOf(dueDiligenceTotalTime).divide(BigDecimal.valueOf(dueDiligenceTotalCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setDueDiligenceTotalTime(new ValueUnitDTO(days.toString(),"天"));
        } else {
            percentageRSP.setDueDiligenceTotalTime(new ValueUnitDTO("0","天"));
        }

        if (reviewTotalTime > 0) {
            BigDecimal days = BigDecimal.valueOf(reviewTotalTime).divide(BigDecimal.valueOf(reviewTotalCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setReviewTotalTime(new ValueUnitDTO(days.toString(),"天"));
        } else {
            percentageRSP.setReviewTotalTime(new ValueUnitDTO("0","天"));
        }

        if (summaryTotalTime > 0) {
            BigDecimal days = BigDecimal.valueOf(summaryTotalTime).divide(BigDecimal.valueOf(summaryTotalCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setSummaryTotalTime(new ValueUnitDTO(days.toString(),"天"));
        } else {
            percentageRSP.setSummaryTotalTime(new ValueUnitDTO("0","天"));
        }

        if (projEstaPaidInTotalTime > 0) {
            BigDecimal days = BigDecimal.valueOf(projEstaPaidInTotalTime).divide(BigDecimal.valueOf(projEstaPaidInTotalCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setProjEstaPaidInTotalTime(new ValueUnitDTO(days.toString(),"天"));
        } else {
            percentageRSP.setProjEstaPaidInTotalTime(new ValueUnitDTO("0","天"));
        }

        if (reviewPaidInTotalTime > 0) {
            BigDecimal days = BigDecimal.valueOf(reviewPaidInTotalTime).divide(BigDecimal.valueOf(reviewPaidInTotalCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setReviewPaidInTotalTime(new ValueUnitDTO(days.toString(),"天"));
        } else {
            percentageRSP.setReviewPaidInTotalTime(new ValueUnitDTO("0","天"));
        }
        return percentageRSP;
    }

    public class DashboardOperationTimeCallerTask implements Callable<Map<String, List<DashboardOperationTimeStatisticsRSP>>> {
        private DashboardOperationTimeStatisticsREQ req;
        private String type;
        public DashboardOperationTimeCallerTask(DashboardOperationTimeStatisticsREQ req, String type) {
            this.req = req;
            this.type = type;
        }
        @Override
        public Map<String, List<DashboardOperationTimeStatisticsRSP>> call() throws Exception {
            Map<String, List<DashboardOperationTimeStatisticsRSP>> map = new HashMap<>();
            List<DashboardOperationTimeStatisticsRSP> res = timeList(req);
            map.put(type, res);
            return map;
        }
    }


    private Page<ProcessResp> getProjEstablishEffect(LocalDate endTimeFrom) {
        //多查询6个月，防止以前创建的
        endTimeFrom = endTimeFrom.minusMonths(6);
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ProjEstablishCreateFlow.name()));
        processPageReq.setPageIndex(1);
        processPageReq.setProcessCreateTimeFrom(Date.from(endTimeFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        processPageReq.setPageSize(Integer.MAX_VALUE);
        processPageReq.setProcessStatusList(Arrays.asList(2, 6));
        return taskApiService.queryProcess(processPageReq);
    }

    public Page<ProcessResp> getReviewBaseProcessByIds(List<String> businessKeys) {
        ProcessPageReq processReq = new ProcessPageReq();
        processReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ProjReviewCreateFlow.name()));
        processReq.setBusinessKeyList(businessKeys);
        processReq.setProcessStatusList(Arrays.asList(2, 6));
        processReq.setPageIndex(1);
        processReq.setPageSize(Integer.MAX_VALUE);
        cn.zswltech.flow.core.util.Page<ProcessResp> processRespPage = taskApiService.queryProcess(processReq);
        return processRespPage;
    }

    private List<ProcessResp> filterProcessResp(Page<ProcessResp> processRespPage, DashboardOperationTimeStatisticsREQ req) {
        if (ObjectUtil.isEmpty(processRespPage) || ObjectUtil.isEmpty(processRespPage.getContents())) {
            return Collections.emptyList();
        }

        List<ProcessResp> contents = processRespPage.getContents();
        return contents.stream().filter(content -> {
            if (ObjectUtil.isNotEmpty(req.getQueryDateFrom()) && ObjectUtil.isNotEmpty(content.getEndTime()) &&
                    req.getQueryDateFrom().isAfter(content.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(req.getQueryDateTo()) && ObjectUtil.isNotEmpty(content.getEndTime()) &&
                    req.getQueryDateTo().isBefore(content.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }


}
