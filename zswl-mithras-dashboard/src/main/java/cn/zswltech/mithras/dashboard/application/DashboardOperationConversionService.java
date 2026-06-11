package cn.zswltech.mithras.dashboard.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.dashboard.enums.DashboardOperationTermEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.dashboard.mapper.*;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.dashboard.model.*;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projestablish.ProjEstablishBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.dashboard.model.DashboardCorpCommerceInfoLibDto;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.dashboard.application.util.DashboardOperationUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ClassName DashboardOperationConversionService
 * @Description TODO
 * @Author zhouning
 * @Date 2024/7/29 5:05 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class DashboardOperationConversionService implements cn.zswltech.mithras.dashboard.application.DashboardOperationConversionApplicationService {

    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ProjEstablishBaseInfoMapper projEstablishBaseInfoMapper;
    @Resource
    private OrgService orgService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private DashboardAdjustPersonInfoMapper adjustPersonInfoMapper;
    @Resource
    private DashboardVisitInfoMapper dashboardVisitInfoMapper;
    @Resource
    private DashboardDueDiligenceInfoMapper dashboardDueDiligenceInfoMapper;
    @Resource
    private DashboardReviewInfoMapper dashboardReviewInfoMapper;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private DashboardOperationConversionMapper dashboardOperationConversionMapper;

    public List<DashboardOperationConversionListRSP> listAll(DashboardOperationConversionStatisticsREQ req) {
        List<DashboardOperationConversionListRSP> getAllCountConversionList = getAllMatchedCount(req);
        List<Map<String, List<DashboardOperationConversionStatisticsRSP>>> termMap = generateTermMap(req);
        //当期
        List<DashboardOperationConversionStatisticsRSP> currentTermList = new ArrayList<>();
        //去年同期
        List<DashboardOperationConversionStatisticsRSP> lastTermList = new ArrayList<>();
        //今年平均
        List<DashboardOperationConversionStatisticsRSP> currentYearList = new ArrayList<>();
        //去年平均
        List<DashboardOperationConversionStatisticsRSP> lastYearList = new ArrayList<>();
        for (Map<String, List<DashboardOperationConversionStatisticsRSP>> map : termMap) {
            if (map.containsKey(DashboardOperationTermEnum.CURRENT_TERM.name())) {
                currentTermList = map.get(DashboardOperationTermEnum.CURRENT_TERM.name());
            } else if (map.containsKey(DashboardOperationTermEnum.LAST_TERM.name())) {
                lastTermList = map.get(DashboardOperationTermEnum.LAST_TERM.name());
            } else if (map.containsKey(DashboardOperationTermEnum.CURRENT_YEAR.name())) {
                currentYearList = map.get(DashboardOperationTermEnum.CURRENT_YEAR.name());
            } else if (map.containsKey(DashboardOperationTermEnum.LAST_YEAR.name())) {
                lastYearList = map.get(DashboardOperationTermEnum.LAST_YEAR.name());
            }
        }
        for (DashboardOperationConversionListRSP conversionListRSP : getAllCountConversionList) {
            Long bizDeptId = conversionListRSP.getBizDeptId();
            for (DashboardOperationConversionStatisticsRSP currentTerm : currentTermList) {
                if (bizDeptId.equals(currentTerm.getBizDeptId())) {
                    conversionListRSP.setDeliveryDueDiliCurrentTerm(currentTerm.getDueDiliDeliveryPer());
                    conversionListRSP.setDeliveryReviewCurrentTerm(currentTerm.getReviewDeliveryPer());
                    conversionListRSP.setDeliveryVisitCurrentTerm(currentTerm.getVisitDeliveryPer());
                    conversionListRSP.setDueDiliProjEstaCurrentTerm(currentTerm.getProjEstaDueDiliPer());
                    conversionListRSP.setProjEstaVisitCurrentTerm(currentTerm.getVisitProjEstaPer());
                    conversionListRSP.setReviewDueDiliCurrentTerm(currentTerm.getDueDiligenceReviewPer());
                }
            }
            for (DashboardOperationConversionStatisticsRSP lastTerm : lastTermList) {
                if (bizDeptId.equals(lastTerm.getBizDeptId())) {
                    conversionListRSP.setDeliveryDueDiliLastTerm(lastTerm.getDueDiliDeliveryPer());
                    conversionListRSP.setDeliveryReviewLastTerm(lastTerm.getReviewDeliveryPer());
                    conversionListRSP.setDeliveryVisitLastTerm(lastTerm.getVisitDeliveryPer());
                    conversionListRSP.setDueDiliProjEstaLastTerm(lastTerm.getProjEstaDueDiliPer());
                    conversionListRSP.setProjEstaVisitLastTerm(lastTerm.getVisitProjEstaPer());
                    conversionListRSP.setReviewDueDiliLastTerm(lastTerm.getDueDiligenceReviewPer());
                }
            }
            for (DashboardOperationConversionStatisticsRSP lastYear : lastYearList) {
                if (bizDeptId.equals(lastYear.getBizDeptId())) {
                    conversionListRSP.setDeliveryDueDiliLastAverage(lastYear.getDueDiliDeliveryPer());
                    conversionListRSP.setDeliveryReviewLastAverage(lastYear.getReviewDeliveryPer());
                    conversionListRSP.setDeliveryVisitLastAverage(lastYear.getVisitDeliveryPer());
                    conversionListRSP.setDueDiliProjEstaLastAverage(lastYear.getProjEstaDueDiliPer());
                    conversionListRSP.setProjEstaVisitLastAverage(lastYear.getVisitProjEstaPer());
                    conversionListRSP.setReviewDueDiliLastAverage(lastYear.getDueDiligenceReviewPer());
                }
            }
            for (DashboardOperationConversionStatisticsRSP currentYear : currentYearList) {
                if (bizDeptId.equals(currentYear.getBizDeptId())) {
                    conversionListRSP.setDeliveryDueDiliCurrentAverage(currentYear.getDueDiliDeliveryPer());
                    conversionListRSP.setDeliveryReviewCurrentAverage(currentYear.getReviewDeliveryPer());
                    conversionListRSP.setDeliveryVisitCurrentAverage(currentYear.getVisitDeliveryPer());
                    conversionListRSP.setDueDiliProjEstaCurrentAverage(currentYear.getProjEstaDueDiliPer());
                    conversionListRSP.setProjEstaVisitCurrentAverage(currentYear.getVisitProjEstaPer());
                    conversionListRSP.setReviewDueDiliCurrentAverage(currentYear.getDueDiligenceReviewPer());
                }
            }
        }
        return getAllCountConversionList;
    }

    private List<DashboardOperationConversionListRSP> getAllMatchedCount(DashboardOperationConversionStatisticsREQ req) {
        if (ObjectUtil.isEmpty(req.getQueryDateFrom())) {
            req.setQueryDateFrom(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        }
        if (ObjectUtil.isEmpty(req.getQueryDateTo())) {
            req.setQueryDateTo(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()));
        }
        List<String> riskControlIndustryClassify = null;
        if (StringUtils.isNotBlank(req.getType())) {
            riskControlIndustryClassify = DashboardOperationUtil.getRiskControlIndustryClassify(req);
        }
        Page<ProcessResp> processRespPage = getProjEstablishEffect(req.getQueryDateFrom());
        Map<Long, ProcessResp> projEstablishId2ProcessMap = filterProcessResp(processRespPage, req).stream().collect(Collectors.toMap(e -> Long.valueOf(e.getBusinessKey()), e -> e, (a, b) -> a));
        List<ProjEstablishBaseInfo> projEstablishBaseInfos = null;
        List<ProjEstablishBaseInfo> projEstablishBaseInfoProcessList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(projEstablishId2ProcessMap)) {
            projEstablishBaseInfos = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                    .in(ProjEstablishBaseInfo::getId, projEstablishId2ProcessMap.keySet())
            );
            Set<Long> projEstablishBaseInfoIdset = projEstablishBaseInfos.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toSet());
            Map<Long, List<ProjEstablishBaseInfo>> groupProjEstaByClientId = projEstablishBaseInfos.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getClientId));
            if (!projEstablishBaseInfoIdset.isEmpty()) {
                DashboardCorpCommerceInfoLibDto dto = new DashboardCorpCommerceInfoLibDto();
                dto.setInRiskControlIndustryClassify(riskControlIndustryClassify);
                dto.setInClientIds(projEstablishBaseInfoIdset);
                List<CorpCommerceInfoLib> corpCommerceInfoLibs = dashboardOperationConversionMapper.listNewestCommerceInfo(dto);
                for (Map.Entry<Long, List<ProjEstablishBaseInfo>> entry : groupProjEstaByClientId.entrySet()) {
                    Long clientId = entry.getKey();
                    for (CorpCommerceInfoLib corpCommerceInfoLib : corpCommerceInfoLibs) {
                        if (clientId.equals(corpCommerceInfoLib.getClientId())) {
                            projEstablishBaseInfoProcessList.addAll(groupProjEstaByClientId.get(clientId));
                        }
                    }
                }
            }
        }

        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .ge(PaymentActualDetail::getPaidInDate, req.getQueryDateFrom())
                .le(PaymentActualDetail::getPaidInDate, req.getQueryDateTo()));

        Map<Long, List<PaymentActualDetail>> groupByPaymentId = new HashMap<>();
        Set<Long> paymentIdSet = new HashSet<>();
        for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
            if (paymentActualDetail != null && paymentActualDetail.getPaymentId() != null) {
                groupByPaymentId.putIfAbsent(paymentActualDetail.getPaymentId(), new ArrayList<>());
                groupByPaymentId.get(paymentActualDetail.getPaymentId()).add(paymentActualDetail);
                paymentIdSet.add(paymentActualDetail.getPaymentId());
            }
        }
        List<PaymentBaseInfo> paymentBaseInfoList = new ArrayList<>();
        Set<Long> contractIdSet = new HashSet<>();
        if (!paymentIdSet.isEmpty()) {
            paymentBaseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery().in(PaymentBaseInfo::getId, paymentIdSet));
            contractIdSet = paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId)
                    .collect(Collectors.toSet());
        }

        List<ContractBaseInfo> contractBaseInfoList = new ArrayList<>();
        Set<Long> projReviewIdSet = new HashSet<>();
        if (!contractIdSet.isEmpty()) {
            contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getId, contractIdSet));
            projReviewIdSet = contractBaseInfoList.stream().map(ContractBaseInfo::getProjReviewId)
                    .collect(Collectors.toSet());
        }

        List<ProjReviewBaseInfo> projReviewBaseInfoList = new ArrayList<>();
        Set<Long> projEstablishIdSet = new HashSet<>();
        if (!projReviewIdSet.isEmpty()) {
            projReviewBaseInfoList = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, projReviewIdSet));
            projEstablishIdSet = projReviewBaseInfoList.stream().map(ProjReviewBaseInfo::getProjEstablishId)
                    .collect(Collectors.toSet());
        }

        List<ProjEstablishBaseInfo> projEstablishBaseInfoList = new ArrayList<>();
        List<ProjEstablishBaseInfo> projEstablishBaseInfoIndustryList = new ArrayList<>();
        if (!projEstablishIdSet.isEmpty()) {
            projEstablishBaseInfoIndustryList = projEstablishBaseInfoMapper.selectList(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().in(ProjEstablishBaseInfo::getId, projEstablishIdSet));
        }

        Map<Long, List<ProjEstablishBaseInfo>> groupProjEstaByClientId = projEstablishBaseInfoIndustryList.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfo::getClientId));
        Set<Long> projEstablishBaseInfoIdset = projEstablishBaseInfoIndustryList.stream().map(ProjEstablishBaseInfo::getClientId).collect(Collectors.toSet());
        if (!projEstablishBaseInfoIdset.isEmpty()) {
            DashboardCorpCommerceInfoLibDto dto = new DashboardCorpCommerceInfoLibDto();
            dto.setInRiskControlIndustryClassify(riskControlIndustryClassify);
            dto.setInClientIds(projEstablishBaseInfoIdset);
            List<CorpCommerceInfoLib> corpCommerceInfoLibs = dashboardOperationConversionMapper.listNewestCommerceInfo(dto);
            for (Map.Entry<Long, List<ProjEstablishBaseInfo>> entry : groupProjEstaByClientId.entrySet()) {
                Long clientId = entry.getKey();
                for (CorpCommerceInfoLib corpCommerceInfoLib : corpCommerceInfoLibs) {
                    if (clientId.equals(corpCommerceInfoLib.getClientId())) {
                        projEstablishBaseInfoList.addAll(groupProjEstaByClientId.get(clientId));
                    }
                }
            }
        }

        //人力调整
        List<DashboardAdjustPersonInfo> latest = adjustPersonInfoMapper.getLatest(new DashboardAdjustPersonLatestQuery(req.getType(), req.getBizDeptIdList()));
        Map<Long, BigDecimal> adjustPersonMap = DashboardOperationUtil.getAdjustPerson(latest);
        //拜访明细
        List<DashboardVisitInfo> dashboardVisitInfoList = dashboardVisitInfoMapper.selectList(Wrappers.<DashboardVisitInfo>lambdaQuery()
                .ge(DashboardVisitInfo::getVisitTime, req.getQueryDateFrom())
                .le(DashboardVisitInfo::getVisitTime, req.getQueryDateTo())
                .in(StringUtils.isNotBlank(req.getType()), DashboardVisitInfo::getBusinessGroup, req.getType()));
        //尽调明细
        List<DashboardDueDiligenceInfo> dashboardDueDiligenceInfoList = dashboardDueDiligenceInfoMapper.selectList(Wrappers.<DashboardDueDiligenceInfo>lambdaQuery()
                .ge(DashboardDueDiligenceInfo::getDueDiligenceTime, req.getQueryDateFrom())
                .le(DashboardDueDiligenceInfo::getDueDiligenceTime, req.getQueryDateTo())
                .in(StringUtils.isNotBlank(req.getType()), DashboardDueDiligenceInfo::getBusinessGroup, req.getType()));
        //评审明细
        List<DashboardReviewInfo> dashboardReviewInfoList = dashboardReviewInfoMapper.selectList(Wrappers.<DashboardReviewInfo>lambdaQuery()
                .ge(DashboardReviewInfo::getConvokeTime, req.getQueryDateFrom())
                .le(DashboardReviewInfo::getConvokeTime, req.getQueryDateTo())
                .in(StringUtils.isNotBlank(req.getType()), DashboardReviewInfo::getBusinessGroup, req.getType()));

        List<DashboardOperationConversionListRSP> res = new ArrayList<>();
        for (OrgDO orgDO : sysUserService.listBizDeptSort(req.getType())) {
            if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
//            if(OrgConstants.DISCARD_ORG.contains(orgDO.getCode()) || (CollectionUtils.isNotEmpty(req.getBizDeptIdList()) && !req.getBizDeptIdList().contains(orgDO.getId()))){
//                continue;
//            }
            DashboardOperationConversionListRSP conversionListRSP = new DashboardOperationConversionListRSP();
            res.add(conversionListRSP);
            long bizDeptId = orgDO.getId();
            String bizDeptName = orgDO.getName();
            conversionListRSP.setBizDeptId(bizDeptId);
            conversionListRSP.setBizDeptName(bizDeptName);
            //调整人数
            BigDecimal adjustPersonSum = adjustPersonMap.getOrDefault(orgDO.getId(),BigDecimal.ZERO);
            if(!Objects.equals(adjustPersonSum, BigDecimal.ZERO)) {
                conversionListRSP.setAdjustCount(new ValueUnitDTO(adjustPersonSum.toString(), "个"));
            } else {
                conversionListRSP.setAdjustCount(new ValueUnitDTO("0", "个"));
            }
            //投放个数
            List<ProjEstablishBaseInfo> tempProjEstablishBaseInfoList = new ArrayList<>();
            List<ProjEstablishBaseInfo> tempProjEstablishBaseInfoProcessList = new ArrayList<>();
            for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoList) {
                if (projEstablishBaseInfo.getBizDeptId().equals(bizDeptId)) {
                    tempProjEstablishBaseInfoList.add(projEstablishBaseInfo);
                }
            }
            BigDecimal deliveryCount = BigDecimal.valueOf(tempProjEstablishBaseInfoList.size());
            conversionListRSP.setDeliveryCount(new ValueUnitDTO(deliveryCount.toString(),"个"));
            //立项个数
            if (projEstablishBaseInfoProcessList != null && !projEstablishBaseInfoProcessList.isEmpty()) {
                for (ProjEstablishBaseInfo projEstablishBaseInfo : projEstablishBaseInfoProcessList) {
                    if (projEstablishBaseInfo.getBizDeptId().equals(bizDeptId)) {
                        tempProjEstablishBaseInfoProcessList.add(projEstablishBaseInfo);
                    }
                }
            }
            BigDecimal projEstaCount = BigDecimal.valueOf(tempProjEstablishBaseInfoProcessList.size());
            conversionListRSP.setProjEstaCount(new ValueUnitDTO(projEstaCount.toString(),"个"));
            //拜访个数
            int totalVisitCount = 0;
            for (DashboardVisitInfo dashboardVisitInfo : dashboardVisitInfoList) {
                if (bizDeptId == dashboardVisitInfo.getDeptId()) {
                    totalVisitCount += dashboardVisitInfo.getVisitClientCount();
                }
            }
            BigDecimal visitInfoCount = BigDecimal.valueOf(totalVisitCount);
            conversionListRSP.setVisitCount(new ValueUnitDTO(visitInfoCount.toString(),"个"));
            //尽调
            List<DashboardDueDiligenceInfo> tempDueDiligenceInfoList = new ArrayList<>();
            for (DashboardDueDiligenceInfo dashboardDueDiligenceInfo : dashboardDueDiligenceInfoList) {
                if (bizDeptId == dashboardDueDiligenceInfo.getDeptId()) {
                    tempDueDiligenceInfoList.add(dashboardDueDiligenceInfo);
                }
            }
            BigDecimal dueDiligenceInfoCount = BigDecimal.valueOf(tempDueDiligenceInfoList.size());
            conversionListRSP.setDueDiligenceCount(new ValueUnitDTO(dueDiligenceInfoCount.toString(),"个"));
            //评审
            List<DashboardReviewInfo> tempDashboardReviewInfoList = new ArrayList<>();
            for (DashboardReviewInfo dashboardReviewInfo : dashboardReviewInfoList) {
                if (bizDeptId == dashboardReviewInfo.getDeptId()) {
                    tempDashboardReviewInfoList.add(dashboardReviewInfo);
                }
            }
            BigDecimal reviewInfoCount = BigDecimal.valueOf(tempDashboardReviewInfoList.size());
            conversionListRSP.setReviewCount(new ValueUnitDTO(reviewInfoCount.toString(),"个"));
        }
        return res;
    }

    public List<DashboardOperationConversionStatisticsRSP> timeList(DashboardOperationConversionStatisticsREQ req) {
        List<DashboardOperationConversionListRSP> res = getAllMatchedCount(req);
        if (res == null || res.isEmpty()) {
            return Collections.emptyList();
        }
        List<DashboardOperationConversionStatisticsRSP> conversionStatisticsRSPS = new ArrayList<>();
        for (DashboardOperationConversionListRSP conversionListRSP : res) {
            DashboardOperationConversionStatisticsRSP rsp = new DashboardOperationConversionStatisticsRSP();
            conversionStatisticsRSPS.add(rsp);
            rsp.setBizDeptId(conversionListRSP.getBizDeptId());
            rsp.setBizDeptName(conversionListRSP.getBizDeptName());
            //访客-立项
            if (conversionListRSP.getVisitCount() != null
                    && conversionListRSP.getProjEstaCount() != null
                    && Long.parseLong(conversionListRSP.getVisitCount().getValue()) > 0) {
                BigDecimal rate = BigDecimal.valueOf(Long.parseLong(conversionListRSP.getProjEstaCount().getValue()))
                        .divide(BigDecimal.valueOf(Long.parseLong(conversionListRSP.getVisitCount().getValue())), 2, RoundingMode.HALF_UP);
                rsp.setVisitProjEstaPer(new ValueUnitDTO(rate.multiply(new BigDecimal("100")).toPlainString(), "%"));
            } else {
                rsp.setVisitProjEstaPer(new ValueUnitDTO(new BigDecimal("0").toPlainString(), "%"));
            }
            //访客-投放
            if (conversionListRSP.getDeliveryCount() != null
                    && conversionListRSP.getVisitCount() != null
                    && Long.parseLong(conversionListRSP.getVisitCount().getValue()) > 0) {
                BigDecimal rate = BigDecimal.valueOf(Long.parseLong(conversionListRSP.getDeliveryCount().getValue()))
                        .divide(BigDecimal.valueOf(Long.parseLong(conversionListRSP.getVisitCount().getValue())), 2, RoundingMode.HALF_UP);
                rsp.setVisitDeliveryPer(new ValueUnitDTO(rate.multiply(new BigDecimal("100")).toPlainString(), "%"));
            } else {
                rsp.setVisitDeliveryPer(new ValueUnitDTO(new BigDecimal("0").toPlainString(), "%"));
            }
            //立项-尽调
            if (conversionListRSP.getDueDiligenceCount() != null
                    && conversionListRSP.getProjEstaCount() != null
                    && Long.parseLong(conversionListRSP.getProjEstaCount().getValue()) > 0) {
                BigDecimal rate = BigDecimal.valueOf(Long.parseLong(conversionListRSP.getDueDiligenceCount().getValue()))
                        .divide(BigDecimal.valueOf(Long.parseLong(conversionListRSP.getProjEstaCount().getValue())), 2, RoundingMode.HALF_UP);
                rsp.setProjEstaDueDiliPer(new ValueUnitDTO(rate.multiply(new BigDecimal("100")).toPlainString(), "%"));
            } else {
                rsp.setProjEstaDueDiliPer(new ValueUnitDTO(new BigDecimal("0").toPlainString(), "%"));
            }
            //尽调-评审
            if (conversionListRSP.getReviewCount() != null
                    && conversionListRSP.getDueDiligenceCount() != null
                    && Long.parseLong(conversionListRSP.getDueDiligenceCount().getValue()) > 0) {
                BigDecimal rate = BigDecimal.valueOf(Long.parseLong(conversionListRSP.getReviewCount().getValue()))
                        .divide(BigDecimal.valueOf(Long.parseLong(conversionListRSP.getDueDiligenceCount().getValue())), 2, RoundingMode.HALF_UP);
                rsp.setDueDiligenceReviewPer(new ValueUnitDTO(rate.multiply(new BigDecimal("100")).toPlainString(), "%"));
            } else {
                rsp.setDueDiligenceReviewPer(new ValueUnitDTO(new BigDecimal("0").toPlainString(), "%"));
            }
            //尽调-投放
            if (conversionListRSP.getDeliveryCount() != null
                    && conversionListRSP.getDueDiligenceCount() != null
                    && Long.parseLong(conversionListRSP.getDueDiligenceCount().getValue()) > 0) {
                BigDecimal rate = BigDecimal.valueOf(Long.parseLong(conversionListRSP.getDeliveryCount().getValue()))
                        .divide(BigDecimal.valueOf(Long.parseLong(conversionListRSP.getDueDiligenceCount().getValue())), 2, RoundingMode.HALF_UP);
                rsp.setDueDiliDeliveryPer(new ValueUnitDTO(rate.multiply(new BigDecimal("100")).toPlainString(), "%"));
            } else {
                rsp.setDueDiliDeliveryPer(new ValueUnitDTO(new BigDecimal("0").toPlainString(), "%"));
            }
            //评审-投放
            if (conversionListRSP.getDeliveryCount() != null
                    && conversionListRSP.getReviewCount() != null
                    && Long.parseLong(conversionListRSP.getReviewCount().getValue()) > 0) {
                BigDecimal rate = BigDecimal.valueOf(Long.parseLong(conversionListRSP.getDeliveryCount().getValue()))
                        .divide(BigDecimal.valueOf(Long.parseLong(conversionListRSP.getReviewCount().getValue())), 2, RoundingMode.HALF_UP);
                rsp.setReviewDeliveryPer(new ValueUnitDTO(rate.multiply(new BigDecimal("100")).toPlainString(), "%"));
            } else {
                rsp.setReviewDeliveryPer(new ValueUnitDTO(new BigDecimal("0").toPlainString(), "%"));
            }
        }
        return conversionStatisticsRSPS;
    }

    public List<DashboardOperationConversionPercentageRSP> timeTerm(DashboardOperationConversionStatisticsREQ req) {
        List<DashboardOperationConversionPercentageRSP> resp = new ArrayList<>();
        List<Map<String, List<DashboardOperationConversionStatisticsRSP>>> result = generateTermMap(req);
        for (Map<String, List<DashboardOperationConversionStatisticsRSP>> map : result) {
            if (map.containsKey(DashboardOperationTermEnum.CURRENT_TERM.name())) {
                DashboardOperationConversionPercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.CURRENT_TERM.name(), map);
                resp.add(percentageRSP);
            } else if (map.containsKey(DashboardOperationTermEnum.LAST_TERM.name())) {
                DashboardOperationConversionPercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.LAST_TERM.name(), map);
                resp.add(percentageRSP);
            } else if (map.containsKey(DashboardOperationTermEnum.CURRENT_YEAR.name())) {
                DashboardOperationConversionPercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.CURRENT_YEAR.name(), map);
                resp.add(percentageRSP);
            } else if (map.containsKey(DashboardOperationTermEnum.LAST_YEAR.name())) {
                DashboardOperationConversionPercentageRSP percentageRSP = generateKey(DashboardOperationTermEnum.LAST_YEAR.name(), map);
                resp.add(percentageRSP);
            }
        }
        return resp;
    }

    private List<Map<String, List<DashboardOperationConversionStatisticsRSP>>> generateTermMap(DashboardOperationConversionStatisticsREQ req) {
        //当期平均
        DashboardOperationConversionStatisticsREQ reqA = new DashboardOperationConversionStatisticsREQ();
        BeanUtil.copyProperties(req, reqA);

        //去年同期
        DashboardOperationConversionStatisticsREQ reqB = new DashboardOperationConversionStatisticsREQ();
        reqB.setType(req.getType());
        LocalDate sameFromMonthLastYear = req.getQueryDateFrom().minus(1, ChronoUnit.YEARS)
                .withDayOfMonth(req.getQueryDateFrom().getDayOfMonth());
        LocalDate sameToMonthLastYear = req.getQueryDateTo().minus(1, ChronoUnit.YEARS)
                .withDayOfMonth(req.getQueryDateTo().getDayOfMonth());
        reqB.setQueryDateFrom(sameFromMonthLastYear);
        reqB.setQueryDateTo(sameToMonthLastYear);

        //本年平均
        DashboardOperationConversionStatisticsREQ reqC = new DashboardOperationConversionStatisticsREQ();
        LocalDate beginTime = req.getQueryDateFrom().with(TemporalAdjusters.firstDayOfYear());
        LocalDate endTime = req.getQueryDateTo().with(TemporalAdjusters.lastDayOfYear());
        reqC.setType(req.getType());
        reqC.setQueryDateFrom(beginTime);
        reqC.setQueryDateTo(endTime);

        //去年平均
        DashboardOperationConversionStatisticsREQ reqD = new DashboardOperationConversionStatisticsREQ();
        LocalDate firstDayOfLastYear = req.getQueryDateFrom().minusYears(1).with(TemporalAdjusters.firstDayOfYear());
        LocalDate lastDayOfLastYear = req.getQueryDateTo().minusYears(1).with(TemporalAdjusters.lastDayOfYear());
        reqD.setType(req.getType());
        reqD.setQueryDateFrom(firstDayOfLastYear);
        reqD.setQueryDateTo(lastDayOfLastYear);


        List<Map<String, List<DashboardOperationConversionStatisticsRSP>>> result = new ArrayList<>();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
        List<Callable<Map<String, List<DashboardOperationConversionStatisticsRSP>>>> tasks = new ArrayList<>();
        //当期平均
        tasks.add(new DashboardOperationConversionCallerTask(reqA, DashboardOperationTermEnum.CURRENT_TERM.name()));
        //去年同期
        tasks.add(new DashboardOperationConversionCallerTask(reqB, DashboardOperationTermEnum.LAST_TERM.name()));
        //本年平均
        tasks.add(new DashboardOperationConversionCallerTask(reqC, DashboardOperationTermEnum.CURRENT_YEAR.name()));
        //去年平均
        tasks.add(new DashboardOperationConversionCallerTask(reqD, DashboardOperationTermEnum.LAST_YEAR.name()));
        try {
            List<Future<Map<String, List<DashboardOperationConversionStatisticsRSP>>>> results = fixedThreadPool.invokeAll(tasks);
            for (Future<Map<String, List<DashboardOperationConversionStatisticsRSP>>> res : results) {
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
        return result;
    }


    public List<DashboardOperationConversionListRSP> detailList(DashboardOperationConversionListREQ req) {
        DashboardOperationConversionStatisticsREQ query = buildQuery(req);
        if(CollectionUtils.isNotEmpty(req.getBizDeptIdList())) {
            query.setBizDeptIdList(req.getBizDeptIdList());
        }
        List<DashboardOperationConversionListRSP> timeStatisticsRSPS = listAll(query);
        return timeStatisticsRSPS;
    }

    private DashboardOperationConversionStatisticsREQ buildQuery(DashboardOperationConversionListREQ req){
        DashboardOperationConversionStatisticsREQ query = BeanUtil.copyProperties(req, DashboardOperationConversionStatisticsREQ.class);
        if(DashboardOperationBaseREQ.publicType.equals(req.getType())) {
            List<OrgDO> orgDOList = sysUserService.listBizDeptSort(req.getType());
            List<Long> orgIdList = orgDOList.stream().filter(f -> Arrays.asList("浙江业务部", "公用事业业务部").contains(f.getName()))
                    .map(OrgDO::getId).collect(Collectors.toList());
            query.setBizDeptIdList((orgIdList));
        }
        return query;
    }


    private DashboardOperationConversionPercentageRSP generateKey(String key, Map<String, List<DashboardOperationConversionStatisticsRSP>> map) {
        DashboardOperationConversionPercentageRSP percentageRSP = new DashboardOperationConversionPercentageRSP();
        double VisitProjEstaPer = 0.00;
        double VisitDeliveryPer = 0.00;
        double ProjEstaDueDiliPer = 0.00;
        double dueDiligenceReviewPer = 0.00;
        double DueDiliDeliveryPer = 0.00;
        double ReviewDeliveryPer = 0.00;
        int VisitProjEstaPerCount = 0;
        int VisitDeliveryPerCount = 0;
        int ProjEstaDueDiliPerCount = 0;
        int dueDiligenceReviewPerCount = 0;
        int DueDiliDeliveryPerCount = 0;
        int ReviewDeliveryPerCount = 0;
        List<DashboardOperationConversionStatisticsRSP> list = map.get(key);
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardOperationConversionStatisticsRSP dash : list) {
                if (ObjectUtil.isNotNull(dash.getVisitProjEstaPer())) {
                    ValueUnitDTO dto = dash.getVisitProjEstaPer();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    VisitProjEstaPer += doubleValue;
                    VisitProjEstaPerCount++;
                }
                if (ObjectUtil.isNotNull(dash.getVisitDeliveryPer())) {
                    ValueUnitDTO dto = dash.getVisitDeliveryPer();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    VisitDeliveryPer += doubleValue;
                    VisitDeliveryPerCount++;
                }
                if (ObjectUtil.isNotNull(dash.getProjEstaDueDiliPer())) {
                    ValueUnitDTO dto = dash.getProjEstaDueDiliPer();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    ProjEstaDueDiliPer += doubleValue;
                    ProjEstaDueDiliPerCount++;
                }
                if (ObjectUtil.isNotNull(dash.getDueDiligenceReviewPer())) {
                    ValueUnitDTO dto = dash.getDueDiligenceReviewPer();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    dueDiligenceReviewPer += doubleValue;
                    dueDiligenceReviewPerCount++;
                }
                if (ObjectUtil.isNotNull(dash.getDueDiliDeliveryPer())) {
                    ValueUnitDTO dto = dash.getDueDiliDeliveryPer();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    DueDiliDeliveryPer += doubleValue;
                    DueDiliDeliveryPerCount++;
                }
                if (ObjectUtil.isNotNull(dash.getReviewDeliveryPer())) {
                    ValueUnitDTO dto = dash.getReviewDeliveryPer();
                    double doubleValue =  Double.valueOf(dto.getValue());
                    ReviewDeliveryPer += doubleValue;
                    ReviewDeliveryPerCount++;
                }
            }
        }
        percentageRSP.setTermName(key);
        if (VisitProjEstaPer > 0) {
            BigDecimal days = BigDecimal.valueOf(VisitProjEstaPer).divide(BigDecimal.valueOf(VisitProjEstaPerCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setVisitProjEstaPer(new ValueUnitDTO(days.toString(),"%"));
        } else {
            percentageRSP.setVisitProjEstaPer(new ValueUnitDTO("0","%"));
        }

        if (VisitDeliveryPer > 0) {
            BigDecimal days = BigDecimal.valueOf(VisitDeliveryPer).divide(BigDecimal.valueOf(VisitDeliveryPerCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setVisitDeliveryPer(new ValueUnitDTO(days.toString(),"%"));
        } else {
            percentageRSP.setVisitDeliveryPer(new ValueUnitDTO("0","%"));
        }

        if (ProjEstaDueDiliPer > 0) {
            BigDecimal days = BigDecimal.valueOf(ProjEstaDueDiliPer).divide(BigDecimal.valueOf(ProjEstaDueDiliPerCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setProjEstaDueDiliPer(new ValueUnitDTO(days.toString(),"%"));
        } else {
            percentageRSP.setProjEstaDueDiliPer(new ValueUnitDTO("0","%"));
        }

        if (dueDiligenceReviewPer > 0) {
            BigDecimal days = BigDecimal.valueOf(dueDiligenceReviewPer).divide(BigDecimal.valueOf(dueDiligenceReviewPerCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setDueDiligenceReviewPer(new ValueUnitDTO(days.toString(),"%"));
        } else {
            percentageRSP.setDueDiligenceReviewPer(new ValueUnitDTO("0","%"));
        }

        if (DueDiliDeliveryPer > 0) {
            BigDecimal days = BigDecimal.valueOf(DueDiliDeliveryPer).divide(BigDecimal.valueOf(DueDiliDeliveryPerCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setDueDiliDeliveryPer(new ValueUnitDTO(days.toString(),"%"));
        } else {
            percentageRSP.setDueDiliDeliveryPer(new ValueUnitDTO("0","%"));
        }

        if (ReviewDeliveryPer > 0) {
            BigDecimal days = BigDecimal.valueOf(ReviewDeliveryPer).divide(BigDecimal.valueOf(ReviewDeliveryPerCount), 2, RoundingMode.HALF_UP);
            percentageRSP.setReviewDeliveryPer(new ValueUnitDTO(days.toString(),"%"));
        } else {
            percentageRSP.setReviewDeliveryPer(new ValueUnitDTO("0","%"));
        }
        return percentageRSP;
    }


    private class DashboardOperationConversionCallerTask implements Callable<Map<String, List<DashboardOperationConversionStatisticsRSP>>> {
        private DashboardOperationConversionStatisticsREQ req;
        private String type;
        public DashboardOperationConversionCallerTask(DashboardOperationConversionStatisticsREQ req, String type) {
            this.req = req;
            this.type = type;
        }
        @Override
        public Map<String, List<DashboardOperationConversionStatisticsRSP>> call() throws Exception {
            Map<String, List<DashboardOperationConversionStatisticsRSP>> map = new HashMap<>();
            List<DashboardOperationConversionStatisticsRSP> res = timeList(req);
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

    private List<ProcessResp> filterProcessResp(Page<ProcessResp> processRespPage, DashboardOperationConversionStatisticsREQ req) {
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
