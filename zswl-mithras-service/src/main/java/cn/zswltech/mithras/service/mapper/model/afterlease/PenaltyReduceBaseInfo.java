package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.enums.fund.receiptrepay.ProcessState;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 罚息减免基本表
 * @author vico
 * @date 2024-08-21
 */
@Data
public class PenaltyReduceBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 收款明细id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 备注
    */
    @TableField("notes")
    private String notes;

    /**
     * {@link ProcessState#name()}
     **/
    @TableField("penalty_reduce_status")
    private String penaltyReduceStatus;

}
