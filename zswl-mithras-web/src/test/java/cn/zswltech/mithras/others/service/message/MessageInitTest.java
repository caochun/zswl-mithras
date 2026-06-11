package cn.zswltech.mithras.others.service.message;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.message.job.MessageJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/6/11 14:46
 */
public class MessageInitTest extends ApplicationTest {

    @Resource
    private MessageJob messageJob;

    @Test
    public void init() {
        messageJob.initMessage();
    }

    @Test
    public void syncJTMessage() {
        messageJob.syncJTMessage();
    }
}
