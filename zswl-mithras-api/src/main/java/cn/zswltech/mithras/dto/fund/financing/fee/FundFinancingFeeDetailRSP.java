package cn.zswltech.mithras.dto.fund.financing.fee;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/18 14:08
 */
@Data
public class FundFinancingFeeDetailRSP extends ListBaseRSP {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机构id")
    private Long organizationId;

    @ApiModelProperty(value = "机构名称")
    private String organizationName;

    /**
     * 费用类型
     */
    @ApiModelProperty(value = "费用类型")
    private String expenseType;

    /**
     * 金额（万元）
     */
    @ApiModelProperty(value = "金额（万元）")
    private Long amount;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式")
    private String paymentMethod;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    private LocalDate payDate;

    /**
     * 核销状态
     */
    @TableField("核销状态")
    private String writeOffStatus;
}
