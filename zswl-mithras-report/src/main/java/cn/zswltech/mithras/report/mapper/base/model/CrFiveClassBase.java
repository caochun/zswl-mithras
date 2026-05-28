package cn.zswltech.mithras.report.mapper.base.model;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-五级分类表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrFiveClassBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 付款申请编号
    */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
    * 五级分类
    */
    @TableField(value = "five_class", updateStrategy = FieldStrategy.IGNORED)
    private String fiveClass;

    /**
    * 认定日期
    */
    @TableField(value = "identification_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime identificationDate;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    public String genBusinessKey() {
        return IdUtil.getSnowflakeNextIdStr();
    }

}
