package cn.zswltech.mithras.dto.projpricing;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/3/10
 * @description
 */
@Data
public class CorpSubjectItemCheckReasonREQ {
    @NotNull(message = "项目评审id不能为空")
    @ApiModelProperty("项目评审id")
    private Long projReviewId;

    @ApiModelProperty("财报不完整理由")
    private List<Data> reasonList;

    @lombok.Data
    public static class Data {
        @ApiModelProperty("客户id")
        private Long clientId;

        @ApiModelProperty("财报不完整理由")
        private String reason;
    }
}
