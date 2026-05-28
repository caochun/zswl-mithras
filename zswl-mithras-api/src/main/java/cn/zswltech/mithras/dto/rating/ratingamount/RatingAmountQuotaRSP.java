package cn.zswltech.mithras.dto.rating.ratingamount;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountQuotaRSP {

    @ApiModelProperty(value = "债项评级模型类型")
    private String modelType;

    @ApiModelProperty(value = "客户限额")
    private String clientQuota;

    @ApiModelProperty(value = "项目限额")
    private String projQuota;

    @ApiModelProperty(value = "集团限额")
    private String groupQuota;

    @ApiModelProperty(value = "集团剩余可用额度")
    private String groupSurplusQuota;

    @ApiModelProperty(value = "评估主体限额")
    private String evaluationSubjectQuota;

    @ApiModelProperty(value = "评估主体评级调整系数")
    private String ratingAdjustFactor;

    @ApiModelProperty(value = "增信措施调整价值")
    private String creditMeasurePrice;

    @ApiModelProperty(value = "租赁物价值")
    private String leaseItemPrice;
}
