package cn.zswltech.mithras.api.contractcp;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.contractcp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @create: 2022-08-15
 **/
@Api(tags = "合同收付款-接口")
public interface ContractCollectionPaymentApi {

    @ApiOperation("合同收付款列表")
    @PostMapping("/contractcp/list")
    R<PageR<ContractCollectionPaymentListRSP>> list(@RequestBody @Valid ContractCollectionPaymentListREQ req);

    /**
     * 本来应该前端界面上直接导出的 前端说时间不够让后端做
     * @param req
     * @return
     */
    @ApiOperation("合同收付款列表（excel导出）")
    @PostMapping("/contractcp/list/export")
//    void exportList(@RequestBody @Valid ContractCollectionPaymentListExportREQ req);
    void exportList(@RequestBody @Valid ContractCollectionPaymentListREQ req);

    @ApiOperation("合同收付款列表（租金支付通知书）")
    @PostMapping("/contractcp/list/pushRentNotify")
    R<Void> pushRentNotify(@RequestBody @Valid ContractCollectionPaymentListREQ req);

    @ApiOperation("项目关联合同列表")
    @PostMapping("/contractcp/contract/list")
    R<List<SelectRSP>> contractList(@RequestBody @Valid ContractcpContractDetailREQ req);

    @ApiOperation("项目关联合同明细")
    @PostMapping("/contractcp/contract/detail")
    R<ContractInfoRSP> contractDetail(@RequestBody @Valid ContractcpContractDetailREQ req);

    @ApiOperation("现金流分类列表")
    @PostMapping("/contractcp/cash/list")
    R<List<SelectRSP>> cashList(@RequestBody @Valid ContractcpContractDetailREQ req);

    @ApiOperation("现金流明细")
    @PostMapping("/contractcp/cash/detail")
    R<PageR<ContractRentActualInfoRSP>> cashDetail(@RequestBody @Valid ContractCollectionPaymentDetailREQ req);

    /**
     * 本来应该前端界面上直接导出的 前端说时间不够让后端做
     * @param req
     * @return
     */
    @ApiOperation("现金流明细（excel导出）")
    @PostMapping("/contractcp/cash/detail/export")
    R<Void> exportCashDetail(@RequestBody @Valid ContractCollectionPaymentDetailExportREQ req);

}
