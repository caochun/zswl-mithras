import EditDescription from '@/components/Table/EditDescription'
import { NoEnumFileTable } from '@/components'
import { observer, history } from '@zswl/admin'
import { Page, App } from '@zswl/components'
import moment from 'moment'
import { useState, useEffect, useRef } from 'react'
import { Rate, Tooltip, Button, message } from 'antd'
import IconFont from '@/components/Icon'
import Api from '@/api/risk/publicMonitor'

function Index({ query, params }) {
  const canEditFlag = true
  const approvalCanEdit = true
  const warnLevelColor = ['#ced4d9', 'green', '#f7cf07', 'red']
  const { id } = params
  const { clientName, creditCode } = query
  const infoRef = useRef()
  const [warnStar, setWarnStar] = useState(0)
  const [visible, setVisible] = useState(false)
  const [baseEdit, setBaseEdit] = useState(false)
  const [isSave, setSave] = useState(false)
  const page = Page.useStore(
    {
      request: async () => {
        const data = await Api.postMonitorDetail({ id })
        if (data && data.id) {
          setWarnStar(data.warnStar)
          setVisible(data.relationType === 'QT')
          setSave(true)
          return {
            ...data,
            clientName: data.clientName || clientName,
            infoPublDate: data.infoPublDate ? moment(data.infoPublDate) : '',
          }
        }
        return {
          id,
          clientName,
          creditCode,
          handleStatus: '未提交',
          infoPublDate: moment().startOf('day'),
          handleResult: 0,
          relationType: '', //默认值确定是否展示描述
        }
      },
    },
    [id]
  )

  const detail = page.getData()
  const ALL_COLUMNS = [
    {
      title: 'ID',
      dataIndex: 'id',
      editable: false,
      requiredMark: true,
    },
    {
      title: '标题',
      dataIndex: 'title',
      editable: {
        rules: [{ required: true, message: '请输入标题' }],
      },
      requiredMark: true,
    },
    {
      title: '客户名称',
      dataIndex: 'clientName',
      editable: false,
      requiredMark: true,
    },
    {
      title: '统一社会信用代码',
      dataIndex: 'creditCode',
      editable: {
        rules: [{ required: true, message: '请输入统一社会信用代码' }],
      },
    },
    {
      title: '关联关系描述',
      dataIndex: 'relationType',
      editable: canEditFlag,
      matchOption: 'riskRelationTypeEnum',
    },
    {
      title: '描述说明',
      dataIndex: 'relationDescription',
      editable: canEditFlag,
      hidden: !visible,
    },
    {
      title: '关联主体',
      dataIndex: 'relateCompanyName',
      editable: canEditFlag,
    },
    {
      title: '关联主体统一社会信用代码',
      dataIndex: 'relateCompanyCode',
      editable: canEditFlag,
    },
    {
      title: '状态',
      dataIndex: 'handleStatus',
      editable: false,
      requiredMark: true,
    },
    {
      title: '预警星级',
      dataIndex: 'warnStar',
      width: 160,
      editable: false,
      requiredMark: true,
      render: (val) => {
        return <Rate value={val} count={3} disabled={!baseEdit} style={{ fontSize: 18 }} onChange={(v) => setWarnStar(v)}></Rate>
      },
    },
    {
      title: '预警信号',
      dataIndex: 'warnLevel',
      editable: false,
      width: 120,
      matchOption: 'warnLevelEnum',
      requiredMark: true,
      render: () => {
        return (
          <Tooltip title={App.matchOption('warnLevelEnum', warnStar).label}>
            <IconFont type="icon-yujingxinhaodeng" style={{ color: warnLevelColor[warnStar], fontSize: 18 }}></IconFont>
          </Tooltip>
        )
      },
    },
    {
      title: '主体机构代码',
      dataIndex: 'majorOrgCode',
      editable: canEditFlag,
    },
    {
      title: '信息发布日期',
      dataIndex: 'infoPublDate',
      requiredMark: true,
      editable: canEditFlag && {
        rules: [{ required: true, message: '请选择信息发布日期' }],
      },
      dateFormat: 'YYYY-MM-DD',
    },
    {
      title: '链接',
      dataIndex: 'linkAddress',
      requiredMark: true,
      editable: canEditFlag && {
        rules: [{ required: true, message: '请输入链接' }],
      },
    },
    {
      title: '是否处置',
      dataIndex: 'handleResult',
      requiredMark: true,
      matchOption: [
        { label: '处置', value: 0 },
        { label: '关闭', value: 1 },
      ],
      editable: false,
    },
    {
      title: '处置意见',
      dataIndex: 'advisement',
      requiredMark: true,
      editable: approvalCanEdit && {
        rules: [{ required: true, message: '请输入处置意见' }],
      },
    },
  ]
  useEffect(() => {
    if (warnStar) {
      page.setData({
        warnStar,
      })
    }
  }, [warnStar])
  const handleChange = (val, vals) => {
    if ('relationType' in val) {
      setVisible(val.relationType === 'QT')
    }
  }
  const saveData = async (values) => {
    if (warnStar === 0) {
      message.error('请选择风险星级')
      throw new Error('请选择风险星级')
    }
    const infoData = {
      ...values,
      warnStar,
      id,
      clientName,
      warnLevel: warnStar,
      infoPublDate: values.infoPublDate.format('YYYY-MM-DD'),
      handleStatus: '未提交',
      handleResult: 0,
    }
    const res = await Api.postMonitorSave(infoData)
    setSave(true)
    page.setData({
      ...infoData,
      infoPublDate: moment(infoData.infoPublDate),
    })
  }
  const handleSubmit = async () => {
    if (warnStar > 0) {
      const infoData = page.getData()
      const res = await Api.postMonitorSubmit({
        ...infoData,
        infoPublDate: infoData.infoPublDate.format('YYYY-MM-DD'),
      })
      if (!res) {
        message.success('提交成功')
        history.push('/risk/publicMonitor')
      }
    } else {
      message.warn('请选择预警星级')
    }
  }
  const handleEditChange = (val) => setBaseEdit(val)

  const handleBeforeUpload = () => {
    if (!isSave) {
      message.error('请先保存舆情信息')
      return false
    }
    return true
  }

  return (
    <Page store={page} header={null} params={{ id }}>
      <EditDescription
        ref={infoRef}
        onEditStatusChange={handleEditChange}
        saveData={saveData}
        canEdit={true}
        columns={ALL_COLUMNS}
        detail={detail}
        editBtnType="default"
        btnSuffix={
          !baseEdit &&
          isSave && (
            <Button type="primary" onClick={handleSubmit}>
              提交
            </Button>
          )
        }
        formProps={{
          onValuesChange: handleChange,
        }}
      />
      <div style={{ marginTop: 20 }}></div>
      <NoEnumFileTable
        title={'附件'}
        canEdit={true}
        onBeforeUpload={handleBeforeUpload}
        params={{
          moduleType: 'RISK_OPINION',
          mainId: id,
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </Page>
  )
}

export default observer(Index)
