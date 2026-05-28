import { observer } from '@zswl/admin'
import styles from '../style.less'
import { options } from '@/utils'
import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import { RadioReadOnly, TextAreaReadOnly } from '@/components/Form/FormRead'
import RowSpan from '../RowSpan'

const { yesOrNoString, templateStatus } = options

function Index(props, ref) {
  const { editable, reportTemplateType } = props
  const isV2 = reportTemplateType === 'V2'

  return (
    <div className={styles.contentBox}>
      <div className={styles.titleRow}>{isV2 ? '风险信号及重大事项' : '检查总结'}</div>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          风险信号及重大事项
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item label="项目是否存在风险信号及重大事项" name="NP_S_1_01_01">
            <RadioReadOnly onlyRead={!editable} options={yesOrNoString} />
          </Form.Item>
          <Form.Item dependencies={['NP_S_1_01_01']}>
            {({ getFieldValue }) => {
              const required = getFieldValue('NP_S_1_01_01') === '1'
              return (
                <Form.Item label="风险防范措施" name="NP_S_1_01_02" rules={[{ required }]}>
                  <TextAreaReadOnly onlyRead={!editable} />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Col>
      </Row>

      {isV2 ? (
        <>
          <div className={styles.titleRow}>检查结论及后续管理计划</div>
          <RowSpan subTitle={'检查结论及后续管理计划'} name="NP_S_2_01">
            <TextAreaReadOnly onlyRead={!editable} />
          </RowSpan>
        </>
      ) : (
        <Row className={styles.row}>
          <Col span={8} className={styles.subTitle}>
            检查分析与检查结果
          </Col>

          <Col span={16} className={styles.right}>
            <Form.Item name="NP_S_1_02">
              <TextAreaReadOnly onlyRead={!editable} />
            </Form.Item>
          </Col>
        </Row>
      )}
    </div>
  )
}

export default observer(Index)
