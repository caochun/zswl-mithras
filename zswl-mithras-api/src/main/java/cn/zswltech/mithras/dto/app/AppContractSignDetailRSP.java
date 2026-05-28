package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author junke
 */
@ApiModel("融租易合同信息-返回体")
@Data
public class AppContractSignDetailRSP {

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "签约人id")
    private Long userId;

    @ApiModelProperty(value = "签约人名字")
    private String userName;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "资料类型")
    private String materialsType;

    @ApiModelProperty("资料类型名称")
    private String materialsTypeName;

    @ApiModelProperty("资料子类型")
    private String materialSubType;

    @ApiModelProperty("资料子类型名称")
    private String materialSubTypeName;

    @ApiModelProperty("签约文件id")
    private List<Long> fileIds;

    @ApiModelProperty("签约文件名字")
    private List<String> fileNames;

    @ApiModelProperty("签约场地照片id")
    private List<Long> filePhotoIds;

    @ApiModelProperty("签约视频id")
    private List<Long> fileVideoIds;

    @ApiModelProperty("人机合照id")
    private List<Long> humanMachineIds;

    @ApiModelProperty("设备照片id")
    private List<Long> deviceIds;

    @ApiModelProperty("租赁物类型")
    private List<String> leaseItemTypes;

    @ApiModelProperty("租赁类型")
    private String leaseType;
}
