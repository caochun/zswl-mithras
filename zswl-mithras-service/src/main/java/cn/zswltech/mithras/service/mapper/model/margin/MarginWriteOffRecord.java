package cn.zswltech.mithras.service.mapper.model.margin;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 核销记录表
 * @TableName margin_write_off_record
 */
@TableName(value ="margin_write_off_record")
@Data
public class MarginWriteOffRecord extends BaseModel implements Serializable {
    /**
     * 核销记录id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 归属id
     */
    private Long marginId;

    /**
     * 收款记录id
     */
    private Long recordId;

    /**
     * 操作
     */
    private String operate;

    /**
     * 被操作明细
     */
    private String operateInfo;

    /**
     * 保证金金额
     */
    private Long marginAmount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}