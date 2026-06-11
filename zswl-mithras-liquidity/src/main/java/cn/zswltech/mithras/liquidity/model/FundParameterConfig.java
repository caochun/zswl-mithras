package cn.zswltech.mithras.liquidity.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 资金基础参数配置
 * </p>
 *
 * @author chenyifei
 * @since 2024-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fund_parameter_config")
public class FundParameterConfig extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 参数code
     */
    @TableField("config_code")
    private String configCode;

    /**
     * 参数描述
     */
    @TableField("config_desc")
    private String configDesc;

    /**
     * 参数值
     */
    @TableField("config_value")
    private String configValue;


}
