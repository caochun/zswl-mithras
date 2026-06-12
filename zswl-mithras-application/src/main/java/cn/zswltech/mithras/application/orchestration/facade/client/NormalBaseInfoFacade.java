package cn.zswltech.mithras.application.orchestration.facade.client;

import cn.zswltech.mithras.customer.application.client.NormalBaseInfoApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoAddREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.client.normal.NormalBaseInfoModifyREQ;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.customer.application.client.auth.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientModifyMainAuthCheckerNew;
import cn.zswltech.mithras.customer.application.client.auth.ClientViewMainAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.basedata.persistence.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.application.client.NormalBaseInfoService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.client.ClientService;

import javax.annotation.Resource;

import static cn.zswltech.mithras.foundation.constant.ResultMsg.ID_CARD_ERROR;
import static cn.zswltech.mithras.foundation.util.Const.CERT_ID_CARD_CODE;
import static cn.zswltech.mithras.foundation.util.Util.checkIDCard;
import org.springframework.stereotype.Service;

/**
 * @author junke
 */
@Service
public class NormalBaseInfoFacade implements NormalBaseInfoApplicationService {

    @Resource
    private NormalBaseInfoService baseInfoService;
    @Resource
    private ClientService clientService;
    @Resource
    private AddressDictionaryMapper addressDictionaryMapper;

    @Override
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = "CLIENT")
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
    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientModifyMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<Void> modify(NormalBaseInfoModifyREQ req) {
        baseInfoService.modify(req);
        return R.ok();
    }


    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = "CLIENT")
    public R<NormalBaseInfoDetailRSP> detail(NormalBaseInfoDetailREQ req) {
        NormalBaseInfoDetailRSP rsp = baseInfoService.detail(req.getClientId(), req.getVersion());
        return R.ok(rsp);
    }
}
