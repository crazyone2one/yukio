<script setup lang="ts">
import { NDivider } from 'naive-ui'
import { ref } from 'vue'
import PersonalMenu from './PersonalMenu.vue'
import logo from '/@/assets/logo.svg'

const value = ref('')
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
        v-model:value="value"
        :options="[]"
        class="w-[200px] focus-within:!bg-[var(--color-text-n8)] hover:!bg-[var(--color-text-n8)]"
      />
      <n-divider vertical class="mr-0" />
    </div>
    <ul class="right-side">
      <li>
        <n-tooltip trigger="hover">
          <template #trigger>
            <div class="message-box-trigger">
              <n-badge :value="8">
                <n-button secondary>
                  <template #icon>
                    <span class="i-solar:bell-outline" />
                  </template>
                </n-button>
              </n-badge>
            </div>
          </template>
          如果它长得像鸭子，走起来像鸭子，叫起来也像鸭子，那它一定是个鸭子。
        </n-tooltip>
      </li>
      <li>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-button secondary>
              <template #icon>
                <span class="i-solar:notification-lines-remove-line-duotone" />
              </template>
            </n-button>
          </template>
          如果它长得像鸭子，走起来像鸭子，叫起来也像鸭子，那它一定是个鸭子。
        </n-tooltip>
      </li>
      <li>
        <n-dropdown trigger="hover" :options="[]">
          <n-button secondary>
            <template #icon>
              <span class="i-solar:question-circle-bold" />
            </template>
          </n-button>
        </n-dropdown>
      </li>
      <li>
        <n-dropdown trigger="hover" :options="[]">
          <n-button secondary>
            <template #icon>
              <span class="i-solar:black-hole-3-line-duotone" />
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
  @apply flex h-full justify-between bg-transparent;
}
.left-side {
  @apply flex items-center;

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
    .arco-btn-secondary {
      @apply !bg-transparent;

      color: var(--color-text-4) !important;
      &:hover,
      &:focus-visible {
        color: var(--color-text-1) !important;
      }
    }
  }
  a {
    @apply no-underline;

    color: var(--color-text-1);
  }
  .nav-btn {
    font-size: 16px;
    border-color: rgb(var(--gray-2));
    color: rgb(var(--gray-8));
    line-height: 24px;
  }
  .trigger-btn,
  .ref-btn {
    @apply absolute;

    bottom: 14px;
  }
  .trigger-btn {
    margin-left: 14px;
  }
}
</style>
