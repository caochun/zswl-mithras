package cn.zswltech.mithras.foundation.port;

import java.util.List;
import java.util.Objects;

public interface CurrentUserJobResolver {

    List<String> queryUserJobList(Long userId);

    boolean currentUserIsSpecificJob(String... jobNames);

    default boolean userIsSpecificJob(Long userId, String... jobNames) {
        List<String> jobs = queryUserJobList(userId);
        if (jobs == null || jobs.isEmpty() || jobNames == null || jobNames.length == 0) {
            return false;
        }
        for (String jobName : jobNames) {
            if (Objects.nonNull(jobName) && jobs.contains(jobName)) {
                return true;
            }
        }
        return false;
    }
}
