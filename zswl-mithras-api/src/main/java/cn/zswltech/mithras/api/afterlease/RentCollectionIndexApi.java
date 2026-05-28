package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 租金催收首页
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:24 PM
 */
@Api(tags = "租金催收首页-接口")
public interface RentCollectionIndexApi {

    @ApiOperation("租金催收首页列表")
    @PostMapping("/rent/collection/index/list")
    R<PageR<RentCollectionListRSP>> indexList(@RequestBody @Valid RentCollectionListREQ req);

    @ApiOperation("租金催收罚息减免-提交审批")
    @PostMapping("/rent/collection/penalty/effect")
    R<Long> penaltyReductionEffect(@Valid RentCollectionPenaltyReduceREQ req);

    @ApiOperation("租金催收罚息减免-变更")
    @PostMapping("/rent/collection/penalty/modify")
    R<Void> penaltyReductionModify(@RequestBody @Valid PenaltyReduceDetailModifyREQ req);

    @ApiOperation("租金催收罚息减免-列表")
    @PostMapping("/rent/collection/penalty/reduction/list")
    R<RentCollectionPenaltyReduceDetailRSP> penaltyReductionList(@RequestBody @Valid RentCollectionPenaltyReduceListREQ req);



}
