package cn.zswltech.mithras.api.kpi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordModifyREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordListREQ;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordListRSP;
import cn.zswltech.mithras.dto.kpi.KpiPaymentAmountRecordRemoveREQ;

/**
* @description 绩效考核-投放信息记录表
* @author vico
* @date 2024-09-27
*/
@Api(tags = "绩效考核-投放信息记录表-接口")
public interface KpiPaymentAmountRecordApi {

    @ApiOperation("新增绩效考核-投放信息记录表")
    @PostMapping("/kpi/payment/amount/record/add")
    R<Void> add(@RequestBody @Valid KpiPaymentAmountRecordAddREQ req);

    @ApiOperation("修改绩效考核-投放信息记录表")
    @PostMapping("/kpi/payment/amount/record/modify")
    R<Void> modify(@RequestBody @Valid KpiPaymentAmountRecordModifyREQ req);

    @ApiOperation("绩效考核-投放信息记录表列表")
    @PostMapping("/kpi/payment/amount/record/list")
    R<PageR<KpiPaymentAmountRecordListRSP>> list(@RequestBody @Valid KpiPaymentAmountRecordListREQ req);

    @ApiOperation("删除绩效考核-投放信息记录表")
    @PostMapping("/kpi/payment/amount/record/remove")
    R<Void> remove(@RequestBody @Valid KpiPaymentAmountRecordRemoveREQ req);

}