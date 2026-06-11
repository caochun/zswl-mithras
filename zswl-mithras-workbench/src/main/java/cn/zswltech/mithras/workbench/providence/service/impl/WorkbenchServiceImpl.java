package cn.zswltech.mithras.workbench.providence.service.impl;

import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.foundation.cache.RedisHelper;
import cn.zswltech.mithras.workbench.providence.req.WorkbenchConfig;
import cn.zswltech.mithras.workbench.providence.service.WorkbenchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2025/1/10 15:14
 */
@Service
@Slf4j
public class WorkbenchServiceImpl implements WorkbenchService {

    @Resource
    private RedisHelper redisHelper;

    @Override
    public void sotre(WorkbenchConfig workbenchConfig) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        redisHelper.putString("workbenchConfig:" + loginInfo.getAccount(), workbenchConfig.getConfig());
    }

    @Override
    public String readWorkbench() {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (redisHelper.exists("workbenchConfig:" + loginInfo.getAccount())) {
            return redisHelper.getString("workbenchConfig:" + loginInfo.getAccount());
        }
        return null;
    }


}
