package cn.zswltech.mithras.collection.job;

import cn.zswltech.mithras.collection.application.job.RentExpireToMailJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ylzhang5
 * @date 2025/12/01
 * @description 定时扫描租金是即将到期
 */
@Slf4j
@Component
public class RentExpireToMailJob {

    @Resource
    private RentExpireToMailJobService rentExpireToMailJobService;

    @XxlJob(value = "rentExpireToMailJob")
    public void collectionRent() {
        rentExpireToMailJobService.collectionRent();
    }
}
