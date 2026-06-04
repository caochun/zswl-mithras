package cn.zswltech.mithras.service.service.Listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.event.ProjReviewApprovalPassEvent;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/9
 * @description 合同同步变更项目评审冗余信息
 */
@Slf4j
@Component
public class ContractUpdateProjReviewInfoListener implements ApplicationListener<ProjReviewApprovalPassEvent> {
    @Resource
    private ContractService contractService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    @Override
    public void onApplicationEvent(@NotNull ProjReviewApprovalPassEvent projReviewApprovalPassEvent) {
        Long projReviewId = projReviewApprovalPassEvent.getProjReviewId();
        log.info("收到项目评审通过事件，尝试更新合同冗余的项目评审信息[projReviewId:{}]", projReviewId);
        try {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.selectListByProjId(projReviewId);
            if (CollectionUtil.isEmpty(contractBaseInfoList)) {
                log.info("没有找到关联评审的合同，无需处理");
                return;
            }
            log.info("需同步变更项目评审数据合同:{}", JSONUtil.toJsonStr(contractBaseInfoList));
            ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(projReviewId);
            Assert.notNull(projReviewBaseInfo, () -> MithrasException.newException("项目评审信息不存在"));
            for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
                try {
                    contractService.syncProjReviewInfo(contractBaseInfo, projReviewBaseInfo);
                } catch (Exception e) {
                    log.error("项目评审通过，同步变更合同{}的冗余信息异常[projReviewId:{}]", contractBaseInfo.getContractCode(), projReviewId, e);
                }
            }
        } catch (Exception e) {
            log.error("项目评审通过，同步更新合同冗余信息异常[projReviewId:{}]", projReviewApprovalPassEvent.getProjReviewId(), e);
        }
    }
}
