package cn.zswltech.mithras.api;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.LeafSelectRSP;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.TreeSelectRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author luyi
 */
@Api(tags = "下拉列表数据接口")
public interface AllSelectApi {

    @ApiOperation(value = "获取所有下拉列表,"
            , notes = "clientType:客户类型；" +
            "continuousStatus:存续状态；" +
            "economyType:经济类型；" +
            "currencyType: 币种；" +
            "genderType: 性别；" +
            "certType：证件类型；" +
            "orgType: 组织机构类型；" +
            "addressType: 法人地址类型；" +
            "moduleType：变更历史模块；" +
            "operationType：变更历史操作类型；" +
            "shareholderType: 股东类型；" +
            "materialsType：法人资料清单类型；" +
            "marriageType: 婚姻类型；" +
            "normalMaterialsType: 自然人资产清单类型；" +
            "orgScaleType: 企业规模；" +
            "subjectReportType：财务报表类型；" +
            "subjectQuarterType：财务报表报告期；" +
            "governmentSubjectItemType：财务报表事业单位指标类型，sheet名；" +
            "subjectItemType：财务报表企业指标类型，sheet名；" +
            "relationshipType: 关联企业关联关系类型；" +
            "clientStatus: 客户状态；" +
            "clientProcessStatus：客户流程状态" +
            "projEstablishBizType：立项业务类型；" +
            "projEstablishApprovalType：立项审批类型；" +
            "leaseType: 租赁类型；" +
            "factoringType: 保理类型；" +
            "projSourceType: 项目来源；" +
            "projEstablishStatus: 立项状态" +
            "processStatus: 流程状态" +
            "taskStatus: 任务状态" +
            "processModelType: 流程类型" +
            "contractStatus: 合同状态" +
            "projEstablishMaterialsEnum: 立项资料清单类型" +
            "jobEnum: 岗位枚举" +
            "blProjectType: 保理业务使用的项目类型" +
            "zrType: 债权转让业务转让类型" +
            "cashFlowItemEnum: 现金流项目类型"+
            "contractTypeEnum: 文本类型"+
            "resolutionTypeEnum: 决议类型"+
            "")

    @GetMapping("select/all")
    R<Map<String, List<SelectRSP>>> allSelect();

    @ApiOperation("获取国家列表")
    @GetMapping("/select/country")
    R<List<SelectRSP>> countryList();

    @ApiOperation("获取省/市/区列表")
    @GetMapping("/select/region/child")
    R<List<LeafSelectRSP>> childRegionList(@RequestParam("code") @ApiParam("区域code") String code);

    @ApiOperation("获取一级行业列表")
    @GetMapping("select/industry/root")
    R<List<LeafSelectRSP>> rootIndustry();

    @ApiOperation("获取行业类型全数据（树形）")
    @GetMapping("/select/industry/all")
    R<List<TreeSelectRSP>> allIndustry();

    @ApiOperation(("获取子行业列表"))
    @GetMapping("select/industry/child")
    R<List<LeafSelectRSP>> childIndustry(@RequestParam("code") @ApiParam("行业code") String code);

    @ApiOperation("获取创建人列表")
    @GetMapping("/select/founder")
    R<List<SelectRSP>> founderList(@ApiParam("创建人") String name, @ApiParam("岗位") String job
            , @RequestParam(value = "sameDept", defaultValue = "true") @ApiParam("是否同部门") Boolean sameDept
            , @RequestParam(value = "pageSize", defaultValue = "30") @ApiParam("页大小") int pageSize);

    @ApiOperation("获取创建部门列表")
    @GetMapping("/select/orgs")
    R<List<SelectRSP>> orgList(@ApiParam("部门名称") String name, @ApiParam("部门类型：0 公司， 1 业务部门，2 领导层") Integer type);

    @ApiOperation("获取新的创建部门列表")
    @GetMapping("/select/new/orgs")
    R<List<SelectRSP>> newOrgList(@ApiParam("部门名称") String name, @ApiParam("部门类型：0 公司， 1 业务部门，2 领导层") Integer type);

    @ApiOperation("获取用户部门")
    @GetMapping("/select/orgs/byUserId")
    R<List<SelectRSP>> orgListByUserId(@ApiParam("用户id") Long userId);

    @ApiOperation("获取部门负责人")
    @GetMapping("/select/businessheader/byDeptId")
    R<List<SelectRSP>> businessheaderListByDeptId(@ApiParam("部门id") Long deptId);

    @ApiOperation("获取二级部门列表")
    @GetMapping("/select/level/orgs")
    R<List<SelectRSP>> levelOrgList(@ApiParam("部门名称") String name, @ApiParam("部门类型：0 公司， 1 业务部门，2 领导层") Integer type,@ApiParam("部门层级：1 一级， 2 二级，3 三级") Integer level);

}
