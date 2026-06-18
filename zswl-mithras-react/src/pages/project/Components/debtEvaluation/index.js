import { Button, Form, Modal, ModalStore, Table } from '@zswl/components'
import { Drawer } from 'antd'
import { observer } from '@zswl/admin'
import { useState } from 'react'
import { CustomerDebtRat as DebtRat } from '@/components/Customer/RatingEntries'

const Index = ({ params }) => {
  const [open, setOpen] = useState(false)

  const showDrawer = () => {
    setOpen(true)
  }

  const onClose = () => {
    setOpen(false)
  }

  return (
    <>
      <Button type="link" onClick={showDrawer}>
        债项评级
      </Button>
      <Drawer title="债项评级" onClose={onClose} open={open} width={800} extra={[]}>
        <DebtRat />
      </Drawer>
    </>
  )
}

export default observer(Index)
