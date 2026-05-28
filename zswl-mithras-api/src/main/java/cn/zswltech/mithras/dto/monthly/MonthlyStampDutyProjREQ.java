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
public class MonthlyStampDutyProjREQ extends PageReq {

    @NotBlank(message = "月份不得为空")
    @ApiModelProperty(value = "处理月份 yyyy-MM")
    private String yearAndMonth;

    private Integer isConfirmed;

    private String batchNumber;

    @ApiModelProperty(value = "客户ID")
    private Long clientId;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;


    public MonthlyStampDutyProjREQ(String yearAndMonth,int isList) {
        this.setPage(1);
        this.setPageSize(10000);
        this.yearAndMonth = yearAndMonth;
    }
}
