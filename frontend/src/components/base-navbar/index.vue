<script setup lang="ts">
import { useRequest } from 'alova'
import { NDivider, SelectOption } from 'naive-ui'
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PersonalMenu from './PersonalMenu.vue'
import { switchProject } from '/@/api/modules/project-management/project'
import logo from '/@/assets/logo.svg'
import { load } from '/@/components/loading/index'
import { useI18n } from '/@/hooks/use-i18n'
import useAppStore from '/@/store/modules/app'
import useUserStore from '/@/store/modules/user'

const projectOptions = ref<Array<SelectOption>>([])
const { t } = useI18n()
const appStore = useAppStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const { send: switchPro } = useRequest((param) => switchProject(param), {
  immediate: false,
})
const handleSelectProject = async (value: string) => {
  load.show()
  await switchPro({
    projectId: value as string,
    userId: userStore.id || '',
  }).finally(() => {
    load.hide()
    router.replace({
      path: route.path,
      query: {
        ...route.query,
        orgId: appStore.currentOrgId,
        pId: appStore.currentProjectId,
      },
    })
  })
}
watch(
  () => appStore.currentOrgId,
  async () => {
    appStore.initProjectList()
  },
  { immediate: true },
)
watch(
  () => appStore.projectList,
  (newValue) => {
    projectOptions.value = []
    newValue.map((item) => {
      projectOptions.value.push({
        label: item.name,
        value: item.id,
        // style: item.id === appStore.currentProjectId ? 'color:#007AFF' : '',
      })
    })
  },
)
</script>
<template>
  <div class="navbar">
    <div class="left-side">
      <n-space>
        <div class="one-line-text flex max-w-[145px] items-center">
          <img :src="logo" class="mr-[4px] h-[34px] w-[32px]" />
          <div class="font-['Helvetica_Neue'] text-[16px] font-bold">Yukio</div>
        </div>
      </n-space>
    </div>
    <div class="center-side">
      <n-divider vertical class="ml-0" />
      <n-select
        v-model:value="appStore.currentProjectId"
        :options="projectOptions"
        filterable
        class="w-[200px]"
        @update:value="handleSelectProject"
      />
      <n-divider vertical class="mr-0" />
    </div>
    <ul class="right-side">
      <li>
        <n-tooltip trigger="hover">
          <template #trigger>
            <div class="message-box-trigger">
              <n-badge :value="8">
                <n-button secondary size="small">
                  <template #icon>
                    <span class="i-carbon:notification" />
                  </template>
                </n-button>
              </n-badge>
            </div>
          </template>
          {{ t('settings.navbar.alerts') }}
        </n-tooltip>
      </li>
      <li>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-button secondary size="small">
              <template #icon>
                <span class="i-carbon:task" />
              </template>
            </n-button>
          </template>
          {{ t('settings.navbar.task') }}
        </n-tooltip>
      </li>
      <li>
        <n-dropdown trigger="hover" :options="[]">
          <n-button secondary size="small">
            <template #icon>
              <span class="i-carbon:help" />
            </template>
          </n-button>
        </n-dropdown>
      </li>
      <li>
        <n-dropdown trigger="hover" :options="[]">
          <n-button secondary size="small">
            <template #icon>
              <span class="i-carbon:ibm-watson-language-translator" />
            </template>
          </n-button>
        </n-dropdown>
      </li>
      <li>
        <personal-menu />
      </li>
    </ul>
  </div>
</template>

<style scoped>
.navbar {
  display: flex;
  height: 100%;
  justify-content: space-between;
  background-color: transparent;
}
.left-side {
  display: flex;
  align-items: center;
  padding-left: 24px;
  width: 185px;
}
.center-side {
  @apply flex flex-1 items-center;
}
.right-side {
  @apply flex list-none;

  padding-right: 20px;
  :deep(.locale-select) {
    border-radius: 20px;
  }
  li {
    @apply flex items-center;

    padding-left: 10px;
  }

  .nav-btn {
    font-size: 16px;
    border-color: rgb(var(--gray-2));
    color: rgb(var(--gray-8));
    line-height: 24px;
  }
  .trigger-btn,
  .ref-btn {
    position: absolute;
    bottom: 14px;
  }
  .trigger-btn {
    margin-left: 14px;
  }
  .select-option-selected {
    color: rgb(#8ae600) !important;
    background-color: rgb(#8ae600) !important;
  }
}
</style>
