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
* @description 资产减值预测表
* @author vico
* @date 2025-10-14
*/
@Api(tags = "资产减值预测表-接口")
public interface EclExecutePredictBaseInfoApi {

    @ApiOperation("新增资产减值预测表")
    @PostMapping("/ecl/execute/predict/base/info/add")
    R<Long> add(@RequestBody @Valid EclExecutePredictBaseInfoAddREQ req);

    @ApiOperation("资产减值预测表列表")
    @PostMapping("/ecl/execute/predict/base/info/list")
    R<PageR<EclExecutePredictBaseInfoListRSP>> list(@RequestBody @Valid EclExecutePredictBaseInfoListREQ req);

    @ApiOperation("删除资产减值预测表")
    @PostMapping("/ecl/execute/predict/base/info/remove")
    R<Void> remove(@RequestBody @Valid EclExecutePredictBaseInfoRemoveREQ req);

    @ApiOperation("资产减值预测详情记录测算表")
    @PostMapping("/ecl/execute/predict/calculation")
    R<Void> calculation(@RequestBody @Valid EclExecutePredictBaseInfoDetailREQ req);


}