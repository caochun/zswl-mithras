package cn.zswltech.mithras.service.service;

/**
 * Generates overdue collection letters without exposing document rendering infrastructure.
 */
public interface CollectionLetterGenerator {

    void genLetter(Long actionId);
}
