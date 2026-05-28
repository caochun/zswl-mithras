package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@Data
@ApiModel("租后-罚息减免基本表列表-请求体")
public class CollectionPenaltyReductionModifyREQ {

    /**
     * 合同id
     */
    @NotNull(message = "罚息减免ID")
    @ApiModelProperty(value = "罚息减免id")
    private Long id;

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

    //文件列表
    @ApiModelProperty(value = "罚息减免新增材料")
    private List<MultipartFile> files;

    @ApiModelProperty(value = "删除文件列表")
    private List<Long> removeFileIds;

}
