package cn.zswltech.mithras.document.job;

import cn.zswltech.mithras.document.job.FileTemplateKeyInitJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @date 2025/1/9 18:15
 * @description
 */
@Component
@Slf4j
public class FileTemplateKeyInitJob {

    @Resource
    private FileTemplateKeyInitJobService fileTemplateKeyInitJobService;

    @XxlJob("fileTemplateKeyInitJob")
    public void init() {
        fileTemplateKeyInitJobService.init();
    }
}
