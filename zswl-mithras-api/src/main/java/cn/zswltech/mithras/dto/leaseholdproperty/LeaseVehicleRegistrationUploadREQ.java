package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Data
public class LeaseVehicleRegistrationUploadREQ {

    @ApiModelProperty(value = "租赁物id")
    @NotNull(message = "租赁物id不能为空")
    private Long leaseholdId;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "车证Id")
    private Long vehicleId;

    @ApiModelProperty(value = "文件列表")
    @NotEmpty(message = "文件列表不能为空")
    private List<MultipartFile> files;

    @ApiModelProperty(value = "车证变更记录文件id")
    private Long changeRecordId;
}
