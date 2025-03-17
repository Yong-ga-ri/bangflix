<template>
  <div class="card-theme">
    <template v-if="userStore.isLoggined">
      <!-- <div v-if="props.theme.reaction.scrap.scraped" class="banner-scraped">스크랩 됨</div>
        <div v-else class="banner-unscraped" /> -->
    </template>

    <Card style="overflow: hidden">
      <template #header>
        <img class="theme-tumb" :src="posterImage" alt="테마 프로필 이미지" @error="onImageError" />
      </template>
      <template #content>
        <div class="content-container">
          <h3 class="theme mb-xxs">{{ props.theme.name }}</h3>
          <div class="mb-s">{{ props.theme.storeName }}</div>
          <AppTypography type="caption" color="darkgray">리뷰 {{ props.theme.reviewCount }}</AppTypography>
        </div>
      </template>
      <template v-if="props.showFooter" #footer>
        <div class="footer mb-xs">
          <Button
            :icon="`pi ${userLiked ? 'pi-heart-fill' : 'pi-heart'}`"
            label="좋아요"
            size="small"
            outlined
            :badge="likeCnt.toString()"
            @click="clickLike(!userLiked)"
          />
          <Button
            :icon="`pi ${userBookmarked ? 'pi-bookmark-fill' : 'pi-bookmark'}`"
            label="스크랩"
            size="small"
            outlined
            :badge="scrapCnt.toString()"
            @click="clickScrap(!userBookmarked)"
          />
        </div>

        <Button
          v-if="props.nextPage === 'THEME'"
          label="테마 상세보기"
          size="small"
          fluid
          as="router-link"
          :to="`/theme/detail/${props.theme.themeCode}`"
        />
      </template>
    </Card>
  </div>
</template>

<script setup>
import { ref, defineProps, watchEffect, onMounted } from 'vue';
import Card from 'primevue/card';
import ReviewIcon from '../common/reaction/ReviewIcon.vue';
import Like from '../common/reaction/ReviewLike.vue';
import Scrap from '../common/reaction/Scrap.vue';
import { useUserStore } from '@/stores/user';
import Button from 'primevue/button';
import AppTypography from '../AppTypography.vue';
import { $api } from '@/services/api/api';
import defaultPosterImageUrl from '@/assets/default/default-poster.png';

const defaultPosterImage = defaultPosterImageUrl;

const userStore = useUserStore();

const props = defineProps({
  theme: {
    type: Object, // theme: ThemeDTO
    required: true,
  },

  nextPage: {
    type: String, // 'EVENT', 'THEME'
    required: false,
    default: 'THEME',
  },

  showFooter: {
    type: Boolean,
    required: false,
    default: true,
  },
});

const userLiked = ref(false);
const userBookmarked = ref(false);
const likeCnt = ref(0);
const scrapCnt = ref(0);

const clickLike = changeTo => {
  $api.theme.setReactions('like', changeTo, props.theme.themeCode).then(() => {
    userLiked.value = changeTo;

    if (changeTo) likeCnt.value += 1;
    else likeCnt.value -= 1;
  });
};

const clickScrap = changeTo => {
  $api.theme.setReactions('scrap', changeTo, props.theme.themeCode).then(() => {
    userBookmarked.value = changeTo;

    if (changeTo) scrapCnt.value += 1;
    else scrapCnt.value -= 1;
  });
};

// 초기 이미지 주소를 ref로 설정
const posterImage = ref(props.theme.posterImage || defaultPosterImage);

// 이미지 로드 실패 시 기본 이미지로 대체하는 핸들러
const onImageError = () => {
  posterImage.value = defaultPosterImage;
};

onMounted(() => {
  userLiked.value = props.theme.isLike;
  userBookmarked.value = props.theme.isScrap;
  likeCnt.value = props.theme.likeCount;
  scrapCnt.value = props.theme.scrapCount;
});
</script>

<style scoped>
.card-theme {
  .p-card {
    position: relative; /* 띠를 절대 위치로 배치하기 위한 부모 설정 */
    /* width: 245px; */
  }

  .content-container {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    flex-grow: 1;
  }

  .theme-tumb {
    display: block;
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  .theme {
    font-weight: bolder;
  }

  .location {
    margin-top: 10px;
  }

  .reaction-container {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
  }

  /* 스크랩 여부에 따른 띠 스타일 */
  .banner-unscraped {
    position: absolute;
    top: 0;
    right: 0;
    background-color: white; /* 띠 배경색 */
    color: #fff; /* 텍스트 색상 */
    padding: 5px 10px;
    font-weight: bold;
    font-size: 12px;
    border-bottom-left-radius: 8px;
  }

  /* 스크랩 여부에 따른 띠 스타일 */
  .banner-scraped {
    position: absolute;
    top: 0;
    right: 0;
    background-color: #ffcc00; /* 띠 배경색 */
    color: #fff; /* 텍스트 색상 */
    padding: 5px 10px;
    font-weight: bold;
    font-size: 12px;
    border-bottom-left-radius: 8px;
  }

  .footer {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
}
</style>
