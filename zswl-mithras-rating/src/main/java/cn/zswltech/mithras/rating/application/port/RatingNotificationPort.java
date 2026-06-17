package cn.zswltech.mithras.rating.application.port;

public interface RatingNotificationPort {

    void sendRatingAmountOverdueRemind(
            Long toId,
            Long ratingAmountId,
            String projectName,
            String clientName,
            Long projReviewId,
            String bizType);

    void sendRatingClientOverdueRemind(Long toId, Long ratingClientId, String clientName);
}
