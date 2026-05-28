package cn.zswltech.mithras.dto.creditreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 征信报告-信息概要表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-信息概要表删除-请求体")
public class CreditReportSummaryRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
