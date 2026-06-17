import FormItemContent from '@/components/FormItemContent'
import { Descriptions, Form, Input, Row, Space } from 'antd'
import { Button } from '@zswl/components'
import { rules } from '@/utils'
import IconFont from '@/components/Icon'
import styles from './index.less'

const SupplierInfo = ({ showValue, getDetailValue, getDetailChange }) => {
  return (
    <FormItemContent
      isChange={getDetailChange('supplierInfo')}
      formContent={
        <Form.Item dependencies={['leaseTypes']}>
          {({ getFieldValue }) => {
            const leaseTypes = getFieldValue('leaseTypes')
            const hasZhiZhu = leaseTypes?.includes('zhi_zu')
            if (!hasZhiZhu) return
            return (
              <Form.List name="supplierInfo" rules={[{ required: true, message: '请输入!' }]}>
                {(fields, { add, remove }) => {
                  return (
                    <>
                      <Row
                        key={0}
                        style={{
                          marginBottom: '8px',
                          display:'block'
                        }}
                      >
                        <Form.Item rules={[{ required: true, message: '请输入!' }]} className={styles.addInput} name={[0, 'clientName']}>
                          <Input />
                        </Form.Item>
                        <Space className={styles.add}>
                          <IconFont type="icon-icon_add" />
                          <Button type="link" onClick={() => add({})} className={styles.add}>
                            添加供应商
                          </Button>
                        </Space>
                      </Row>
                      {fields?.map(({ key, name, ...restField }, index) => {
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
                            <Form.Item rules={[{ required: true, message: '请输入!' }]} {...restField} name={[name, 'clientName']}>
                              <Input />
                            </Form.Item>
                            <Button type="link" onClick={() => remove(name)} className={styles.remove}>
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
      }
      value={
        getDetailValue('supplierInfo')
          ?.map((item) => item.clientName)
          .join(',') ?? '-'
      }
      showValue={showValue}
    />
  )
}

export default SupplierInfo
