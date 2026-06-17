package cn.zswltech.mithras.ftp.newftp.application.port;

import cn.zswltech.mithras.ftp.newftp.application.port.model.DirectFinancingCostSample;
import cn.zswltech.mithras.ftp.newftp.application.port.model.GuaranteeCostSample;
import cn.zswltech.mithras.ftp.newftp.application.port.model.IndirectFinancingCostSample;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface NewFtpFundDataPort {

    List<IndirectFinancingCostSample> listIndirectFinancingCostSamples(LocalDate beginDate, LocalDate endDate);

    List<DirectFinancingCostSample> listDirectFinancingCostSamples(LocalDate beginDate, LocalDate endDate);

    List<GuaranteeCostSample> listGuaranteeCostSamples(LocalDate month);

    boolean existsPledge(Set<Long> contractIds);

    boolean existsDirectPledge(Set<Long> contractIds);
}
