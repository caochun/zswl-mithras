package cn.zswltech.mithras.service.providence.entity;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/31 14:59
 */
@Data
@TableName("public_outer_info_record")
public class OuterInfoRecord extends BaseModel {

    @TableId
    private Long id;

    @TableField("public_info_query_id")
    private Long publicInfoQueryId;

    @TableField("config_key")
    private String configKey;

    @TableField("`index`")
    private Integer index;

    @TableField("query_result")
    private String queryResult;

    @TableField("version")
    private Integer version;
}
