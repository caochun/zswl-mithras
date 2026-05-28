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
* @description 帐龄主表
* @author vico
* @date 2024-09-10
*/
@Api(tags = "帐龄主表-接口")
public interface FinanceAccountAgeBaseInfoApi {

    @ApiOperation("新增帐龄主表")
    @PostMapping("/finance/account/age/base/info/add")
    R<Long> add(@RequestBody @Valid FinanceAccountAgeBaseInfoAddREQ req);

    @ApiOperation("关闭帐龄主表")
    @PostMapping("/finance/account/age/base/info/close")
    R<Void> close(@RequestBody @Valid FinanceAccountAgeBaseInfoCloseREQ req);

    @ApiOperation("帐龄主表列表")
    @PostMapping("/finance/account/age/base/info/list")
    R<PageR<FinanceAccountAgeBaseInfoListRSP>> list(@RequestBody @Valid FinanceAccountAgeBaseInfoListREQ req);

    @ApiOperation("帐龄主表详情")
    @PostMapping("/finance/account/age/base/info/detail")
    R<FinanceAccountAgeBaseInfoDetailRSP> detail(@RequestBody @Valid FinanceAccountAgeBaseInfoDetailREQ req);

    @ApiOperation("删除帐龄主表")
    @PostMapping("/finance/account/age/base/info/remove")
    R<Void> remove(@RequestBody @Valid FinanceAccountAgeBaseInfoRemoveREQ req);

    @ApiOperation("帐龄主表-完成")
    @PostMapping("/finance/account/age/base/info/effect")
    R<Void> effect(@RequestBody @Valid FinanceAccountAgeBaseInfoRemoveREQ req);

    @ApiOperation("帐龄月份-统计")
    @PostMapping("/finance/account/age/base/info/count")
    R<FinanceAccountAgeCountRSP> count(@RequestBody @Valid FinanceAccountAgeBaseInfoDetailREQ req);


}