package cn.zswltech.mithras.riskcontrol.adapter;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.constant.OrgConstants;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.foundation.port.CurrentUserOrgResolver;
import cn.zswltech.mithras.foundation.port.DeptUserResolver;
import cn.zswltech.mithras.foundation.port.OrgJobUserResolver;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationUserScopeService;
import cn.zswltech.mithras.foundation.enums.JobEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class RiskControlConcentrationUserScopeServiceAdapter implements RiskControlConcentrationUserScopeService {

    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;
    @Resource
    private OrgJobUserResolver orgJobUserResolver;
    @Resource
    private DeptUserResolver deptUserResolver;

    @Override
    public Set<Long> bizUserIds() {
        Set<Long> userIds = new HashSet<>();
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<OrgDO> orgList = currentUserOrgResolver.getUserDeptList();
        for (OrgDO org : orgList) {
            if (ObjectUtil.equal(org.getType(), OrgConstants.BUSINESS_DEPT)) {
                Long leaderId = firstOrgJobUser(org.getId(), JobEnum.businesshead.name());
                if (ObjectUtil.equal(leaderId, currentUserId)) {
                    userIds.addAll(deptUserResolver.userIdsByDeptCode(org.getCode()));
                } else {
                    userIds.add(currentUserId);
                }
            } else if (ObjectUtil.equal(org.getType(), OrgConstants.LEADERSHIP)) {
                return null;
            }
        }
        return userIds;
    }

    private Long firstOrgJobUser(Long orgId, String jobCode) {
        List<Long> userIds = orgJobUserResolver.orgJobUsers(orgId, jobCode);
        if (ObjectUtil.isEmpty(userIds)) {
            return null;
        }
        return userIds.get(0);
    }
}
