package cn.zswltech.mithras.dto.stampduty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author luyujie
 * @date 2026/1/24
 * @description
 */
@Data
@ApiModel("印花税缴纳明细导入-请求体")
public class StampDutyImportREQ {
    @NotNull(message = "导入文件不能为空")
    @ApiModelProperty("印花税缴纳明细文件")
    private MultipartFile file;

}
