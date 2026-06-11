package cn.zswltech.mithras.application.orchestration.job.ftp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.ftp.oldftp.job.FtpIncomeRateInitJob;
import cn.zswltech.mithras.ftp.oldftp.job.data_init.dto.FtpIncomeDetailExcelModel;
import cn.zswltech.mithras.ftp.oldftp.job.data_init.dto.FtpIncomeRateExcelModel;
import cn.zswltech.mithras.ftp.oldftp.job.data_init.dto.ZsRentLeaseAbsExcelModel;
import cn.zswltech.mithras.ftp.oldftp.service.job.FtpIncomeRateInitJobService;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingProductDetailService;
import cn.zswltech.mithras.ftp.oldftp.model.FtpIncomeBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpIncomeDetailRecord;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.application.orchestration.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.ftp.oldftp.service.FtpIncomeDetailRecordService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @version 1.0
 * @description FTP收益率初始化任务
 * @since 2025/8/21 16:08
 **/
@Slf4j
@Component
public class FtpIncomeRateInitJobServiceImpl implements FtpIncomeRateInitJobService {

    @Autowired
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Autowired
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Autowired
    private FundDirectFinancingProductDetailService directFinancingProductDetailService;
    @Autowired
    private FtpIncomeBaseInfoService baseInfoService;
    @Autowired
    private FtpIncomeDetailRecordService detailRecordService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void ftpIncomeRateInitJob() {
        try {
            // 读取resources下的doc/ftp_income_rate.xlsx的两个sheet页面
            InputStream ins = FtpIncomeRateInitJob.class.getResourceAsStream("/doc/ftp_income_rate.xlsx");
            List<FtpIncomeRateExcelModel> baseInfoList = ExcelUtil.getReader(ins).read(0, 0, FtpIncomeRateExcelModel.class);
            ins.close();
            ins = FtpIncomeRateInitJob.class.getResourceAsStream("/doc/ftp_income_rate.xlsx");
            List<FtpIncomeDetailExcelModel> recordList = ExcelUtil.getReader(ins, 1).read(0, 0, FtpIncomeDetailExcelModel.class);
            ins.close();
            ins = FtpIncomeRateInitJob.class.getResourceAsStream("/doc/ftp_income_rate.xlsx");
            List<ZsRentLeaseAbsExcelModel> productList = ExcelUtil.getReader(ins, 2).read(0, 0, ZsRentLeaseAbsExcelModel.class);
            ins.close();
            Map<String, ZsRentLeaseAbsExcelModel> absExcelModelMap = productList.stream().collect(Collectors.toMap(ZsRentLeaseAbsExcelModel::getLoanName, Function.identity()));

            // 查询已有数据，存在融资编号的，无需处理
            List<FtpIncomeBaseInfo> ftpIncomeBaseInfoList = baseInfoService.list();
            if (CollUtil.isNotEmpty(ftpIncomeBaseInfoList)) {
                List<String> ftpIncomeCodeList = ftpIncomeBaseInfoList.stream().map(FtpIncomeBaseInfo::getFinancingCode).collect(Collectors.toList());
                baseInfoList = baseInfoList.stream().filter(baseInfo -> !ftpIncomeCodeList.contains(baseInfo.getFinanceCode())).collect(Collectors.toList());
            }
            // 过滤掉期初的那一行数据
            Map<String, List<FtpIncomeDetailExcelModel>> detailsMap = recordList.stream().filter(e -> !"期初".equals(e.getDealTime()))
                    .filter(e -> CharSequenceUtil.isNotBlank(e.getBalance()))
                    .collect(Collectors.groupingBy(FtpIncomeDetailExcelModel::getFinanceCode));

            // 提前找到直融和间融的数据
            Map<String, FundFinancingBaseInfo> fundFinancingBaseInfoMap = financingBaseInfoService.list().stream().collect(Collectors.toMap(FundFinancingBaseInfo::getFinancingCode, Function.identity()));
            List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list();
            Map<String, FundDirectFinancingBaseInfo> directFundFinancingBaseInfoMap = directFinancingBaseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getFinancingCode, Function.identity()));
            Map<String, FundDirectFinancingProductDetail> productDetailMap = directFinancingProductDetailService.list(Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                            .in(FundDirectFinancingProductDetail::getFinancingId, directFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList())))
                    .stream().collect(Collectors.toMap(FundDirectFinancingProductDetail::getAbbreviation, Function.identity()));
            for (FtpIncomeRateExcelModel ftpIncomeRateExcelModel : baseInfoList) {
                // 构建基本信息
                FtpIncomeBaseInfo ftpIncomeBaseInfo = new FtpIncomeBaseInfo();
                // 基本信息只能是直融和间融的一种
                FundFinancingBaseInfo fundFinancingBaseInfo = fundFinancingBaseInfoMap.get(ftpIncomeRateExcelModel.getFinanceCode());
                FundDirectFinancingBaseInfo directFinancingBaseInfo = directFundFinancingBaseInfoMap.get(ftpIncomeRateExcelModel.getFinanceCode());
                ftpIncomeBaseInfo.setFinancingCode(ftpIncomeRateExcelModel.getFinanceCode());
                ftpIncomeBaseInfo.setFinancingType(fundFinancingBaseInfo != null ? FinancingTypeEnum.INDIRECT.name() : FinancingTypeEnum.DIRECT.name());
                ftpIncomeBaseInfo.setFinancingAmount(fundFinancingBaseInfo != null ? fundFinancingBaseInfo.getFinancingAmount() : directFinancingBaseInfo.getFinancingAmount() * 10000);
                // 将4.40% 转换成44000
                ftpIncomeBaseInfo.setFtpYieldRate(new BigDecimal(ftpIncomeRateExcelModel.getFtpIncomeRate().replaceAll("%", "")).multiply(BigDecimal.valueOf(1000000)).intValue());
                ftpIncomeBaseInfo.setFundManagerId(fundFinancingBaseInfo != null ? fundFinancingBaseInfo.getFundManagerId() : directFinancingBaseInfo.getFundManagerId());
                ftpIncomeBaseInfo.setFundFinancingId(fundFinancingBaseInfo != null ? fundFinancingBaseInfo.getId() : directFinancingBaseInfo.getId());

                baseInfoService.save(ftpIncomeBaseInfo);

                // 处理详情
                List<FtpIncomeDetailExcelModel> details = detailsMap.get(ftpIncomeRateExcelModel.getFinanceCode());
                if (CollUtil.isNotEmpty(details)) {
                    details = details.stream()
                            .filter(item -> new BigDecimal(item.getRepayPrincipal()).compareTo(BigDecimal.ZERO) > 0
                                    || new BigDecimal(item.getBalance()).compareTo(BigDecimal.ZERO) > 0)
                            .filter(item -> {
                                LocalDate localDate = LocalDateTimeUtil.parseDate(item.getDealTime(), "yyyy-MM-dd HH:mm:ss");
                                if (fundFinancingBaseInfo != null
                                        && !localDate.isAfter(fundFinancingBaseInfo.getActualExpireDate())
                                        && !localDate.isBefore(fundFinancingBaseInfo.getActualLoanDate())) {
                                    return true;
                                } else {
                                    return directFinancingBaseInfo != null
                                            && !localDate.isBefore(directFinancingBaseInfo.getCarryInterestTime())
                                            && !localDate.isAfter(directFinancingBaseInfo.getDurationTime());
                                }
                            }).collect(Collectors.toList());
                }
                if (CollUtil.isEmpty(details)) {
                    return;
                }
                details.sort(Comparator.comparing(FtpIncomeDetailExcelModel::getDealTime));
                CopyOnWriteArrayList<FtpIncomeDetailExcelModel> copyOnWriteArrayList = ListUtil.toCopyOnWriteArrayList(details);
                copyOnWriteArrayList.sort(Comparator.comparing(FtpIncomeDetailExcelModel::getDealTime));
                if (CollUtil.isNotEmpty(details)) {
                    List<FtpIncomeDetailRecord> needSaveList = new ArrayList<>();
                    List<FtpIncomeBaseInfo> needUpdateList = new ArrayList<>();
                    for (FtpIncomeDetailExcelModel detail : details) {
                        FtpIncomeDetailRecord incomeDetailRecord = new FtpIncomeDetailRecord();
                        incomeDetailRecord.setFtpIncomeId(ftpIncomeBaseInfo.getId());
                        incomeDetailRecord.setInterestDate(LocalDateTimeUtil.parseDate(detail.getDealTime(), "yyyy-MM-dd HH:mm:ss"));
                        incomeDetailRecord.setRemainingPrincipal(new BigDecimal(detail.getBalance()).multiply(BigDecimal.valueOf(10000)).longValue());
                        incomeDetailRecord.setFtpYieldRate(new BigDecimal(detail.getFtpIncomeRate().replaceAll("%", "")).multiply(BigDecimal.valueOf(1000000)).intValue());
                        incomeDetailRecord.setFtpYieldRateDay(new BigDecimal(incomeDetailRecord.getFtpYieldRate())
                                .divide(new BigDecimal(360), 2, RoundingMode.HALF_UP));

                        // 计算截止到当前日期的年累计收益
                        Optional<BigDecimal> reduce = copyOnWriteArrayList.stream().filter(e -> !LocalDateTimeUtil.parseDate(e.getDealTime(), "yyyy-MM-dd HH:mm:ss")
                                        .isAfter(incomeDetailRecord.getInterestDate()))
                                .map(e -> new BigDecimal(e.getFtpIncome()).multiply(new BigDecimal(10000))).reduce(BigDecimal::add);
                        reduce.ifPresent(bigDecimal -> incomeDetailRecord.setFtpIncomeCurrentYear(bigDecimal.longValue()));
                        incomeDetailRecord.setFtpIncome(new BigDecimal(detail.getFtpIncome()).multiply(new BigDecimal(10000)).longValue());
                        needSaveList.add(incomeDetailRecord);
                        ZsRentLeaseAbsExcelModel absExcelModel = absExcelModelMap.get(detail.getLoanName());
                        if (Objects.nonNull(absExcelModel)) {
                            ftpIncomeBaseInfo.setAbbreviation(absExcelModel.getStockAbbreviation());
                            FundDirectFinancingProductDetail productDetail = productDetailMap.get(absExcelModel.getStockAbbreviation());
                            ftpIncomeBaseInfo.setFinancingProductId(productDetail.getId());
                            ftpIncomeBaseInfo.setProductFtpYieldRate(productDetail.getFtpYieldRate());
                            if (!needUpdateList.stream().map(FtpIncomeBaseInfo::getFinancingCode).collect(Collectors.toList()).contains(ftpIncomeBaseInfo.getFinancingCode())) {
                                needUpdateList.add(ftpIncomeBaseInfo);
                            }
                        }
                    }
                    if (!needSaveList.isEmpty()) {
                        detailRecordService.saveBatch(needSaveList);
                    }
                    if (!needUpdateList.isEmpty()) {
                        baseInfoService.updateBatchById(needUpdateList);
                    }
                }

            }
        } catch (
                Exception e) {
            throw new RuntimeException(e);
        }
    }
}
