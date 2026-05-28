import { useRef } from 'react'
import { observer } from '@zswl/admin'
import ModalEditTable from '@/pages/kpi/Component/ModalEditTable'
import { InputNumberEditable } from '@/components/Format'
import Api from './api'
import { message } from 'antd'

const CONNECTOR = '_'
const PROJECT_RATE_TITLE = '基础提奖比例'

const render = (value) => value + '%'

const editableRender = (canEdit) => {
  return canEdit
    ? InputNumberEditable({
        precision: 3,
        addonAfter: '%',
      })
    : false
}

const Index = ({ typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList({ baseId: typeInfo.id })
    detailData.current = res
    const data = {}
    res.configValue?.forEach(({ projectType, projectSource, projectRadio }) => {
      data[`${projectType}${CONNECTOR}${projectSource}`] = projectRadio
    })
    return [{ PROJECT_RATE_TITLE, ...data }]
  }

  const saveData = async ({ list }) => {
    const objValues = list[0]
    const configValue = []
    Object.keys(objValues).forEach((key) => {
      if (key.indexOf(CONNECTOR) > -1 && objValues[key] !== PROJECT_RATE_TITLE) {
        const [projectType, projectSource] = key.split(CONNECTOR)
        configValue.push({
          projectType,
          projectSource,
          projectRadio: objValues[key],
          configValueType: 'VALUE',
        })
      }
    })

    await Api.saveList({ ...detailData.current, configValue, id: typeInfo.id })
  }

  return (
    <ModalEditTable
      columnWidth={140}
      tableApi={getData}
      saveApi={saveData}
      typeInfo={typeInfo}
      rowKey={({ projectType, projectSource }) => `${projectType}_${projectSource}`}
      columns={(editable) => {
        return [
          {
            title: '项目类型',
            children: [
              {
                title: '项目来源',
                dataIndex: 'PROJECT_RATE_TITLE',
                width: 140,
              },
            ],
          },
          {
            title: '产业类',
            children: [
              {
                title: '存量',
                dataIndex: 'INDUSTRY_HISTORY',
                width: 140,
                editable: editableRender(editable),
                render,
              },
              {
                title: '新增',
                dataIndex: 'INDUSTRY_NEW',
                width: 140,
                editable: editableRender(editable),
                render,
              },
            ],
          },
          {
            title: '公用事业类',
            children: [
              {
                title: '存量',
                dataIndex: 'PUBLIC_HISTORY',
                width: 140,
                editable: editableRender(editable),
                render,
              },
              {
                title: '新增',
                dataIndex: 'PUBLIC_NEW',
                width: 140,
                editable: editableRender(editable),
                render,
              },
            ],
          },
        ]
      }}
    ></ModalEditTable>
  )
}

export default observer(Index)
