<script setup lang="ts">
import BasePagination from '/@/components/base-pagination/index.vue'
import { DataTableColumns, NButton } from 'naive-ui'
import { CaseManagementTable } from '/@/models/case-management/feature-case.ts'
import { usePagination } from '@alova/scene-vue'
import { getCaseList } from '/@/api/modules/case-management/feature-case.ts'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { h, ref } from 'vue'
const { t } = useI18n()
const activeDetailId = ref<string>('')
const activeCaseIndex = ref<number>(0)
const columns: DataTableColumns<CaseManagementTable> = [
    {
        title: t('caseManagement.featureCase.tableColumnID'),
        key: 'num',
        width: 200,
        render(record, rowIndex) {
            return h(
                'span',
                { onClick: () => showCaseDetail(record.id, rowIndex) },
                { default: () => record.num },
            )
        },
    },

    {
        title: t('caseManagement.featureCase.tableColumnName'),
        key: 'name',
        width: 300,
        render(record, rowIndex) {
            return h(
                NButton,
                { text: true, onClick: () => showCaseDetail(record.id, rowIndex) },
                { default: () => record.name },
            )
        },
    },
    {
        title: t('caseManagement.featureCase.tableColumnLevel'),
        key: 'caseLevel',
        width: 200,
    },
    {
        title: t('caseManagement.featureCase.tableColumnReviewResult'),
        key: 'reviewStatus',
        width: 200,
    },
    {
        title: t('caseManagement.featureCase.tableColumnModule'),
        key: 'moduleId',
        width: 300,
    },
    {
        title: t('caseManagement.featureCase.tableColumnTag'),
        key: 'tags',
    },
    {
        title: t('caseManagement.featureCase.tableColumnUpdateUser'),
        key: 'updateUserName',
        width: 200,
        render(record) {
            return h('span', {}, { default: () => record.updateUserName || '-' })
        },
    },
    {
        title: t('caseManagement.featureCase.tableColumnUpdateTime'),
        key: 'updateTime',
        width: 200,
    },
    {
        title: t('caseManagement.featureCase.tableColumnCreateUser'),
        key: 'createUserName',
        width: 200,
    },
    {
        title: t('caseManagement.featureCase.tableColumnCreateTime'),
        key: 'createTime',
        width: 200,
    },
    {
        title: t('caseManagement.featureCase.tableColumnActions'),
        key: 'operation',
        width: 260,
    },
]
const { loading, data, page, pageSize } = usePagination(
    // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
    (page, pageSize) => {
        const param = {
            current: page,
            pageSize,
        }
        return getCaseList(param)
    },
    {
        // 请求前的初始数据（接口返回的数据格式）
        initialData: {
            total: 0,
            data: [],
        },
        initialPage: 1, // 初始页码，默认为1
        initialPageSize: 10, // 初始每页数据条数，默认为10
        data: (response) => response.records,
    },
)
// 抽屉详情
const showCaseDetail = (id: string, index: number) => {
    activeDetailId.value = id
    activeCaseIndex.value = index
}
</script>

<template>
    <n-spin :show="loading">
        <n-data-table :columns="columns" :data="data" />
        <base-pagination :page="page" :page-size="pageSize" />
    </n-spin>
</template>

<style scoped></style>
