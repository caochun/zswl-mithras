package cn.zswltech.mithras.api.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionPageReq;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlRelatedTransactionRsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description 金控关联方名录
 * @date 2023-03-08
 */
@Api(tags = "金控关联方名录-接口")
public interface RiskControlRelatedClientApi {

    /**
     * @param file
     * @return
     * @see cn.zswltech.mithras.riskcontrol.report.gljy.RiskControlGljyReportService#syncRelatedClient
     */
    @Deprecated
    @ApiOperation("导入金控关联方名录")
    @PostMapping("/risk/control/related/client/add")
    R<Void> importFile(@RequestParam("file") MultipartFile file);

    @ApiOperation("付款流水列表")
    @PostMapping("/risk/control/related/transaction/payment")
    R<PageR<RiskControlRelatedTransactionRsp>> paymentList(@RequestBody @Valid RiskControlRelatedTransactionPageReq req);

    @ApiOperation("收款流水列表")
    @PostMapping("/risk/control/related/transaction/collection")
    R<PageR<RiskControlRelatedTransactionRsp>> collectionList(@RequestBody @Valid RiskControlRelatedTransactionPageReq req);

    @ApiOperation("专用下拉列表")
    @PostMapping("/risk/control/related/client/pulldown")
    R<Map<String, List<String>>> pullDown();


}
