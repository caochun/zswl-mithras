import messageNotificationApi from '@/api/message/messageNotification'

export default {
  postMessageList: messageNotificationApi.postMessageList,
  postReadMessage: messageNotificationApi.postReadMessage,
  postReadAllMessage: messageNotificationApi.postReadAllMessage,
}
