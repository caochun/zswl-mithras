package cn.zswltech.mithras.projectprocess.job.service;

import java.util.Collection;
import java.util.Map;

public interface ProjectRiskControlIndustryPort {

    Map<Long, String> mapRiskControlIndustryClassify(Collection<Long> clientIds);
}
