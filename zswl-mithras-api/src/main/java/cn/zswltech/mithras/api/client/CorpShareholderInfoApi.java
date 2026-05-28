package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.shareholder.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "法人股东信息-接口")
public interface CorpShareholderInfoApi {

    @ApiOperation("股东信息新增")
    @PostMapping("/corp/shareholder/info/add")
    R<Void> add(@RequestBody @Valid CorpShareholderInfoAddREQ req);

    @ApiOperation("股东信息变更")
    @PostMapping("/corp/shareholder/info/modify")
    R<Void> modify(@RequestBody @Valid CorpShareholderInfoModifyREQ req);

    @ApiOperation("股东信息删除")
    @PostMapping("/corp/shareholder/info/remove")
    R<Void> remove(@RequestBody @Valid CorpShareholderInfoRemoveREQ req) throws Exception;

    @ApiOperation("股东信息列表")
    @PostMapping("/corp/shareholder/info/list")
    R<PageR<CorpShareholderInfoListRSP>> list(@RequestBody @Valid CorpShareholderInfoListREQ req);
    

}
