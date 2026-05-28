package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @description 金融局报送-最大10家客户（含集团）集中度统计编辑
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
@ApiModel("金融局报送-最大10家客户（含集团）集中度统计编辑-请求体")
public class AssociationTop10ClientConcentrationModifyListREQ {

    /**
    * 报表实例唯一标识 | uuid格式-
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    @NotBlank(message = "报表实例唯一标识不能为空")
    private String reportInstanceId;

    List<AssociationTop10ClientConcentrationModifyREQ> dataList;


}
