package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author lllin
 * @date 2026/1/12
 */
@Data
public class FundFinancingBatchDownloadREQ{
    @NotNull
    @ApiModelProperty("申请id")
    private Long id;

    @NotNull
    @ApiModelProperty("模块code")
    private String moduleCode;

    @ApiModelProperty("文件id")
    private List<Long> fileIds;
}
