import { observer } from '@zswl/admin'
import CollectionTable from './CollectionTable'

const RecordList = ({ store }) => {
  // return (
  //   <Tabs defaultActiveKey={1}>
  //     <TabPane tab="收款记录" key={0}>
  //       <CollectionTable />
  //     </TabPane>
  //     <TabPane tab="核销记录" key={1}>
  //       <WriteOffTable />
  //     </TabPane>
  //   </Tabs>
  // )
  return <CollectionTable store={store}/>
}

export default observer(RecordList)
