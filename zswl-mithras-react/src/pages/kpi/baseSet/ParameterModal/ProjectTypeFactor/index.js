import { useRef } from 'react'
import { observer } from '@zswl/admin'
import { KpiModalEditTable as ModalEditTable } from '@/components/Kpi/BaseSetModalDetailEntries'
import { InputNumberEditable } from '@/components/Format'
import Api from './api'
import { uniqueId } from 'lodash'
import { message } from 'antd'

const mockData = {
  configCode: 'PROJECT_FACTOR',
  configDesc: '项目类型系数',
  configValue: [
    {
      configValueType: 'VALUE',
      businessType: '=回租',
      projectRadio: '7.2',
    },
    {
      configValueType: 'VALUE',
      businessType: '≠回租',
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
              title: '业务类型',
              dataIndex: 'projectType',
            },
            {
              title: '系数',
              dataIndex: 'projectRadio',
              editable: () => {
                return editable ? InputNumberEditable({ precision: 2 }) : false
              },
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
