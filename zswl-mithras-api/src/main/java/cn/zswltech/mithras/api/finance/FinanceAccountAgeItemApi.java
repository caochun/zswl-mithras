package cn.zswltech.mithras.api.finance;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.finance.accountage.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 帐龄-详情表
* @author vico
* @date 2024-09-10
*/
@Api(tags = "帐龄-详情表-接口")
public interface FinanceAccountAgeItemApi {

    @ApiOperation("新增帐龄-详情表")
    @PostMapping("/finance/account/age/item/add")
    R<Void> add(@RequestBody @Valid FinanceAccountAgeItemAddREQ req);

    @ApiOperation("修改帐龄-详情表")
    @PostMapping("/finance/account/age/item/modify")
    R<Void> modify(@RequestBody @Valid FinanceAccountAgeItemModifyREQ req);

    @ApiOperation("帐龄-详情表列表")
    @PostMapping("/finance/account/age/item/list")
    R<PageR<FinanceAccountAgeItemListRSP>> list(@RequestBody @Valid FinanceAccountAgeItemListREQ req);

    @ApiOperation("删除帐龄-详情表")
    @PostMapping("/finance/account/age/item/remove")
    R<Void> remove(@RequestBody @Valid FinanceAccountAgeItemRemoveREQ req);

    @ApiOperation("重新生成-详情表")
    @PostMapping("/finance/account/age/item/regeneration")
    R<Void> regeneration(@RequestBody @Valid FinanceAccountAgeItemRemoveREQ req);

    @ApiOperation("推送至苍穹")
    @PostMapping("/finance/account/age/item/send")
    R<Void> sendRemote(@RequestBody @Valid FinanceAccountAgeItemRemoveREQ req);


}