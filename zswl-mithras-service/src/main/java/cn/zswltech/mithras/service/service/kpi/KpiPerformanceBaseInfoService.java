package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.dto.kpi.KpiPerformanceManageListREQ;
import cn.zswltech.mithras.dto.kpi.KpiPerformanceManageListRSP;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.kpi.mapper.PerformanceBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.PerformanceRecordInfoMapper;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceMainInfo;
import cn.zswltech.mithras.kpi.mapper.model.PerformanceRecordInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.kpi.excel.*;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/6/24/14:58
 * @description
 */
@Slf4j
@Service
public class KpiPerformanceBaseInfoService extends ServiceImpl<PerformanceBaseInfoMapper, PerformanceBaseInfo> {

    @Resource
    private OrgDOMapper orgMapper;
    @Resource
    private PerformanceBaseInfoMapper performanceBaseInfoMapper;
    @Resource
    private PerformanceRecordInfoMapper performanceRecordInfoMapper;
    @Resource
    private KpiPerformanceMainInfoService kpiPerformanceMainInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserDOMapper userManager;
    @Resource
    private UserService userService;

    public void exportExcel(PerformanceMainInfo performanceMainInfo, ServletOutputStream outputStream) {
        List<CompanyExcelModel> companyExcelModels = new ArrayList<>();
        List<DeptExcelModel> deptExcelModels = new ArrayList<>();
        List<PersonalExcelModel> personalExcelModels = new ArrayList<>();
        buildData(performanceMainInfo, personalExcelModels, companyExcelModels, deptExcelModels);
        //分sheet导出数据
        ExcelWriter excelWriter = EasyExcelFactory.write(outputStream).build();
        List<CompanyExcelExportModel> companyExcelExportModelList = BeanUtil.copyToList(companyExcelModels, CompanyExcelExportModel.class);
        WriteSheet companySheet = EasyExcelFactory.writerSheet(0, BelongTypeEnum.COMPANY.getDisplay())
                .head(CompanyExcelExportModel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .build();
        excelWriter.write(companyExcelExportModelList, companySheet);
        // 部门数据要进行计算转换
        List<DeptExcelExportModel> deptExcelExportModels = BeanUtil.copyToList(deptExcelModels, DeptExcelExportModel.class);
        WriteSheet deptSheet = EasyExcelFactory.writerSheet(1, BelongTypeEnum.DEPARTMENT.getDisplay())
                .head(DeptExcelExportModel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .build();
        excelWriter.write(deptExcelExportModels, deptSheet);
        WriteSheet personalSheet = EasyExcelFactory.writerSheet(2, BelongTypeEnum.PERSONAL.getDisplay())
                .head(PersonalExcelModel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .build();
        excelWriter.write(personalExcelModels, personalSheet);
        excelWriter.finish();
    }


    private void buildData(PerformanceMainInfo performanceMainInfo, List<PersonalExcelModel> personalExcelModels, List<CompanyExcelModel> companyExcelModels, List<DeptExcelModel> deptExcelModels) {
        List<PerformanceBaseInfo> performanceBaseInfos = performanceBaseInfoMapper.selectList(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                .eq(PerformanceBaseInfo::getMainId, performanceMainInfo.getId()));
        if (!CollectionUtils.isEmpty(performanceBaseInfos)) {
            Map<String, List<PerformanceBaseInfo>> baseInfoMap = performanceBaseInfos.stream().collect(Collectors.groupingBy(PerformanceBaseInfo::getBelongType));
            List<Long> baseIds = performanceBaseInfos.stream().map(PerformanceBaseInfo::getId).collect(Collectors.toList());
            List<PerformanceRecordInfo> performanceRecordInfos = performanceRecordInfoMapper.selectList(Wrappers.<PerformanceRecordInfo>lambdaQuery()
                    .in(PerformanceRecordInfo::getPerformanceId, baseIds));
            Map<Long, List<PerformanceRecordInfo>> recordInfoMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(performanceRecordInfos)) {
                recordInfoMap = performanceRecordInfos.stream().collect(Collectors.groupingBy(PerformanceRecordInfo::getPerformanceId));
            }
            //三个sheet分别处理
            for (BelongTypeEnum belongTypeEnum : BelongTypeEnum.values()) {
                List<PerformanceBaseInfo> baseInfos = baseInfoMap.get(belongTypeEnum.name());
                if (!CollectionUtils.isEmpty(baseInfos)) {
                    switch (belongTypeEnum) {
                        case PERSONAL:
                            baseInfos.sort(Comparator.comparing(PerformanceBaseInfo::getId));
                            for (PerformanceBaseInfo baseInfo : baseInfos) {
                                PersonalExcelModel personalExcelModel = new PersonalExcelModel();
                                String businessType = Optional.ofNullable(BusinessTypeEnum.ofName(baseInfo.getBusinessType()))
                                        .map(BusinessTypeEnum::getDisplay)
                                        .orElse("");
                                if (baseInfo.getBelongDeptId() != null) {
                                    OrgDO org = orgMapper.selectByPrimaryKey(baseInfo.getBelongDeptId());
                                    if (ObjectUtil.isNotNull(org)) {
                                        personalExcelModel.setDepartment(org.getName());
                                    }
                                } else {
                                    personalExcelModel.setDepartment(baseInfo.getBelongDeptName());
                                }
                                if (Objects.nonNull(baseInfo.getBusinessUserId())) {
                                    final Response<UserVO> userInfoById = userService.getUserInfoById(baseInfo.getBusinessUserId());
                                    if (userInfoById.isSuccess() && Objects.nonNull(userInfoById.getData())) {
                                        personalExcelModel.setLoginAccountName(userInfoById.getData().getAccount());
                                        personalExcelModel.setStaffName(userInfoById.getData().getUserName());
                                    }
                                }
                                personalExcelModel.setBusinessType(businessType);
                                personalExcelModel.setInvestmentTarget(convertData(baseInfo.getAdvertisingAmount()));
                                personalExcelModel.setProfitTarget(convertData(baseInfo.getProfitTarget()));
                                personalExcelModel.setRevenueTarget(convertData(baseInfo.getRevenueTarget()));
                                personalExcelModel.setAssetBalanceTarget(convertData(baseInfo.getAssetBalanceTarget()));
                                personalExcelModels.add(personalExcelModel);
                            }
                            break;
                        case COMPANY:
                            baseInfos.sort(Comparator.comparing(PerformanceBaseInfo::getId));
                            for (PerformanceBaseInfo baseInfo : baseInfos) {
                                CompanyExcelModel companyExcelModel = new CompanyExcelModel();
                                String businessType = Optional.ofNullable(BusinessTypeEnum.ofName(baseInfo.getBusinessType()))
                                        .map(BusinessTypeEnum::getDisplay)
                                        .orElse("");
                                companyExcelModel.setBusinessType(businessType);
                                companyExcelModel.setAssetBalanceTarget(convertData(baseInfo.getAssetBalanceTarget()));
                                companyExcelModel.setRevenueTarget(convertData(baseInfo.getRevenueTarget()));
                                companyExcelModel.setProfitTarget(convertData(baseInfo.getProfitTarget()));
                                companyExcelModel.setInvestmentTarget(convertData(baseInfo.getAdvertisingAmount()));


                                // 新加字段
                                companyExcelModel.setConsultingFeeIncome(convertData(baseInfo.getConsultingFeeIncome()));
                                companyExcelModel.setInterestIncome(convertData(baseInfo.getInterestIncome()));
                                companyExcelModel.setBizFee(convertData(baseInfo.getBizFee()));
                                companyExcelModel.setBusinessTripFee(convertData(baseInfo.getBusinessTripFee()));
                                companyExcelModel.setBusinessServeFee(convertData(baseInfo.getBusinessServeFee()));
                                companyExcelModel.setBeforeProfitTarget(convertData(baseInfo.getBeforeProfitTarget()));

                                List<PerformanceRecordInfo> recordInfos = recordInfoMap.get(baseInfo.getId());
                                if (!CollectionUtils.isEmpty(recordInfos)) {
                                    Map<Integer, Long> recordMap = recordInfos.stream().collect(Collectors.toMap(PerformanceRecordInfo::getMonth, PerformanceRecordInfo::getTargetAmount, (a, b) -> a));
                                    fillMonthData(recordMap, companyExcelModel);
                                }
                                companyExcelModels.add(companyExcelModel);
                            }
                            break;
                        case DEPARTMENT:
                            baseInfos.sort(Comparator.comparing(PerformanceBaseInfo::getId));
                            for (PerformanceBaseInfo baseInfo : baseInfos) {
                                DeptExcelModel deptExcelModel = new DeptExcelModel();
                                String businessType = Optional.ofNullable(BusinessTypeEnum.ofName(baseInfo.getBusinessType()))
                                        .map(BusinessTypeEnum::getDisplay)
                                        .orElse("");
                                deptExcelModel.setBusinessType(businessType);
                                if (baseInfo.getBelongDeptId() != null) {
                                    OrgDO org = orgMapper.selectByPrimaryKey(baseInfo.getBelongDeptId());
                                    if (ObjectUtil.isNotNull(org)) {
                                        deptExcelModel.setDepartment(org.getName());
                                    }
                                } else {
                                    deptExcelModel.setDepartment(baseInfo.getBelongDeptName());
                                }
                                deptExcelModel.setAnnualInvestmentTarget(convertData(baseInfo.getAdvertisingAmount()));
                                deptExcelModel.setAnnualRevenueTarget(convertData(baseInfo.getRevenueTarget()));
                                deptExcelModel.setAnnualProfitTarget(convertData(baseInfo.getProfitTarget()));

                                // 新加字段
                                deptExcelModel.setAssetBalanceTarget(convertData(baseInfo.getAssetBalanceTarget()));
                                deptExcelModel.setConsultingFeeIncome(convertData(baseInfo.getConsultingFeeIncome()));
                                deptExcelModel.setInterestIncome(convertData(baseInfo.getInterestIncome()));
                                deptExcelModel.setBizFee(convertData(baseInfo.getBizFee()));
                                deptExcelModel.setBusinessTripFee(convertData(baseInfo.getBusinessTripFee()));
                                deptExcelModel.setBusinessServeFee(convertData(baseInfo.getBusinessServeFee()));
                                deptExcelModel.setBeforeProfitTarget(convertData(baseInfo.getBeforeProfitTarget()));

                                List<PerformanceRecordInfo> recordInfos = recordInfoMap.get(baseInfo.getId());
                                if (!CollectionUtils.isEmpty(recordInfos)) {
                                    Map<Integer, Long> recordMap = recordInfos.stream().collect(Collectors.toMap(PerformanceRecordInfo::getMonth, PerformanceRecordInfo::getTargetAmount, (a, b) -> a));
                                    fillMonthData(recordMap, deptExcelModel);
                                }
                                deptExcelModels.add(deptExcelModel);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private static void fillMonthData(Map<Integer, Long> recordMap, BaseExcelModel companyExcelModel) {
        for (int i = 1; i < 13; i++) {
            Long amount = recordMap.get(i);
            if (amount != null) {
                switch (i) {
                    case 1:
                        companyExcelModel.setJanuaryTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 2:
                        companyExcelModel.setFebruaryTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 3:
                        companyExcelModel.setMarchTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 4:
                        companyExcelModel.setAprilTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 5:
                        companyExcelModel.setMayTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 6:
                        companyExcelModel.setJuneTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 7:
                        companyExcelModel.setJulyTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 8:
                        companyExcelModel.setAugustTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 9:
                        companyExcelModel.setSeptemberTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 10:
                        companyExcelModel.setOctoberTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 11:
                        companyExcelModel.setNovemberTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    case 12:
                        companyExcelModel.setDecemberTarget(BigDecimal.valueOf(amount)
                                .divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public KpiPerformanceManageListRSP selectList(KpiPerformanceManageListREQ req) {
        PerformanceMainInfo mainInfo = kpiPerformanceMainInfoService.getById(req.getId());
        if (Objects.isNull(mainInfo)) {
            throw new MithrasException("未找到该年度的绩效考核目标");
        }
        List<CompanyExcelModel> companyExcelModels = new ArrayList<>();
        List<DeptExcelModel> deptExcelModels = new ArrayList<>();
        List<PersonalExcelModel> personalExcelModels = new ArrayList<>();
        List<KpiPerformanceManageListRSP.CompanyListVO> companyListVoList = new ArrayList<>();
        List<KpiPerformanceManageListRSP.DeptListVO> deptListVoList = new ArrayList<>();
        List<KpiPerformanceManageListRSP.PersonalListVO> personalListVoList = new ArrayList<>();
        buildData(mainInfo, personalExcelModels, companyExcelModels, deptExcelModels);
        dataConverter(companyExcelModels, deptExcelModels, personalExcelModels, companyListVoList, deptListVoList, personalListVoList, mainInfo);
        KpiPerformanceManageListRSP rsp = new KpiPerformanceManageListRSP();
        rsp.setCompanyListVOList(companyListVoList);
        rsp.setDeptListVOList(deptListVoList);
        rsp.setPersonalListVOList(personalListVoList);
        return rsp;
    }

    private void dataConverter(List<CompanyExcelModel> companyExcelModels, List<DeptExcelModel> deptExcelModels, List<PersonalExcelModel> personalExcelModels,
                               List<KpiPerformanceManageListRSP.CompanyListVO> companyListVoList, List<KpiPerformanceManageListRSP.DeptListVO> deptListVoList,
                               List<KpiPerformanceManageListRSP.PersonalListVO> personalListVoList, PerformanceMainInfo mainInfo) {

        companyExcelModels.forEach(companyExcelModel -> {
            KpiPerformanceManageListRSP.CompanyListVO companyListVO = new KpiPerformanceManageListRSP.CompanyListVO();
            companyListVO.setBusinessType(companyExcelModel.getBusinessType());
            companyListVO.setYear(mainInfo.getYear());
            companyListVO.setAssetBalanceTarget(convertDataToString(companyExcelModel.getAssetBalanceTarget()));
            companyListVO.setProfitTarget(convertDataToString(companyExcelModel.getProfitTarget()));
            companyListVO.setInvestmentTarget(convertDataToString(companyExcelModel.getInvestmentTarget()));
            companyListVO.setRevenueTarget(convertDataToString(companyExcelModel.getRevenueTarget()));
            // 新加字段
            companyListVO.setConsultingFeeIncome(convertDataToString(companyExcelModel.getConsultingFeeIncome()));
            companyListVO.setInterestIncome(convertDataToString(companyExcelModel.getInterestIncome()));
            companyListVO.setBizFee(convertDataToString(companyExcelModel.getBizFee()));
            companyListVO.setBusinessTripFee(convertDataToString(companyExcelModel.getBusinessTripFee()));
            companyListVO.setBusinessServeFee(convertDataToString(companyExcelModel.getBusinessServeFee()));
            companyListVO.setBeforeProfitTarget(convertDataToString(companyExcelModel.getBeforeProfitTarget()));
            build12Month(companyListVO, companyExcelModel);
            companyListVoList.add(companyListVO);
        });

        deptExcelModels.forEach(deptExcelModel -> {
            KpiPerformanceManageListRSP.DeptListVO deptListVO = new KpiPerformanceManageListRSP.DeptListVO();
            deptListVO.setYear(mainInfo.getYear());
            deptListVO.setBusinessType(deptExcelModel.getBusinessType());
            deptListVO.setDept(deptExcelModel.getDepartment());
            deptListVO.setProfitTarget(convertDataToString(deptExcelModel.getAnnualProfitTarget()));
            deptListVO.setInvestmentTarget(convertDataToString(deptExcelModel.getAnnualInvestmentTarget()));
            deptListVO.setRevenueTarget(convertDataToString(deptExcelModel.getAnnualRevenueTarget()));
            // 新加字段
            deptListVO.setAssetBalanceTarget(convertDataToString(deptExcelModel.getAssetBalanceTarget()));
            deptListVO.setConsultingFeeIncome(convertDataToString(deptExcelModel.getConsultingFeeIncome()));
            deptListVO.setInterestIncome(convertDataToString(deptExcelModel.getInterestIncome()));
            deptListVO.setBizFee(convertDataToString(deptExcelModel.getBizFee()));
            deptListVO.setBusinessTripFee(convertDataToString(deptExcelModel.getBusinessTripFee()));
            deptListVO.setBusinessServeFee(convertDataToString(deptExcelModel.getBusinessServeFee()));
            deptListVO.setBeforeProfitTarget(convertDataToString(deptExcelModel.getBeforeProfitTarget()));
            build12Month(deptListVO, deptExcelModel);
            deptListVoList.add(deptListVO);
        });

        personalExcelModels.forEach(personalExcelModel -> {
            KpiPerformanceManageListRSP.PersonalListVO personalListVO = new KpiPerformanceManageListRSP.PersonalListVO();
            personalListVO.setYear(mainInfo.getYear());
            personalListVO.setBusinessType(personalExcelModel.getBusinessType());
            personalListVO.setDept(personalExcelModel.getDepartment());
            personalListVO.setStaffName(personalExcelModel.getStaffName());
            personalListVO.setLoginAccountName(personalExcelModel.getLoginAccountName());
            personalListVO.setProfitTarget(convertDataToString(personalExcelModel.getProfitTarget()));
            personalListVO.setInvestmentTarget(convertDataToString(personalExcelModel.getInvestmentTarget()));
            personalListVO.setRevenueTarget(convertDataToString(personalExcelModel.getRevenueTarget()));
            personalListVO.setAssetBalanceTarget(convertDataToString(personalExcelModel.getAssetBalanceTarget()));
//            build12Month(personalListVO, personalExcelModel);
            personalListVoList.add(personalListVO);
        });

    }

    private void build12Month(KpiPerformanceManageListRSP.BaseListVO baseListVo, BaseExcelModel baseExcelModel) {
        baseListVo.setJanuaryTarget(Optional.ofNullable(baseExcelModel.getJanuaryTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setFebruaryTarget(Optional.ofNullable(baseExcelModel.getFebruaryTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setMarchTarget(Optional.ofNullable(baseExcelModel.getMarchTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setAprilTarget(Optional.ofNullable(baseExcelModel.getAprilTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setMayTarget(Optional.ofNullable(baseExcelModel.getMayTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setJuneTarget(Optional.ofNullable(baseExcelModel.getJuneTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setJulyTarget(Optional.ofNullable(baseExcelModel.getJulyTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setAugustTarget(Optional.ofNullable(baseExcelModel.getAugustTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setSeptemberTarget(Optional.ofNullable(baseExcelModel.getSeptemberTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setOctoberTarget(Optional.ofNullable(baseExcelModel.getOctoberTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setNovemberTarget(Optional.ofNullable(baseExcelModel.getNovemberTarget()).orElse(BigDecimal.ZERO).toPlainString());
        baseListVo.setDecemberTarget(Optional.ofNullable(baseExcelModel.getDecemberTarget()).orElse(BigDecimal.ZERO).toPlainString());
    }

    private List<DeptExcelExportModel> convertDeptData(List<DeptExcelModel> deptExcelModels) {
        if (CollectionUtil.isNotEmpty(deptExcelModels)) {
            List<DeptExcelExportModel> deptExcelExportModels = new ArrayList<>();
            deptExcelModels.forEach(item -> {
                final DeptExcelExportModel deptExcelExportModel = BeanUtil.copyProperties(item, DeptExcelExportModel.class);
                final BigDecimal annualRevenueTarget = Objects.nonNull(item.getAnnualRevenueTarget()) ? item.getAnnualRevenueTarget() : BigDecimal.ZERO;
                final BigDecimal consultingFeeIncome = Objects.nonNull(item.getConsultingFeeIncome()) ? item.getConsultingFeeIncome() : BigDecimal.ZERO;
                final BigDecimal interestIncome = Objects.nonNull(item.getInterestIncome()) ? item.getInterestIncome() : BigDecimal.ZERO;
                final BigDecimal bizFee = Objects.nonNull(item.getBizFee()) ? item.getBizFee() : BigDecimal.ZERO;
                final BigDecimal businessTripFee = Objects.nonNull(item.getBusinessTripFee()) ? item.getBusinessTripFee() : BigDecimal.ZERO;
                final BigDecimal businessServeFee = Objects.nonNull(item.getBusinessServeFee()) ? item.getBusinessServeFee() : BigDecimal.ZERO;
//                deptExcelExportModel.setConsultingFeeIncomeCal(annualRevenueTarget + "-" + consultingFeeIncome);
//                deptExcelExportModel.setInterestIncomeCal(annualRevenueTarget + "-" + interestIncome);
//                deptExcelExportModel.setBusinessTripFeeCal(bizFee + "-" + businessTripFee);
//                deptExcelExportModel.setBusinessServeFeeCal(bizFee + "-" + businessServeFee);
                deptExcelExportModels.add(deptExcelExportModel);
            });
            return deptExcelExportModels;
        } else {
            return new ArrayList<>();
        }
    }

    String convertDataToString(BigDecimal data) {
        if (Objects.isNull(data)) {
            return "";
        }
        return data.toString();
    }

    BigDecimal convertData(Long data) {
        if (Objects.nonNull(data)) {
            return BigDecimal.valueOf(data).divide(BigDecimal.valueOf(100000000), 2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }
}
