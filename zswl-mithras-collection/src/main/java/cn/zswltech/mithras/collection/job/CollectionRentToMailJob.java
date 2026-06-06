package cn.zswltech.mithras.collection.job;

import cn.zswltech.mithras.collection.application.job.CollectionRentToMailJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ylzhang5
 * @date 2025/12/01
 * @description 定时扫描租金是否按时还款
 */
@Slf4j
@Component
public class CollectionRentToMailJob {
    @Resource
    private CollectionRentToMailJobService collectionRentToMailJobService;

    @XxlJob(value = "collectionRentToMailJob")
    public void collectionRent() {
        collectionRentToMailJobService.collectionRent();
    }
}
