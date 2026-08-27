import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { subscriptionApi } from "@/api/subscription.js";

export const UNKNOWN = "UNKNOWN";
export const NONE = "NONE";

function unwrapList(res) {
  if (Array.isArray(res.data?.data)) return res.data.data;
  if (Array.isArray(res.data)) return res.data;
  return [];
}

function unwrapOne(res) {
  const inner = res.data?.data;
  return inner && typeof inner === "object" ? inner : res.data;
}

export const useSubscriptionStore = defineStore("subscription", () => {
  const subscriptions = ref([]);
  const loading = ref(false);
  const error = ref(null);

  // Api 조회 실패 -> 구독 이력 없음으로 조회되는 것 방지용 필드
  const loaded = ref(false);

  const activeCount = computed(
    () => subscriptions.value.filter((s) => s.status === "ACTIVE").length,
  );

  function statusOf(productId) {
    if (!loaded.value) return UNKNOWN;
    if (productId === null || productId === undefined) return NONE;
    const matched = subscriptions.value.find(
      (item) => Number(item.productId) === Number(productId),
    );
    return matched ? matched.status : NONE;
  }

  // 중복 조회 방지를 위해 force 옵션이 있음 -> 강제 갱신하려면 force=true로 넘기기
  async function fetchMySubscriptions({ force = false } = {}) {
    if (loaded.value && !force) return;

    loading.value = true;
    error.value = null;

    try {
      const res = await subscriptionApi.getMySubscriptions();
      subscriptions.value = unwrapList(res);
      loaded.value = true;
    } catch (e) {
      console.error("[SubscriptionStore] fetchMySubscriptions failed:", e);
      error.value = "구독 목록을 불러오지 못했습니다.";
      subscriptions.value = [];
      loaded.value = false;
    } finally {
      loading.value = false;
    }
  }

  async function subscribe(productId) {
    const res = await subscriptionApi.subscribe(productId);
    const status = unwrapOne(res)?.status ?? "PENDING";
    await fetchMySubscriptions({ force: true });
    return status;
  }

  function reset() {
    subscriptions.value = [];
    loading.value = false;
    error.value = null;
    loaded.value = false;
  }

  return {
    subscriptions,
    loading,
    error,
    loaded,
    activeCount,
    statusOf,
    fetchMySubscriptions,
    subscribe,
    reset,
  };
});
