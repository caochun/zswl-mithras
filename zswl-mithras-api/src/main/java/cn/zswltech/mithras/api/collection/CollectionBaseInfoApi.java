package cn.zswltech.mithras.api.collection;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @create: 2022-08-15
 **/

@Api(tags = "收款核销明细-接口")
public interface CollectionBaseInfoApi {
    @ApiOperation("收款核销列表")
    @PostMapping("/collection/list")
    R<PageR<CollectionBaseInfoListRSP>> list(@RequestBody @Valid CollectionBaseInfoREQ req);

    /**
     * 本来应该前端界面上直接导出的
     * @param req
     * @return
     */
    @ApiOperation("收款核销列表（excel导出）")
    @PostMapping("/collection/list/export")
//    void exportList(@RequestBody @Valid CollectionBaseInfoListExportREQ req);
    void exportList(@RequestBody @Valid CollectionBaseInfoREQ req);

    @ApiOperation("收款核销明细")
    @PostMapping("/collection/detail")
    R<CollectionBaseInfoRSP> detail(@RequestBody @Valid CollectionBaseInfoDetailREQ req);


    @ApiOperation("罚息信息")
    @PostMapping("/collection/penaltyInterest/detail")
    R<CollectionPenaltyInterestRSP> penaltyInterestDetail (@RequestBody @Valid CollectionBaseInfoDetailREQ req);


    @ApiOperation("罚息记录")
    @PostMapping("/collection/penaltyInterest/record/list")
    R<PageR<PenaltyInterestListRSP>> penaltyInterestList(@RequestBody @Valid CollectionPenaltyInterestREQ req);

//    @ApiOperation("罚息修改")
//    @PostMapping("/collection/update/penaltyInterest/record")
//    R<Void> updateRecord(@RequestBody @Valid CollectionBaseInfoUpdateREQ req);
}
