package cn.zswltech.mithras.api.InterestPay;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.interestPay.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "应付利息")
@RestController
public interface InterestPayApi {

    @ApiOperation("列表")
    @PostMapping("/interestPay/list")
    R<List<InterestPayRSP>> listPage(@RequestBody @Valid InterestPayListREQ req);

    @ApiOperation("计提利息计算")
    @PostMapping("/interestPay/calculate")
    R<List<InterestPayRSP>> calculate(@RequestBody @Valid InterestPayREQ req);

    @ApiOperation("基本详情")
    @PostMapping("/interestPay/basic/detail")
    R<InterestPayBasicDetailRSP> basicDetail(@RequestBody @Valid InterestPayBasicDetailREQ req);

    @ApiOperation("应付计提利息详情")
    @PostMapping("/interestPay/cal/detail")
    R<List<InterestPayCalDetailMultiRSP>> calDetail(@RequestBody @Valid InterestPayCalDetailREQ req);

    @ApiOperation("应付计提利息详情修改")
    @PostMapping("/interestPay/cal/detail/modify")
    R<Void> modify(@RequestBody @Valid InterestPayCalDetailModifyREQ req);
}
