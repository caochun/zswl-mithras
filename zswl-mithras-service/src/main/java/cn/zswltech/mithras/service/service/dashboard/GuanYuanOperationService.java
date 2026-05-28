package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowModelApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.dao.OperateRecordMapper;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.NodeDefineResp;
import cn.zswltech.flow.core.domain.resp.ProcessHistoryResp;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.dashboard.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.service.enums.dashboard.BusinessGroupEnum;
import cn.zswltech.mithras.service.enums.projestablish.ContractBusinessModelEnum;
import cn.zswltech.mithras.service.enums.projestablish.FactoringType;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.projestablish.ZrType;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.dashboard.boss.GuanYuanBasicService;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.BusinessContractSummaryDTO;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.PayIncomeDTO;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.ProjectSituationDTO;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss.OperationYYContractApprovalArrive;
import cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss.OperationYYContractApprovalDTO;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.sleipnir.toolkit.enums.GuanYuanFilterTypeEnum;
import cn.zswltech.sleipnir.toolkit.request.guanyuan.GuanYuanDSRequest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName DashboardOperationService
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/15 5:05 下午
 * @Version 1.0
 **/
@Service
@Slf4j
public class GuanYuanOperationService extends GuanYuanBasicService {

    @Resource
    private FlowTaskApiService taskApiService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FlowModelApiService flowModelApiService;
    @Resource
    private OperateRecordMapper operateRecordMapper;
    @Resource
    private HistoryService historyService;
    @Value("${spring.profiles.active}")
    private String environment;

    private static final String YUN_YING_GUANG_LI_JING_BAN = "userTask_yunYingGuanLi";
    private static final String YUN_YING_GUANG_LI_FU_HE = "userTask_yunYingGuanLiReview";
    private static final String YUN_YING_GUANG_FU_ZHE_REN = "Activity_0ikb27z";
    private static final String YUN_YING_GUANG_FU_ZHE_REN_2 = "Activity_1mof9ra";

    public List<PayIncomeDTO> listPayIncome(LocalDate queryFrom, LocalDate queryTo) {
        GuanYuanDSRequest.Body body = new GuanYuanDSRequest.Body();
//        GuanYuanDSRequest.Filter filter1 = new GuanYuanDSRequest.Filter();
//        filter1.setName("放款时间");
//        filter1.setFilterType(GuanYuanFilterTypeEnum.GE.name());
//        filter1.setFilterValue(Collections.singletonList(LocalDateTimeUtil.format(queryFrom.atStartOfDay(), DatePattern.NORM_DATETIME_PATTERN)));
//        GuanYuanDSRequest.Filter filter2 = new GuanYuanDSRequest.Filter();
//        filter2.setName("放款时间");
//        filter2.setFilterType(GuanYuanFilterTypeEnum.LE.name());
//        filter2.setFilterValue(Collections.singletonList(LocalDateTimeUtil.format(queryTo.atStartOfDay(), DatePattern.NORM_DATETIME_PATTERN)));
//        body.setFilters(ListUtil.toList(filter1, filter2));
        List<PayIncomeDTO> result = queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.PayIncomeETLResult, body, PayIncomeDTO.class);
        // 过滤数据（指定filter的方式一直报错，改为本地内存过滤吧，需要问一下guanyuan的技术支持）
        if (CollectionUtil.isEmpty(result)) {
            return result;
        }
        return result.stream().filter(e -> Objects.nonNull(e.getPayDate()) && !e.getPayDate().isBefore(queryFrom) && !e.getPayDate().isAfter(queryTo)).collect(Collectors.toList());
    }

    public List<ProjectSituationDTO> listProjectSituation(LocalDate targetDate) {
        GuanYuanDSRequest.Body body = new GuanYuanDSRequest.Body();
        GuanYuanDSRequest.Filter filter1 = new GuanYuanDSRequest.Filter();
        filter1.setName("报表日期");
        filter1.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
        filter1.setFilterValue(Collections.singletonList(LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN)));
        GuanYuanDSRequest.Filter filter2 = new GuanYuanDSRequest.Filter();
