package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.dto.dashboard.operation.*;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.dashboard.BusinessGroupEnum;
import cn.zswltech.mithras.service.enums.dashboard.DashboardAdjustPositionEnum;
import cn.zswltech.mithras.service.enums.dashboard.DashboardExcelFileTypeEnum;
import cn.zswltech.mithras.service.enums.groupcreditreview.GroupCreditReviewMaterialsEnum;
import cn.zswltech.mithras.service.enums.kpi.BelongTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.BusinessTypeEnum;
import cn.zswltech.mithras.service.excel.importer.DashboardAdjustPersonImporter;
import cn.zswltech.mithras.service.excel.importer.DashboardDueDiligenceImporter;
import cn.zswltech.mithras.service.excel.importer.DashboardReviewImporter;
import cn.zswltech.mithras.service.excel.importer.DashboardVisitImporter;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardAdjustPersonModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardDueDiligenceModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardReviewModel;
import cn.zswltech.mithras.service.excel.model.dashboard.DashboardVisitModel;
import cn.zswltech.mithras.service.mapper.dashboard.*;
import cn.zswltech.mithras.service.mapper.kpi.PerformanceRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.dashboard.*;
import cn.zswltech.mithras.service.mapper.model.kpi.dto.PerformanceTargetQuery;
import cn.zswltech.mithras.service.mapper.model.kpi.dto.PerformanceTargetResult;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.DashboardOperationUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DashboardOperationPayService {

    @Resource
    private DashboardOperationPayMapper dashboardOperationPayMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private PerformanceRecordInfoMapper performanceRecordInfoMapper;

    public List<DashboardOperationPayListRSP> payList(DashboardOperationPayListREQ req) {
        List<DashboardOperationPayListRSP> rspList = new ArrayList<>();
        DashboardOperationPayQuery query = buildQuery(req);
        PerformanceTargetQuery performanceQuery = buildPerformanceQuery(req);
        if(CollectionUtils.isNotEmpty(req.getBizDeptIdList())) {
            query.setBizDeptIdList(req.getBizDeptIdList());
            performanceQuery.setBizDeptIdList(req.getBizDeptIdList());
        }
        List<DashboardOperationPayResult> resultList = Optional.ofNullable(dashboardOperationPayMapper.payList(query)).orElse(new ArrayList<>());
        List<PerformanceTargetResult> performanceResultList = Optional.ofNullable(performanceRecordInfoMapper.queryTargetAmount(performanceQuery)).orElse(new ArrayList<>());
        Map<Long, DashboardOperationPayResult> resultMap = resultList.stream().collect(Collectors.toMap(DashboardOperationPayResult::getBizDeptId, Function.identity()));
        Map<Long, PerformanceTargetResult> performanceMap = performanceResultList.stream().collect(Collectors.toMap(PerformanceTargetResult::getBizDeptId, Function.identity()));
        for (OrgDO orgDO : sysUserService.listBizDeptSort(req.getType())) {
            if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
            if(CollectionUtils.isNotEmpty(req.getBizDeptIdList()) && !req.getBizDeptIdList().contains(orgDO.getId())){
                continue;
            }
            DashboardOperationPayListRSP rsp = new DashboardOperationPayListRSP();
            rsp.setBizDeptId(orgDO.getId());
            rsp.setBizDeptName(orgDO.getName());
            DashboardOperationPayResult result = resultMap.get(orgDO.getId());
            PerformanceTargetResult performanceTargetResult = performanceMap.get(orgDO.getId());
            if(result != null) {
                rsp.setPayAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(result.getActualPayAmountLong()), "万元"));
            }
            if (performanceTargetResult != null) {
                rsp.setPayPlanAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(performanceTargetResult.getTargetAmount()), "万元"));
            }
            if(result != null && performanceTargetResult != null){
                Long difference = performanceTargetResult.getTargetAmount() - result.getActualPayAmountLong();
                rsp.setDifference(new ValueUnitDTO(Util.toWanYuanWithoutSplit(difference),"万元"));
                if (Objects.nonNull(performanceTargetResult.getTargetAmount()) && performanceTargetResult.getTargetAmount() != 0) {
                    BigDecimal rateYear = BigDecimal.valueOf(result.getActualPayAmountLong()).divide(BigDecimal.valueOf(performanceTargetResult.getTargetAmount()), 2, RoundingMode.HALF_UP);
                    rsp.setFinishRate(new ValueUnitDTO(rateYear.multiply(new BigDecimal("100")).toPlainString(), "%"));
                }
            }
            rspList.add(rsp);
        }
        return rspList;
    }

    public List<DashboardOperationPayStatisticsRSP> payStatistics(DashboardOperationPayStatisticsREQ req) {
        List<DashboardOperationPayStatisticsRSP> rspList = new ArrayList<>();
        DashboardOperationPayQuery query = buildQuery(req);
        PerformanceTargetQuery performanceQuery = buildPerformanceQuery(req);
        // 当期
        List<DashboardOperationPayResult> resultList = dashboardOperationPayMapper.payList(query);
        List<PerformanceTargetResult> performanceResultList = Optional.ofNullable(performanceRecordInfoMapper.queryTargetAmount(performanceQuery)).orElse(new ArrayList<>());
        Map<Long, DashboardOperationPayResult> resultMap = resultList.stream().collect(Collectors.toMap(DashboardOperationPayResult::getBizDeptId, Function.identity()));
        Map<Long, PerformanceTargetResult> performanceMap = performanceResultList.stream().collect(Collectors.toMap(PerformanceTargetResult::getBizDeptId, Function.identity()));
        // 本年
        LocalDate now = LocalDate.now();
        query.setQueryDateFrom(now.with(TemporalAdjusters.firstDayOfYear()));
        query.setQueryDateTo(now.with(TemporalAdjusters.lastDayOfYear()));
        performanceQuery.buildDateMap(query.getQueryDateFrom(), query.getQueryDateTo());

        List<DashboardOperationPayResult> resultYearList = dashboardOperationPayMapper.payList(query);
        List<PerformanceTargetResult> performanceResultYearList = Optional.ofNullable(performanceRecordInfoMapper.queryTargetAmount(performanceQuery)).orElse(new ArrayList<>());
        Map<Long, DashboardOperationPayResult> resultYearMap = resultYearList.stream().collect(Collectors.toMap(DashboardOperationPayResult::getBizDeptId, Function.identity()));
        Map<Long, PerformanceTargetResult> performanceYearMap = performanceResultYearList.stream().collect(Collectors.toMap(PerformanceTargetResult::getBizDeptId, Function.identity()));

        for (OrgDO orgDO : sysUserService.listBizDeptSort(req.getType())) {
            if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                continue;
            }
//            if (OrgConstants.DISCARD_ORG.contains(orgDO.getCode())) {
//                continue;
//            }
            DashboardOperationPayStatisticsRSP rsp = new DashboardOperationPayStatisticsRSP();
            rsp.setBizDeptId(orgDO.getId());
            rsp.setBizDeptName(orgDO.getName());
            DashboardOperationPayResult result = resultMap.get(orgDO.getId());
            DashboardOperationPayResult resultYear = resultYearMap.get(orgDO.getId());
            PerformanceTargetResult performance = performanceMap.get(orgDO.getId());
            PerformanceTargetResult performanceYear = performanceYearMap.get(orgDO.getId());
            if(resultYear != null && performanceYear != null && performanceYear.getTargetAmount() != 0L){
                BigDecimal rateYear = BigDecimal.valueOf(resultYear.getActualPayAmountLong()).divide(BigDecimal.valueOf(performanceYear.getTargetAmount()), 2, RoundingMode.HALF_UP);
                rsp.setYearPayFinishRate(new ValueUnitDTO(rateYear.multiply(new BigDecimal("100")).toPlainString(),"%"));
            }
            if (performance != null && performance.getTargetAmount() != null && performance.getTargetAmount() != 0L) {
                rsp.setPayPlanAmount(new ValueUnitDTO(Util.toYiYuanWithoutSplit(performance.getTargetAmount()), "亿元"));
            }
            if(result != null) {
                if (performance != null && performance.getTargetAmount() != null && performance.getTargetAmount() != 0L) {
                    BigDecimal rateYear = BigDecimal.valueOf(result.getActualPayAmountLong()).divide(BigDecimal.valueOf(performance.getTargetAmount()), 2, RoundingMode.HALF_UP);
                    rsp.setCurrentPayFinishRate(new ValueUnitDTO(rateYear.multiply(new BigDecimal("100")).toPlainString(), "%"));
                }
                rsp.setPayAmount(new ValueUnitDTO(Util.toYiYuanWithoutSplit(result.getActualPayAmountLong()), "亿元"));
            }
            rspList.add(rsp);
        }
        return rspList;
    }

    private DashboardOperationPayQuery buildQuery(DashboardOperationBaseREQ req){
        DashboardOperationPayQuery query = BeanUtil.copyProperties(req, DashboardOperationPayQuery.class);
        if(req.getType() != null) {
            query.setRiskControlList(DashboardOperationUtil.getRiskControlIndustryClassify(req));
            if (DashboardOperationBaseREQ.publicType.equals(req.getType())) {
                List<OrgDO> orgDOList = sysUserService.listBizDeptSort(req.getType());
                List<Long> orgIdList = orgDOList.stream()
                        .filter(e -> Objects.equals(e.getState(), YesOrNoNumberEnum.YES.getCode()))
                        .filter(f -> Arrays.asList("浙江业务部", "公用事业业务部").contains(f.getName()))
                        .map(OrgDO::getId).collect(Collectors.toList());
                query.setBizDeptIdList((orgIdList));
            }
        }
        return query;
    }

    private PerformanceTargetQuery buildPerformanceQuery(DashboardOperationBaseREQ req){
        PerformanceTargetQuery query = new PerformanceTargetQuery();
        query.setBelongType(BelongTypeEnum.DEPARTMENT.name());
        query.buildDateMap(req.getQueryDateFrom(), req.getQueryDateTo());
        if(req.getType() != null) {
            if (DashboardOperationBaseREQ.publicType.equals(req.getType())) {
                List<OrgDO> orgDOList = sysUserService.listBizDeptSort(req.getType());
                List<Long> orgIdList = orgDOList.stream().filter(f -> Arrays.asList("浙江业务部", "公用事业业务部").contains(f.getName()))
                        .map(OrgDO::getId).collect(Collectors.toList());
                query.setBizDeptIdList((orgIdList));
                query.setBusinessType(BusinessTypeEnum.PLATFORM.name());
            }else {
                query.setBusinessType(BusinessTypeEnum.INDUSTRY.name());
            }
        }else{
            query.setBusinessType(BusinessTypeEnum.DEPT_TOTAL.name());
        }
        return query;

    }

