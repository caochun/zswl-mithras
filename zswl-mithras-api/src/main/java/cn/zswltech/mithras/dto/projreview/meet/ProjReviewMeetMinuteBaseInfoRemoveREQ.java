package cn.zswltech.mithras.dto.projreview.meet;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
@ApiModel("项目评审会议纪要表删除-请求体")
public class ProjReviewMeetMinuteBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
