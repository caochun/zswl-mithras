package cn.zswltech.mithras.leaseholdproperty.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 租赁物车证信息
 *
 * @author yupengfei
 * @date 2024/5/8 16:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "lease_item_vehicle_registration_certificate")
public class LeaseItemVehicleRegistrationCertificate extends BaseModelWithLogicDelete {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 租赁物审核id
     */
    @TableField("lease_item_info_id")
    private Long leaseItemInfoId;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 机动车所有人
     */
    @TableField("vehicle_registration_owner")
    private String vehicleRegistrationOwner;

    /**
     * 机动车登记编号
     */
    @TableField("vehicle_registration_number")
    private String vehicleRegistrationNumber;

    /**
     * 车辆识别代号/车架号
     */
    @TableField("vehicle_vin")
    private String vehicleVin;

    /**
     * 制造厂名称
     */
    @TableField("vehicle_manufacturer")
    private String vehicleManufacturer;

    /**
     * 锁定内容不支持修改
     */
    @TableField("locked")
    private Boolean locked;

    /**
     * 识别状态
     */
    @TableField("status")
    private String status;

    /**
     * 操作
     */
    @TableField("operation")
    private String operation;

    /**
     * 机动车登记证书编号
     */
    @TableField("registration_page_no")
    private String registrationPageNo;

    /**
     * 变更记录
     */
    @TableField("change_record")
    private String changeRecord;

    /**
     * 是否存在首页
     */
    @TableField("is_present_home_page")
    private Boolean isPresentHomePage;

    /**
     * 图片张数
     */
    @TableField("picture_count")
    private Integer pictureCount;

    public LeaseItemVehicleRegistrationCertificate(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        super.setCreateTime(now);
        super.setUpdateTime(now);
        super.setUpdateBy(userId);
        super.setCreateBy(userId);
    }
}
