package cn.zswltech.mithras.application.orchestration.auth.rule;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/2/10
 * @description
 */
@Component
public class DataAuthBusinessHeadRule {
    @Resource
    private SysUserService sysUserService;

    public void check(Long deptId) {
        List<UserDO> businessHeadList = sysUserService.listSpecificOrgJobUser(deptId, JobEnum.businesshead.name());
        boolean isBusinessHead = false;
        if (CollectionUtil.isNotEmpty(businessHeadList)) {
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            for (UserDO userDO : businessHeadList) {
                if (Objects.equals(userDO.getId(), currentUserId)) {
                    isBusinessHead = true;
                    break;
                }
            }
        }
        if (!isBusinessHead) {
            throw new AuthCheckException("非业务负责人，不允许操作");
        }
    }
}
