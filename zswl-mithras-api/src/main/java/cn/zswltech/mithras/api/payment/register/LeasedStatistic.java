package cn.zswltech.mithras.api.payment.register;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhouning
 * @date 2025/03/13
 * @description
 */
@Data
public class LeasedStatistic {

    private Integer statisticsId;

    private String leasedType;

    private String leasedType2;

    private Integer leasedCount;

    private String leasedCurrency;

    private BigDecimal leasedPrice;

    public Integer getStatisticsId() {
        return statisticsId;
    }

    public void setStatisticsId(Integer statisticsId) {
        this.statisticsId = statisticsId;
    }

    public String getLeasedType() {
        return leasedType;
    }

    public void setLeasedType(String leasedType) {
        this.leasedType = leasedType;
    }

    public String getLeasedType2() {
        return leasedType2;
    }

    public void setLeasedType2(String leasedType2) {
        this.leasedType2 = leasedType2;
    }

    public Integer getLeasedCount() {
        return leasedCount;
    }

    public void setLeasedCount(Integer leasedCount) {
        this.leasedCount = leasedCount;
    }

    public String getLeasedCurrency() {
        return leasedCurrency;
    }

    public void setLeasedCurrency(String leasedCurrency) {
        this.leasedCurrency = leasedCurrency;
    }

    public BigDecimal getLeasedPrice() {
        return leasedPrice;
    }

    public void setLeasedPrice(BigDecimal leasedPrice) {
        this.leasedPrice = leasedPrice;
    }
}