//    private void buildYearAndMonth(DashboardOperationPayQuery query) {
//        LocalDate queryDateFrom = query.getQueryDateFrom();
//        LocalDate queryDateTo = query.getQueryDateTo();
//        Map<Integer, List<Integer>> yearMonthMap = new LinkedHashMap<>();
//        long monthsBetween = ChronoUnit.MONTHS.between(queryDateFrom, queryDateTo) + 1;
//        for (long i = 0; i < monthsBetween; i++) {
//            LocalDate date = queryDateFrom.plusMonths(i);
//            int year = date.getYear();
//            int month = date.getMonthValue();
//            yearMonthMap.computeIfAbsent(year, k -> new ArrayList<>()).add(month);
//        }
//        query.setMap(yearMonthMap);
//    }

    @Resource
    private DashboardVisitImporter visitImporter;
    @Resource
    private DashboardDueDiligenceImporter dueDiligenceImporter;
    @Resource
    private DashboardReviewImporter reviewImporter;
    @Resource
    private DashboardAdjustPersonImporter adjustPersonImporter;
    @Resource
    private DashboardAdjustPersonInfoMapper adjustPersonInfoMapper;
    @Resource
    private DashboardVisitInfoMapper visitInfoMapper;
    @Resource
    private DashboardReviewInfoMapper reviewInfoMapper;
    @Resource
    private DashboardDueDiligenceInfoMapper dueDiligenceInfoMapper;
    @Resource
    private OrgService orgService;
    @Resource
    private UserService userService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
