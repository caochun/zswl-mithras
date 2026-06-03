package cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("lease_item_list_row_data")
public class LeaseItemListRowData extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("lease_item_info_id")
    private Long leaseItemInfoId;

    @TableField("row_data")
    private String rowData;

    @TableField("match_columns")
    private String matchColumns;
}
