package cn.zswltech.mithras.dto.contract.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @ClassName ContractPriceModifyREQ
 * @Description
 * @Author jackerhe
 * @Date 2022/8/16 2:04 下午
 * @Version 1.0
 **/
@Data
@ApiModel("合同报价方案更新-返回体")
public class ContractPriceDetailRSP {

    @ApiModelProperty("租赁报价方案返回体")
    private ContractLeasePriceDetailRSP leasePriceModifyRSP;

    @ApiModelProperty("债权转让请求体")
    private ContractAocPriceDetailRSP aocPriceRSP;

    @ApiModelProperty("保理方案请求体")
    private ContractFactoringPriceDetailRSP factoringPriceRSP;

    public boolean isNull() {
        return Objects.isNull(leasePriceModifyRSP) && Objects.isNull(aocPriceRSP) && Objects.isNull(factoringPriceRSP);
    }

    public Long getId() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getId();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getId();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getId();
        }
        return null;
    }

    public Integer getMonthCount() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getLeaseMonthCount();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getAocCreditTerm();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getFactoringCreditTerm();
        }
        return null;
    }

    public Long getContractId() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getContractId();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getContractId();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getContractId();
        }
        return null;
    }

    //获取合同金额
    public Long getApplyCreditAmount() {

        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getApplyCreditAmount();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getContractAmount();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getContractAmount();
        }
        return null;
    }

    public Long getEarnestMoney() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getEarnestMoney();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getEarnestMoney();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getEarnestMoney();
        }
        return null;
    }

    public Long getConsultingFee() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getConsultingFee();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getConsultingFee();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getConsultingFee();
        }
        return null;
    }

    public Long getCommission() {
        if (leasePriceModifyRSP != null) {
            return Optional.ofNullable(leasePriceModifyRSP.getCommission()).orElse(0L);
        }
        return 0L;
    }

    public Long getFirstInstallmentInterest() {
        if (leasePriceModifyRSP != null) {
            return Optional.ofNullable(leasePriceModifyRSP.getFirstInstallmentInterest()).orElse(0L);
        }
        return 0L;
    }

    public Long getDownPayment() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getDownPayment();
        }
        if (aocPriceRSP != null) {
            return 0L;
        }
        if (factoringPriceRSP != null) {
            return 0L;
        }
        return null;
    }

    public Long getNominalPrice() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getNominalPrice();
        }
        if (aocPriceRSP != null) {
            return 0L;
        }
        if (factoringPriceRSP != null) {
            return 0L;
        }
        return null;
    }

    public Integer getLprPercent() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getLprPercent();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getLprPercent();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getLprPercent();
        }
        return null;
    }

    public Integer getLprAddPercent() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getLprAddPercent();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getLprAddPercent();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getLprAddPercent();
        }
        return null;
    }

    public Integer getRepayTimesTotal(){
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getRepayTimesTotal();
        }
        return null;
    }

    public String getRateType() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getRateType();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getRateType();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getRateType();
        }
        return null;
    }

    public Integer getIrr() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getIrrPercent();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getIrrPercent();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getIrrPercent();
        }
        return null;
    }

    public Integer getPricingIrr() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getPricingIrrPercent();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getPricingIrrPercent();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getPricingIrrPercent();
        }
        return null;
    }

    public String getRepayRate() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getRepayRate();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getRepayRate();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getRepayRate();
        }
        return null;
    }

    public String getRentalCalcType() {
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getRentalCalcType();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getRepayCalcType();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getRepayCalcType();
        }
        return null;
    }

    public String getInterestWay(){
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getInterestWay();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getInterestWay();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getInterestWay();
        }
        return null;
    }

    public List<StructuredInterest> getStructuredInterestList(){
        if (leasePriceModifyRSP != null) {
            return leasePriceModifyRSP.getStructuredInterestList();
        }
        if (aocPriceRSP != null) {
            return aocPriceRSP.getStructuredInterestList();
        }
        if (factoringPriceRSP != null) {
            return factoringPriceRSP.getStructuredInterestList();
        }
        return null;
    }
}
