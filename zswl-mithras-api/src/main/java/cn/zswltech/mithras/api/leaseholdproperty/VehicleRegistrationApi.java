package cn.zswltech.mithras.api.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Api(tags = "租赁物车证清单-接口")
@RequestMapping("lease/vehicle")
public interface VehicleRegistrationApi {

    @ApiOperation("车证上传/追加车证/重新上传")
    @PostMapping("upload")
    R<LeaseVehicleRegistrationUploadRSP> vehicleRegistrationUpload(@Valid LeaseVehicleRegistrationUploadREQ req);

    @ApiOperation("车证分页列表")
    @PostMapping("list")
    R<PageR<LeaseVehicleRegistrationListRSP>> queryVehicleRegistrationList(@RequestBody LeaseVehicleRegistrationQueryREQ req);

    @ApiOperation("车证批量删除")
    @PostMapping("delete")
    R<Void> remove(@Valid @RequestBody LeaseVehicleRegistrationRemoveREQ req);

    @ApiOperation("车证批量修改")
    @PostMapping("update")
    R<Void> update(@Valid @RequestBody LeaseVehicleRegistrationUpdateREQ req);

    @ApiOperation("车证锁定")
    @PostMapping("lock")
    R<Void> lock(@Valid @RequestBody LeaseVehicleRegistrationLockREQ req);

    @ApiOperation("车证解锁")
    @PostMapping("unlock")
    R<Void> unlock(@Valid @RequestBody LeaseVehicleRegistrationUnlockREQ req);

    @ApiOperation("统计各状态车证数量")
    @PostMapping("count")
    R<LeaseVehicleRegistrationCountRSP> count(@Valid @RequestBody LeaseItemIdREQ req);

    @ApiOperation(value = "车证下载")
    @PostMapping("exportExcel")
    R<Void> exportExcel(@RequestBody LeaseVehicleRegistrationQueryREQ req);


}