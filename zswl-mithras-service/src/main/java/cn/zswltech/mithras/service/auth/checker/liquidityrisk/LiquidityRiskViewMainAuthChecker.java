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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jackerhe
 * @date 2022/8/31
 * @description
 */
@Component
public class LiquidityRiskViewMainAuthChecker implements IDataAuthChecker {

    @Resource
    private SysUserService sysUserService;

    private final static Set<String> orgSet;

    static {
        orgSet = new HashSet<>();
        orgSet.add("ZJGLB");
        orgSet.add("JHCWB");
    }

    /**
     * 资金部门和财务部的用户，都有权查看流动性风险信息
     **/
    @Override
    public boolean check(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        List<OrgDO> userDeptList = sysUserService.getUserDeptList();
        for(OrgDO orgDO : userDeptList){
            if(orgSet.contains(orgDO.getCode())){
                return true;
            }
        }
        throw new MithrasException("仅资金部门和财务部门全员可查看!");
    }

    @Override
    public boolean checkBatch(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
