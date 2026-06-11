package cn.zswltech.mithras.application.orchestration.facade.workbench;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.baseinfo.jsonbean.ProjEstablishPersonInfo;
import cn.zswltech.mithras.dto.projestablish.priceaoc.ProjEstablishAocPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricefactoring.ProjEstablishFactoringPriceRSP;
import cn.zswltech.mithras.dto.projestablish.pricelease.ProjEstablishLeasePriceRSP;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.dto.workbench.*;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.projectprocess.projlifecycle.enums.ProjStageEnum;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricRole;
import cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence.ClientProjLifecycleListParam;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishAocPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishFactoringPrice;
import cn.zswltech.mithras.projectprocess.model.projestablish.ProjEstablishLeasePrice;
import cn.zswltech.mithras.projectprocess.model.projreview.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyClientAuxiliaryLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishAocPriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishFactoringPriceService;
import cn.zswltech.mithras.projectprocess.application.projestablish.ProjEstablishLeasePriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projlifecycle.ProjectLifecycleService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyService;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEventBus;
import cn.zswltech.mithras.workbench.application.WorkbenchBarChartMetricService;
import cn.zswltech.mithras.workbench.application.WorkbenchFundsLiquidityMetricService;
import cn.zswltech.mithras.workbench.application.WorkbenchOverallReturnRateMetricService;
import cn.zswltech.mithras.workbench.application.WorkbenchChartMetricApplicationService;
import cn.zswltech.mithras.workbench.application.job.WorkbenchChartMetricService;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isEmpty;
import static cn.hutool.core.collection.CollUtil.isNotEmpty;

