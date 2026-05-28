package cn.zswltech.mithras.dto.projestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 立项报告文件生成报告-请求体
 *
 * @author wangchuanhao
 * @date 2022/7/22 1:31 PM
 */
@Data
@ApiModel("立项报告文件生成报告-请求体")
public class ProjEstablishReportGenerateREQ {

    @NotNull
    @ApiModelProperty("projEstablishId")
    private Long projEstablishId;

}
