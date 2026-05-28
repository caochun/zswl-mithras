package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 金融局报表申请表
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融局报表申请表删除-请求体")
public class AssociationReportApplyRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
