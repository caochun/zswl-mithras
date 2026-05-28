package cn.zswltech.mithras.dto.app;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author junke
 */
@ApiModel("融租易APP我的项目合同数目-返回体")
@Data
public class AppProContractBaseRSP {
    /**
     * id
     */
    @ApiModelProperty(value = "合同id")
    private Long id;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    @ApiModelProperty(value = "合同金额(元)")
    private Long applyCreditAmount;

    @ApiModelProperty(value = "合同状态")
    private String contractStatus;
}
