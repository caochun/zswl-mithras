
import { observer } from '@zswl/admin'

function Index({ detail }) {
  return (
    <EditDescription
      style={{ marginTop: 20 }}
      title="补充信息"
      detail={detail}
      canEdit={false}
      columns={[
        {
          title: '是否结束投放',
          dataIndex: 'isFinishPut',
          matchOption: 'trueOrFalse',
        },
        detail.isFinishPut && { title: '租金表收款日', dataIndex: 'defaultCollectionDay' },
      ]}
      column={3}
      contentStyle={{
        width: 200,
      }}
    />
  )
}

export default observer(Index)
