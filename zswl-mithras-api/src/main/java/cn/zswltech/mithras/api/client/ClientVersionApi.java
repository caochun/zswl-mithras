package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.clientversion.ClientVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 客户版本接口
 *
 * @author wangchuanhao
 * @date 2022/6/23 2:34 PM
 */
@Api(value = "客户版本接口", tags = "客户版本接口")
@RequestMapping("/client/version")
public interface ClientVersionApi {

    @ApiOperation("客户版本列表")
    @PostMapping("/list")
    R<PageR<CommonVersionListRSP>> list(@RequestBody @Valid CommonVersionListREQ req);

    @ApiOperation("客户版本比较详情（与上一版本比较）")
    @PostMapping("/compare/preVersion")
    R<CommonVersionDiffRSP> comparePreVersion(@RequestBody @Valid ClientVersionDiffREQ req);


}
