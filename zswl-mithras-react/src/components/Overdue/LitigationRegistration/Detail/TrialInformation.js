import { DateColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Form } from '@zswl/components'
import { Row } from 'antd'

const Index = () => {
  return (
    <div>
      <h2>审判信息</h2>
      <h3>一审</h3>
      <Form.Group
        column={3}
        items={[
          { title: '案号', dataIndex: 'fcaseNo' },
          DateColumn({ title: '立案时间', dataIndex: 'ffilingDate' }),
          { title: '受审法院', dataIndex: 'facceptingCourt' },
          DateColumn({ title: '开庭日期', dataIndex: 'fhearingDate' }),
          DateColumn({ title: '判决时间', dataIndex: 'fjudgmentDate' }),
        ]}
      />
      <h3>二审</h3>
      <Form.Group
        column={3}
        items={[
          { title: '案号', dataIndex: 'scaseNo' },
          DateColumn({ title: '立案时间', dataIndex: 'sfilingDate' }),
          { title: '受审法院', dataIndex: 'sacceptingCourt' },
          DateColumn({ title: '开庭日期', dataIndex: 'shearingDate' }),
          DateColumn({ title: '判决时间', dataIndex: 'sjudgmentDate' }),
        ]}
      />
      <h3>再审</h3>
      <Form.Group
        column={3}
        items={[
          { title: '案号', dataIndex: 'tcaseNo' },
          DateColumn({ title: '立案时间', dataIndex: 'tfilingDate' }),
          { title: '受审法院', dataIndex: 'tacceptingCourt' },
          DateColumn({ title: '开庭日期', dataIndex: 'thearingDate' }),
          DateColumn({ title: '判决时间', dataIndex: 'tjudgmentDate' }),
        ]}
      />
      <h3>执行</h3>
      <Form.Group
        column={3}
        items={[
          { title: '执行案号', dataIndex: 'executionNo' },
          DateColumn({ title: '执行时间', dataIndex: 'executionDate' }),
        ]}
      />
      <h3>保全</h3>
      <Form.Group
        column={3}
        items={[
          DateColumn({ label: '保全完成日期', dataIndex: 'preservationCompletionDate' }),
          DateColumn({ label: '查封到期日', dataIndex: 'sealingExpirationDate' }),
        ]}
      />
    </div>
  )
}

export default observer(Index)
