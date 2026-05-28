package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @description 金融协会报送数据-修改-高管信息
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
@ApiModel("金融协会报送数据-修改-高管信息编辑-请求体")
public class AssociationSeniorExecutiveInfoModifyListREQ {

    /**
    * 报表实例唯一标识 | uuid格式
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    @NotBlank(message = "报表实例唯一标识不能为空")
    private String reportInstanceId;

    List<AssociationSeniorExecutiveInfoModifyREQ> dataList;


}
