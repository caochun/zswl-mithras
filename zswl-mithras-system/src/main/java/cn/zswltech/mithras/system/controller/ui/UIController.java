package cn.zswltech.mithras.system.controller.ui;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.system.mapper.SystemSwitchMapper;
import cn.zswltech.mithras.system.mapper.model.SystemSwitch;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @author yibin
 */
@RestController
public class UIController {

    @Resource
    private SystemSwitchMapper systemSwitchMapper;

    @GetMapping("/system/switch/memorialDay")
    public R<Boolean> memorialDay() {
        SystemSwitch systemSwitch = systemSwitchMapper.selectOne(Wrappers.<SystemSwitch>lambdaQuery().eq(SystemSwitch::getCode, "MEMORIAL_DAY"));
        if (isNotNull(systemSwitch) && Objects.equals(systemSwitch.getValue(), 1)) {
            return R.ok(true);
        }
        return R.ok(false);
    }
}
