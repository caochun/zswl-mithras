package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description ecl_预测业务配置表
* @author vico
* @date 2025-10-14
*/
@Api(tags = "ecl_预测业务配置表-接口")
public interface EclPredictBusinessConfigApi {


    @ApiOperation("修改ecl_预测业务配置表")
    @PostMapping("/ecl/predict/business/config/modify")
    R<Void> modify(@RequestBody @Valid EclPredictBusinessConfigModifyREQ req);

    @ApiOperation("ecl_业务配置表列表")
    @PostMapping("/ecl/predict/business/config/list")
    R<PageR<EclPredictBusinessConfigListRSP>> list(@RequestBody @Valid EclPredictBusinessConfigListREQ req);

    @ApiOperation("修改ecl_预测业务配置表详情")
    @PostMapping("/ecl/predict/business/config/detail")
    R<EclPredictBusinessConfigDetailRSP> detail(@RequestBody @Valid EclPredictBusinessConfigDetailREQ req);


}