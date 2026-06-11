package cn.zswltech.mithras.others.service.osstemplate;

import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.document.enums.materialslist.FileTemplateEnum;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @ClassName PaymentPolicyTest
 * @Description TODO
 * @Author jackerhe
 * @Date 2022/12/30 9:34 上午
 * @Version 1.0
 **/
public class PaymentTest extends ApplicationTest {

    @Resource
    private OssClient ossClient;

    @Test
    public void updatePolicyTemplate() throws FileNotFoundException {
        File file = new File("/Users/zswl/Downloads/保单信息模版.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        ossClient.upLoad(inputStream, GlobalConstants.TEMPLATE_OSS_NAME_PAYMENT_POLICY_ITEM);
    }

    @Test
    public void updateFileTemplate() throws FileNotFoundException{
        File file = new File("/Users/zswl/Downloads/概算支付表模版.xlsx");
        FileInputStream inputStream1 = new FileInputStream(file);
        FileInputStream inputStream2 = new FileInputStream(file);
        FileInputStream inputStream3 = new FileInputStream(file);
        FileInputStream inputStream4 = new FileInputStream(file);
        ossClient.upLoad(inputStream1, FileTemplateEnum.TEMPLATE_OSS_NAME_ACTUAL_PAYMENT_ITEM.display);
        ossClient.upLoad(inputStream2, FileTemplateEnum.TEMPLATE_OSS_NAME_ESTIMATE_PAYMENT_ITEM.display);
        ossClient.upLoad(inputStream3, FileTemplateEnum.TEMPLATE_OSS_NAME_ACTUAL_RENT_ITEM.display);
        ossClient.upLoad(inputStream4, FileTemplateEnum.TEMPLATE_OSS_NAME_ESTIMATE_RENT_ITEM.display);
    }
}
