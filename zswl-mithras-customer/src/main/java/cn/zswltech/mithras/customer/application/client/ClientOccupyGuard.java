package cn.zswltech.mithras.customer.application.client;

/**
 * Checks whether the current operation can use a client occupied by another user.
 */
public interface ClientOccupyGuard {

    void checkClientOccupy(Long clientId);

    void checkClientOccupy(Long clientId, Long userId);
}
