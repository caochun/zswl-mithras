package cn.zswltech.mithras.service.service.newftp.service.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.newftp.NewFtpParameterDTO;
import cn.zswltech.mithras.service.enums.newftp.FluctuationValueEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.convert.NewFtpTreasuryBondYieldConfigConverter;
import cn.zswltech.mithras.service.service.newftp.excel.NewFtpTreasuryBondYieldExcelModel;
import cn.zswltech.mithras.service.service.newftp.excel.NewFtpTreasuryBondYieldImporter;
import cn.zswltech.mithras.service.service.newftp.mapper.config.NewFtpTreasuryBondYieldConfigMapper;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpParameterSettingConfig;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpTreasuryBondYieldConfig;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import cn.zswltech.mithras.service.service.newftp.service.drift.NewFtpParameterSettingDraftService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【new_ftp_treasury_bond_yield_config(十年期国债收益率配置表)】的数据库操作Service实现
 * @createDate 2024-03-22 14:00:38
 */
@Service
@Slf4j
public class NewFtpTreasuryBondYieldConfigService extends ServiceImpl<NewFtpTreasuryBondYieldConfigMapper, NewFtpTreasuryBondYieldConfig> {
    @Resource
    private NewFtpTreasuryBondYieldImporter importer;
    @Resource
    private NewFtpTreasuryBondYieldConfigConverter converter;
    @Resource
    private NewFtpTreasuryBondYieldPricingConfigService bondYieldPricingConfigService;
    @Resource
    private NewFtpParameterSettingDraftService parameterSettingDraftService;
    @Resource
    private NewFtpParameterSettingConfigService newFtpParameterSettingConfigService;
    @Resource
    private ExpressRunner expressRunner;

    public static final String PARAM_NAME = "十年期国债收益率波动计价标准";

    @Transactional(rollbackFor = Throwable.class)
    public void importFile(InputStream inputStream) {
        List<NewFtpTreasuryBondYieldExcelModel> parse = importer.parse(inputStream);
        parse.removeIf(e -> e.getDate() == null || e.getValue() == null);
        NewFtpTreasuryBondYieldConfig lastest = baseMapper.selectOne(
                Wrappers.<NewFtpTreasuryBondYieldConfig>lambdaQuery()
                        .orderByDesc(NewFtpTreasuryBondYieldConfig::getDate)
                        .last(StringUtil.mysqlLimitOne()));
        /**
         * 保存数据
         */
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
        List<LocalDate> monthsNeedCalSortedList = new ArrayList<>(monthsNeedCal).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        monthsNeedCalSortedList.forEach(month -> {
            List<NewFtpTreasuryBondYieldConfig> list = list(
                    Wrappers.<NewFtpTreasuryBondYieldConfig>lambdaQuery()
                            .ge(NewFtpTreasuryBondYieldConfig::getDate, month)
                            .le(NewFtpTreasuryBondYieldConfig::getDate, month.with(TemporalAdjusters.lastDayOfMonth())));
            int monthTotal = list.stream().map(NewFtpTreasuryBondYieldConfig::getValue).reduce(0, Integer::sum);
            BigDecimal average = new BigDecimal(monthTotal)
                    .divide(new BigDecimal(list.size()), 0, RoundingMode.HALF_UP);
            Map<LocalDate, NewFtpTreasuryBondYieldPricingConfig> map = bondYieldPricingConfigService.list(
                            Wrappers.<NewFtpTreasuryBondYieldPricingConfig>lambdaQuery()
                                    .in(NewFtpTreasuryBondYieldPricingConfig::getMonth, month, month.minusMonths(1), month.minusMonths(2)))
                    .stream().collect(Collectors.toMap(NewFtpTreasuryBondYieldPricingConfig::getMonth, e -> e));
            NewFtpTreasuryBondYieldPricingConfig curMonth = map.get(month);
            if (curMonth == null) {
                curMonth = new NewFtpTreasuryBondYieldPricingConfig();
                curMonth.setMonth(month);
            }
            curMonth.setAverage(average.intValue());

            NewFtpTreasuryBondYieldPricingConfig preMonth = map.get(month.minusMonths(1));
            if (preMonth != null) {
                curMonth.setFluctuationRange(average.intValue() - preMonth.getAverage());
            } else {
                curMonth.setFluctuationRange(0);
            }
            // 计算Ftp计价
            curMonth.setFtpPricing(calculateFtpPricing(curMonth));
            bondYieldPricingConfigService.saveOrUpdate(curMonth);
        });
    }

