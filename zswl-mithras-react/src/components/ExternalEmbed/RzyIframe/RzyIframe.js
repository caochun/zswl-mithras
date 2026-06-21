import { rzyLink } from '@/utils/domains/rzy/RzyConfig'
import styles from './index.less'

const ExternalEmbedRzyIframe = ({ title }) => {
  const path = rzyLink[title]?.url
  return <iframe src={path} className={styles.iframe} id={title}></iframe>
}

export default ExternalEmbedRzyIframe
