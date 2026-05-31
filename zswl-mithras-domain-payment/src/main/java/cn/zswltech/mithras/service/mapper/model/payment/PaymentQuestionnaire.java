package cn.zswltech.mithras.service.mapper.model.payment;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description payment_questionnaire
 * @author zhaozhengkang
 * @date 2022-08-15
 */
@Data
public class PaymentQuestionnaire extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 序号
    */
    @TableField("seq_code")
    private String seqCode;

    /**
    * 问题分类
    */
    @TableField("question_type")
    private String questionType;

    /**
    * 问题
    */
    @TableField("question")
    private String question;

    /**
    * 版本
    */
    @TableField("version")
    private String version;

}
