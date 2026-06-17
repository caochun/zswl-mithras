package cn.zswltech.mithras.assetclassify.application;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyClientRelationPort;
import cn.zswltech.mithras.dto.assetclassify.AssetClassifyClientListREQ;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.foundation.port.CurrentUserDataScopeResolver;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2023/1/9
 * @description
 */
@Service
public class AssetClassifyCommonService {
    @Resource
    private CurrentUserDataScopeResolver currentUserDataScopeResolver;
    @Resource
    private AssetClassifyClientRelationPort assetClassifyClientRelationPort;

    public <T extends AssetClassifyClient> LambdaQueryWrapper<T> buildQuery(AssetClassifyClientListREQ req) {
        LambdaQueryWrapper<T> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.eq(T::getAssetClassifyId, req.getAssetClassifyId());
        List<Long> canViewDeptIds = currentUserDataScopeResolver.canViewDeptIds();
        if (Objects.nonNull(canViewDeptIds)) {
            if (CollectionUtil.isEmpty(canViewDeptIds)) {
                canViewDeptIds.add(Long.MAX_VALUE);
            }
            conditionQuery.and(innerQuery -> {
                innerQuery.eq(T::getBelongSponsorId, AccountUtil.getLoginInfo().getId());
                innerQuery.or().in(T::getBelongDeptId, canViewDeptIds);
            });
        }
        conditionQuery.eq(Objects.nonNull(req.getClientId()), T::getClientId, req.getClientId());
        conditionQuery.like(StrUtil.isNotBlank(req.getClientName()), T::getClientName, req.getClientName());
        conditionQuery.eq(StrUtil.isNotBlank(req.getClassifyResult()), T::getSuggestResult, req.getClassifyResult());
        conditionQuery.eq(StrUtil.isNotBlank(req.getReviewStatus()), T::getReviewStatus, req.getReviewStatus());
        // 新增查询条件：根据所属机构查询
        conditionQuery.eq(Objects.nonNull(req.getBelongDeptId()), T::getBelongDeptId, req.getBelongDeptId());
        // 新增查询条件：是否关联方
        if (Objects.nonNull(req.getIsRelated())){
            List<Long> relatedClientIds = assetClassifyClientRelationPort.listClientIdsByRelated(req.getIsRelated());
            if (CollectionUtil.isNotEmpty(relatedClientIds)) {
                conditionQuery.in(T::getClientId, relatedClientIds);
            }
        }
        if (Objects.nonNull(req.getBoardMeeting()) && req.getBoardMeeting()) {
            // 如果是董事会场景下，筛选特定条件的客户
            conditionQuery.eq(T::getBoardMeeting, YesOrNoNumberEnum.YES.getCode());
        }
        return conditionQuery;
    }
}
