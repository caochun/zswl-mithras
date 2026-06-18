import { useRef } from 'react'
import { observer } from '@zswl/admin'
import { KpiModalEditTable as ModalEditTable } from '@/components/Kpi/BaseSetModalDetailEntries'
import { InputNumberEditable } from '@/components/Format'
import Api from './api'
import { uniqueId } from 'lodash'
import { message } from 'antd'

const mockData = {
  configCode: 'PROJECT_SCALE',
  configDesc: '投放奖金系数',
  configValue: [
    {
      configValueType: 'VALUE',
      projectClass: '产业类',
      projectRadio: '7.2',
    },
    {
      configValueType: 'VALUE',
      projectClass: '公用事业类',
      projectRadio: '4.5',
    },
  ],
}

const Index = ({ typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList({ baseId: typeInfo.id })
    detailData.current = res
    return res.configValue.map((v) => ({ ...v, id: uniqueId() }))
  }

  const saveData = async ({ list }) => {
    await Api.saveList({ ...detailData.current, configValue: list, id: typeInfo.id })
  }

  return (
    <>
      <ModalEditTable
        tableApi={getData}
        saveApi={saveData}
        typeInfo={typeInfo}
        columns={(editable) => {
          return [
            {
              title: '项目类别',
              dataIndex: 'projectType',
            },
            {
              title: '系数',
              dataIndex: 'projectRadio',
              editable: () => {
                return editable ? InputNumberEditable({ precision: 2, addonAfter: '%' }) : false
              },
              render: (value) => value + '%',
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
