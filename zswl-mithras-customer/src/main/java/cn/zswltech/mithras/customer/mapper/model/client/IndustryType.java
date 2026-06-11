package cn.zswltech.mithras.customer.mapper.model.client;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author luyi
 */

@Data
@TableName("industry_type")
public class IndustryType {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("display")
    private String display;

    @TableField("parent_id")
    private Long parentId;

    @TableField("level")
    private Integer level;
}
