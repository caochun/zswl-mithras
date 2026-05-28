package cn.zswltech.mithras.api.payment.register;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhouning
 * @date 2025/03/13
 * @description
 */
@Data
public class RegisterSaveReq {
    @ApiModelProperty(value = "infoId")
    private Integer infoId;

    @ApiModelProperty(value = "登记用户的身份")
    private String identificationType;

    @ApiModelProperty(value = "登记类型")
    private String regTypeNo;

    @ApiModelProperty(value = "填表人证件类型")
    private String certificateType;

    @ApiModelProperty(value = "填表人证件号码")
    private String certificateCode;

    @ApiModelProperty(value = "交易业务类型")
    private String businessType;

    @ApiModelProperty(value = "登记期限_月")
    private Integer regtimeLimitClean;

    private String regDate;

    @ApiModelProperty(value = "登记到期日")
    private String regDateEnd;

    @ApiModelProperty(value = "填表人归档号")
    private String fillNo;

    @ApiModelProperty(value = "初始登记编号")
    private String initRegNo;

    @ApiModelProperty(value = "授权人")
    private String authorizer;

    private String regStatus;

    private String regActionNo;

    private String leaseBusinessType;

    private Long thirdUserId;

    @ApiModelProperty(value = "债务人类型")
    private String debtorType;

    @ApiModelProperty(value = "债务人信息")
    private List<Debtor> debtorList;

    @ApiModelProperty(value = "债权人类型")
    private String pawneeType;

    @ApiModelProperty(value = "债权人信息")
    private List<Pawnee> pawneeList;

    @ApiModelProperty(value = "财产信息")
    private ContractInfo contractInfo;

    @ApiModelProperty(value = "租赁财产统计")
    private List<LeasedStatistic> leasedStatistics;

    public Integer getInfoId() {
        return infoId;
    }

    public void setInfoId(Integer infoId) {
        this.infoId = infoId;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getRegTypeNo() {
        return regTypeNo;
    }

    public void setRegTypeNo(String regTypeNo) {
        this.regTypeNo = regTypeNo;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getRegtimeLimitClean() {
        return regtimeLimitClean;
    }

    public void setRegtimeLimitClean(Integer regtimeLimitClean) {
        this.regtimeLimitClean = regtimeLimitClean;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegDateEnd() {
        return regDateEnd;
    }

    public void setRegDateEnd(String regDateEnd) {
        this.regDateEnd = regDateEnd;
    }

    public String getFillNo() {
        return fillNo;
    }

    public void setFillNo(String fillNo) {
        this.fillNo = fillNo;
    }

    public String getInitRegNo() {
        return initRegNo;
    }

    public void setInitRegNo(String initRegNo) {
        this.initRegNo = initRegNo;
    }

    public String getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(String authorizer) {
        this.authorizer = authorizer;
    }

    public String getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(String regStatus) {
        this.regStatus = regStatus;
    }

    public String getRegActionNo() {
        return regActionNo;
    }

    public void setRegActionNo(String regActionNo) {
        this.regActionNo = regActionNo;
    }

    public String getLeaseBusinessType() {
        return leaseBusinessType;
    }

    public void setLeaseBusinessType(String leaseBusinessType) {
        this.leaseBusinessType = leaseBusinessType;
    }

    public String getDebtorType() {
        return debtorType;
    }

    public void setDebtorType(String debtorType) {
        this.debtorType = debtorType;
    }

    public List<Debtor> getDebtorList() {
        return debtorList;
    }

    public void setDebtorList(List<Debtor> debtorList) {
        this.debtorList = debtorList;
    }

    public String getPawneeType() {
        return pawneeType;
    }

    public void setPawneeType(String pawneeType) {
        this.pawneeType = pawneeType;
    }

    public List<Pawnee> getPawneeList() {
        return pawneeList;
    }

    public void setPawneeList(List<Pawnee> pawneeList) {
        this.pawneeList = pawneeList;
    }

    public ContractInfo getContractInfo() {
        return contractInfo;
    }

    public void setContractInfo(ContractInfo contractInfo) {
        this.contractInfo = contractInfo;
    }

    public List<LeasedStatistic> getLeasedStatistics() {
        return leasedStatistics;
    }

    public void setLeasedStatistics(List<LeasedStatistic> leasedStatistics) {
        this.leasedStatistics = leasedStatistics;
    }
}
