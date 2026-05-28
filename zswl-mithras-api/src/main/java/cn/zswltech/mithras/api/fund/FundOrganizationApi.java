package cn.zswltech.mithras.api.fund;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.fund.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 资金管理-机构表
 * @date 2022-12-13
 */
@Api(tags = "资金管理-机构表-接口")
public interface FundOrganizationApi {

    @ApiOperation("新增资金管理-机构表")
    @PostMapping("/fund/organization/add")
    R<Void> add(@RequestBody @Valid FundOrganizationAddREQ req);

    @ApiOperation("修改资金管理-机构表")
    @PostMapping("/fund/organization/modify")
    R<Void> modify(@RequestBody @Valid FundOrganizationModifyREQ req);

    @ApiOperation("资金管理-机构表列表")
    @PostMapping("/fund/organization/list")
    R<PageR<FundOrganizationListRSP>> list(@RequestBody @Valid FundOrganizationListREQ req);

    @ApiOperation("资金管理-机构表详情")
    @PostMapping("/fund/organization/detail")
    R<FundOrganizationDetailRSP> detail(@RequestBody @Valid FundOrganizationDetailREQ req);

    @ApiOperation("删除资金管理-机构表")
    @PostMapping("/fund/organization/remove")
    R<Void> remove(@RequestBody @Valid FundOrganizationRemoveREQ req);

    @ApiOperation("获取机构代码")
    @PostMapping("/fund/institutionCode")
    R<FundOrganizationCommonRSP> getInstitutionCode(@RequestBody @Valid FundOrganizationCommonREQ req);

    @ApiOperation("获取去重后的机构简称列表")
    @PostMapping("/fund/organization/abbreviation")
    R<List<String>> listDistinctAbbreviation(@RequestBody FundOrganizationCommonREQ req);

}