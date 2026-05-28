package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author junke
 */
@Api(tags = "自然人配偶信息-接口")
public interface NormalSpouseApi {

    @ApiOperation("可选配偶列表")
    @PostMapping("/normal/spouse/select")
    R<List<NormalSpouseSelectRSP>> select(@RequestBody @Valid NormalSpouseSelectREQ req);

    @ApiOperation("自然人配偶信息-新增")
    @PostMapping("/normal/spouse/add")
    R<Void> add(@RequestBody @Valid NormalSpouseAddREQ req);

    @ApiOperation("自然人配偶信息-修改")
    @PostMapping("/normal/spouse/modify")
    R<Void> modify(@RequestBody @Valid NormalSpouseModifyREQ req);

    @ApiOperation("自然人配偶信息-删除")
    @PostMapping("/normal/spouse/remove")
    R<Void> remove(@RequestBody @Valid NormalSpouseRemoveREQ req);

    @ApiOperation("自然人配偶信息-详情")
    @PostMapping("/normal/spouse/detail")
    R<PageR<NormalSpouseListRSP>> detail(@RequestBody @Valid NormalSpouseListREQ req);

}
