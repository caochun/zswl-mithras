import styles from './StarDom.less'
const StarDom = ({ name }) => {
  return (
    <span className={styles.colorsWrap}>
      <span className={styles.colors}>*</span>
      <span>{name}</span>
    </span>
  )
}

export default StarDom
