package cn.zswltech.mithras.dto.capital;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/5/21/10:24
 * @description 联合查询直融、直融还款计划、间融、间融还款计划
 */
@Data
public class UnionSelectAllPlanDTO {

    @ApiModelProperty(value = "idKey")
    private String idKey;

    @ApiModelProperty(value = "核销状态")
    private String writeOffStatus;

    @ApiModelProperty(value = "应收/付款类型")
    private String type;

    @ApiModelProperty(value = "现金流项目")
    private String cashFlowItem;

    @ApiModelProperty(value = "金额")
    private Long amount;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "流水ID")
    private String serialNo;

    @ApiModelProperty(value = "融资渠道")
    private String financingRoute;

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

}
