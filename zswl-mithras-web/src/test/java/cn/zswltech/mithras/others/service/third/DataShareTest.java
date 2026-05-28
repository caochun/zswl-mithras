package cn.zswltech.mithras.others.service.third;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.share.DataShareService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @ClassName DataShareMerchants
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/14 9:28 上午
 * @Version 1.0
 **/
public class DataShareTest extends ApplicationTest {

    @Resource
    private DataShareService dataShareService;

    /**
     * 第一次执行 2023 03 14 10:53:00
     * @author: jackerhe
     * @date: 2023/3/14 10:53 上午
     **/
    @Test
    public void syncMerchants(){
        for(int i = 0; i < 1 ; i++){
            dataShareService.syncMerchants();
        }
    }

    /**
     * 第一次执行 2023 03 14 10:53:00
     * @author: jackerhe
     * @date: 2023/3/14 10:53 上午
     **/
    @Test
    public void syncMainCode(){
        for(int i = 0; i < 5 ; i++){
            dataShareService.syscMainCode();
        }
    }


}