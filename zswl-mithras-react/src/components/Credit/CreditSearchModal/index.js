import { Button, Drawer } from '@zswl/components'
import { observer } from '@zswl/admin'
import SearchList from '@/pages/creditManage/search'
import { useState, cloneElement } from 'react'

const CreditSearchModal = ({
  trigger,
  title = '征信查询',
  width = 1200,
  onOpen,
  onClose,
  params = {
    bizSource: 'creditList',
  },
}) => {
  const [open, setOpen] = useState(false)

  const showDrawer = () => {
    setOpen(true)
    onOpen?.()
  }

  const handleClose = () => {
    setOpen(false)
    onClose?.()
  }

  return (
    <>
      {trigger ? (
        cloneElement(trigger, { onClick: showDrawer })
      ) : (
        <Button type="link" onClick={showDrawer}>
          征信报告查询
        </Button>
      )}
      <Drawer title={title} onClose={handleClose} open={open} width={width} destroyOnClose>
        <SearchList params={params} onClose={handleClose} />
      </Drawer>
    </>
  )
}

export default observer(CreditSearchModal)
