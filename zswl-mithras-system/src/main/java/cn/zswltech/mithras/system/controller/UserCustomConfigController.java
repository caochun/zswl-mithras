package cn.zswltech.mithras.system.controller;

import cn.zswltech.mithras.api.UserCustomConfigApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigDetailREQ;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigDetailRSP;
import cn.zswltech.mithras.dto.usercustomconfig.UserCustomConfigSaveREQ;
import cn.zswltech.mithras.system.config.UserCustomConfigService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/2/11
 * @description
 */
@RestController
public class UserCustomConfigController implements UserCustomConfigApi {
    @Resource
    private UserCustomConfigService userCustomConfigService;

    @Override
    public R<Void> save(UserCustomConfigSaveREQ req) {
        userCustomConfigService.save(req);
        return R.ok();
    }

    @Override
    public R<List<UserCustomConfigDetailRSP>> query(UserCustomConfigDetailREQ req) {
        return R.ok(userCustomConfigService.query(req));
    }
}
