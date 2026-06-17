import styles from './index.less'

const MultilineText = ({ content }) => {
  return (
    <p className={styles.wrap}>
      {`${content ?? ''}`?.split('\n').map((value, index) => (
        <div key={index}>{value}</div>
      ))}
    </p>
  )
}

export default MultilineText
