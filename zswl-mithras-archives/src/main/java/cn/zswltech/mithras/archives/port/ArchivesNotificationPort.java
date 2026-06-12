package cn.zswltech.mithras.archives.port;

public interface ArchivesNotificationPort {

    void sendRemind(Long archivesId, String projName, Long projSponsorUserId);
}
