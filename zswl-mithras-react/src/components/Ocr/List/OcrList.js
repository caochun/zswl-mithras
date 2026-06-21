import { observer } from '@zswl/admin'
import { Page, Tabs } from '@zswl/components'
import Invoice from './Invoice'
import CarCard from './CarCard'

const OcrList = () => {
  const items = [
    { label: '发票识别', key: 'invoice', children: <Invoice /> },
    { label: '车证识别', key: 'carCard', children: <CarCard /> },
  ]
  return (
    <Page>
      <Tabs items={items}></Tabs>
    </Page>
  )
}

export default observer(OcrList)
