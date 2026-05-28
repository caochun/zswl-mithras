package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Api(tags = "租赁物-评估机构接口")
@RequestMapping("/ledger/appraisal")
public interface LeaseAppraisalApi{

    @ApiOperation("租赁物内评估机构列表")
    @PostMapping("/leaseItem/list")
    R<List<LeaseAppraisalItemListRSP>> appraisalLeaseList(@RequestBody LeaseAppraisalItemListREQ req);

    @ApiOperation("获取系统中所有评估机构")
    @PostMapping("/company/list")
    R<PageR<LeaseAppraisalCompanyListRSP>> appraisalCompanyList(@RequestBody LeaseAppraisalCompanyListREQ req);

    @ApiOperation("新增评估机构")
    @PostMapping("/add")
    R<Long> appraisalAdd(@RequestBody LeaseAppraisalAddREQ req);

    @ApiOperation("模糊搜索评估机构")
    @PostMapping("/queryCompany")
    R<List<LeaseAppraisalQueryCompanyRSP>> appraisalQueryCompany(@RequestBody LeaseAppraisalQueryCompanyREQ req);

    @ApiOperation("评估机构信息详情")
    @PostMapping("/detail")
    R<LeaseAppraisalDetailRSP> detail(@RequestBody LeaseAppraisalDetailREQ req);

    @ApiOperation("更新评估机构最新信息")
    @PostMapping("/lasted")
    R<Void> appraisalLasted(@RequestBody LeaseAppraisalLastedREQ req);

    @ApiOperation("关联评估机构信息")
    @PostMapping("/relation")
    R<Void> relation(@RequestBody LeaseAppraisalRelationREQ req);

}
