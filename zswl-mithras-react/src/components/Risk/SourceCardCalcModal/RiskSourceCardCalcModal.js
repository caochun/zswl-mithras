import riskCardTargetCalc from '@/api/risk/riskCardTargetCalc'
import { ReadOnly } from '@/components/Form'
import { observer } from '@zswl/admin'
import { App, Form, Modal, Select, Table, TableStore } from '@zswl/components'
import { Cascader, Col, Input, Row } from 'antd'
import { debounce as _debounce } from 'lodash'
import { useEffect, forwardRef, useImperativeHandle, useMemo, useState } from 'react'
import { saveServer } from '@/utils'

const { Item } = Form
// bad、good、moderate
const gradeMap = {
  bad: '较差',
  good: '优良',
  moderate: '一般',
}

function Index({ modalStore, id, modalStatus, cardData, tryData, year }, ref) {
  const isDetail = modalStatus === 'view'
  const isTryCalc = modalStatus === 'edit'
  const [form] = Form.useForm()

  const columns = [
    { title: '指标', dataIndex: 'targetName' },
    { title: '权重', dataIndex: 'targetWeight' },
    {
      title: '分值',
      dataIndex: 'data',
      editable: (val, index) => {
        const { optionGrade } = val
        if (!optionGrade) {
          return <ReadOnly value={val.data} />
        }
        const options = Object.entries(optionGrade).map(([key, value]) => ({
          label: gradeMap[key],
          value,
        }))
        return (
          <Select
            disabled={isDetail}
            value={val?.score}
            options={options}
            style={{ width: 200 }}
            onChange={(value) => gradeChange(value, index)}
          />
        )
      },
    },
    { title: '得分', dataIndex: 'score' },
  ]

  const gradeChange = (val, index) => {
    const rowData = table.getList()[index]
    table.setRowByIndex(index, { ...rowData, score: val })
    const list = table.getList()
    form.setFields([{ name: 'totalSource', value: getTotalScore(list) }])
  }
  const table = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          const areaId = params.areaId
          const cardId = id
          if (modalStatus) {
            let newCardData = {
              ...cardData,
            }
            if (modalStatus === 'edit') {
              newCardData = tryData
            }
            form.setFields([
              {
                name: 'areaId',
                value: [newCardData.province, newCardData.city, newCardData.area],
              },
              { name: 'executiveLevel', value: newCardData?.regionalLevel },
              { name: 'province', value: [newCardData?.province, newCardData?.city].join('/') },
              { name: 'totalSource', value: newCardData?.totalPoints },
            ])
            return newCardData?.calculateDetailBodies || newCardData?.tryCalculateBodies || []
          }
          if (areaId) {
            const res = await riskCardTargetCalc.postTryCalculate({ areaId, cardId })
            const totalScore = getTotalScore(res?.tryCalculateBodies)
            form.setFields([
              { name: 'executiveLevel', value: res?.regionalLevel },
              { name: 'province', value: [res?.province, res?.city].join('/') },
              { name: 'totalSource', value: totalScore },
            ])
            return res?.tryCalculateBodies || []
          }
          return []
        },
        pagination: false,
      }),
    [id, isDetail, cardData?.calculateDetailBodies]
  )
  const areaChange = async () => {
    const areaId = (form.getFieldValue('areaId') || []).slice(-1)[0]
    table.setParams({ areaId })
    await table.search()
  }
  const getTotalScore = (list = []) => {
    const totalScore = list.reduce((acc, cur) => {
      return acc + cur.score
    }, 0)
    return totalScore?.toFixed(2)
  }

  useEffect(() => {
    if (cardData) {
      table.search()
    } else {
      form.setFields([{ name: 'areaId', value: undefined }])
    }
  }, [cardData?.cardId])

  useImperativeHandle(ref, () => ({
    getTableData: () => {
      return table.getList()
    },
  }))
  const [options, setOptions] = useState([])
  // 给没有 id 的地区加上 id
  const deepOptions = (option) => {
    return (option || []).map((item) => {
      if (!item.id) {
        item.id = item.areaName
      }
      if (item.child) {
        item.child = deepOptions(item.child)
      }
      return item
    })
  }
  const getOptions = _debounce(async () => {
    const res = await riskCardTargetCalc.postAreaAll({
      year,
    })
    setOptions(deepOptions(res))
    return res
  }, 500)

  useEffect(() => {
    getOptions()
  }, [])
  return (
    <Modal
      title={isDetail ? '详情' : isTryCalc ? '计算' : '试计算'}
      store={modalStore}
      okText={'确定'}
      destroyOnClose
      width={1200}
      {...(!isDetail && modalStatus ? {} : { footer: null })}
    >
      <Form form={form} labelCol={{ span: 6 }} preserve={true}>
        <Row>
          <Col span={12}>
            <Item
              label={'地区'}
              name={'areaId'}
              rules={[{ required: true, message: '请选择地区！' }]}
            >
              <Cascader
                disabled={modalStatus}
                options={options}
                onChange={areaChange}
                expandTrigger="hover"
                fieldNames={{
                  label: 'areaName',
                  value: 'id',
                  children: 'child',
                }}
                placeholder="请选择地区"
              />
            </Item>
          </Col>
          <Col span={12}>
            <Item label={'所属区域'} name={'executiveLevel'}>
              <Select options={'areaTypeEnum'} placeholder={'请输入'} disabled />
            </Item>
          </Col>
          <Col span={12}>
            <Item label={'省/市'} name={'province'}>
              <Input placeholder={'请输入'} disabled />
            </Item>
          </Col>
          <Col span={12}>
            <Item label={'得分'} name={'totalSource'}>
              <ReadOnly />
            </Item>
          </Col>
        </Row>
      </Form>
      <Table columnsFilter={'detail_CalcModal_1'}
        onFilter={(key, val) => saveServer('detail_CalcModal_1', val)} columns={columns} store={table} />
    </Modal>
  )
}

export default observer(forwardRef(Index))
