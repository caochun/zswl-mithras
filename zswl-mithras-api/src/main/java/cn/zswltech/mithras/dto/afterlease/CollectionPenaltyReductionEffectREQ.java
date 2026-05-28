package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@Data
@ApiModel("租后-罚息减免基本表列表-请求体")
public class CollectionPenaltyReductionEffectREQ {

    /**
     * 合同id
     */
    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    /**
     * 罚息减免金额
     */
    @ApiModelProperty(value = "罚息减免金额")
    @NotNull(message = "罚息减免金额不能为空")
    private Long penaltyInterestDeductionAmount;

    /**
     * 原因简述
     */
    @ApiModelProperty(value = "原因简述")
    @NotNull(message = "原因简述不能为空")
    private String reasonExplain;

    //文件列表
    @NotEmpty(message = "罚息减免材料不能为空")
    private List<MultipartFile> files;

}
