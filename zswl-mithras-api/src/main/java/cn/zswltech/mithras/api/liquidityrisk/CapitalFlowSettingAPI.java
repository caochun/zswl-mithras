package cn.zswltech.mithras.api.liquidityrisk;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.liquidityrisk.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.time.LocalDate;

/**
 * @author wwj
 * @create: 2023-05-15
 **/

@Api(tags = "流动性风险-数据设置-接口")
public interface CapitalFlowSettingAPI {

    @ApiOperation("现金流数据设置")
    @PostMapping("/capital/flow/setting/edit")
    R<Void> setting(@RequestBody @Valid CapitalFlowSettingReq req);

    @ApiOperation("现金流数据设置详情")
    @PostMapping("/capital/flow/setting/detail")
    R<CapitalFlowSettingRsp> detail(@RequestBody @Valid CapitalFlowSettingDetailReq req);

}
