package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiParameterConfigApi;
import cn.zswltech.mithras.dto.kpi.ContractAssessDeptConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigCommonReq;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.BusinessDeptAssessConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.CareerLevelConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ContractAssessDeptConfigListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.DeptProfitFinishRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ExpenseRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.FinancialMarketDeptAssessConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.FinancialMarketDeptRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.MiddleBackDeptAssessConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProfitAdjustConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectPaymentBonusRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectScaleRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectTypeRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProvisionRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.TaxRateConfig;
import cn.zswltech.mithras.kpi.application.KpiParameterConfigApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class KpiParameterConfigController implements KpiParameterConfigApi {

    @Resource
    private KpiParameterConfigApplicationService kpiParameterConfigApplicationService;

    @Override
    public R<PageR<KpiParameterConfigListRSP>> pageList(KpiParameterConfigListREQ req) {
        return kpiParameterConfigApplicationService.pageList(req);
    }

    @Override
    public R<TaxRateConfig> getTaxRateConfig() {
        return kpiParameterConfigApplicationService.getTaxRateConfig();
    }

    @Override
    public R<Void> saveTaxRate(TaxRateConfig taxRateConfig) {
        return kpiParameterConfigApplicationService.saveTaxRate(taxRateConfig);
    }

    @Override
    public R<ProvisionRadioConfig> getProvisionRadioConfig() {
        return kpiParameterConfigApplicationService.getProvisionRadioConfig();
    }

    @Override
    public R<Void> saveProvisionRadioConfig(ProvisionRadioConfig provisionRadioConfig) {
        return kpiParameterConfigApplicationService.saveProvisionRadioConfig(provisionRadioConfig);
    }

    @Override
    public R<ExpenseRadioConfig> getExpenseRadioConfig() {
        return kpiParameterConfigApplicationService.getExpenseRadioConfig();
    }

    @Override
    public R<Void> saveExpenseRadioConfig(ExpenseRadioConfig expenseRadioConfig) {
        return kpiParameterConfigApplicationService.saveExpenseRadioConfig(expenseRadioConfig);
    }

    @Override
    public R<ProjectRadioConfig> getProjectRadioConfig(KpiParameterConfigCommonReq req) {
        return kpiParameterConfigApplicationService.getProjectRadioConfig(req);
    }

    @Override
    public R<Void> saveProjectRadioConfig(ProjectRadioConfig projectRadioConfig) {
        return kpiParameterConfigApplicationService.saveProjectRadioConfig(projectRadioConfig);
    }

    @Override
    public R<ProjectPaymentBonusRadioConfig> getProjectPaymentBonusRadioConfig(KpiParameterConfigCommonReq req) {
        return kpiParameterConfigApplicationService.getProjectPaymentBonusRadioConfig(req);
    }

    @Override
    public R<Void> saveProjectPaymentBonusRadioConfig(ProjectPaymentBonusRadioConfig projectRadioConfig) {
        return kpiParameterConfigApplicationService.saveProjectPaymentBonusRadioConfig(projectRadioConfig);
    }

    @Override
    public R<ProjectScaleRadioConfig> getScaleRadioConfig(KpiParameterConfigCommonReq req) {
        return kpiParameterConfigApplicationService.getScaleRadioConfig(req);
    }

    @Override
    public R<Void> saveScaleRadioConfig(ProjectScaleRadioConfig req) {
        return kpiParameterConfigApplicationService.saveScaleRadioConfig(req);
    }

    @Override
    public R<ProjectTypeRadioConfig> getProjectTypeRadioConfig(KpiParameterConfigCommonReq req) {
        return kpiParameterConfigApplicationService.getProjectTypeRadioConfig(req);
    }

    @Override
    public R<Void> saveProjectTypeRadioConfig(ProjectTypeRadioConfig projectRadioConfig) {
        return kpiParameterConfigApplicationService.saveProjectTypeRadioConfig(projectRadioConfig);
    }

    @Override
    public R<DeptProfitFinishRadioConfig> getDeptProfitFinishRadioConfig() {
        return kpiParameterConfigApplicationService.getDeptProfitFinishRadioConfig();
    }

    @Override
    public R<Void> saveDeptProfitFinishRadioConfig(DeptProfitFinishRadioConfig deptProfitFinishRadioConfig) {
        return kpiParameterConfigApplicationService.saveDeptProfitFinishRadioConfig(deptProfitFinishRadioConfig);
    }

    @Override
    public R<CareerLevelConfig> getCareerLevelConfig() {
        return kpiParameterConfigApplicationService.getCareerLevelConfig();
    }

    @Override
    public R<Void> saveCareerLevelConfig(CareerLevelConfig careerLevelConfig) {
        return kpiParameterConfigApplicationService.saveCareerLevelConfig(careerLevelConfig);
    }

    @Override
    public R<BusinessDeptAssessConfig> getBusinessDeptAssessConfig() {
        return kpiParameterConfigApplicationService.getBusinessDeptAssessConfig();
    }

    @Override
    public R<Void> saveBusinessDeptAssessConfig(BusinessDeptAssessConfig businessDeptAssessConfig) {
        return kpiParameterConfigApplicationService.saveBusinessDeptAssessConfig(businessDeptAssessConfig);
    }

    @Override
    public R<FinancialMarketDeptRadioConfig> getFinancialMarketDeptRadioConfig() {
        return kpiParameterConfigApplicationService.getFinancialMarketDeptRadioConfig();
    }

    @Override
    public R<Void> saveFinancialMarketDeptRadioConfig(FinancialMarketDeptRadioConfig financialMarketDeptRadioConfig) {
        return kpiParameterConfigApplicationService.saveFinancialMarketDeptRadioConfig(financialMarketDeptRadioConfig);
    }

    @Override
    public R<ProfitAdjustConfig> getProfitAdjustConfig() {
        return kpiParameterConfigApplicationService.getProfitAdjustConfig();
    }

    @Override
    public R<Void> saveProfitAdjustConfig(ProfitAdjustConfig profitAdjustConfig) {
        return kpiParameterConfigApplicationService.saveProfitAdjustConfig(profitAdjustConfig);
    }

    @Override
    public R<FinancialMarketDeptAssessConfig> getFinancialMarketDeptAssessConfig() {
        return kpiParameterConfigApplicationService.getFinancialMarketDeptAssessConfig();
    }

    @Override
    public R<Void> saveFinancialMarketDeptAssessConfig(FinancialMarketDeptAssessConfig financialMarketDeptAssessConfig) {
        return kpiParameterConfigApplicationService.saveFinancialMarketDeptAssessConfig(financialMarketDeptAssessConfig);
    }

    @Override
    public R<MiddleBackDeptAssessConfig> getMiddleBackDeptAssessConfig() {
        return kpiParameterConfigApplicationService.getMiddleBackDeptAssessConfig();
    }

    @Override
    public R<Void> saveMiddleBackDeptAssessConfig(MiddleBackDeptAssessConfig middleBackDeptAssessConfig) {
        return kpiParameterConfigApplicationService.saveMiddleBackDeptAssessConfig(middleBackDeptAssessConfig);
    }

    @Override
    public R<List<ContractAssessDeptConfigListRSP>> getContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ) {
        return kpiParameterConfigApplicationService.getContractAssessDeptConfig(configListREQ);
    }

    @Override
    public R<Void> saveContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ) {
        return kpiParameterConfigApplicationService.saveContractAssessDeptConfig(configListREQ);
    }

    @Override
    public R<Void> deleteContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ) {
        return kpiParameterConfigApplicationService.deleteContractAssessDeptConfig(configListREQ);
    }
}
