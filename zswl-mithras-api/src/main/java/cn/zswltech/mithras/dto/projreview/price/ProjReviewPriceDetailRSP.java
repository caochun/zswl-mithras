package cn.zswltech.mithras.dto.projreview.price;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@ApiModel("报价方案表详情-返回体")
public class ProjReviewPriceDetailRSP {
    @ApiModelProperty("租赁报价方案")
    private ProjReviewLeasePriceRSP leasePriceDetailRSP;
    @ApiModelProperty("保理报价方案")
    private ProjReviewFactoringPriceRSP factoringPriceDetailRSP;
    @ApiModelProperty("债权转让报价方案")
    private ProjReviewAocPriceRSP aocPriceDetailRSP;

    public Long getProjectReviewId() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getProjectId();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getProjectId();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getProjectId();
        }
        return null;
    }

    public LocalDate getPlanStartDate() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getPlannedStartingDate();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getPlannedStartingDate();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getPlannedStartingDate();
        }
        return null;
    }

    public Long getApplyCreditAmount() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getApplyCreditAmount();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getApplyCreditAmount();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getApplyCreditAmount();
        }
        return null;
    }

    public Long getApprovedAmount() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getApprovedAmount();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getApprovedAmount();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getApprovedAmount();
        }
        return null;
    }

    public Integer getMonthCount() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getLeaseMonthCount();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getFactoringCreditTerm();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getAocCreditTerm();
        }
        return null;
    }

    public Long getEarnestMoney() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getEarnestMoney();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getEarnestMoney();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getEarnestMoney();
        }
        return null;
    }

    public Long getConsultingFee() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getConsultingFee();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getConsultingFee();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getConsultingFee();
        }
        return null;
    }

    public Long getCommission() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return Optional.ofNullable(leasePriceDetailRSP.getCommission()).orElse(0L);
        }
        return 0L;
    }

    public Long getFirstInstallmentInterest() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return Optional.ofNullable(leasePriceDetailRSP.getFirstInstallmentInterest()).orElse(0L);
        }
        return 0L;
    }

    public Long getDownPayment() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getDownPayment();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return 0L;
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return 0L;
        }
        return null;
    }

    public Long getNominalPrice() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getNominalPrice();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return 0L;
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return 0L;
        }
        return null;
    }

    public String getCalculateType() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getRentalCalcType();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getRentalCalcType();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getRentalCalcType();
        }
        return null;
    }

    public String getRepayRate() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getRepayRate();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getRepayRate();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getRepayRate();
        }
        return null;
    }

    public String getRateType() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getRateType();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getRateType();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getRateType();
        }
        return null;
    }

    public Integer getRatePercent() {
        if (Objects.nonNull(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getLeaseRatePercent();
        }
        if (Objects.nonNull(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getFactoringRatePercent();
        }
        if (Objects.nonNull(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getAocRatePercent();
        }
        return null;
    }

    public Integer getIrr() {
        if (ObjectUtil.isNotEmpty(leasePriceDetailRSP)) {
            return leasePriceDetailRSP.getIrrPercent();
        }
        if (ObjectUtil.isNotEmpty(factoringPriceDetailRSP)) {
            return factoringPriceDetailRSP.getIrrPercent();
        }
        if (ObjectUtil.isNotEmpty(aocPriceDetailRSP)) {
            return aocPriceDetailRSP.getIrrPercent();
        }
        return null;
    }
}
