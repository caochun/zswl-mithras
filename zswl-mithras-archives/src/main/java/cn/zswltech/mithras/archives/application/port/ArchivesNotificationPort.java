package cn.zswltech.mithras.archives.application.port;

public interface ArchivesNotificationPort {

    void sendRemind(Long archivesId, String projName, Long projSponsorUserId);
}
