import { observer } from '@zswl/admin'

const Index = () => {
  const columns = [
    { title: '定量得分', dataIndex: 'score' },
    { title: '定性得分', dataIndex: 'score' },
    { title: '模型得分', dataIndex: 'score' },
    { title: '违约率', dataIndex: 'score' },
    { title: '初评等级', dataIndex: 'score' },
    { title: '评级结果', dataIndex: 'score' },
  ]
  return (
    <>
      <div>摘要信息</div>
      <Row gutter={16}>
        {columns.map((item) => {
          return (
            <Col span={4} key={item.dataIndex}>
              <div>{item.title}</div>
              <div>100</div>
            </Col>
          )
        })}
      </Row>
    </>
  )
}

export default observer(Index)
