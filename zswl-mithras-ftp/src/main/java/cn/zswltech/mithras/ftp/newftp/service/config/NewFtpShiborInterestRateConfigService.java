package cn.zswltech.mithras.ftp.newftp.service.config;

import cn.zswltech.mithras.ftp.newftp.convert.NewFtpShiborInterestRateConfigConverter;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpShiborInterestRateExcelModel;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpShiborInterestRateImporter;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpShiborInterestRateConfigMapper;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRateConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRatePricingConfig;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpShiborInterestRatePricingConfigService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author yangxiong
* @description 针对表【new_ftp_shibor_interest_rate_config(一年期shibor利率配置表)】的数据库操作Service实现
* @createDate 2024-03-22 14:00:38
*/
@Service
public class NewFtpShiborInterestRateConfigService extends ServiceImpl<NewFtpShiborInterestRateConfigMapper, NewFtpShiborInterestRateConfig> {
    @Resource
    private NewFtpShiborInterestRateImporter importer;
    @Resource
    private NewFtpShiborInterestRateConfigConverter converter;
    @Resource
    private NewFtpShiborInterestRatePricingConfigService shiborInterestRatePricingConfigService;

    @Transactional(rollbackFor = Throwable.class)
    public void importFile(InputStream inputStream) {
        List<NewFtpShiborInterestRateExcelModel> parse = importer.parse(inputStream);
        parse.removeIf(e -> e.getDate() == null || e.getValue() == null);
        NewFtpShiborInterestRateConfig lastest = baseMapper.selectOne(
                Wrappers.<NewFtpShiborInterestRateConfig>lambdaQuery()
                        .orderByDesc(NewFtpShiborInterestRateConfig::getDate)
                        .last(StringUtil.mysqlLimitOne()));
        // 保存数据
        if (lastest == null) {
            saveBatch(converter.excelModel2entity(parse));
        } else {
            LocalDate date = lastest.getDate();
            parse.removeIf(e -> e.getDate().isBefore(date) || e.getDate().isEqual(date));
            saveBatch(converter.excelModel2entity(parse));
        }
        // 计算月均值
        Set<LocalDate> monthsNeedCal = parse.stream()
                .map(model -> model.getDate().with(TemporalAdjusters.firstDayOfMonth()))
                .collect(Collectors.toSet());
        List<LocalDate> monthsNeedCalSortedList = monthsNeedCal.stream().collect(Collectors.toList()).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        monthsNeedCalSortedList.forEach(month -> {
            List<NewFtpShiborInterestRateConfig> list = list(
                    Wrappers.<NewFtpShiborInterestRateConfig>lambdaQuery()
                            .ge(NewFtpShiborInterestRateConfig::getDate, month)
                            .lt(NewFtpShiborInterestRateConfig::getDate, month.with(TemporalAdjusters.lastDayOfMonth())));
            int monthTotal = list.stream().map(NewFtpShiborInterestRateConfig::getValue).reduce(0, Integer::sum);
            BigDecimal average = new BigDecimal(monthTotal)
                    .divide(new BigDecimal(list.size()), 0, RoundingMode.HALF_UP);
            Map<LocalDate, NewFtpShiborInterestRatePricingConfig> map = shiborInterestRatePricingConfigService.list(
                            Wrappers.<NewFtpShiborInterestRatePricingConfig>lambdaQuery()
                                    .in(NewFtpShiborInterestRatePricingConfig::getMonth, month, month.minusMonths(1), month.minusMonths(2))).stream()
                    .collect(Collectors.toMap(NewFtpShiborInterestRatePricingConfig::getMonth, e -> e, (a, b) -> a));
            NewFtpShiborInterestRatePricingConfig curMonth = map.get(month);
            if (curMonth == null) {
                curMonth = new NewFtpShiborInterestRatePricingConfig();
                curMonth.setMonth(month);
            }
            curMonth.setAverage(average.intValue());

            NewFtpShiborInterestRatePricingConfig preMonth = map.get(month.minusMonths(1));
            if (preMonth != null) {
                curMonth.setFtpPricing(curMonth.getAverage() - preMonth.getAverage());
            } else {
                curMonth.setFtpPricing(0);
            }
            shiborInterestRatePricingConfigService.saveOrUpdate(curMonth);
        });
    }
}



