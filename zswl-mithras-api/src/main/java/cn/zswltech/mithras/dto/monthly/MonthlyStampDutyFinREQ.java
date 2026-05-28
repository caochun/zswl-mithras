package cn.zswltech.mithras.dto.monthly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MonthlyStampDutyFinREQ extends PageReq {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;

    private Integer isConfirmed;

    private String batchNumber;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty("融资编号")
    private String financingCode;

    public MonthlyStampDutyFinREQ(String yearAndMonth,Boolean isList) {
        this.setPage(1);
        this.setPageSize(10000);
        this.yearAndMonth = yearAndMonth;
    }
}
