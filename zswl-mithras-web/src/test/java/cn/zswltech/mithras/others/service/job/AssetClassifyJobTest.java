package cn.zswltech.mithras.others.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyBizNodeEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.assetclassify.job.AssetClassifyJob;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyNodeRecord;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyClientService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyNodeRecordService;
import cn.zswltech.mithras.assetclassify.versioning.AssetClassifyLibVersionService;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/1/4
 * @description
 */
public class AssetClassifyJobTest extends ApplicationTest {
    @Resource
    private AssetClassifyJob assetClassifyJob;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private AssetClassifyNodeRecordService assetClassifyNodeRecordService;
    @Resource
    private AssetClassifyLibVersionService assetClassifyLibVersionService;

    @Test
    public void reviewAutoPassTest() {
        assetClassifyJob.reviewAutoPass();
    }

    @Test
    public void initTest() {
        assetClassifyJob.init();
    }

    @Test
    public void remindTest() {
        assetClassifyJob.weekdayRemind();
    }

    @Test
    public void reviewFinishTest() {
        Long assetClassifyId = 88L;
        List<AssetClassifyClient> assetClassifyClientList = assetClassifyClientService.listByAssetClassifyId(assetClassifyId);
        if (CollectionUtil.isEmpty(assetClassifyClientList)) {
            log.info("没有找到客户数据，不执行批量复核逻辑");
            return;
        }
        for (AssetClassifyClient assetClassifyClient : assetClassifyClientList) {
            assetClassifyClient.setReviewStatus(ProcessStatus.APPROVAL_PASS.name());
            assetClassifyClient.setReviewPassTime(LocalDateTime.now());
        }
        assetClassifyClientService.updateBatchById(assetClassifyClientList);
        AssetClassifyNodeRecord node = assetClassifyNodeRecordService.getSpecificNode(assetClassifyId, AssetClassifyBizNodeEnum.REVIEW);
        node.setEndTime(LocalDateTime.now());
        node.setNodeStatue(AssetClassifyStatusEnum.FINISH.name());
        assetClassifyNodeRecordService.updateById(node);
        assetClassifyLibVersionService.recordVersion(assetClassifyId, VersionTypeEnum.EFFECT, null, null, VersionTypeConstants.NORMAL);
    }
}
