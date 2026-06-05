package cn.zswltech.mithras.projectprocess.service;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;

public interface ProjectProcessBaseInfoAssembler {

    ProjEstablishBaseInfoListRSP establishLib2Rsp(ProjEstablishBaseInfoLib lib);
}
