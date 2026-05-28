package cn.zswltech.mithras.service.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author luyi
 */
@Data
@TableName("general_dictionary")
public class GeneralDictionary {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("dict_key")
    private String dictKey;
    @TableField("dict_desc")
    private String dictDesc;
    @TableField("code")
    private String code;
    @TableField("display")
    private String display;
    @TableField("sort")
    private Integer sort;

}
