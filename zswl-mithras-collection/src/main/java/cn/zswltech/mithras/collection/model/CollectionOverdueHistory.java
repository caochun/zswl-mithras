package cn.zswltech.mithras.collection.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 逾期历史表
 * @author vico
 * @date 2023-05-31
 */
@Data
public class CollectionOverdueHistory extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 合同编号
    */
    @TableField("contract_code")
    private String contractCode;

    /**
    * 借据id
    */
    @TableField("receipt_id")
    private Long receiptId;

    /**
    * 收款明细id
    */
    @TableField("collection_id")
    private Long collectionId;

    /**
    * 收款编号
    */
    @TableField("collection_code")
    private String collectionCode;

    /**
    * 期项
    */
    @TableField("phase")
    private Integer phase;

    /**
    * 逾期金额
    */
    @TableField("overdue_amount")
    private Long overdueAmount;

    /**
     * 逾期天数
     */
    @TableField("overdue_days")
    private Long overdueDays;

    /**
     * 批次号，依次递增
     */
    @TableField("batch_number")
    private Long batchNumber;

    @TableField("client_id")
    private Long clientId;

}
