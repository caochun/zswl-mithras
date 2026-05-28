package cn.zswltech.mithras.dto.client.subjectitem;

import cn.zswltech.mithras.dto.AuthBaseReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@ApiModel("法人财务信息客户列表-请求体")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CorpSubjectItemRemoveREQ extends AuthBaseReq {
    @NotBlank
    @ApiModelProperty("sheet名称")
    private String subjectType;
    @NotBlank
    @ApiModelProperty("报告类型")
    private String reportType;
    @NotNull
    @ApiModelProperty("年份")
    private Integer year;
    @NotNull
    @ApiModelProperty("报告期")
    private Integer quarter;
    @NotNull
    private Long clientId;
}
