package cn.zswltech.mithras.service.service.creditreport.resp;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName CreditReportBaseReq
 * @Description 4.1新增档案信息
 * @Author jackerhe
 * @Date 2025/11/21 09:07
 * @Version 1.0
 **/
@Data
public class CreditReportObtainResultPDFResp extends CreditReportBaseResp {

    //结构化数据JSON
    private String pdf;

    private MultipartFile multipartFile;

}
