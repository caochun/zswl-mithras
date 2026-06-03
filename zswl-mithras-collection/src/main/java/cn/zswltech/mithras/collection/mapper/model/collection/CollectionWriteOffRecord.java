package cn.zswltech.mithras.collection.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 核销记录表
 * @TableName collection_write_off_record
 */
@TableName(value ="collection_write_off_record")
@Data
public class CollectionWriteOffRecord extends BaseModel implements Serializable {
    /**
     * 核销记录id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 归属id
     */
    private Long collectionId;

    /**
     * 收款记录id
     */
    private Long recordId;

    /**
     * 操作
     */
    private String operate;

    /**
     * 单据状态
     */
    private String receiptStatus;


    /**
     * 被操作明细
     */
    private String operateInfo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}