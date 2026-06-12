package cn.zswltech.mithras.customer.mobile.model;

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


/**
 * @author zhouning
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "app_contract_sign")
public class AppContractSign extends BaseModelWithLogicDelete {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;


    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件id
     */
    @TableField(value = "file_id")
    private Long fileId;
}
