package cn.zswltech.mithras.api.payment;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsOperateReq;
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
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/12 18:36
 */
@Api(tags = "付款管理-材料接口")
public interface PaymentMaterialsApi {
    @ApiOperation("资料清单")
    @PostMapping("/materials/payment/list")
    R<Map<String,List<PaymentMaterialsListRsp>>> list(@RequestBody @Valid PaymentMaterialsListReq req);

    @ApiOperation("材料上传")
    @PostMapping("/materials/payment/upload")
    R<Void> upload(@RequestParam("file") MultipartFile file,
                   @RequestParam("paymentId")Long paymentId,
                   @RequestParam("materialsType") String materialsType);

    @ApiOperation("材料删除")
    @PostMapping("/materials/payment/remove")
    R<Void> remove(@RequestBody @Valid PaymentMaterialsOperateReq req);

}
