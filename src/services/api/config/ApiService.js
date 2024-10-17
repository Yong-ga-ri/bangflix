import BaseApiService from '@/services/api/config/BaseApiService';
import DOMEventService from '@/services/DOMEventService';
import { useUserStore } from '@/stores/user';

export default class ApiService extends BaseApiService {
  #userStore;

  constructor(resource) {
    super(resource);

    const userStore = useUserStore();
    this.#userStore = userStore;
  }

  // 요청을 처리하는 부분
  async #callApi(url, options) {
    try {
      const fetchOptions = { ...options };

      // headers default 설정값
      const myHeaders = new Headers();

      if (this.#userStore.accessToken) {
        myHeaders.append('Authorization', `Bearer ${this.#userStore.accessToken}`);
      }

      // 요청 시 보낼 최종 options 셋팅
      fetchOptions['headers'] = myHeaders;

      // 요청 시작
      const response = await fetch(url, fetchOptions);

      if (!response.ok) {
        throw new Error(`Error occurred: ${response.status}`);
      }

      return await response.json();
    } catch (err) {
      this.handleError(err);
    }
  }

  async getAll(queryParams) {
    let url = `${this.baseUrl}/${this.resource}`;
    if (queryParams) {
      url += `?${queryParams}`;
    }

    const responseData = await this.#callApi(url);
    return responseData.result;
  }

  async getById(id) {
    if (!id) throw new Error('{id} is not provided');

    const url = `${this.baseUrl}/${this.resource}/${id}`;

    const responseData = await this.#callApi(url);
    return responseData.result;
  }

  async post(data = {}, subUrl) {
    let url = `${this.baseUrl}/${this.resource}`;
    if (subUrl) {
      url += `/${subUrl}`;
    }

    let requestBody = JSON.stringify(data);

    // formData 자체로 넘어올 예정
    if (data instanceof FormData) {
      requestBody = data;
    }

    const options = {
      method: 'POST',
      body: requestBody,
    };

    const responseData = await this.#callApi(url, options);
    DOMEventService.dispatchApiSuccess(responseData.msg || '성공');

    return responseData.result;
  }

  async put(id, data = {}) {
    if (!id) throw new Error('{id} is not provided');

    const url = `${this.baseUrl}/${this.resource}/${id}`;

    let requestBody = JSON.stringify(data);

    // formData 자체로 넘어올 예정
    if (data instanceof FormData) {
      requestBody = data;
    }

    const options = {
      method: 'PUT',
      body: requestBody,
    };

    const responseData = await this.#callApi(url, options);
    DOMEventService.dispatchApiSuccess(responseData.msg || '성공');

    return responseData.result;
  }

  async delete(id) {
    if (!id) throw new Error('{id} is not provided');

    const url = `${this.baseUrl}/${this.resource}/${id}`;
    const options = {
      method: 'DELETE',
    };

    const responseData = await this.#callApi(url, options);
    DOMEventService.dispatchApiSuccess(responseData.msg || '성공');

    return responseData.result;
  }
}
