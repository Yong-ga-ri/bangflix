<template>
  <div class="container-user-rank-card">
    <Card>
      <template #title>
        <div class="flex-row content-between">
          <AppTypography type="title3">{{ props.rankMember.point.toLocaleString() }} point</AppTypography>
          <Tag v-if="props.rank >= 4" :value="`${props.rank}위`" rounded severity="info"></Tag>
        </div>
      </template>
      <template #content>
        <div class="flex-row gap-10 items-center">
          <UserAvatar :image-path="props.rankMember.image" />
          <AppTypography class="mb-xxs">{{ props.rankMember.nickname }}</AppTypography>
        </div>
      </template>
      <template #footer>
        <!-- <Button
          label="프로필 보기"
          size="small"
          severity="secondary"
          fluid
          as="router-link"
          :to="`/member/detail/${props.rankMember.memberCode}`"
        /> -->
        <Button label="프로필 보기" size="small" severity="secondary" fluid />
      </template>
    </Card>

    <!-- 메달 -->
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
  </div>
</template>

<script setup>
import { defineProps } from 'vue';
import AppTypography from '../AppTypography.vue';
import Image from 'primevue/image';
import Button from 'primevue/button';
import { useRouter } from 'vue-router';
import Tag from 'primevue/tag';
import UserAvatar from '../common/UserAvatar.vue';

const props = defineProps({
  rankMember: {
    type: Object, // MemberRankingDTO
    required: true,
  },

  rank: {
    type: Number,
    required: true,
  },
});

const router = useRouter();

const clickUser = userId => {
  // router.push()
};
</script>

<style scoped>
.container-user-rank-card {
  position: relative;

  .item-medal {
    position: absolute;
    top: -10px;
    left: -25px;
  }
}
</style>
