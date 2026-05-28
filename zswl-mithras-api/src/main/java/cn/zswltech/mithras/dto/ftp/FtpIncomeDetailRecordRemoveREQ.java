package cn.zswltech.mithras.dto.ftp;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 资金管理-融资管理-ftp收益记录表
 * @author vico
 * @date 2025-07-15
 */
@Data
@ApiModel("资金管理-融资管理-ftp收益记录表删除-请求体")
public class FtpIncomeDetailRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
