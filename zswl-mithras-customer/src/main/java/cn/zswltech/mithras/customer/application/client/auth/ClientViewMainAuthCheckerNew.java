package cn.zswltech.mithras.customer.application.client.auth;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.dto.AuthBaseReq;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserDataScopeResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserJobResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserResolver;
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
    private static final String AFTER_LEASE_CHECK_REPORT = "AFTER_LEASE_CHECK_REPORT";

    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private CurrentUserJobResolver currentUserJobResolver;
    @Resource
    private CurrentUserDataScopeResolver currentUserDataScopeResolver;

    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
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
        Long currentUserId = currentUserResolver.currentUserId();
        if (currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.businesshead.name()) && !currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.leaderincharge.name())) {
            List<Long> deptIds = currentUserDataScopeResolver.canViewDeptIds();
            if (deptIds == null) {
                return true;
            }
            if (CollectionUtil.isEmpty(deptIds) || !deptIds.contains(client.getBelongDeptId())) {
                throw new AuthCheckException("无权查看当前客户详情");
            }
        }
        if (currentUserJobResolver.userIsSpecificJob(currentUserId, JobEnum.projmanager.name())) {
            if (!Objects.equals(client.getBelongSponsorId(), currentUserId) && !Objects.equals(client.getCreateBy(), currentUserId)) {
                throw new AuthCheckException("无权查看当前客户详情");
            }
        }
        return true;
    }

    private boolean pass(AuthBaseReq authBaseReq) {
        return AFTER_LEASE_CHECK_REPORT.equals(authBaseReq.getSourceScene());
    }
}
