package cn.zswltech.mithras.basedata.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("address_dictionary")
public class AddressDictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 显示名称
     */
    @TableField("display")
    private String display;

    /**
     * 父级id
     */
    @TableField("parent_id")
    private Long parentId;

    @TableField("sort")
    private Integer sort;

    @TableField("level")
    private Integer level;

    @TableField("history")
    private Integer history;
}
