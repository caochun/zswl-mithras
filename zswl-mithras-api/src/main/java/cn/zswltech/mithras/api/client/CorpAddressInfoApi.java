package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.addressinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "法人公司地址信息-接口")
public interface CorpAddressInfoApi {

    @ApiOperation("新增公司地址信息")
    @PostMapping("/corp/address/add")
    R<Void> add(@RequestBody @Valid CorpAddressInfoAddREQ req);

    @ApiOperation("修改地址信息")
    @PostMapping("/corp/address/modify")
    R<Void> modify(@RequestBody @Valid CorpAddressInfoModifyREQ req);

    @ApiOperation(("地址信息列表"))
    @PostMapping("/corp/address/list")
    R<PageR<CorpAddressInfoListRSP>> list(@RequestBody @Valid CorpAddressInfoListREQ req);

    @ApiOperation("删除地址信息")
    @PostMapping("/corp/address/remove")
    R<Void> remove(@RequestBody @Valid CorpAddressInfoRemoveREQ req);
}
