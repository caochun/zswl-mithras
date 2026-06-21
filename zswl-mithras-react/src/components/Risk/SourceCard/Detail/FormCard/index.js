import { observer } from '@zswl/admin'
import { Card, Col, Form, Modal, Row, Space } from 'antd'
import { App, Button, Select } from '@zswl/components'
import GroupSource from './GroupSource'
import { forwardRef, useImperativeHandle, useRef, useState } from 'react'
import { InputNumberReadOnly, RadioReadOnly, SelectReadOnly } from '@/components/Form'
import { ApiSelect } from '@/components/Select'
import riskCardTargetApi from '@/api/risk/riskCardTargetApi'
import { rules } from '@/utils'

const AreaCard = forwardRef((props, ref) => {
  const { key, name, index, listName, store, year, ...restField } = props
  const [isEdit, setIsEdit] = useState(false)

  const { areaStatusEnum } = App.getData().optionsType
  const form = Form.useFormInstance()
  const handleEdit = () => {
    setIsEdit(true)
  }
  const handleCancel = () => {
    store.cancel()
    setIsEdit(false)
  }
  const handleSave = async () => {
    const areaConfigMinPath = new Array(4).fill(0).map((v, i) => ['areaConfig', i, 'min'])
    const areaConfigMaxPath = new Array(4).fill(0).map((v, i) => ['areaConfig', i, 'max'])
    const namePath = [
      ['targetName'],
      ['targetWeight'],
      ['gradeType'],
      ['areaStatus'],
      ['optionGrade', 'good'],
      ['optionGrade', 'moderate'],
      ['optionGrade', 'bad'],
      ['areaConfig'],
      ['optionGrade'],
      ['id'],
      ...areaConfigMinPath,
      ...areaConfigMaxPath,
    ].map((v) => [listName, name, ...v])
    const data = await form.validateFields(namePath)
    const params = data[listName][index]
    await store.saveTarget(params)
    setIsEdit(false)
  }
  const handleDelete = async () => {
    const data = await form.getFieldsValue(true)
    const id = data[listName][index].id
    Modal.confirm({
      title: '提示',
      content: '确定删除该指标吗？',
      onOk: async () => await store.deleteTarget(id),
    })
  }
  const numberValidator = {
    validator: (rule, value, callback) => {
      // 不得大于指标权重
      const data = form.getFieldsValue(true)
      const targetWeight = data[listName][index].targetWeight
      if (value > targetWeight) {
        callback(new Error('不得大于指标权重'))
        return
      }
      callback()
    },
  }
  const gradeTypeChange = (e) => {
    if (e === 'LINEAR') {
      form.setFieldValue([listName, name, 'areaStatus'], 'NO_PARTITION')
      const newData = new Array(1).fill({
        min: undefined,
        max: undefined,
        areaType: 'NO_PARTITION',
      })
      form.setFieldValue([listName, name, 'areaConfig'], newData)
    }
  }
  useImperativeHandle(ref, () => ({
    handleEdit,
  }))
  return (
    <Card
      key={key}
      title={`指标${index + 1}`}
      style={{ marginBottom: 12 }}
      extra={[
        <Space key="btn">
          {!isEdit && (
            <Button.Delete key="delete" type="primary" onClick={handleDelete}>
              删除
            </Button.Delete>
          )}
          {isEdit ? (
            <Button key="cancel" type="primary" onClick={handleCancel}>
              取消
            </Button>
          ) : (
            <Button.Edit key="edit" type="primary" onClick={handleEdit}>
              编辑
            </Button.Edit>
          )}
          {isEdit && (
            <Button.Save key="save" type="primary" onClick={handleSave}>
              保存
            </Button.Save>
          )}
        </Space>,
      ]}
    >
      <Row gutter={12}>
        <Col span={8}>
          <Form.Item
            {...restField}
            name={[name, 'targetName']}
            label="指标名称"
            rules={[rules.required('请选择')]}
          >
            <ApiSelect
              onlyRead={!isEdit}
              api={async (params) => {
                const res = await riskCardTargetApi.postTargetSearch(params)
                return res.map((v) => ({ label: v.name, value: v.name }))
              }}
              params={{ year }}
            />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item
            {...restField}
            name={[name, 'targetWeight']}
            label="指标权重"
            rules={[rules.required('请输入')]}
          >
            <InputNumberReadOnly min={1} max={100} onlyRead={!isEdit} precision={0} />
          </Form.Item>
        </Col>
        <Col span={8}>
          <Form.Item
            {...restField}
            name={[name, 'gradeType']}
            label="线性计算"
            rules={[rules.required('请选择')]}
          >
            <SelectReadOnly options={'gradeEnum'} onlyRead={!isEdit} onChange={gradeTypeChange} />
          </Form.Item>
        </Col>
      </Row>
      <Form.Item dependencies={[[listName, name, 'gradeType']]} noStyle>
        {({ getFieldValue }) => {
          const gradeType = getFieldValue([listName, name, 'gradeType'])
          return gradeType === 'LINEAR' ? (
            <>
              <Form.Item
                {...restField}
                name={[name, 'areaStatus']}
                label="分区积分"
                rules={[rules.required('请选择')]}
              >
                <RadioReadOnly options={areaStatusEnum} onlyRead={!isEdit} />
              </Form.Item>
              <Form.Item dependencies={[[listName, name, 'areaStatus']]} noStyle>
                {({ getFieldValue: chilrenGetFieldValue }) => {
                  const areaStatus = chilrenGetFieldValue([listName, name, 'areaStatus'])
                  return (
                    <GroupSource groupType={areaStatus} path={[listName, name]} isEdit={isEdit} />
                  )
                }}
              </Form.Item>
            </>
          ) : (
            <Row gutter={12}>
              <Col span={8}>
                <Form.Item
                  {...restField}
                  name={[name, 'optionGrade', 'good']}
                  label="优良分值"
                  rules={[rules.required('请选择'), numberValidator]}
                >
                  <InputNumberReadOnly min={0} onlyRead={!isEdit} precision={0} />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  {...restField}
                  name={[name, 'optionGrade', 'moderate']}
                  label="适中分值"
                  rules={[rules.required('请选择'), numberValidator]}
                >
                  <InputNumberReadOnly min={0} onlyRead={!isEdit} precision={0} />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  {...restField}
                  name={[name, 'optionGrade', 'bad']}
                  label="较差分值"
                  rules={[rules.required('请选择'), numberValidator]}
                >
                  <InputNumberReadOnly min={0} onlyRead={!isEdit} precision={0} />
                </Form.Item>
              </Col>
            </Row>
          )
        }}
      </Form.Item>
      <Form.Item {...restField} name={[name, 'id']} noStyle></Form.Item>
    </Card>
  )
})
function RiskSourceCardFormCard({ value, store, year, ...rest }) {
  const listName = 'targetList'

  // 获取AreaCard组件的 ref
  const areaCardRef = useRef([])
  function getRef(dom) {
    areaCardRef.current.push(dom)
  }
  return (
    <Form.List name={listName} {...rest} initialValue={value}>
      {(fields, { add, remove }) => {
        return (
          <div>
            {fields.map(({ key, name, ...restField }, index) => {
              return (
                <AreaCard
                  key={key}
                  index={index}
                  name={name}
                  listName={listName}
                  store={store}
                  year={year}
                  ref={getRef}
                  {...restField}
                />
              )
            })}
            <Button
              onClick={() => {
                add({ areaScoreList: [], gradeType: 'OPTION' })
                setTimeout(() => {
                  areaCardRef.current[areaCardRef.current.length - 1]?.handleEdit?.()
                }, 0)
              }}
              block
            >
              添加指标
            </Button>
          </div>
        )
      }}
    </Form.List>
  )
}

export default observer(RiskSourceCardFormCard)
