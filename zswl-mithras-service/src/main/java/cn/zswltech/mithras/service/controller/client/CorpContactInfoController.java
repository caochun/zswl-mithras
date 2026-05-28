package cn.zswltech.mithras.service.controller.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.zswltech.mithras.api.client.CorpContactInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.contactinfo.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifySubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientRemoveSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.corp.CorpContactInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpContactInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpContactInfoService;
import cn.zswltech.mithras.service.service.lib.client.CorpContactInfoLibService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.zswltech.mithras.service.constant.ResultMsg.ID_CARD_ERROR;
import static cn.zswltech.mithras.service.others.Const.CERT_ID_CARD_CODE;
import static cn.zswltech.mithras.service.others.Util.checkIDCard;

/**
 * @author luyi
 */
@RestController
public class CorpContactInfoController implements CorpContactInfoApi {

    @Resource
    private CorpContactInfoService contactInfoService;
    @Resource
    private CorpContactInfoLibService contactInfoLibService;

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(CorpContactAddInfoREQ req) {
        if (isNotBlank(req.getCertNumber()) && CERT_ID_CARD_CODE.equals(req.getCertType())) {
            if (!checkIDCard(req.getCertNumber())) {
                throw new MithrasException(ID_CARD_ERROR);
            }
        }
        contactInfoService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientModifySubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpContactInfoMapper.class)
    public R<Void> modify(CorpContactInfoModifyREQ req) {
        if (CERT_ID_CARD_CODE.equals(req.getCertType())) {
            if (!checkIDCard(req.getCertNumber())) {
                throw new MithrasException(ID_CARD_ERROR);
            }
        }
        if (!Validator.isEmail(req.getMail())) {
            throw new MithrasException("邮箱格式有误或邮箱号为空");
        }
        contactInfoService.modify(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<PageR<CorpContactInfoListRSP>> list(CorpContactInfoListREQ req) {
//        if(!SpringContextHolder.getBean(ClientService.class).checkClientAuth(req.getClientId(), null)){
//            return R.ok();
//        }
        if (StringUtils.isBlank(req.getVersion())) {
            Page<CorpContactInfo> data = contactInfoService.list(req);
            List<CorpContactInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpContactInfoListRSP.class);
            return R.ok(PageR.of(list, data.getTotal(),
                    data.getPages(),
                    data.getCurrent(),
                    data.getSize()));
        } else {
            return R.ok(contactInfoLibService.list(req));
        }
    }

    @Override
    public R<PageR<CorpContactInfoListRSP>> listOld(@Valid CorpContactInfoListREQ req) {
        Page<CorpContactInfoLib> data = contactInfoService.listOld(req);
        List<CorpContactInfoListRSP> list = BeanUtil.copyToList(data.getRecords(), CorpContactInfoListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ClientRemoveSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT, mapperClass = CorpContactInfoMapper.class)
    public R<Void> remove(CorpContactInfoRemoveREQ req) {
        contactInfoService.remove(req.getId());
        return R.ok();
    }
}
