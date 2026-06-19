import styles from './index.less'

const VideoPreview = ({ url }) => (
  <div className={styles.wrap}>
    <video src={url} controls="controls">
      您的浏览器不支持 video 标签
    </video>
  </div>
)

export default VideoPreview
