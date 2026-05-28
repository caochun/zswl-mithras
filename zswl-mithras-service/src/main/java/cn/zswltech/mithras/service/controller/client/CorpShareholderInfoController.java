package cn.zswltech.mithras.service.controller.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.client.CorpShareholderInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.shareholder.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.corp.CorpShareholderInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpShareHolderInfoService;
import cn.zswltech.mithras.service.service.lib.client.impl.CorpShareholderInfoLibServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.ObjectUtil.isNotNull;

/**
 * @author luyi
 */
@RestController
public class CorpShareholderInfoController implements CorpShareholderInfoApi {
    @Resource
    private CorpShareHolderInfoService shareHolderInfoService;
    @Resource
    private CorpShareholderInfoLibServiceImpl shareHolderInfoLibService;


    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(CorpShareholderInfoAddREQ req) {
        shareHolderInfoService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpShareholderInfoMapper.class)
    public R<Void> modify(CorpShareholderInfoModifyREQ req) {
        shareHolderInfoService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpShareholderInfoMapper.class)
    public R<Void> remove(CorpShareholderInfoRemoveREQ req) throws Exception {
        if (isNotNull(req.getId())) {
            //根据id来删除
            shareHolderInfoService.remove(req.getId());
        } else if (isNotBlank(req.getShareholderName()) && isNotNull(req.getClientId())) {
            shareHolderInfoService.removeByName(req.getClientId(), req.getShareholderName());
        } else {
            throw new MissingServletRequestParameterException("id and shareholderName", "");
        }
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<PageR<CorpShareholderInfoListRSP>> list(CorpShareholderInfoListREQ req) {
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            return R.ok(PageR.of(Collections.emptyList(), 0));
//        }
        if (StringUtils.isBlank(req.getVersion())) {
            Page<CorpShareholderInfo> data = null;
            SFunction<NewCorpShareholderInfo, ?> orderBy = NewCorpShareholderInfo::getCapitalPercent;
            if ("paidTotal".equals(req.getOrderField())) {
                orderBy = NewCorpShareholderInfo::getPaidTotal;
            } else if ("actualPaidTotal".equals(req.getOrderField())) {
                orderBy = NewCorpShareholderInfo::getActualPaidTotal;
            }
            data = shareHolderInfoService.list(req, orderBy, req.getOrderType());
            List<CorpShareholderInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpShareholderInfoListRSP.class);
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            SFunction<CorpShareholderInfoLib, ?> orderBy = CorpShareholderInfoLib::getCapitalPercent;
            if ("paidTotal".equals(req.getOrderField())) {
                orderBy = CorpShareholderInfoLib::getPaidTotal;
            } else if ("actualPaidTotal".equals(req.getOrderField())) {
                orderBy = CorpShareholderInfoLib::getActualPaidTotal;
            }
            return R.ok(shareHolderInfoLibService.list(req, orderBy, req.getOrderType()));
        }

    }
}
