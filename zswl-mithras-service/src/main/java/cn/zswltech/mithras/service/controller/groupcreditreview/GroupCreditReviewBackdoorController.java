package cn.zswltech.mithras.service.controller.groupcreditreview;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.groupcreditreview.GroupCreditReviewBackdoorApi;
import cn.zswltech.mithras.common.enums.RecordStatus;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.groupcreditreview.GroupCreditReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 后门接口 方便查看一个项目评审对应的授信评审，和一个授信评审对应多少项目评审
 *
 * @author wangchuanhao
 * @date 2022/11/28 10:06 AM
 */
@RestController
public class GroupCreditReviewBackdoorController implements GroupCreditReviewBackdoorApi {

    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private GroupCreditReviewBaseInfoService groupCreditReviewBaseInfoService;

    @Override
    public R<Map<String, Object>> queryProjReview(Long projReviewId) {
        Map<String, Object> resMap = new HashMap<>();
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
        if (Objects.isNull(projReviewBaseInfo)) {
            return R.ok(resMap);
        }
        resMap.put("项目名称", projReviewBaseInfo.getProjName());
        resMap.put("项目编号", projReviewBaseInfo.getProjCode());
        resMap.put("申请授信金额", Util.mithrasLong2BigDecimal(projReviewBaseInfo.getDeclaredAmount()).toPlainString());
        resMap.put("流程状态", Optional.ofNullable(ProjProcessState.of(projReviewBaseInfo.getProjReviewProcessStatus())).map(p -> p.display).orElse(projReviewBaseInfo.getProjReviewProcessStatus()));
        resMap.put("评审状态", Optional.ofNullable(RecordStatus.of(projReviewBaseInfo.getProjReviewStatus())).map(p -> p.display).orElse(projReviewBaseInfo.getProjReviewStatus()));
        resMap.put("是否用信数据", ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projReviewBaseInfo.getRelationDataType()));
        if (ReviewRelationDataType.GROUP_CREDIT_REVIEW.name().equals(projReviewBaseInfo.getRelationDataType())) {
            GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(projReviewBaseInfo.getGroupCreditReviewId());
            resMap.put("关联授信id", groupCreditReviewBaseInfo.getId());
            resMap.put("关联授信名称", groupCreditReviewBaseInfo.getProjName());
            resMap.put("关联授信编号", groupCreditReviewBaseInfo.getProjCode());
        }
        return R.ok(resMap);
    }

    @Override
    public R<List<Map<String, Object>>> queryRelationship(Long groupCreditReviewId) {
        List<Map<String, Object>> resList = new ArrayList<>();
        GroupCreditReviewBaseInfo groupCreditReviewBaseInfo = groupCreditReviewBaseInfoService.getById(groupCreditReviewId);
        if (Objects.isNull(groupCreditReviewBaseInfo)) {
            return R.ok(resList);
        }
        List<ProjReviewBaseInfo> projReviewBaseInfoList = projReviewBaseInfoService.getBaseMapper().selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getRelationDataType, ReviewRelationDataType.GROUP_CREDIT_REVIEW.name())
                .eq(ProjReviewBaseInfo::getGroupCreditReviewId, groupCreditReviewId)
        );
        for (ProjReviewBaseInfo projReviewBaseInfo : projReviewBaseInfoList) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("项目评审id", projReviewBaseInfo.getId());
            itemMap.put("项目名称", projReviewBaseInfo.getProjName());
            itemMap.put("项目编号", projReviewBaseInfo.getProjCode());
            itemMap.put("申请授信金额", Util.mithrasLong2BigDecimal(projReviewBaseInfo.getDeclaredAmount()).toPlainString());
            itemMap.put("流程状态", Optional.ofNullable(ProjProcessState.of(projReviewBaseInfo.getProjReviewProcessStatus())).map(p -> p.display).orElse(projReviewBaseInfo.getProjReviewProcessStatus()));
            itemMap.put("评审状态", Optional.ofNullable(RecordStatus.of(projReviewBaseInfo.getProjReviewStatus())).map(p -> p.display).orElse(projReviewBaseInfo.getProjReviewStatus()));
            resList.add(itemMap);
        }
        return R.ok(resList);
    }

}
