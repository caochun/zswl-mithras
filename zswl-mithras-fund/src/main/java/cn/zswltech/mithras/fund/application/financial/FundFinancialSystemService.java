package cn.zswltech.mithras.fund.application.financial;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 财资系统对接服务层
 * @author zswl
 */
@Slf4j
@Service
public class FundFinancialSystemService {

    @Autowired
    public List<FinancialSystemService> financialSystemServiceList;


    @XxlJob("financialSystemPush")
    public void send() {
        String batchNum = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        for (FinancialSystemService financialSystemService : financialSystemServiceList) {
            try {
                financialSystemService.push(batchNum);
            }catch (Exception e){
                log.error("推送财资数据发生异常，类型为{}", financialSystemService.getModule().display(), e);
            }
        }
    }


}
