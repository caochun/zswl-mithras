package cn.zswltech.mithras.service.mapper.model;
import cn.zswltech.mithras.common.model.BaseModel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/2/28
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("system_database_record")
public class SystemDatabaseRecord extends BaseModel {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "operation")
    private String operation;

    @TableField(value = "operator_id")
    private Long operatorId;

    @TableField(value = "batch_sequence")
    private String batchSequence;

    @TableField(value = "origin_sql")
    private String originSql;

    @TableField(value = "origin_parameter")
    private String originParameter;

    @TableField(value = "table_name")
    private String tableName;

    @TableField(value = "row_data")
    private String rowData;
}
