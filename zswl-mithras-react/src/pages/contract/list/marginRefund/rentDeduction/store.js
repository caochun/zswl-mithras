import { makeAutoObservable, history } from '@zswl/admin'
import Api from '../api'

class Store {
  constructor(contractId) {
    makeAutoObservable(this)
    this.contractId = contractId
  }
  dataSource
  retreatInfoId
  summary = {
    planCollectionAmount:'',
    principal:'',
    interest:'',
    collectionAmount:'',
    restAmount:'',
  }
  rentList = async (retreatInfoId) => {
    if(!retreatInfoId){
        return;
    }
    this.retreatInfoId = retreatInfoId
    this.getTableData()
  }
  refresh = async() => {
    if(!this.retreatInfoId){
        return;
    }
    this.getTableData()
  }
  getTableData = async() => {
    const res = await Api.rentList({ retreatInfoId:this.retreatInfoId })
    this.dataSource = res
    this.summary = res.reduce((pre, cur)=>{
      pre.planCollectionAmount = pre.planCollectionAmount + cur.planCollectionAmount || 0
      pre.principal = pre.principal + cur.principal
      pre.interest = pre.interest + cur.interest
      pre.collectionAmount = pre.collectionAmount + cur.collectionAmount || 0
      pre.restAmount = pre.restAmount + cur.restAmount
      return pre
    },{
      planCollectionAmount:0,
      principal:0,
      interest:0,
      collectionAmount:0,
      restAmount:0,
    })
  }
  visible = false
  flowList = []
  open = async() => {
    const res = await Api.getFlowNum({contractId:this.contractId})
    if(res){
      this.flowList = Object.entries(res).map(itm => ({label:itm[1],value:itm[0]}))
      this.visible = true
    }
  }
  flowNumDetail
  getFlowNumDetail = async (collectionId) => {
    if(!collectionId){
      return;
    }
    const res = await Api.getFlowNumDetail(collectionId)
    this.flowNumDetail = {
      ...res,
      retreatInfoId:this.retreatInfoId,
    }
    return res
  }
  close = () => this.visible = false
  add = async (clientName) => {
    if(this.flowNumDetail){
      await Api.rentAdd(JSON.parse(JSON.stringify({...this.flowNumDetail,clientName})))
      this.refresh()
      this.close()
    }
  }
  delRentItem = async (id) => {
    await Api.rentDel({id})
    this.refresh()
  }
}
export default Store
