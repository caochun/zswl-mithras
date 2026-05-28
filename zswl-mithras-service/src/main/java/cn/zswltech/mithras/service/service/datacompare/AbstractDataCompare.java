package cn.zswltech.mithras.service.service.datacompare;

import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.dto.version.DiffValueList;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @create: 2022-08-03
 **/

public abstract class AbstractDataCompare {
    public abstract Map<String, DiffValue> compareone(Long mainId);

    public abstract Map<String, DiffValue> compareone(Long mainId, Integer versionType);

    public abstract List<Map<String, DiffValue>> comparelist(Long mainId);

    public abstract List<Map<String, DiffValue>> comparelist(Long mainId, Integer versionType);


}

