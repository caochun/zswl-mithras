
import { observer } from '@zswl/admin'
import { Row, Col } from 'antd'
import styles from '../style.less'
import { tableRequired, columns2 } from '../Utils'
import { Form } from '@zswl/components'

const FinancialConditionAnalysis = observer(
  ({ editable, nameIndex, attributionList, required = true }) => {
    // 普通的 归属于 LR_C_2_03_X
    // guarantor 归属于 LR_C_3_05_X
    const getName = (number) => {
      const name =
        attributionList === 'guarantor' ? [nameIndex, `LR_C_3_05_${number}`] : `LR_C_2_03_${number}`
      return name
    }
    return (
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          重点财务指标
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            label={''}
            name={getName('03')}
            className={styles.tableItem}
            required
            rules={required && [tableRequired()]}
          >
            <FormTable onlyRead={!editable} columns={columns2} canAddDelete={false} />
          </Form.Item>
        </Col>
      </Row>
    )
  }
)

export default FinancialConditionAnalysis
