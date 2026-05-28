package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("上传文件-请求体")
public class FileUploadREQ {
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty("文件")
    private MultipartFile file;

    @NotNull(message = "模块主id不能为空")
    @ApiModelProperty("模块主id")
    private Long mainId;

    @NotBlank(message = "模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

    @NotNull(message = "文件类型不能为空")
    @ApiModelProperty("文件类型 MaterialsEnum")
    private String materialsType;

    @ApiModelProperty("文件子类型")
    private String materialsSubType;

    @ApiModelProperty(value = "是否添加水印 0 不添加， 1添加")
    private Integer needWatermark;

    @ApiModelProperty("批次版本号")
    private String batchNo;

    @ApiModelProperty("流程ID")
    private String processInstanceId;

    @ApiModelProperty("来源业务key")
    private String sourceBusinessKey;

    @ApiModelProperty("各模块扩展参数")
    private Map<String, Object> ext;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("创建人")
    private Long createdBy;

    @ApiModelProperty("上传地点")
    private String location;

    public FileUploadREQ(){}

    public FileUploadREQ(MultipartFile file, Long mainId, String moduleType, String materialsType, String materialsSubType, Integer needWatermark, String batchNo, String processInstanceId, String sourceBusinessKey, Map<String, Object> ext, Long userId, Long createdBy, String location) {
        this.file = file;
        this.mainId = mainId;
        this.moduleType = moduleType;
        this.materialsType = materialsType;
        this.materialsSubType = materialsSubType;
        this.needWatermark = needWatermark;
        this.batchNo = batchNo;
        this.processInstanceId = processInstanceId;
        this.sourceBusinessKey = sourceBusinessKey;
        this.ext = ext;
        this.userId = userId;
        this.createdBy = createdBy;
        this.location = location;
    }
}
