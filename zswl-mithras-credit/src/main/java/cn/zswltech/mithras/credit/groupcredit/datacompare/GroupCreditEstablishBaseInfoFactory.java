package cn.zswltech.mithras.credit.groupcredit.datacompare;

import cn.zswltech.mithras.dto.groupcreditestablish.baseinfo.GroupCreditEstablishBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.mapper.GroupCreditEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfo;
import cn.zswltech.mithras.credit.groupcredit.establish.model.GroupCreditEstablishBaseInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.credit.groupcredit.establish.versioning.handler.impl.GroupCreditEstablishBaseInfoLibHandler;
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
        return new DefaultDataCompare<GroupCreditEstablishBaseInfo, GroupCreditEstablishBaseInfoLib, GroupCreditEstablishBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper, "GROUP_CREDIT_ESTABLISH", version);
    }
}