//
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(MultipartFile file, String fileType){
        DashboardExcelFileTypeEnum dashboardExcelFileTypeEnum = DashboardExcelFileTypeEnum.find(fileType);
        if(dashboardExcelFileTypeEnum == null){
            throw new MithrasException("该文件所属类型未注册");
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> notExistUser = new ArrayList<>();
        switch (dashboardExcelFileTypeEnum){
            case ADJUST_PERSON:
                List<DashboardAdjustPersonModel> adjustPersonModelList = adjustPersonImporter.parse(inputStream);
                if(CollectionUtils.isNotEmpty(adjustPersonModelList)) {
                    Map<String, Long> orgNameMap = getOrgIdMap(adjustPersonModelList.stream().map(DashboardAdjustPersonModel::getDeptName).filter(Objects::nonNull).collect(Collectors.toSet()));
                    Map<String, Long> userNameMap = getUserIdMap(adjustPersonModelList.stream().map(DashboardAdjustPersonModel::getProjManagerName).filter(Objects::nonNull).collect(Collectors.toSet()));
                    String batchNumber = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
                    // 业务组分类不属于产业或公用无需入库
                    List<DashboardAdjustPersonInfo> infoList = adjustPersonModelList.stream().map(adjustPerson -> {
                        DashboardAdjustPersonInfo info = BeanUtil.copyProperties(adjustPerson, DashboardAdjustPersonInfo.class);
                        info.setBusinessGroup(BusinessGroupEnum.getNameByDisplay(adjustPerson.getBusinessGroup()));
                        info.setDeptId(orgNameMap.get(adjustPerson.getDeptName()));
                        info.setProjManagerId(userNameMap.get(adjustPerson.getProjManagerName()));
                        DashboardAdjustPositionEnum positionEnum = DashboardAdjustPositionEnum.findByDisplay(adjustPerson.getPosition());
                        info.setPosition(positionEnum != null ? positionEnum.name() : adjustPerson.getPosition());
                        info.setBatchNumber(Long.valueOf(batchNumber));
                        return info;
                    }).filter(f -> f.getBusinessGroup() != null).collect(Collectors.toList());
                    for (DashboardAdjustPersonInfo adjustPersonInfo : infoList) {
                        if (adjustPersonInfo.getProjManagerId() == null) {
                            notExistUser.add(adjustPersonInfo.getProjManagerName());
                        }
                        if(adjustPersonInfo.getDeptId() == null){
                            throw new MithrasException("部门不存在: " + adjustPersonInfo.getDeptName());
                        }
                        // 数据量小，可以循环内插入
                        adjustPersonInfoMapper.insert(adjustPersonInfo);
                    }
                }
                break;
            case DUE_DILIGENCE:
                List<DashboardDueDiligenceModel> dueDiligenceModelList = dueDiligenceImporter.parse(inputStream);
                if(CollectionUtils.isNotEmpty(dueDiligenceModelList)) {
                    List<String> projCodeList = dueDiligenceModelList.stream().map(DashboardDueDiligenceModel::getProjCode).collect(Collectors.toList());
                    List<ProjReviewBaseInfo> list = reviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getProjCode, projCodeList).orderByDesc(ProjReviewBaseInfo::getCreateTime));
                    Map<String, Long> reviewMap = list.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getProjCode, ProjReviewBaseInfo::getId, (m1,m2) -> m1));
                    List<MaterialsList> materialsListList = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery().in(MaterialsList::getBelongId, reviewMap.values())
                            .eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW.name())
                            .eq(MaterialsList::getMaterialsType, GroupCreditReviewMaterialsEnum.DUE_DILIGENCE_REPORT.name())
                            .orderByDesc(MaterialsList::getCreateTime));

                    Map<Long, LocalDateTime> materialsListMap = Optional.ofNullable(materialsListList).orElse(new ArrayList<>())
                            .stream().collect(Collectors.toMap(MaterialsList::getBelongId, MaterialsList::getCreateTime ,(m1,m2) -> m1));
                    Set<String> deptNameList = dueDiligenceModelList.stream().map(DashboardDueDiligenceModel::getDeptName).filter(Objects::nonNull).collect(Collectors.toSet());
                    Set<String> userNameList = dueDiligenceModelList.stream().map(DashboardDueDiligenceModel::getProjManagerName).filter(Objects::nonNull).collect(Collectors.toSet());
                    Set<String> riskManagerNameList = dueDiligenceModelList.stream().map(DashboardDueDiligenceModel::getRiskManagerName).filter(Objects::nonNull).collect(Collectors.toSet());
                    userNameList.addAll(riskManagerNameList);
                    Map<String, Long> orgNameMap = getOrgIdMap(deptNameList);
                    Map<String, Long> userNameMap = getUserIdMap(userNameList);
