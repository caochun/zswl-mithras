package cn.zswltech.mithras.projectprocess.service.datacompare;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projestablish.ProjEstablishBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.service.lib.projestablish.handler.impl.ProjEstablishBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-03
 **/
@Service("projEstablishBaseInfo")
public class ProjEstablishBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private ProjEstablishBaseInfoLibMapper libMapper;
    @Resource
    private ProjEstablishBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<ProjEstablishBaseInfo,ProjEstablishBaseInfoLib, ProjEstablishBaseInfoListRSP>(rsps, libMapper, handler, commonVersionMapper,"PROJ_ESTABLISH", version);
    }
}
