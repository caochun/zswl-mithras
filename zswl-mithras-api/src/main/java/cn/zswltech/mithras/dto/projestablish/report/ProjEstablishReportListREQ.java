package cn.zswltech.mithras.dto.projestablish.report;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 立项报告文件列表-请求体
 *
 * @author wangchuanhao
 * @date 2022/7/22 10:43 AM
 */
@Data
@ApiModel("立项报告文件列表-请求体")
public class ProjEstablishReportListREQ extends PageReq {

    @NotNull
    @ApiModelProperty("projEstablishId")
    private Long projEstablishId;

}
