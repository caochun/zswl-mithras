import { rzyLink } from '@/utils/rzyConfig'
import styles from './index.less'

const Index = ({ title }) => {
  const path = rzyLink[title]?.url
  return <iframe src={path} className={styles.iframe} id={title}></iframe>
}

export default Index
