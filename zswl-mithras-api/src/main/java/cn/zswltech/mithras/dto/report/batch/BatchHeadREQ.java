package cn.zswltech.mithras.dto.report.batch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yangxiong
 * @date 2024/4/9/18:50
 * @description
 */
@Data
public class BatchHeadREQ implements Serializable {
    private static final long serialVersionUID = -3507802010471550348L;

    @ApiModelProperty(value = "流程Id")
    @NotNull(message = "流程ID不能为空")
    private Long processInstanceId;
}
