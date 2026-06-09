package cn.zswltech.mithras.system.controller;

import cn.zswltech.mithras.api.SystemUserOperateLogApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SystemUserOperateLogREQ;
import cn.zswltech.mithras.dto.SystemUserOperateLogRSP;
import cn.zswltech.mithras.system.audit.SystemUserOperateLogService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2024/9/10
 * @description
 */
@RestController
public class SystemUserOperateLogController implements SystemUserOperateLogApi {
    @Resource
    private SystemUserOperateLogService systemUserOperateLogService;

    @Override
    public R<PageR<SystemUserOperateLogRSP>> pageList(@Valid SystemUserOperateLogREQ req) {
        return R.ok(systemUserOperateLogService.pageList(req));
    }
}
