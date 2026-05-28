package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkDto;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkListReq;
import cn.zswltech.mithras.dto.workbench.WorkbenchHyperlinkModifyReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@Api(tags = "首页工作台-超链接-接口")
public interface WorkbenchHyperlinkApi {
    @ApiOperation("修改首页工作台-超链接")
    @PostMapping("/workbench/hyperlink/modify")
    R<Void> modify(@RequestBody @Valid WorkbenchHyperlinkModifyReq req);

    @ApiOperation("首页工作台-超链接列表")
    @PostMapping("/workbench/hyperlink/list")
    R<List<WorkbenchHyperlinkDto>> list(@RequestBody @Valid WorkbenchHyperlinkListReq req);
}