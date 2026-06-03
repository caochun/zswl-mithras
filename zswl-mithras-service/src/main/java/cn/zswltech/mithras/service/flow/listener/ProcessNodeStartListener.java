package cn.zswltech.mithras.service.flow.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.flow.core.extension.event.NodeStartEvent;
import cn.zswltech.flow.core.extension.event.context.NodeCommonContext;
import cn.zswltech.mithras.factory.model.RatingClient;
import cn.zswltech.mithras.factory.model.RatingSnapshot;
import cn.zswltech.mithras.factory.service.RatingClientService;
import cn.zswltech.mithras.factory.service.RatingSnapshotService;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.FundCreditMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundCredit;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.fund.application.FundFinancingCreditRefService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
/**
 * 节点开始监听
 */
@Slf4j
@Component
public class ProcessNodeStartListener implements ApplicationListener<NodeStartEvent> {

    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private RatingSnapshotService ratingSnapshotService;

    @Override
    public void onApplicationEvent(NodeStartEvent nodeStartEvent) {
        NodeCommonContext nodeCommonContext = nodeStartEvent.getNodeCommonContext();

        // 到发起人节点后释放额度
        if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.FundFinancingCreateFlow.name(), ProcessModelTypeEnum.FundFinancingModifyFlow.name())
                && CharSequenceUtil.equalsAny(nodeCommonContext.getActivityId(), "userTask_startUser")) {
            Long financingId = Long.valueOf(nodeCommonContext.getBusinessKey());
            List<FundFinancingCreditRef> refList = SpringContextHolder.getBean(FundFinancingCreditRefService.class).queryByFinancingId(financingId);
            if (CollectionUtil.isEmpty(refList)) {
                return;
            }
            FundCredit fundCredit = SpringContextHolder.getBean(FundCreditMapper.class).selectById(refList.get(0).getId());
            CreditLimitDetailBO creditLimitDetailBO = SpringContextHolder.getBean(FundCreditService.class).queryLimitDetail(fundCredit, true);
            List<CreditLimitDetailBO.CreditLimitOccupyDetailBO> occupyDetailList = Optional.ofNullable(creditLimitDetailBO).map(CreditLimitDetailBO::getOccupyDetailList).orElse(null);
            if (CollectionUtil.isNotEmpty(occupyDetailList) && occupyDetailList.stream().filter(f -> Objects.equals(f.getBizTargetKey(), nodeCommonContext.getBusinessKey())).count() > 0) {
                FundFinancingBaseInfo financingBaseInfo = SpringContextHolder.getBean(FundFinancingBaseInfoService.class).getById(financingId);
                SpringContextHolder.getBean(FundCreditService.class).release(financingBaseInfo.getId(), financingBaseInfo.getFinancingAmount());
            }
        }

        if (CharSequenceUtil.equalsAny(nodeCommonContext.getModelKey(), ProcessModelTypeEnum.RatingClientCreateFlow.name(), ProcessModelTypeEnum.RatingClientUpdateFlow.name())) {
            RatingClient ratingClient = ratingClientService.getById(nodeCommonContext.getBusinessKey());
            // 发起人节点
            if (CharSequenceUtil.equals(nodeCommonContext.getActivityId(), "userTask_startUser")) {
                RatingSnapshot ratingSnapshot = ratingSnapshotService.getById(ratingClient.getSnapshotId());
                ratingSnapshotService.update(Wrappers.<RatingSnapshot>lambdaUpdate()
                        .eq(RatingSnapshot::getId, ratingClient.getSnapshotId())
                        .set(RatingSnapshot::getLastResult, Optional.ofNullable(ratingSnapshot).map(RatingSnapshot::getResult).orElse(null)));
            }

        }
    }
}
