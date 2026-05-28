package cn.zswltech.mithras.api.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyAddREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyModifyREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRSP;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRemoveREQ;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * 环保处罚
 *
 * @author wangchuanhao
 * @date 2022/6/23 4:01 PM
 */
@Api(tags = "外部信息-环保处罚-接口")
@RequestMapping("/environment/penalty")
public interface EnvironmentPenaltyApi {

    @ApiOperation("新增环保处罚信息")
    @PostMapping("/add")
    R<Void> add(@RequestBody @Valid EnvironmentPenaltyAddREQ req);

    @ApiOperation("修改环保处罚信息")
    @PostMapping("/modify")
    R<Void> modify(@RequestBody @Valid EnvironmentPenaltyModifyREQ req);

    @ApiOperation(("环保处罚信息列表"))
    @PostMapping("/list")
    R<PageR<EnvironmentPenaltyRSP>> list(@RequestBody @Valid ExternalPageREQ req);

    @ApiOperation("删除环保处罚信息")
    @PostMapping("/remove")
    R<Void> remove(@RequestBody @Valid EnvironmentPenaltyRemoveREQ req);

}
