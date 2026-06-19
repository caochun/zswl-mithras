import { observer } from '@zswl/admin'
import { Form, Space } from 'antd'
import { useEffect, useState } from 'react'
import { InputReadOnly } from '@/components/Form'
import { App } from '@zswl/components'

//AreaTypeEnum
function Index({ isEdit, groupType = 'NO_PARTITION', onChange, path, ...rest }) {
  const lableMap = App.getData().optionsType.areaTypeEnum.filter((v) => v.value !== 'NO_PARTITION')
  const newPath = [...path, 'areaConfig']
  const form = Form.useFormInstance()
  const [init, setInit] = useState(true)
  useEffect(() => {
    init && setInit(false)
  }, [])
  useEffect(() => {
    if (!init) {
      const newData = new Array(groupType === 'PARTITION' ? 4 : 1)
        .fill({ min: undefined, max: undefined })
        .map((v, index) => {
          const areaType = groupType === 'PARTITION' ? lableMap[index].value : groupType
          return { ...v, areaType }
        })
      form.setFieldValue(newPath, newData)
    }
  }, [groupType])
  const areaConfigValidate = {
    validator: (rule, value, callback) => {
      // min,max 必填
      const isMin = value?.some((v) => !v.min)
      const isMax = value?.some((v) => !v.max)
      if (isMin || isMax) {
        callback(new Error('请填写完整min,max'))
        return
      }
      callback()
    },
  }
  return (
    <Form.List name={[...path.slice(-1), 'areaConfig']} rules={[areaConfigValidate]} {...rest}>
      {(fields, { add, remove }, { errors }) => {
        return (
          <div>
            {fields.map(({ key, name, ...restField }, index) => {
              return (
                <div key={key}>
                  <Space>
                    {groupType === 'PARTITION' ? (
                      <div style={{ marginBottom: 24 }}>{lableMap[index].label}</div>
                    ) : (
                      <></>
                    )}
                    <Form.Item {...restField} name={[name, 'min']} label="MIN" required>
                      <InputReadOnly onlyRead={!isEdit} />
                    </Form.Item>
                    <Form.Item {...restField} name={[name, 'max']} label="MAX" required>
                      <InputReadOnly onlyRead={!isEdit} />
                    </Form.Item>
                  </Space>
                </div>
              )
            })}
            <Form.ErrorList errors={errors} />
          </div>
        )
      }}
    </Form.List>
  )
}

export default observer(Index)
