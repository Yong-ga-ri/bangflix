<template>
  <div class="container-review-item">
    <!-- 메달 (in 베스트 리뷰) -->
    <Image
      v-if="props.rank === 1"
      src="https://forprototype.s3.ap-northeast-2.amazonaws.com/bangflix/medal-gold.png"
      class="item-medal"
      image-style="width: 50px"
    />
    <Image
      v-else-if="props.rank === 2"
      src="https://forprototype.s3.ap-northeast-2.amazonaws.com/bangflix/medal-silver.png"
      class="item-medal"
      image-style="width: 50px"
    />
    <Image
      v-else-if="props.rank === 3"
      src="https://forprototype.s3.ap-northeast-2.amazonaws.com/bangflix/medal-bronze.png"
      class="item-medal"
      image-style="width: 50px"
    />

    <div class="area-review flex-row">
      <!-- 왼쪽 부분 -->
      <div class="grow-1 area-left">
        <div>
          <div class="flex-row items-center content-between mb-m">
            <div class="flex-row gap-10 items-center">
              <UserAvatar :image-path="props.review.memberImage" />
              <AppTypography type="body1" color="darkgray">{{ props.review.memberNickname }}</AppTypography>
              <AppTypography type="body2" color="gray">{{
                Helper.Date.formatDateTime(props.review.createdAt)
              }}</AppTypography>
              <!-- <AppTypography type="body2" color="gray">선호 장르: {{ props.review.genres.join(',') }}</AppTypography> -->
            </div>

            <div>
              <template v-if="props.review.memberNickname === userStore.nickname">
                <!-- <Button label="수정" size="small" severity="secondary" class="mr-xxs" @click="editReview" /> -->
                <Button label="삭제" size="small" severity="secondary" @click="clickRemoveMyReview" />
              </template>

              <!-- <Button
              label="신고하기"
              outlined
              icon="pi pi-ban"
              size="small"
              severity="danger"
              @click="emit('clickReport', $event)"
            /> -->
            </div>
          </div>

          <div class="flex-row gap-10 mb-s">
            <AppTypography type="body1" class="grow-1">{{ props.review.content }}</AppTypography>

            <!-- 이미지 있는 경우 표시 -->
            <img
              v-if="props.review.imagePaths.length > 0"
              :src="`${Helper.getImageUrl(props.review.imagePaths[0])}`"
              width="150"
              height="150"
              @click="emit('clickImage', props.review.imagePaths)"
            />
          </div>
        </div>

        <div>
          <!-- 테마 정보 -->
          <div v-if="props.showTheme" class="area-theme mb-xxs">
            <Image :src="Helper.getImageUrl(props.review.themeImage)" width="50" />
            <div>
              <AppTypography type="title3" class="mb-xxs">테마: {{ props.review.themeName }}</AppTypography>
              <Button
                label="테마 상세"
                size="small"
                severity="secondary"
                as="router-link"
                :to="`/theme/detail/${props.review.themeCode}`"
              />
            </div>
          </div>

          <!-- 좋아요 버튼 -->
          <ReviewLike
            :is-user-like="reviewUserLike"
            :count="reviewCount"
            @handle-deactivate="handleDeactivate"
            @handle-active="handleActive"
          />
        </div>
      </div>

      <!-- 구분선 -->
      <Divider layout="vertical" />

      <!-- 상세 평가항목(오른쪽) -->
      <div class="area-right shrink-0 flex-row gap-10 items-center content-center">
        <div class="flex-col gap-5">
          <AppTypography type="body1" class="mb-xs">만족도</AppTypography>
          <AppTypography type="title1" class="mb-m">{{ props.review.totalScore }}/5</AppTypography>
          <ReviewTag label="참가인원" :value="props.review.headcount.toString()" type="PEOPLE" />
          <ReviewTag label="소요시간" :value="props.review.takenTime.toString()" type="TIME" />
        </div>

        <div class="flex-col gap-5">
          <ReviewTag label="문제구성" :value="props.review.composition" type="QUIZ" />
          <ReviewTag label="난이도" :value="props.review.level" type="LEVEL" />
          <ReviewTag label="공포도" :value="props.review.horrorLevel" type="SCARY" />
          <ReviewTag label="활동성" :value="props.review.activity" type="ACTIVITY" />
          <ReviewTag label="인테리어" :value="props.review.interior" type="INTERIOR" />
          <ReviewTag label="개연성" :value="props.review.probability" type="PROBABILITY" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, watchEffect } from 'vue';
import Divider from 'primevue/divider';
import Button from 'primevue/button';
import Image from 'primevue/image';
import AppTypography from '@/components/AppTypography.vue';
import ReviewTag from '@/components/review/ReviewTag.vue';
import ReviewLike from '@/components/common/reaction/ReviewLike.vue';
import { Helper } from '@/utils/Helper';
import { $api } from '@/services/api/api';
import UserAvatar from '../common/UserAvatar.vue';
import { useUserStore } from '@/stores/user';
import { useConfirm } from 'primevue/useconfirm';

const props = defineProps({
  review: {
    type: Object, // ReviewDTO
    required: true,
  },
  showTheme: {
    type: Boolean,
    required: false,
  },
  rank: {
    type: Number,
    required: false,
    default: null,
  },
});

const emit = defineEmits(['clickImage', 'clickReport', 'onRemovedMyReview']);
const userStore = useUserStore();
const confirm = useConfirm();

const reviewCount = ref(0);
const reviewUserLike = ref(false);

const handleActive = () => {
  $api.review.activeLike(props.review.reviewCode).then(() => {
    reviewCount.value += 1;
    reviewUserLike.value = true;
  });
};

const handleDeactivate = () => {
  $api.review.deactivateLike(props.review.reviewCode).then(() => {
    reviewCount.value -= 1;
    reviewUserLike.value = false;
  });
};

const removeMyReview = () => {
  $api.review.removeMyReview(props.review.reviewCode).then(() => {
    emit('onRemovedMyReview');
  });
};

const clickRemoveMyReview = () => {
  confirm.require({
    message: '이 테마에 작성한 내 리뷰를 삭제할까요?',
    header: '리뷰 삭제',
    rejectProps: {
      label: '취소',
      severity: 'secondary',
      outlined: true,
    },
    acceptProps: {
      label: '리뷰 삭제',
      severity: 'danger',
    },
    accept: removeMyReview,
  });
};

watchEffect(() => {
  reviewCount.value = props.review.likes;
  reviewUserLike.value = props.review.isLike;
});
</script>

<style scoped>
.container-review-item {
  position: relative;

  .area-review {
    background-color: white;
    border: 1px solid #e4e4e7;
    border-radius: 10px;
    padding: 20px;

    img {
      object-fit: cover;
    }

    .area-right {
      width: 390px;
    }

    .area-left {
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      gap: 16px;
    }
  }

  .item-medal {
    position: absolute;
    top: -10px;
    left: -25px;
  }

  .area-theme {
    display: flex;
    align-items: center;
    gap: 10px;

    .square {
      width: 20px;
      height: 20px;
      border-left: 1px solid #b3b3b3;
      border-bottom: 1px solid #b3b3b3;
      border-end-start-radius: 3px;
      margin-right: 10px;
    }

    .store {
      background-color: white;
      border: 1px solid #e4e4e7;
      border-radius: 5px;
      padding: 10px;
    }
  }
}
</style>
