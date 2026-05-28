package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @description 重大事项报告情况编辑
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
@ApiModel("重大事项报告情况编辑-请求体")
public class AssociationMajorMattersEventReportModifyListREQ {

    /**
    * 报表实例唯一标识 | uuid格式-
    */
    @ApiModelProperty(value = "报表实例唯一标识 | uuid格式")
    @NotBlank(message = "报表实例唯一标识不能为空")
    private String reportInstanceId;

    List<AssociationMajorMattersEventReportModifyREQ> dataList;


}
