package cn.zswltech.mithras.service.service;

import java.util.List;

/**
 * Sends workflow copy notifications for business modules.
 */
public interface ProcessCcNotifier {

    void cc(String processInstanceId, List<Long> userIds);
}
