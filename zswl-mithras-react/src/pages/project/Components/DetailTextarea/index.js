import styles from './index.less'
const DetailTextarea = ({ content }) => {
  return (
    <p className={styles.wrap}>
      {`${content ?? ''}`?.split('\n').map((v, i) => (
        <div key={i}>{v}</div>
      ))}
    </p>
  )
}
export default DetailTextarea
