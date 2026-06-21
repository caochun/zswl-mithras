import { observer } from '@zswl/admin'
import { Form, Space } from 'antd'
import { FormAmount } from '@/components/Form'
import { ReadOnly } from '@/components/Form'

function Index({ isEdit, name: listName, value, text = '限额值', ...rest }) {
  return (
    <Form.List name={listName} {...rest} initialValue={value}>
      {(fields, { add, remove }) => {
        return (
          <div>
            {fields.map(({ key, name, ...restField }, index) => {
              return (
                <Space key={key} style={{ width: '100%', padding: '6px 0 ' }}>
                  {text} {index + 1}:
                  <Form.Item
                    {...restField}
                    name={[name, 'comparisonMethod']}
                    label=""
                    style={{ margin: 0 }}
                  >
                    <ReadOnly defaultValue="<=" />
                  </Form.Item>
                  <Form.Item dependencies={[name, 'valueUnit']} noStyle>
                    {({ getFieldValue }) => {
                      const valueUnit = getFieldValue([listName, name, 'valueUnit'])
                      return (
                        <FormAmount.Item
                          {...restField}
                          name={[name, 'earlyWarningValue']}
                          label=""
                          initFormat={valueUnit === '%' ? 100 : 10000}
                          style={{ margin: 0 }}
                          max={99999}
                        />
                      )
                    }}
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'valueUnit']}
                    label=""
                    style={{ margin: 0 }}
                  >
                    <ReadOnly />
                  </Form.Item>
                </Space>
              )
            })}
          </div>
        )
      }}
    </Form.List>
  )
}

export default observer(Index)
