package cn.zswltech.mithras.application.orchestration.auth.rule;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.foundation.auth.DataAuthDeptGuard;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 业务主办校验规则
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:50 PM
 */
@Component
@Slf4j
public class DataAuthDeptRule implements DataAuthDeptGuard {
    @Resource
    private SysUserService sysUserService;

    /**
     * 啥也不用 就判断当前登陆人 是不是业务部门的
     */
    @Override
    public void check() {
        log.info("当前登陆用户id：{}", AccountUtil.getLoginInfo().getId());
        OrgDO orgDO = sysUserService.currentUserBizDept();
        if (orgDO == null) {
            // 说明当前登陆用户不归属任何业务部门
            throw new AuthCheckException("非业务部门用户不支持该操作");
        }
    }
}
