package cn.zswltech.mithras.leaseholdproperty.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.leaseholdproperty.VehicleRegistrationApi;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseItemIdREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationCountRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationListRSP;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationLockREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationQueryREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationRemoveREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationUnlockREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationUpdateREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationUploadREQ;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseVehicleRegistrationUploadRSP;
import cn.zswltech.mithras.leaseholdproperty.application.VehicleRegistrationApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LeaseVehicleRegistrationController implements VehicleRegistrationApi {
    @Resource
    private VehicleRegistrationApplicationService vehicleRegistrationApplicationService;

    @Override
    public R<LeaseVehicleRegistrationUploadRSP> vehicleRegistrationUpload(LeaseVehicleRegistrationUploadREQ req) {
        return vehicleRegistrationApplicationService.vehicleRegistrationUpload(req);
    }

    @Override
    public R<PageR<LeaseVehicleRegistrationListRSP>> queryVehicleRegistrationList(LeaseVehicleRegistrationQueryREQ req) {
        return vehicleRegistrationApplicationService.queryVehicleRegistrationList(req);
    }

    @Override
    public R<Void> remove(LeaseVehicleRegistrationRemoveREQ req) {
        return vehicleRegistrationApplicationService.remove(req);
    }

    @Override
    public R<Void> update(LeaseVehicleRegistrationUpdateREQ req) {
        return vehicleRegistrationApplicationService.update(req);
    }

    @Override
    public R<Void> lock(LeaseVehicleRegistrationLockREQ req) {
        return vehicleRegistrationApplicationService.lock(req);
    }

    @Override
    public R<Void> unlock(LeaseVehicleRegistrationUnlockREQ req) {
        return vehicleRegistrationApplicationService.unlock(req);
    }

    @Override
    public R<LeaseVehicleRegistrationCountRSP> count(LeaseItemIdREQ req) {
        return vehicleRegistrationApplicationService.count(req);
    }

    @Override
    public R<Void> exportExcel(LeaseVehicleRegistrationQueryREQ req) {
        return vehicleRegistrationApplicationService.exportExcel(req);
    }
}
