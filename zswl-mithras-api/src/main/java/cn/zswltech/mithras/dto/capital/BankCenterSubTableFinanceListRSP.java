package cn.zswltech.mithras.dto.capital;

import cn.zswltech.mithras.dto.capital.base.BusinessFlowBaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * @author yangxiong
 * @date 2024/5/18/14:49
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BankCenterSubTableFinanceListRSP extends BusinessFlowBaseModel {

    @ApiModelProperty(value = "业务ID")
    private Long id;

    @ApiModelProperty("流水类型 PlatformApiEnum")
    private String platform;

    @ApiModelProperty("来源途径 ExceptionSourceENUM")
    private String source;

    /**
     * 资金收付款id
     */
    private Long receiptRepayBaseId;

    /**
     * 资金收付款code
     */
    private String receiptRepayBaseCode;

    /**
     * 现金流编号
     */
    private String cashFlowCode;

    /**
     * 机构ID
     */
    private List<Long> orgIds;

    /**
     * 机构名称
     */
    private List<String> orgNames;

    @ApiModelProperty(value = "是否直融，1-是，0-否")
    private Integer isDirect;

    @ApiModelProperty("业务类型")
    private String businessType;
}
