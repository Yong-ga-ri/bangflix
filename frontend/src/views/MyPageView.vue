<template>
  <PageLayout title="마이페이지">
    <div v-if="userInfo">
      <div class="user-info-container">
        <img :src="userInfo.image" class="profile-image" alt="Image" preview />
        <div class="user-info">
          <h3>{{ userInfo.nickname }}</h3>
          <div>{{ userInfo.point }} P</div>
        </div>
      </div>

      <!-- 구분선 -->
      <Divider type="solid" />

      <!-- Report Section -->
      <div class="report-section">
        <h3>Report</h3>
        <p>방탈러 님의 리뷰 평균 점수는 {{ userReport.avgScore }}점이에요</p>
        <p>방탈러 님이 자주 플레이한 장르는 {{ userReport.genres }} 장르에요</p>
      </div>

      <!-- Review Section -->
      <div class="review-section">
        <h3>Review ({{ reviews.length }})</h3>
        <div v-if="reviews.length > 0">
          <Carousel
            :value="reviews"
            :num-visible="3"
            :num-scroll="1"
            :responsive-options="responsiveOptions"
            circular
            :autoplay-interval="5000"
          >
            <template #item="review">
              <ReviewPreviewCard :review="review.data" />
            </template>
          </Carousel>
        </div>
        <template v-else> 작성하신 리뷰가 없습니다. </template>
      </div>

      <!-- Post Section -->
      <div class="post-section">
        <h3>작성한 글 ({{ posts.length }})</h3>
        <div v-if="posts.length > 0">
          <Carousel
            :value="posts"
            :num-visible="5"
            :num-scroll="1"
            :responsive-options="responsiveOptions"
            circular
            :autoplay-interval="5000"
          >
            <template #item="post">
              <PostPreviewCard :post="post.data" />
            </template>
          </Carousel>
        </div>
      </div>

      <!-- Comment Section -->
      <div class="comment-section">
        <h3>작성한 댓글 ({{ comments.length }})</h3>
        <div v-if="comments.length > 0">
          <Carousel
            :value="comments"
            :num-visible="5"
            :num-scroll="1"
            :responsive-options="responsiveOptions"
            circular
            :autoplay-interval="5000"
          >
            <template #item="comment">
              <CommentPreview :comment="comment.data" />
            </template>
          </Carousel>
        </div>
      </div>

      <!-- theme Section -->
      <div class="theme-section">
        <h3>스크랩한 방탈출 ({{ themes.length }})</h3>
        <div v-if="themes.length > 0">
          <Carousel
            :value="themes"
            :num-visible="5"
            :num-scroll="1"
            :responsive-options="responsiveOptions"
            circular
            :autoplay-interval="5000"
          >
            <template #item="theme">
              <ThemeCard :theme="theme.data" next-page="THEME" />
            </template>
          </Carousel>
        </div>
      </div>
    </div>
    <div v-else>
      <PageLoadingLayout />
    </div>
  </PageLayout>
</template>

<script setup>
import CommentPreview from '@/components/cards/preview/CommentPreview.vue';
import PostPreviewCard from '@/components/cards/preview/PostPreviewCard.vue';
import ReviewPreviewCard from '@/components/cards/preview/ReviewPreviewCard.vue';
import ThemeCard from '@/components/cards/ThemeCard.vue';
import PageLayout from '@/components/layouts/PageLayout.vue';
import PageLoadingLayout from '@/components/layouts/PageLoadingLayout.vue';
import { $api } from '@/services/api/api';
import { useUserStore } from '@/stores/user';
import Carousel from 'primevue/carousel';
import { onMounted, ref } from 'vue';

const baseServerUrl = `${import.meta.env.VITE_SERVER_URL}`;

const userStore = useUserStore();

const userInfo = ref({});
const userReport = ref({});
const reviews = ref([]);
const posts = ref([]);
const comments = ref([]);
const themes = ref([]);

const responsiveOptions = ref([
  {
    breakpoint: '1024px', // 화면 너비가 1024px 이하일 때
    numVisible: 3, // 보이는 항목 수
    numScroll: 1, // 스크롤할 항목 수
  },
  {
    breakpoint: '768px', // 화면 너비가 768px 이하일 때
    numVisible: 2,
    numScroll: 1,
  },
  {
    breakpoint: '560px', // 화면 너비가 560px 이하일 때
    numVisible: 1,
    numScroll: 1,
  },
]);

onMounted(async () => {
  const token = userStore.accessToken;
  if (token) {
    try {
      // 사용자 정보 조회
      userInfo.value = await $api.user.get('mypage');
      if (userInfo.value) {
        // 사용자가 업로드한 것인지, 더미데이터인지 확인 필요
        if (userInfo.value.image && !(userInfo.value.image.substr(0, 20) === 'https://drive.google')) {
          userInfo.value.image = baseServerUrl + userInfo.value.image;
          console.log('userInfo.value: ', userInfo.value);
        }
      } else {
        userInfo.value = [];
      }

      // 사용자 report 조회
      const responseReport = await $api.review.get('user/report');
      if (responseReport) {
        responseReport.genres = responseReport.genres.join(', ');
        userReport.value = responseReport;
      }

      // 사용자 review 조회
      reviews.value = await $api.review.get('user');
      if (!reviews.value) {
        reviews.value = [];
      }

      // 사용자 post 조회
      posts.value = await $api.community.get('my');
      if (reviews.value) {
        posts.value = [];
      }

      // 작성 댓글 조회
      comments.value = await $api.comment.get();
      if (!comments.value) {
        comments.value = [];
      }
      // console.log('comments.value: ', comments.value);

      // 테마 조회
      themes.value = await $api.theme.getThemesByMe();
      if (!themes.value) {
        themes.value = [];
      }
    } catch (error) {
      console.error('Failed to fetch user info:', error);
    }
  } else {
    console.log('User is not authenticated.');
  }
});
</script>

<style scoped>
.user-info-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
}

.report-section {
  margin-top: 30px;
}

.review-section {
  margin-top: 80px;
}

.comment-section {
  margin-top: 80px;
}

.post-section {
  margin-top: 80px;
}

.theme-section {
  margin-top: 50px;
}
</style>
