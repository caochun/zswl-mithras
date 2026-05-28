package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName CollectionReconciliationLetterREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/28 9:31 上午
 * @Version 1.0
 **/
@Data
@ApiModel("对账函-返回体")
public class CollectionReconciliationLetterRSP {

    ////直租：
    //融资租赁租金及留购货款（租金+名义价款）
    @ApiModelProperty("剩余租金及留购价款")
    private BigDecimal rentAndNominalPriceTotal;

    //融资租赁保证金（保证金余额）
    @ApiModelProperty("保证金总额")
    private BigDecimal marginTotal;

    //保理本金
    @ApiModelProperty("剩余本息总额")
    private BigDecimal blPrincipalAndInterestTotal;

    @ApiModelProperty("数据")
    private List<ReconciliationLetterBO> reconciliationLetterBOS;

    @Data
    public class ReconciliationLetterBO {

        private Long clientId;

        private String clientName;

        private String data;

        //保理保证金
        @ApiModelProperty("保证金")
        private BigDecimal margin;

        ///////回租：）、
        //融资性售后回租租金及留购货款（租金+名义价款)
        @ApiModelProperty("剩余租金及留购价款")
        private BigDecimal rentAndNominalPrice;

        //融资性售后回租保证金（保证金余额）
        @ApiModelProperty("剩余本息")
        private BigDecimal blPrincipalAndInterest;

    }
}
