package cn.zswltech.mithras.service.mapper.model.leaseholdproperty;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author yupengfei
 * @date 2024/6/24 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "lease_item_vehicle_license_change_record")
public class LeaseItemVehicleLicenseChangeRecord extends BaseModelWithLogicDelete {

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 租赁物审核id
     */
    @TableField("lease_item_info_id")
    private Long leaseItemInfoId;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private Long fileId;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 机动车登记证书编号
     */
    @TableField("mortgage_no")
    private String mortgageNo;

    /**
     * 姓名/名称
     */
    @TableField("name")
    private String name;

    /**
     * 身份证明名称/号码
     */
    @TableField("number")
    private String number;

    /**
     * 获得方式
     */
    @TableField("method_of_obtaining")
    private String methodOfObtaining;

    /**
     * 转移登记日期
     */
    @TableField("record_date")
    private String recordDate;

    public LeaseItemVehicleLicenseChangeRecord(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        super.setCreateTime(now);
        super.setUpdateTime(now);
        super.setUpdateBy(userId);
        super.setCreateBy(userId);
    }
}
