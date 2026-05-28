import { getInputNumberAmountProps } from '@/utils'
import { Col, InputNumber, Row } from 'antd'
import styles from './index.less'
const AmountRange = ({ value = [], onChange }) => {
  const onFromChange = (val) => {
    const nextValue = [...value]
    nextValue[0] = val
    onChange(nextValue)
  }
  const onToChange = (val) => {
    const nextValue = [...value]
    nextValue[1] = val
    onChange(nextValue)
  }
  return (
    <div className={styles.wrap}>
      <Row>
        <Col span={11}>
          <InputNumber
            placeholder="请输入"
            {...getInputNumberAmountProps()}
            value={value[0]}
            onChange={onFromChange}
          />
        </Col>
        <Col span={2}>
          <div className={styles.line}>-</div>
        </Col>
        <Col span={11}>
          <InputNumber
            placeholder="请输入"
            {...getInputNumberAmountProps()}
            value={value[1]}
            onChange={onToChange}
          />
        </Col>
      </Row>
    </div>
  )
}

export default AmountRange
