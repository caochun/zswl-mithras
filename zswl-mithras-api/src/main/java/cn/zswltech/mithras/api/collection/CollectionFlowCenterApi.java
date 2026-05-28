package cn.zswltech.mithras.api.collection;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName CollectionFlowCenterApi
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/2/26 10:13 上午
 * @Version 1.0
 **/
@Api(tags = "收款核销明细-流水中心接口")
public interface CollectionFlowCenterApi {

    @ApiOperation("付款业务流水列表")
    @PostMapping("/collection/flow/center/business/payment/list")
    R<PageR<CollectionFlowCenterBusinessPaymentListRSP>> paymentList(@RequestBody @Valid CollectionFlowCenterBusinessPaymentListREQ req);

    @ApiOperation("付款手工核销")
    @PostMapping("/collection/flow/center/business/payment/manual/record")
    R<Void> paymentManualRecord(@RequestBody @Valid CollectionFlowCenterBusinessPaymentManualRecordREQ req);

    @ApiOperation("付款业务流水待核销现金流列表")
    @PostMapping(path = "/collection/flow/center/business/payment/manual/cashFlowList")
    R<List<CollectionFlowCenterBusinessPaymentManualRecordRSP>> paymentManualCashFlowList(@RequestBody @Valid CollectionFlowCenterBusinessPaymentManualCashFlowListREQ req);

    @ApiOperation("付款业务流水结算明细")
    @PostMapping("/collection/flow/center/business/payment/settle/detail")
    R<List<CollectionFlowCenterBusinessPaymentSettleDetailRSP>> paymentSettleDetail(@RequestBody @Valid CollectionFlowCenterBusinessPaymentSettleDetailREQ req);

    @ApiOperation("收款业务流水列表")
    @PostMapping("/collection/flow/center/business/collection/list")
    R<PageR<CollectionFlowCenterBusinessCollectionListRSP>> collectionList(@RequestBody @Valid CollectionFlowCenterBusinessCollectionListREQ req);

    @ApiOperation("收款手工核销")
    @PostMapping("/collection/flow/center/business/collection/manual/record")
    R<Void> collectionManualRecord(@RequestBody @Valid CollectionFlowCenterBusinessCollectionManualRecordREQ req);

    @ApiOperation("收款业务流水结算明细")
    @PostMapping("/collection/flow/center/business/collection/settle/detail")
    R<List<CollectionFlowCenterBusinessCollectionSettleDetailRSP>> collectionSettleDetail(@RequestBody @Valid CollectionFlowCenterBusinessCollectionSettleDetailREQ req);

    @ApiOperation("流水中心统计")
    @PostMapping("/collection/flow/center/count")
    R<CollectionFlowCenterCountRSP> flowCenterCount();

    @ApiOperation("导出收款业务流水")
    @PostMapping("/collection/flow/center/business/export")
    void exportBusinessCollection(@RequestBody @Valid CollectionFlowCenterBusinessCollectionExportListREQ collectionExportListREQ);

    @ApiOperation("收款业务流水列表详情")
    @PostMapping("/collection/flow/center/business/export/list")
    R<List<CollectionFlowCenterBusinessCollectionListRSP>> businessCollectionExportList(@RequestBody @Valid CollectionFlowCenterBusinessCollectionExportREQ collectionExportREQ);

    @ApiOperation("查询客户下可用合同保证金")
    @PostMapping("/collection/flow/center/client/contract/margin")
    R<List<CollectionFlowCenterClientContractMarginRSP>> clientContractMargin(@RequestBody @Valid CollectionFlowCenterClientContractMarginREQ req);


    @ApiOperation("获取保证金回收计划")
    @PostMapping("/collection/flow/center/recycle/margin/plan")
    R<CollectionFlowCenterRecycleMarginPlanRSP> recycleMarginPlan(@RequestBody @Valid CollectionFlowCenterRecycleMarginPlanREQ req);

}
