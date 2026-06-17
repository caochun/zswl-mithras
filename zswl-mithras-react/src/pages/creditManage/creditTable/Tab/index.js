import { useState, cloneElement, useMemo, useCallback } from 'react'
import { observer } from '@zswl/admin'
import { Space, message, Input } from 'antd'
import { Button, Form, Modal, ModalStore, Tabs } from '@zswl/components'
import IconFont from '@/components/Icon'
import styles from '@/pages/creditManage/creditTable/Tab/index.less'
import { tabList } from '@/pages/creditManage/creditTable/Tab/config'
import Api from '@/api/credit/creditTable'
import { ReadOnly } from '@/components'
import Reason from '../Components/Reason'
import DataFileList from '../Components/DataFileList'
import Export from '@/components/Actions/Export'

const Index = (props) => {
  const {
    channel,
    batchId,
    accountId,
    businessKey,
    businessVersion,
    showActionColumn,
    isFormApproval,
    showSearch,
    processInstanceId,
    canEdit = true,
  } = props

  // 账户维度，Tab特殊处理
  const n_tabList = channel === 'EFFECT' ? tabList.slice(1) : tabList
  const [curTab, setCurTab] = useState(channel === 'EFFECT' ? '2' : '1')
  const baseParams = {
    channel,
    procBusinessKey: channel === 'PROC' ? businessKey : undefined, // 审批流里
    batchId: channel === 'PROC_BATCH' ? batchId : undefined, // 批次查询
    accountId: channel === 'EFFECT' ? accountId : undefined, // 账户维度穿透
    businessVersion,
  }
  const onTabClick = (key) => {
    setCurTab(key)
  }
  const sync = async () => {
    await Api.sync()
    message.success('同步成功')
    setCurTab('-1')
    setTimeout(() => {
      setCurTab('1')
    }, 0)
  }
  const submit = async (values) => {
    await Api.submit(values)
    message.success('提交成功')
    modal.close()
    setCurTab('-1')
    setTimeout(() => {
      setCurTab('1')
    }, 0)
  }
  const modal = useMemo(
    () =>
      new ModalStore({
        onFinish: submit,
        onOpen: async () => {
          const batchNo = await Api.getBatchNumber()
          return { batchNo }
        },
      }),
    []
  )

  const batchNo = modal.getInitialValues()?.batchNo

  const [form] = Form.useForm()
  const items = useMemo(() => {
    return n_tabList.map((item) => {
      return {
        ...item,
        children: cloneElement(item.children, {
          ...props,
          componentKey: item.key,
          curTab,
          canEdit,
          baseParams,
          showSearch,
        }),
      }
    })
  }, [curTab, props])
  const exportExcel = async () => {
    const params = {
      channel,
      procBusinessKey: channel === 'PROC' ? businessKey : undefined, // 审批流里
      batchId: channel === 'PROC_BATCH' ? batchId : undefined, // 批次查询
      accountId: channel === 'EFFECT' ? accountId : undefined, // 账户维度穿透
    }
    await Api.exportExcel(params)
  }
  return (
    <div className={styles.wrap}>
      <Reason isFormApproval={isFormApproval} processInstanceId={processInstanceId} />
      <Tabs
        destroyInactiveTabPane
        onTabClick={onTabClick}
        activeKey={curTab}
        items={items}
        tabBarExtraContent={[
          channel !== 'EFFECT' && <Export onClick={exportExcel} />,
          !isFormApproval && showActionColumn && canEdit && (
            <Button type="primary" onClick={() => modal.open()}>
              <IconFont type="icon-icon_submit" />
              提交审批
            </Button>
          ),
        ]}
      />

      <Modal title="提交审批" store={modal} destroyOnClose width={1000}>
        <Form form={form} preserve={false}>
          <Form.Item label="报送批次" name={'batchNo'}>
            <ReadOnly />
          </Form.Item>
          <Form.Item label="报送说明" name="reportDescription">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
        {batchNo && <DataFileList batchNo={batchNo}></DataFileList>}
      </Modal>
    </div>
  )
}

export default observer(Index)
