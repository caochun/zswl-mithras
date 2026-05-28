package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
@Data
public class LeaseItemImportREQ {
    @ApiModelProperty("租赁物管理id")
    private Long id;

    @ApiModelProperty("文件")
    private MultipartFile file;
}
