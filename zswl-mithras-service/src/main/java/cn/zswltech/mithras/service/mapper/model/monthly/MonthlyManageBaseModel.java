package cn.zswltech.mithras.service.mapper.model.monthly;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author yangxiong
 * @date 2024/7/31/09:46
 * @description 月结管理基础model
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonthlyManageBaseModel extends BaseModelWithLogicDelete {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 是否已经更新
     */
    @TableField(value = "new_update")
    private Integer newUpdate;

    /**
     * 源数据ID
     */
    @TableField(value = "source_id")
    private Long sourceId;

    /**
     * 关联主表ID
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 是否推送，默认0未推送
     */
    @TableField(value = "is_send_cq")
    private Integer isSendCq;

    /**
     * 收入是否已确认
     */
    @TableField(value = "is_confirmed")
    private Integer isConfirmed;

    /**
     * 是否激活，默认1激活
     */
    @TableField(value = "is_effect")
    private Integer isEffect;

    /**
     * 批次号
     */
    @TableField(value = "batch_number")
    private String batchNumber;

}
