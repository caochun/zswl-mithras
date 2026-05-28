package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.ContractAssessDeptConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigCommonReq;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterConfigListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@Api(tags = "绩效考核-参数设置相关接口")
@RequestMapping(path = "/kpi/parameterconfig")
public interface KpiParameterConfigApi {
    @ApiOperation("列表")
    @PostMapping(path = "/pagelist")
    R<PageR<KpiParameterConfigListRSP>> pageList(@RequestBody @Valid KpiParameterConfigListREQ req);

    @ApiOperation("税率维护-详情")
    @PostMapping(path = "/taxrate/get")
    R<TaxRateConfig> getTaxRateConfig();

    @ApiOperation("税率维护-保存")
    @PostMapping(path = "/taxrate/save")
    R<Void> saveTaxRate(@RequestBody @Valid TaxRateConfig taxRateConfig);

    @ApiOperation("拨备计提比例-详情")
    @PostMapping(path = "/provisionradio/get")
    R<ProvisionRadioConfig> getProvisionRadioConfig();

    @ApiOperation("拨备计提比例-保存")
    @PostMapping(path = "/provisionradio/save")
    R<Void> saveProvisionRadioConfig(@RequestBody @Valid ProvisionRadioConfig provisionRadioConfig);

    @ApiOperation("费用计提比例-详情")
    @PostMapping(path = "/expenseradio/get")
    R<ExpenseRadioConfig> getExpenseRadioConfig();

    @ApiOperation("费用计提比例-保存")
    @PostMapping(path = "/expenseradio/save")
    R<Void> saveExpenseRadioConfig(@RequestBody @Valid ExpenseRadioConfig expenseRadioConfig);


    @ApiOperation("项目提奖比例-基础提奖比例详情")
    @PostMapping(path = "/projectradio/get")
    R<ProjectRadioConfig> getProjectRadioConfig(@RequestBody @Valid KpiParameterConfigCommonReq req);

    @ApiOperation("项目提奖比例-基础提奖比例保存")
    @PostMapping(path = "/projectradio/save")
    R<Void> saveProjectRadioConfig(@RequestBody @Valid ProjectRadioConfig projectRadioConfig);

    @ApiOperation("项目提奖比例-投放比例详情")
    @PostMapping(path = "/projectradio/payment/get")
    R<ProjectPaymentBonusRadioConfig> getProjectPaymentBonusRadioConfig(@RequestBody @Valid KpiParameterConfigCommonReq req);

    @ApiOperation("项目提奖比例-投放比例保存")
    @PostMapping(path = "/projectradio/payment/save")
    R<Void> saveProjectPaymentBonusRadioConfig(@RequestBody @Valid ProjectPaymentBonusRadioConfig projectRadioConfig);

    @ApiOperation("项目提奖比例-项目规模系数详情")
    @PostMapping(path = "/projectradio/scale/get")
    R<ProjectScaleRadioConfig> getScaleRadioConfig(@RequestBody @Valid KpiParameterConfigCommonReq req);

    @ApiOperation("项目提奖比例-项目规模系数保存")
    @PostMapping(path = "/projectradio/scale/save")
    R<Void> saveScaleRadioConfig(@RequestBody @Valid ProjectScaleRadioConfig req);

    @ApiOperation("项目提奖比例-项目类型系数详情")
    @PostMapping(path = "/projectradio/type/get")
    R<ProjectTypeRadioConfig> getProjectTypeRadioConfig(@RequestBody @Valid KpiParameterConfigCommonReq req);

    @ApiOperation("项目提奖比例-项目类型系数保存")
    @PostMapping(path = "/projectradio/type/save")
    R<Void> saveProjectTypeRadioConfig(@RequestBody @Valid ProjectTypeRadioConfig projectRadioConfig);

    @ApiOperation("部门利润完成率系数-详情")
    @PostMapping(path = "/deptprofitfinishradio/get")
    R<DeptProfitFinishRadioConfig> getDeptProfitFinishRadioConfig();

