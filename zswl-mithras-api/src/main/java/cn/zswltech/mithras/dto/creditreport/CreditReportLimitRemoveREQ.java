package cn.zswltech.mithras.dto.creditreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 征信报告-信用额度表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-信用额度表删除-请求体")
public class CreditReportLimitRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
