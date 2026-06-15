package cn.zswltech.mithras.dashboard.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.dashboard.model.CommonAuthQuery;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.port.UserDataScopeResolver;

import java.util.List;

public final class DashboardAuthQueryHelper {

    private DashboardAuthQueryHelper() {
    }

    public static void fillAuthQuery(CommonAuthQuery query) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (loginInfo == null) {
            loginInfo = query.getAccountVO();
        }
        if (ObjectUtil.isEmpty(loginInfo)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        List<Long> deptIds = SpringContextHolder.getBean(UserDataScopeResolver.class).canViewDeptIds(loginInfo);
        if (deptIds == null) {
            return;
        }
        if (deptIds.isEmpty()) {
            query.setAuthCurrentUserId(loginInfo.getId());
        } else {
            query.setAuthBizDeptIds(deptIds);
        }
    }
}
