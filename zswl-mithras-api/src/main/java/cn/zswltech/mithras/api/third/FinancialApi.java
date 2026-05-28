package cn.zswltech.mithras.api.third;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.third.financial.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 财务对接系统api
 * @author: jackerhe
 * esb命名规范[ip]:[端口]/[业务域]/[系统]/[服务]/[操作]规范命名接口
 * @date: 2022/10/17 2:22 下午
 **/
@Api(tags = "三方-财务系统-苍穹")
public interface FinancialApi {

    @ApiOperation("付款记录明细")
    @PostMapping("/cico/mithras/payment/record")
    R<String> paymentRecode(@RequestBody @Valid ThirdPaymentDetailREQ req);

    @ApiOperation("收款记录明细")
    @PostMapping("/cico/mithras/collection/record")
    R<String> collectionRecode(@RequestBody @Valid ThirdCollectionRecordREQ req);

    @ApiOperation("保证金管理-内扣/退回")
    @PostMapping("/cico/mithras/margin/record")
    R<String> addBackRecord(@RequestBody @Valid ThirdMarginRecordREQ req);

    @ApiOperation("内部使用，收款核销")
    @PostMapping("/inner/record")
    R<Void> innerRecord(@RequestBody InnerCollectionRecordREQ req);

    @ApiOperation("苍穹接口撤回")
    @PostMapping("/third/financial/withdraw")
    R<List<ThirdFinancialWithdrawRSP>> withdraw(@RequestBody List<ThirdFinancialWithdrawREQ> req);


}
