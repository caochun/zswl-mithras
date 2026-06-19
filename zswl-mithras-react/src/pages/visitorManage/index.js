import { Page, Tabs, Table, Modal, ModalStore, Button } from '@zswl/components'
import { getTableColumns, getFormColumns, download } from '@/utils'
import ALL_COLUMNS from './Column'
import { Card, message, Dropdown } from 'antd'
import Api from '@/api/visitorManage'
import { useState } from 'react'
import { saveServer } from '@/utils'
import { DownloadOutlined } from '@ant-design/icons'
import ImageModal from './ImageModal'
import TaskFloatStore from '@/layout/TaskFloat/store'

const VisitorDetail = () => {
  const nameColumns = [
    '拜访时间',
    '拜访人',
    '部门',
    '拜访对象',
    '拜访类型',
    '打卡类型',
    '打卡地点/补卡地点',
    '拜访阶段',
    '关联项目编号',
    '关联合同编号',
    '关联租后检查计划',
  ]
  const formNameColumns = ['拜访对象', '拜访人', '拜访时间', '拜访阶段', '打卡类型', '拜访类型']
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  const imageModal = new ModalStore({
    onOpen: async (val) => {
      // console.log('val-image: ', val)
      const res = await Api.getVisitRecordFileList({
        moduleType: 'VISIT_RECORD',
        mainId: val?.id,
        // mainId: 1057,
        needPreviewUrl: true,
      })
      if (!res?.list || res?.list?.length === 0) {
        message.warning('暂无图片')
        return false
      }
      return {
        id: val?.id,
        list: res?.list,
      }
    },
  })
  const dealColumns = [
    ...columns,
    {
      title: '操作',
      width: 120,
      fixed: 'right',
      isAction: true,
      actions(value) {
        return [
          {
            name: '查看图片',
            onClick: () => {
              imageModal.open(value)
            },
          },
        ]
      },
    },
  ]
  const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
  const table = Table.useStore({
    request: (params) => {
      return Api.visitDetail(params)
    },
  })
  const handleExport = async () => {
    const params = table.getParams()
    const { keys } = table.getSelected()
    return await Api.appFileExport({
      businessType: 'APP_PC_VISIT_RECORD_DETAIL',
      ids: keys?.length ? keys : '',
      ext: {
        ...params,
      },
    })
  }
  const handleAllExport = async () => {
    const exportPhoto = async () => {
      const { keys } = table.getSelected()
      const params = table.getParams()

      const res = await Api.downloadVisitFiles({
        ...params,
        visitRecordIds: keys?.length ? keys : '',
      })
      window.open(res)
    }
    await TaskFloatStore.executeTask({
      currentTask: exportPhoto,
      name: '导出拜访照片',
    })
  }
  const [loading, setLoading] = useState(false)
  const handleDropExport = async ({ key }) => {
    try {
      setLoading(true)
      if (key === 'export') {
        await handleExport()
      } else if (key === 'photoExport') {
        await handleAllExport()
      }
    } finally {
      setLoading(false)
    }
  }
  return (
    <>
      <Table
        extra={[
          <Dropdown
            loading={loading}
            arrow
            menu={{
              onClick: handleDropExport,
              items: [
                {
                  key: 'photoExport',
                  label: <div>照片批量导出</div>,
                },
                {
                  key: 'export',
                  label: <div>拜访记录批量导出</div>,
                },
              ],
            }}
          >
            <Button.Download loading={loading} type="primary">
              导出
            </Button.Download>
          </Dropdown>,
        ]}
        editable={false}
        scroll={{ x: 'max-content' }}
        selectable
        store={table}
        columns={dealColumns}
        columnsFilter="拜访管理_拜访明细"
        onFilter={(key, val) => saveServer('拜访管理_拜访明细', val)}
        searchbar={{
          labelCol: { span: 6 },
          items: formColumns,
        }}
      ></Table>
      <ImageModal store={imageModal} />
    </>
  )
}

const VisitorSummary = () => {
  const nameColumns = ['部门', '拜访总次数', '拜访总家数']
  const formNameColumns = ['拜访阶段', '打卡类型', '拜访时间', '拜访类型']
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  const innerColumns = getTableColumns(ALL_COLUMNS, [
    '人员',
    {
      title: '拜访总次数',
      rename: '拜访次数',
    },
    {
      title: '拜访总家数',
      rename: '拜访家数',
    },
  ])
  const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
  const [sumData, setSumData] = useState({})

  const table = Table.useStore({
    request: async (params) => {
      const data = await Api.visitSummary(params)
      setSumData(data.sumData || {})
      return data.records
    },
  })
  const handleExport = async () => {
    const params = table.getParams()
    await Api.appFileExport({
      businessType: 'APP_PC_VISIT_RECORD_SUMMARY',
      ext: {
        ...params,
      },
    })
  }
  return (
    <Table
      columnsFilter="拜访管理_拜访明细_2"
      onFilter={(key, val) => saveServer('拜访管理_拜访明细_2', val)}
      extra={[{ name: '导出', type: 'primary', onClick: handleExport }]}
      editable={false}
      store={table}
      columns={columns}
      scroll={{ x: true }}
      // columnsFilter="拜访管理_拜访汇总"
      searchbar={{
        labelCol: { span: 6 },
        items: formColumns,
      }}
      summary={() => {
        return (
          <Table.Summary fixed>
            <Table.Summary.Row>
              <Table.Summary.Cell index={0} colSpan={2}>
                合计值
              </Table.Summary.Cell>
              <Table.Summary.Cell index={2}>{sumData.visitCount?.value}</Table.Summary.Cell>
              <Table.Summary.Cell index={3}>{sumData.clientCount?.value}</Table.Summary.Cell>
            </Table.Summary.Row>
          </Table.Summary>
        )
      }}
      expandable={{
        expandedRowRender: (record) => {
          return (
            <Card type="inner">
              <Table
                columnsFilter="拜访管理_拜访明细_3"
                onFilter={(key, val) => saveServer('拜访管理_拜访明细_3', val)}
                scroll={{ x: true }}
                dataSource={record.objectInfoList}
                columns={innerColumns}
                pagination={false}
              ></Table>
            </Card>
          )
        },
      }}
    ></Table>
  )
}

const Index = () => {
  return (
    <Page>
      <Tabs
        items={[
          {
            key: '1',
            label: '拜访明细',
            children: <VisitorDetail></VisitorDetail>,
          },
          {
            key: '2',
            label: '拜访汇总',
            children: <VisitorSummary></VisitorSummary>,
          },
        ]}
      ></Tabs>
    </Page>
  )
}

export default Index
