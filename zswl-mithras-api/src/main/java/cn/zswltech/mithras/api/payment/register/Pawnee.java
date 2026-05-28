package cn.zswltech.mithras.api.payment.register;

import lombok.Data;

/**
 * @author zhouning
 * @date 2025/03/13
 * @description
 */
@Data
public class Pawnee {

    private Integer debtorId;

    private String type;

    private String orgType1;

    public String getOrgType1() {
        return orgType1;
    }

    public void setOrgType1(String orgType1) {
        this.orgType1 = orgType1;
    }

    public String getOrgType2() {
        return orgType2;
    }

    public void setOrgType2(String orgType2) {
        this.orgType2 = orgType2;
    }

    public String getOrgType3() {
        return orgType3;
    }

    public void setOrgType3(String orgType3) {
        this.orgType3 = orgType3;
    }

    private String orgType2;

    private String orgType3;

    private String name;

    private String fininstCode;

    private String orgCode;

    private String creditCode;

    private String orgRegCode;

    private String lei;

    private String sydwFrCode;

    private String frName;

    private String addressType;

    private String address;

    private String province;

    private String city;

    private String county;

    private String size;

    private String industrycode;

    private String certificateCode;

    private String certificateAddress;

    private String certificateType;

    private String certificateTypeNo;

    private String passportCountry;

    private String passportEndDate;

    public Integer getDebtorId() {
        return debtorId;
    }

    public void setDebtorId(Integer debtorId) {
        this.debtorId = debtorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFininstCode() {
        return fininstCode;
    }

    public void setFininstCode(String fininstCode) {
        this.fininstCode = fininstCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getOrgRegCode() {
        return orgRegCode;
    }

    public void setOrgRegCode(String orgRegCode) {
        this.orgRegCode = orgRegCode;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public String getSydwFrCode() {
        return sydwFrCode;
    }

    public void setSydwFrCode(String sydwFrCode) {
        this.sydwFrCode = sydwFrCode;
    }

    public String getFrName() {
        return frName;
    }

    public void setFrName(String frName) {
        this.frName = frName;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIndustrycode() {
        return industrycode;
    }

    public void setIndustrycode(String industrycode) {
        this.industrycode = industrycode;
    }

    public String getCertificateCode() {
        return certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    public String getCertificateAddress() {
        return certificateAddress;
    }

    public void setCertificateAddress(String certificateAddress) {
        this.certificateAddress = certificateAddress;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateTypeNo() {
        return certificateTypeNo;
    }

    public void setCertificateTypeNo(String certificateTypeNo) {
        this.certificateTypeNo = certificateTypeNo;
    }

    public String getPassportCountry() {
        return passportCountry;
    }

    public void setPassportCountry(String passportCountry) {
        this.passportCountry = passportCountry;
    }

    public String getPassportEndDate() {
        return passportEndDate;
    }

    public void setPassportEndDate(String passportEndDate) {
        this.passportEndDate = passportEndDate;
    }
}
