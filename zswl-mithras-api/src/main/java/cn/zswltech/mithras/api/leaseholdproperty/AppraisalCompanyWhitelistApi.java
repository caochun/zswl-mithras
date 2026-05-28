package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2025/9/2
 * @description
 */
@Api(tags = "评估机构白名单")
@RequestMapping(path = "/appraisalcompany/whitelist")
public interface AppraisalCompanyWhitelistApi {
    @ApiOperation("评估机构白名单-分页列表")
    @PostMapping(path = "/pagelist")
    R<PageR<AppraisalCompanyWhitelistPageRSP>> pageList(@RequestBody @Valid AppraisalCompanyWhitelistPageREQ req);

    @ApiOperation("评估机构白名单-新增")
    @PostMapping(path = "/add")
    R<Long> add(@RequestBody @Valid AppraisalCompanyWhitelistAddREQ req);

    @ApiOperation("评估机构白名单-评估机构详情")
    @PostMapping(path = "/detail")
    R<AppraisalCompanyDetailRSP> detail(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("评估机构白名单-更新工商信息")
    @PostMapping(path = "/commerce/refresh")
    R<Void> refreshCommerce(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("评估机构白名单-提交审批")
    @PostMapping(path = "/submit")
    R<String> submit(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("评估机构白名单-提交出库申请")
    @PostMapping(path = "/out/submit")
    R<String> submitOut(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("评估机构白名单-删除")
    @PostMapping(path = "/delete")
    R<Void> deleteById(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("评估机构白名单-取消操作")
    @PostMapping(path = "/cancel")
    R<Void> cancel(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("评估机构白名单-保存出库原因")
    @PostMapping(path = "/out/reason/save")
    R<Void> saveOutReason(@RequestBody @Valid AppraisalCompanyWhitelistModifyREQ req);
}
