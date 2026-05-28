package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @description 资产减值记录表
 * @author vico
 * @date 2025-09-28
 */
@Data
@ApiModel("资产减值记录表新增-请求体")
public class EclExecuteRecordImportREQ {

    /**
    * 调用记录编号
    */
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty(value = "上传文件")
    private MultipartFile file;


}
