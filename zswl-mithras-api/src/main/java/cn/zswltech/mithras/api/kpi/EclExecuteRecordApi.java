package cn.zswltech.mithras.api.kpi;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
* @description 资产减值记录表
* @author vico
* @date 2025-09-28
*/
@Api(tags = "资产减值记录表-接口")
public interface EclExecuteRecordApi {

    @ApiOperation("新增资产减值记录表")
    @PostMapping("/ecl/execute/record/add")
    R<Void> add(@RequestBody @Valid EclExecuteRecordAddREQ req);

    @ApiOperation("新增资产减值记录表检查")
    @PostMapping("/ecl/execute/record/addCheck")
    R<String> addCheck(@RequestBody @Valid EclExecuteRecordAddREQ req);

    @ApiOperation("新增资产减值记录表导入")
    @PostMapping("/ecl/execute/record/import")
    R<Void> importFile(@Valid EclExecuteRecordImportREQ req);

    @ApiOperation("新增资产减值记录表导入检查")
    @PostMapping("/ecl/execute/record/import/check")
    R<Set<String>> importFileCheck(@Valid EclExecuteRecordImportREQ req);

    @ApiOperation("修改资产减值记录表")
    @PostMapping("/ecl/execute/record/modify")
    R<Void> modify(@RequestBody @Valid EclExecuteRecordModifyREQ req);

    @ApiOperation("资产减值记录表列表")
    @PostMapping("/ecl/execute/record/list")
    R<PageR<EclExecuteRecordListRSP>> list(@RequestBody @Valid EclExecuteRecordListREQ req);

    @ApiOperation("资产减值记录表比对列表")
    @PostMapping("/ecl/execute/record/list/compare")
    R<List<EclExecuteRecordListRSP>> listCompare(@RequestBody @Valid EclExecuteRecordListREQ req);

    @ApiOperation("删除资产减值记录表")
    @PostMapping("/ecl/execute/record/remove")
    R<Void> remove(@RequestBody @Valid EclExecuteRecordRemoveREQ req);

    @ApiOperation("删除资产减值全量记录表")
    @PostMapping("/ecl/execute/record/all/remove")
    R<Void> allRecordRemove(@RequestBody @Valid EclExecuteRecordRemoveREQ req);

}