package cn.zswltech.mithras.service.auth.checker.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.AuthBaseReq;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.AuthCheckSourceSceneEnum;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/8/22
 * @description
 */
@Component
public class ClientViewMainAuthCheckerNew implements IDataAuthChecker {
    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        // 判断是否有AuthBaseReq，如果有的话先处理
        for (Object arg : args) {
            if (arg instanceof AuthBaseReq) {
                if (this.pass((AuthBaseReq) arg)) {
                    return true;
                }
            }
        }
        if (Objects.isNull(keyId)) {
            throw new AuthCheckException("主表id不能为空");
        }
        Object data = SpringUtil.getBean(businessModule.getMainMapperClass()).selectById(keyId);
        Client client;
        if (data instanceof Client) {
            client = (Client) data;
        } else {
            throw new MithrasException("主表数据类型错误");
        }
        if (Objects.isNull(client.getBelongDeptId()) || Objects.isNull(client.getBelongSponsorId())) {
            // 公海客户不鉴权
            return true;
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        if (sysUserService.userIsSpecificJob(currentUserId, JobEnum.businesshead.name()) && !sysUserService.userIsSpecificJob(currentUserId, JobEnum.leaderincharge.name())) {
            List<Long> deptIds = sysUserService.canViewDeptIds();
            if (deptIds == null) {
                return true;
            }
            if (CollectionUtil.isEmpty(deptIds) || !deptIds.contains(client.getBelongDeptId())) {
                throw new AuthCheckException("无权查看当前客户详情");
            }
        }
        if (sysUserService.userIsSpecificJob(currentUserId, JobEnum.projmanager.name())) {
            if (!Objects.equals(client.getBelongSponsorId(), currentUserId) && !Objects.equals(client.getCreateBy(), currentUserId)) {
                throw new AuthCheckException("无权查看当前客户详情");
            }
        }
        return true;
    }

    private boolean pass(AuthBaseReq authBaseReq) {
        return AuthCheckSourceSceneEnum.AFTER_LEASE_CHECK_REPORT.name().equals(authBaseReq.getSourceScene());
    }
}
