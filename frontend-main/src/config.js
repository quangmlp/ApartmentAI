const config = {
  // Tự động chọn URL dựa trên môi trường chạy
  API_BASE_URL: process.env.NODE_ENV === 'production' 
    ? 'https://backend-6w7s.onrender.com/demo'  // URL Backend khi deploy
    : 'http://localhost:22986/demo'             // URL Backend khi chạy local
};

export default config;