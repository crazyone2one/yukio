<script setup lang="ts">
import { useAppStore } from '/@/store'
import { ComponentPublicInstance, computed, ref, watchEffect } from 'vue'
import BaseCard from '/@/components/base-card/index.vue'
import { useI18n } from '/@/hooks/use-i18n.ts'
import { useRoute, useRouter } from 'vue-router'
import { ActionTemplateManage, DefinedFieldItem } from '/@/models/setting/template.ts'
import { FormInst } from 'naive-ui'
import CaseTemplateLeftContent from '/@/views/setting/organization/template/components/CaseTemplateLeftContent.vue'
import { getTemplateName } from '/@/views/setting/organization/template/components/field-setting.ts'

const props = defineProps<{
    mode: 'organization' | 'project'
}>()
type refItem = Element | ComponentPublicInstance | null
const appStore = useAppStore()
const { t } = useI18n()
const route = useRoute()
const router = useRouter()

const title = ref('')
const currentOrgId = computed(() => appStore.currentOrgId)
const currentProjectId = computed(() => appStore.currentProjectId)
const initTemplateForm: ActionTemplateManage = {
    id: '',
    name: '',
    remark: '',
    scopeId: props.mode === 'organization' ? currentOrgId.value : currentProjectId.value,
    enableThirdPart: false,
    platForm: '',
}
const initBugForm = {
    name: '',
    description: '',
}
const formRef = ref<FormInst | null>(null)
const defectForm = ref({ ...initBugForm })
const isEdit = computed(() => !!route.query.id)
const templateForm = ref<ActionTemplateManage>({ ...initTemplateForm })
const totalTemplateField = ref<DefinedFieldItem[]>([])
const selectData = ref<DefinedFieldItem[]>([])

watchEffect(async () => {
    if (isEdit.value && route.params.mode === 'copy') {
        title.value = t('system.orgTemplate.copyTemplate')
    } else if (isEdit.value) {
        title.value = t('system.orgTemplate.editTemplateType', {
            type: getTemplateName('organization', route.query.type as string),
        })
    } else {
        title.value = t('system.orgTemplate.createTemplateType', {
            type: getTemplateName('organization', route.query.type as string),
        })
        // templateForm.value.name = title.value;
    }
})
</script>

<template>
    <base-card
        :hide-back="true"
        :special-height="55"
        :title="title"
        :is-edit="isEdit && route.params.mode !== 'copy'"
    >
        <template #headerLeft>
            <n-alert
                v-if="templateForm.enableThirdPart && route.query.type === 'BUG'"
                class="mb-[16px] w-full"
            >
                {{ t('system.orgTemplate.enableApiAlert') }}
            </n-alert>
            <n-form
                ref="formRef"
                :model="templateForm"
                label-placement="left"
                label-width="auto"
                require-mark-placement="right-hanging"
                class="mt-1 max-w-[710px]"
            >
                <n-form-item v-if="!templateForm?.internal" class="mb-0 max-w-[710px]">
                    <n-input
                        v-model:value="templateForm.name"
                        :placeholder="t('system.orgTemplate.templateNamePlaceholder')"
                        :maxlength="255"
                        class="max-w-[732px]"
                        :disabled="templateForm?.internal"
                    />
                </n-form-item>
                <span v-else class="font-medium underline">{{ templateForm.name }}</span>
            </n-form>
        </template>
        <template #headerRight>
            <div class="flex items-center">
                <n-checkbox
                    v-if="route.query.type === 'BUG'"
                    v-model:checked="templateForm.enableThirdPart"
                    class="mx-2"
                >
                    {{ t('system.orgTemplate.thirdParty') }}
                </n-checkbox>
                <n-icon size="16">
                    <span class="i-tabler:refresh-alert" />
                </n-icon>
            </div>
        </template>
        <div class="wrapper-preview">
            <div class="preview-left pr-4">
                <case-template-left-content />
            </div>
            <div class="preview-left pr-4"></div>
        </div>
    </base-card>
</template>

<style scoped></style>
