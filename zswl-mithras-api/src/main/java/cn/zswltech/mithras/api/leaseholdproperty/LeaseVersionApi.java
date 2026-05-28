package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseReviewEffectREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 租赁物流程API接口
 **/
@Api(tags = "租赁物类型API接口")
@RequestMapping(path = "/lease/review")
public interface LeaseVersionApi {

    @PostMapping("/modify/effect")
    @ApiOperation("租赁物审核-变更流程审核")
    R<Void> effect(@RequestBody LeaseReviewEffectREQ param);

}
