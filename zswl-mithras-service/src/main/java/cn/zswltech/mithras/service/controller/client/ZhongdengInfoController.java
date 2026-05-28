package cn.zswltech.mithras.service.controller.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.client.ZhongdengInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoAddREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoModifyREQ;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRSP;
import cn.zswltech.mithras.dto.client.external.zhongdeng.ZhongdengInfoRemoveREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.corp.ZhongdengInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.ZhongdengInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.tyc.ZhongdengInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 外部信息 中登网
 *
 * @author wangchuanhao
 * @date 2022/6/21 2:58 PM
 */
@RestController
public class ZhongdengInfoController implements ZhongdengInfoApi {

    @Resource
    private ZhongdengInfoService zhongdengInfoService;

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(ZhongdengInfoAddREQ req) {
        zhongdengInfoService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = ZhongdengInfoMapper.class)
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
    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = ZhongdengInfoMapper.class)
    public R<Void> remove(ZhongdengInfoRemoveREQ req) {
        zhongdengInfoService.remove(req);
        return R.ok();
    }

}
