package cn.zswltech.mithras.report.auth;

import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.system.user.SysUserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 征信报送权限校验拦截
 *
 * @author wangchuanhao
 * @date 2022/7/21 2:40 PM
 */
@Aspect
@Component
public class ReportAuthCheckAdvice {

    @Resource
    private SysUserService sysUserService;

    @Pointcut("@annotation(cn.zswltech.mithras.report.auth.ReportAuthCheck)")
    public void authCheck() {

    }

    @Before("authCheck() && @annotation(reportAuthCheck)")
    public void doBefore(JoinPoint joinPoint, ReportAuthCheck reportAuthCheck) {
        ReportAuthCheck.AuthType authType = reportAuthCheck.type();
        if (ReportAuthCheck.AuthType.EDIT.equals(authType)) {
//            if (!sysUserService.currentUserIsSpecificJob(JobEnum.riskmanager.name())) {
//                throw new AuthCheckException("只有风控经理可以修改征信报送的数据");
//            }
        } else if (ReportAuthCheck.AuthType.VIEW.equals(authType)) {
            // 菜单开放给风控部全员+领导层（领导层+风委会+评审会+董事会）此处不做权限 由菜单控制

        }
    }

}
