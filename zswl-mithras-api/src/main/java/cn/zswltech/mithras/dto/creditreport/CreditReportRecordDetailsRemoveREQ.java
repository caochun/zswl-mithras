package cn.zswltech.mithras.dto.creditreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 征信报告-信贷记录明细表
 * @author vico
 * @date 2025-12-01
 */
@Data
@ApiModel("征信报告-信贷记录明细表删除-请求体")
public class CreditReportRecordDetailsRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
