package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.*;
import cn.zswltech.mithras.dto.contract.HighSeasCustomersREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author luyi
 */
@Api(tags = "法人工商信息-接口")
public interface CorpCommerceInfoApi {

    @ApiOperation("新增法人工商信息")
    @PostMapping("/corp/commerce/add")
    R<Void> add(@RequestBody @Valid CorpCommerceInfoAddREQ req);

    @ApiOperation("修改法人工商信息")
    @PostMapping("/corp/commerce/modify")
    R<Void> modify(@RequestBody @Valid CorpCommerceInfoModifyREQ req);

    @ApiOperation("法人工商信息详情")
    @PostMapping("/corp/commerce/detail")
    R<CorpCommerceInfoDetailRSP> detail(@RequestBody @Valid CorpCommerceInfoDetailREQ req);

    @ApiOperation("获取公海客户列表")
    @PostMapping("/high/seas/customers/list")
    R<List<SelectRSP>> listHighSegasCustomers(@RequestBody HighSeasCustomersREQ req);

    @ApiOperation("客户工商信息校验")
    @PostMapping("/corp/commerce/valid")
    R<ClientCorpCommerceInfoValidRSP> valid(@RequestBody @Valid ClientCorpCommerceInfoValidREQ req);

}
