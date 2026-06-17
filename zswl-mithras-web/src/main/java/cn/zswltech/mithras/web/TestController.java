package cn.zswltech.mithras.web;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.application.orchestration.job.NextMonthRentNotify;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author luyi
 */
@RestController
@Profile({"local", "dev"})
@RequestMapping("test")
public class TestController {

    @GetMapping("nextMonthRentNotify")
    public Object triggerNextMonthRentNotify() {
        getBean(NextMonthRentNotify.class).runJob(null,null, null);
        return R.ok();
    }
}
