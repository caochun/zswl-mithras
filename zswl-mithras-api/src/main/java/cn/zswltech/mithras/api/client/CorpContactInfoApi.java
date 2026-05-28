package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.contactinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "法人联系人-接口")
public interface CorpContactInfoApi {

    @ApiOperation("新增联系人")
    @PostMapping("/corp/contact/add")
    R<Void> add(@RequestBody @Valid CorpContactAddInfoREQ req);

    @ApiOperation("修改联系人")
    @PostMapping("/corp/contact/modify")
    R<Void> modify(@RequestBody @Valid CorpContactInfoModifyREQ req);

    @ApiOperation("联系人列表")
    @PostMapping("/corp/contact/list")
    R<PageR<CorpContactInfoListRSP>> list(@RequestBody @Valid CorpContactInfoListREQ req);

    @ApiOperation("联系人列表")
    @PostMapping("/corp/contact/old/list")
    R<PageR<CorpContactInfoListRSP>> listOld(@RequestBody @Valid CorpContactInfoListREQ req);

    @ApiOperation("删除联系人")
    @PostMapping("/corp/contact/remove")
    R<Void> remove(@RequestBody @Valid CorpContactInfoRemoveREQ req);
}
