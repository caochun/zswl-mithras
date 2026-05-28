package cn.zswltech.mithras.api.workbench;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsListRsp;
import cn.zswltech.mithras.dto.workbench.WorkbenchShortcutsMaintainReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName WorkbenchShortcutsApi
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/16 4:51 下午
 * @Version 1.0
 **/
@Api(tags = "首页工作台-快捷功能-接口")
public interface WorkbenchShortcutsApi {

    @ApiOperation("新增首页工作台-快捷功能-列表")
    @PostMapping("/workbench/shortcuts/list")
    R<List<WorkbenchShortcutsListRsp>> list();

    @ApiOperation("新增首页工作台-快捷功能-维护")
    @PostMapping("/workbench/shortcuts/maintain")
    R<Void> add(@RequestBody @Valid WorkbenchShortcutsMaintainReq req);

}
