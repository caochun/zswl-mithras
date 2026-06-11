package cn.zswltech.mithras.payment.mapper.model;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import lombok.Data;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;

/**
 * @description payment_questionnaire_answer
 * @author zhaozhengkang
 * @date 2022-08-15
 */
@Data
public class PaymentQuestionnaireAnswer extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属付款id
    */
    @TableField("payment_id")
    private Long paymentId;

    /**
    * 问题id
    */
    @TableField("question_id")
    private Long questionId;

    /**
    * 问题答案
    */
    @TableField("question_answer")
    private String questionAnswer;

    /**
    * 备注
    */
    @TableField("remarks")
    private String remarks;

    @Override
    public void setMainId(Long paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public Long getMainId() {
        return this.paymentId;
    }
}