//        filter2.setName("是否关联方交易");
//        filter2.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
//        filter2.setFilterValue(Collections.singletonList(YesOrNoNumberEnum.YES.getCode().toString()));
        body.setFilters(ListUtil.toList(filter1, filter2));
        return queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.ProjectSituation, body, ProjectSituationDTO.class);
    }

    public List<BusinessContractSummaryDTO> listBusinessContractSummary(LocalDate targetDate) {
        GuanYuanDSRequest.Body body = new GuanYuanDSRequest.Body();
        GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
        filter.setName("截止日期");
        filter.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
        filter.setFilterValue(Collections.singletonList(LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN)));
        body.setFilters(Collections.singletonList(filter));
        return queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.BusinessContractSummaryETLResult, body, BusinessContractSummaryDTO.class);
    }

    public List<DashboardApprovalListRSP> approvalListGuanYuan(DashboardApprovalListREQ req){
        GuanYuanDSRequest.Body body = new GuanYuanDSRequest.Body();
        StopWatch st = new StopWatch("运营审批统计");
        st.start();
        List<OperationYYContractApprovalDTO> dtoList = queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationYYContractApproval, body, OperationYYContractApprovalDTO.class);
        st.stop();
        log.info("调用观远合同详情接口耗时 {}ms", st.getLastTaskTimeMillis());

        List<OperationYYContractApprovalDTO> newDtoList = filterCondition(dtoList,req);

        st.start();
        List<OperationYYContractApprovalArrive> arriveList = queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.OperationYYContractApprovalArrive, body, OperationYYContractApprovalArrive.class);
        st.stop();
        log.info("调用观远合同各流程节点到达时间接口耗时 {}ms", st.getLastTaskTimeMillis());
        Map<Long, OperationYYContractApprovalArrive> arriveTimeMap = buildArriveTimeMap(arriveList);

        List<DashboardApprovalListRSP> rspList = newDtoList.stream().map(item -> {
//            LocalDateTime applyTime = item.getApplyTime();
//            // 累加运营经办前节点的耗时
//            BigDecimal operationJbAfter = Optional.ofNullable(item.getStartUser()).orElse(BigDecimal.ZERO)
//                    .add(Optional.ofNullable(item.getStartUserConfirm()).orElse(BigDecimal.ZERO))
//                    .add(Optional.ofNullable(item.getBizDeptMaster()).orElse(BigDecimal.ZERO))
//                    .add(Optional.ofNullable(item.getBizDivisionLeader()).orElse(BigDecimal.ZERO))
//                    .add(Optional.ofNullable(item.getFinancialOfficer()).orElse(BigDecimal.ZERO));
//            // 运营复核前节点的耗时
//            BigDecimal operationFhAfter = operationJbAfter.add(Optional.ofNullable(item.getLawManager()).orElse(BigDecimal.ZERO))
//                    .add(Optional.ofNullable(item.getLawManagerConfirm()).orElse(BigDecimal.ZERO))
//                    .add(Optional.ofNullable(item.getYunYingGuanLiJb()).orElse(BigDecimal.ZERO));

            OperationYYContractApprovalArrive dto2 = arriveTimeMap.get(item.getContractId());
            if (item.getYunYingGuanLiJb() != null) {
                item.setYunYingJbStartTime(dto2.getYunYingJbStartTime());
                item.setYunYingJbEndTime(dto2.getYunYingJbEndTime());
            }
            if (item.getYunYingGuanLiFh() != null) {
                item.setYunYingFhStartTime(dto2.getYunYingFhStartTime());
                item.setYunYingFhEndTime(dto2.getYunYingFhEndTime());
            }
            BigDecimal yunYingTime = Optional.ofNullable(item.getYunYingGuanLiJb()).orElse(BigDecimal.ZERO)
                    .add(Optional.ofNullable(item.getYunYingGuanLiFh()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(item.getYunYingPrincipal()).orElse(BigDecimal.ZERO));
            item.setYunYingTime(yunYingTime.equals(BigDecimal.ZERO) ? null : yunYingTime);

            return buildRsp(item);
        }).collect(Collectors.toList());

        return rspList;

    }

    private Map<Long, OperationYYContractApprovalArrive> buildArriveTimeMap(List<OperationYYContractApprovalArrive> arriveList) {
        Map<Long, OperationYYContractApprovalArrive> resultMap = new HashMap<>();
        // 运营经办
        Map<Long, OperationYYContractApprovalArrive> jbResultMap = new HashMap<>();
        // 运营复核
        Map<Long, OperationYYContractApprovalArrive> fhResultMap = new HashMap<>();

        // 处理节点到达时间
        for (OperationYYContractApprovalArrive approvalDto : arriveList) {
            if(approvalDto.getYunYingJbStartTime() != null){
                OperationYYContractApprovalArrive originDto = jbResultMap.get(approvalDto.getContractId());
                if(originDto != null){
                    if(originDto.getYunYingJbStartTime().isAfter(approvalDto.getYunYingJbStartTime())){
                        continue;
                    }
                    // 发起时间保持第一次发起的时间
                    approvalDto.setYunYingJbStartTime(originDto.getYunYingJbStartTime());
                }
                jbResultMap.put(approvalDto.getContractId(), approvalDto);
            }else if(approvalDto.getYunYingFhStartTime() != null){
                OperationYYContractApprovalArrive originDto = fhResultMap.get(approvalDto.getContractId());
                if(originDto != null) {
                    if (originDto.getYunYingFhStartTime().isAfter(approvalDto.getYunYingFhStartTime())) {
                        continue;
                    }
                    // 发起时间保持第一次发起的时间
                    approvalDto.setYunYingFhStartTime(originDto.getYunYingFhStartTime());
                }
                fhResultMap.put(approvalDto.getContractId(), approvalDto);
            }
        }
        // 以合同维度合并经办和复核

        for (Long contractId : Stream.of(jbResultMap.keySet(), fhResultMap.keySet()).flatMap(Collection::stream).collect(Collectors.toSet())) {
            OperationYYContractApprovalArrive jbResult = jbResultMap.get(contractId);
            OperationYYContractApprovalArrive fhResult = fhResultMap.get(contractId);
            OperationYYContractApprovalArrive result = new OperationYYContractApprovalArrive();
            result.setContractId(contractId);
            if(jbResult != null){
                result.setYunYingJbStartTime(jbResult.getYunYingJbStartTime());
                result.setYunYingJbEndTime(jbResult.getYunYingJbEndTime());
                result.setProcessId(jbResult.getProcessId());
            }
            if(fhResult != null){
                result.setYunYingFhStartTime(fhResult.getYunYingFhStartTime());
                result.setYunYingFhEndTime(fhResult.getYunYingFhEndTime());
                result.setProcessId(fhResult.getProcessId());
            }
            resultMap.put(contractId, result);
        }
        return resultMap;

    }

    private DashboardApprovalListRSP buildRsp(OperationYYContractApprovalDTO dto) {
        DashboardApprovalListRSP rsp = new DashboardApprovalListRSP();
        rsp.setId(dto.getContractId());
        rsp.setFlowId(Optional.ofNullable(dto.getProcessId()).map(String::valueOf).orElse(null));
        rsp.setProjName(dto.getProjName());
        rsp.setProjCode(dto.getProjCode());
        rsp.setContractCode(dto.getContractCode());
        rsp.setClientId(dto.getClientId());
        rsp.setClientName(dto.getClientName());
        rsp.setProjSponsorUserId(dto.getProjSponsorUserId());
        rsp.setProjSponsorUserName(dto.getProjSponsorUserName());
        rsp.setBizDeptId(dto.getBizDeptId());
        rsp.setBizDeptName(dto.getBizDeptName());
//        rsp.setRiskControlIndustryClassify();
        rsp.setBusinessGroup(Optional.ofNullable(dto.getBusinessGroup()).map(BusinessGroupEnum::getNameByDisplay).orElse(null));
        rsp.setBusinessModel(Optional.ofNullable(FactoringType.getNameByDisplay(dto.getBusinessModel()))
                .orElse(Optional.ofNullable(LeaseType.getNameByDisplay(dto.getBusinessModel()))
                        .orElse(ZrType.getNameByDisplay(dto.getBusinessModel()))));
        rsp.setApplyTime(dto.getApplyTime());
        rsp.setEndTime(dto.getEndTime());

        rsp.setApprovalTotalTime(dto.getProcessTime());
        rsp.setOperationHandlingArrivalTime(dto.getYunYingJbStartTime());
        rsp.setOperationHandlingSubmitTime(dto.getYunYingJbEndTime());
        rsp.setOperationHandlingTotalTime(Optional.ofNullable(dto.getYunYingGuanLiJb()).map(m -> m.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP)).orElse(null));
        rsp.setOperationReviewArrivalTime(dto.getYunYingFhStartTime());
        rsp.setOperationReviewSubmitTime(dto.getYunYingFhEndTime());
        rsp.setOperationReviewTotalTime(Optional.ofNullable(dto.getYunYingGuanLiFh()).map(m -> m.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP)).orElse(null));
        rsp.setOperationTotalTime(Optional.ofNullable(dto.getYunYingTime()).map(m -> m.divide(new BigDecimal("24"), 2, RoundingMode.HALF_UP)).orElse(null));
        return rsp;
    }

