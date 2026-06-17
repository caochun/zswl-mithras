package cn.zswltech.mithras.afterlease.application;

public interface PaymentNoticeRenderDataPort {
    PaymentNoticeRenderData load(Long collectionId, Long bankId);
}
