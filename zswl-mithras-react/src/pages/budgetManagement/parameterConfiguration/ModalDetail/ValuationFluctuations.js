import { observer } from '@zswl/admin'
import { useEffect, useMemo, useRef, useState } from 'react'
import { InputCalcEditable, InputNumberEditable } from '@/components/Format'
import { Button, Form, FormStore, Select } from '@zswl/components'
import { FormTable } from '@/components'
import { myInputEditable, myInputRender } from './context'
import styles from './ModalEditTable/index.less'
import Api from '@/api/baseData/pricing/baseSet/ftpBaseSet'
import { Input, Space, message } from 'antd'
import { FormulaValueTip } from '@/components'

const IS_WARE_OPTIONS = 'fluctuationValueEnum'
const paramOtherNameEnum = [
  '十年期国债收益率波动计价标准',
  '一年期shibor利率波动计价标准',
  'LPR波动计价标准',
  '成本趋势波动计价标准',
]
const VERSIBLE = 'T'
const Index = ({ baseStore, typeInfo }) => {
  useEffect(() => {
    getData()
  }, [typeInfo.list])
  const [editable, setEditable] = useState(typeInfo.isEdit)
  const getData = async () => {
    const newData = {}
    const data = typeInfo.list.map(({ formula, ...item }, index) => {
      newData[`taxRate${index}`] = formula
      return item
    })
    newData.data = data
    form.setFieldsValue(newData)
  }
  const form = useMemo(() => {
    return new FormStore({})
  }, [])
  const columns = [
    {
      title: (
        <div>
          <span>波动值(%)</span>
          <FormulaValueTip versible={VERSIBLE}></FormulaValueTip>
        </div>
      ),
      dataIndex: 'fluctuationFormula',
      editable: () => {
        return editable ? InputCalcEditable({ acceptCode: [VERSIBLE] }) : false
      },
      render: (value) => value,
    },
    {
      title: '映射值(%)',
      dataIndex: 'mappingVal',
      editable: (record) => {
        return myInputEditable({ editable, record, dataIndex: 'mappingVal', initFormat: 1 })
      },
      render: (value) => value,
    },
  ]

  const saveEditData = async () => {
    const formData = await form.submit()
    const newArr = formData?.data.map((item, index) => {
      const isDirect = item.paramOtherName === 'DIRECT'
      const { list, ...infos } = typeInfo

      return {
        paramName: paramOtherNameEnum[index],
        ...infos,
        ...item,
        formula: isDirect ? [] : formData[`taxRate${index}`],
      }
    })
    await Api.postSettingModify(newArr)
    message.success('保存成功')
    baseStore.$editModal.close()
    baseStore.$table.search()
    setEditable(false)
  }

  return (
    <>
      {typeInfo.isEdit ? (
        <div className={styles.header}>
          {!editable ? (
            <Button type="primary" onClick={() => setEditable(true)}>
              编辑
            </Button>
          ) : (
            <Space>
              <Button onClick={() => setEditable(false)}>取消</Button>
              <Button onClick={saveEditData} type="primary">
                确认
              </Button>
            </Space>
          )}
        </div>
      ) : null}
      <Form store={form}>
        <Form.Item label="十年期国债收益率波动计价标准" name={['data', 0, 'paramOtherName']}>
          <Select options={IS_WARE_OPTIONS} disabled={!editable} />
        </Form.Item>
        <Form.Item noStyle dependencies={[['data', 0, 'paramOtherName']]}>
          {({ getFieldValue }) => {
            const taxRate = getFieldValue(['data', 0, 'paramOtherName'])
            if (taxRate === 'DIRECT') return null
            return (
              <Form.Item name={'taxRate0'}>
                <FormTable columns={columns} onlyRead={!editable} />
              </Form.Item>
            )
          }}
        </Form.Item>

        <Form.Item label="一年期shibor利率波动计价标准" name={['data', 1, 'paramOtherName']}>
          <Select options={IS_WARE_OPTIONS} disabled={!editable} />
        </Form.Item>
        <Form.Item noStyle dependencies={[['data', 1, 'paramOtherName']]}>
          {({ getFieldValue }) => {
            const taxRate = getFieldValue(['data', 1, 'paramOtherName'])
            if (taxRate === 'DIRECT') return null
            return (
              <Form.Item name={'taxRate1'}>
                <FormTable columns={columns} onlyRead={!editable} />
              </Form.Item>
            )
          }}
        </Form.Item>
        <Form.Item label="LPR波动计价标准" name={['data', 2, 'paramOtherName']}>
          <Select options={IS_WARE_OPTIONS} disabled={!editable} />
        </Form.Item>
        <Form.Item noStyle dependencies={[['data', 2, 'paramOtherName']]}>
          {({ getFieldValue }) => {
            const taxRate = getFieldValue(['data', 2, 'paramOtherName'])
            if (taxRate === 'DIRECT') return null
            return (
              <Form.Item name={'taxRate2'}>
                <FormTable columns={columns} onlyRead={!editable} />
              </Form.Item>
            )
          }}
        </Form.Item>
        <Form.Item label="成本趋势波动计价标准" name={['data', 3, 'paramOtherName']}>
          <Select options={IS_WARE_OPTIONS} disabled={!editable} />
        </Form.Item>
        <Form.Item noStyle dependencies={[['data', 3, 'paramOtherName']]}>
          {({ getFieldValue }) => {
            const taxRate = getFieldValue(['data', 3, 'paramOtherName'])
            if (taxRate === 'DIRECT') return null
            return (
              <Form.Item name={'taxRate3'}>
                <FormTable columns={columns} onlyRead={!editable} />
              </Form.Item>
            )
          }}
        </Form.Item>

        <Form.Item hidden name={['data', 0, 'id']}>
          <Input />
        </Form.Item>
        <Form.Item hidden name={['data', 1, 'id']}>
          <Input />
        </Form.Item>
        <Form.Item hidden name={['data', 2, 'id']}>
          <Input />
        </Form.Item>
        <Form.Item hidden name={['data', 3, 'id']}>
          <Input />
        </Form.Item>
      </Form>
    </>
  )
}

export default observer(Index)
