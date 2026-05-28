package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("金融局报送数据提交审批-请求体")
public class ReportApplySubmitREQ implements Serializable {

    @Valid
    @NotEmpty(message = "您还未选择要提交的报表")
    @ApiModelProperty("报表实例唯一标识（数组）")
    private List<String> reportInstanceIdList;

}
