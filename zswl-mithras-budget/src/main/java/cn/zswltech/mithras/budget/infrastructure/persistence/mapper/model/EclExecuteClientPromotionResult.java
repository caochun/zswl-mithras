package cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 资产减值客户是否上迁记录表
 * @author vico
 * @date 2025-12-15
 */
@Data
public class EclExecuteClientPromotionResult extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 客户id
    */
   /* @TableField("client_id")
    private Long clientId;*/

    /**
     * 合同ID
     **/
    @TableField("contract_id")
    private Long contractId;

    /**
    * 间隔
    */
    @TableField("interval_month")
    private Integer intervalMonth;

    /**
    * 结果0不满足，1满足
    */
    @TableField("conclusion")
    private Integer conclusion;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @TableField("deleted")
    private Integer deleted;

}
