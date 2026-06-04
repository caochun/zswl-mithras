package cn.zswltech.mithras.others.service.service.creditreport;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.creditreport.mapper.dto.credit.XJCreditReportJsonDTO;
import cn.zswltech.mithras.creditreport.service.CreditReportBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @ClassName FinancialServiceTest
 * @Description TODO
 * @Author jackerhe
 * @Date 2022/12/26 2:26 下午
 * @Version 1.0
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("uat")
public class CreditReportBaseInfoServiceTest {

    @Resource
    private CreditReportBaseInfoService creditReportBaseInfoService;

    @Test
    public void importByXJCreditReportJsonDTO() {
        String body = IoUtil.readUtf8(CreditReportBaseInfoServiceTest.class.getResourceAsStream("/mock/征信报告.json"));
        XJCreditReportJsonDTO xjCreditReportJsonDTO = JSONUtil.toBean(body, XJCreditReportJsonDTO.class);
        creditReportBaseInfoService.importByXJCreditReportJsonDTO(xjCreditReportJsonDTO, 6L, 12L);
    }

    @Test
    public void importByXJCreditReportObtainResultPDFResp() {
        creditReportBaseInfoService.importByXJCreditReportObtainResultPDFResp(null, 12L);
    }

}
