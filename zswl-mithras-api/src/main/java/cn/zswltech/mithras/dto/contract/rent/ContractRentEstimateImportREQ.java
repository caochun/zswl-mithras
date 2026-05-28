package cn.zswltech.mithras.dto.contract.rent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/15
 * @description
 */
@Data
@ApiModel("概算租金表数据导入-请求体")
public class ContractRentEstimateImportREQ {
    @NotNull(message = "导入文件不能为空")
    @ApiModelProperty("导入文件")
    private MultipartFile file;

    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @NotBlank(message = "计划起租日不能为空")
    @ApiModelProperty("计划起租日")
    private String planStartDate;

    @NotBlank(message = "导入场景不能为空")
    @ApiModelProperty("导入场景")
    private String scene;
}
