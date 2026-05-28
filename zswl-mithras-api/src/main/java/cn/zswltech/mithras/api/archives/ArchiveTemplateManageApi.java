package cn.zswltech.mithras.api.archives;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.archives.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @create: 2023-02-23
 **/
@Api(tags = "归档任务模版管理-接口")
public interface ArchiveTemplateManageApi {

    @ApiOperation("模版列表")
    @PostMapping("/archive/template/list")
    R<PageR<ArchiveTemplateListRSP>> list(@RequestBody @Valid ArchiveTemplateListREQ req);

    @ApiOperation("模版新增")
    @PostMapping("/archive/template/add")
    R<Void> add(@RequestBody @Valid ArchiveTemplateAddREQ req);

    @ApiOperation("模版详情")
    @PostMapping("/archive/template/info")
    R<ArchiveTemplateInfoRSP> info(@RequestBody @Valid ArchiveTemplateInfoREQ req);

    @ApiOperation("状态修改")
    @PostMapping("/archive/template/update")
    R<Void> updateStatus(@RequestBody @Valid ArchiveTemplateUpdateREQ req);

}
