package cn.zswltech.mithras.collection.application.job;

import java.time.LocalDate;

public interface CollectionPenaltyInterestJobService {

    void penaltyInterestJobHandler();

    @Deprecated
    void penaltyInterestJobHandler2(LocalDate targetDate);

    void penaltyInterestJobHandler3(LocalDate targetDate);

    void overdueSnapshot();

    void updateClientPenaltyInterest();
}
