package cn.zswltech.mithras.api.file.template;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.template.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 文件模板管理-API
 *
 * @author yibin
 */
@Api(tags = "文件模板管理接口")
public interface FileTemplateApi {

    @ApiOperation("新增模板类型")
    @PostMapping("/file/template/type/add")
    R<Void> addTemplateType(@RequestBody @Valid FileTemplateTypeAddREQ req);

    @ApiOperation("删除模板类型")
    @PostMapping("/file/template/type/remove")
    R<Void> removeTemplateType(@RequestBody @Valid FileTemplateTypeRemoveREQ req);

    @ApiOperation(value = "模板类型列表", notes = "暂不分页")
    @PostMapping("/file/template/type/list")
    R<FileTemplateTypeListRSP> listTemplateType();

    @ApiOperation("新增模板文件")
    @PostMapping("/file/template/add")
    R<Void> addTemplate(@Valid FileTemplateAddREQ req);

    @ApiOperation("替换模板文件")
    @PostMapping("/file/template/replace")
    R<Void> replaceTemplate(@Valid FileTemplateReplaceREQ req);

    @ApiOperation(value = "模板文件列表", notes = "暂不分页")
    @PostMapping("/file/template/list")
    R<PageR<FileTemplateListRSP>> listTemplate(@RequestBody @Valid FileTemplateListREQ req);

    @ApiOperation("某个模板文件的历史记录")
    @PostMapping("/file/template/history/list")
    R<PageR<FileTemplateHistoryListRSP>> listTemplateHistory(@RequestBody @Valid FileTemplateHistoryListREQ req);

    @PostMapping("/file/template/history/rollback")
    @ApiOperation("回滚到某个历史版本")
    R<Void> rollbackHistory(@RequestBody @Valid FileTemplateHistoryRollbackREQ req);

    @ApiOperation("修改模版")
    @PostMapping("/file/template/update")
    R<Void> updateTemplate(@RequestBody @Valid FileTemplateUpdateREQ req);
}
