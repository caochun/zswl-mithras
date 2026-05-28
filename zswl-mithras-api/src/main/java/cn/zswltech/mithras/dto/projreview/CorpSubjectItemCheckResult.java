package cn.zswltech.mithras.dto.projreview;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/3/10
 * @description
 */
@Data
public class CorpSubjectItemCheckResult {
    @ApiModelProperty("校验结果，0-不通过，1-通过")
    private Integer checkResult;

    @ApiModelProperty("校验结果信息")
    private String checkResultMsg;
}