    @ApiOperation("部门利润完成率系数-保存")
    @PostMapping(path = "/deptprofitfinishradio/save")
    R<Void> saveDeptProfitFinishRadioConfig(@RequestBody @Valid DeptProfitFinishRadioConfig deptProfitFinishRadioConfig);

    @ApiOperation("职等系数-详情")
    @PostMapping(path = "/careerlevel/get")
    R<CareerLevelConfig> getCareerLevelConfig();

    @ApiOperation("职等系数-保存")
    @PostMapping(path = "/careerlevel/save")
    R<Void> saveCareerLevelConfig(@RequestBody @Valid CareerLevelConfig careerLevelConfig);

    @ApiOperation("业务部门综合考评系数-详情")
    @PostMapping(path = "/businessdeptassess/get")
    R<BusinessDeptAssessConfig> getBusinessDeptAssessConfig();

    @ApiOperation("业务部门综合考评系数-保存")
    @PostMapping(path = "/businessdeptassess/save")
    R<Void> saveBusinessDeptAssessConfig(@RequestBody @Valid BusinessDeptAssessConfig businessDeptAssessConfig);

    @ApiOperation("金融市场部提奖比例-详情")
    @PostMapping(path = "/financialmarketdeptradio/get")
    R<FinancialMarketDeptRadioConfig> getFinancialMarketDeptRadioConfig();

    @ApiOperation("金融市场部提奖比例-保存")
    @PostMapping(path = "/financialmarketdeptradio/save")
    R<Void> saveFinancialMarketDeptRadioConfig(@RequestBody @Valid FinancialMarketDeptRadioConfig financialMarketDeptRadioConfig);

    @ApiOperation("公司利润调整系数-详情")
    @PostMapping(path = "/profitadjust/get")
    R<ProfitAdjustConfig> getProfitAdjustConfig();

    @ApiOperation("公司利润调整系数-保存")
    @PostMapping(path = "/profitadjust/save")
    R<Void> saveProfitAdjustConfig(@RequestBody @Valid ProfitAdjustConfig profitAdjustConfig);

    @ApiOperation("金融市场部综合考评系数-详情")
    @PostMapping(path = "/financialmarketdeptassess/get")
    R<FinancialMarketDeptAssessConfig> getFinancialMarketDeptAssessConfig();

    @ApiOperation("金融市场部综合考评系数-保存")
    @PostMapping(path = "/financialmarketdeptassess/save")
    R<Void> saveFinancialMarketDeptAssessConfig(@RequestBody @Valid FinancialMarketDeptAssessConfig financialMarketDeptAssessConfig);

    @ApiOperation("中后台部门综合考评系数-详情")
    @PostMapping(path = "/middlebackdeptassess/get")
    R<MiddleBackDeptAssessConfig> getMiddleBackDeptAssessConfig();

    @ApiOperation("中后台部门综合考评系数-保存")
    @PostMapping(path = "/middlebackdeptassess/save")
    R<Void> saveMiddleBackDeptAssessConfig(@RequestBody @Valid MiddleBackDeptAssessConfig middleBackDeptAssessConfig);

    @ApiOperation("考核部门设置-详情")
    @PostMapping(path = "/contractAssessDept/get")
    R<List<ContractAssessDeptConfigListRSP>> getContractAssessDeptConfig(@RequestBody @Valid ContractAssessDeptConfigListREQ configListREQ);

    @ApiOperation("考核部门设置-保存")
    @PostMapping(path = "/contractAssessDept/save")
    R<Void> saveContractAssessDeptConfig(@RequestBody @Valid ContractAssessDeptConfigListREQ configListREQ);

    @ApiOperation("考核部门设置-删除")
    @PostMapping(path = "/contractAssessDept/delete")
    R<Void> deleteContractAssessDeptConfig(@RequestBody @Valid ContractAssessDeptConfigListREQ configListREQ);
}
