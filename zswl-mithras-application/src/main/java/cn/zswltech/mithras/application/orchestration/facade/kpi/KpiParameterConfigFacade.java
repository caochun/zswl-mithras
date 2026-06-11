package cn.zswltech.mithras.application.orchestration.facade.kpi;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.ContractAssessDeptConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigCommonReq;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.*;
import cn.zswltech.mithras.kpi.application.KpiParameterConfigApplicationService;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.kpi.application.auth.KpiParameterConfigModifyChecker;
import cn.zswltech.mithras.kpi.convert.KpiParameterConfigConvert;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.kpi.enums.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.finance.service.ContractAssessDeptDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@Slf4j
@Service
public class KpiParameterConfigFacade implements KpiParameterConfigApplicationService {
    @Resource
    private KpiParameterConfigService parameterConfigService;
    @Resource
    private ContractAssessDeptDetailService contractAssessDeptDetailService;

    @Override
    public R<PageR<KpiParameterConfigListRSP>> pageList(@Valid KpiParameterConfigListREQ req) {
        return R.ok(parameterConfigService.pageList(req));
    }

    @Override
    public R<TaxRateConfig> getTaxRateConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.TAX_RATE);
        try {
            TaxRateConfig taxRateConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, TaxRateConfig.class);
            taxRateConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), TaxRateConfig.Data.class));
            return R.ok(taxRateConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.TAX_RATE.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveTaxRate(@Valid TaxRateConfig taxRateConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.TAX_RATE, JSONUtil.toJsonStr(taxRateConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<ProvisionRadioConfig> getProvisionRadioConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.PROVISION_RADIO);
        try {
            ProvisionRadioConfig provisionRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ProvisionRadioConfig.class);
            provisionRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProvisionRadioConfig.Data.class));
            return R.ok(provisionRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.PROVISION_RADIO.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveProvisionRadioConfig(@Valid ProvisionRadioConfig provisionRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.PROVISION_RADIO, JSONUtil.toJsonStr(provisionRadioConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<ExpenseRadioConfig> getExpenseRadioConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.EXPENSE_RADIO);
        try {
            ExpenseRadioConfig expenseRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ExpenseRadioConfig.class);
            expenseRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ExpenseRadioConfig.Data.class));
            return R.ok(expenseRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.EXPENSE_RADIO.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveExpenseRadioConfig(@Valid ExpenseRadioConfig expenseRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.EXPENSE_RADIO, JSONUtil.toJsonStr(expenseRadioConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<ProjectRadioConfig> getProjectRadioConfig(KpiParameterConfigCommonReq req) {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.PROJECT_RADIO, req.getBaseId());
        try {
            ProjectRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ProjectRadioConfig.class);
            projectRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProjectRadioConfig.Data.class));
            return R.ok(projectRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.PROJECT_RADIO.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveProjectRadioConfig(@Valid ProjectRadioConfig projectRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.PROJECT_RADIO, JSONUtil.toJsonStr(projectRadioConfig.getConfigValue()), projectRadioConfig.getId());
        return R.ok();
    }

    @Override
    public R<ProjectPaymentBonusRadioConfig> getProjectPaymentBonusRadioConfig(@Valid KpiParameterConfigCommonReq req) {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.PROJECT_RADIO_PAYMENT, req.getBaseId());
        try {
            ProjectPaymentBonusRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ProjectPaymentBonusRadioConfig.class);
            projectRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProjectPaymentBonusRadioConfig.PaymentBonusRatio.class));
            return R.ok(projectRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.PROJECT_RADIO_PAYMENT.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @Override
    public R<Void> saveProjectPaymentBonusRadioConfig(@Valid ProjectPaymentBonusRadioConfig projectRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.PROJECT_RADIO_PAYMENT, JSONUtil.toJsonStr(projectRadioConfig.getConfigValue()), projectRadioConfig.getId());
        return R.ok();
    }

    @Override
    public R<ProjectScaleRadioConfig> getScaleRadioConfig(@Valid KpiParameterConfigCommonReq req) {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.PROJECT_RADIO_SCALE, req.getBaseId());
        try {
            ProjectScaleRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ProjectScaleRadioConfig.class);
            projectRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProjectScaleRadioConfig.ProjScaleRatio.class));
            return R.ok(projectRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.PROJECT_RADIO_SCALE.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @Override
    public R<Void> saveScaleRadioConfig(@Valid ProjectScaleRadioConfig req) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.PROJECT_RADIO_SCALE, JSONUtil.toJsonStr(req.getConfigValue()), req.getId());
        return R.ok();
    }

    @Override
    public R<ProjectTypeRadioConfig> getProjectTypeRadioConfig(@Valid KpiParameterConfigCommonReq req) {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.PROJECT_RADIO_TYPE, req.getBaseId());
        try {
            ProjectTypeRadioConfig projectRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ProjectTypeRadioConfig.class);
            projectRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProjectTypeRadioConfig.ProjTypeRatio.class));
            return R.ok(projectRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.PROJECT_RADIO_TYPE.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @Override
    public R<Void> saveProjectTypeRadioConfig(@Valid ProjectTypeRadioConfig projectRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.PROJECT_RADIO_TYPE, JSONUtil.toJsonStr(projectRadioConfig.getConfigValue()), projectRadioConfig.getId());
        return R.ok();
    }

    @Override
    public R<DeptProfitFinishRadioConfig> getDeptProfitFinishRadioConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.DEPT_PROFIT_FINISH_RADIO);
        try {
            DeptProfitFinishRadioConfig deptProfitFinishRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, DeptProfitFinishRadioConfig.class);
            deptProfitFinishRadioConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), DeptProfitFinishRadioConfig.Data.class));
            return R.ok(deptProfitFinishRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.DEPT_PROFIT_FINISH_RADIO.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveDeptProfitFinishRadioConfig(@Valid DeptProfitFinishRadioConfig deptProfitFinishRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.DEPT_PROFIT_FINISH_RADIO, JSONUtil.toJsonStr(deptProfitFinishRadioConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<CareerLevelConfig> getCareerLevelConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.CAREER_LEVEL);
        try {
            CareerLevelConfig careerLevelConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, CareerLevelConfig.class);
            careerLevelConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), CareerLevelConfig.Data.class));
            return R.ok(careerLevelConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.CAREER_LEVEL.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveCareerLevelConfig(@Valid CareerLevelConfig careerLevelConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.CAREER_LEVEL, JSONUtil.toJsonStr(careerLevelConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<BusinessDeptAssessConfig> getBusinessDeptAssessConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.BUSINESS_DEPT_ASSESS);
        try {
            BusinessDeptAssessConfig businessDeptAssessConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, BusinessDeptAssessConfig.class);
            businessDeptAssessConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), BusinessDeptAssessConfig.Data.class));
            return R.ok(businessDeptAssessConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.BUSINESS_DEPT_ASSESS.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveBusinessDeptAssessConfig(@Valid BusinessDeptAssessConfig businessDeptAssessConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.BUSINESS_DEPT_ASSESS, JSONUtil.toJsonStr(businessDeptAssessConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<FinancialMarketDeptRadioConfig> getFinancialMarketDeptRadioConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.FINANCIAL_MARKET_DEPT_RADIO);
        try {
            FinancialMarketDeptRadioConfig financialMarketDeptRadioConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, FinancialMarketDeptRadioConfig.class);
            financialMarketDeptRadioConfig.setConfigValue(JSONUtil.toBean(kpiParameterConfig.getConfigValue(), FinancialMarketDeptRadioConfig.Data.class));
            return R.ok(financialMarketDeptRadioConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.FINANCIAL_MARKET_DEPT_RADIO.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveFinancialMarketDeptRadioConfig(@Valid FinancialMarketDeptRadioConfig financialMarketDeptRadioConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.FINANCIAL_MARKET_DEPT_RADIO, JSONUtil.toJsonStr(financialMarketDeptRadioConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<ProfitAdjustConfig> getProfitAdjustConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.PROFIT_ADJUST);
        try {
            ProfitAdjustConfig profitAdjustConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, ProfitAdjustConfig.class);
            profitAdjustConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), ProfitAdjustConfig.Data.class));
            return R.ok(profitAdjustConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.PROFIT_ADJUST.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveProfitAdjustConfig(@Valid ProfitAdjustConfig profitAdjustConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.PROFIT_ADJUST, JSONUtil.toJsonStr(profitAdjustConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<FinancialMarketDeptAssessConfig> getFinancialMarketDeptAssessConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.FINANCIAL_MARKET_DEPT_ASSESS);
        try {
            FinancialMarketDeptAssessConfig financialMarketDeptAssessConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, FinancialMarketDeptAssessConfig.class);
            financialMarketDeptAssessConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), FinancialMarketDeptAssessConfig.Data.class));
            return R.ok(financialMarketDeptAssessConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.FINANCIAL_MARKET_DEPT_ASSESS.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveFinancialMarketDeptAssessConfig(@Valid FinancialMarketDeptAssessConfig financialMarketDeptAssessConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.FINANCIAL_MARKET_DEPT_ASSESS, JSONUtil.toJsonStr(financialMarketDeptAssessConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<MiddleBackDeptAssessConfig> getMiddleBackDeptAssessConfig() {
        KpiParameterConfig kpiParameterConfig = parameterConfigService.getOneWithCheck(KpiParameterConfigCodeEnum.MIDDLE_BACK_DEPT_ASSESS);
        try {
            MiddleBackDeptAssessConfig middleBackDeptAssessConfig = KpiParameterConfigConvert.toConfigBase(kpiParameterConfig, MiddleBackDeptAssessConfig.class);
            middleBackDeptAssessConfig.setConfigValue(JSONUtil.toList(kpiParameterConfig.getConfigValue(), MiddleBackDeptAssessConfig.Data.class));
            return R.ok(middleBackDeptAssessConfig);
        } catch (Exception e) {
            log.error(String.format("[获取参数设置-%s]类型转换发生异常", KpiParameterConfigCodeEnum.MIDDLE_BACK_DEPT_ASSESS.getDesc()), e);
            throw new MithrasException("数据处理发生未知异常");
        }
    }

    @DataAuthCheck(paramType = DataAuthCheck.ParamType.NO, checkerClass = KpiParameterConfigModifyChecker.class, businessModule = "KPI_PARAMETER_CONFIG")
    @Override
    public R<Void> saveMiddleBackDeptAssessConfig(@Valid MiddleBackDeptAssessConfig middleBackDeptAssessConfig) {
        parameterConfigService.modifyConfigValue(KpiParameterConfigCodeEnum.MIDDLE_BACK_DEPT_ASSESS, JSONUtil.toJsonStr(middleBackDeptAssessConfig.getConfigValue()));
        return R.ok();
    }

    @Override
    public R<List<ContractAssessDeptConfigListRSP>> getContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ) {
        List<ContractAssessDeptConfigListRSP> assessDeptConfig = contractAssessDeptDetailService.getContractAssessDeptConfig(configListREQ);
        return R.ok(assessDeptConfig);
    }

    @Override
    public R<Void> saveContractAssessDeptConfig(@Valid ContractAssessDeptConfigListREQ configListREQ) {
        contractAssessDeptDetailService.saveContractAssessDeptConfig(configListREQ);
        return R.ok();
    }

    @Override
    public R<Void> deleteContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ) {
        contractAssessDeptDetailService.deleteContractAssessDeptConfig(configListREQ);
        return R.ok();
    }
}
