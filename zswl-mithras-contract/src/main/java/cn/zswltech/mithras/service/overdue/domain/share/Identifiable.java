package cn.zswltech.mithras.service.overdue.domain.share;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/14 14:50
 */
public interface Identifiable<ID extends Identifier> {
    ID getBizId();

    /**
     * 获取id的string
     *
     * @return
     */
    String bizIdString();
}