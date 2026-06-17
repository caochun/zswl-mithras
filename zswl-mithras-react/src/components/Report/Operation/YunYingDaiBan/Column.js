import { InputColumn } from '@/components/Format'

export const ALL_COLUMNS = (store) => {
  const render = (value) => {
    if (value?.length) {
      return (
        <a
          onClick={() => {
            store.listDrawer.open(value)
          }}
        >
          {value?.length}
        </a>
      )
    }
    return 0
  }

  return [
    {
      title: '流程类型',
      dataIndex: 'processDisplay',
    },
    {
      title: '已到达待处理(数量)',
      children: [
        InputColumn({
          title: '经办',
          dataIndex: 'arriveYYJBProcessInstanceIds',
          align: 'right',
          render,
        }),
        InputColumn({
          title: '复核',
          dataIndex: 'arriveYYFHProcessInstanceIds',
          align: 'right',
          render,
        }),
        InputColumn({
          title: '运营部负责人',
          dataIndex: 'arriveYYFZRProcessInstanceIds',
          align: 'right',
          render,
        }),
        InputColumn({
          title: '合计',
          dataIndex: 'arriveHJProcessInstanceIds',
          align: 'right',
          render,
        }),
      ],
    },
    {
      title: '将到达(数量)',
      children: [
        InputColumn({
          title: '经办',
          dataIndex: 'willArriveYYJBProcessInstanceIds',
          align: 'right',
          render,
        }),
        InputColumn({
          title: '复核',
          dataIndex: 'willArriveYYFHProcessInstanceIds',
          align: 'right',
          render,
        }),
        InputColumn({
          title: '运营部负责人',
          dataIndex: 'willArriveYYFZRProcessInstanceIds',
          align: 'right',
          render,
        }),
        InputColumn({
          title: '合计',
          dataIndex: 'willArriveHJProcessInstanceIds',
          align: 'right',
          render,
        }),
      ],
    },
  ]
}
