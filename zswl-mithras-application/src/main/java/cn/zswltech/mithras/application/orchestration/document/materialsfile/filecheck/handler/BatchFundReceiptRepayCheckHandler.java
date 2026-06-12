package cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.rule.BatchFundReceiptRepayAuthProcessRule;
import cn.zswltech.mithras.fund.application.auth.receiptrepay.rule.FundReceiptRepayAuthMoneyManagerRule;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.filecheck.FileModuleCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Component
public class BatchFundReceiptRepayCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private BatchFundReceiptRepayAuthProcessRule batchFundReceiptRepayAuthProcessRule;
    @Resource
    private FundReceiptRepayAuthMoneyManagerRule fundReceiptRepayAuthMoneyManagerRule;


    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY, mainId);
        batchFundReceiptRepayAuthProcessRule.check(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY, mainId);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY, materialsList.getBelongId());
        batchFundReceiptRepayAuthProcessRule.check(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY, materialsList.getBelongId());
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        List<MaterialsList> materialsListList = materialsListService.getByIds(fileIds);
        if (CollectionUtils.isEmpty(materialsListList)) {
            throw new MithrasException("可删除文件为空");
        }
        Long mainIdCount = materialsListList.stream().map(MaterialsList::getBelongId).distinct().count();
        if (mainIdCount > 1) {
            throw new MithrasException("同时只可删除一条资金信息的文件数据");
        }
        Long mainId = materialsListList.stream().map(MaterialsList::getBelongId).findFirst().orElse(null);
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY, mainId);
        batchFundReceiptRepayAuthProcessRule.check(BusinessModuleEnum.BATCH_FUND_RECEIPT_REPAY, mainId);
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
//        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
//            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
//        }
    }

    @Override
    public void checkDownload(String moduleKey, Long fileId, List<Long> fileIds) {
//        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
//            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
//        }
    }

}
