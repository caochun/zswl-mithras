package cn.zswltech.mithras.dto.fund.financing.repay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Data
@ApiModel("融资管理-还款表导入-请求体")
public class FundFinancingRepayImportREQ {
    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

    @ApiModelProperty("文件")
    private MultipartFile file;

    @ApiModelProperty("导入场景")
    private String scene;

    @ApiModelProperty("是否校验利息差额")
    private Boolean isCheck;
}