//    private LocalDateTime addHours(LocalDateTime applyTime, BigDecimal hours){
//        BigDecimal integerPart = hours.setScale(0, RoundingMode.HALF_UP);
//        // 小数部分转成秒
//        BigDecimal secondPart = hours.subtract(integerPart).multiply(new BigDecimal("3600"));
//        return applyTime.plusHours(integerPart.longValue()).plusSeconds(secondPart.longValue());
//    }


    private List<OperationYYContractApprovalDTO> filterCondition(List<OperationYYContractApprovalDTO> dtoList, DashboardApprovalListREQ req) {

        Map<Long, OperationYYContractApprovalDTO> resultMap = dtoList.stream().filter(item -> {
            if (Objects.nonNull(req.getQueryDateFrom()) && Objects.nonNull(req.getQueryDateTo())) {
                if (!item.getEndTime().isAfter(req.getQueryDateFrom().atStartOfDay()) || !item.getEndTime().isBefore(req.getQueryDateTo().atStartOfDay())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getEndTimeFrom()) && Objects.nonNull(req.getEndTimeTo())) {
                if (!item.getEndTime().isAfter(req.getEndTimeFrom().atStartOfDay()) || !item.getEndTime().isBefore(req.getEndTimeTo().atStartOfDay())) {
                    return false;
                }
            }
            if (CollectionUtil.isNotEmpty(req.getBizDeptId())) {
                if (!req.getBizDeptId().contains(item.getBizDeptId())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getClientId())) {
                if (!req.getClientId().equals(item.getClientId())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getProjName())) {
                if (!req.getProjName().equals(item.getProjName())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getContractCode())) {
                if (!req.getContractCode().equals(item.getContractCode())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getProjSponsorUserId())) {
                if (!req.getProjSponsorUserId().equals(item.getProjSponsorUserId())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getBusinessGroup())) {
                String businessGroupDisplay = Optional.ofNullable(BusinessGroupEnum.of(req.getBusinessGroup())).map(BusinessGroupEnum::getDisplay).orElse(null);
                if (!Objects.equals(businessGroupDisplay, item.getBusinessGroup())) {
                    return false;
                }
            }
            if (Objects.nonNull(req.getBusinessModel())) {
                String businessModelDisplay = Optional.ofNullable(FactoringType.of(req.getBusinessModel())).map(FactoringType::getDisplay)
                        .orElse(Optional.ofNullable(LeaseType.of(req.getBusinessModel())).map(LeaseType::getDisplay)
                                .orElse(Optional.ofNullable(ZrType.of(req.getBusinessModel())).map(ZrType::getDisplay).orElse(null)));
                if (!Objects.equals(businessModelDisplay, item.getBusinessModel())) {
                    return false;
                }
            }
            if (CollectionUtil.isNotEmpty(req.getContractIds())) {
                if (!req.getContractIds().contains(item.getContractId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toMap(OperationYYContractApprovalDTO::getProcessId, Function.identity(), (m1, m2) -> m1));
        // 利用map根据流程id出重（观远返回的有重复数据）

        if(CollectionUtil.isNotEmpty(resultMap)){
            return new ArrayList<>(resultMap.values());
        }
        return Collections.emptyList();
    }

    public List<DashboardApprovalListRSP> approvalList(DashboardApprovalListREQ req) {
        StopWatch st = new StopWatch("运营审批统计");
        st.start("查询流程，合同");
        if (ObjectUtil.isEmpty(req.getEndTimeFrom())) {
            req.setEndTimeFrom(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        }
        //查询所有合同创建流程 每年大约2～3百条 数量较少，这里先查符合的流程再过滤合同

        Page<ProcessResp> processRespPage = getContractEffect(req.getEndTimeFrom());
        Map<Long, ProcessResp> contractId2ProcessMap = filterProcessResp(processRespPage, req).stream().collect(Collectors.toMap(e -> Long.valueOf(e.getBusinessKey()), e -> e, (a, b) -> a));
        if (ObjectUtil.isEmpty(contractId2ProcessMap)) {
            return Collections.emptyList();
        }
        boolean isPublic = false;
        boolean isNotPublic = false;
        if(Objects.nonNull(req.getBusinessGroup())){
            if(req.getBusinessGroup().equals(BusinessGroupEnum.PUBLIC_CATEGORY.name())) {
                isPublic = true;
            } else {
                isNotPublic = true;
            }
        }
        List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractId2ProcessMap.keySet())
                .in(ObjectUtil.isNotEmpty(req.getBizDeptId()), ContractBaseInfo::getBizDeptId, req.getBizDeptId())
                .eq(ObjectUtil.isNotEmpty(req.getClientId()), ContractBaseInfo::getClientId, req.getClientId())
                .like(ObjectUtil.isNotEmpty(req.getProjName()), ContractBaseInfo::getProjName, req.getProjName())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), ContractBaseInfo::getContractCode, req.getContractCode())
                .eq(ObjectUtil.isNotEmpty(req.getProjSponsorUserId()), ContractBaseInfo::getProjSponsorUserId, req.getProjSponsorUserId())
                .in(isPublic, ContractBaseInfo::getRiskControlIndustryClassify, BusinessGroupEnum.PUBLIC_CATEGORY.getRiskControlIndustryClassifys())
                .notIn(isNotPublic, ContractBaseInfo::getRiskControlIndustryClassify, BusinessGroupEnum.PUBLIC_CATEGORY.getRiskControlIndustryClassifys())
                .and(Objects.nonNull(req.getBusinessModel()), consumer -> {
                    consumer.or().eq(Objects.nonNull(req.getBusinessModel()), ContractBaseInfo::getLeaseType, req.getBusinessModel())
                            .or().eq(Objects.nonNull(req.getBusinessModel()), ContractBaseInfo::getFactoringType, req.getBusinessModel())
                            .or().eq(Objects.nonNull(req.getBusinessModel()), ContractBaseInfo::getZrType, req.getBusinessModel());
                })
        );
        st.stop();
        st.start("拼接统一参数");
        //拼接统一参数
        List<DashboardApprovalListRSP> dashboardApprovalBaseRSPS = buildApprovalBase(BeanUtil.copyToList(list, DashboardApprovalListRSP.class), contractId2ProcessMap);
        //特殊参数
        st.stop();
        st.start("拼接节点");
        Map<String, List<ProcessHistoryResp>> processHistoryMap = processHistoryList(processRespPage.getContents().stream().map(ProcessResp::getProcessInstanceId).collect(Collectors.toSet()));
        if (ObjectUtil.isEmpty(processHistoryMap)) {
            return ListUtil.empty();
        }
        //Map<Long, String> userId2Name = id2NameService.sysUserId2Name(processHistoryMap.values().stream().flatMap(Collection::stream).map(e -> Long.valueOf(e.getOperatorId())).collect(Collectors.toSet()));
        dashboardApprovalBaseRSPS.forEach(dashboard -> {
            //查询节点信息
            ProcessResp processResp = contractId2ProcessMap.get(dashboard.getId());
            if (ObjectUtil.isNotEmpty(processResp)) {
                dashboard.setApplyTime(processResp.getStartTime() == null ? null : processResp.getStartTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                dashboard.setEndTime(processResp.getEndTime() == null ? null : processResp.getEndTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                Long workMinutes = DateUtil.countWorkdayHouse(dashboard.getApplyTime(), dashboard.getEndTime());
                if (workMinutes != null && workMinutes > 0) {
                    dashboard.setApprovalTotalTime(BigDecimal.valueOf(workMinutes).divide(BigDecimal.valueOf(24*60L), 2, RoundingMode.HALF_UP));
                }
                //运营经办到达时间
                List<ProcessHistoryResp> historys = processHistoryMap.get(processResp.getProcessInstanceId());
                LocalDateTime arriveDate = null;
                if (ObjectUtil.isNotEmpty(historys) && ObjectUtil.isNotEmpty(historys)) {
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
                        //运营管理（经办）
                        if (ObjectUtil.equals(YUN_YING_GUANG_LI_JING_BAN, history.getTaskActivityId())) {
                            if (ObjectUtil.isEmpty(dashboard.getOperationHandlingArrivalTime())) {
                                dashboard.setOperationHandlingArrivalTime(arriveDate);
                            }
                            dashboard.setOperationHandlingSubmitTime(history.getOperateTime() == null ? null : history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                            if (addDay != null) {
                                dashboard.setOperationHandlingTotalTime(addDay.add(dashboard.getOperationHandlingTotalTime() == null ? BigDecimal.ZERO : dashboard.getOperationHandlingTotalTime()));
                                dashboard.setOperationTotalTime(addDay.add(dashboard.getOperationTotalTime() == null ? BigDecimal.ZERO : dashboard.getOperationTotalTime()));
                            }
                        } else if (ObjectUtil.equals(YUN_YING_GUANG_LI_FU_HE, history.getTaskActivityId())) {
                            //运营管理（符合）
                            if (ObjectUtil.isEmpty(dashboard.getOperationReviewArrivalTime())) {
                                dashboard.setOperationReviewArrivalTime(arriveDate);
                            }
                            dashboard.setOperationReviewSubmitTime(history.getOperateTime() == null ? null : history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                            if (addDay != null) {
                                dashboard.setOperationReviewTotalTime(addDay.add(dashboard.getOperationReviewTotalTime() == null ? BigDecimal.ZERO : dashboard.getOperationReviewTotalTime()));
                                dashboard.setOperationTotalTime(addDay.add(dashboard.getOperationTotalTime() == null ? BigDecimal.ZERO : dashboard.getOperationTotalTime()));
                            }
                        } else if (CharSequenceUtil.equalsAny(history.getTaskActivityId(), YUN_YING_GUANG_FU_ZHE_REN, YUN_YING_GUANG_FU_ZHE_REN_2)) {
                            if (addDay != null) {
                                dashboard.setOperationTotalTime(addDay.add(dashboard.getOperationTotalTime() == null ? BigDecimal.ZERO : dashboard.getOperationTotalTime()));
                            }
                        }
                        if (ObjectUtil.isNotEmpty(history.getOperateTime())) {
                            arriveDate = history.getOperateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        }
                    }
                }
            }
        });
        st.stop();
        log.info(st.prettyPrint(TimeUnit.SECONDS));
        return dashboardApprovalBaseRSPS;
    }

    private Page<ProcessResp> getContractEffect(LocalDate endTimeFrom) {
        //查询所有合同创建流程 每年大约2～3百条 数量较少，这里先查符合的流程再过滤合同
        //多查询6个月，防止以前创建的
        endTimeFrom = endTimeFrom.minusMonths(6);
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setModelKeyList(Collections.singletonList(ProcessModelTypeEnum.ContractCreateFlow.name()));
        processPageReq.setPageIndex(1);
        processPageReq.setProcessCreateTimeFrom(Date.from(endTimeFrom.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        processPageReq.setPageSize(Integer.MAX_VALUE);
        processPageReq.setProcessStatusList(Arrays.asList(2, 6));
        return taskApiService.queryProcess(processPageReq);
    }

    private List<ProcessResp> filterProcessResp(Page<ProcessResp> processRespPage, DashboardApprovalListREQ req) {
        if (ObjectUtil.isEmpty(processRespPage) || ObjectUtil.isEmpty(processRespPage.getContents())) {
            return Collections.emptyList();
        }

        List<ProcessResp> contents = processRespPage.getContents();
        return contents.stream().filter(content -> {
            if (ObjectUtil.isNotEmpty(req.getEndTimeFrom()) && ObjectUtil.isNotEmpty(content.getEndTime()) &&
                    req.getEndTimeFrom().isAfter(content.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(req.getEndTimeTo()) && ObjectUtil.isNotEmpty(content.getEndTime()) &&
                    req.getEndTimeTo().isBefore(content.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(req.getContractIds()) && !req.getContractIds().contains(Long.valueOf(content.getBusinessKey()))) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<ProcessResp> filterContractReturnProcess(Page<ProcessResp> processRespPage, DashboardContractReturnListREQ req) {
        if (ObjectUtil.isEmpty(processRespPage) || ObjectUtil.isEmpty(processRespPage.getContents())) {
            return Collections.emptyList();
        }
        List<ProcessResp> contents = processRespPage.getContents();
        return contents.stream().filter(content -> {
            if (ObjectUtil.isNotEmpty(req.getEndTimeFrom()) && ObjectUtil.isNotEmpty(content.getEndTime()) &&
                    req.getEndTimeFrom().isAfter(content.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(req.getEndTimeTo()) && ObjectUtil.isNotEmpty(content.getEndTime()) &&
                    req.getEndTimeTo().isBefore(content.getEndTime().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate())) {
                return false;
            }
            if (ObjectUtil.isNotEmpty(req.getContractIds()) && !req.getContractIds().contains(Long.valueOf(content.getBusinessKey()))) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private <T extends DashboardApprovalBaseRSP> List<T> buildApprovalBase(List<T> dashboardApprovalBaseRSPS, Map<Long, ProcessResp> contractId2ProcessMap) {
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(dashboardApprovalBaseRSPS.stream().map(DashboardApprovalBaseRSP::getClientId).collect(Collectors.toList()));
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(dashboardApprovalBaseRSPS.stream().map(DashboardApprovalBaseRSP::getBizDeptId).collect(Collectors.toList()));
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(dashboardApprovalBaseRSPS.stream().map(DashboardApprovalBaseRSP::getProjSponsorUserId).collect(Collectors.toList()));
        dashboardApprovalBaseRSPS.forEach(rsp -> {
            rsp.setClientName(clientId2Name.get(rsp.getClientId()));
            rsp.setProjSponsorUserName(userId2Name.get(rsp.getProjSponsorUserId()));
            rsp.setBizDeptName(deptId2Name.get(rsp.getBizDeptId()));
            rsp.setFlowId(Optional.ofNullable(contractId2ProcessMap.get(rsp.getId())).map(ProcessResp::getProcessInstanceId).orElse(null));
            // businessGroup
            rsp.setBusinessGroup(BusinessGroupEnum.getBusinessByRiskControlIndustryClassify(rsp.getRiskControlIndustryClassify()).name());
            rsp.setBusinessModel(Optional.ofNullable(ContractBusinessModelEnum.getBusinessModel(rsp.getLeaseType(), rsp.getFactoringType(), rsp.getZrType())).map(ContractBusinessModelEnum::name).orElse(null));
        });
        return dashboardApprovalBaseRSPS;
    }

    public DashboardOperationApprovalStatisticsRSP approvalStatistics(DashboardApprovalListREQ req) {
        if (ObjectUtil.isEmpty(req.getEndTimeFrom())) {
            req.setEndTimeFrom(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        }
        if(ObjectUtil.isNotEmpty(req.getQueryDateFrom())) {
            req.setEndTimeFrom(req.getQueryDateFrom());
        }
        if(ObjectUtil.isNotEmpty(req.getQueryDateTo())) {
            req.setEndTimeFrom(req.getQueryDateFrom());
        }
        //统计本年
        List<DashboardApprovalListRSP> dashboardApprovalListRSPS = null;
        if(Objects.equals(environment, "dev")) {
            dashboardApprovalListRSPS = this.approvalList(req);
        }else{
            dashboardApprovalListRSPS = this.approvalListGuanYuan(req);
        }
        DashboardOperationApprovalStatisticsRSP rsp = new DashboardOperationApprovalStatisticsRSP();
        if(ObjectUtil.isEmpty(dashboardApprovalListRSPS)) {
            return rsp;
        }
        rsp.setContractApprovedNumber(dashboardApprovalListRSPS.size());
        BigDecimal operationHandlingTotalTime = BigDecimal.ZERO;
        int totalCount = dashboardApprovalListRSPS.size();
        BigDecimal operationReviewTotalTime = BigDecimal.ZERO;
        BigDecimal operationTotalTime = BigDecimal.ZERO;
        BigDecimal approvalTotalTime = BigDecimal.ZERO;
        for (DashboardApprovalListRSP approvalListRSP : dashboardApprovalListRSPS) {
            if (ObjectUtil.isNotEmpty(approvalListRSP.getOperationHandlingTotalTime())) {
                operationHandlingTotalTime = operationHandlingTotalTime.add(approvalListRSP.getOperationHandlingTotalTime());
            }
            if (ObjectUtil.isNotEmpty(approvalListRSP.getOperationReviewTotalTime())) {
                operationReviewTotalTime = operationReviewTotalTime.add(approvalListRSP.getOperationReviewTotalTime());
            }
            if (ObjectUtil.isNotEmpty(approvalListRSP.getOperationTotalTime())) {
                operationTotalTime = operationTotalTime.add(approvalListRSP.getOperationTotalTime());
            }
            if(ObjectUtil.isNotEmpty(approvalListRSP.getApprovalTotalTime())) {
                approvalTotalTime = approvalTotalTime.add(approvalListRSP.getApprovalTotalTime());
            }
        }
        rsp.setOperationHandAverage(operationHandlingTotalTime.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));
        rsp.setOperationReviewAverage(operationReviewTotalTime.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));
        rsp.setOperationAverage(operationTotalTime.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));
        rsp.setProcessAverage(approvalTotalTime.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));
        return rsp;
    }

    public List<DashboardContractReturnListRSP> contractReturnList(DashboardContractReturnListREQ req) {
        if (ObjectUtil.isEmpty(req.getEndTimeFrom())) {
            req.setEndTimeFrom(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        }
        //查询所有合同创建流程 每年大约2～3百条 数量较少，这里先查符合的流程再过滤合同
        Page<ProcessResp> processRespPage = getContractEffect(req.getEndTimeFrom());
        Map<Long, ProcessResp> contractId2ProcessMap = filterContractReturnProcess(processRespPage, req).stream().collect(Collectors.toMap(e -> Long.valueOf(e.getBusinessKey()), e -> e, (a, b) -> a));
        if (ObjectUtil.isEmpty(contractId2ProcessMap)) {
            return Collections.emptyList();
        }
        boolean isPublic = false;
        boolean isNotPublic = false;
        if(ObjectUtil.isNotEmpty(req.getBusinessGroup()) && req.getBusinessGroup().size() == 1){
            if(req.getBusinessGroup().contains(BusinessGroupEnum.PUBLIC_CATEGORY.name())) {
                isPublic = true;
            } else {
                isNotPublic = true;
            }
        }
        List<ContractBaseInfo> list = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractId2ProcessMap.keySet())
                .in(ObjectUtil.isNotEmpty(req.getBizDeptId()), ContractBaseInfo::getBizDeptId, req.getBizDeptId())
                .eq(ObjectUtil.isNotEmpty(req.getClientId()), ContractBaseInfo::getClientId, req.getClientId())
                .like(ObjectUtil.isNotEmpty(req.getProjName()), ContractBaseInfo::getProjName, req.getProjName())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), ContractBaseInfo::getContractCode, req.getContractCode())
                .eq(ObjectUtil.isNotEmpty(req.getProjSponsorUserId()), ContractBaseInfo::getProjSponsorUserId, req.getProjSponsorUserId())
                .in(isPublic, ContractBaseInfo::getRiskControlIndustryClassify, BusinessGroupEnum.PUBLIC_CATEGORY.getRiskControlIndustryClassifys())
                .notIn(isNotPublic, ContractBaseInfo::getRiskControlIndustryClassify, BusinessGroupEnum.PUBLIC_CATEGORY.getRiskControlIndustryClassifys())
                .and(CollectionUtil.isNotEmpty(req.getBusinessModel()), consumer -> {
                    consumer.or().in(ObjectUtil.isNotEmpty(req.getBusinessModel()), ContractBaseInfo::getLeaseType, req.getBusinessModel())
                            .or().in(ObjectUtil.isNotEmpty(req.getBusinessModel()), ContractBaseInfo::getFactoringType, req.getBusinessModel())
                            .or().in(ObjectUtil.isNotEmpty(req.getBusinessModel()), ContractBaseInfo::getZrType, req.getBusinessModel());
                })
        );
        //拼接统一参数
        List<DashboardContractReturnListRSP> dashboardApprovalBaseRSPS = buildApprovalBase(BeanUtil.copyToList(list, DashboardContractReturnListRSP.class), contractId2ProcessMap);
        List<DashboardContractReturnListRSP> rsps = new ArrayList<>();
        //特殊参数

        List<String> tuEnum = ListUtil.toList(CommentTypeEnum.BH.name(), CommentTypeEnum.BHFQR.name(), CommentTypeEnum.BHFQR_ZJDW.name());
        Map<String, List<ProcessHistoryResp>> processHistoryMap = processHistoryList(processRespPage.getContents().stream().map(ProcessResp::getProcessInstanceId).collect(Collectors.toSet()));
        if (ObjectUtil.isEmpty(processHistoryMap)) {
            return ListUtil.empty();
        }
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(processHistoryMap.values().stream().flatMap(Collection::stream).map(ProcessHistoryResp::getOperatorId).filter(ObjectUtil::isNotEmpty).map(Long::valueOf).collect(Collectors.toSet()));
        dashboardApprovalBaseRSPS.forEach(dashboard -> {
            //查询节点信息
            ProcessResp processResp = contractId2ProcessMap.get(dashboard.getId());
            if (ObjectUtil.isNotEmpty(processResp)) {
                dashboard.setApplyTime(processResp.getStartTime() == null ? null : processResp.getStartTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                dashboard.setEndTime(processResp.getEndTime() == null ? null : processResp.getEndTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
                //流程详情
                List<ProcessHistoryResp> historys = processHistoryMap.get(processResp.getProcessInstanceId());
                DashboardContractReturnListRSP needAddRsp = BeanUtil.copyProperties(dashboard, DashboardContractReturnListRSP.class);
                needAddRsp.setYunYingReturnCount(0);
                //填充节点信息
                if (ObjectUtil.isNotEmpty(historys)) {
                    //查询有退回流程
                    boolean needAdd = false;
                    StringBuilder sb = new StringBuilder();
                    //填充节点人名称
                    for (ProcessHistoryResp history : historys) {
                        //运营管理（经办）
                        if (ObjectUtil.equals(YUN_YING_GUANG_LI_JING_BAN, history.getTaskActivityId())) {
                            needAddRsp.setYunYingGuanLiName(userId2Name.get(Long.valueOf(history.getOperatorId())));

                        } else if (ObjectUtil.equals(YUN_YING_GUANG_LI_FU_HE, history.getTaskActivityId())) {
                            needAddRsp.setYunYingGuanLiReviewName(userId2Name.get(Long.valueOf(history.getOperatorId())));

                        }
                    }

                    for (ProcessHistoryResp history : historys) {
                        if (!tuEnum.contains(history.getType())) {
                            continue;
                        }
                        //运营管理（经办）
                        if (ObjectUtil.equals(YUN_YING_GUANG_LI_JING_BAN, history.getTaskActivityId())) {
                            needAddRsp.setYunYingReturnCount(needAddRsp.getYunYingReturnCount() + 1);
                            sb.append(history.getTaskNodeName()).append("第").append(needAddRsp.getYunYingReturnCount()).append("次退回:").append(history.getMessage()).append(";");
                            if (!(ObjectUtil.isNotEmpty(req.getYunYingGuanLiId()) && !ObjectUtil.equals(String.valueOf(req.getYunYingGuanLiId()), history.getOperatorId()))) {
                                needAdd = true;
                            }
                        } else if (ObjectUtil.equals(YUN_YING_GUANG_LI_FU_HE, history.getTaskActivityId())) {
                            needAddRsp.setYunYingReturnCount(needAddRsp.getYunYingReturnCount() + 1);
                            sb.append(history.getTaskNodeName()).append("第").append(needAddRsp.getYunYingReturnCount()).append("次退回:").append(history.getMessage()).append(";");
                            if (!(ObjectUtil.isNotEmpty(req.getYunYingGuanLiReviewId()) && !ObjectUtil.equals(String.valueOf(req.getYunYingGuanLiReviewId()), history.getOperatorId()))) {
                                needAdd = true;
                            }
                        } else if (CharSequenceUtil.equalsAny(history.getTaskActivityId(), YUN_YING_GUANG_FU_ZHE_REN, YUN_YING_GUANG_FU_ZHE_REN_2)) {
                            needAddRsp.setYunYingReturnCount(needAddRsp.getYunYingReturnCount() + 1);
                            sb.append(history.getTaskNodeName()).append("第").append(needAddRsp.getYunYingReturnCount()).append("次退回:").append(history.getMessage()).append(";");
                            needAdd = true;
                        }
                    }
                    if (needAdd) {
                        if (sb.length() > 0) {
                            needAddRsp.setReasonReturn(sb.toString());
                        }
                        rsps.add(needAddRsp);
                    }
                }
            }
        });
        return rsps;
    }

    public DashboardOperationContractReturnStatisticsRSP contractReturnStatistics(DashboardContractReturnListREQ req) {
        DashboardOperationContractReturnStatisticsRSP rsp = new DashboardOperationContractReturnStatisticsRSP();
        if(ObjectUtil.isEmpty(req.getEndTimeFrom())) {
            req.setEndTimeFrom(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
        }
        if(ObjectUtil.isNotEmpty(req.getQueryDateFrom())) {
            req.setEndTimeFrom(req.getQueryDateFrom());
        }
        if(ObjectUtil.isNotEmpty(req.getQueryDateTo())) {
            req.setEndTimeFrom(req.getQueryDateFrom());
        }
        List<DashboardContractReturnListRSP> dashboardContractReturnListRSPS = this.contractReturnList(req);
        if(ObjectUtil.isEmpty(dashboardContractReturnListRSPS)) {
            return rsp;
        }
        //查询合同审批通过数量
        Page<ProcessResp> processRespPage = this.getContractEffect(req.getEndTimeFrom());
        Map<Long, ProcessResp> contractId2ProcessMap = filterContractReturnProcess(processRespPage, req).stream().collect(Collectors.toMap(e -> Long.valueOf(e.getBusinessKey()), e -> e, (a, b) -> a));
        if (ObjectUtil.isEmpty(contractId2ProcessMap) || ObjectUtil.isEmpty(contractId2ProcessMap.keySet())){
            return rsp;
        }
        Integer yunYingReturnCount = dashboardContractReturnListRSPS.stream().map(DashboardContractReturnListRSP::getYunYingReturnCount).reduce(Integer::sum).get();
        rsp.setContractApprovedNumber(contractId2ProcessMap.keySet().size());
        rsp.setContractReturnNumber(dashboardContractReturnListRSPS.size());
        rsp.setReturnRate(BigDecimal.valueOf(rsp.getContractReturnNumber()).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(rsp.getContractApprovedNumber()), 2, RoundingMode.HALF_UP));
        rsp.setReturnCount(yunYingReturnCount);
        rsp.setReturnAverage(BigDecimal.valueOf(rsp.getReturnCount()).divide(BigDecimal.valueOf(rsp.getContractReturnNumber()), 2, RoundingMode.HALF_UP));
        return rsp;
    }

    public Map<String, List<ProcessHistoryResp>> processHistoryList(Set<String> processInstanceIds) {
        //查询所有流程实例
        List<HistoricProcessInstance> processInstanceList = historyService.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
        if (ObjectUtil.isEmpty(processInstanceList)) {
            return MapUtil.empty();
        }
        Set<String> processDefinitionIds = processInstanceList.stream().map(HistoricProcessInstance::getProcessDefinitionId).collect(Collectors.toSet());
        List<NodeDefineResp> nodeDefineResps = new ArrayList<>();
        processDefinitionIds.forEach(processDefinitionId -> {
            nodeDefineResps.addAll(flowModelApiService.getNodeDefineListByProcessDefinitionId(processDefinitionId));
        });
        //查询所有模型
        Map<String, NodeDefineResp> nodeDefineMap = nodeDefineResps.stream().collect(Collectors.toMap(NodeDefineResp::getActivityId, e -> e, (a, b) -> a));

        Example example = new Example(cn.zswltech.flow.core.domain.entity.OperateRecord.class);
        example.createCriteria().andIn("processInstanceId", processInstanceIds);
        List<cn.zswltech.flow.core.domain.entity.OperateRecord> operateRecordList = operateRecordMapper.selectByCondition(example);
        Map<String, List<cn.zswltech.flow.core.domain.entity.OperateRecord>> map = operateRecordList.stream().collect(Collectors.groupingBy(cn.zswltech.flow.core.domain.entity.OperateRecord::getProcessInstanceId));
        Map<String, List<ProcessHistoryResp>> resultMap = new HashMap<>();
        map.forEach((k, v) -> {
            resultMap.put(k, v.stream().map((c) ->
                    ProcessHistoryResp.builder()
                            .type(c.getType())
                            .typeName(CommentTypeEnum.getByName(c.getType()).getMessage())
                            .message(c.getNote())
                            .operateTime(c.getGmtCreate())
                            .operatorId(c.getHandlerId())
                            .taskActivityId(c.getTaskActivityId())
                            .taskNodeName(Optional.ofNullable(c.getTaskActivityId())
                                    .map((t) -> (NodeDefineResp) nodeDefineMap.get(t)).map(NodeDefineResp::getName).orElse(null)).build()).collect(Collectors.toList()));
        });
        return resultMap;
    }


}
