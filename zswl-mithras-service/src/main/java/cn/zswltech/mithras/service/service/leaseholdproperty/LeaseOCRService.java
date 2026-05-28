package cn.zswltech.mithras.service.service.leaseholdproperty;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.LeaseFileNameComparisonREQ;

import java.util.List;

/**
 * @author yupengfei
 * @date 2024/5/15 11:52
 */
public interface LeaseOCRService {

    R<List<String>> fileNameComparison(LeaseFileNameComparisonREQ req);
}
