<script setup lang="ts">
import BaseCard from '/@/components/base-card/index.vue'
import CaseTemplateDetail from '/@/views/case-management/case-management-feature/components/CaseTemplateDetail.vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { useRoute, useRouter } from 'vue-router'
import { computed, ref, watchEffect } from 'vue'
import { CaseManagementRouteEnum } from '/@/enums/route-enum.ts'
import useLeaveUnSaveTip from '/@/hooks/use-leave-un-save-tip.ts'
import { useForm } from '@alova/scene-vue'
import {
    createCaseRequest,
    updateCaseRequest,
} from '/@/api/modules/case-management/feature-case.ts'

const { setState } = useLeaveUnSaveTip()
setState(false)
const { t } = useI18n()
const route = useRoute()
const router = useRouter()
// const caseDetailInfo = ref({
//     request: {} as CreateOrUpdateCase,
//     fileList: [],
// })
const title = ref('')
const okText = ref<string>('')
const caseModuleDetailRef = ref()
const isContinueFlag = ref(false)
const createSuccessId = ref<string>('')
const isEdit = computed(() => !!route.query.id)

const isFormReviewCase = computed(() => route.query.reviewId)

const cancelHandler = () => {
    router.back()
}
const saveHandler = (isContinue = false, isReview = false) => {
    const { isPass } = caseModuleDetailRef.value
    isContinueFlag.value = isContinue
    if (isPass) {
        return save(isReview, isContinue)
    }
}
const {
    loading: submitting,
    send: submit,
    form: caseDetailInfo,
} = useForm(
    (formData) => {
        // 可以在此转换表单数据并提交
        return route.params.mode === 'edit'
            ? updateCaseRequest(formData)
            : createCaseRequest(formData)
    },
    {
        // 初始化表单数据
        initialForm: {
            request: {},
            files: [],
        },
    },
)
const save = (isReview: boolean, isContinue: boolean) => {
    if (route.params.mode === 'edit') {
        // todo 编辑用例
        submit()
        window.$message.success(t('caseManagement.featureCase.editSuccess'))
        router.push({
            name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE,
            query: { organizationId: route.query.organizationId, projectId: route.query.projectId },
        })
        setState(true)
    } else {
        // 创建并关联
        if (isReview) {
            caseDetailInfo.value.request.reviewId = route.query.reviewId
        }
        // todo 创建用例
        console.log(caseDetailInfo.value)
        submit().then((res) => {
            console.log(res)
            window.$message.success(
                route.params.mode === 'copy'
                    ? t('ms.description.copySuccess')
                    : t('common.addSuccess'),
            )
        })
        if (isContinue) {
            window.$message.success(t('caseManagement.featureCase.addSuccess'))
            caseModuleDetailRef.value.resetForm()
            return
        }

        if (isReview) {
            router.back()
            return
        }
        if (!route.query.id) {
            router.push({
                name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE_CREATE_SUCCESS,
                query: {
                    id: createSuccessId.value,
                    ...route.query,
                },
            })
        } else {
            router.push({
                name: CaseManagementRouteEnum.CASE_MANAGEMENT_CASE,
                query: {
                    organizationId: route.query.organizationId,
                    projectId: route.query.projectId,
                },
            })
        }
        setState(true)
    }
}
watchEffect(() => {
    if (route.params.mode === 'edit') {
        title.value = t('caseManagement.featureCase.updateCase')
        okText.value = t('mscard.defaultUpdate')
    } else if (route.params.mode === 'copy') {
        title.value = t('caseManagement.featureCase.copyCase')
        okText.value = t('mscard.defaultConfirm')
    } else {
        title.value = t('caseManagement.featureCase.creatingCase')
        okText.value = t('mscard.defaultConfirm')
    }
})
</script>

<template>
    <base-card :title="title" :is-edit="isEdit" :hide-continue="!isEdit" :loading="submitting">
        <case-template-detail
            ref="caseModuleDetailRef"
            :case-id="(route.query.id as string) || ''"
            v-model:form-mode-value="caseDetailInfo"
        />
        <template #footerRight>
            <div class="flex justify-end gap-[16px]">
                <n-button @click="cancelHandler">{{ t('mscard.defaultCancelText') }}</n-button>
                <n-button v-if="!isEdit" @click="saveHandler(true)">
                    {{ t('mscard.defaultSaveAndContinueText') }}
                </n-button>
                <n-button v-if="!isFormReviewCase" type="primary" @click="saveHandler(false)">
                    {{ okText }}
                </n-button>
                <n-button v-if="isFormReviewCase" type="primary" @click="saveHandler(false, true)">
                    {{ t('caseManagement.featureCase.createAndLink') }}
                </n-button>
            </div>
        </template>
    </base-card>
</template>

<style scoped></style>
