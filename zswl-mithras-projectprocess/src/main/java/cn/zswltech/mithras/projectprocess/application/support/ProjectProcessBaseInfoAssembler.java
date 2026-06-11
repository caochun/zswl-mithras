package cn.zswltech.mithras.projectprocess.application.support;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;

public interface ProjectProcessBaseInfoAssembler {

    ProjEstablishBaseInfoListRSP establishLib2Rsp(ProjEstablishBaseInfoLib lib);
}
