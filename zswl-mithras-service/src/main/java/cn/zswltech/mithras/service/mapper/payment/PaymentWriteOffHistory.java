package cn.zswltech.mithras.service.mapper.payment;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.common.model.BaseModel;

/**
 * @description payment_write_off_history
 * @author zhaozhengkang
 * @date 2022-08-19
 */
@Data
public class PaymentWriteOffHistory extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属付款申请id
    */
    @TableField("paymentid")
    private Long paymentid;

    /**
    * 操作时间
    */
    @TableField("operate_time")
    private LocalDateTime operateTime;

    /**
    * 操作人id
    */
    @TableField("operate_person_id")
    private Long operatePersonId;

    /**
    * 操作人姓名
    */
    @TableField("operate_person_name")
    private String operatePersonName;

    /**
    * 操作数据类型 （记录还是申请）
    */
    @TableField("operate_data_type")
    private String operateDataType;

    /**
    * 被操作数据序号（记录是递增的数字，申请是paymentcode）
    */
    @TableField("operate_data_code")
    private String operateDataCode;

    /**
    * 操作
    */
    @TableField("operation")
    private String operation;

    /**
    * 单据状态
    */
    @TableField("data_status")
    private String dataStatus;

}
