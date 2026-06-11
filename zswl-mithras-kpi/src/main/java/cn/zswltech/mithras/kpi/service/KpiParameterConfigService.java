package cn.zswltech.mithras.kpi.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ExpenseRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProvisionRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.TaxRateConfig;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.kpi.enums.KpiConfigValueTypeEnum;
import cn.zswltech.mithras.kpi.enums.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectSourceEnum;
import cn.zswltech.mithras.kpi.enums.config.TaxRateEnum;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.kpi.mapper.KpiParameterConfigMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KpiParameterConfigService extends ServiceImpl<KpiParameterConfigMapper, KpiParameterConfig> {
    @Resource
    private ExpressRunner expressRunner;

    public PageR<KpiParameterConfigListRSP> pageList(KpiParameterConfigListREQ req) {
        if (StrUtil.isBlank(req.getQueryFlag())){
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        //查询对应的参数设置，项目利润 TAX_RATE("税率维护"),PROVISION_RADIO("拨备计提比例"),EXPENSE_RADIO("费用计提比例"); 绩效考核 PROJECT_RADIO("项目提奖比例")
        List<KpiParameterConfigCodeEnum> profitList = Arrays.asList(KpiParameterConfigCodeEnum.TAX_RATE,KpiParameterConfigCodeEnum.PROVISION_RADIO, KpiParameterConfigCodeEnum.EXPENSE_RADIO);
        List<KpiParameterConfigCodeEnum> list2 = Arrays.asList(KpiParameterConfigCodeEnum.PROJECT_RADIO);
        Page<KpiParameterConfig> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<KpiParameterConfig> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.like(StrUtil.isNotBlank(req.getConfigDesc()), KpiParameterConfig::getConfigDesc, req.getConfigDesc());
        if (req.getQueryFlag().equals("1")){
            conditionQuery.in(KpiParameterConfig::getConfigCode, profitList);
        }else if (req.getQueryFlag().equals("2")){
            conditionQuery.in(KpiParameterConfig::getConfigCode, list2);
        } else if (ObjectUtil.isNotEmpty(req.getQueryEnums())) {
            conditionQuery.in(KpiParameterConfig::getConfigCode, req.getQueryEnums());
        }
        Page<KpiParameterConfig> dbResult = this.page(pageQuery, conditionQuery);
        if (CollectionUtil.isEmpty(dbResult.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<KpiParameterConfigListRSP> result = dbResult.getRecords().stream().map(item -> {
            KpiParameterConfigListRSP rsp = new KpiParameterConfigListRSP();
            rsp.setId(item.getId());
            rsp.setConfigCode(item.getConfigCode());
            rsp.setConfigDesc(item.getConfigDesc());
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(result, dbResult.getTotal(), req.getPage(), req.getPageSize());
    }

    public KpiParameterConfig getOneByConfigCode(KpiParameterConfigCodeEnum configCodeEnum) {
        return getOneByConfigCode(configCodeEnum, null);
    }

    public KpiParameterConfig getOneByConfigCode(KpiParameterConfigCodeEnum configCodeEnum, Long baseId) {
        LambdaQueryWrapper<KpiParameterConfig> query = Wrappers.lambdaQuery();
        query.eq(KpiParameterConfig::getConfigCode, configCodeEnum.name());
        query.eq(ObjectUtil.isNotEmpty(baseId), KpiParameterConfig::getParameterBaseId, baseId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    /**
     * 获取税率 如:0.06
     * @param taxRateEnum
     * @return
     */
    public BigDecimal getTaxRate(TaxRateEnum taxRateEnum){
        KpiParameterConfig oneByConfigCode = this.getOneByConfigCode(KpiParameterConfigCodeEnum.TAX_RATE);
        if(oneByConfigCode == null){
            throw new MithrasException("尚未维护税率");
        }
        List<TaxRateConfig.Data> data;
        try {
            data = JSON.parseArray(oneByConfigCode.getConfigValue(), TaxRateConfig.Data.class);
        }catch (JSONException e){
            throw new MithrasException("税率表格式异常");
        }
        Map<String, Map<String, List<TaxRateConfig.Data>>> map = data.stream().collect(Collectors.groupingBy(TaxRateConfig.Data::getBizType,
                Collectors.groupingBy(TaxRateConfig.Data::getTaxType)));
        List<TaxRateConfig.Data> dataList = Optional.ofNullable(map.get(taxRateEnum.getBizType())).orElse(new HashMap<>()).get(taxRateEnum.getTaxType());
        if(dataList.size() != 1){
            throw new MithrasException("存在重复的税率信息");
        }
        String taxRate = Optional.ofNullable(dataList.get(0)).orElse(new TaxRateConfig.Data()).getTaxRate();
        return new BigDecimal(Optional.ofNullable(taxRate).orElse("0")).divide(new BigDecimal("100"));
    }

    public KpiParameterConfig getOneWithCheck(KpiParameterConfigCodeEnum configCodeEnum) {
        KpiParameterConfig kpiParameterConfig = this.getOneByConfigCode(configCodeEnum);
        Assert.notNull(kpiParameterConfig, () -> MithrasException.newException(String.format("参数设置不存在[%s]", configCodeEnum.getDesc())));
        return getOneWithCheck(configCodeEnum, null);
    }

    public KpiParameterConfig getOneWithCheck(KpiParameterConfigCodeEnum configCodeEnum, Long baseId) {
        KpiParameterConfig kpiParameterConfig = this.getOneByConfigCode(configCodeEnum, baseId);
        Assert.notNull(kpiParameterConfig, () -> MithrasException.newException(String.format("参数设置不存在[%s]", configCodeEnum.getDesc())));
        return kpiParameterConfig;
    }

    public void modifyConfigValue(KpiParameterConfigCodeEnum configCodeEnum, String configValue) {
        this.modifyConfigValue(configCodeEnum, configValue, null);
    }

    public void modifyConfigValue(KpiParameterConfigCodeEnum configCodeEnum, String configValue, Long baseId) {
        KpiParameterConfig kpiParameterConfig = this.getOneWithCheck(configCodeEnum, baseId);
        KpiParameterConfig toUpdate = new KpiParameterConfig();
        toUpdate.setId(kpiParameterConfig.getId());
        toUpdate.setConfigValue(configValue);
        this.updateById(toUpdate);
    }

    public BigDecimal ensureProjectRadio(String configValue, String projClassify, LocalDate contractStartDate, LocalDate targetDate) {
        if (StrUtil.isBlank(configValue)) {
            return null;
        }
        List<JSONObject> jsonObjectList = JSONUtil.toList(configValue, JSONObject.class);
        if (CollectionUtil.isEmpty(jsonObjectList)) {
            return null;
        }
        String projSource;
        // 当月-投放日期所在月，如果小于等于12，则投放时效=新增； 否则投放时效=存量
        long diff = LocalDateTimeUtil.between(contractStartDate.atStartOfDay(), targetDate.atStartOfDay(), ChronoUnit.MONTHS);
        if (diff <= 12) {
            projSource = KpiProjectSourceEnum.NEW.name();
        } else {
            projSource = KpiProjectSourceEnum.HISTORY.name();
        }
        for (JSONObject jsonObject : jsonObjectList) {
            String classify = jsonObject.getStr("projectType");
            String source = jsonObject.getStr("projectSource");
            String projectRadio = jsonObject.getStr("projectRadio");
            if (Objects.equals(classify, projClassify) && Objects.equals(source, projSource)) {
                return new BigDecimal(projectRadio).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            }
        }
        return null;
    }

    public BigDecimal ensureZZSRate(String bizType, String leaseType) {
        KpiParameterConfig kpiParameterConfig = this.getOneByConfigCode(KpiParameterConfigCodeEnum.TAX_RATE);
        if (Objects.isNull(kpiParameterConfig)) {
            throw new MithrasException("没有设置税率参数");
        }
        if (StrUtil.isBlank(kpiParameterConfig.getConfigValue())) {
            throw new MithrasException("税率参数为空值");
        }
        TaxRateEnum taxRateEnum;
        if (Objects.equals(bizType, ProjectBizType.ZR.name())) {
            taxRateEnum = TaxRateEnum.ZZS_ZR;
        } else if (Objects.equals(bizType, ProjectBizType.BL.name())) {
            taxRateEnum = TaxRateEnum.ZZS_BL;
        } else {
            if (Objects.equals(leaseType, LeaseType.jyx_zu.name())) {
                taxRateEnum = TaxRateEnum.ZZS_ZL_JYX;
            } else if (Objects.equals(leaseType, LeaseType.zhi_zu.name())) {
                taxRateEnum = TaxRateEnum.ZZS_ZL_ZZ;
            } else {
                taxRateEnum = TaxRateEnum.ZZS_ZL_HZ;
            }
        }
        List<TaxRateConfig.Data> dataList = JSONUtil.toList(kpiParameterConfig.getConfigValue(), TaxRateConfig.Data.class);
        for (TaxRateConfig.Data data : dataList) {
            if (Objects.equals(data.getBizType(), taxRateEnum.getBizType()) && Objects.equals(data.getTaxType(), taxRateEnum.getTaxType())) {
                return new BigDecimal(data.getTaxRate()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            }
        }
        return null;
    }


    public BigDecimal ensureXMSRate(String bizType, String leaseType) {
        KpiParameterConfig kpiParameterConfig = this.getOneByConfigCode(KpiParameterConfigCodeEnum.TAX_RATE);
        if (Objects.isNull(kpiParameterConfig)) {
            throw new MithrasException("没有设置税率参数");
        }
        if (StrUtil.isBlank(kpiParameterConfig.getConfigValue())) {
            throw new MithrasException("税率参数为空值");
        }
        TaxRateEnum taxRateEnum;
        if (Objects.equals(bizType, ProjectBizType.ZR.name())) {
            taxRateEnum = TaxRateEnum.ZZS_ZR;
        } else if (Objects.equals(bizType, ProjectBizType.BL.name())) {
            taxRateEnum = TaxRateEnum.ZZS_BL;
        } else {
            if (Objects.equals(leaseType, LeaseType.jyx_zu.name())) {
                taxRateEnum = TaxRateEnum.ZZS_ZL_JYX;
            } else if (Objects.equals(leaseType, LeaseType.zhi_zu.name())) {
                taxRateEnum = TaxRateEnum.XMS_ZL_ZZ;
            } else {
                taxRateEnum = TaxRateEnum.XMS_ZL_HZ;
            }
        }
        List<TaxRateConfig.Data> dataList = JSONUtil.toList(kpiParameterConfig.getConfigValue(), TaxRateConfig.Data.class);
        for (TaxRateConfig.Data data : dataList) {
            if (Objects.equals(data.getBizType(), taxRateEnum.getBizType()) && Objects.equals(data.getTaxType(), taxRateEnum.getTaxType())) {
                return new BigDecimal(data.getTaxRate()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            }
        }
        return null;
    }

    public BigDecimal ensureExpenseRadio(Long assessDept) {
        KpiParameterConfig kpiParameterConfig = this.getOneByConfigCode(KpiParameterConfigCodeEnum.EXPENSE_RADIO);
        if (Objects.isNull(kpiParameterConfig)) {
            throw new MithrasException("未配置费用计提比例");
        }
        if (StrUtil.isBlank(kpiParameterConfig.getConfigValue())) {
            throw new MithrasException("未配置费用计提比例");
        }
        List<ExpenseRadioConfig.Data> dataList = JSONUtil.toList(kpiParameterConfig.getConfigValue(), ExpenseRadioConfig.Data.class);
        if (CollectionUtil.isNotEmpty(dataList)) {
            Map<String, String> dataMap = dataList.stream()
                    .collect(Collectors.toMap(ExpenseRadioConfig.Data::getAssessDept, ExpenseRadioConfig.Data::getExpenseRadio,(k1, k2)->k1));
            String expenseRadio = dataMap.get(String.valueOf(assessDept));
            if (StrUtil.isBlank(expenseRadio)) {
                expenseRadio = "20";
            }
            return new BigDecimal(expenseRadio).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        }
        return null;
    }

    public String ensureProvision(String projectType, String assetClassify, long remainingMonths) {
        KpiParameterConfig kpiParameterConfig = this.getOneByConfigCode(KpiParameterConfigCodeEnum.PROVISION_RADIO);
        if (Objects.isNull(kpiParameterConfig)) {
            throw new MithrasException("未配置拨备计提比例");
        }
        if (StrUtil.isBlank(kpiParameterConfig.getConfigValue())) {
            throw new MithrasException("未配置拨备计提比例");
        }
        List<ProvisionRadioConfig.Data> dataList = JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProvisionRadioConfig.Data.class);
        if (CollectionUtil.isEmpty(dataList)) {
            return null;
        }
        dataList.removeIf(e -> !Objects.equals(projectType, e.getProjectType()));
        dataList.removeIf(e -> !Objects.equals(assetClassify, e.getAssetClassify()));
        if (CollectionUtil.isEmpty(dataList)) {
            return null;
        }
        ProvisionRadioConfig.Data data = dataList.get(0);
        if (Objects.equals(data.getConfigValueType(), KpiConfigValueTypeEnum.VALUE.name())) {
            // 如果是数值则直接返回即可
            return data.getProvisionRadio();
        } else {
            String formulaValue = data.getProvisionRadio();
            String result;
            // 如果是公式，则分两种，区间和公式
            if (this.isRange(formulaValue)) {
                result = this.parseRangeValue(formulaValue);
            } else {
                // 利用QLExpress执行公式得到结果
                formulaValue = formulaValue.replace("if", "excel_if").replace("and", "excel_and").replace("or", "excel_or");
                DefaultContext<String, Object> context = new DefaultContext<>();
                if (formulaValue.contains("T")) {
                    context.put("T", BigDecimal.valueOf(remainingMonths).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP).doubleValue());
                }
                try {
                    Object r = expressRunner.execute(formulaValue, context, null, true, false);
                    result = r.toString();
                    if (this.isRange(result)) {
                        result = parseRangeValue(result);
                    }
                } catch (Exception e) {
                    log.error("执行公式计算发生异常[express:{}, remainingMonths:{}]", formulaValue, remainingMonths, e);
                    throw new MithrasException("确定拨备计提比例发生异常");
                }
            }
            return result;
        }
    }

    private boolean isRange(String value) {
        boolean startWith = value.startsWith("(") || value.startsWith("[");
        boolean endWith = value.endsWith(")") || value.endsWith("]");
        return startWith && endWith;
    }

    private String parseRangeValue(String value) {
        String[] array = value.substring(1, value.length() - 1).split(",");
        return array[0];
    }
}
