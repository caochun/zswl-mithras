package cn.zswltech.mithras.service.mapper.model;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 契约锁调用日志记录表
 *
 * @author bigbear
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qiyuesuo_invoke_log")
public class QiyuesuoInvokeLog extends BaseModelWithLogicDelete {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("method")
    private String method;

    @TableField("param")
    private String param;

    @TableField("result")
    private String result;

}
