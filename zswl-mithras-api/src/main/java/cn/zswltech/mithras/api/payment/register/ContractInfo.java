package cn.zswltech.mithras.api.payment.register;

import lombok.Data;

import java.io.InputStream;
import java.math.BigDecimal;

/**
 * @author zhouning
 * @date 2025/03/13
 * @description
 */
@Data
public class ContractInfo {

    private String contractId;

    private String mainType;

    private String mainNo;

    private String mainCurrency;

    private BigDecimal mainSum;

    private String debtDateBegin;

    private String debtDateEnd;

    private String relateType;

    private String relateNo;

    private String relateCurrency;

    private BigDecimal relateSum;

    private String identificationCode;

    private String describe;

    //private InputStream accessoryStream;

    private String leaseBusinessType;

    //private String fileName;



    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }

    public String getMainNo() {
        return mainNo;
    }

    public void setMainNo(String mainNo) {
        this.mainNo = mainNo;
    }

    public String getMainCurrency() {
        return mainCurrency;
    }

    public void setMainCurrency(String mainCurrency) {
        this.mainCurrency = mainCurrency;
    }

    public BigDecimal getMainSum() {
        return mainSum;
    }

    public void setMainSum(BigDecimal mainSum) {
        this.mainSum = mainSum;
    }

    public String getDebtDateBegin() {
        return debtDateBegin;
    }

    public void setDebtDateBegin(String debtDateBegin) {
        this.debtDateBegin = debtDateBegin;
    }

    public String getDebtDateEnd() {
        return debtDateEnd;
    }

    public void setDebtDateEnd(String debtDateEnd) {
        this.debtDateEnd = debtDateEnd;
    }

    public String getRelateType() {
        return relateType;
    }

    public void setRelateType(String relateType) {
        this.relateType = relateType;
    }

    public String getRelateNo() {
        return relateNo;
    }

    public void setRelateNo(String relateNo) {
        this.relateNo = relateNo;
    }

    public String getRelateCurrency() {
        return relateCurrency;
    }

    public void setRelateCurrency(String relateCurrency) {
        this.relateCurrency = relateCurrency;
    }

    public BigDecimal getRelateSum() {
        return relateSum;
    }

    public void setRelateSum(BigDecimal relateSum) {
        this.relateSum = relateSum;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getLeaseBusinessType() {
        return leaseBusinessType;
    }

    public void setLeaseBusinessType(String leaseBusinessType) {
        this.leaseBusinessType = leaseBusinessType;
    }
}
