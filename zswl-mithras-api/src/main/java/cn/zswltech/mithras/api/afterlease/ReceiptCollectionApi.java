package cn.zswltech.mithras.api.afterlease;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

@Api(tags = "租金催收-借据卡逾期")
public interface ReceiptCollectionApi {

    //@ApiOperation("借据卡逾期汇总")
    //@PostMapping("/receipt/collection/list")
    R<PageR<ReceiptCollectionListRSP>> list(@RequestBody @Valid ReceiptCollectionListREQ req);

    @ApiOperation("借据卡逾期催收")
    @PostMapping("/receipt/overdue/collection")
    R<CollectionOverdueRSP> overdue(@RequestBody @Valid CollectionOverdueREQ req);

    /*@ApiOperation("通知财务系统收款")
    @PostMapping("/receipt/collection/notice")
    R<Void> collectionNotice(@RequestBody @Valid ReceiptCollectionNoticeREQ req);*/

    @ApiOperation("罚息减免审批")
    @PostMapping("/receipt/collection/effect")
    R<Void> effect(@Valid CollectionPenaltyReductionEffectREQ req);

    @ApiOperation("罚息减免修改")
    @PostMapping("/receipt/collection/modify")
    R<Void> modify(@Valid CollectionPenaltyReductionModifyREQ req);

    @ApiOperation("租后-罚息减免基本表列表")
    @PostMapping("/collection/penalty/reduction/info/list")
    R<PageR<CollectionPenaltyReductionInfoListRSP>> reductionList(@RequestBody @Valid CollectionPenaltyReductionInfoListREQ req);

    @ApiOperation("罚息减免获取合同ID")
    @PostMapping("/receipt/relation/contract")
    R<CollectionRelationContractRSP> relationContract(@RequestBody @Valid CollectionRelationContractREQ req);

    @ApiOperation("获取借据下剩余罚息")
    @PostMapping("/receipt/receipt/interest")
    R<ReceiptReduceInterestRSP> calculationInterest(@RequestBody @Valid ReceiptReduceInterstREQ req);

}
