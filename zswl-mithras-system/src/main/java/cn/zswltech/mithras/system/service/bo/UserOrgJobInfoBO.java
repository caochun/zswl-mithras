package cn.zswltech.mithras.system.service.bo;

import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2024/9/25
 * @description
 */
@Data
public class UserOrgJobInfoBO {
    private boolean bizDept;
    private Map<String, List<OrgDO>> orgJobMap;
}
