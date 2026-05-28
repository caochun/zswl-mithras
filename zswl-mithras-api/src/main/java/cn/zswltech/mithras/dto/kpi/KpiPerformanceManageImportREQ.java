package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @date 2024/7/1/10:56
 * @description
 */
@Data
public class KpiPerformanceManageImportREQ {

    @ApiModelProperty(value = "导入的excel文件")
    @NotNull(message = "导入的excel文件不能为空")
    private MultipartFile file;

    @ApiModelProperty(value = "主表记录ID")
    @NotNull(message = "主表记录ID不能为空")
    private Long mainId;
}
