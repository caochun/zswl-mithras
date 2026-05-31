package cn.zswltech.mithras.service.mapper.model.dashboard;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.SysUserService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonAuthQuery extends CommonLimitQuery {
    private List<Long> authBizDeptIds;
    private Long authCurrentUserId;
    private AccountVO accountVO;

    public void fillAuthQuery() {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (loginInfo == null) {
            loginInfo = accountVO;
        }
        if (ObjectUtil.isEmpty(loginInfo)) {
           throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        List<Long> longs = SpringContextHolder.getBean(SysUserService.class).canViewDeptIds(loginInfo);
        if (longs == null) {
            return;
        }
        if (longs.isEmpty()) {
            authCurrentUserId = loginInfo.getId();
        } else {
            authBizDeptIds = longs;
        }
    }
}
