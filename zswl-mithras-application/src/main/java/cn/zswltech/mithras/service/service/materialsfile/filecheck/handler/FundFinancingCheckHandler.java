package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;


import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;


@Component
public class FundFinancingCheckHandler extends FileModuleCheck {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private FundCreditService fundCreditService;

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.FUND_FINANCING.name();
    }

    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理可以上传资料!");
        }
    }



    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能删除!");
        }
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
        if (!sysUserService.currentUserIsSpecificJob("moneymanager")) {
            throw new MithrasException("只有资金经理才能删除!");
        }
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
//        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
//            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
//        }
    }

    @Override
    public void checkDownload(String moduleKey, Long fileId, List<Long> fileIds) {
        if (!sysUserService.currentUserIsSpecificDept("JHCWB","ZJGLB") && !sysUserService.adminAuth()) {
            throw new MithrasException("只有计划财务部及资金管理部员工可以操作");
        }
    }

}
