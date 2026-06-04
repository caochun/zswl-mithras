package cn.zswltech.mithras.creditreport.service.req;

import lombok.Data;

import java.io.File;

/**
 * @ClassName CreditReportBaseReq
 * @Description 4.1新增档案信息
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportQueryEntFourEleAuthReq extends CreditReportBaseReq {

    //企业名称
    private String entName;

    //企业身份标识类型
    private String entCertType;

    //企业身份标识号码
    private String entCertNum;

    //授权起始日 yyyy-mm-dd
    private String authStartDate;

    //授权结束日 yyyy-mm-dd
    private String authExpiryDate;

    //营业执照复印件文件
    private File businessLicenseCopy;

    //法人身份证复印件(正面)
    private File legalIdCardFront;

    //法人身份证复印件(反面)
    private File legalIdCardBack;

    //法人授权书复印件
    private File legalAuthorize;


}
