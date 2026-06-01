package cn.zswltech.mithras.service.repository;

import java.util.Collections;
import java.util.List;

/**
 * Exposes request metadata needed by retry and withdraw flows without binding
 * the platform API registry to a concrete business handler implementation.
 */
public interface PlatformApiRequestInspector {

    PlatformApiEnum platformApi();

    default Object getReqFromString(String reqString) {
        return null;
    }

    default String getSituationDescription(String reqString) {
        return null;
    }

    default List<String> getBillNo(String reqString) {
        return Collections.emptyList();
    }
}
