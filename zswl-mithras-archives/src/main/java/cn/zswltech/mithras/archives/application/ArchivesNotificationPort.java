package cn.zswltech.mithras.archives.application;

public interface ArchivesNotificationPort {

    void sendRemind(Long archivesId, String projName, Long projSponsorUserId);
}
