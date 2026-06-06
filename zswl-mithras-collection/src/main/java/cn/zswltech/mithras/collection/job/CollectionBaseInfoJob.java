package cn.zswltech.mithras.collection.job;

import cn.zswltech.mithras.collection.application.job.CollectionBaseInfoMsgJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhouning
 * @date 2024/07/08/18:51
 * @description 定时扫描租金还款表，提前30天提醒
 */
@Slf4j
@Component
public class CollectionBaseInfoJob {
    @Resource
    private CollectionBaseInfoMsgJobService collectionBaseInfoMsgJobService;

    @XxlJob(value = "collectionBaseInfoMsg")
    public void collectionBaseInfoMsg() {
        collectionBaseInfoMsgJobService.collectionBaseInfoMsg();
    }
}
