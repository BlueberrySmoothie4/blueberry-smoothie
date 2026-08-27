import api from "./index.js";

export const paymentApi = {
  getByUser(userId) {
    return api.get(`/api/payments/user/${userId}`);
  },
};
