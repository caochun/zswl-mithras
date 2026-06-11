package cn.zswltech.mithras.application.orchestration.auth.rule;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.foundation.auth.DataAuthAssetManagerGuard;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/21
 * @description
 */
@Component
public class DataAuthAssetManagerRule implements DataAuthAssetManagerGuard {
    @Resource
    private SysUserService sysUserService;

    @Override
    public void check() {
        Long userId = AccountUtil.getLoginInfo().getId();
        List<String> jobList = sysUserService.queryUserJobList(userId);
        boolean isAssetManager = false;
        boolean isAdministrator = false;
        for (String jobCode : jobList) {
            if (Objects.equals(jobCode, JobEnum.assetmanagement.name())) {
                isAssetManager = true;
            }
            if (Objects.equals(jobCode, JobEnum.admin.name())) {
                isAdministrator = true;
            }
        }
        if (!isAdministrator && !isAssetManager) {
            throw new AuthCheckException("只有资产管理和管理员可以操作");
        }
    }
}
