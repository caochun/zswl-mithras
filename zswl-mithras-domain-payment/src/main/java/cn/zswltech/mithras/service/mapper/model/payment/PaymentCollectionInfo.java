package cn.zswltech.mithras.service.mapper.model.payment;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PaymentCollectionInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

//    @TableField("process_instance_id")
//    private Long processInstanceId;


    @TableField("payment_id")
    private Long paymentId;

    /**
     * 首期租金
     */
    @TableField(value = "down_payment")
    private long downPayment;


    /**
     * 保证金
     */
    @TableField(value = "earnest_money")
    private long earnestMoney;

    /**
     * 质保金
     **/
    @TableField(value = "retention_money")
    private long retentionMoney;

    /**
     * 服务费/咨询费
     */
    @TableField(value = "consulting_fee")
    private long consultingFee;

    /**
     * 手续费(元)
     */
    @TableField(value = "commission")
    private long commission;

    /**
     * 首期利息(元)
     */
    @TableField(value = "first_installment_interest")
    private long firstInstallmentInterest;

   /**
    *  质保金收款方式
    */
   @TableField(value = "warranty_pay_way")
    private Integer warrantyPayWay;

    /**
     * 厂商质保金退还日期
     */
    @TableField(value = "warranty_return_date")
    private LocalDate warrantyReturnDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public Long getMainId() {
        return getId();
    }

    @Override
    public void setMainId(Long id) {
        setId(id);
    }
}
