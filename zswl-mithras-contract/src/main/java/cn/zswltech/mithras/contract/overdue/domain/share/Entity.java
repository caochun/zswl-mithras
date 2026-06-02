package cn.zswltech.mithras.contract.overdue.domain.share;


import java.io.Serializable;

/**
 * @description: 实体类的Marker接口
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:49
 */

public interface Entity<T extends Identifier> extends Identifiable<T>, Serializable {

    /**
     * Entities compare by identity, not by attributes.
     *
     * @param other The other entity.
     * @return true if the identities are the same, regardless of other attributes.
     */
    boolean sameIdentityAs(T other);
}