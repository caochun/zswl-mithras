package cn.zswltech.mithras.projectprocess.datacompare;

import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoDetailRSP;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @create: 2022-08-03
 **/
@Service("projReviewBaseInfo")
public class ProjReviewBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private ProjReviewBaseInfoLibMapper libMapper;
    @Resource
    private ProjReviewBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        /**
         * 为了不影响其他代码而特殊处理。以下字段在detail接口null->null，在(ProjReviewBaseInfoDetailRSP lib2Rsp(o))时null -> Collections.emptyList()
         * 导致对比结果出错，就此特殊处理
         */
        ((List<ProjReviewBaseInfoDetailRSP>) rsps).forEach(o ->{
            o.setDebtorInfo(Optional.ofNullable(o.getDebtorInfo())
                    .orElse(Collections.emptyList()));
            o.setGuaranteeInfo(Optional.ofNullable(o.getGuaranteeInfo())
                    .orElse(Collections.emptyList()));
            o.setLesseeInfo(Optional.ofNullable(o.getLesseeInfo())
                    .orElse(Collections.emptyList()));
            o.setMortgagorInfo(Optional.ofNullable(o.getMortgagorInfo())
                    .orElse(Collections.emptyList()));
            o.setPledgorInfo(Optional.ofNullable(o.getPledgorInfo())
                    .orElse(Collections.emptyList()));
            o.setLeaseTypes(Optional.ofNullable(o.getLeaseTypes())
                    .orElse(Collections.emptyList()));
            o.setFactoringTypes(Optional.ofNullable(o.getFactoringTypes())
                    .orElse(Collections.emptyList()));
            o.setProjCosponsorUserIds(Optional.ofNullable(o.getProjCosponsorUserIds())
                    .orElse(Collections.emptyList()));
        });
        return new DefaultDataCompare<ProjReviewBaseInfo, ProjReviewBaseInfoLib, ProjReviewBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,"PROJ_REVIEW", version);
    }
}
