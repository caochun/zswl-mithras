import { useRef } from 'react'
import { observer } from '@zswl/admin'
import ModalEditTable from '../../ModalEditTable/KpiModalEditTable'
import { InputNumberEditable } from '@/components/Format'
import { projectRatioApi as Api } from '@/api/kpi/baseSet/parameterConfigApi'

const CONNECTOR = '_'
const PROJECT_RATE_TITLE = '项目提奖比例'

const render = (value) => value + '%'

const editableRender = (canEdit) => {
  return canEdit
    ? InputNumberEditable({
        precision: 3,
        addonAfter: '%',
      })
    : false
}

const Index = ({ baseStore, typeInfo }) => {
  const detailData = useRef({})

  const getData = async () => {
    const res = await Api.getList()
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
    await Api.saveList({
      ...detailData.current,
      configValue,
    })
  }

  return (
    <>
      <ModalEditTable
        columnWidth={140}
        tableApi={getData}
        saveApi={saveData}
        typeInfo={typeInfo}
        baseStore={baseStore}
        columns={(editable) => {
          return [
            {
              title: '项目类型',
              children: [
                {
                  title: '投放时效',
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
            {
              title: '厂商类',
              children: [
                {
                  title: '存量',
                  dataIndex: 'FACTORY_HISTORY',
                  width: 140,
                  editable: editableRender(editable),
                  render,
                },
                {
                  title: '新增',
                  dataIndex: 'FACTORY_NEW',
                  width: 140,
                  editable: editableRender(editable),
                  render,
                },
              ],
            },
          ]
        }}
      ></ModalEditTable>
    </>
  )
}

export default observer(Index)
