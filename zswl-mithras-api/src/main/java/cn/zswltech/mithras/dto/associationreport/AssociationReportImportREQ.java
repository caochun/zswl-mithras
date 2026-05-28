package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@Data
public class AssociationReportImportREQ {
    @ApiModelProperty("报表实例id")
    @NotBlank(message = "报表实例id不能为空")
    private String reportInstanceId;

    @ApiModelProperty("导入文件")
    private MultipartFile file;
}
