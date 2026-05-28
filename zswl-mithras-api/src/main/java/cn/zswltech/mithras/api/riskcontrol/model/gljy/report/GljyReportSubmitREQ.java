package cn.zswltech.mithras.api.riskcontrol.model.gljy.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author yibin
 */
@Data
@ApiModel("关联交易提交-请求体")
public class GljyReportSubmitREQ {
    @NotEmpty
    @ApiModelProperty("提交报送记录id列表")
    private List<Long> idList;
}
