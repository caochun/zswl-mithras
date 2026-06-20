import { useState, useEffect } from 'react'
import { Form, Page, Button, Table, Select, App } from '@zswl/components'
import { Card, message, Input, InputNumber, Row, Col, Space } from 'antd'
import styles from './style.less'
import { observer } from '@zswl/admin'
import baseInfoApi from '@/api/financial/liquidity/baseInfoApi'
import { FormAmount } from '@/components/Form'
import { saveServer } from '@/utils'

const LevelEdit = ({ value = {}, onChange }) => {
  const { fundParameterSignType } = App.getData().optionsType
  const [formValue, setFormValue] = useState({
    sign: value.sign || undefined,
    value: value.value || undefined,
  })

  const handleChange = (key, val) => {
    const newValue = {
      ...formValue,
      [key]: val,
    }
    setFormValue(newValue)
    onChange?.(newValue)
  }

  return (
    <Space>
      <Select
        value={formValue.sign}
        onChange={(val) => handleChange('sign', val)}
        options={fundParameterSignType?.map(({ label }) => ({ label, value: label }))}
        style={{ width: 80 }}
      />
      <InputNumber
        value={formValue.value}
        onChange={(val) => handleChange('value', val)}
        style={{ width: '50%' }}
        placeholder="请输入数值"
      />
    </Space>
  )
}
const renderLevel = (value) => {
  return (
    <div>
      {value.sign ?? '-'}
      {value.value ?? '-'}
    </div>
  )
}
/**
 * 预测参数配置组件
 */
const Parameters = observer(() => {
  const [form] = Form.useForm()
  const [baseEditing, setBaseEditing] = useState(false)
  const [indexEditing, setIndexEditing] = useState(false)
  const table = Table.useStore({
    request: async () => {
      const res = await baseInfoApi.postParameterIndexDetail({})
      const newData = res.map((item) => ({
        indexName: item.indexName,
        indexDisplay: item.indexDisplay,
        red: { sign: item.redSign, value: item.redValue },
        yellow: { sign: item.yellowSign, value: item.yellowValue },
      }))
      return newData
    },
  })

  // 获取基础参数详情
  const getBaseDetail = async () => {
    try {
      const res = await baseInfoApi.postParameterBaseDetail({})
      if (res) {
        form.setFieldsValue({
          saveStock: res.saveStock,
          flexibleCredit: res.flexibleCredit,
        })
      }
    } catch (error) {
      message.error('获取基础参数失败')
    }
  }

  // 保存基础参数
  const handleBaseSave = async () => {
    try {
      const values = await form.validateFields(['saveStock', 'flexibleCredit'])
      await baseInfoApi.postParameterBaseModify(values)
      message.success('保存成功')
      setBaseEditing(false)
      getBaseDetail()
    } catch (error) {
      message.error('保存失败')
    }
  }

  // 保存流动性指标
  const handleIndexSave = async (record) => {
    try {
      const { list, values } = await table.submit()

      const newData = list.map((item) => ({
        indexName: item.indexName,
        indexDisplay: item.indexDisplay,
        redSign: item.red.sign,
        redValue: item.red.value,
        yellowSign: item.yellow.sign,
        yellowValue: item.yellow.value,
      }))
      await baseInfoApi.postParameterIndexModify(newData)
      table.search()
      setIndexEditing(false)
      message.success('保存成功')
    } catch (error) {
      message.error('保存失败')
    }
  }

  const columns = [
    {
      title: '指标名称',
      dataIndex: 'indexDisplay',
      editable: false,
    },
    {
      title: '一级预警（红色）',
      editable: { element: <LevelEdit /> },
      dataIndex: 'red',
      render: renderLevel,
    },
    {
      title: '二级预警（黄色）',
      dataIndex: 'yellow',
      editable: { element: <LevelEdit /> },
      render: renderLevel,
    },
  ]

  useEffect(() => {
    getBaseDetail()
  }, [])

  return (
    <Page noStyle className={styles.parameters}>
      <Card
        title="基础参数"
        extra={
          baseEditing ? (
            <Space>
              <Button.Withdraw onClick={() => setBaseEditing(false)}>取消</Button.Withdraw>
              <Button.Save type="primary" onClick={handleBaseSave}>
                保存
              </Button.Save>
            </Space>
          ) : (
            <Button.Edit type="primary" onClick={() => setBaseEditing(true)}>
              编辑
            </Button.Edit>
          )
        }
      >
        <Form form={form} layout="horizontal" initialValues={{}}>
          <Row>
            <Col span={8}>
              <FormAmount.Item
                label="安全库存"
                name="saveStock"
                addonAfter="万元"
                initFormat={10000 * 10000}
                disabled={!baseEditing}
              />
            </Col>
            <Col span={8}>
              <FormAmount.Item
                label="灵活授信"
                name="flexibleCredit"
                addonAfter="万元"
                initFormat={10000 * 10000}
                disabled={!baseEditing}
              />
            </Col>
          </Row>
        </Form>
      </Card>

      <Card
        title="流动性指标"
        className={styles.indicatorCard}
        extra={
          indexEditing ? (
            <Space>
              <Button.Withdraw onClick={() => setIndexEditing(false)}>取消</Button.Withdraw>
              <Button.Save type="primary" onClick={handleIndexSave}>
                保存
              </Button.Save>
            </Space>
          ) : (
            <Button.Edit type="primary" onClick={() => setIndexEditing(true)}>
              编辑
            </Button.Edit>
          )
        }
      >
        <Table
          columnsFilter={'liquidity_predictionParameters_Parameters'}
          onFilter={(key, val) => saveServer('liquidity_predictionParameters_Parameters', val)}
          editable={indexEditing}
          columns={columns}
          store={table}
          rowKey={'indexName'}
          pagination={false}
          rowClassName="editable-row"
        />
      </Card>
    </Page>
  )
})

export default Parameters
