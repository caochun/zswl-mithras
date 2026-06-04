package cn.zswltech.mithras.service.service.managereport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.enums.CommentTypeEnum;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveREQ;
import cn.zswltech.mithras.dto.dashboard.operate.DashboardOperateTodoArriveRSP;
import cn.zswltech.mithras.dto.managereport.*;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.dashboard.domain.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.dashboard.domain.enums.BusinessGroupEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.ToDoOperateRecordMapper;
import cn.zswltech.mithras.guanbao.mapper.managereport.*;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.OperateRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.service.service.dashboard.DashboardOperateTodoService;
import cn.zswltech.mithras.service.service.dashboard.boss.GuanYuanBasicService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.ZLHeTongShiXiaoDTO;
import cn.zswltech.sleipnir.toolkit.enums.GuanYuanFilterTypeEnum;
import cn.zswltech.sleipnir.toolkit.request.guanyuan.GuanYuanDSRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@Slf4j
@Service
public class ManageReportService extends GuanYuanBasicService {
    @Resource
    private ManageReportMapper manageReportMapper;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private DashboardOperateTodoService dashboardOperateTodoService;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;
    @Resource
    private ToDoOperateRecordMapper toDoOperateRecordMapper;
    @Resource
    private SysUserService sysUserService;

    public List<HeTongShiXiaoStatisticRSP> statisticHeTongShiXiao(HeTongShiXiaoStatisticREQ req) {
        // 复用明细接口数据
        List<HeTongShiXiaoDetailRSP> detailList = this.queryHeTongShiXiaoDetail(req);
        // 按照部门id分组
        Map<Long, List<HeTongShiXiaoDetailRSP>> deptGroupMap = detailList.stream().collect(Collectors.groupingBy(HeTongShiXiaoDetailRSP::getBizDeptId));
        // 获取生效业务部门
        List<OrgDO> orgList = sysUserService.listBizDept();
        orgList.removeIf(e -> Objects.equals(e.getState(), YesOrNoNumberEnum.NO.getCode()));
        // 统计并组装返回结果
        List<HeTongShiXiaoStatisticRSP> result = new LinkedList<>();
        for (OrgDO org : orgList) {
            HeTongShiXiaoStatisticRSP rsp = this.build(org, deptGroupMap.get(org.getId()));
            if (Objects.equals(req.getBusinessCategory(), BusinessGroupEnum.PUBLIC_CATEGORY.name())) {
                rsp.setBusinessCategory("公用");
            } else if (Objects.equals(req.getBusinessCategory(), BusinessGroupEnum.INDUSTRY_CATEGORY.name())) {
                rsp.setBusinessCategory("产业");
            } else {
                rsp.setBusinessCategory("不限");
            }
            if (Objects.equals(req.getLeaseType(), LeaseType.zhi_zu.name())) {
                rsp.setLeaseType("直租");
            } else if (Objects.equals(req.getLeaseType(), LeaseType.hui_zu.name())) {
                rsp.setLeaseType("回租");
            } else if (Objects.equals(req.getLeaseType(), LeaseType.jyx_zu.name())) {
                rsp.setLeaseType("经营性租赁");
            } else {
                rsp.setLeaseType("不限");
            }
            result.add(rsp);
        }
        return result;
    }

