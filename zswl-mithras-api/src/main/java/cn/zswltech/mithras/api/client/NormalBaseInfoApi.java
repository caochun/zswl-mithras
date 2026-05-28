package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoModifyREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author junke
 */
@Api(tags = "自然人基本信息-接口")
public interface NormalBaseInfoApi {

    @ApiOperation("自然人基本信息-创建")
    @PostMapping("/normal/base/info/add")
    R<Void> add(@RequestBody @Valid NormalBaseInfoAddREQ req);

    @ApiOperation("自然人基本信息-修改")
    @PostMapping("/normal/base/info/modify")
    R<Void> modify(@RequestBody @Valid NormalBaseInfoModifyREQ req);

//    R<Void> remove(@RequestBody @Valid NormalBaseInfoRemoveREQ req);

    @ApiOperation("自然人基本信息-详情")
    @PostMapping("/normal/base/info/detail")
    R<NormalBaseInfoDetailRSP> detail(@RequestBody @Valid NormalBaseInfoDetailREQ req);
}
