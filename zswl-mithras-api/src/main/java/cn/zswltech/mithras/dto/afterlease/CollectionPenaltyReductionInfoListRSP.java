package cn.zswltech.mithras.dto.afterlease;
import cn.zswltech.mithras.dto.file.FileListRSP;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@Data
@ApiModel("租后-罚息减免基本表列表-返回体")
public class CollectionPenaltyReductionInfoListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 罚息减免金额
    */
    @ApiModelProperty(value = "罚息减免金额")
    private Long penaltyInterestDeductionAmount;

    /**
    * 原因简述
    */
    @ApiModelProperty(value = "原因简述")
    private String reasonExplain;

    /**
    * 流程状态
    */
    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    /**
    * 罚息减免状态
    */
    @ApiModelProperty(value = "罚息减免状态 RecordStatus")
    private String collectionStatus;

    @ApiModelProperty(value = "发起时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "发起人")
    private Long createBy;

    @ApiModelProperty(value = "发起人姓名")
    private String createByName;

    @ApiModelProperty(value = "通过时间")
    private LocalDateTime processTime;

    private List<FileListRSP> fileList;

}
