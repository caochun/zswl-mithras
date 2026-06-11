package cn.zswltech.mithras.contract.job;

import cn.zswltech.mithras.contract.job.service.NominalPriceInitJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description 因为初始化名义价款生成时间节点放到起租后，存在合同起租后-结清前数据没有名义价款，所以这里初始化上线跑一次即可
 * @author huangping
 * @date 2025/12/16 14:30
 * @version 1.0
 */
@Slf4j
@Component
public class NominalPriceInitJob {

    @Resource
    private NominalPriceInitJobService nominalPriceInitJobService;

    @XxlJob("NominalPriceInitJob")
    public void nominalPriceInitJob() {
        nominalPriceInitJobService.nominalPriceInitJob(XxlJobHelper.getJobParam());
    }
}
