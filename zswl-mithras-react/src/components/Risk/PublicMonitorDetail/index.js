import { useEffect, useState } from 'react'
import { observer } from '@zswl/admin'
import { EditDescription } from '@/components/Table'
import { NoEnumFileTable } from '@/components/Table'
import Api from '@/api/risk/publicMonitor'
import Vector from '/public/assets/risk/monitoringAlertList/vector.svg'
import Frame from '/public/assets/risk/monitoringAlertList/Frame.svg'
import styles from './style.less'
import { Radio, Rate, Tooltip } from 'antd'
import { App } from '@zswl/components'
import IconFont from '@/components/Icon'

const warnLevelColor = ['', 'green', '#f7cf07', 'red']

const Index = ({ params, query }) => {
  const { id } = params
  const { taskActivityId, processInstanceId, canEditFlags } = query
  const canEditFlag = canEditFlags === 'true'
  const approvalCanEdit = ['riskControlManager', 'assetManagement'].includes(taskActivityId)
  const canEdit = canEditFlag || approvalCanEdit
  const [detail, setDetail] = useState({})

  const nameColumns = [
    {
      title: '预警编号',
      dataIndex: 'warnCode',
      editable: canEditFlag,
    },

    {
      title: '企业名称',
      dataIndex: 'chiName',
      editable: canEditFlag,
    },
    {
      title: '信息发布时间',
      dataIndex: 'dataTime',
      editable: canEditFlag,
    },
    {
      title: '预警信号',
      dataIndex: 'warnLevel',
      editable: false,
      width: 120,
      matchOption: 'warnLevelEnum',
      render: (val) => {
        if (!val) return '-'
        return (
          <Tooltip title={App.matchOption('warnLevelEnum', val).label}>
            <IconFont type="icon-yujingxinhaodeng" style={{ color: warnLevelColor[val], fontSize: 18 }}></IconFont>
            {/* {warnLevelMatch[val]} */}
          </Tooltip>
        )
      },
    },
    {
      title: '预警星级',
      dataIndex: 'warnStar',
      width: 160,
      editable: false,
      render: (val) => {
        return <Rate value={val} disabled count={3} style={{ fontSize: 18 }}></Rate>
      },
    },

    {
      title: '处置状态',
      dataIndex: 'handleStatus',
      matchOption: 'riskControlOpinionHandleStatus',
      editable: false,
    },
    {
      title: '预警指标',
      dataIndex: 'riskType',
      editable: false,
    },

    {
      title: '舆情标题',
      dataIndex: 'title',
      span: 2,
      editable: false,
      render: (val, record) => {
        return (
          <div>
            {val}{' '}
            {record.linkAddress && (
              <a href={record.linkAddress} target="_blank">
                查看原文
              </a>
            )}
          </div>
        )
      },
    },
    {
      title: '是否处置',
      dataIndex: 'handleResult',
      requiredMark: true,
      matchOption: [
        { label: '处置', value: 1 },
        { label: '关闭', value: 0 },
      ],
      editable: approvalCanEdit && {
        element: (
          <Radio.Group>
            <Radio value={1}>处置</Radio>
            <Radio value={0}>关闭</Radio>
          </Radio.Group>
        ),
        rules: [{ required: true, message: '请选择' }],
      },
    },
    {
      title: '处置意见',
      dataIndex: 'advisement',
      requiredMark: true,
      editable: approvalCanEdit && {
        rules: [{ required: true, message: '请输入' }],
      },
    },
  ]
  const getDetail = async () => {
    const res = await Api.postMonitorWarnDetail({ id })
    setDetail(res ?? {})
  }

  const saveData = async (values) => {
    await Api.postMonitorModify({
      ...values,
      processInstanceId,
      id,
    })
    getDetail()
  }

  useEffect(() => {
    id && getDetail()
  }, [id])

  return (
    <div>
      <EditDescription detail={detail} saveData={saveData} canEdit={canEdit} columns={nameColumns} />
      <div style={{ marginTop: 20 }}></div>
      <NoEnumFileTable
        title={'附件'}
        canEdit={canEdit}
        params={{
          moduleType: 'RISK_WARN',
          mainId: id,
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </div>
  )
}
export default observer(Index)
