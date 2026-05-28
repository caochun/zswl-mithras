package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @ClassName RiskContralScoreCordImportREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 2:04 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordImportREQ {

    @ApiModelProperty("经济数据文件")
    @NotNull(message = "经济数据文件不能为空")
    private MultipartFile file;

    @ApiModelProperty("年份")
    @NotNull(message = "年份不能为空")
    private Integer year;

}