/**
 * @author zhaozhengkang
 * @description 工作台-柱状图指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchChartMetricFacade implements WorkbenchChartMetricApplicationService {
    private static final String WORKBENCH_CHART_METRIC_ROLE_CONFIG_KEY = "workbenchChartMetric2RoleConfig";
    @Resource
    private WorkbenchBarChartMetricService barChartMetricService;
    @Resource
    private WorkbenchFundsLiquidityMetricService fundsLiquidityMetricService;
    @Resource
    private WorkbenchOverallReturnRateMetricService overallReturnRateMetricService;
    @Resource
    private RiskControlStrategyService riskControlStrategyService;
    @Resource
    private SystemConfigService configService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ProjReviewAocPriceLibService reviewAocPriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService reviewFactoringPriceLibService;
    @Resource
    private ProjReviewLeasePriceLibService reviewLeasePriceLibService;
    @Resource
    private CommonVersionMapper versionMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;


    @Override
    public R<List<String>> tabs(WorkbenchMetricReq req) {
        Response<SystemConfigDO> config = configService.getConfig(WORKBENCH_CHART_METRIC_ROLE_CONFIG_KEY);
        if (config.isSuccess()) {
            String configValue = config.getData().getConfigValue();
            Map<String, List<String>> role2Tabs = JSON.parseObject(configValue, new TypeReference<Map<String, List<String>>>() {
            });
            return R.ok(role2Tabs.get(req.getCurrentRoleCode()));
        } else {
            throw new MithrasException("获取配置失败");
        }
    }

    @Override
    public R<LineBarChartValueVO> barChart(WorkbenchBarMetricReq req) {
        return R.ok(barChartMetricService.barChart(req));
    }

    @Override
    public R<LineBarChartValueVO> fundsLiquidityChart(WorkbenchMetricReq req) {
        return R.ok(fundsLiquidityMetricService.fundsLiquidityChartRealTime(req));
    }

    @Override
    public R<LineBarChartValueVO> deptReturnRateChart(WorkbenchMetricReq req) {
        return R.ok(overallReturnRateMetricService.deptReturnRateChart(req));
    }

    @Override
    public R<LineBarChartValueVO> projectTypeReturnRateChart(WorkbenchMetricReq req) {
        return R.ok(overallReturnRateMetricService.projectTypeReturnRateChart(req));
    }

    @Override
    public R<LineBarChartValueVO> stockPrincipalChart(WorkbenchMetricReq req) {
        return R.ok(riskControlStrategyService.stockPrincipalChart(req));
    }

    @Override
    public R<LineBarChartValueVO> releaseCollectionChart(WorkbenchMetricReq req) {
        if (!WorkbenchMetricRole.XMJL.name().equals(req.getCurrentRoleCode())) {
            throw new MithrasException("当前不是业务人员角色，无法查看");
        }

        LocalDate now = LocalDate.now();
        LocalDate start = now.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(6);
        LocalDate end = now.with(TemporalAdjusters.lastDayOfMonth()).plusMonths(6);
        Long userId = AccountUtil.getLoginInfo().getId();
        Set<Long> contractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                        .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.SETTLE.name(), ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name())))
                .stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        if (contractIds.isEmpty()) {
            throw new MithrasException("当前用户没有主办的业务");
        }
        List<CollectionBaseInfo> collections = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIds)
                .between(CollectionBaseInfo::getPlanCollectionDate, start, end)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name()));
        Map<String, List<CollectionBaseInfo>> monthGroup = collections.stream()
                .collect(Collectors.groupingBy(collectionBaseInfo ->
                        DateUtil.getMonthStr(collectionBaseInfo.getPlanCollectionDate())));
        List<ChartBaseDataVO> rentReceivables = new ArrayList<>();
        List<ChartBaseDataVO> actualRepayment = new ArrayList<>();
        List<ChartBaseDataVO> overdueAmounts = new ArrayList<>();
        monthGroup.forEach((month, collectionBaseInfos) -> {
            ChartBaseDataVO rentReceivable = new ChartBaseDataVO();
            rentReceivable.setName(month);
            rentReceivable.setUnitDisplay("万元");
            rentReceivable.setValue(collectionBaseInfos.stream()
                    .map(CollectionBaseInfo::getPlanCollectionAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            rentReceivables.add(rentReceivable);

            ChartBaseDataVO actualRepaymentData = new ChartBaseDataVO();
            actualRepaymentData.setName(month);
            rentReceivable.setUnitDisplay("万元");
            actualRepaymentData.setValue(collectionBaseInfos.stream()
                    .map(CollectionBaseInfo::getCollectionAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            actualRepayment.add(actualRepaymentData);

            ChartBaseDataVO overdueAmount = new ChartBaseDataVO();
            overdueAmount.setName(month);
            overdueAmount.setValue(collectionBaseInfos.stream()
                    .filter(CollectionBaseInfo::overdued)
                    .map(CollectionBaseInfo::getPlanCollectionAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toString());
            overdueAmounts.add(overdueAmount);
        });
        rentReceivables.sort(Comparator.comparing(ChartBaseDataVO::getName));
        actualRepayment.sort(Comparator.comparing(ChartBaseDataVO::getName));
        overdueAmounts.sort(Comparator.comparing(ChartBaseDataVO::getName));
        List<ChartDataVO> data = new ArrayList<>();
        ChartDataVO rentReceivableData = new ChartDataVO("应收租金", "line", rentReceivables);
        ChartDataVO actualRepaymentData = new ChartDataVO("实际回款", "line", actualRepayment);
        ChartDataVO overdueAmountData = new ChartDataVO("逾期金额", "line", overdueAmounts);
        data.add(rentReceivableData);
        data.add(actualRepaymentData);
        data.add(overdueAmountData);
        return R.ok(new LineBarChartValueVO("项目投放/回款情况", data));
    }

    @Override
    public R<List<ProjectVO>> reviewList(ProjectMetricReq req) {
        LocalDateTime startTime = req.getStartDateTime();
        if (req.getStartDateTime() == null) {
            LocalDateTime now = LocalDateTime.now();
            startTime = now.with(TemporalAdjusters.firstDayOfMonth());
        }
        List<CommonVersion> firstCreateIds = versionMapper.firstCreateVersion("PROJ_REVIEW", startTime);
        List<ProjectVO> rsp = new ArrayList<>();
        for (CommonVersion version : firstCreateIds) {
            ProjReviewBaseInfoLib one = projReviewBaseInfoLibService.getOne(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
                    .eq(ProjReviewBaseInfoLib::getVersion, version.getVersion())
                    .eq(ProjReviewBaseInfoLib::getOriginId, version.getMainId()));
            if (one == null) {
                continue;
            }
            ProjectVO vo = new ProjectVO();
            ProjectBizType projectBizType = ProjectBizType.valueOf(one.getBizType());
            switch (projectBizType) {
                case ZL:
                case ZZ:
                    ProjReviewLeasePriceLib leasePriceLib = reviewLeasePriceLibService.getOne(Wrappers.<ProjReviewLeasePriceLib>lambdaQuery()
                            .eq(ProjReviewLeasePriceLib::getVersion, version.getVersion())
                            .eq(ProjReviewLeasePrice::getProjectId, version.getMainId()));
                    vo.setApplyCreditAmount(leasePriceLib.getApplyCreditAmount().toString());
                    break;
                case BL:
                    ProjReviewFactoringPriceLib factoringPriceLib = reviewFactoringPriceLibService.getOne(Wrappers.<ProjReviewFactoringPriceLib>lambdaQuery()
                            .eq(ProjReviewFactoringPriceLib::getVersion, version.getVersion())
                            .eq(ProjReviewFactoringPrice::getProjectId, version.getMainId()));
                    vo.setApplyCreditAmount(factoringPriceLib.getApplyCreditAmount().toString());
                    break;
                case ZR:
                    ProjReviewAocPriceLib aocPriceLib = reviewAocPriceLibService.getOne(Wrappers.<ProjReviewAocPriceLib>lambdaQuery()
                            .eq(ProjReviewAocPriceLib::getVersion, version.getVersion())
                            .eq(ProjReviewAocPrice::getProjectId, version.getMainId()));
                    vo.setApplyCreditAmount(aocPriceLib.getApplyCreditAmount().toString());
                    break;
                default:
                    break;
            }
            vo.setProjectId(one.getOriginId());
            vo.setProjectName(one.getProjName());
            vo.setDeptName(one.getBizDeptId().toString());
            vo.setBizType(one.getBizType());
            vo.setProjectCoOrganizer(one.getProjCosponsorUserIds());
            vo.setProjectOrganizer(one.getProjSponsorUserId().toString());
            vo.setApproveTime(version.getCreateTime());
            vo.setCreateTime(one.getCreateTime());
            rsp.add(vo);
        }
        List<Long> userIds = new ArrayList<>();
        List<Long> deptIds = new ArrayList<>();
        rsp.stream().forEach(projectVO -> {
            userIds.add(Long.valueOf(projectVO.getProjectOrganizer()));
            if (ObjectUtil.isNotEmpty(projectVO.getProjectCoOrganizer())) {
                userIds.addAll(JSON.parseObject(projectVO.getProjectCoOrganizer(), new TypeReference<List<Long>>() {
                }));
            }
            deptIds.add(Long.valueOf(projectVO.getDeptName()));
        });
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);

        for (ProjectVO projectVO : rsp) {
            if (ObjectUtil.isNotEmpty(projectVO.getProjectCoOrganizer())) {
                List<String> coNames = JSON.parseObject(projectVO.getProjectCoOrganizer(), new TypeReference<List<Long>>() {
                }).stream().map(userId2Name::get).collect(Collectors.toList());
                projectVO.setProjectCoOrganizer(String.join(",", coNames));
            }
            projectVO.setBizType(ProjectBizType.valueOf(projectVO.getBizType()).display());
            projectVO.setProjectOrganizer(userId2Name.get(Long.valueOf(projectVO.getProjectOrganizer())));
            projectVO.setDeptName(deptId2Name.get(Long.valueOf(projectVO.getDeptName())));
            BigDecimal amount = new BigDecimal(projectVO.getApplyCreditAmount()).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP);
            projectVO.setApplyCreditAmount(String.valueOf(amount));
        }
        rsp.sort(Comparator.comparing(ProjectVO::getCreateTime).reversed());
        return R.ok(rsp);
    }

    @Override
    public R<PageR<PaymentDetailRsp>> launchList(@Valid ProjectMetricReq req) {
        // 合同编号 项目名称 类别 业务部门 投放金额 应付日期 实付日期
        Page<PaymentActualDetail> detailPage = paymentActualDetailService.page(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .ge(PaymentActualDetail::getPaidInDate, req.getStartDateTime())
                        .orderByDesc(BaseModel::getCreateTime));
        if (detailPage.getRecords().isEmpty()) {
            return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }
        Set<Long> contractIds = detailPage.getRecords().stream()
                .map(PaymentActualDetail::getContractId).collect(Collectors.toSet());
        Map<Long, ContractBaseInfo> contractMap = contractBaseInfoService.list(
                        Wrappers.<ContractBaseInfo>lambdaQuery()
                                .in(ContractBaseInfo::getId, contractIds)).stream()
                .collect(Collectors.toMap(ContractBaseInfo::getId, v -> v));
        Set<Long> deptIds = contractMap.values().stream().map(ContractBaseInfo::getBizDeptId).collect(Collectors.toSet());
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);

        Map<Long, PaymentBaseInfo> paymentMap = paymentBaseInfoService.list(
                        Wrappers.<PaymentBaseInfo>lambdaQuery()
                                .in(PaymentBaseInfo::getId, detailPage.getRecords().stream()
                                        .map(PaymentActualDetail::getPaymentId).collect(Collectors.toSet())))
                .stream().collect(Collectors.toMap(PaymentBaseInfo::getId, v -> v));

        Map<Long, Long> remainingAmount = paymentActualDetailService.remainingAmount(contractIds);

        List<PaymentDetailRsp> rspList = new ArrayList<>();
        for (PaymentActualDetail record : detailPage.getRecords()) {
            ContractBaseInfo contract = contractMap.get(record.getContractId());
            PaymentDetailRsp rsp = new PaymentDetailRsp();
            rsp.setProjectName(contract.getProjName());
            rsp.setProjectId(contract.getProjReviewId());
            rsp.setContractId(contract.getId());
            rsp.setContractCode(contract.getContractCode());
            rsp.setPaidAmount(record.getPaidInAmount());
            rsp.setPaidInDate(record.getPaidInDate());
            rsp.setBizDeptName(deptId2Name.get(contract.getBizDeptId()));
            rsp.setBizType(ProjectBizType.valueOf(contract.getBizType()).display());
            rsp.setPlanPayDate(paymentMap.get(record.getPaymentId()).getApplyPaymentDate().toLocalDate());
            rsp.setRemainAmount(remainingAmount.get(record.getContractId()));
            rspList.add(rsp);
        }
        return R.ok(PageR.of(detailPage, rspList));
    }


    /******************************** 以下为指标卡片联动接口********************************/

    @Resource
    private ProjectLifecycleService projectLifecycleService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ClientService clientService;
    @Resource
    private SysUserService sysUserService;

    @Override
    public R<PageR<ClientProjectListRSP>> clientList(CardListReq req) {
        LocalDate startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDateTime startTime = startDay.atStartOfDay();
        Long userId = AccountUtil.getLoginInfo().getId();
        // 获取今年所有有实际投放的客户Id
        List<PaymentActualDetail> paymentActualDetails = paymentActualDetailService.writtenOffDetailsByClientIds(null, startDay, null);
        if (isEmpty(paymentActualDetails)){
            R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }
        List<Long> actualPaidClientIds = paymentActualDetails.stream().map(PaymentActualDetail::getClientId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<Client> qw = Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientStatus, RecordStatus.TAKE_EFFECT.name())
                .in(Client::getId, actualPaidClientIds)
                .ge(BaseModel::getCreateTime, startTime);
        if (Objects.equals(req.getCurrentRoleCode(), WorkbenchMetricRole.XMJL.name())) {
            qw.eq(Client::getBelongSponsorId, userId);
        } else if (req.getCurrentRoleCode().endsWith("_BDS")) {
            Long deptId = sysUserService.getOrgIdByCode(req.getCurrentRoleCode().split("_")[0]);
            qw.eq(Client::getBelongDeptId, deptId);
        }
        List<Client> clients = clientService.list(qw);
        if (isNotEmpty(clients)) {
            ClientProjLifecycleListParam param = new ClientProjLifecycleListParam();
            param.setClientIds(clients.stream().map(Client::getId).collect(Collectors.toList()));
            param.setPage(req.getPage());
            param.setPageSize(req.getPageSize());
            return R.ok(projectLifecycleService.workbenchClientList(param));
        }
        return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
    }

    @Resource
    private ProjEstablishBaseInfoService projEstablishBaseInfoService;
    @Resource
    private ProjEstablishFactoringPriceService projEstablishFactoringPriceService;
    @Resource
    private ProjEstablishLeasePriceService projEstablishLeasePriceService;
    @Resource
    private ProjEstablishAocPriceService projEstablishAocPriceService;

    @Override
    public R<PageR<NewProjectListRSP>> projList(@Valid CardListReq req) {
        LocalDate startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDateTime startTime = startDay.atStartOfDay();
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()), LocalTime.MAX);
        if (!req.getIsYear()) {
            startTime = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            endTime = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
        }
        Long userId = AccountUtil.getLoginInfo().getId();

        LambdaQueryWrapper<ProjEstablishBaseInfo> qw = Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, RecordStatus.TAKE_EFFECT.name())
                .ge(BaseModel::getCreateTime, startTime)
                .le(BaseModel::getCreateTime, endTime);
        if (Objects.equals(req.getCurrentRoleCode(), WorkbenchMetricRole.XMJL.name())) {
            qw.eq(ProjEstablishBaseInfo::getProjSponsorUserId, userId);
        } else if (req.getCurrentRoleCode().endsWith("_BDS")) {
            Long deptId = sysUserService.getOrgIdByCode(req.getCurrentRoleCode().split("_")[0]);
            qw.eq(ProjEstablishBaseInfo::getBizDeptId, deptId);
        }
        Page<ProjEstablishBaseInfo> establishBaseInfoPage = projEstablishBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()), qw);
        List<ProjEstablishBaseInfo> records = establishBaseInfoPage.getRecords();
        if (isNotEmpty(records)) {
            List<Long> ids = records.stream().map(ProjEstablishBaseInfo::getId).collect(Collectors.toList());
            Map<Long, ProjEstablishPriceDetailRSP> projPriceMap = getProjPriceMap(ids);
            Set<Long> clientIds = new HashSet<>();
            Set<Long> sysUserIds = new HashSet<>();
            Set<Long> deptIds = new HashSet<>();
            Set<Long> lesseeIds = new HashSet<>();
            for (ProjEstablishBaseInfo record : records) {
                sysUserIds.add(record.getProjSponsorUserId());
                clientIds.add(record.getClientId());
                deptIds.add(record.getBizDeptId());
                List<ProjEstablishPersonInfo> res = JSON.parseArray(record.getLesseeInfo(), ProjEstablishPersonInfo.class);
                if (isNotEmpty(res)) {
                    lesseeIds.add(res.get(0).getClientId());
                }
            }
            Map<Long, Client> clientMap = new HashMap<>();
            if (isNotEmpty(clientIds)) {
                clientMap = clientService.getBaseMapper().selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getId, clientIds)
                ).stream().collect(Collectors.toMap(Client::getId, o -> o));
            }
            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
            Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
            Map<Long, Long> riskExposureMap = contractBaseInfoService.getStockRiskExposureByClients(lesseeIds);
            List<NewProjectListRSP> rsps = new ArrayList<>();
            for (ProjEstablishBaseInfo record : records) {
                NewProjectListRSP rsp = new NewProjectListRSP();
                rsp.setProjectId(record.getId());
                rsp.setProjectName(record.getProjName());
                rsp.setClientId(record.getClientId());
                Client client = clientMap.get(record.getClientId());
                if (client != null) {
                    rsp.setClientName(client.getClientName());
                    rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                    rsp.setClientType(client.getClientType());
                }
                rsp.setBizType(record.getBizType());
                rsp.setProjSponsorUserId(record.getProjSponsorUserId());
                rsp.setProjSponsorUserName(sysUserMap.get(record.getProjSponsorUserId()));
                rsp.setBizDeptName(deptMap.get(record.getBizDeptId()));
                ProjEstablishPriceDetailRSP projEstablishPriceDetailRSP = projPriceMap.get(record.getId());
                if (projEstablishPriceDetailRSP != null) {
                    rsp.setApplyCreditAmount(LongUtil.null2zero(projEstablishPriceDetailRSP.getDeclaredAmount()));
                }else {
                    rsp.setApplyCreditAmount(0L);
                }
                List<ProjEstablishPersonInfo> res = JSON.parseArray(record.getLesseeInfo(), ProjEstablishPersonInfo.class);
                if (isNotEmpty(res)) {
                    rsp.setStockRiskExposure(LongUtil.null2zero(riskExposureMap.get(res.get(0).getClientId())));
                }else {
                    rsp.setStockRiskExposure(0L);
                }
                rsps.add(rsp);
            }
            return R.ok(PageR.of(rsps, establishBaseInfoPage.getTotal(),
                    establishBaseInfoPage.getPages(),
                    establishBaseInfoPage.getCurrent(),
                    establishBaseInfoPage.getSize()));
        }
        return R.ok(PageR.empty(req.getPage(),req.getPageSize()));
    }

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    @Override
    public R<PageR<NewReviewListRSP>> reviewList(@Valid CardListReq req) {
        LocalDate startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDateTime startTime = startDay.atStartOfDay();
        Long userId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<ProjReviewBaseInfo> qw = Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .ne(ProjReviewBaseInfo::getProjReviewProcessStatus, "NEW_UN_SUBMIT")
                .ge(BaseModel::getCreateTime, startTime);
        if (Objects.equals(req.getCurrentRoleCode(), WorkbenchMetricRole.XMJL.name())) {
            qw.eq(ProjReviewBaseInfo::getProjSponsorUserId, userId);
        } else if (req.getCurrentRoleCode().endsWith("_BDS")) {
            Long deptId = sysUserService.getOrgIdByCode(req.getCurrentRoleCode().split("_")[0]);
            qw.eq(ProjReviewBaseInfo::getBizDeptId, deptId);
        }
        Page<ProjReviewBaseInfo> reviewBaseInfoPage = projReviewBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()), qw);
        List<ProjReviewBaseInfo> records = reviewBaseInfoPage.getRecords();
        if (isNotEmpty(records)) {
            List<Long> ids = records.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
            Map<Long, ProjReviewPriceDetailRSP> reviewPriceMap = projectLifecycleService.getReviewPriceMap(ids,false);
            Set<Long> clientIds = new HashSet<>();
            Set<Long> sysUserIds = new HashSet<>();
            Set<Long> deptIds = new HashSet<>();
            Set<Long> lesseeIds = new HashSet<>();
            for (ProjReviewBaseInfo record : records) {
                sysUserIds.add(record.getProjSponsorUserId());
                clientIds.add(record.getClientId());
                deptIds.add(record.getBizDeptId());
                List<ProjEstablishPersonInfo> res = JSON.parseArray(record.getLesseeInfo(), ProjEstablishPersonInfo.class);
                if (isNotEmpty(res)) {
                    lesseeIds.add(res.get(0).getClientId());
                }
            }
            Map<Long, Client> clientMap = new HashMap<>();
            if (isNotEmpty(clientIds)) {
                clientMap = clientService.getBaseMapper().selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getId, clientIds)
                ).stream().collect(Collectors.toMap(Client::getId, o -> o));
            }            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
            Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
            Map<Long, Long> riskExposureMap = contractBaseInfoService.getStockRiskExposureByClients(lesseeIds);
            List<NewReviewListRSP> rsps = new ArrayList<>();
            for (ProjReviewBaseInfo record : records) {
                NewReviewListRSP rsp = new NewReviewListRSP();
                rsp.setProjectId(record.getId());
                rsp.setProjectName(record.getProjName());
                rsp.setClientId(record.getClientId());
                Client client = clientMap.get(record.getClientId());
                if (client != null) {
                    rsp.setClientName(client.getClientName());
                    rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                    rsp.setClientType(client.getClientType());
                }
                rsp.setBizType(record.getBizType());
                rsp.setProjSponsorUserId(record.getProjSponsorUserId());
                rsp.setProjSponsorUserName(sysUserMap.get(record.getProjSponsorUserId()));
                rsp.setBizDeptName(deptMap.get(record.getBizDeptId()));
                ProjReviewPriceDetailRSP projReviewPriceDetailRSP = reviewPriceMap.get(record.getId());
                if (projReviewPriceDetailRSP != null) {
                    rsp.setApplyCreditAmount(LongUtil.null2zero(projReviewPriceDetailRSP.getApplyCreditAmount()));
                }else {
                    rsp.setApplyCreditAmount(0L);
                }
                List<ProjEstablishPersonInfo> res = JSON.parseArray(record.getLesseeInfo(), ProjEstablishPersonInfo.class);
                if (isNotEmpty(res)) {
                    rsp.setStockRiskExposure(LongUtil.null2zero(riskExposureMap.get(res.get(0).getClientId())));
                }else {
                    rsp.setStockRiskExposure(0L);
                }
                rsps.add(rsp);
            }
            return R.ok(PageR.of(rsps, reviewBaseInfoPage.getTotal(),
                    reviewBaseInfoPage.getPages(),
                    reviewBaseInfoPage.getCurrent(),
                    reviewBaseInfoPage.getSize()));
        }
        return R.ok(PageR.empty(req.getPage(),req.getPageSize()));
    }

    @Override
    public R<PageR<NewPaymentListRSP>> paymentList(@Valid CardListReq req) {
        LocalDate startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate endTime = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        if (!req.getIsYear()) {
            startDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
            endTime = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        }
        Long userId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<ContractBaseInfo> qw = Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractStatus, ListUtil.toList(ContractStatus.TAKE_EFFECT.name(), ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()));
        if (Objects.equals(req.getCurrentRoleCode(), WorkbenchMetricRole.XMJL.name())) {
            qw.eq(ContractBaseInfo::getProjSponsorUserId, userId);
            //项目经理查看自己所属合同
        } else if (req.getCurrentRoleCode().endsWith("_BDS")) {
            Long deptId = sysUserService.getOrgIdByCode(req.getCurrentRoleCode().split("_")[0]);
            qw.eq(ContractBaseInfo::getBizDeptId, deptId);
            //部门领导查看部门所属合同
        }
        Set<Long> targetContractIds = contractBaseInfoService.list(qw).stream()
                .map(ContractBaseInfo::getId).collect(Collectors.toSet());
        List<PaymentActualDetail> actualDetails = paymentActualDetailService.writtenOffDetailsByContractIds(targetContractIds, startDay, endTime);
        if (isNotEmpty(actualDetails)) {
            Set<Long> contractIds = actualDetails.stream().map(PaymentActualDetail::getContractId).collect(Collectors.toSet());
            Page<ContractBaseInfo> contractBaseInfoPage = contractBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getId, contractIds));
            List<ContractBaseInfo> records = contractBaseInfoPage.getRecords();
            if (isNotEmpty(records)) {
                List<Long> ids = records.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                Map<Long, ContractPriceDetailRSP> contractPriceMap = projectLifecycleService.getContractPriceMap(ids);
                Set<Long> clientIds = new HashSet<>();
                Set<Long> sysUserIds = new HashSet<>();
                Set<Long> deptIds = new HashSet<>();
                for (ContractBaseInfo record : records) {
                    sysUserIds.add(record.getProjSponsorUserId());
                    clientIds.add(record.getClientId());
                    deptIds.add(record.getBizDeptId());
                }
                Map<Long, Client> clientMap = new HashMap<>();
                if (isNotEmpty(clientIds)) {
                    clientMap = clientService.getBaseMapper().selectList(Wrappers.<Client>lambdaQuery()
                            .in(Client::getId, clientIds)
                    ).stream().collect(Collectors.toMap(Client::getId, o -> o));
                }                Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
                Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
                List<NewPaymentListRSP> rsps = new ArrayList<>();
                for (ContractBaseInfo record : records) {
                    NewPaymentListRSP rsp = new NewPaymentListRSP();
                    rsp.setContractId(record.getId());
                    rsp.setContractCode(record.getContractCode());
                    rsp.setProjectId(record.getProjReviewId());
                    rsp.setProjectName(record.getProjName());
                    rsp.setClientId(record.getClientId());
                    Client client = clientMap.get(record.getClientId());
                    if (client != null) {
                        rsp.setClientName(client.getClientName());
                        rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                        rsp.setClientType(client.getClientType());
                    }
                    rsp.setBizType(record.getBizType());
                    rsp.setProjSponsorUserId(record.getProjSponsorUserId());
                    rsp.setProjSponsorUserName(sysUserMap.get(record.getProjSponsorUserId()));
                    rsp.setBizDeptName(deptMap.get(record.getBizDeptId()));
                    ContractPriceDetailRSP priceDetailRSP = contractPriceMap.get(record.getId());
                    if (priceDetailRSP != null) {
                        rsp.setContractAmount(priceDetailRSP.getApplyCreditAmount());
                    }else {
                        rsp.setContractAmount(0L);
                    }
                    rsp.setContractStatus(record.getContractStatus());
                    rsp.setActualLeaseDate(record.getActualLeaseDate());
                    rsps.add(rsp);
                }
                return R.ok(PageR.of(rsps, contractBaseInfoPage.getTotal(),
                        contractBaseInfoPage.getPages(),
                        contractBaseInfoPage.getCurrent(),
                        contractBaseInfoPage.getSize()));
            }
        }
            return R.ok(PageR.empty(req.getPage(),req.getPageSize()));
    }

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public R<PageR<ProjInfoListRSP>> overdueList(@Valid CardListReq req) {
        Long userId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<Client> qw = Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientStatus, RecordStatus.TAKE_EFFECT.name());
        if (Objects.equals(req.getCurrentRoleCode(), WorkbenchMetricRole.XMJL.name())) {
            qw.eq(Client::getBelongSponsorId, userId);
        } else if (req.getCurrentRoleCode().endsWith("_BDS")) {
            Long deptId = sysUserService.getOrgIdByCode(req.getCurrentRoleCode().split("_")[0]);
            qw.eq(Client::getBelongDeptId, deptId);
        }
        Set<Long> targetClientIds = clientService.list(qw).stream()
                .map(Client::getId).collect(Collectors.toSet());
        List<CollectionBaseInfo> overdueRecord = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now())
                .in(CollectionBaseInfo::getClientId, targetClientIds)
                .isNull(CollectionBaseInfo::getCollectionDate)
                .eq(CollectionBaseInfo::getCashFlowItem, "RENT"));
        List<Long> contractId = overdueRecord.stream().map(CollectionBaseInfo::getContractId).distinct().collect(Collectors.toList());
        if (contractId.isEmpty()) {
            return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getId, contractId));
        Set<Long> projReviewIds = contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toSet());
        Page<ProjReviewBaseInfo> projReviewBaseInfoPage = projReviewBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, projReviewIds));
        List<ProjReviewBaseInfo> records = projReviewBaseInfoPage.getRecords();
        List<ProjInfoListRSP> rsps = proj2Rsps(records, contractBaseInfos);
        return R.ok(PageR.of(rsps, projReviewBaseInfoPage.getTotal(),
                projReviewBaseInfoPage.getPages(),
                projReviewBaseInfoPage.getCurrent(),
                projReviewBaseInfoPage.getSize()));
    }

    private List<ProjInfoListRSP> proj2Rsps(List<ProjReviewBaseInfo> records,List<ContractBaseInfo> contractBaseInfos){
        List<Long> contractId = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        Map<Long, List<ContractBaseInfo>> rcMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        Map<Long, ContractPriceDetailRSP> contractPriceMap = projectLifecycleService.getContractPriceMap(contractId);
        Map<Long, Long> marginBalancesMap = marginBaseInfoService.getMarginBalances(contractId);
        List<ProjInfoListRSP> rsps = new ArrayList<>();
        if (isNotEmpty(records)) {
            Set<Long> clientIds = new HashSet<>();
            Set<Long> sysUserIds = new HashSet<>();
            Set<Long> deptIds = new HashSet<>();
            for (ProjReviewBaseInfo record : records) {
                sysUserIds.add(record.getProjSponsorUserId());
                clientIds.add(record.getClientId());
                deptIds.add(record.getBizDeptId());
            }
            Map<Long, Client> clientMap = new HashMap<>();
            if (isNotEmpty(clientIds)) {
                clientMap = clientService.getBaseMapper().selectList(Wrappers.<Client>lambdaQuery()
                        .in(Client::getId, clientIds)
                ).stream().collect(Collectors.toMap(Client::getId, o -> o));
            }            Map<Long, String> sysUserMap = id2NameService.sysUserId2Name(sysUserIds);
            Map<Long, String> deptMap = id2NameService.deptId2Name(deptIds);
            for (ProjReviewBaseInfo record : records) {
                ProjInfoListRSP rsp = new ProjInfoListRSP();
                rsp.setProjectId(record.getId());
                rsp.setProjectName(record.getProjName());
                rsp.setClientId(record.getClientId());
                Client client = clientMap.get(record.getClientId());
                if (client != null) {
                    rsp.setClientName(client.getClientName());
                    rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                    rsp.setClientType(client.getClientType());
                }
                rsp.setBizType(record.getBizType());
                rsp.setProjSponsorUserId(record.getProjSponsorUserId());
                rsp.setProjSponsorUserName(sysUserMap.get(record.getProjSponsorUserId()));
                rsp.setBizDeptName(deptMap.get(record.getBizDeptId()));
                List<ContractBaseInfo> infos = rcMap.get(record.getId());
                if (isNotEmpty(infos)) {
                    List<Long> contractIds = infos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                    List<PaymentActualDetail> actualDetails = paymentActualDetailService.getBaseMapper().selectList(Wrappers.<PaymentActualDetail>lambdaQuery().eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()).in(PaymentActualDetail::getContractId, contractIds));
                    Map<Long, List<PaymentActualDetail>> casMap = new HashMap<>();
                    if (isNotEmpty(actualDetails)) {
                        casMap = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
                    }
                    List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.getBaseMapper().selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getCashFlowItem, ListUtil.of(CashFlowItemEnum.FIRST_RENT.name(), CashFlowItemEnum.RENT.name())).in(CollectionBaseInfo::getContractId, contractIds));
                    Map<Long, List<CollectionBaseInfo>> ccsMap = new HashMap<>();
                    if (isNotEmpty(collectionBaseInfos)) {
                        ccsMap = collectionBaseInfos.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
                    }
                    if (isNotEmpty(infos)) {
                        long margin = 0L;
                        for (ContractBaseInfo info : infos) {
                            ContractPriceDetailRSP priceDetailRSP = contractPriceMap.get(info.getId());
                            if (priceDetailRSP != null) {
                                rsp.setContractAmount(LongUtil.null2zero(rsp.getContractAmount()) + LongUtil.null2zero(priceDetailRSP.getApplyCreditAmount()));
                            }else {
                                rsp.setContractAmount(LongUtil.null2zero(rsp.getContractAmount()));
                            }
                            List<PaymentActualDetail> actualDetails1 = casMap.get(info.getId());
                            if (isNotEmpty(actualDetails1)) {
                                for (PaymentActualDetail paymentActualDetail : actualDetails1) {
                                    rsp.setLastPrincipal(LongUtil.null2zero(rsp.getLastPrincipal()) + LongUtil.null2zero(paymentActualDetail.getPaidInAmount()));
                                }
                            }else {
                                rsp.setLastPrincipal(LongUtil.null2zero(rsp.getLastPrincipal()));
                            }
                            List<CollectionBaseInfo> collectionBaseInfos1 = ccsMap.get(info.getId());
                            if (isNotEmpty(collectionBaseInfos1)) {
                                for (CollectionBaseInfo baseInfo : collectionBaseInfos1) {
                                    Long amount;
                                    if (CashFlowItemEnum.FIRST_RENT.name().equals(baseInfo.getCashFlowItem())) {
                                        amount = baseInfo.getCollectionAmount();
                                    } else {
                                        amount = baseInfo.getCollectionPrincipal();
                                    }
                                    rsp.setLastPrincipal(LongUtil.null2zero(rsp.getLastPrincipal()) - LongUtil.null2zero(amount));
                                }
                            }
                            margin += LongUtil.null2zero(marginBalancesMap.get(info.getId()));
                        }
                        rsp.setStockRiskExposure(LongUtil.null2zero(rsp.getLastPrincipal()) - margin);
                    }
                }else {
                    rsp.setContractAmount(0L);
                    rsp.setLastPrincipal(0L);
                    rsp.setStockRiskExposure(0L);
                }
                rsps.add(rsp);
            }
        }
        return rsps;
    }
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private AssetClassifyClientAuxiliaryLibService clientLibService;
    private final Set<String> lastThree = AssetClassifyResultEnum.lastThree();

    @Override
    public R<PageR<ProjInfoListRSP>> undesirableList(@Valid CardListReq req) {
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify();
        if (!assetClassify.isPresent()) {
            return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }
        Set<Long> lastThreeClientIds = clientLibService
                .newestClassifyClientLib(assetClassify.get().getId())
                .stream().filter(lib -> lastThree.contains(lib.getClassifyResult()))
                .map(AssetClassifyClient::getClientId).collect(Collectors.toSet());
        if (lastThreeClientIds.isEmpty()) {
            return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }
        Page<ProjReviewBaseInfo> projReviewBaseInfoPage = projReviewBaseInfoService.page(new Page<>(req.getPage(), req.getPageSize()),Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT")
                .in(ProjReviewBaseInfo::getClientId, lastThreeClientIds));
        List<ProjReviewBaseInfo> records = projReviewBaseInfoPage.getRecords();
        if (isNotEmpty(records)) {
            List<Long> ids = records.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().notIn(ContractBaseInfo::getContractStatus, ListUtil.of(ContractStatus.CLOSED.name(), ContractStatus.INVALID.name())).in(ContractBaseInfo::getProjReviewId, ids));
            List<ProjInfoListRSP> rsps = proj2Rsps(records, contractBaseInfos);
            return R.ok(PageR.of(rsps, projReviewBaseInfoPage.getTotal(),
                    projReviewBaseInfoPage.getPages(),
                    projReviewBaseInfoPage.getCurrent(),
                    projReviewBaseInfoPage.getSize()));
        }
        return R.ok(PageR.empty(req.getPage(),req.getPageSize()));
    }

    public Map<Long, ProjEstablishPriceDetailRSP> getProjPriceMap(List<Long> projEstablishIds) {
        Set<Long> ids = new HashSet<>(projEstablishIds);
        Map<Long,ProjEstablishPriceDetailRSP> map = new HashMap<>();
        // 处理租赁
        List<ProjEstablishLeasePrice> projEstablishLeasePrice = projEstablishLeasePriceService.list(Wrappers.<ProjEstablishLeasePrice>lambdaQuery().in(ProjEstablishLeasePrice::getProjEstablishId,ids));
        if (isNotEmpty(projEstablishLeasePrice)) {
            for (ProjEstablishLeasePrice establishLeasePrice : projEstablishLeasePrice) {
                ProjEstablishPriceDetailRSP rsp = new ProjEstablishPriceDetailRSP();
                rsp.setLeasePriceRSP(BeanUtil.toBean(establishLeasePrice, ProjEstablishLeasePriceRSP.class));
                map.put(establishLeasePrice.getProjEstablishId(), rsp);
            }
        }
        // 处理保理
        List<ProjEstablishFactoringPrice> projEstablishFactoringPrices = projEstablishFactoringPriceService.list(Wrappers.<ProjEstablishFactoringPrice>lambdaQuery().in(ProjEstablishFactoringPrice::getProjEstablishId,ids));
        if (isNotEmpty(projEstablishFactoringPrices)) {
            for (ProjEstablishFactoringPrice establishFactoringPrice : projEstablishFactoringPrices) {
                ProjEstablishPriceDetailRSP rsp = new ProjEstablishPriceDetailRSP();
                rsp.setFactoringPriceRSP(BeanUtil.toBean(establishFactoringPrice, ProjEstablishFactoringPriceRSP.class));
                map.put(establishFactoringPrice.getProjEstablishId(), rsp);
            }
        }
        // 处理债权转让
        List<ProjEstablishAocPrice> projEstablishAocPrices = projEstablishAocPriceService.list(Wrappers.<ProjEstablishAocPrice>lambdaQuery().in(ProjEstablishAocPrice::getProjEstablishId,ids));
        if (isNotEmpty(projEstablishAocPrices)) {
            for (ProjEstablishAocPrice establishAocPrice : projEstablishAocPrices) {
                ProjEstablishPriceDetailRSP rsp = new ProjEstablishPriceDetailRSP();
                rsp.setAocPriceRSP(BeanUtil.toBean(establishAocPrice, ProjEstablishAocPriceRSP.class));
                map.put(establishAocPrice.getProjEstablishId(), rsp);
            }
        }
        return map;
    }


    @Resource
    private WorkbenchChartMetricService workbenchChartMetricService;
    @Resource
    private MetricComputeEventBus metricComputeEventBus;

    @Override
    public void testCalculate() {
//        workbenchChartMetricService.workbenchMetricJobHandler();
        MetricComputeEvent metricComputeEvent = new MetricComputeEvent();
        metricComputeEvent.setSnapshotDate(LocalDate.now());
        metricComputeEventBus.post(metricComputeEvent);
    }

}
