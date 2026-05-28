import { useState, useCallback } from 'react'
import { observer } from '@zswl/admin'
import Api from '@/pages/afterLease/level5Classify/api'
import { TextAreaEditable } from '@/components/Format'
import { EditTable } from '@/components/Table'
import { Radio } from 'antd'
import { App } from '@zswl/components'
import _ from 'lodash'

const Index = ({ canEdit = true, id }) => {
  const [groupMap, setGroupMap] = useState({})

  const getList = useCallback(
    async (params) => {
      const result = await Api.postRiskFactor({
        ...params,
        id,
      })
      calcGroupCell(result)
      return { list: result }
    },
    [id]
  )

  const calcGroupCell = (data) => {
    const grouped = _.groupBy(data, 'type')
    const groupLengths = _.mapValues(grouped, (group) => group.length)
    const groupLengthsArr = Object.values(groupLengths)
    const _groupMap = {}
    groupLengthsArr.reduce((prev, cur, index) => {
      _groupMap[prev] = groupLengthsArr[index]
      return prev + cur
    }, 0)
    setGroupMap(_groupMap)
  }

  const saveData = async (list, values) => {
    const configValue = []
    list.forEach((item) => {
      configValue.push({
        templateId: item.templateId,
        hasRisk: values[item?.templateId]?.hasRisk,
        remark: values[item?.templateId]?.remark,
      })
    })
    await Api.postRiskFactorModify({
      id,
      riskFactors: configValue,
    })
  }

  return (
    <EditTable
      scroll={{
        x: 1300,
      }}
      rowKey="templateId"
      tableStoreConfig={{
        pagination: false,
      }}
      tableApi={getList}
      saveData={saveData}
      canEdit={canEdit}
      title="风险因素清单"
      columns={[
        {
          title: '类别',
          dataIndex: 'type',
          width: 100,
          editable: false,
          onCell: (record, index) => {
            if (groupMap[index]) {
              return { rowSpan: groupMap[index] }
            }
            return { rowSpan: 0 }
          },
        },
        {
          title: '主要风险因素',
          dataIndex: 'riskFactor',
          width: 400,
          editable: false,
        },
        {
          title: '是否出现所述风险因素',
          dataIndex: 'hasRisk',
          width: 140,
          editable: () => {
            return {
              element: (
                <Radio.Group>
                  <Radio value={true}>是</Radio>
                  <Radio value={false}>否</Radio>
                </Radio.Group>
              ),
              required: true,
              rules: [{ required: true, message: '请选择' }],
            }
          },
          matchOption: 'trueOrFalse',
          render: (val) => {
            const labelVal = App.matchOption('trueOrFalse', val).label
            return <span style={labelVal === '是' ? { color: 'red' } : {}}>{labelVal}</span>
          },
        },
        {
          title: '说明',
          width: 200,
          dataIndex: 'remark',
          editable: TextAreaEditable({
            autoSize: true,
          }),
        },
      ]}
    ></EditTable>
  )
}

export default observer(Index)
