package cn.zswltech.mithras.web;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.service.job.NextMonthRentNotify;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author luyi
 */
@RestController
@ActiveProfiles("dev")
@RequestMapping("test")
public class TestController {

    @GetMapping("nextMonthRentNotify")
    public Object triggerNextMonthRentNotify() {
        getBean(NextMonthRentNotify.class).runJob(null,null, null);
        return R.ok();
    }
}
