package cn.zswltech.mithras.service.controller.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.entity.SystemConfigDO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.workbench.WorkbenchLittleChartApi;
import cn.zswltech.mithras.dto.workbench.ProjectMetricReq;
import cn.zswltech.mithras.dto.workbench.ProjectVO;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.PieChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.RadarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.PieDataVO;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.assetclassify.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.service.mapper.lib.fund.financing.FinancingQueryDto;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassify;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.projestablish.*;
import cn.zswltech.mithras.service.mapper.model.projreview.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.fund.financing.FundFinancingBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.fund.financing.FundFinancingPlanLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishLeasePriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.service.service.workbench.WorkbenchRadarChartMetricService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/10 14:22
 */
@RestController
public class WorkbenchLittleChartController implements WorkbenchLittleChartApi {

    private static final String WORKBENCH_CHART_METRIC_ROLE_CONFIG_KEY = "workbenchLittleChart2RoleConfig";
    @Resource
    private SystemConfigService configService;
    @Resource
    private WorkbenchRadarChartMetricService radarChartMetricService;
    @Resource
    private FundFinancingBaseInfoLibService financingBaseInfoLibService;
    @Resource
    private FundFinancingPlanLibService planLibService;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private CommonVersionMapper versionMapper;
    @Resource
    private ProjEstablishBaseInfoLibService projEstablishBaseInfoLibService;
    @Resource
    private ProjEstablishAocPriceLibService aocPriceLibService;
    @Resource
    private ProjEstablishFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjEstablishLeasePriceLibService leasePriceLibService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ProjReviewBaseInfoLibService projReviewBaseInfoLibService;
    @Resource
    private ProjReviewAocPriceLibService reviewAocPriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService reviewFactoringPriceLibService;
    @Resource
    private ProjReviewLeasePriceLibService reviewLeasePriceLibService;

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
    public R<RadarChartValueVO> radarChart(WorkbenchMetricReq req) {
        return R.ok(radarChartMetricService.radarChart(req));
    }

    @Override
    public R<PieChartValueVO> fiveLevelPieChart(WorkbenchMetricReq req) {
        Optional<AssetClassify> assetClassify = assetClassifyService.currentClassify();
        if (!assetClassify.isPresent()) {
            throw new MithrasException("当前暂无资产分类数据");
        }
        Map<String, List<AssetClassifyClientAuxiliaryLib>> assetResultGroup = assetClassifyClientAuxiliaryLibMapper
                .newestClassifyClientLib(assetClassify.get().getId()).stream()
                .collect(Collectors.groupingBy(AssetClassifyClient::getClassifyResult));
        List<PieDataVO> data = new ArrayList<>();
        assetResultGroup.forEach((result, libs) -> {
            String resultDis = AssetClassifyResultEnum.valueOf(result).display();
            Set<Long> clientIds = libs.stream()
                    .map(AssetClassifyClientAuxiliaryLib::getClientId).collect(Collectors.toSet());
            BigDecimal thisTotalAmount = contractBaseInfoLibService.newestContractAmountClientId(clientIds);
            data.add(new PieDataVO(resultDis, thisTotalAmount.divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString(), null));
        });
        return R.ok(new PieChartValueVO("五级分类饼图", data));
    }

    @Override
    public R<PieChartValueVO> financeCostPieChart(WorkbenchMetricReq req) {
        List<FundFinancingBaseInfoLib> finances = financingBaseInfoLibService.queryLastestVersionLibs(new FinancingQueryDto());
        Set<Long> financeIds = finances.stream().map(FundFinancingBaseInfoLib::getOriginId).collect(Collectors.toSet());
        Map<Long, Integer> costs = planLibService.queryLastestVersionLibs(financeIds)
                .stream()
                .filter(lib -> lib.getComprehensiveInterestRate() != null).collect(Collectors.toMap(FundFinancingPlan::getFinancingId, FundFinancingPlan::getComprehensiveInterestRate, (a, b) -> a));


        Map<String, List<FundFinancingBaseInfoLib>> typeGroup = finances.stream()
                .filter(lib -> !"ABS".equals(lib.getBusinessType()))
                .collect(Collectors.groupingBy(lib -> String.join("-", FundFinancingTimeLimitTypeEnum.valueOf(lib.getTimeLimitType()).display(), FundFinancingBizTypeEnum.valueOf(lib.getBusinessType()).display())));
        List<PieDataVO> data = new ArrayList<>();
        typeGroup.forEach((typeName, libs) -> {
            BigDecimal thisTotal = libs.stream().map(FundFinancingBaseInfo::getFinancingAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal averageCost = BigDecimal.ZERO;
            for (FundFinancingBaseInfoLib financing : libs) {
                Integer cost = costs.get(financing.getOriginId());
                if (cost == null) {
                    continue;
                }
                averageCost = averageCost.add(new BigDecimal(financing.getFinancingAmount())
                        .divide(thisTotal, 10, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(cost)));
            }
            data.add(new PieDataVO(typeName, thisTotal.divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString(), averageCost.divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP) + "%"));
        });
        return R.ok(new PieChartValueVO("融资成本饼图", data));
    }


