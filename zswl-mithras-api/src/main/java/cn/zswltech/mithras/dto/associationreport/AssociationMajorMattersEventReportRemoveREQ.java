package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 重大事项报告表-重大事项报告情况
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
@ApiModel("重大事项报告表-重大事项报告情况删除-请求体")
public class AssociationMajorMattersEventReportRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
