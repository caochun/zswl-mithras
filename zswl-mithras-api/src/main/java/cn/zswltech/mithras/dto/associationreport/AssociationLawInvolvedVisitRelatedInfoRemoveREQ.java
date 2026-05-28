package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 涉法涉讼涉访信息表
 * @author hspcadmin
 * @date 2025-08-27
 */
@Data
@ApiModel("涉法涉讼涉访信息表删除-请求体")
public class AssociationLawInvolvedVisitRelatedInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
