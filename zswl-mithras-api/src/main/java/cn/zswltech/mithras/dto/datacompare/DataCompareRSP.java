package cn.zswltech.mithras.dto.datacompare;

import cn.zswltech.mithras.dto.version.DiffValue;
import lombok.Data;

import java.util.Map;

/**
 * @create: 2022-08-02
 **/

@Data
public class DataCompareRSP {
    private Map<String, DiffValue> diffData;
}