    private HeTongShiXiaoStatisticRSP build(OrgDO orgDO, List<HeTongShiXiaoDetailRSP> detailList) {
        HeTongShiXiaoStatisticRSP rsp = new HeTongShiXiaoStatisticRSP();
        rsp.setBizDeptId(orgDO.getId());
        rsp.setBizDeptName(orgDO.getName());
        if (CollectionUtil.isNotEmpty(detailList)) {
            BigDecimal durationSum = BigDecimal.ZERO;
            BigDecimal durationYYJBSum = BigDecimal.ZERO;
            BigDecimal durationYYBSum = BigDecimal.ZERO;
            BigDecimal durationFWBSum = BigDecimal.ZERO;
            BigDecimal durationCWBSum = BigDecimal.ZERO;
            for (HeTongShiXiaoDetailRSP detailRSP : detailList) {
                // 全流程总耗时（工作日小时）
                durationSum = durationSum.add(new BigDecimal(detailRSP.getDuration()));
                // 运营经办总耗时（工作日小时）
                durationYYJBSum = durationYYJBSum.add(new BigDecimal(detailRSP.getDurationYYJB()));
                // 运营部总耗时 = 运营经办总耗时（工作日小时） + 运营复核总耗时（工作日小时） + 运营负责人总耗时（工作日小时）
                durationYYBSum = durationYYBSum.add(new BigDecimal(detailRSP.getDurationYYJB())).add(new BigDecimal(detailRSP.getDurationYYFH())).add(new BigDecimal(detailRSP.getDurationYYFZR()));
                // 法务部总耗时 = 法务经理总耗时（工作日小时） + 法规部负责人总耗时（工作日小时）
                durationFWBSum = durationFWBSum.add(new BigDecimal(detailRSP.getDurationFWJL())).add(new BigDecimal(detailRSP.getDurationFWFZR()));
                // 财务部总耗时 = 财务主管总耗时（工作日小时）
                durationCWBSum = durationCWBSum.add(new BigDecimal(detailRSP.getDurationCWZG()));
            }
            // 计算平均耗时（转成工作日）
            rsp.setAverageDuration(durationSum.divide(BigDecimal.valueOf(detailList.size()), 20, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(24), 3, RoundingMode.HALF_UP).toPlainString());
            rsp.setAverageDurationYYJB(durationYYJBSum.divide(BigDecimal.valueOf(detailList.size()), 20, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(24), 3, RoundingMode.HALF_UP).toPlainString());
            rsp.setAverageDurationYYB(durationYYBSum.divide(BigDecimal.valueOf(detailList.size()), 20, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(24), 3, RoundingMode.HALF_UP).toPlainString());
            rsp.setAverageDurationFWB(durationFWBSum.divide(BigDecimal.valueOf(detailList.size()), 20, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(24), 3, RoundingMode.HALF_UP).toPlainString());
            rsp.setAverageDurationCWB(durationCWBSum.divide(BigDecimal.valueOf(detailList.size()), 20, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(24), 3, RoundingMode.HALF_UP).toPlainString());
        }
        return rsp;
    }

    public List<HeTongShiXiaoDetailRSP> queryHeTongShiXiaoDetail(HeTongShiXiaoDetailREQ req) {
        // 为了保证底层数据一致，直接从观远取数据
        GuanYuanDSRequest.Body reqBody = this.buildGuanYuanReqBody(req);
        List<ZLHeTongShiXiaoDTO> sourceList = this.queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum.YunYingManageReportZLHeTongShiXiao, reqBody, ZLHeTongShiXiaoDTO.class);
        if (CollectionUtil.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        // 合并重复数据
        Map<String, ZLHeTongShiXiaoDTO> map = new HashMap<>();
        sourceList.forEach(item -> map.put(item.getContractCode(), item));
        // 数据模型转换
        return map.values().stream()
                .map(item -> BeanUtil.copyProperties(item, HeTongShiXiaoDetailRSP.class))
                .sorted(Comparator.comparing(HeTongShiXiaoDetailRSP::getProcessEndTime).reversed())
                .collect(Collectors.toList());
    }

