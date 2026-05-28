package cn.zswltech.mithras.dto.collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CollectionFlowCenterBusinessCollectionManualRecordREQ {

    @ApiModelProperty("现金流项目")
    @NotNull(message = "付款ID不能为空")
    private String collectionId;

    @ApiModelProperty("收款编号")
    @NotNull(message = "收款编号不能为空")
    private String collectionCode;

    @ApiModelProperty("现金流项目")
    @NotNull(message = "现金流项目不能为空")
    private String cashFlowItem;

    @NotNull(message = "收款类型不能为空")
    @ApiModelProperty("收款类型")
    private String collectionType;

    @ApiModelProperty("抵扣保证金合同ID")
    private Long deductionContractId;

    @NotNull(message = "实收日期不能为空")
    @ApiModelProperty("实收日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate collectionDate;

    @ApiModelProperty("实际收款金额")
    private Long collectionAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    private BillManagementAddREQ billManagementAddREQ;

    @ApiModelProperty("是否回收保证金 0不回收 1 回收")
    private Integer isRecycleManager;

    @ApiModelProperty("是否回收保证金金额")
    private Long recycleManagerAmount;

    @ApiModelProperty("是否回收保证金日期")
    private LocalDate recycleManagerDate;


}
