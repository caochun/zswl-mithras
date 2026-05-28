package cn.zswltech.mithras.dto.third.financial;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;


/**
 * @create: 2022-08-17
 **/
@Data
public class InnerCollectionRecordREQ{

    @ApiModelProperty("合同id")
    private String contractId;

    /**
     * 现金流项目
     */
    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty("实收日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate collectionDate;

}