    private Integer calculateFtpPricing(NewFtpTreasuryBondYieldPricingConfig pricing) {
        NewFtpParameterSettingConfig newFtpParameterSettingConfig = newFtpParameterSettingConfigService.getOne(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
        .eq(NewFtpParameterSettingConfig::getParamName, PARAM_NAME)
        .orderByDesc(NewFtpParameterSettingConfig::getId)
        .last(StringUtil.mysqlLimitOne()));
        if(ObjectUtil.isEmpty(newFtpParameterSettingConfig)){
            return 0;
        }
        if(FluctuationValueEnum.DIRECT.name().equals(newFtpParameterSettingConfig.getParamOtherName())){
            //直接取波动值
            return pricing.getFluctuationRange();
        } else {
            //取波动值
            Long aLong = LongUtil.other2Long(String.valueOf(analyticalFormula(newFtpParameterSettingConfig, LongUtil.tenThousand2Dollar(String.valueOf(pricing.getFluctuationRange())))));
            return Math.toIntExact(aLong);
        }
    }

    /**
     * 解析公式，原值入，原值出
     **/
    public BigDecimal analyticalFormula(NewFtpParameterSettingConfig newFtpParameterSettingConfig, BigDecimal fluctuationRange){
        if(ObjectUtil.isEmpty(newFtpParameterSettingConfig.getFormula())){
            return fluctuationRange;
        }
        List<NewFtpParameterDTO> newFtpParameterDTOS = JSONUtil.toList(newFtpParameterSettingConfig.getFormula(), NewFtpParameterDTO.class);
        if(ObjectUtil.isEmpty(newFtpParameterDTOS)){
            return fluctuationRange;
        }
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("T", String.valueOf(fluctuationRange));
        for(NewFtpParameterDTO dto : newFtpParameterDTOS){
            String fluctuationFormula = dto.getFluctuationFormula();
            if(ObjectUtil.isEmpty(fluctuationFormula)){
                return fluctuationRange;
            }
            //'in(1,3)'
            String format;
            if(fluctuationFormula.startsWith("=")){
                fluctuationFormula = fluctuationFormula.substring(1, fluctuationFormula.length());
            }

            if(fluctuationFormula.contains("if")){
                //自定义公式
                format = fluctuationFormula;
            } else if(isRange(fluctuationFormula)){
                //区间
                fluctuationFormula = fluctuationFormula.replace("(", "T>");
                fluctuationFormula = fluctuationFormula.replace("[", "T>=");
                fluctuationFormula = fluctuationFormula.replace(")", ">T");
                fluctuationFormula = fluctuationFormula.replace("]", ">=T");
                fluctuationFormula = "and(" + fluctuationFormula;
                fluctuationFormula = fluctuationFormula + ")";
                format = String.format("if(%s,true,false)", fluctuationFormula);
            } else if(StringUtil.isNumeric(fluctuationFormula)) {
                format = String.format("if(T==%s,true,false)", fluctuationFormula);
            } else {
                format = String.format("if(T%s,true,false)", fluctuationFormula);
            }
            try {
                if(((Boolean)expressRunner.execute(StringUtil.formatExcelFormula(format), context, null, true, false)).booleanValue()){
                    return new BigDecimal(dto.getMappingVal());
                }
            } catch (Exception e) {
                log.error("计算失败 {}", format, e);
                throw new MithrasException("计算失败");
            }
        }
        return fluctuationRange;
    }

    private boolean isRange(String value) {
        boolean startWith = value.startsWith("(") || value.startsWith("[");
        boolean endWith = value.endsWith(")") || value.endsWith("]");
        return startWith && endWith;
    }

}




