package cn.zswltech.mithras.application.orchestration.facade.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.customer.application.client.ZhongdengInfoApplicationService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoAddREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoModifyREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRSP;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRemoveREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.mapper.ZhongdengInfoMapper;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.model.ZhongdengInfo;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.externaldata.zhongdeng.application.ZhongdengInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 外部信息 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:58 PM
 */
@Service
public class ZhongdengInfoFacade implements ZhongdengInfoApplicationService {

    @Resource
    private ZhongdengInfoService zhongdengInfoService;

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> add(ZhongdengInfoAddREQ req) {
        zhongdengInfoService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = ZhongdengInfoMapper.class)
    public R<Void> modify(ZhongdengInfoModifyREQ req) {
        zhongdengInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<ZhongdengInfoRSP>> list(ExternalPageREQ req) {
        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
            return R.ok(PageR.of(Collections.emptyList(), 0));
        }
        Page<ZhongdengInfo> data = zhongdengInfoService.list(req);
        List<ZhongdengInfoRSP> list = BeanUtil.copyToList(data.getRecords(), ZhongdengInfoRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = "CLIENT", mapperClass = ZhongdengInfoMapper.class)
    public R<Void> remove(ZhongdengInfoRemoveREQ req) {
        zhongdengInfoService.remove(req);
        return R.ok();
    }

}
