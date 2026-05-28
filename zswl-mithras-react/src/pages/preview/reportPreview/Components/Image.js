import { Image } from 'antd'
import styles from './index.less'
const ImagePreview = ({ url }) => (
  <div className={styles.wrap}>
    <Image
      preview={{
        visible: true,
      }}
      width={0}
      src={url}
    />
  </div>
)

export default ImagePreview
