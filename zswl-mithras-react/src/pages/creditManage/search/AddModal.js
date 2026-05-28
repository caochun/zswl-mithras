import { observer } from '@zswl/admin'
import { Button, Form, Modal, ModalStore, Select } from '@zswl/components'
import { DatePicker, Input, message } from 'antd'
import { useMemo, useRef, useState } from 'react'
import creditReportApi from '@/api/credit/creditReportApi'
import { hasJob, isAssetJon, isProjmanager } from '@/utils'
import creditSearchProjectApi from '@/api/credit/creditSearchProjectApi'
import { re } from 'mathjs'

const { Item } = Form

function AddModal({ store }) {
  // 运营管理部、资产管理岗、法务部负责人、风险管理部、项目经理
  const canAdd = isProjmanager() || isAssetJon()
  //业务类型 PROJ_ESTABLISH:项目立项 PROJ_REVIEW:项目评审 GROUP_CREDIT_ESTABLISH:授信立项 GROUP_CREDIT_REVIEW：授信评审 PAYMENT：付款申请
  const { bizSource, ...restParams } = store.params
  const isProject = [
    'PROJ_ESTABLISH',
    'PROJ_REVIEW',
    'GROUP_CREDIT_ESTABLISH',
    'GROUP_CREDIT_REVIEW',
    'PAYMENT',
  ].includes(bizSource)
  const isClient = ['clientList'].includes(bizSource)
  const modal = useMemo(
    () =>
      new ModalStore({
        onOpen: (values) => {
          setProjectInfos([])
          return values
        },
        onFinish: async (values) => {
          await creditReportApi.postBaseAdd({ ...values })
          message.success('新增成功')
          setProjectInfos([])
          modal.close()
          store?.table?.search?.()
        },
      }),
    []
  )

  const [projectInfos, setProjectInfos] = useState([])
  const onClientChange = async (clientId) => {
    const formStore = modal.getFormStore()
    if (!clientId) return
    if (isProject) {
      const clientInfo = clientInfos.find((item) => item.value === clientId)
      console.log('clientInfo: ', clientInfo)
      formStore.setFieldsValue?.({
        clientId: clientInfo?.value,
        clientName: clientInfo?.label,
        cscCode: clientInfo?.cscCode,
        zhongZhengCode: clientInfo?.zhongZhengCode,
      })
      return
    }
    const res = await creditReportApi.getBaseShowCreditReportByClientId({ clientId })
    setProjectInfos(res?.projectInfos || [])
    formStore?.setFieldsValue?.({
      clientId: res?.clientId,
      clientName: res?.clientName,
      cscCode: res?.cscCode,
      zhongZhengCode: res?.zhongZhengCode,
      selectVersion: res?.selectVersion,
      reportFormat: res?.reportFormat,
    })
  }
  const [clientInfos, setClientInfos] = useState([])
  const getClientOptions = async (clientName) => {
    const res = await creditReportApi.getBaseClientInfo({ clientName })
    const newList = res?.map((item) => ({ value: item.clientId, label: item.clientName })) || []
    setClientInfos(newList)
  }
  const projChange = (projId) => {
    const projInfo = projectInfos.find((item) => item.projId === projId)
    if (projInfo) {
      modal.getFormStore()?.setFieldsValue?.({
        projName: projInfo.projName,
        projCode: projInfo.projCode,
        projIdDataType: projInfo.projIdDataType,
      })
    }
  }
  const openModal = async () => {
    if (isProject) {
      const { clientInfos, projectName, ...rest } =
        await creditSearchProjectApi.postProjectShowCreditReportByProjId({
          projectId: restParams.projectId,
          bizType: bizSource,
        })
      setClientInfos(
        clientInfos.map((item) => ({ ...item, value: item.clientId, label: item.clientName })) || []
      )
      console.log('projectName: ', projectName)
      modal.open({ ...rest, projName: projectName, projIdDataType: bizSource })
      return
    }
    if (isClient) {
      console.log('restParams: ', restParams)
      modal.open(restParams)
      onClientChange(restParams.clientId)
      return
    }
    getClientOptions()
    modal.open()
  }
  return (
    <>
      <Button.Add onClick={openModal} key="addCreditQuery">
        新增查询
      </Button.Add>
      <Modal
        title="新增征信报告查询"
        width={720}
        store={modal}
        destroyOnClose
        onCancel={() => {
          setProjectInfos([])
          modal.close()
        }}
      >
        <Form labelCol={{ span: 6 }}>
          <Item
            name="clientId"
            label="客户名称"
            rules={[{ required: true, message: '请选择' }]}
            hidden={isClient}
          >
            <Select options={clientInfos} onChange={onClientChange} />
          </Item>
          {/* 隐藏字段用于提交 */}
          <Item name="clientName" hidden={!isClient} label="客户名称">
            <Input disabled />
          </Item>
          <Item name="cscCode" label="统一社会信用代码" required>
            <Input disabled placeholder="自动反显" />
          </Item>
          <Item name="zhongZhengCode" label="中征码">
            <Input disabled placeholder="自动反显" />
          </Item>
          <Item
            name="authorizationBeganDate"
            label="授权起始日"
            required
            rules={[{ required: true, message: '请选择' }]}
            transform={(value) => value && value?.format('YYYY-MM-DD')}
          >
            <DatePicker format="YYYY-MM-DD" />
          </Item>
          <Item
            name="projId"
            label="关联项目名称"
            required
            rules={[{ required: true, message: '请选择' }]}
            hidden={isProject}
          >
            <Select
              options={projectInfos.map((item) => ({ value: item.projId, label: item.projName }))}
              placeholder="请选择"
              onChange={projChange}
            />
          </Item>
          {/* 隐藏字段用于提交 */}
          <Item name="projName" hidden={!isProject} label="关联项目名称">
            <Input disabled />
          </Item>
          <Item name="projCode" label="项目编号" hidden>
            <Input />
          </Item>
          <Item name="projIdDataType" label="项目ID类型" hidden>
            <Input />
          </Item>
          <Item name="selectGoal" label="查询目的" rules={[{ required: true, message: '请选择' }]}>
            <Select options={'searchGoalEnum'} placeholder="请选择" />
          </Item>
          <Item
            name="selectVersion"
            label="查询版本"
            rules={[{ required: true, message: '请选择' }]}
          >
            <Input disabled placeholder="企业信用报告（授信机构版）" />
          </Item>
          <Item name="reportFormat" label="信用报告封装格式">
            <Input disabled placeholder="html格式" />
          </Item>
        </Form>
      </Modal>
    </>
  )
}

export default observer(AddModal)
