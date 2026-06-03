package cn.zswltech.mithras.customer.infohistory.infrastructure.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
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
 * @since 2022-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("info_history")
public class InfoHistory extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 变更编号
     */
    @TableField("history_code")
    private String historyCode;

    /**
     * 模块
     */
    @TableField("module_code")
    private String moduleCode;

    /**
     * 模块中对应表记录的id
     */
    @TableField("module_record_id")
    private Long moduleRecordId;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 变更前数据
     */
    @TableField("original_data")
    private String originalData;

    /**
     * 变更后数据
     */
    @TableField("current_data")
    private String currentData;

}
