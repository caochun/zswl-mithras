package cn.zswltech.mithras.service.controller.collection.checker;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

///**
// * @description:
// * @author: zhaozhengkang
// * @date: 2022/8/25 11:32
// */
//@Component
//public class CollectionWriteOffAuthChecker implements IDataAuthChecker {
//    @Resource
//    private SysUserService sysUserService;
//    @Override
//    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
//        List<String> jobs = sysUserService.queryUserJobList(AccountUtil.getLoginInfo().getId());
//        boolean canWriteOff = false;
//        for (String job : jobs) {
//            if(job.equals(JobEnum.financialmanager.name()) || job.equals(JobEnum.cashier.name())){
//                canWriteOff = true;
//            }
//        }
//        if(!canWriteOff){
//            throw new MithrasException("财务经理或出纳才可执行操作");
//        }
//        return true;
//    }
//
//    @Override
//    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
//        return false;
//    }
//}
