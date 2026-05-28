package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 金融局报表数据权限
 * @author hspcadmin
 * @date 2025-09-25
 */
@Data
@ApiModel("金融局报表数据权限删除-请求体")
public class AssociationReportDataAccessRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
