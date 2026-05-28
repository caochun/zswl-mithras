package cn.zswltech.mithras.api.client.lifecycle;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.client.lifecycle.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:25
 */
@Api(value = "客户全周期接口", tags = "客户全周期接口")
public interface ClientLifeCycleApi {

    @ApiOperation("客户全周期卡片")
    @PostMapping("/client/lifecycle/card")
    R<ClientLifeCycleCardRsp> card();

    @ApiOperation("客户列表")
    @PostMapping("/client/lifecycle/clientlist")
    R<PageR<ClientListRSP>> clientList(@RequestBody @Valid ClientLifeCycleListReq req);

    @ApiOperation("客户详情")
    @PostMapping("/client/lifecycle/clientdetail")
    R<ClientListRSP> clientDetail(@RequestBody @Valid ClientLifeCycleDetailReq req);

    @ApiOperation("客户项目列表")
    @PostMapping("/client/lifecycle/projectlist")
    R<List<ClientLifeCycleProjectListRsp>> projectList(@RequestBody @Valid ClientLifeCycleDetailReq req);

    @ApiOperation("查询五级分类")
    @PostMapping("/client/lifecycle/fivelevel")
    R<String> fiveLevel(@RequestBody @Valid ClientLifeCycleDetailReq req);

    @ApiOperation("借据详情")
    @PostMapping("/client/lifecycle/receipt")
    R<ClientLifeCycleReceiptRsp> receipt(@RequestBody @Valid ClientLifeCycleDetailReq req);
}
