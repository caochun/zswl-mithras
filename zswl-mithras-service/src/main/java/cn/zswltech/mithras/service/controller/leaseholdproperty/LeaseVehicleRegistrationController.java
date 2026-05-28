package cn.zswltech.mithras.service.controller.leaseholdproperty;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.VehicleRegistrationApi;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.service.service.leaseholdproperty.LeaseVehicleRegistrationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@RestController
public class LeaseVehicleRegistrationController implements VehicleRegistrationApi {

    @Resource
    private LeaseVehicleRegistrationService leaseVehicleRegistrationService;

    /**
     * 车证上传
     */
    @Override
    public R<LeaseVehicleRegistrationUploadRSP> vehicleRegistrationUpload(LeaseVehicleRegistrationUploadREQ req) {
        return leaseVehicleRegistrationService.vehicleRegistrationUpload(req);
    }

    /**
     * 车证分页列表
     */
    @Override
    public R<PageR<LeaseVehicleRegistrationListRSP>> queryVehicleRegistrationList(LeaseVehicleRegistrationQueryREQ req) {
        return leaseVehicleRegistrationService.queryVehicleRegistrationList(req);
    }

    /**
     * 车证批量删除
     */
    @Override
    public R<Void> remove(LeaseVehicleRegistrationRemoveREQ req) {
        return leaseVehicleRegistrationService.remove(req);
    }

    /**
     * 车证批量修改
     */
    @Override
    public R<Void> update(LeaseVehicleRegistrationUpdateREQ req) {
        return leaseVehicleRegistrationService.update(req);
    }

    @Override
    public R<Void> lock(LeaseVehicleRegistrationLockREQ req) {
        return leaseVehicleRegistrationService.lock(req);
    }

    @Override
    public R<Void> unlock(LeaseVehicleRegistrationUnlockREQ req) {
        return leaseVehicleRegistrationService.unlock(req);
    }

    /**
     * 统计各状态车证数量
     */
    @Override
    public R<LeaseVehicleRegistrationCountRSP> count(LeaseItemIdREQ req) {
        return leaseVehicleRegistrationService.count(req);
    }

    /**
     * 车证下载
     */
    @Override
    public R<Void> exportExcel(LeaseVehicleRegistrationQueryREQ req) {
        return leaseVehicleRegistrationService.exportExcel(req);
    }
}
