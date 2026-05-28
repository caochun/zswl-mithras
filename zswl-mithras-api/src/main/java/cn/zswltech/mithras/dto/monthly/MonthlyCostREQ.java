package cn.zswltech.mithras.dto.monthly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("成本计提-列表-参数")
public class MonthlyCostREQ extends PageReq {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    private Integer isConfirmed;

    private String batchNumber;

    private boolean interestPay;

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资类型 DK, ZR")
    private String financingType;

//    @ApiModelProperty("更新开始时间")
//    private  LocalDate startDate;
//
//    @ApiModelProperty("更新结束时间")
//    private  LocalDate endDate;

}
