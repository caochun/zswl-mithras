import { observer } from '@zswl/admin'

import { Row, Col } from 'antd'
import { Form } from '@zswl/components'
import styles from '../style.less'
import RowSpan from '../RowSpan'
import { options } from '@/utils'

const { yesOrNoString: yesOrNo } = options

/**
 * 区域经济情况组件
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {boolean} props.required - 是否必填
 * @param {string} props.namePrefix - 表单字段名前缀，默认为 'BUS'
 */
const RegionalEconomicSituation = observer(({ editable, required = true, namePrefix }) => {
  return (
    <>
      <div className={styles.titleRow}>区域经济情况</div>
      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人所在区域GDP、一般公共预算收入是否较上一年度下滑超过20%
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            label=""
            name={`${namePrefix}_C_9_01_01`}
            required={required}
            rules={[{ required }]}
          >
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>

          <Form.Item dependencies={[`${namePrefix}_C_9_01_01`]}>
            {({ getFieldValue }) => {
              const isRequired = getFieldValue(`${namePrefix}_C_9_01_01`) === '1'
              return (
                <Form.Item
                  label="情况说明"
                  name={`${namePrefix}_C_9_01_02`}
                  required={isRequired}
                  rules={[{ required: isRequired }]}
                >
                  <TextAreaReadOnly onlyRead={!editable} placeholder="请填写情况说明" />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Col>
      </Row>

      <Row className={styles.row}>
        <Col span={8} className={styles.subTitle}>
          承租人所在区域内是否有融资主体出现违约行为
        </Col>
        <Col span={16} className={styles.right}>
          <Form.Item
            label=""
            name={`${namePrefix}_C_9_02_01`}
            required={required}
            rules={[{ required }]}
          >
            <RadioReadOnly onlyRead={!editable} options={yesOrNo} />
          </Form.Item>

          <Form.Item dependencies={[`${namePrefix}_C_9_02_01`]}>
            {({ getFieldValue }) => {
              const isRequired = getFieldValue(`${namePrefix}_C_9_02_01`) === '1'
              return (
                <Form.Item
                  label="情况说明"
                  name={`${namePrefix}_C_9_02_02`}
                  required={isRequired}
                  rules={[{ required: isRequired }]}
                >
                  <TextAreaReadOnly onlyRead={!editable} placeholder="请填写情况说明" />
                </Form.Item>
              )
            }}
          </Form.Item>
        </Col>
      </Row>

      <RowSpan subTitle={'区域经济情况补充说明'} name={`${namePrefix}_C_9_03`} required={false}>
        <TextAreaReadOnly onlyRead={!editable} placeholder="请填写补充说明" />
      </RowSpan>
    </>
  )
})

export default RegionalEconomicSituation
