package cn.zswltech.mithras.service.service.ftp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeBaseInfoListRSP;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListREQ;
import cn.zswltech.mithras.dto.ftp.FtpIncomeDetailRecordListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectRepayActualSplitRSP;
import cn.zswltech.mithras.capital.domain.enums.FinancePaymentWriteOffOrderEnum;
import cn.zswltech.mithras.service.enums.fund.DirectFinancingType;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActualSplitRecord;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingProductDetailService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualSplitRecordService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualSplitService;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpIncomeBaseInfoMapper;
import cn.zswltech.mithras.ftp.oldftp.model.FtpIncomeBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpIncomeDetailRecord;
import cn.zswltech.mithras.ftp.oldftp.service.FtpIncomeDetailRecordService;
import cn.zswltech.mithras.service.mapper.model.fund.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPlan;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.fund.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 资金管理-融资管理-ftp收益表
 * @date 2025-07-15
 */
@Service
public class FtpIncomeBaseInfoService extends ServiceImpl<FtpIncomeBaseInfoMapper, FtpIncomeBaseInfo> {


    @Resource
    private FundFinancingBaseInfoService fundFinancingBaseInfoService;
    @Resource
    private FundFinancingPlanService fundFinancingPlanService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;
    @Resource
    private FundDirectFinancingProductDetailService fundDirectFinancingProductDetailService;
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FtpIncomeDetailRecordService ftpIncomeDetailRecordService;
    @Resource
    private FundDirectFinancingRepayActualSplitRecordService fundDirectFinancingRepayActualSplitRecordService;
    @Resource
    private FundDirectFinancingRepayActualSplitService fundDirectFinancingRepayActualSplitService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FtpIncomeBaseInfoMapper ftpIncomeBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundOrganizationService fundOrganizationService;

    private final static String YEAR_DAY = "360";
    private final static String HZ = "汇总";

