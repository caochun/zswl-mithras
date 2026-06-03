package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayAddSubAuthChecker;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthMoneyManagerRule;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthProcessRule;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Component
public class FundReceiptRepayCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private FundReceiptRepayAddSubAuthChecker fundReceiptRepayAddSubAuthChecker;
    @Resource
    private FundReceiptRepayAuthProcessRule fundReceiptRepayAuthProcessRule;
    @Resource
    private FundReceiptRepayAuthMoneyManagerRule fundReceiptRepayAuthMoneyManagerRule;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.FUND_RECEIPT_REPAY.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        fundReceiptRepayAddSubAuthChecker.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, FundReceiptRepayBaseInfoMapper.class, mainId, null);
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materialsList = materialsListService.getById(fileId);
        if (Objects.isNull(materialsList)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, materialsList.getBelongId());
        fundReceiptRepayAuthProcessRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, materialsList.getBelongId());
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
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, mainId);
        fundReceiptRepayAuthProcessRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, mainId);
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
