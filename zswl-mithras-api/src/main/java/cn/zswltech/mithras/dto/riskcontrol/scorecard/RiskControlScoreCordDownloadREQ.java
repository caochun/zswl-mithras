package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RiskContralScoreCordImportREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/27 2:04 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordDownloadREQ {

    @ApiModelProperty("年份 默认当年")
    private Integer year;

}
