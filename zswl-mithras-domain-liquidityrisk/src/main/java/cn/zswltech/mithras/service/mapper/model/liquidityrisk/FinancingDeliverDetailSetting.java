package cn.zswltech.mithras.service.mapper.model.liquidityrisk;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @create: 2023-05-16
 * 融资投放明细设置
 * financing_deliver_detail_setting
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "financing_deliver_detail_setting")
@Data
public class FinancingDeliverDetailSetting extends BaseModel implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 融资投放数据设置id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *融资/投放金额
     **/
    @TableField("amount")
    private Long amount;

    /**
     *融资/投放日期
     **/
    @TableField("date")
    private LocalDate date;

    /**
     *0 融资/ 1 投放
     **/
    @TableField("type")
    private Integer type;

    /**
     *备注
     **/
    @TableField("remark")
    private String remark;
}
