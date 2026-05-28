package cn.zswltech.mithras.api.contract;

import cn.zswltech.mithras.api.common.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName ContractStartRentNoticeApi
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/4/27 3:18 下午
 * @Version 1.0
 **/
@Api(tags = "合同起租提醒相关接口")
public interface ContractStartRentNoticeApi {

    @ApiOperation("十分钟后提醒我")
    @PostMapping("/contract/start/rent/delay")
    R<Void> savePlanNormal();

}