    private GuanYuanDSRequest.Body buildGuanYuanReqBody(HeTongShiXiaoDetailREQ req) {
        GuanYuanDSRequest.Body reqBody = new GuanYuanDSRequest.Body();
        List<GuanYuanDSRequest.Filter> filterList = new LinkedList<>();
        if (StrUtil.isNotBlank(req.getBusinessCategory())) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("项目分类");
            filter.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
            if (Objects.equals(req.getBusinessCategory(), BusinessGroupEnum.PUBLIC_CATEGORY.name())) {
                filter.setFilterValue(Collections.singletonList("公用类"));
            } else if (Objects.equals(req.getBusinessCategory(), BusinessGroupEnum.INDUSTRY_CATEGORY.name())) {
                filter.setFilterValue(Collections.singletonList("产业类"));
            } else {
                filter.setFilterValue(ListUtil.of("公用类", "产业类"));
            }
            filterList.add(filter);
        }
        if (StrUtil.isNotBlank(req.getLeaseType())) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("项目类型");
            filter.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
            if (Objects.equals(req.getLeaseType(), LeaseType.zhi_zu.name())) {
                filter.setFilterValue(Collections.singletonList("直租"));
            } else if (Objects.equals(req.getLeaseType(), LeaseType.hui_zu.name())) {
                filter.setFilterValue(Collections.singletonList("回租"));
            } else if (Objects.equals(req.getLeaseType(), LeaseType.jyx_zu.name())) {
                filter.setFilterValue(Collections.singletonList("经营性租赁"));
            } else {
                filter.setFilterValue(ListUtil.of("直租", "回租", "经营性租赁"));
            }
            filterList.add(filter);
        }
        if (Objects.nonNull(req.getBizDeptId())) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("biz_dept_id");
            filter.setFilterType(GuanYuanFilterTypeEnum.EQ.name());
            filter.setFilterValue(Collections.singletonList(req.getBizDeptId().toString()));
            filterList.add(filter);
        }
        if (Objects.nonNull(req.getQueryDateFrom())) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("合同审批通过时间");
            filter.setFilterType(GuanYuanFilterTypeEnum.GE.name());
            LocalDateTime from = LocalDateTime.of(req.getQueryDateFrom().getYear(), req.getQueryDateFrom().getMonthValue(), req.getQueryDateFrom().getDayOfMonth(), 0, 0, 0);
            filter.setFilterValue(Collections.singletonList(LocalDateTimeUtil.format(from, DatePattern.NORM_DATETIME_PATTERN)));
            filterList.add(filter);
        }
        if (Objects.nonNull(req.getQueryDateTo())) {
            GuanYuanDSRequest.Filter filter = new GuanYuanDSRequest.Filter();
            filter.setName("合同审批通过时间");
            filter.setFilterType(GuanYuanFilterTypeEnum.LE.name());
            LocalDateTime to = LocalDateTime.of(req.getQueryDateTo().getYear(), req.getQueryDateTo().getMonthValue(), req.getQueryDateTo().getDayOfMonth(), 23, 59, 59);
            filter.setFilterValue(Collections.singletonList(LocalDateTimeUtil.format(to, DatePattern.NORM_DATETIME_PATTERN)));
            filterList.add(filter);
        }
        reqBody.setFilters(filterList);
        return reqBody;
    }

    public List<YunYingDaiBanStatisticRSP> statisticYunYingDaiBan(YunYingDaiBanStatisticREQ req) {
        // 先复用运营工作台的逻辑获取数据，以此来保持和工作台的数据是一致的
        DashboardOperateTodoArriveREQ arriveREQ = new DashboardOperateTodoArriveREQ();
        arriveREQ.setType(DashboardOperateTodoArriveREQ.ARRIVE);
        List<DashboardOperateTodoArriveRSP> arriveList = dashboardOperateTodoService.todoStatisticsArrive(arriveREQ);
        Map<String, DashboardOperateTodoArriveRSP> arriveMap = arriveList.stream().collect(Collectors.toMap(DashboardOperateTodoArriveRSP::getModelName, e -> e));
        DashboardOperateTodoArriveREQ willArriveREQ = new DashboardOperateTodoArriveREQ();
        willArriveREQ.setType(DashboardOperateTodoArriveREQ.WILL_ARRIVE);
        List<DashboardOperateTodoArriveRSP> willArriveList = dashboardOperateTodoService.todoStatisticsWillArrive(willArriveREQ);
        Map<String, DashboardOperateTodoArriveRSP> willArriveMap = willArriveList.stream().collect(Collectors.toMap(DashboardOperateTodoArriveRSP::getModelName, e -> e));
        // 转返回参数
        return DashboardOperateTodoService.modelNameListOne.stream().map(modelName -> {
            DashboardOperateTodoArriveRSP arrive = arriveMap.get(modelName);
            DashboardOperateTodoArriveRSP willArrive = willArriveMap.get(modelName);
            return convert(modelName, arrive, willArrive);
        }).collect(Collectors.toList());
    }

    private YunYingDaiBanStatisticRSP convert(String modelName, DashboardOperateTodoArriveRSP arrive, DashboardOperateTodoArriveRSP willArrive) {
        YunYingDaiBanStatisticRSP rsp = new YunYingDaiBanStatisticRSP();
        rsp.setProcessDisplay(modelName);
        Map<String, List<String>> m1;
        Map<String, List<String>> m2;
        if (CollectionUtil.isEmpty(arrive.getContent())) {
            m1 = Collections.emptyMap();
        } else {
            m1 = arrive.getContent().stream().collect(Collectors.toMap(DashboardOperateTodoArriveRSP.content::getActivityDisplay, DashboardOperateTodoArriveRSP.content::getProcessInstanceIdList));
        }
        if (CollectionUtil.isEmpty(willArrive.getContent())) {
            m2 = Collections.emptyMap();
        } else {
            m2 = willArrive.getContent().stream().collect(Collectors.toMap(DashboardOperateTodoArriveRSP.content::getActivityDisplay, DashboardOperateTodoArriveRSP.content::getProcessInstanceIdList));
        }
        rsp.setArriveYYJBProcessInstanceIds(Optional.ofNullable(m1.get(DashboardOperateTodoService.YYJB)).orElse(Collections.emptyList()));
        rsp.setArriveYYFHProcessInstanceIds(Optional.ofNullable(m1.get(DashboardOperateTodoService.YYFH)).orElse(Collections.emptyList()));
        rsp.setWillArriveYYFZRProcessInstanceIds(Optional.ofNullable(m1.get(DashboardOperateTodoService.YYFZR)).orElse(Collections.emptyList()));
        rsp.setArriveHJProcessInstanceIds(Optional.ofNullable(m1.get(DashboardOperateTodoService.HJ)).orElse(Collections.emptyList()));
        rsp.setWillArriveYYJBProcessInstanceIds(Optional.ofNullable(m2.get(DashboardOperateTodoService.YYJB)).orElse(Collections.emptyList()));
        rsp.setWillArriveYYFHProcessInstanceIds(Optional.ofNullable(m2.get(DashboardOperateTodoService.YYFH)).orElse(Collections.emptyList()));
        rsp.setWillArriveYYFZRProcessInstanceIds(Optional.ofNullable(m2.get(DashboardOperateTodoService.YYFZR)).orElse(Collections.emptyList()));
        rsp.setWillArriveHJProcessInstanceIds(Optional.ofNullable(m2.get(DashboardOperateTodoService.HJ)).orElse(Collections.emptyList()));
        return rsp;
    }

    public List<YunYingDaiBanDetailRSP> queryYunYingDaiBanDetail(YunYingDaiBanDetailREQ req) {
        // 先复用运营工作台的逻辑获取数据，以此来保持和工作台的数据是一致的
        DashboardOperateTodoArriveREQ arriveREQ = new DashboardOperateTodoArriveREQ();
        arriveREQ.setType(DashboardOperateTodoArriveREQ.ARRIVE);
        List<DashboardOperateTodoArriveRSP> arriveList = dashboardOperateTodoService.todoStatisticsArrive(arriveREQ);
        DashboardOperateTodoArriveREQ willArriveREQ = new DashboardOperateTodoArriveREQ();
        willArriveREQ.setType(DashboardOperateTodoArriveREQ.WILL_ARRIVE);
        List<DashboardOperateTodoArriveRSP> willArriveList = dashboardOperateTodoService.todoStatisticsWillArrive(willArriveREQ);
        // 根据过滤条件合并数据
        Set<String> processInstanceIds = new HashSet<>();
        this.filterAndMergeProcessInstanceIds(req, arriveList, processInstanceIds);
        this.filterAndMergeProcessInstanceIds(req, willArriveList, processInstanceIds);
        if (CollectionUtil.isEmpty(processInstanceIds)) {
            return Collections.emptyList();
        }
        // 过滤后的流程实例ID作为查询条件进行数据查询
        YunYingDaiBanQuery query = new YunYingDaiBanQuery();
        query.setProcessInstanceIds(new LinkedList<>(processInstanceIds));
        List<YunYingDaiBanResult> dbList = manageReportMapper.selectYunYingDaiBan(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        // 按照流程ID进行分组
        Map<String, List<YunYingDaiBanResult>> dbResultMap = dbList.stream().collect(Collectors.groupingBy(YunYingDaiBanResult::getProcessInstanceId));
        List<YunYingDaiBanDetailRSP> result = new LinkedList<>();
        for (Map.Entry<String, List<YunYingDaiBanResult>> entry : dbResultMap.entrySet()) {
            List<String> assignerIds = entry.getValue().stream().map(e -> e.getCurrentAssignerId().toString()).collect(Collectors.toList());
            List<String> assignerNames = entry.getValue().stream().map(YunYingDaiBanResult::getCurrentAssignerName).collect(Collectors.toList());
            YunYingDaiBanDetailRSP rsp = this.convert(entry.getValue().get(0));
            rsp.setCurrentAssignerId(CharSequenceUtil.join("、", assignerIds));
            rsp.setCurrentAssignerName(CharSequenceUtil.join("、", assignerNames));
            result.add(rsp);
        }
        // 补充退回意见
        // 先查询运营相关岗位的人员
        Example example = new Example(UserOrgJobDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("jobCode", ListUtil.of(JobEnum.yunYingGuanLi.name(), JobEnum.yunYingGuanLiReview.name(), JobEnum.operationManagement.name(), JobEnum.operationManagementReview.name(), JobEnum.headofyyglb.name(), JobEnum.yyglbleader.name()));
        List<UserOrgJobDO> userOrgJobList = userOrgJobDOMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(userOrgJobList)) {
            Set<String> userIds = userOrgJobList.stream().map(UserOrgJobDO::getUserId).map(Object::toString).collect(Collectors.toSet());
            // 查询指定流程的退回操作记录
            LambdaQueryWrapper<OperateRecord> operateRecordQuery = Wrappers.lambdaQuery();
            if (CollectionUtil.isNotEmpty(req.getProcessInstanceIdList())) {
                operateRecordQuery.in(OperateRecord::getProcessInstanceId, req.getProcessInstanceIdList());
            }
            operateRecordQuery.in(OperateRecord::getType, ListUtil.of(CommentTypeEnum.BH.name(), CommentTypeEnum.BHFQR.name(), CommentTypeEnum.BHFQR_ZJDW.name()));
            operateRecordQuery.in(OperateRecord::getHandlerId, userIds);
            List<OperateRecord> operateRecordList = toDoOperateRecordMapper.selectList(operateRecordQuery);
            // 按照流程id分组
            Map<String, List<OperateRecord>> operateRecordMap = operateRecordList.stream().collect(Collectors.groupingBy(OperateRecord::getProcessInstanceId));
            result.forEach(e -> {
                List<OperateRecord> list = operateRecordMap.get(e.getProcessInstanceId());
                if (CollectionUtil.isNotEmpty(list)) {
                    List<String> backRemarkList = list.stream().filter(item -> StrUtil.isNotBlank(item.getNote())).map(item -> {
                        String s = item.getNote();
                        s = s.replace("<div>", "").replace("</div>", "").replace("&nbsp;", "\n");
                        return s;
                    }).collect(Collectors.toList());
                    e.setBackRemark(CharSequenceUtil.join("\n", backRemarkList));
                }
            });
        }
        return result;
    }

    private void filterAndMergeProcessInstanceIds(YunYingDaiBanDetailREQ req, List<DashboardOperateTodoArriveRSP> list, Set<String> processInstanceIds) {
        if (CollectionUtil.isNotEmpty(req.getProcessInstanceIdList())) {
            processInstanceIds.addAll(req.getProcessInstanceIdList());
            return;
        }
        for (DashboardOperateTodoArriveRSP rsp : list) {
            if (CollectionUtil.isEmpty(rsp.getContent())) {
                continue;
            }
            for (DashboardOperateTodoArriveRSP.content content : rsp.getContent()) {
                if (CollectionUtil.isEmpty(content.getProcessInstanceIdList())) {
                    continue;
                }
                processInstanceIds.addAll(content.getProcessInstanceIdList());
            }
        }
    }

    private YunYingDaiBanDetailRSP convert(YunYingDaiBanResult dbResult) {
        YunYingDaiBanDetailRSP rsp = BeanUtil.copyProperties(dbResult, YunYingDaiBanDetailRSP.class);
        if (StrUtil.isNotBlank(dbResult.getLeaseTypes())) {
            List<String> keyList = JSONUtil.toList(dbResult.getLeaseTypes(), String.class);
            List<String> valueList = keyList.stream().map(e -> Optional.ofNullable(LeaseType.of(e)).map(LeaseType::display).orElse("")).collect(Collectors.toList());
            rsp.setLeaseTypesDisplay(CharSequenceUtil.join("、", valueList));
        }
        if (Objects.nonNull(dbResult.getProcessStatus())) {
            rsp.setProcessStatusDisplay(Optional.ofNullable(ProcessBusinessStatusEnum.getByType(dbResult.getProcessStatus())).map(ProcessBusinessStatusEnum::getDisplay).orElse(""));
        }
        return rsp;
    }

    public PageR<YeWuYunXingFenXiDetailRSP> queryYeWuYunXingFenXiDetailWithPage(YeWuYunXingFenXiDetailREQ req) {
        YeWuYunXingFenXiQuery query = this.convert(req);
        query.setStart((req.getPage() - 1) * req.getPageSize());
        query.setSize(req.getPageSize());
        List<YeWuYunXingFenXiResult> list = manageReportMapper.selectYeWuYunXingFenXi(query);
        long total = manageReportMapper.countYeWuYunXingFenXi(query);
        if (total == 0) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<YeWuYunXingFenXiDetailRSP> result = list.stream().map(this::convert).collect(Collectors.toList());
        return PageR.of(result, total, req.getPage(), req.getPageSize());
    }

    public List<YeWuYunXingFenXiStatisticRSP> statisticYeWuYunXingFenXi(YeWuYunXingFenXiStatisticREQ req) {
        List<YeWuYunXingFenXiStatisticRSP> result = new LinkedList<>();
        YeWuYunXingFenXiQuery query = this.convert(req);
        query.setGroupType(req.getGroupType());
        List<YeWuYunXingFenXiStatisticResult> dbList = manageReportMapper.statisticYeWuYunXingFenXi(query);
        if (Objects.equals(req.getGroupType(), YeWuYunXingFenXiStatisticREQ.GROUP_TYPE_DEPT)) {
            List<OrgDO> bizDeptList = this.listAllBizDept();
            if (CollectionUtil.isEmpty(bizDeptList)) {
                return Collections.emptyList();
            }
            // 根据部门分组
            Map<Long, List<YeWuYunXingFenXiStatisticResult>> bizDeptGroupMap = dbList.stream().collect(Collectors.groupingBy(YeWuYunXingFenXiStatisticResult::getBizDeptId));
            // 组内拆分不同流程的数据
            for (OrgDO orgDO : bizDeptList) {
                if (Objects.equals(orgDO.getState(), YesOrNoNumberEnum.NO.getCode())) {
                    continue;
                }
                YeWuYunXingFenXiStatisticRSP rsp = new YeWuYunXingFenXiStatisticRSP();
                rsp.setBizDeptId(orgDO.getId());
                rsp.setBizDeptName(orgDO.getName());
                this.fillInfo(rsp, bizDeptGroupMap.get(orgDO.getId()));
                result.add(rsp);
            }
            // 部门统计在列表尾部增加合计行
            this.addSum(result);
        }
        if (Objects.equals(req.getGroupType(), YeWuYunXingFenXiStatisticREQ.GROUP_TYPE_MONTH)) {
            // 根据月份分组
            Map<String, List<YeWuYunXingFenXiStatisticResult>> monthGroupMap = dbList.stream().collect(Collectors.groupingBy(YeWuYunXingFenXiStatisticResult::getYearAndMonth));
            for (Map.Entry<String, List<YeWuYunXingFenXiStatisticResult>> entry : monthGroupMap.entrySet()) {
                YeWuYunXingFenXiStatisticRSP rsp = new YeWuYunXingFenXiStatisticRSP();
                rsp.setYearAndMonth(entry.getKey());
                this.fillInfo(rsp, entry.getValue());
                result.add(rsp);
            }
            result.sort(Comparator.comparing(YeWuYunXingFenXiStatisticRSP::getYearAndMonth));
        }
        return result;
    }

    private void addSum(List<YeWuYunXingFenXiStatisticRSP> list) {
        YeWuYunXingFenXiStatisticRSP sumRSP = new YeWuYunXingFenXiStatisticRSP();
        sumRSP.setBizDeptName("合计");
        for (YeWuYunXingFenXiStatisticRSP rsp : list) {
            sumRSP.setProjEstablishCreateQuantity(sumRSP.getProjEstablishCreateQuantity() + rsp.getProjEstablishCreateQuantity());
            sumRSP.setProjEstablishCreateAmount(sumRSP.getProjEstablishCreateAmount() + rsp.getProjEstablishCreateAmount());
            sumRSP.setProjReviewCreateQuantity(sumRSP.getProjReviewCreateQuantity() + rsp.getProjReviewCreateQuantity());
            sumRSP.setProjReviewCreateAmount(sumRSP.getProjReviewCreateAmount() + rsp.getProjReviewCreateAmount());
            sumRSP.setLeaseItemCreateQuantity(sumRSP.getLeaseItemCreateQuantity() + rsp.getLeaseItemCreateQuantity());
            sumRSP.setLeaseItemCreateAmount(sumRSP.getLeaseItemCreateAmount() + rsp.getLeaseItemCreateAmount());
            sumRSP.setContractCreateQuantity(sumRSP.getContractCreateQuantity() + rsp.getContractCreateQuantity());
            sumRSP.setContractCreateAmount(sumRSP.getContractCreateAmount() + rsp.getContractCreateAmount());
            sumRSP.setPaymentCreateQuantity(sumRSP.getPaymentCreateQuantity() + rsp.getPaymentCreateQuantity());
            sumRSP.setPaymentCreateAmount(sumRSP.getPaymentCreateAmount() + rsp.getPaymentCreateAmount());
            sumRSP.setPaymentActualPayQuantity(sumRSP.getPaymentActualPayQuantity() + rsp.getPaymentActualPayQuantity());
            sumRSP.setPaymentActualPayAmount(sumRSP.getPaymentActualPayAmount() + rsp.getPaymentActualPayAmount());
        }
        list.add(sumRSP);
    }

    private void fillInfo(YeWuYunXingFenXiStatisticRSP rsp, List<YeWuYunXingFenXiStatisticResult> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (YeWuYunXingFenXiStatisticResult dbResult : list) {
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name())) {
                rsp.setProjEstablishCreateQuantity(dbResult.getQuantity());
                rsp.setProjEstablishCreateAmount(dbResult.getAmount());
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.ProjReviewCreateFlow.name())) {
                rsp.setProjReviewCreateQuantity(dbResult.getQuantity());
                rsp.setProjReviewCreateAmount(dbResult.getAmount());
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.LeaseCreateFlow.name())) {
                rsp.setLeaseItemCreateQuantity(dbResult.getQuantity());
                rsp.setLeaseItemCreateAmount(dbResult.getAmount());
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.ContractCreateFlow.name())) {
                rsp.setContractCreateQuantity(dbResult.getQuantity());
                rsp.setContractCreateAmount(dbResult.getAmount());
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.PaymentCreateFlow.name())) {
                rsp.setPaymentCreateQuantity(dbResult.getQuantity());
                rsp.setPaymentCreateAmount(dbResult.getAmount());
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.PaymentActualDetailFlow.name())) {
                rsp.setPaymentActualPayQuantity(dbResult.getQuantity());
                rsp.setPaymentActualPayAmount(dbResult.getAmount());
            }
        }
    }

    private YeWuYunXingFenXiQuery convert(YeWuYunXingFenXiDetailREQ req) {
        YeWuYunXingFenXiQuery query = new YeWuYunXingFenXiQuery();
        int queryStartYear = req.getProcessStartDateFrom().getYear();
        int queryEndYear = req.getProcessStartDateTo().getYear();
        if (queryStartYear != queryEndYear) {
            throw new MithrasException("查询日期区间不允许跨年");
        }
        query.setProcessStartTimeFrom(LocalDateTime.of(req.getProcessStartDateFrom(), LocalTime.of(0, 0, 0)));
        query.setProcessStartTimeTo(LocalDateTime.of(req.getProcessStartDateTo(), LocalTime.of(23, 59, 59)));
        if (CollectionUtil.isEmpty(req.getProcessModelTypeList())) {
            // 啥都不传默认查下面流程
            query.setProcessModelTypeList(ListUtil.of(
                    ProcessModelTypeEnum.ProjEstablishCreateFlow.name(),
                    ProcessModelTypeEnum.ProjReviewCreateFlow.name(),
                    ProcessModelTypeEnum.LeaseCreateFlow.name(),
                    ProcessModelTypeEnum.ContractCreateFlow.name(),
                    ProcessModelTypeEnum.PaymentCreateFlow.name(),
                    ProcessModelTypeEnum.PaymentActualDetailFlow.name()
            ));
        } else {
            query.setProcessModelTypeList(req.getProcessModelTypeList());
        }
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        if (StrUtil.isNotBlank(req.getBusinessCategory())) {
            if (Objects.equals(req.getBusinessCategory(), BusinessGroupEnum.PUBLIC_CATEGORY.name())) {
                query.setRiskControlIndustryClassifyList(RiskControlIndustryClassify.listPublic());
            }
            if (Objects.equals(req.getBusinessCategory(), BusinessGroupEnum.INDUSTRY_CATEGORY.name())) {
                query.setRiskControlIndustryClassifyList(RiskControlIndustryClassify.listIndustry());
            }
        }
        if (Objects.nonNull(req.getProcessStatus())) {
            if (Objects.equals(req.getProcessStatus(), "RUNNING")) {
                query.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.RUNNING.getType()));
            }
            if (Objects.equals(req.getProcessStatus(), "FINISH")) {
                query.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            }
        } else {
            // 啥都不传默认查询审批中和审批完成
            query.setProcessStatusList(ListUtil.of(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
        }
        if (StrUtil.isNotBlank(req.getLeaseType())) {
            query.setLeaseType(req.getLeaseType());
        }
        return query;
    }

    private YeWuYunXingFenXiDetailRSP convert(YeWuYunXingFenXiResult dbResult) {
        YeWuYunXingFenXiDetailRSP rsp = BeanUtil.copyProperties(dbResult, YeWuYunXingFenXiDetailRSP.class);
        if (RiskControlIndustryClassify.listPublic().contains(dbResult.getClientRiskControlIndustryClassify())) {
            rsp.setBusinessCategory("公用");
        } else {
            rsp.setBusinessCategory("产业");
        }
        if (StrUtil.isNotBlank(dbResult.getLeaseTypes())) {
            List<String> keyList = JSONUtil.toList(dbResult.getLeaseTypes(), String.class);
            List<String> valueList = keyList.stream().map(e -> Optional.ofNullable(LeaseType.of(e)).map(LeaseType::display).orElse("")).collect(Collectors.toList());
            rsp.setLeaseTypesDisplay(CharSequenceUtil.join("、", valueList));
        }
        if (StrUtil.isNotBlank(dbResult.getProcessModelType())) {
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.ProjEstablishCreateFlow.name())) {
                rsp.setProjectStage("立项创建");
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.ProjReviewCreateFlow.name())) {
                rsp.setProjectStage("评审创建");
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.LeaseCreateFlow.name())) {
                rsp.setProjectStage("租赁物创建");
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.ContractCreateFlow.name())) {
                rsp.setProjectStage("合同创建");
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.PaymentCreateFlow.name())) {
                rsp.setProjectStage("合同付款");
            }
            if (Objects.equals(dbResult.getProcessModelType(), ProcessModelTypeEnum.PaymentActualDetailFlow.name())) {
                rsp.setProjectStage("合同投放");
            }
        }
        if (Objects.nonNull(dbResult.getProcessStartTime())) {
            rsp.setProcessStartTimeStr(LocalDateTimeUtil.format(dbResult.getProcessStartTime(), DatePattern.NORM_DATETIME_PATTERN));
        }
        if (Objects.nonNull(dbResult.getProcessEndTime())) {
            rsp.setProcessEndTimeStr(LocalDateTimeUtil.format(dbResult.getProcessEndTime(), DatePattern.NORM_DATETIME_PATTERN));
            rsp.setProcessEndTimeMonth(LocalDateTimeUtil.format(dbResult.getProcessEndTime(), DatePattern.NORM_MONTH_PATTERN));
            rsp.setProcessEndTimeYear(LocalDateTimeUtil.format(dbResult.getProcessEndTime(), DatePattern.NORM_YEAR_PATTERN));
        }
        if (Objects.nonNull(dbResult.getMinPayDate())) {
            rsp.setMinPayDateStr(LocalDateTimeUtil.format(dbResult.getMinPayDate(), DatePattern.NORM_DATE_PATTERN));
            rsp.setMinPayDateMonth(LocalDateTimeUtil.format(dbResult.getMinPayDate(), DatePattern.NORM_MONTH_PATTERN));
        }
        return rsp;
    }

    private List<OrgDO> listAllBizDept() {
        Example example = new Example(OrgDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", OrgConstants.BUSINESS_DEPT);
        return orgDOMapper.selectByExample(example);
    }
}