    //todo 这里需要优化
    @Transactional(rollbackFor = Throwable.class)
    public void createOrUpdate(Long fundFinancingId, String financingType, LocalDate updateTime) {
        //区分直/间融
        if (ObjectUtil.equals(financingType, FinancingTypeEnum.DIRECT.name())) {
            //处理直融
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = fundDirectFinancingBaseInfoService.getById(fundFinancingId);
            if (ObjectUtil.isEmpty(fundDirectFinancingBaseInfo)) {
                return;
            }
            if (!StrUtil.equalsAny(fundDirectFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABS.name(), DirectFinancingType.ABN.name())) {
                //创建baseinfo
                FtpIncomeBaseInfo ftpIncomeBaseInfo = buildDirectFtpDetailRecord(fundDirectFinancingBaseInfo);
                ftpIncomeBaseInfo.setFtpYieldRate(fundDirectFinancingBaseInfo.getFtpYieldRate() == null ? 0 : fundDirectFinancingBaseInfo.getFtpYieldRate());
                //保存baseinfo
                List<FtpIncomeBaseInfo> byFinancing = this.getByFinancing(fundDirectFinancingBaseInfo.getId(), FinancingTypeEnum.DIRECT.name());
                if (CollectionUtil.isNotEmpty(byFinancing)) {
                    ftpIncomeBaseInfo.setId(byFinancing.get(0).getId());
                    this.updateById(ftpIncomeBaseInfo);
                } else {
                    this.save(ftpIncomeBaseInfo);
                }
                //构建详情表
                List<FtpIncomeDetailRecord> records = buildDirectFtpDetailRecord(fundDirectFinancingBaseInfo, ftpIncomeBaseInfo);
                List<FtpIncomeDetailRecord> addRecords = new ArrayList<>();
                List<FtpIncomeDetailRecord> updateRecords = new ArrayList<>();
                if (ObjectUtil.isNotEmpty(records)) {
                    //区分更新or新增
                    List<FtpIncomeDetailRecord> oldRecord = ftpIncomeDetailRecordService.listByFtpIncomeId(ftpIncomeBaseInfo.getId(), updateTime);
                    if (ObjectUtil.isNotEmpty(oldRecord)) {
                        Map<LocalDate, Long> day2Id = oldRecord.stream().collect(Collectors.toMap(FtpIncomeDetailRecord::getInterestDate, FtpIncomeDetailRecord::getId, (a, b) -> b));
                        records.forEach(e -> {
                            if ((ObjectUtil.isNotEmpty(updateTime) && e.getInterestDate().isBefore(updateTime)) || ObjectUtil.isEmpty(e.getInterestDate())) {
                                return;
                            }
                            Long aLong = day2Id.get(e.getInterestDate());
                            if (ObjectUtil.isNotEmpty(aLong)) {
                                e.setId(aLong);
                                updateRecords.add(e);
                            } else {
                                addRecords.add(e);
                            }
                        });
                    } else {
                        addRecords.addAll(records);
                    }
                    if (!addRecords.isEmpty()) {
                        ftpIncomeDetailRecordService.saveBatch(addRecords);
                    }
                    if (!updateRecords.isEmpty()) {
                        ftpIncomeDetailRecordService.updateBatchById(updateRecords);
                    }
                }
                FtpIncomeDetailRecord record = records.stream().max(Comparator.comparing(FtpIncomeDetailRecord::getInterestDate)).get();
                ftpIncomeDetailRecordService.remove(Wrappers.<FtpIncomeDetailRecord>lambdaQuery()
                        .eq(FtpIncomeDetailRecord::getFtpIncomeId, record.getFtpIncomeId())
                        .gt(FtpIncomeDetailRecord::getInterestDate, record.getInterestDate()));
                return;
            }

            //获取产品明细
            List<FundDirectFinancingProductDetail> financingProductDetails = fundDirectFinancingProductDetailService.listByFinancingId(fundDirectFinancingBaseInfo.getId());

            //获取日期
            Map<String, FundDirectRepayActualSplitRSP> productCode2directRepayActualSplitMap = fundDirectFinancingRepayActualSplitService.listSplitRspByFinancingId(fundDirectFinancingBaseInfo.getId()).stream().collect(Collectors.toMap(FundDirectRepayActualSplitRSP::getSecuritiesCode, e -> e, (a,
                    b) -> b));
            //获取付款日期
            //List<FundReceiptFlowPlan> financingRepayFlowPlan = fundReceiptRepayBaseInfoService.getFinancingRepayFlowPlan(fundDirectFinancingBaseInfo.getId(), FinancingTypeEnum.DIRECT.name());



            if (ObjectUtil.isEmpty(financingProductDetails)) {
                return;
            }
            //abs下产品数据有限
            financingProductDetails.forEach(productDetail -> {
                if (ObjectUtil.isEmpty(productDetail.getFtpYieldRate())) {
                    return;
                }
                //创建baseinfo
                FtpIncomeBaseInfo ftpIncomeBaseInfo = buildDirectAbsFtpBaseInfo(productDetail, fundDirectFinancingBaseInfo);
                ftpIncomeBaseInfo.setFtpYieldRate(fundDirectFinancingBaseInfo.getFtpYieldRate());
                //保存baseinfo
                List<FtpIncomeBaseInfo> byFinancing = this.getByFinancing(fundDirectFinancingBaseInfo.getId(), FinancingTypeEnum.DIRECT.name(), productDetail.getId());
                if (CollectionUtil.isNotEmpty(byFinancing)) {
                    ftpIncomeBaseInfo.setId(byFinancing.get(0).getId());
                    this.updateById(ftpIncomeBaseInfo);
                } else {
                    this.save(ftpIncomeBaseInfo);
                }
                //构建详情表
                List<FtpIncomeDetailRecord> records = null;
                if (StrUtil.equalsAny(fundDirectFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABS.name(), DirectFinancingType.ABN.name())) {
                    records = buildDirectAbsAbnFtpDetailRecord(productDetail, ftpIncomeBaseInfo, productCode2directRepayActualSplitMap.getOrDefault(productDetail.getSecuritiesCode(), new FundDirectRepayActualSplitRSP()));
                }
                List<FtpIncomeDetailRecord> addRecords = new ArrayList<>();
                List<FtpIncomeDetailRecord> updateRecords = new ArrayList<>();
                if (ObjectUtil.isNotEmpty(records)) {
                    //区分更新or新增
                    List<FtpIncomeDetailRecord> oldRecord = ftpIncomeDetailRecordService.listByFtpIncomeId(ftpIncomeBaseInfo.getId(), updateTime);
                    if (ObjectUtil.isNotEmpty(oldRecord)) {
                        Map<LocalDate, Long> day2Id = oldRecord.stream().collect(Collectors.toMap(FtpIncomeDetailRecord::getInterestDate, FtpIncomeDetailRecord::getId, (a, b) -> b));
                        records.forEach(e -> {
                            if ((ObjectUtil.isNotEmpty(updateTime) && e.getInterestDate().isBefore(updateTime)) || ObjectUtil.isEmpty(e.getInterestDate())) {
                                return;
                            }
                            Long aLong = day2Id.get(e.getInterestDate());
                            if (ObjectUtil.isNotEmpty(aLong)) {
                                e.setId(aLong);
                                updateRecords.add(e);
                            } else {
                                addRecords.add(e);
                            }
                        });
                    } else {
                        addRecords.addAll(records);
                    }
                    if (!addRecords.isEmpty()) {
                        ftpIncomeDetailRecordService.saveBatch(addRecords);
                    }
                    if (!updateRecords.isEmpty()) {
                        ftpIncomeDetailRecordService.updateBatchById(updateRecords);
                    }
                    //清理多余数据
                    FtpIncomeDetailRecord record = records.stream().max(Comparator.comparing(FtpIncomeDetailRecord::getInterestDate)).get();
                    ftpIncomeDetailRecordService.remove(Wrappers.<FtpIncomeDetailRecord>lambdaQuery()
                            .eq(FtpIncomeDetailRecord::getFtpIncomeId, record.getFtpIncomeId())
                            .gt(FtpIncomeDetailRecord::getInterestDate, record.getInterestDate()));
                }

            });

        } else {
            //处理间融
            FundFinancingBaseInfo financingBaseInfo = fundFinancingBaseInfoService.getById(fundFinancingId);
            if (ObjectUtil.isEmpty(financingBaseInfo)) {
                return;
            }
            //获取报价方案
            FundFinancingPlan financingPlan = fundFinancingPlanService.getOneByFinancingId(financingBaseInfo.getId());
            if (ObjectUtil.isEmpty(financingPlan) || ObjectUtil.isEmpty(financingPlan.getFtpYieldRate())) {
                return;
            }
            //创建baseinfo
            FtpIncomeBaseInfo ftpIncomeBaseInfo = buildFtpDetailRecord(financingBaseInfo);
            ftpIncomeBaseInfo.setFtpYieldRate(financingPlan.getFtpYieldRate() == null ? 0 : financingPlan.getFtpYieldRate());
            //保存baseinfo
            List<FtpIncomeBaseInfo> byFinancing = this.getByFinancing(financingBaseInfo.getId(), FinancingTypeEnum.INDIRECT.name());
            if (CollectionUtil.isNotEmpty(byFinancing)) {
                ftpIncomeBaseInfo.setId(byFinancing.get(0).getId());
                this.updateById(ftpIncomeBaseInfo);
            } else {
                this.save(ftpIncomeBaseInfo);
            }
            //构建详情表
            List<FtpIncomeDetailRecord> records = buildFtpDetailRecord(financingBaseInfo, ftpIncomeBaseInfo);
            List<FtpIncomeDetailRecord> addRecords = new ArrayList<>();
            List<FtpIncomeDetailRecord> updateRecords = new ArrayList<>();
            if (ObjectUtil.isNotEmpty(records)) {
                //区分更新or新增
                List<FtpIncomeDetailRecord> oldRecord = ftpIncomeDetailRecordService.listByFtpIncomeId(ftpIncomeBaseInfo.getId(), updateTime);
                if (ObjectUtil.isNotEmpty(oldRecord)) {
                    Map<LocalDate, Long> day2Id = oldRecord.stream().collect(Collectors.toMap(FtpIncomeDetailRecord::getInterestDate, FtpIncomeDetailRecord::getId, (a, b) -> b));
                    records.forEach(e -> {
                        if ((ObjectUtil.isNotEmpty(updateTime) && e.getInterestDate().isBefore(updateTime)) || ObjectUtil.isEmpty(e.getInterestDate())) {
                            return;
                        }
                        Long aLong = day2Id.get(e.getInterestDate());
                        if (ObjectUtil.isNotEmpty(aLong)) {
                            e.setId(aLong);
                            updateRecords.add(e);
                        } else {
                            addRecords.add(e);
                        }
                    });
                } else {
                    addRecords.addAll(records);
                }
                if (!addRecords.isEmpty()) {
                    ftpIncomeDetailRecordService.saveBatch(addRecords);
                }
                if (!updateRecords.isEmpty()) {
                    ftpIncomeDetailRecordService.updateBatchById(updateRecords);
                }
            }
            FtpIncomeDetailRecord record = records.stream().max(Comparator.comparing(FtpIncomeDetailRecord::getInterestDate)).get();
            ftpIncomeDetailRecordService.remove(Wrappers.<FtpIncomeDetailRecord>lambdaQuery()
            .eq(FtpIncomeDetailRecord::getFtpIncomeId, record.getFtpIncomeId())
            .gt(FtpIncomeDetailRecord::getInterestDate, record.getInterestDate()));
        }
    }

