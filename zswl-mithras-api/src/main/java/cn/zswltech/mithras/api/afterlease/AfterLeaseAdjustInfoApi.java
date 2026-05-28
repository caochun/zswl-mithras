package cn.zswltech.mithras.api.afterlease;
import cn.zswltech.mithras.dto.afterlease.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;

/**
* @description 租后调整信息表
* @author vico
* @date 2022-11-08
*/
@Api(tags = "租后调整信息表-接口")
public interface AfterLeaseAdjustInfoApi {

    @ApiOperation("新增租后调整信息表")
    @PostMapping("/after/lease/adjust/info/add")
    R<AfterLeaseAdjustInfoAddRSP> add(@RequestBody @Valid AfterLeaseAdjustInfoAddREQ req);

    @ApiOperation("修改租后调整信息表")
    @PostMapping("/after/lease/adjust/info/modify")
    R<Void> modify(@RequestBody @Valid AfterLeaseAdjustInfoModifyREQ req);

    @ApiOperation("租后调整信息表列表")
    @PostMapping("/after/lease/adjust/info/list")
    R<PageR<AfterLeaseAdjustInfoListRSP>> list(@RequestBody @Valid AfterLeaseAdjustInfoListREQ req);

    @ApiOperation("租后调整信息表详情")
    @PostMapping("/after/lease/adjust/info/detail")
    R<AfterLeaseAdjustDetailRSP> detail(@RequestBody @Valid AfterLeaseAdjustDetailREQ req);


}