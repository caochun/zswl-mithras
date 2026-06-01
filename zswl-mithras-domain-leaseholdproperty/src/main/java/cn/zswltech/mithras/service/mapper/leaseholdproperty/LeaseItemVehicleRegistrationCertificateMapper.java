package cn.zswltech.mithras.service.mapper.leaseholdproperty;

import cn.zswltech.mithras.service.mapper.model.leaseholdproperty.LeaseItemVehicleRegistrationCertificate;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
public interface LeaseItemVehicleRegistrationCertificateMapper extends CustomBaseMapper<LeaseItemVehicleRegistrationCertificate> {
    int insertBatch(@Param("vehicles") List<LeaseItemVehicleRegistrationCertificate> vehicles);

}
