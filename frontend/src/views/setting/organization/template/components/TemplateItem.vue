<script setup lang="ts">
import { useI18n } from '/@/hooks/use-i18n.ts'
import { useAppStore } from '/@/store'
import useTemplateStore from '/@/store/modules/setting/template.ts'
import { computed, ref, watch } from 'vue'

const { t } = useI18n()
const appStore = useAppStore()
const templateStore = useTemplateStore()
const props = defineProps<{
    cardItem: Record<string, any>
    mode: 'organization' | 'project'
}>()
const emit = defineEmits<{
    (e: 'fieldSetting', key: string): void
    (e: 'templateManagement', key: string): void
    (e: 'workflowSetup', key: string): void
    (e: 'updateState'): void
}>()
const showEnableVisible = ref<boolean>(false)
const validateKeyWord = ref<string>('')
const confirmLoading = ref<boolean>(false)
const templateCardInfo = ref<Record<string, any>>({})
const currentOrgId = computed(() => appStore.currentOrgId)
const isEnableProject = computed(() => {
    return props.mode === 'organization'
        ? !templateStore.projectStatus[props.cardItem.key]
        : templateStore.ordStatus[props.cardItem.key]
})
const orgName = computed(() => {
    return (
        appStore.ordList.find((item: any) => item.id === appStore.currentOrgId)?.name || '默认组织'
    )
})
const fieldSetting = () => {
    emit('fieldSetting', props.cardItem.key)
}
const templateManagement = () => {
    emit('templateManagement', props.cardItem.key)
}

const workflowSetup = () => {
    emit('workflowSetup', props.cardItem.key)
}
watch(
    () => props.cardItem,
    (val) => {
        if (val) {
            templateCardInfo.value = { ...props.cardItem }
        }
    },
    { deep: true },
)
</script>

<template>
    <div class="outer-wrapper p-[3px]">
        <div class="inner-wrapper">
            <div class="content">
                <div class="logo-img h-[48px] w-[48px]">
                    <n-icon size="30">
                        <span :class="'i-icon-' + [props.cardItem.value]" />
                        <!--                        <span class="i-icon-caseTemplate" />-->
                    </n-icon>
                </div>
                <div class="template-operation">
                    <div class="flex items-center">
                        <span class="font-medium">{{ props.cardItem.name }}</span>
                        <span v-if="!isEnableProject" class="enable">
                            {{ t('system.orgTemplate.enabledTemplates') }}
                        </span>
                    </div>
                    <div class="flex min-w-[300px] flex-nowrap items-center">
                        <span class="operation hover:text-[rgb(var(--primary-5))]">
                            <span @click="fieldSetting">
                                {{ t('system.orgTemplate.fieldSetting') }}
                            </span>
                            <n-divider direction="vertical" />
                        </span>
                        <span class="operation hover:text-[rgb(var(--primary-5))]">
                            <span @click="templateManagement">
                                {{ t('system.orgTemplate.TemplateManagement') }}
                            </span>
                            <n-divider
                                v-if="isEnableProject || props.cardItem.key === 'BUG'"
                                direction="vertical"
                            />
                        </span>
                        <span
                            v-if="props.cardItem.key === 'BUG'"
                            class="operation hover:text-[rgb(var(--primary-5))]"
                        >
                            <span @click="workflowSetup">
                                {{ t('system.orgTemplate.workflowSetup') }}
                            </span>
                            <n-divider
                                v-if="props.mode === 'organization' && isEnableProject"
                                direction="vertical"
                            />
                        </span>
                        <span
                            v-if="props.mode === 'organization' && isEnableProject"
                            class="rounded p-[2px] hover:bg-[rgb(var(--primary-9))]"
                        >
                            <!--                            <MsTableMoreAction-->
                            <!--                                :list="moreActions"-->
                            <!--                                @select="handleMoreActionSelect"-->
                            <!--                            />-->
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.outer-wrapper {
    box-shadow: 0 6px 15px rgba(120 56 135/ 5%);
    border-radius: 0.25rem;
    .inner-wrapper {
        border-radius: 0.25rem;
        padding: 1.5rem;
        .content {
            display: flex;
            .logo-img {
                margin-right: 0.75rem;
                display: flex;
                align-items: center;
            }
            .template-operation {
                display: flex;
                flex-direction: column;
                justify-content: space-between;
                .operation {
                    cursor: pointer;
                    margin-right: 10px;
                }
            }
        }
    }
}
</style>