//                    Map<Long, String> businessGroup = getBusinessGroup(userNameMap.values());
                    List<DashboardDueDiligenceInfo> infoList = dueDiligenceModelList.stream().map(due -> {
                        DashboardDueDiligenceInfo info = BeanUtil.copyProperties(due, DashboardDueDiligenceInfo.class);
                        info.setBusinessGroup(BusinessGroupEnum.getNameByDisplay(due.getBusinessGroup()));
                        info.setDeptId(orgNameMap.get(due.getDeptName()));
                        info.setProjManagerId(userNameMap.get(due.getProjManagerName()));
                        info.setRiskManagerId(userNameMap.get(due.getRiskManagerName()));
                        Long reviewId = reviewMap.get(due.getProjCode());
                        info.setProjReviewId(reviewId);
                        LocalDateTime localDateTime = materialsListMap.get(reviewId);
                        info.setDueDiligenceReportTime(localDateTime);
                        if(due.getAmount() != null) {
                            info.setAmount(BigDecimal.valueOf(due.getAmount()).multiply(new BigDecimal("100000000")).longValue());
                        }
                        return info;
                    }).filter(f -> f.getBusinessGroup() != null).collect(Collectors.toList());
                    for (DashboardDueDiligenceInfo dueDiligenceInfo : infoList) {
                        if(dueDiligenceInfo.getDeptId() == null){
                            throw new MithrasException("部门不存在: " + dueDiligenceInfo.getDeptName());
                        }
                        dueDiligenceInfoMapper.insert(dueDiligenceInfo);
                    }
                }
                    break;
            case REVIEW:
                List<DashboardReviewModel> reviewModelList = reviewImporter.parse(inputStream);
                if(CollectionUtils.isNotEmpty(reviewModelList)){
                    Map<String, Long> orgNameMap = getOrgIdMap(reviewModelList.stream().map(DashboardReviewModel::getDeptName).filter(Objects::nonNull).collect(Collectors.toSet()));
                    List<DashboardReviewInfo> infoList = reviewModelList.stream().map(review -> {
                        DashboardReviewInfo info = BeanUtil.copyProperties(review, DashboardReviewInfo.class);
                        info.setBusinessGroup(BusinessGroupEnum.getNameByDisplay(review.getBusinessGroup()));
                        info.setDeptId(orgNameMap.get(review.getDeptName()));
                        if(review.getProjAmount() != null) {
                            info.setProjAmount(BigDecimal.valueOf(review.getProjAmount()).multiply(new BigDecimal("100000000")).longValue());
                        }
                        if(review.getApprovalAmount() != null) {
                            info.setApprovalAmount(BigDecimal.valueOf(review.getApprovalAmount()).multiply(new BigDecimal("100000000")).longValue());
                        }
                        return info;
                    }).filter(f -> f.getBusinessGroup() != null).collect(Collectors.toList());
                    for (DashboardReviewInfo reviewInfo : infoList) {
                        if(reviewInfo.getDeptId() == null){
                            throw new MithrasException("部门不存在: " + reviewInfo.getDeptName());
                        }
                        reviewInfoMapper.insert(reviewInfo);
                    }
                }
                break;
            case VISIT:
                List<DashboardVisitModel> visitModelList = visitImporter.parse(inputStream);
                if(CollectionUtils.isNotEmpty(visitModelList)){
                    Map<String, Long> orgNameMap = getOrgIdMap(visitModelList.stream().map(DashboardVisitModel::getDeptName).filter(Objects::nonNull).collect(Collectors.toSet()));
                    Map<String, Long> userNameMap = getUserIdMap(visitModelList.stream().map(DashboardVisitModel::getProjManagerName).filter(Objects::nonNull).collect(Collectors.toSet()));
//                    Map<Long, String> businessGroup = getBusinessGroup(userNameMap.values());
                    List<DashboardVisitInfo> infoList = visitModelList.stream().map(visit -> {
                        DashboardVisitInfo info = BeanUtil.copyProperties(visit, DashboardVisitInfo.class);
                        info.setBusinessGroup(BusinessGroupEnum.getNameByDisplay(visit.getBusinessGroup()));
                        info.setDeptId(orgNameMap.get(visit.getDeptName()));
                        info.setProjManagerId(userNameMap.get(visit.getProjManagerName()));
                        return info;
                    }).filter(f -> f.getBusinessGroup() != null).collect(Collectors.toList());
                    for (DashboardVisitInfo visitInfo : infoList) {
                        if (visitInfo.getProjManagerId() == null) {
                            notExistUser.add(visitInfo.getProjManagerName());
                        }
                        if(visitInfo.getDeptId() != null){
                            visitInfoMapper.insert(visitInfo);
                        }
                    }
                }
                break;
        }
        if(CollectionUtils.isNotEmpty(notExistUser)){
            throw new MithrasException("用户不存在: " + notExistUser);
        }
    }



    private Map<String,Long> getOrgIdMap(Collection<String> deptNameList){
        if(CollectionUtils.isNotEmpty(deptNameList)) {
            Example exampleOrg = new Example(OrgDO.class);
            exampleOrg.createCriteria().andIn("name", deptNameList);
            List<OrgDO> orgDOList = orgService.selectByExample(exampleOrg);
            if(CollectionUtils.isNotEmpty(orgDOList)) {
                return orgDOList.stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId));
            }
        }
        return new HashMap<>();
    }


    private Map<String,Long> getUserIdMap(Collection<String> userNameList){
        if(CollectionUtils.isNotEmpty(userNameList)) {
            Example exampleUser = new Example(UserDO.class);
            exampleUser.createCriteria().andIn("userName", userNameList);
            List<UserDO> userDOList = userService.selectByExample(exampleUser);
            if (CollectionUtils.isNotEmpty(userDOList)) {
                return userDOList.stream().collect(Collectors.toMap(UserDO::getUserName, UserDO::getId));
            }
        }
        return new HashMap<>();
    }

    private Map<Long,String> getBusinessGroup(Collection<Long> projManagerIdList){
        List<DashboardAdjustPersonInfo> infoList = adjustPersonInfoMapper.selectList(Wrappers.<DashboardAdjustPersonInfo>lambdaQuery()
                .in(DashboardAdjustPersonInfo::getProjManagerId, projManagerIdList));
        return infoList.stream().filter(f -> f.getBusinessGroup() != null).collect(Collectors.toMap(DashboardAdjustPersonInfo::getProjManagerId,DashboardAdjustPersonInfo::getBusinessGroup));
    }

}
