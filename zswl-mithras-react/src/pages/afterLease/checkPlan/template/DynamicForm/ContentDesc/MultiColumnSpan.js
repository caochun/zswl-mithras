import { observer } from '@zswl/admin'
import styles from './style.less'
import { Col, Row, Tooltip } from 'antd'
import { Form } from '@zswl/components'
import React from 'react'
import { QuestionCircleOutlined } from '@ant-design/icons'

/**
 * 多列表单组件
 * @param {string} subTitle - 子标题
 * @param {boolean} required - 是否必填，默认为 true
 * @param {Array} columns - 行配置数组，每个元素包含 { columns: [{ children, name, span, rules }] }
 * @param {string} tooltip - 提示信息
 */
const MultiColumnSpan = ({ subTitle, required = true, columns = [], tooltip }) => {
  return (
    <Row className={styles.row}>
      <Col span={8} className={styles.subTitle}>
        {subTitle}
        {tooltip && (
          <Tooltip className={styles.tooltip} title={tooltip}>
            <QuestionCircleOutlined />
          </Tooltip>
        )}
      </Col>
      <Col span={16} className={styles.right}>
        {columns.map((row, rowIndex) => {
          const { columns: rowColumns = [] } = row
          return (
            <Row
              key={rowIndex}
              gutter={16}
              style={{ marginBottom: rowIndex < columns.length - 1 ? 16 : 0 }}
            >
              {rowColumns.map((column, colIndex) => {
                const { children, name, span = 12, rules = [] } = column
                return (
                  <Col key={colIndex} span={span}>
                    <Form.Item
                      colon={false}
                      name={name}
                      required={false}
                      messageVariables={{
                        label: subTitle,
                      }}
                      rules={[{ required }, ...rules]}
                    >
                      {React.cloneElement(children, {})}
                    </Form.Item>
                  </Col>
                )
              })}
            </Row>
          )
        })}
      </Col>
    </Row>
  )
}

export default observer(MultiColumnSpan)
