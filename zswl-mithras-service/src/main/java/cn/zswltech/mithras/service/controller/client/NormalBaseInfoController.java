package cn.zswltech.mithras.service.controller.client;

import cn.zswltech.mithras.api.client.NormalBaseInfoApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoModifyREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.NormalBaseInfoService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.zswltech.mithras.service.constant.ResultMsg.ID_CARD_ERROR;
import static cn.zswltech.mithras.service.others.Const.CERT_ID_CARD_CODE;
import static cn.zswltech.mithras.service.others.Util.checkIDCard;

/**
 * @author junke
 */
@RestController
public class NormalBaseInfoController implements NormalBaseInfoApi {

    @Resource
    private NormalBaseInfoService baseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(NormalBaseInfoAddREQ req) {
        String certType = req.getCertType();
        //身份证
        if (certType.equals(CERT_ID_CARD_CODE)) {
            if (!checkIDCard(req.getCertNumber())) {
                throw new MithrasException(ID_CARD_ERROR);
            }
        }
        baseInfoService.add(req);
        return R.ok();
    }

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientModifyMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> modify(NormalBaseInfoModifyREQ req) {
        baseInfoService.modify(req);
        return R.ok();
    }


    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<NormalBaseInfoDetailRSP> detail(NormalBaseInfoDetailREQ req) {
        NormalBaseInfoDetailRSP rsp = baseInfoService.detail(req.getClientId(), req.getVersion());
        return R.ok(rsp);
    }
}
