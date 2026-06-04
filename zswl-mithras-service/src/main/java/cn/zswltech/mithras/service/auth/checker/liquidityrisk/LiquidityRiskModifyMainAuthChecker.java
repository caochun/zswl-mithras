package cn.zswltech.mithras.service.auth.checker.liquidityrisk;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @author jackerhe
 * @date 2022/8/31
 * @description
 */
@Component
public class LiquidityRiskModifyMainAuthChecker implements IDataAuthChecker {

    @Resource
    private SysUserService sysUserService;

    private final static String ZJGLB = "ZJGLB";


    /**
     * 只有资金部门的用户才能操作数据设置
     **/
    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        List<OrgDO> userDeptList = sysUserService.getUserDeptList();
        for(OrgDO orgDO : userDeptList){
            if(ZJGLB.equals(orgDO.getCode())){
                return true;
            }
        }
        throw new MithrasException("仅资金部门全员可编辑!");
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
