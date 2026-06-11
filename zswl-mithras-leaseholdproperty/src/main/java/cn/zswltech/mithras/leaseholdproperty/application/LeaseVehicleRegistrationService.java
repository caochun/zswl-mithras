package cn.zswltech.mithras.leaseholdproperty.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.leaseholdproperty.model.LeaseItemVehicleRegistrationCertificate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
public interface LeaseVehicleRegistrationService extends IService<LeaseItemVehicleRegistrationCertificate> {

    R<LeaseVehicleRegistrationUploadRSP> vehicleRegistrationUpload(LeaseVehicleRegistrationUploadREQ req);

    R<PageR<LeaseVehicleRegistrationListRSP>> queryVehicleRegistrationList(LeaseVehicleRegistrationQueryREQ req);

    R<Void> remove(LeaseVehicleRegistrationRemoveREQ req);

    R<Void> update(LeaseVehicleRegistrationUpdateREQ req);

    R<Void> lock(LeaseVehicleRegistrationLockREQ req);

    R<Void> unlock(LeaseVehicleRegistrationUnlockREQ req);

    R<LeaseVehicleRegistrationCountRSP> count(LeaseItemIdREQ req);

    R<Void> exportExcel(LeaseVehicleRegistrationQueryREQ req);

    List<String> getLockedPrefixFileName(Long leaseholdId, String operateType);
}
