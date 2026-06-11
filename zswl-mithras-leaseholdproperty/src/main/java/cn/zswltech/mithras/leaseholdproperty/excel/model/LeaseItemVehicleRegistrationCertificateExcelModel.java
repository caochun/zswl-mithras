package cn.zswltech.mithras.leaseholdproperty.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.*;

/**
 * @author yangxiong
 * @description 台账分页返回实体
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeaseItemVehicleRegistrationCertificateExcelModel extends ExcelModel{

    @SimpleExcelHeader(headerName = "文件名")
    private String fileName;

    @SimpleExcelHeader(headerName ="机动车登记证书编号")
    private String registrationPageNo;

    @SimpleExcelHeader(headerName = "机动车所有人")
    private String vehicleRegistrationOwner;

    @SimpleExcelHeader(headerName = "机动车登记编号")
    private String vehicleRegistrationNumber;

    @SimpleExcelHeader(headerName = "制造厂名称")
    private String vehicleManufacturer;

    @SimpleExcelHeader(headerName = "车辆识别代号/车架号")
    private String vehicleVin;

    @SimpleExcelHeader(headerName = "变更记录")
    private String changeRecordStr;

    @SimpleExcelHeader(headerName = "图片张数")
    private Integer pictureCount;
}


