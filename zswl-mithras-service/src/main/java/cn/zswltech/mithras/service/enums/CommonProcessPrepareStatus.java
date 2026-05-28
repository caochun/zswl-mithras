package cn.zswltech.mithras.service.enums;

import lombok.Getter;

/**
 * @author luyi
 */
@Getter
public enum CommonProcessPrepareStatus {
    PEND_COMMIT, COMMITTED, CLOSED , AUTO_COMMITTED ,WAITING_PEND
}
