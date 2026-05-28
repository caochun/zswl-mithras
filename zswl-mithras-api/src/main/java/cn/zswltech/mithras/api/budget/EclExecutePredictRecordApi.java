package cn.zswltech.mithras.api.budget;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Set;

/**
* @description 资产减值预测详情记录表
* @author vico
* @date 2025-10-14
*/
@Api(tags = "资产减值预测详情记录表-接口")
public interface EclExecutePredictRecordApi {

    @ApiOperation("新增资产减值预测详情记录表")
    @PostMapping("/ecl/execute/predict/record/add")
    R<Void> add(@RequestBody @Valid EclExecutePredictRecordAddREQ req);

    @ApiOperation("新增资产减值预测检查")
    @PostMapping("/ecl/execute/predict/record/addCheck")
    R<String> addCheck(@RequestBody @Valid EclExecutePredictRecordAddREQ req);

    @ApiOperation("新增资产减值预测导入")
    @PostMapping("/ecl/execute/predict/record/import")
    R<Void> importFile(@Valid EclExecutePredictRecordImportREQ req);

    @ApiOperation("新增资产减值预测导入检查")
    @PostMapping("/ecl/execute/predict/record/check")
    R<Set<String>> importFileCheck(@Valid EclExecutePredictRecordImportREQ req);

    @ApiOperation("修改资产减值预测详情记录表")
    @PostMapping("/ecl/execute/predict/record/modify")
    R<Void> modify(@RequestBody @Valid EclExecutePredictRecordModifyREQ req);

    @ApiOperation("资产减值预测详情记录表列表")
    @PostMapping("/ecl/execute/predict/record/list")
    R<PageR<EclExecutePredictRecordListRSP>> list(@RequestBody @Valid EclExecutePredictRecordListREQ req);

    @ApiOperation("删除资产减值预测详情记录表")
    @PostMapping("/ecl/execute/predict/record/remove")
    R<Void> remove(@RequestBody @Valid EclExecutePredictRecordRemoveREQ req);

}