package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 租金催收首页列表请求体
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("租金催收首页列表请求体")
@Data
public class RentCollectionPenaltyReduceREQ {

    @ApiModelProperty("备注")
    @NotBlank(message = "备注不能为空")
    private String notes;

    private List<RentCollectionPenaltyReduceDetailRSP.RentCollectionPenaltyReduceItem> items;

    @ApiModelProperty("上传文件")
    private List<MultipartFile> files;

}
