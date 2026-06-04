package cn.zswltech.mithras.service.auth.rule;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.service.SysUserService;
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
public class DataAuthDeptRule {
    @Resource
    private SysUserService sysUserService;

    /**
     * 啥也不用 就判断当前登陆人 是不是业务部门的
     */
    public void check() {
        log.info("当前登陆用户id：{}", AccountUtil.getLoginInfo().getId());
        OrgDO orgDO = sysUserService.currentUserBizDept();
        if (orgDO == null) {
            // 说明当前登陆用户不归属任何业务部门
            throw new AuthCheckException("非业务部门用户不支持该操作");
        }
    }
}
