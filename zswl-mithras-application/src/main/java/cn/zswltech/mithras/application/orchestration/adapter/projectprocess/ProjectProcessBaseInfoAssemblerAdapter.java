package cn.zswltech.mithras.application.orchestration.adapter.projectprocess;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.projectprocess.application.support.ProjectProcessBaseInfoAssembler;
import cn.zswltech.mithras.projectprocess.convert.projestablish.ProjEstablishBaseInfoConverter;
import cn.zswltech.mithras.application.orchestration.projectprocess.projestablish.ProjEstablishBaseInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProjectProcessBaseInfoAssemblerAdapter implements ProjectProcessBaseInfoAssembler {

    @Resource
    private ProjEstablishBaseInfoConverter baseInfoConverter;
    @Resource
    private ProjEstablishBaseInfoService baseInfoService;

    @Override
    public ProjEstablishBaseInfoListRSP establishLib2Rsp(ProjEstablishBaseInfoLib lib) {
        ProjEstablishBaseInfoListRSP rsp = baseInfoConverter.entityToDetailRSP(lib);
        baseInfoService.join(rsp);
        rsp.setId(lib.getOriginId());
        return rsp;
    }
}
