package cn.zswltech.mithras.api.client;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionAddREQ;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionListREQ;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionListRSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
* @description 客户工商信息处理意见表
* @author vico
* @date 2023-09-11
*/
@Api(tags = "客户工商信息处理意见表-接口")
public interface ClientBusinessOpinionApi {

    @ApiOperation("新增客户工商信息处理意见表")
    @PostMapping("/client/business/opinion/add")
    R<Void> add(@RequestBody @Valid ClientBusinessOpinionAddREQ req);

    @ApiOperation("客户工商信息处理意见表列表")
    @PostMapping("/client/business/opinion/list")
    R<PageR<ClientBusinessOpinionListRSP>> list(@RequestBody @Valid ClientBusinessOpinionListREQ req);

}