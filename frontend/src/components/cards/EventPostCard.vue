<template>
  <Card style="overflow: hidden" class="event-post-card">
    <template #header>
      <img class="theme-tumb" :src="posterImage" alt="테마 포스터 이미지" @error="onImageError" />
    </template>
    <template #content>
      <h3 class="theme mb-xs">{{ props.eventPost.eventTheme.name }}</h3>
      <AppTypography type="caption" color="darkgray">{{ props.eventPost.content }}</AppTypography>
    </template>
    <template #footer>
      <Button
        label="이벤트 상세보기"
        size="small"
        fluid
        as="router-link"
        :to="`/event/detail/${props.eventPost.eventPostCode}`"
      />
    </template>
  </Card>
</template>

<script setup>
import { defineProps, ref } from 'vue';
import AppTypography from '../AppTypography.vue';
import Button from 'primevue/button';
import defaultPosterImageUrl from '@/assets/default/default-poster.png';

const defaultPosterImage = defaultPosterImageUrl;

const props = defineProps({
  eventPost: {
    type: Object,
    required: true,
  },
});

// 초기 이미지 주소를 ref로 설정
const posterImage = ref(props.eventPost.eventTheme.posterImage || defaultPosterImage);

// 이미지 로드 실패 시 기본 이미지로 대체하는 핸들러
const onImageError = () => {
  posterImage.value = defaultPosterImage;
};
</script>

<style scoped>
.event-post-card {
  .theme-tumb {
    display: block;
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
}
</style>
