package cn.zswltech.mithras.foundation.datacompare;

import java.util.List;

/**
 * @create: 2022-08-03
 **/
public interface EditdataCompareFactory {
    AbstractDataCompare createCompare(List rsps, String version);
}
