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
* @description 绩效考核-参数设置基本表
* @author vico
* @date 2024-09-21
*/
@Api(tags = "绩效考核-参数设置基本表-接口")
public interface KpiParameterBaseApi {

    @ApiOperation("新增绩效考核-参数设置基本表")
    @PostMapping("/kpi/parameter/base/add")
    R<Long> add(@RequestBody @Valid KpiParameterBaseAddREQ req);

    @ApiOperation("绩效考核-参数设置基本表-生效")
    @PostMapping("/kpi/parameter/base/effect")
    R<Void> effect(@RequestBody @Valid KpiParameterBaseCommonREQ req);

    @ApiOperation("绩效考核-参数设置基本表-关闭")
    @PostMapping("/kpi/parameter/base/close")
    R<Void> close(@RequestBody @Valid KpiParameterBaseCommonREQ req);

    @ApiOperation("绩效考核-参数设置基本表列表")
    @PostMapping("/kpi/parameter/base/list")
    R<PageR<KpiParameterBaseListRSP>> list(@RequestBody @Valid KpiParameterBaseListREQ req);

    @ApiOperation("删除绩效考核-参数设置基本表")
    @PostMapping("/kpi/parameter/base/remove")
    R<Void> remove(@RequestBody @Valid KpiParameterBaseCommonREQ req);

    @ApiOperation("复制到绩效考核-参数设置基本表")
    @PostMapping("/kpi/parameter/base/copy")
    R<Long> copy(@RequestBody @Valid KpiParameterBaseCopyREQ req);

}