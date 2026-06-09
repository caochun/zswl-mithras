package cn.zswltech.mithras.contract.overdue.application.collection;

/**
 * Generates overdue collection letters without exposing document rendering infrastructure.
 */
public interface CollectionLetterGenerator {

    void genLetter(Long actionId);
}
