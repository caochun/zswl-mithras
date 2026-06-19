import { Select, App } from '@zswl/components'
import { MatchFormat } from '@/components/Format'
import { history } from '@zswl/admin'

const { optionsType } = App.getData()
const ALL_COLUMNS = [
  {
    title: '租后检查计划名称',
    dataIndex: 'planName',
    width: 330,
    fixed: 'left',
    actions({ planName, checkPlanClientId, id, planType }) {
      console.log()
      return [
        {
          name: planName,
          onClick: () => {
            // if (['CHECKING', 'FINISH'].includes($planStatus)) {
              if (planType === '一般检查计划') {
                history.push(`/afterLease/checkPlan/commonTemplate/${id}`)
                return
              }
              history.push(`/afterLease/checkPlan/planDetail/${checkPlanClientId}`)
            }
            // else {
            //   message.warn('只有检查中、检查完毕才能查看详情')
            // }
          // }
        },
      ]
    },
  },
  {
    title: '客户名称',
    dataIndex: 'clientName',
    width: 200,
  },
  {
    title: '计划类型',
    dataIndex: 'planType',
    width: 160,
  },
  {
    title: '项目主办',
    dataIndex: 'belongSponsorUserName',
    requiredMark: true,
  },
  {
    title: '业务部门',
    requiredMark: true,
    dataIndex: 'belongDeptName',
    width: 250,
  },
  {
    title: '资产经理',
    width: 180,
    dataIndex: 'riskManagerName',
  },
  {
    title: '检查形式',
    width: 180,
    dataIndex: 'checkWay',
  },
  {
    title: '检查报告模板',
    dataIndex: 'reportType',
    width: 230,
  },
  {
    title: '租后检查截止日',
    dataIndex: 'deadLine',
    width: 130,
  },
  {
    title: '现场检查日期',
    dataIndex: 'checkTime',
    width: 300,
  },
  {
    title: '报告提交日期',
    dataIndex: 'commitTime',
  },
  {
    title: '当前状态',
    width: 180,
    dataIndex: 'checkStatus',
    // editable: {
    //   element: (
    //     <Select
    //       options={optionsType.checkStatus}
    //     />
    //   ),
    // },
    // render: (val) => <MatchFormat value={val} matchOption="checkStatus" />,
  },
  {
    title: '是否逾期',
    width: 100,
    dataIndex: 'overdue',
    editable: {
      element: (
        <Select
          options={[{value:'0',label:'否'},{value:'1',label:'是'}]}
        />
      ),
    },
    render: (val) => val === '1' ? '是' : '否'
  },
  { title: '逾期天数', dataIndex: 'overdueDays', width: 100 },
]
export default ALL_COLUMNS
