package cn.zswltech.mithras.associationreport.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/4/19
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("association_dictionary")
public class AssociationDictionary extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "category_code")
    private String categoryCode;

    @TableField(value = "category_name")
    private String categoryName;

    @TableField(value = "item_code")
    private String itemCode;

    @TableField(value = "item_name")
    private String itemName;
}
