import { makeAutoObservable } from '@zswl/admin'
import { FormStore, PageStore } from '@zswl/components'
import Api from '../api'
class Store {
	constructor() {
		makeAutoObservable(this)
	}

	templateData = {}
	treeDataTemp = []
	initLoop = (list) => {
		return list.map(item => {
			const {
				materialsData,
				projName,
				files,
				key,
				materialsName,
				fileName,
				fileId,
				fileTypes,
				fileTypeName,
				grey,
				size,
				...rest
			} = item
			const obj = {
				projName: materialsName || fileName || fileTypeName,
				key: key || fileId,
				isGrey: grey,
				size: size || 0,
				...rest,
			}
			if (materialsData || files || fileTypes) {
				obj.children = this.initLoop(materialsData || files || fileTypes)
			}
			return obj
		})
	}
	page = new PageStore({
		request: async (params) => {
			const detail = await Api.archivesFlow({
				id: params.id
			})
			this.templateData = detail
			const dataTest = this.initLoop(detail?.materials?.materialsData)
			this.treeDataTemp = dataTest
		}
	})
	form = new FormStore()
}
export default Store
