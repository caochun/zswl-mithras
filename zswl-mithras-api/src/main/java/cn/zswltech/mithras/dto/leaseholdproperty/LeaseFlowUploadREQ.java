package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LeaseFlowUploadREQ {

    @ApiModelProperty(value = "是否添加水印 0 不添加， 1添加")
    @NotNull(message = "是否添加水印不能为空")
    private Integer needWatermark;

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseholdId;

    @NotNull(message = "文件类型不能为空")
    @ApiModelProperty("文件类LeaseFileTypeEnums")
    private String materialsType;

    @ApiModelProperty("文件子类型")
    private String materialsSubType;

    @ApiModelProperty(value = "文件列表")
    @NotNull(message = "文件列表不能为空")
    private List<MultipartFile> files;

}
