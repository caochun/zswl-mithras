package cn.zswltech.mithras.dashboard.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 观远数据集信息
 * @author yangxiong
 * @TableName guanyuan_ds_info
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="guanyuan_ds_info")
public class GuanyuanDsInfo extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务key
     */
    @TableField(value = "business_key")
    private String businessKey;

    /**
     * 观远数据集id
     */
    @TableField(value = "guanyuan_ds_id")
    private String guanyuanDsId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}