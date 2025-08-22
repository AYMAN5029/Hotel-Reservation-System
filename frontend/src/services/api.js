import axios from 'axios';

// API Base URL for direct API calls (like image URLs)
const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with base configuration
const api = axios.create({
  baseURL: '/api', // This will proxy to http://localhost:8080/api
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      // For now, we'll just pass the token as a simple header
      // In production, this would be a proper JWT token
      config.headers['X-Auth-Token'] = token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
  isAdmin: () => {
    const user = authAPI.getCurrentUser();
    return user && user.role === 'ADMIN';
  }
};

// Hotel API
export const hotelAPI = {
  getAllHotels: () => api.get('/hotels'),
  getHotelById: (id) => api.get(`/hotels/${id}`),
  searchHotels: (params) => api.get('/hotels', { params }),
  searchByCity: (city) => api.get(`/hotels/search/city/${city}`),
  searchByState: (state) => api.get(`/hotels/search/state/${state}`),
  searchByCountry: (country) => api.get(`/hotels/search/country/${country}`),
  searchByName: (name) => api.get(`/hotels/search/name/${name}`),
  searchByCityAndMaxAcCost: (city, maxCost) => api.get(`/hotels/search/city/${city}/maxac/${maxCost}`),
  searchByCityAndMaxNonAcCost: (city, maxCost) => api.get(`/hotels/search/city/${city}/maxnonac/${maxCost}`),
  addHotel: (hotelData) => api.post('/hotels', hotelData),
  updateHotel: (id, hotelData) => api.put(`/hotels/${id}`, hotelData),
  deleteHotel: (id) => api.delete(`/hotels/${id}`),
  uploadHotelImage: (id, imageFile) => {
    const formData = new FormData();
    formData.append('image', imageFile);
    return api.post(`/hotels/${id}/upload-image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  getHotelImageUrl: (id) => `${API_BASE_URL}/hotels/${id}/image`
};

// Reservation API
export const reservationAPI = {
  getAllReservations: () => api.get('/reservations'),
  getReservationById: (id) => api.get(`/reservations/${id}`),
  getUserReservations: (userId) => api.get(`/reservations/user/${userId}`),
  createReservation: (reservationData) => api.post('/reservations', reservationData),
  updateReservation: (id, reservationData) => api.put(`/reservations/${id}`, reservationData),
  cancelReservation: (id) => api.put(`/reservations/${id}/cancel`)
};

// Payment API
export const paymentAPI = {
  processPayment: (paymentData) => api.post('/payments/process', paymentData),
  getPaymentById: (id) => api.get(`/payments/${id}`),
  getUserPayments: (userId) => api.get(`/payments/user/${userId}`)
};

// User API
export const userAPI = {
  getUserById: (id) => api.get(`/users/${id}`),
  updateUser: (id, userData) => api.put(`/users/${id}`, userData),
  getAllUsers: () => api.get('/users')
};

export default api;
