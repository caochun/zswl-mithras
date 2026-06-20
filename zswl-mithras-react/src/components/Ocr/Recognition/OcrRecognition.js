import { getQuery, history, observer } from '@zswl/admin'
import { Button, Form, Modal, ModalStore, Page, Tabs } from '@zswl/components'
import { useEffect, useMemo, useState } from 'react'
import FileUploadModal from '../FileUploadModal'

const Index = () => {
  const query = getQuery()
  const { businessType } = query
  const [activeTab, setActiveTab] = useState(businessType)
  const invoiceModal = useMemo(() => new ModalStore({}), [])
  const carModal = useMemo(() => new ModalStore({}), [])
  const defaultProps = {
    getContainer: false,
    open: true,
    closable: false,
    mask: false,
  }

  const items = [
    {
      label: '发票识别',
      key: 'invoice',
      children: <FileUploadModal type={'invoice'} store={invoiceModal} {...defaultProps} />,
    },
    {
      label: '车证识别',
      key: 'carCard',
      children: <FileUploadModal type={'carCard'} store={carModal} {...defaultProps} />,
    },
  ]
  return (
    <Page>
      <Tabs items={items} activeKey={activeTab} onChange={setActiveTab}></Tabs>
    </Page>
  )
}

export default observer(Index)
