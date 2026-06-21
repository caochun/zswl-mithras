import { observer } from '@zswl/admin'
import { Descriptions, Form } from '@zswl/components'
import { Button, Space, Input, Radio, Checkbox } from 'antd'
import { forwardRef, useImperativeHandle, useMemo } from 'react'
import styles from './style.less'

const { TextArea } = Input
const labelStyle = {
  // color: 'red',
  background: '#F5F6FA',
}
const CheckBoxEle = ({ value, onChange, options }) => {
  const checkValue = useMemo(() => {
    return value?.split(',')
  }, [value])

  const checkedChange = (e) => {
    onChange(e.join(','))
  }
  return (
    <Checkbox.Group
      checked={checkValue}
      onChange={checkedChange}
      options={options}
      defaultValue={checkValue}
    />
  )
}

const contentStyle = { width: 230, maxWidth: 230 }
function DynamicDescMap({ contentList, editable, required = false, groupName }, ref) {
  const getItems = (list) => {
    const reduceList = list.reduce((pre, cur) => {
      // 根据templateTitle 聚合
      const { templateTitle, tooltip, ...restCur } = cur
      const item = pre.find((v) => v.templateTitle === templateTitle)
      if (item) {
        item.elementList = [...item.elementList, restCur]
      } else {
        pre.push({
          templateTitle,
          tooltip,
          elementList: [restCur],
        })
      }
      return pre
    }, [])

    const data = reduceList.map((content) => {
      const { templateTitle: title, elementList, tooltip, ...rest } = content
      const getElement = (content) => {
        const { templateContentInputType, templateOptionList } = content
        if (templateContentInputType === 'radio') {
          return {
            element: <Radio.Group options={templateOptionList} />,
            render: (value) => {
              const label = templateOptionList?.find((v) => v.value === value)?.label
              return <div>{label}</div>
            },
          }
        }
        if (templateContentInputType === 'checkbox') {
          return {
            element: <CheckBoxEle options={templateOptionList} />,
            render: (value) => {
              const label =
                value &&
                value
                  .split(',')
                  .map(
                    (labelValue) => templateOptionList?.find((v) => v.value === labelValue)?.label
                  )
                  .join('、')
              return <div>{label}</div>
            },
          }
        }
        if (templateContentInputType === 'input') {
          return {
            element: <Input />,
            render: (value) => {
              return <div>{value}</div>
            },
          }
        }
        return {
          element: <TextArea maxLength={2000} autoSize={{ minRows: 2, maxRows: 6 }} />,
          render: (value) => {
            return <div>{value}</div>
          },
        }
      }

      const dataIndex = elementList.map(({ templateId }) => templateId).join(',')
      const element = (value) =>
        elementList?.map((v) => {
          const { templateContentInputLabel, templateId, required: elementRequired } = v
          const initialValue = value?.[templateId]
          return (
            <div style={{ width: '100%' }}>
              <Form.Item
                name={templateId}
                label={templateContentInputLabel ?? ''}
                rules={[{ required: elementRequired ?? required }]}
                initialValue={initialValue}
              >
                {getElement(v).element}
              </Form.Item>
            </div>
          )
        })
      const render = (value, record) => {
        return elementList.map((v) => {
          const { templateContentInputLabel, templateId } = v
          const newValue = record?.[templateId]
          return (
            <div style={{ display: 'flex', width: '100%' }}>
              <span
                style={{
                  minWidth: templateContentInputLabel && 100,
                  marginRight: templateContentInputLabel && 12,
                }}
              >
                {templateContentInputLabel && `${templateContentInputLabel}:`}
              </span>
              {getElement(v)?.render(newValue) ?? newValue}
            </div>
          )
        })
      }

      return {
        title,
        dataIndex,
        tooltip,
        editable: element,
        render,
      }
    })
    return data
  }
  const form = Form.useStore()

  useImperativeHandle(ref, () => ({
    commitForm: (cb) => {
      handleCommit(cb)
    },
    ...form,
  }))

  const handleCommit = async (cb) => {
    try {
      const values = await form.validateFields()
      cb(values)
    } catch (err) {
    }
  }
  const dataSource = useMemo(() => {
    const map = {}
    contentList?.forEach(({ templateId, content }) => {
      map[templateId] = content
    })
    return map
  }, [contentList])

  return (
    <div key={groupName} className={styles.contentBox}>
      <div className={styles.titleRow}>{groupName}</div>
      <Descriptions
        bordered
        column={1}
        items={getItems(contentList)}
        form={form}
        labelStyle={{ width: 300 }}
        dataSource={dataSource}
        contentStyle={{ padding: ' 16px 0' }}
        editable={!editable}
      />
    </div>
  )
}

export default forwardRef(DynamicDescMap)
