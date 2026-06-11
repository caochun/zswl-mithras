package cn.zswltech.mithras.collection.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

/**
 * 逾期表
 * @TableName collection_overdue_record_info
 */
@TableName(value ="collection_overdue_record_info")
@Data
public class CollectionOverdueRecordInfo extends BaseModel implements Serializable {
    /**
     * 罚息记录明细id

     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收款id
     */
    private Long collectionId;

    /**
     * 逾期金额
     */
    private Long overdueAmount;

    /**
     * 单日产生罚息
     */
    private Long dayPenaltyInterest;

    /**
     * 罚息余额
     */
    private Long lastPenaltyInterest;

    /**
     *  记录日期
     */
    private LocalDate recordDate;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
