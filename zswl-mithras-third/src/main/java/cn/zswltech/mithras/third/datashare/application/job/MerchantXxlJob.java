package cn.zswltech.mithras.third.datashare.application.job;

import cn.zswltech.gruul.web.api.intercept.WhiteListUtil;
import cn.zswltech.mithras.third.datashare.service.DataShareService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MerchantXxlJob {

    @Autowired
    private DataShareService dataShareService;

    /**
     * 1、客商同步任务（Bean模式）
     */
    @XxlJob("merchantsJobHandler")
    public void demoJobHandler() {
        try {
            log.info(">>>>>>>>>>>>>>merchantsJobHandler began syncMerchants");
            //当前线程加入白名单，绕过拦截器 AuditDataInterceptor 获取登录用户操作
            WhiteListUtil.setWhiteListFlagHolder(Boolean.TRUE);
            dataShareService.syncMerchants();
            log.info(">>>>>>>>>>>>>>merchantsJobHandler over syncMerchants");
        } catch (Exception e){
            log.error("merchantsJobHandler error", e);
        }

    }

    @XxlJob("syscMainJobHandler")
    public void syscMain() {
        try {
            log.info(">>>>>>>>>>>>>>syscMainJobHandler began syncMerchants");
            //当前线程加入白名单，绕过拦截器 AuditDataInterceptor 获取登录用户操作
            WhiteListUtil.setWhiteListFlagHolder(Boolean.TRUE);
            dataShareService.syscMainCode();
            log.info(">>>>>>>>>>>>>>syscMainJobHandler over syncMerchants");
        } catch (Exception e){
            log.error(">>>>>>>>>>>>>>syscMainJobHandler error", e);
        }

    }
}
