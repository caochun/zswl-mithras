import { getValue } from '@/components/Format'
import IconFont from '@/components/Icon'
import FormListItem from '@/components/Project/FormListItem'
import { rules } from '@/utils'
import { Form } from '@zswl/components'
import { Button, Input, Row, Space } from 'antd'
import styles from './index.less'

export const PeopleListColumn = ({
  title,
  dataIndex,
  isDebtor,
  noClientType,
  requiredMark,
  scene = 'other',
  ...rest
}) => {
  return {
    title,
    dataIndex,
    span: 2,
    requiredMark,
    editable: {
      rules: [requiredMark && rules.required()],
      element: (
        <Form.List
          name={dataIndex}
          rules={[requiredMark ? rules.required() : undefined]}
          className="review-ant-form-item"
        >
          {(fields, { add, remove }) => {
            return (
              <FormListItem
                fields={fields}
                add={add}
                remove={remove}
                scene={scene}
                addText={`添加${title}`}
                fieldKey={dataIndex}
                isDebtor={isDebtor}
                required={requiredMark}
                noClientType={noClientType}
              />
            )
          }}
        </Form.List>
      ),
    },
    render: (val) => <FormListItem.Detail values={getValue(val)} isDebtor={isDebtor} />,
    ...rest,
  }
}

export const SupplierColumn = ({
  dataIndex = 'supplierInfo',
  title = '供应商',
  requiredMark = true,
  ...rest
}) => {
  return {
    title,
    dataIndex,
    span: 2,
    requiredMark,
    editable: {
      element: (
        <Form.Item dependencies={['leaseTypes']} noStyle>
          {({ getFieldValue }) => {
            const leaseTypes = getFieldValue('leaseTypes')
            const hasZhiZhu = leaseTypes?.includes('zhi_zu')
            if (!hasZhiZhu) return
            return (
              <Form.List
                name={dataIndex}
                rules={[{ required: true, message: '请输入!' }]}
                initialValue={[{}]}
              >
                {(fields, { add, remove }) => {
                  return (
                    <>
                      <Row
                        key={0}
                        style={{
                          marginBottom: '8px',
                          display: 'block',
                        }}
                      >
                        <Form.Item
                          rules={[{ required: true, message: '请输入!' }]}
                          className={styles.addInput}
                          name={[0, 'clientName']}
                        >
                          <Input />
                        </Form.Item>
                        <Space className={styles.add}>
                          <IconFont type="icon-icon_add" />
                          <Button type="link" onClick={() => add({})} className={styles.add}>
                            添加供应商
                          </Button>
                        </Space>
                      </Row>
                      {fields?.map(({ key, name, ...restField }) => {
                        if (key === 0) {
                          return
                        }
                        return (
                          <Row
                            key={key}
                            style={{
                              display: 'flex',
                              alignItems: 'flex-start',
                              marginBottom: '8px',
                            }}
                          >
                            <Form.Item
                              rules={[{ required: true, message: '请输入!' }]}
                              {...restField}
                              name={[name, 'clientName']}
                            >
                              <Input />
                            </Form.Item>
                            <Button
                              type="link"
                              onClick={() => remove(name)}
                              className={styles.remove}
                            >
                              删除
                            </Button>
                          </Row>
                        )
                      })}
                    </>
                  )
                }}
              </Form.List>
            )
          }}
        </Form.Item>
      ),
    },
    render: (val, record) => {
      return record[dataIndex]?.map((item) => item.clientName)?.join(',') || '-'
    },
    ...rest,
  }
}
