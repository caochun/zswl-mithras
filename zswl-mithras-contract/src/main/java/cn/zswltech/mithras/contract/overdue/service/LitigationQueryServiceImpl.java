package cn.zswltech.mithras.contract.overdue.service;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.contract.overdue.application.assembler.LitigationAssembler;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationListDto;
import cn.zswltech.mithras.contract.overdue.application.query.LitigationPageQuery;
import cn.zswltech.mithras.contract.overdue.application.litigation.LitigationQueryService;
import cn.zswltech.mithras.contract.overdue.dao.LitigationRegistrationDao;
import cn.zswltech.mithras.contract.overdue.model.LitigationRegistration;
import cn.zswltech.mithras.foundation.port.CurrentUserDataScopeResolver;
import cn.zswltech.mithras.foundation.port.CurrentUserResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/22 17:04
 */
@Service
public class LitigationQueryServiceImpl implements LitigationQueryService {

    @Resource
    private LitigationRegistrationDao litigationRegistrationDao;
    @Resource
    private LitigationAssembler litigationAssembler;
    @Resource
    private UserNameResolver userNameResolver;

    @Resource
    private CurrentUserDataScopeResolver currentUserDataScopeResolver;

    @Resource
    private CurrentUserResolver currentUserResolver;

    @Override
    public PageR<LitigationListDto> page(LitigationPageQuery query) {
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        boolean isBizUser = null != canViewDeptIds;
        query.setIsBizUser(isBizUser);
        if (ObjectUtil.isEmpty(canViewDeptIds)) {
            query.setDeptIdList(Collections.singletonList(0L));
        } else {
            query.setDeptIdList(canViewDeptIds);
        }
        query.setCurrentUserId(currentUserResolver.currentUserId());
        Page<LitigationRegistration> page = litigationRegistrationDao.advancedList(query);
        
        List<LitigationListDto> rspList = litigationAssembler.po2ListDto(page.getRecords());
        Map<Long, String> userNames = userNameResolver.sysUserId2Name(rspList.stream().map(LitigationListDto::getCreateBy).collect(Collectors.toSet()));
        rspList.forEach(item -> item.setCreateByName(userNames.get(item.getCreateBy())));
        return PageR.of(page, rspList);
    }
}
