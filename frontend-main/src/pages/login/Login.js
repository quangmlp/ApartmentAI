import React, { useState, useEffect, useRef } from "react";
import { useNavigate, Link, NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { FaEye, FaEyeSlash, FaUser, FaLock, FaBuilding } from "react-icons/fa";
import chungCu from "./Chung_cu4.jpeg";
import "./Login.css";

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const otpInputs = useRef(Array(6).fill(null));

  const [formData, setFormData] = useState({ username: "", password: "" });
  const [rememberMe, setRememberMe] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const savedUsername = localStorage.getItem("rememberedUsername");
    if (savedUsername) {
      setFormData((prev) => ({ ...prev, username: savedUsername }));
      setRememberMe(true);
    }
  }, []);

  const requestHeaders = {
    "Content-Type": "application/json",
    Accept: "application/json",
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");

    if (!formData.username || !formData.password) {
      setError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
      return;
    }

    try {
      setLoading(true);
      const response = await fetch("http://localhost:22986/demo/auth/login", {
        method: "POST",
        headers: requestHeaders,
        body: JSON.stringify({
          username: formData.username,
          password: formData.password,
        }),
      });

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || "Đăng nhập thất bại");
      }

      const token = data?.result?.token;
      const roles = data?.result?.roles || [];

      if (!token || roles.length === 0) {
        throw new Error("Thông tin xác thực không hợp lệ");
      }

      localStorage.setItem("authToken", token);
      localStorage.setItem("userRoles", JSON.stringify(roles));

      if (rememberMe) {
        localStorage.setItem("rememberedUsername", formData.username);
      } else {
        localStorage.removeItem("rememberedUsername");
      }

      login(roles);
      navigate("/admin/user");
    } catch (err) {
      setError(err.message);
      localStorage.removeItem("authToken");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      {/* Left side - Image */}
      <div className="login-image-side">
        <div className="login-image-overlay"></div>
        <img src={chungCu} alt="Chung cư" className="login-background-image" />
        <div className="login-image-content">
          <FaBuilding className="login-hero-icon" />
          <h1>ApartmentAI</h1>
          <p>Hệ thống quản lý chung cư thông minh</p>
        </div>
      </div>

      {/* Right side - Form */}
      <div className="login-form-side">
        <div className="login-form-container">
          <div className="login-form-header">
            <h2>Chào mừng trở lại!</h2>
            <p>Đăng nhập để tiếp tục quản lý</p>
          </div>

          <form className="login-form" onSubmit={handleLogin} autoComplete="off">
            {error && (
              <div className="login-error-alert">
                <span className="error-icon">⚠️</span>
                {error}
              </div>
            )}

            <div className="login-input-group">
              <label htmlFor="username">Tên đăng nhập</label>
              <div className="login-input-wrapper">
                <FaUser className="input-icon" />
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username}
                  placeholder="Nhập tên đăng nhập"
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  disabled={loading}
                  className="login-input"
                />
              </div>
            </div>

            <div className="login-input-group">
              <label htmlFor="password">Mật khẩu</label>
              <div className="login-input-wrapper">
                <FaLock className="input-icon" />
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  name="password"
                  value={formData.password}
                  placeholder="Nhập mật khẩu"
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  disabled={loading}
                  className="login-input"
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  tabIndex={-1}
                >
                  {showPassword ? <FaEyeSlash /> : <FaEye />}
                </button>
              </div>
            </div>

            <div className="login-options">
              <label className="login-checkbox">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                  disabled={loading}
                />
                <span className="checkbox-mark"></span>
                <span className="checkbox-label">Ghi nhớ đăng nhập</span>
              </label>
              <Link to="/forgot" className="forgot-link">Quên mật khẩu?</Link>
            </div>

            <button type="submit" className="login-submit-btn" disabled={loading}>
              {loading ? (
                <span className="login-spinner"></span>
              ) : (
                "Đăng nhập"
              )}
            </button>
          </form>

          <div className="login-divider">
            <span>hoặc</span>
          </div>

          <div className="login-signup-prompt">
            Chưa có tài khoản?{" "}
            <Link to="/signup" className="signup-link">Đăng ký ngay</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;