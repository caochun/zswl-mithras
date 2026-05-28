package cn.zswltech.mithras.dto.kpi;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author dingqi
 * @date 2023/6/26
 * @description
 */
@Data
public class KpiProjectDistributionImportREQ {
    private MultipartFile file;
}
