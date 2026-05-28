package cn.zswltech.mithras.api.report;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.report.fiveclass.*;
import cn.zswltech.mithras.dto.version.DiffFile;
import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 征信报送-五级分类表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:16 PM
 */
@Api("征信报送-五级分类表接口")
public interface CrFiveClassApi {

    @ApiOperation("五级分类表列表查询")
    @PostMapping("/cr/five/class/list")
    R<List<Map<String, DiffValue>>> list(@RequestBody @Valid FiveClassListREQ req);

    @ApiOperation("五级分类表新增")
    @PostMapping("/cr/five/class/add")
    R<Void> add(@RequestBody @Valid FiveClassAddREQ req);

    @ApiOperation("五级分类表编辑")
    @PostMapping("/cr/five/class/modify")
    R<Void> modify(@RequestBody @Valid FiveClassModifyREQ req);

    @ApiOperation("五级分类表删除")
    @PostMapping("/cr/five/class/remove")
    R<Void> remove(@RequestBody @Valid FiveClassRemoveREQ req);

    @ApiOperation("五级分类表取消删除")
    @PostMapping("/cr/five/class/cancel/remove")
    R<Void> cancelRemove(@RequestBody @Valid FiveClassRemoveREQ req);

}
