package cn.zswltech.mithras.service.overdue.domain.share;

/**
 * @description: 聚合根的Marker接口
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:48
 */
public interface Aggregate<ID extends Identifier> extends Entity<ID> {

    void createNewId(ID id);

    Long getLockVersion();
}
