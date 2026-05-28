package cn.zswltech.mithras.api.projestablish;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailREQ;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceDetailRSP;
import cn.zswltech.mithras.dto.projestablish.ProjEstablishPriceModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Api(tags = "立项管理-报价方案复合接口")
public interface ProjEstablishPriceApi {

    @ApiOperation("修改报价方案")
    @PostMapping("/proj/establish/price/modify")
    R<Void> modify(@RequestBody @Valid ProjEstablishPriceModifyREQ req);

    @ApiOperation("查询报价方案")
    @PostMapping("/proj/establish/price/detail")
    R<ProjEstablishPriceDetailRSP> detail(@RequestBody @Valid ProjEstablishPriceDetailREQ req);
}
