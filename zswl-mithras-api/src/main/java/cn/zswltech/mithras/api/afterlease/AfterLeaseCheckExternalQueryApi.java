package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
* @description 租后检查外部查询任务
* @author zhaozhengkang
* @date 2022-11-17
*/
@Api(tags = "租后检查-外部查询-接口")
@RequestMapping("/afterlease/check/external/query")
public interface AfterLeaseCheckExternalQueryApi {
    @ApiOperation("查询任务提交审批")
    @PostMapping("/submit")
    R<Void> submit(@RequestBody @Valid AfterLeaseCheckExternalQueryIdReq id);

    @ApiOperation("修改查询任务")
    @PostMapping("/modify")
    R<Void> modify(@RequestBody @Valid AfterLeaseCheckExternalQueryModifyReq req);
    @ApiOperation("修改查询任务结论")
    @PostMapping("/modifyConclusion")
    R<Void> modifyConclusion(@RequestBody @Valid AfterLeaseCheckExternalQueryConclusionModifyReq req);

    @ApiOperation("list页统计信息")
    @PostMapping("/list/statistics")
    R<AfterLeaseCheckExternalQueryListStatisticsRsp> listStatistics(@RequestBody @Valid AfterLeaseCheckExternalQueryListReq req);

    @ApiOperation("外部查询任务列表")
    @PostMapping("/list")
    R<PageR<AfterLeaseCheckExternalQueryListRsp>> list(@RequestBody @Valid AfterLeaseCheckExternalQueryListReq req);

    @ApiOperation("外部查询任务详情")
    @PostMapping("/detail")
    R<AfterLeaseCheckExternalQueryDetailRsp> detail(@Valid @RequestBody AfterLeaseCheckExternalQueryIdReq req);

    @ApiOperation("修改外部查询承租人/担保人信息")
    @PostMapping("/clientinfo/modify")
    R<Void> clientInfoModify(@RequestBody @Valid AfterLeaseCheckExternalQueryClientInfoModifyReq req);

    @ApiOperation("下载报告")
    @PostMapping("/report")
    R<Void> downLoadReport(@RequestBody @Valid AfterLeaseCheckExternalQueryIdReq req) throws Exception;
}