    @Override
    public R<List<ProjectVO>> projEstablishList(ProjectMetricReq req) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.with(TemporalAdjusters.firstDayOfMonth());

        List<CommonVersion> firstCreateIds = versionMapper.firstCreateVersion("PROJ_ESTABLISH", startTime);
        List<ProjectVO> rsp = new ArrayList<>();
        for (CommonVersion version : firstCreateIds) {
            ProjEstablishBaseInfoLib one = projEstablishBaseInfoLibService.getOne(Wrappers.<ProjEstablishBaseInfoLib>lambdaQuery()
                    .eq(ProjEstablishBaseInfoLib::getVersion, version.getVersion())
                    .eq(ProjEstablishBaseInfoLib::getOriginId, version.getMainId()));
            if (one == null) {
                continue;
            }
            ProjectVO vo = new ProjectVO();
            ProjectBizType projectBizType = ProjectBizType.valueOf(one.getBizType());
            switch (projectBizType) {
                case ZL:
                case ZZ:
                    ProjEstablishLeasePriceLib leasePriceLib = leasePriceLibService.getOne(Wrappers.<ProjEstablishLeasePriceLib>lambdaQuery()
                            .eq(ProjEstablishLeasePriceLib::getVersion, version.getVersion())
                            .eq(ProjEstablishLeasePrice::getProjEstablishId, version.getMainId()));
                    vo.setApplyCreditAmount(leasePriceLib.getApplyCreditAmount().toString());
                    break;
                case BL:
                    ProjEstablishFactoringPriceLib factoringPriceLib = factoringPriceLibService.getOne(Wrappers.<ProjEstablishFactoringPriceLib>lambdaQuery()
                            .eq(ProjEstablishFactoringPriceLib::getVersion, version.getVersion())
                            .eq(ProjEstablishFactoringPrice::getProjEstablishId, version.getMainId()));
                    vo.setApplyCreditAmount(factoringPriceLib.getApplyCreditAmount().toString());
                    break;
                case ZR:
                    ProjEstablishAocPriceLib aocPriceLib = aocPriceLibService.getOne(Wrappers.<ProjEstablishAocPriceLib>lambdaQuery()
                            .eq(ProjEstablishAocPriceLib::getVersion, version.getVersion())
                            .eq(ProjEstablishAocPrice::getProjEstablishId, version.getMainId()));
                    vo.setApplyCreditAmount(aocPriceLib.getApplyCreditAmount().toString());
                    break;
                default:
                    break;
            }
            vo.setProjectName(one.getProjName());
            vo.setCreateTime(one.getCreateTime());
            vo.setDeptName(one.getBizDeptId().toString());
            vo.setBizType(one.getBizType());
            vo.setProjectCoOrganizer(one.getProjCosponsorUserIds());
            vo.setProjectOrganizer(one.getProjSponsorUserId().toString());
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
            projectVO.setApplyCreditAmount(amount + "万元");
        }
        rsp.sort(Comparator.comparing(ProjectVO::getCreateTime).reversed());
        return R.ok(rsp);
    }

    @Override
    public R<List<ProjectVO>> projReviewList(ProjectMetricReq req) {
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
            vo.setProjectName(one.getProjName());
            vo.setCreateTime(one.getCreateTime());
            vo.setDeptName(one.getBizDeptId().toString());
            vo.setBizType(one.getBizType());
            vo.setProjectCoOrganizer(one.getProjCosponsorUserIds());
            vo.setProjectOrganizer(one.getProjSponsorUserId().toString());
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
            projectVO.setApplyCreditAmount(amount + "万元");
        }

        rsp.sort(Comparator.comparing(ProjectVO::getCreateTime).reversed());
        return R.ok(rsp);
    }
}
