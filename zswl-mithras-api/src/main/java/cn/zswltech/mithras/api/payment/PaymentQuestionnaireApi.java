package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionModifyReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 11:06
 */
@Api(tags= "付款管理-问卷调查接口")
public interface PaymentQuestionnaireApi {

    @ApiOperation("问卷查询")
    @PostMapping("/payment/questionnaire/list")
    R<List<PaymentQuestionListRsp>> list(@RequestBody PaymentQuestionListReq req);

    @ApiOperation("问卷更新")
    @PostMapping("/payment/questionnaire/modify")
    R<Void> modify(@RequestBody List<PaymentQuestionModifyReq> reqs);

}