    public PageR<FtpIncomeBaseInfoListRSP> list(FtpIncomeBaseInfoListREQ req) {
        //融资机构搜索
        if (ObjectUtil.isNotEmpty(req.getOrganizationId())) {
            req.setFinancingIds(financingCreditRefService.queryByOrgId(req.getOrganizationId()).stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList()));
            if (CollectionUtil.isEmpty(req.getFinancingIds())) {
                return null;
            }
        }
        Page<FtpIncomeBaseInfo> ftpIncomeBaseInfoPage = ftpIncomeBaseInfoMapper.getFundFinancingIds(new Page<>(req.getPage(), req.getPageSize()), req);
        List<Long> needFinancingIds = ftpIncomeBaseInfoPage.getRecords().stream().map(FtpIncomeBaseInfo::getFundFinancingId).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(needFinancingIds)) {
            return null;
        }
        List<FtpIncomeBaseInfo> ftpIncomeBaseInfos = ftpIncomeBaseInfoMapper.selectList(Wrappers.<FtpIncomeBaseInfo>lambdaQuery()
                .in(FtpIncomeBaseInfo::getFundFinancingId, needFinancingIds));
        //
        Map<Long, String> systemUserId2Name = id2NameService.sysUserId2Name(ftpIncomeBaseInfos.stream().map(FtpIncomeBaseInfo::getFundManagerId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()));
        List<FtpIncomeBaseInfo> directBaseInfo = ftpIncomeBaseInfos.stream().filter(e -> ObjectUtil.equals(e.getFinancingType(), FinancingTypeEnum.DIRECT.name())).collect(Collectors.toList());
        List<FtpIncomeBaseInfo> baseInfo = ftpIncomeBaseInfos.stream().filter(e -> !ObjectUtil.equals(e.getFinancingType(), FinancingTypeEnum.DIRECT.name())).collect(Collectors.toList());
        List<FtpIncomeBaseInfoListRSP> rsps = new ArrayList<>();
        LocalDate today = LocalDate.now();
        // 本月第一天
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        // 本月最后一天
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        // 本年第一天
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        // 本年最后一天
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        //获取统计
        List<Long> ftpIncomeIds = ftpIncomeBaseInfos.stream().map(FtpIncomeBaseInfo::getId).collect(Collectors.toList());
        Map<Long, Long> monthMap = ftpIncomeDetailRecordService.countByDate(firstDayOfMonth, lastDayOfMonth, ftpIncomeIds);
        Map<Long, Long> yearMap = ftpIncomeDetailRecordService.countByDate(firstDayOfYear, lastDayOfYear, ftpIncomeIds);

        //间融
        if (ObjectUtil.isNotEmpty(baseInfo)) {
            Map<Long, List<FundFinancingCreditRef>> financingId2FundFinancingCreditRefMap = financingCreditRefService.queryBatchByFinancingId(baseInfo.stream().map(FtpIncomeBaseInfo::getFundFinancingId).collect(Collectors.toList()));
            Map<Long, String> originId2Name = fundOrganizationService.listByIds(financingId2FundFinancingCreditRefMap.values().stream().flatMap(List::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundOrganization::getId,
                    FundOrganization::getOrganizationName, (a, b) -> a));
            baseInfo.forEach(e -> {
                FtpIncomeBaseInfoListRSP rsp = BeanUtil.copyProperties(e, FtpIncomeBaseInfoListRSP.class);
                rsp.setFtpIncomeCurrentMonth(monthMap.get(e.getId()));
                rsp.setFtpIncomeCurrentYear(yearMap.get(e.getId()));
                rsp.setFundManagerName(systemUserId2Name.get(rsp.getFundManagerId()));
                List<FundFinancingCreditRef> fundFinancingCreditRefs = financingId2FundFinancingCreditRefMap.get(e.getFundFinancingId());
                if (ObjectUtil.isNotEmpty(fundFinancingCreditRefs)) {
                    //补充机构信息
                    fundFinancingCreditRefs.forEach(x -> {
                        List<Long> organizationId = rsp.getOrganizationId();
                        if (ObjectUtil.isEmpty(organizationId)) {
                            organizationId = new ArrayList<>();
                            rsp.setOrganizationId(organizationId);
                        }
                        organizationId.add(x.getOrganizationId());
                        List<String> organizationName = rsp.getOrganizationName();
                        if (ObjectUtil.isEmpty(organizationName)) {
                            organizationName = new ArrayList<>();
                            rsp.setOrganizationName(organizationName);
                        }
                        organizationName.add(originId2Name.get(x.getOrganizationId()));
                    });
                }
                rsps.add(rsp);
            });
        }
        //直融
        if(ObjectUtil.isNotEmpty(directBaseInfo)) {
            Map<Long, List<FtpIncomeBaseInfo>> financingId2FtpIncomeBaseInfo = directBaseInfo.stream().collect(Collectors.groupingBy(FtpIncomeBaseInfo::getFundFinancingId));
            Map<Long, String> financingId2Name = fundDirectFinancingBaseInfoService.listByIds(financingId2FtpIncomeBaseInfo.keySet()).stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, FundDirectFinancingBaseInfo::getProductName, (a, b) -> b));
            financingId2FtpIncomeBaseInfo.forEach((financing, ftpBaseInfos) -> {
                if (ObjectUtil.isNotEmpty(ftpBaseInfos)) {
                    FtpIncomeBaseInfoListRSP ftpIncomeBaseInfoListRSP = BeanUtil.copyProperties(ftpBaseInfos.get(0), FtpIncomeBaseInfoListRSP.class);
                    ftpIncomeBaseInfoListRSP.setOrganizationName(Collections.singletonList(financingId2Name.get(ftpIncomeBaseInfoListRSP.getFundFinancingId())));
                    ftpIncomeBaseInfoListRSP.setFinancingAmount(ftpBaseInfos.stream().map(FtpIncomeBaseInfo::getFinancingAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
                    ftpIncomeBaseInfoListRSP.setFundManagerName(systemUserId2Name.get(ftpIncomeBaseInfoListRSP.getFundManagerId()));
                    ftpBaseInfos.forEach(e -> {
                        ftpIncomeBaseInfoListRSP.setFtpIncomeCurrentMonth(monthMap.getOrDefault(e.getId(), 0L) + LongUtil.null2zero(ftpIncomeBaseInfoListRSP.getFtpIncomeCurrentMonth()));
                        ftpIncomeBaseInfoListRSP.setFtpIncomeCurrentYear(yearMap.getOrDefault(e.getId(), 0L) + LongUtil.null2zero(ftpIncomeBaseInfoListRSP.getFtpIncomeCurrentYear()));
                    });
                    rsps.add(ftpIncomeBaseInfoListRSP);
                }
            });
        }
        return PageR.of(rsps, ftpIncomeBaseInfoPage.getTotal());
    }

    public FtpIncomeBaseInfoListRSP ftpIncomeCount(FtpIncomeBaseInfoListREQ req){
        //融资机构搜索
        if (ObjectUtil.isNotEmpty(req.getOrganizationId())) {
            req.setFinancingIds(financingCreditRefService.queryByOrgId(req.getOrganizationId()).stream().map(FundFinancingCreditRef::getFinancingId).collect(Collectors.toList()));
            if (CollectionUtil.isEmpty(req.getFinancingIds())) {
                return null;
            }
        }

        LocalDate today = LocalDate.now();
        // 本月第一天
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        // 本月最后一天
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        // 本年第一天
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        // 本年最后一天
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        //获取符合条件的数据
        //获取统计
        List<Long> incomeBaseInfo = ftpIncomeBaseInfoMapper.listFtpIncome(req).stream().map(FtpIncomeBaseInfo::getId).collect(Collectors.toList());
        Map<Long, Long> monthMap = ftpIncomeDetailRecordService.countByDate(firstDayOfMonth, lastDayOfMonth, incomeBaseInfo);
        Map<Long, Long> yearMap = ftpIncomeDetailRecordService.countByDate(firstDayOfYear, lastDayOfYear, incomeBaseInfo);

        FtpIncomeBaseInfoListRSP rsp = new FtpIncomeBaseInfoListRSP();
        rsp.setFtpIncomeCurrentMonth(monthMap.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
        rsp.setFtpIncomeCurrentYear(yearMap.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
        rsp.setFinancingAmount(ftpIncomeBaseInfoMapper.countFinancingAmountByDate(req));
        return rsp;
    }

    public FtpIncomeBaseInfoListRSP detail(FtpIncomeDetailRecordListREQ req){
        if (ObjectUtil.isEmpty(req.getFinancingType())) {
            req.setFinancingType(FinancingTypeEnum.INDIRECT.name());
        }
        List<FtpIncomeBaseInfo> ftpIncomeBaseInfos = this.getByFinancing(req.getFundFinancingId(), req.getFinancingType());
        if (CollectionUtil.isEmpty(ftpIncomeBaseInfos)) {
            return null;
        }
        FtpIncomeBaseInfoListRSP rsp = new FtpIncomeBaseInfoListRSP();
        //区分直/间融
        if (ObjectUtil.equals(req.getFinancingType(), FinancingTypeEnum.DIRECT.name())) {
            //直融
            FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo = fundDirectFinancingBaseInfoService.getById(req.getFundFinancingId());
            if (ObjectUtil.isEmpty(fundDirectFinancingBaseInfo)) {
                return null;
            }
            rsp = BeanUtil.copyProperties(ftpIncomeBaseInfos.get(0), FtpIncomeBaseInfoListRSP.class);
            rsp.setFinancingAmount(ftpIncomeBaseInfos.stream().map(FtpIncomeBaseInfo::getFinancingAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
            rsp.setOrganizationName(Collections.singletonList(fundDirectFinancingBaseInfo.getProductName()));
            rsp.setFundManagerName(id2NameService.sysUserId2NameSingle(rsp.getFundManagerId()));
        } else {
            Map<Long, List<FundFinancingCreditRef>> financingId2FundFinancingCreditRefMap = financingCreditRefService.queryBatchByFinancingId(Collections.singleton(req.getFundFinancingId()));
            Map<Long, String> originId2Name = fundOrganizationService.listByIds(financingId2FundFinancingCreditRefMap.values().stream().flatMap(List::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toList())).stream().collect(Collectors.toMap(FundOrganization::getId,
                    FundOrganization::getOrganizationName, (a, b) -> a));
            rsp = BeanUtil.copyProperties(ftpIncomeBaseInfos.get(0), FtpIncomeBaseInfoListRSP.class);
            rsp.setFundManagerName(id2NameService.sysUserId2NameSingle(rsp.getFundManagerId()));
            List<FundFinancingCreditRef> fundFinancingCreditRefs = financingId2FundFinancingCreditRefMap.get(req.getFundFinancingId());
            if (ObjectUtil.isNotEmpty(fundFinancingCreditRefs)) {
                //补充机构信息
                for (FundFinancingCreditRef x : fundFinancingCreditRefs) {
                    List<Long> organizationId = rsp.getOrganizationId();
                    if (ObjectUtil.isEmpty(organizationId)) {
                        organizationId = new ArrayList<>();
                        rsp.setOrganizationId(organizationId);
                    }
                    organizationId.add(x.getOrganizationId());
                    List<String> organizationName = rsp.getOrganizationName();
                    if (ObjectUtil.isEmpty(organizationName)) {
                        organizationName = new ArrayList<>();
                        rsp.setOrganizationName(organizationName);
                    }
                    organizationName.add(originId2Name.get(x.getOrganizationId()));
                }
            }
        }
        return rsp;
    }

    public List<FtpIncomeDetailRecordListRSP> recordList(FtpIncomeDetailRecordListREQ req) {
        if (ObjectUtil.isEmpty(req.getFinancingType())) {
            req.setFinancingType(FinancingTypeEnum.INDIRECT.name());
        }
        List<FtpIncomeBaseInfo> ftpIncomeBaseInfos = this.getByFinancing(req.getFundFinancingId(), req.getFinancingType());
        if (CollectionUtil.isEmpty(ftpIncomeBaseInfos)) {
            return null;
        }
        Map<Long, List<FtpIncomeDetailRecord>> ftpIncomeId2DetailRecord = ftpIncomeDetailRecordService.listByFtpIncomeIds(ftpIncomeBaseInfos.stream().map(FtpIncomeBaseInfo::getId).collect(Collectors.toList()));
        List<FtpIncomeDetailRecordListRSP> results = new ArrayList<>();
        //获取汇总信息
        LocalDate today = LocalDate.now();
         // 本年第一天
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        // 本年最后一天
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        Map<Long, Long> year = ftpIncomeDetailRecordService.countByDate(firstDayOfYear, lastDayOfYear, new ArrayList<>(ftpIncomeId2DetailRecord.keySet()));
        Map<Long, Long> total = ftpIncomeDetailRecordService.countByDate(null, null, new ArrayList<>(ftpIncomeId2DetailRecord.keySet()));

        ftpIncomeBaseInfos.forEach(e -> {
            List<FtpIncomeDetailRecord> records = ftpIncomeId2DetailRecord.get(e.getId());
            FtpIncomeDetailRecordListRSP rsp = new FtpIncomeDetailRecordListRSP();
            List<FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody> incomeDetailRecordListBodies = BeanUtil.copyToList(records, FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody.class);
            rsp.setBodys(incomeDetailRecordListBodies);
            rsp.setFtpIncomeId(e.getId());
            rsp.setAbbreviation(e.getAbbreviation());
            FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody count = new FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody();
            count.setFtpIncome(total.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
            count.setFtpIncomeCurrentYear(year.values().stream().filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L));
            rsp.setCount(count);
            results.add(rsp);
        });

        if (ObjectUtil.equals(req.getFinancingType(), FinancingTypeEnum.DIRECT.name())) {
            //查询直融数据
            FundDirectFinancingBaseInfo directFinancingBaseInfo = fundDirectFinancingBaseInfoService.getById(ftpIncomeBaseInfos.get(0).getFundFinancingId());
            if (ObjectUtil.isNotEmpty(directFinancingBaseInfo) && StrUtil.equalsAny(directFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name())) {
                //直融要求合并
                Map<LocalDate, FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody> date2BodyMap = new HashMap<>();
                results.forEach(e -> {
                    List<FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody> bodys = e.getBodys();
                    if (CollectionUtil.isNotEmpty(bodys)) {
                        bodys.forEach(d -> {
                            FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody orDefault = date2BodyMap.getOrDefault(d.getInterestDate(), new FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody());
                            orDefault.setRemainingPrincipal(LongUtil.null2zero(orDefault.getRemainingPrincipal()) + LongUtil.null2zero(d.getRemainingPrincipal()));
                            //orDefault.setFtpYieldRateDay(LongUtil.null2zero(orDefault.getFtpYieldRateDay()) + LongUtil.null2zero(d.getFtpYieldRateDay()));
                            orDefault.setFtpIncome(LongUtil.null2zero(orDefault.getFtpIncome()) + LongUtil.null2zero(d.getFtpIncome()));
                            orDefault.setFtpIncomeCurrentYear(LongUtil.null2zero(orDefault.getFtpIncomeCurrentYear()) + LongUtil.null2zero(d.getFtpIncomeCurrentYear()));
                            orDefault.setInterestDate(d.getInterestDate());
                            date2BodyMap.put(d.getInterestDate(), orDefault);
                        });
                    }
                });
                FtpIncomeDetailRecordListRSP rsp = new FtpIncomeDetailRecordListRSP();
                rsp.setAbbreviation(HZ);
                rsp.setBodys(date2BodyMap.values().stream().sorted(Comparator.comparing(FtpIncomeDetailRecordListRSP.IncomeDetailRecordListBody::getInterestDate).reversed()).collect(Collectors.toList()));
                results.add(0, rsp);
            }
        }
        return results;
    }

    private List<FtpIncomeBaseInfo> getByFinancing(Long financingId, String financingType) {
        return this.getByFinancing(financingId, financingType, null);
    }

    private List<FtpIncomeBaseInfo> getByFinancing(Long financingId, String financingType, Long financingProductId) {
        return this.baseMapper.selectList(Wrappers.<FtpIncomeBaseInfo>lambdaQuery()
                .eq(FtpIncomeBaseInfo::getFundFinancingId, financingId)
                .eq(ObjectUtil.isNotEmpty(financingProductId), FtpIncomeBaseInfo::getFinancingProductId, financingProductId)
                .eq(ObjectUtil.isNotEmpty(financingType), FtpIncomeBaseInfo::getFinancingType, financingType));
    }

    //构建ftp 基本信息
    private FtpIncomeBaseInfo buildFtpDetailRecord(FundFinancingBaseInfo financingBaseInfo) {
        FtpIncomeBaseInfo info = new FtpIncomeBaseInfo();
        info.setFundFinancingId(financingBaseInfo.getId());
        info.setFinancingType(FinancingTypeEnum.INDIRECT.name());
        info.setFinancingCode(financingBaseInfo.getFinancingCode());
        info.setFinancingAmount(financingBaseInfo.getFinancingAmount());
        info.setFundManagerId(financingBaseInfo.getFundManagerId());
        return info;
    }

    //构建ftp 基本信息
    private FtpIncomeBaseInfo buildDirectFtpDetailRecord(FundDirectFinancingBaseInfo financingBaseInfo) {
        FtpIncomeBaseInfo info = new FtpIncomeBaseInfo();
        info.setFundFinancingId(financingBaseInfo.getId());
        info.setFinancingType(FinancingTypeEnum.DIRECT.name());
        info.setFinancingCode(financingBaseInfo.getFinancingCode());
        info.setFinancingAmount(LongUtil.other2Long(LongUtil.null2zero(financingBaseInfo.getFinancingAmount()).toString()));
        info.setFundManagerId(financingBaseInfo.getFundManagerId());
        return info;
    }

    private FtpIncomeBaseInfo buildDirectAbsFtpBaseInfo(FundDirectFinancingProductDetail financingProductDetail,  FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo) {
        FtpIncomeBaseInfo info = new FtpIncomeBaseInfo();
        info.setFundFinancingId(financingProductDetail.getFinancingId());
        info.setFinancingType(FinancingTypeEnum.DIRECT.name());
        info.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
        info.setFinancingAmount(LongUtil.other2Long(String.valueOf(financingProductDetail.getIssuanceAmount())));
        info.setFundManagerId(fundDirectFinancingBaseInfo.getFundManagerId());
        info.setProductFtpYieldRate(financingProductDetail.getFtpYieldRate());
        info.setFinancingProductId(financingProductDetail.getId());
        info.setAbbreviation(financingProductDetail.getAbbreviation());
        return info;
    }

   /* //构建直融ftp 基本信息
    private List<FtpIncomeBaseInfo> buildDirectFtpDetailRecord(FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, List<FundDirectFinancingProductDetail> financingProductDetails) {
        List<FtpIncomeBaseInfo> infos = new ArrayList<>();
        financingProductDetails.forEach(e -> {
            FtpIncomeBaseInfo info = new FtpIncomeBaseInfo();
            info.setFundFinancingId(fundDirectFinancingBaseInfo.getId());
            info.setFinancingType(FinancingTypeEnum.INDIRECT.name());
            info.setFinancingCode(fundDirectFinancingBaseInfo.getFinancingCode());
            info.setFinancingProductId(e.getId());
            info.setAbbreviation(e.getAbbreviation());
            info.setFinancingAmount(e.getIssuanceAmount());
            info.setFtpYieldRate(e.getFtpYieldRate());
            info.setFundManagerId(fundDirectFinancingBaseInfo.getFundManagerId());
            infos.add(info);
        });
        return infos;
    }
*/
    //构建间融数据明细
    private List<FtpIncomeDetailRecord> buildFtpDetailRecord(FundFinancingBaseInfo financingBaseInfo, FtpIncomeBaseInfo ftpIncomeBaseInfo) {
        //获取区间内日期
        List<LocalDate> daysBetween = getDaysBetween(financingBaseInfo.getActualLoanDate(), financingBaseInfo.getActualExpireDate());
        //获取间融每天的剩余本金
        List<FundReceiptFlowDetail> financingRepayDetail = fundReceiptRepayBaseInfoService.getFinancingRepayDetail(financingBaseInfo.getId(), FinancingTypeEnum.INDIRECT.name());
        Map<LocalDate, Long> everyDayRemainingPrincipalMap = getEveryDayRemainingPrincipal(financingRepayDetail, daysBetween, financingBaseInfo.getFinancingAmount());
        //计算每天的ftp收益
        List<FtpIncomeDetailRecord> records = new ArrayList<>();
        //获取日利率
        BigDecimal dayFtpUpper = new BigDecimal(ftpIncomeBaseInfo.getFtpYieldRate().toString()).divide(new BigDecimal(YEAR_DAY), 10, RoundingMode.HALF_UP);
        BigDecimal dayFtp = dayFtpUpper.divide(new BigDecimal("1000000"), 10, RoundingMode.HALF_UP);
        Map<Integer, Long> year2IncomeCurrentYear = new HashMap<>();
        //本日剩余本金（元）*FTP收益日利率（%）
        Long dayAmount;
        for (LocalDate date : daysBetween) {
            dayAmount = everyDayRemainingPrincipalMap.getOrDefault(date, 0L);
            FtpIncomeDetailRecord record = new FtpIncomeDetailRecord();
            record.setFtpIncomeId(ftpIncomeBaseInfo.getId());
            record.setInterestDate(date);
            record.setRemainingPrincipal(dayAmount);
            record.setFtpYieldRate(ftpIncomeBaseInfo.getFtpYieldRate());
            record.setFtpYieldRateDay(dayFtpUpper);
            //本日剩余本金（元）*FTP收益日利率（%）
            record.setFtpIncome(new BigDecimal(dayAmount.toString()).multiply(dayFtp).longValue());
            Integer year = date.getYear();
            record.setFtpIncomeCurrentYear(year2IncomeCurrentYear.getOrDefault(year, 0L) + record.getFtpIncome());
            year2IncomeCurrentYear.put(year, record.getFtpIncomeCurrentYear());
            records.add(record);
        }
        return records;
    }

    //构建直融数据明细
    private List<FtpIncomeDetailRecord> buildDirectFtpDetailRecord(FundDirectFinancingBaseInfo directFinancingBaseInfo, FtpIncomeBaseInfo ftpIncomeBaseInfo) {
        //获取区间内日期
        List<LocalDate> daysBetween = getDaysBetween(directFinancingBaseInfo.getDurationFrom(), directFinancingBaseInfo.getDurationTo());
        //获取间融每天的剩余本金
        List<FundReceiptFlowDetail> financingRepayDetail = fundReceiptRepayBaseInfoService.getFinancingRepayDetail(directFinancingBaseInfo.getId(), FinancingTypeEnum.DIRECT.name());
        Map<LocalDate, Long> everyDayRemainingPrincipalMap = getEveryDayRemainingPrincipal(financingRepayDetail, daysBetween, LongUtil.other2Long(LongUtil.null2zero(directFinancingBaseInfo.getFinancingAmount()).toString()));
        //计算每天的ftp收益
        List<FtpIncomeDetailRecord> records = new ArrayList<>();
        //获取日利率
        BigDecimal dayFtpUpper = new BigDecimal(ftpIncomeBaseInfo.getFtpYieldRate().toString()).divide(new BigDecimal(YEAR_DAY), 10, RoundingMode.HALF_UP);
        BigDecimal dayFtp = dayFtpUpper.divide(new BigDecimal("1000000"), 10, RoundingMode.HALF_UP);
        Map<Integer, Long> year2IncomeCurrentYear = new HashMap<>();
        //本日剩余本金（元）*FTP收益日利率（%）
        Long dayAmount;
        for (LocalDate date : daysBetween) {
            dayAmount = everyDayRemainingPrincipalMap.getOrDefault(date, 0L);
            FtpIncomeDetailRecord record = new FtpIncomeDetailRecord();
            record.setFtpIncomeId(ftpIncomeBaseInfo.getId());
            record.setInterestDate(date);
            record.setRemainingPrincipal(dayAmount);
            record.setFtpYieldRate(ftpIncomeBaseInfo.getFtpYieldRate());
            record.setFtpYieldRateDay(dayFtpUpper);
            //本日剩余本金（元）*FTP收益日利率（%）
            record.setFtpIncome(new BigDecimal(dayAmount.toString()).multiply(dayFtp).longValue());
            Integer year = date.getYear();
            record.setFtpIncomeCurrentYear(year2IncomeCurrentYear.getOrDefault(year, 0L) + record.getFtpIncome());
            year2IncomeCurrentYear.put(year, record.getFtpIncomeCurrentYear());
            records.add(record);
        }
        return records;
    }

    //构建直融数据明细
    private List<FtpIncomeDetailRecord> buildDirectAbsAbnFtpDetailRecord(FundDirectFinancingProductDetail financingProductDetail, FtpIncomeBaseInfo ftpIncomeBaseInfo, FundDirectRepayActualSplitRSP repayActualSplitRSP) {
        //获取区间内日期
        List<LocalDate> daysBetween = getDaysBetween(financingProductDetail.getValueDate(), financingProductDetail.getExpectedExpirationDate());
        //获取直融每天的剩余本金
        List<FundDirectFinancingRepayActualSplitRecord> productRepayDetail = fundDirectFinancingRepayActualSplitRecordService.getProductRepayDetail(financingProductDetail.getId(), FinancePaymentWriteOffOrderEnum.PRINCIPAL.name());
        Map<String, String> cashFlowCode2RepayDate = new HashMap<>();
        if (ObjectUtil.isNotEmpty(repayActualSplitRSP) && CollectionUtil.isNotEmpty(repayActualSplitRSP.getCashFlowList())) {
            cashFlowCode2RepayDate.putAll(repayActualSplitRSP.getCashFlowList().stream().filter(e -> ObjectUtil.isNotEmpty(e.getRepayDate())).collect(Collectors.toMap(FundDirectRepayActualSplitRSP.RepayData::getCashFlowCode, FundDirectRepayActualSplitRSP.RepayData::getRepayDate, (a, b) -> b)));
        }

        //repayActualSplitRSPS
        Map<LocalDate, Long> everyDayRemainingPrincipalMap = getDirectEveryDayRemainingPrincipal(productRepayDetail, daysBetween, financingProductDetail.getIssuanceAmount(), cashFlowCode2RepayDate);
        //计算每天的ftp收益
        List<FtpIncomeDetailRecord> records = new ArrayList<>();
        //获取日利率
        BigDecimal dayFtpUpper = new BigDecimal(ftpIncomeBaseInfo.getProductFtpYieldRate().toString()).divide(new BigDecimal(YEAR_DAY), 15, RoundingMode.HALF_UP);
        BigDecimal dayFtp = dayFtpUpper.divide(new BigDecimal("1000000"), 15, RoundingMode.HALF_UP);
        Map<Integer, Long> year2IncomeCurrentYear = new HashMap<>();
        //本日剩余本金（元）*FTP收益日利率（%）
        Long dayAmount;
        for (LocalDate date : daysBetween) {
            dayAmount = everyDayRemainingPrincipalMap.getOrDefault(date, 0L);
            FtpIncomeDetailRecord record = new FtpIncomeDetailRecord();
            record.setFtpIncomeId(ftpIncomeBaseInfo.getId());
            record.setInterestDate(date);
            record.setRemainingPrincipal(dayAmount);
            record.setFtpYieldRate(ftpIncomeBaseInfo.getProductFtpYieldRate());
            record.setFtpYieldRateDay(dayFtpUpper);
            //本日剩余本金（元）*FTP收益日利率（%）
            record.setFtpIncome(new BigDecimal(dayAmount.toString()).multiply(dayFtp).longValue());
            Integer year = date.getYear();
            record.setFtpIncomeCurrentYear(year2IncomeCurrentYear.getOrDefault(year, 0L) + record.getFtpIncome());
            year2IncomeCurrentYear.put(year, record.getFtpIncomeCurrentYear());
            records.add(record);
        }
        return records;
    }


    //获取直融每天的剩余本金
    private Map<LocalDate, Long> getDirectEveryDayRemainingPrincipal(List<FundDirectFinancingRepayActualSplitRecord> financingRepayDetail, List<LocalDate> everyDay, Long financingAmount, Map<String, String> cashFlowCode2RepayDate) {
        if (ObjectUtil.isEmpty(everyDay) || ObjectUtil.isEmpty(financingAmount) || financingAmount <= 0) {
            return MapUtil.empty();
        }
        financingAmount = LongUtil.other2Long(financingAmount.toString());
        Map<LocalDate, List<FundDirectFinancingRepayActualSplitRecord>> date2ReceiptFlowDetail = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        financingRepayDetail.forEach(e -> {
            String s = cashFlowCode2RepayDate.get(e.getCashFlowCode());
            if (ObjectUtil.isEmpty(s)) {
                return;
            }
            LocalDate localDate = LocalDate.parse(s, formatter);
            List<FundDirectFinancingRepayActualSplitRecord> orDefault = date2ReceiptFlowDetail.getOrDefault(localDate, new ArrayList<>());
            orDefault.add(e);
            date2ReceiptFlowDetail.put(localDate, orDefault);
        });

        Map<LocalDate, Long> day2RemainingPrincipal = new HashMap<>();
        for (LocalDate day : everyDay) {
            List<FundDirectFinancingRepayActualSplitRecord> fundReceiptFlowDetails = date2ReceiptFlowDetail.get(day);
            if (ObjectUtil.isNotEmpty(fundReceiptFlowDetails)) {
                financingAmount = financingAmount - fundReceiptFlowDetails.stream().map(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
            }
            day2RemainingPrincipal.put(day, financingAmount);
        }
        return day2RemainingPrincipal;
    }

    //获取直融每天的剩余本金
    private Map<LocalDate, Long> getEveryDayRemainingPrincipal(List<FundReceiptFlowDetail> financingRepayDetail, List<LocalDate> everyDay, Long financingAmount) {
        if (ObjectUtil.isEmpty(everyDay) || ObjectUtil.isEmpty(financingAmount) || financingAmount <= 0) {
            return MapUtil.empty();
        }
        Map<LocalDate, List<FundReceiptFlowDetail>> date2ReceiptFlowDetail = financingRepayDetail.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowDate));
        Map<LocalDate, Long> day2RemainingPrincipal = new HashMap<>();
        for (LocalDate day : everyDay) {
            List<FundReceiptFlowDetail> fundReceiptFlowDetails = date2ReceiptFlowDetail.get(day);
            if (ObjectUtil.isNotEmpty(fundReceiptFlowDetails)) {
                financingAmount = financingAmount - fundReceiptFlowDetails.stream().map(FundReceiptFlowDetail::getPrincipalAmount).filter(ObjectUtil::isNotEmpty).reduce(Long::sum).orElse(0L);
            }
            day2RemainingPrincipal.put(day, financingAmount);
        }
        return day2RemainingPrincipal;
    }

    private List<LocalDate> getDaysBetween(LocalDate from, LocalDate to) {
        if (ObjectUtil.isEmpty(from) || ObjectUtil.isEmpty(to)) {
            return ListUtil.empty();
        }
        // 计算天数差
        long daysBetween = ChronoUnit.DAYS.between(from, to);

        // 生成日期列表
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i <= daysBetween; i++) {
            dates.add(from.plusDays(i));
        }
        return dates;
    }


}
