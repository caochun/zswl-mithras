package cn.zswltech.mithras.api.afterlease;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckChangeRecordListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckChangeRecordListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 租后检查计划-基本信息表
* @author vico
* @date 2024-04-22
*/
@Api(tags = "租后检查计划-基本信息表-接口")
public interface AfterLeaseCheckChangeRecordApi {

    @ApiOperation("租后检查计划-基本信息表列表")
    @PostMapping("/after/lease/check/change/record/list")
    R<PageR<AfterLeaseCheckChangeRecordListRSP>> list(@RequestBody @Valid AfterLeaseCheckChangeRecordListREQ req);

}