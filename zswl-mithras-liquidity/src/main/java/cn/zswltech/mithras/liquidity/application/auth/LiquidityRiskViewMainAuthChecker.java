package cn.zswltech.mithras.liquidity.application.auth;

import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserDeptCodeResolver;
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
    private CurrentUserDeptCodeResolver currentUserDeptCodeResolver;

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
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        List<String> userDeptCodes = currentUserDeptCodeResolver.currentUserDeptCodes();
        for(String deptCode : userDeptCodes){
            if(orgSet.contains(deptCode)){
                return true;
            }
        }
        throw new MithrasException("仅资金部门和财务部门全员可查看!");
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
