package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.infrastructure.persistence.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.credit.application.groupcredit.establish.handler.impl.GroupCreditEstablishBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("groupCreditEstablishBaseInfo")
public class GroupCreditEstablishBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private GroupCreditEstablishBaseInfoLibMapper libMapper;
    @Resource
    private GroupCreditEstablishBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<GroupCreditEstablishBaseInfo, GroupCreditEstablishBaseInfoLib, GroupCreditEstablishBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.GROUP_CREDIT_ESTABLISH.name(), version);
    }
}
