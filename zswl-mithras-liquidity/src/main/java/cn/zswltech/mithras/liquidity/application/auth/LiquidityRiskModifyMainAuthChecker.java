package cn.zswltech.mithras.liquidity.application.auth;

import cn.zswltech.mithras.foundation.auth.checker.IDataAuthChecker;
import cn.zswltech.mithras.foundation.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserDeptCodeResolver;
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
    private CurrentUserDeptCodeResolver currentUserDeptCodeResolver;

    private final static String ZJGLB = "ZJGLB";


    /**
     * 只有资金部门的用户才能操作数据设置
     **/
    @Override
    public boolean check(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId, Object[] args) {
        List<String> userDeptCodes = currentUserDeptCodeResolver.currentUserDeptCodes();
        for(String deptCode : userDeptCodes){
            if(ZJGLB.equals(deptCode)){
                return true;
            }
        }
        throw new MithrasException("仅资金部门全员可编辑!");
    }

    @Override
    public boolean checkBatch(DataAuthBusinessModule businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIds, Object[] args) {
        return false;
    }
}
