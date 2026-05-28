package cn.zswltech.mithras.api.kpi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description ecl_业务配置表
* @author vico
* @date 2025-09-24
*/
@Api(tags = "ecl_业务配置表-接口")
public interface EclBusinessConfigApi {

    @ApiOperation("修改ecl_业务配置表")
    @PostMapping("/ecl/business/config/modify")
    R<Void> modify(@RequestBody @Valid EclBusinessConfigModifyREQ req);

    @ApiOperation("ecl_业务配置表列表")
    @PostMapping("/ecl/business/config/list")
    R<PageR<EclBusinessConfigListRSP>> list(@RequestBody @Valid EclBusinessConfigListREQ req);

    @ApiOperation("ecl_业务配置表详情")
    @PostMapping("/ecl/business/config/detail")
    R<EclBusinessConfigDetailRSP> detail(@RequestBody @Valid EclBusinessConfigDetailREQ req);

    @ApiOperation("ecl_业务配置表版本列表")
    @PostMapping("/ecl/business/config/version/list")
    R<PageR<EclBusinessConfigListRSP>> versionList(@RequestBody @Valid EclBusinessConfigListREQ req);

    @ApiOperation("ecl_业务配置表版本详情")
    @PostMapping("/ecl/business/config/version/detail")
    R<EclBusinessConfigDetailRSP> versionDetail(@RequestBody @Valid EclBusinessConfigDetailREQ req);


}