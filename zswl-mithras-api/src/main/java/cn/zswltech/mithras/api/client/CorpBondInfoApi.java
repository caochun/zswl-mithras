package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bondinfo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author luyi
 */
@Api(tags = "法人公司发债及评级信息-接口")
public interface CorpBondInfoApi {

    @ApiOperation("发债及评级信息新增")
    @PostMapping("/corp/bond/info/add")
    R<Void> add(@RequestBody @Valid CorpBondInfoAddREQ req);

    @ApiOperation("发债及评级信息变更")
    @PostMapping("/corp/bond/info/modify")
    R<Void> modify(@RequestBody @Valid CorpBondInfoModifyREQ req);

    @ApiOperation("发债及评级信息删除")
    @PostMapping("/corp/bond/info/remove")
    R<Void> remove(@RequestBody @Valid CorpBondInfoRemoveREQ req);

    @ApiOperation("发债及评级信息列表")
    @PostMapping("/corp/bond/info/list")
    R<PageR<CorpBondInfoListRSP>> list(@RequestBody @Valid CorpBondInfoListREQ req);

}
