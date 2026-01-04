import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { FaEye, FaEyeSlash, FaUser, FaLock, FaBuilding, FaEnvelope, FaCalendar, FaUserCircle } from "react-icons/fa";
import chungCu from "./Chung_cu4.jpeg";
import "./Signup.css";

const Signup = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    dob: "",
    email: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSignup = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:22986/demo/users/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        const data = await response.json();
        console.log(data.message);
        if(data.message == "Must be at least {min}"){
          throw new Error("Phải đủ 18 tuổi để đăng kí tài khoản");
        }
        else{
          throw new Error(data.message || "Đăng ký thất bại");
        }
        
      }

      navigate("/login");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="signup-page">
      {/* Left side - Image */}
      <div className="signup-image-side">
        <div className="signup-image-overlay"></div>
        <img src={chungCu} alt="Chung cư" className="signup-background-image" />
        <div className="signup-image-content">
          <FaBuilding className="signup-hero-icon" />
          <h1>ApartmentAI</h1>
          <p>Tham gia cộng đồng cư dân thông minh</p>
        </div>
      </div>

      {/* Right side - Form */}
      <div className="signup-form-side">
        <div className="signup-form-container">
          <div className="signup-form-header">
            <h2>Tạo tài khoản mới</h2>
            <p>Điền thông tin để đăng ký</p>
          </div>

          <form className="signup-form" onSubmit={handleSignup} autoComplete="off">
            {error && (
              <div className="signup-error-alert">
                <span className="error-icon">⚠️</span>
                {error}
              </div>
            )}

            <div className="signup-row">
              <div className="signup-input-group">
                <label htmlFor="lastName">Họ</label>
                <div className="signup-input-wrapper">
                  <FaUserCircle className="input-icon" />
                  <input
                    type="text"
                    id="lastName"
                    placeholder="Nhập họ"
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                    disabled={loading}
                    className="signup-input"
                  />
                </div>
              </div>

              <div className="signup-input-group">
                <label htmlFor="firstName">Tên</label>
                <div className="signup-input-wrapper">
                  <FaUserCircle className="input-icon" />
                  <input
                    type="text"
                    id="firstName"
                    placeholder="Nhập tên"
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                    disabled={loading}
                    className="signup-input"
                  />
                </div>
              </div>
            </div>

            <div className="signup-input-group">
              <label htmlFor="username">Tên đăng nhập</label>
              <div className="signup-input-wrapper">
                <FaUser className="input-icon" />
                <input
                  type="text"
                  id="username"
                  placeholder="Nhập tên đăng nhập"
                  value={formData.username}
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  disabled={loading}
                  className="signup-input"
                />
              </div>
            </div>

            <div className="signup-input-group">
              <label htmlFor="email">Email</label>
              <div className="signup-input-wrapper">
                <FaEnvelope className="input-icon" />
                <input
                  type="email"
                  id="email"
                  placeholder="Nhập email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  disabled={loading}
                  className="signup-input"
                />
              </div>
            </div>

            <div className="signup-input-group">
              <label htmlFor="dob">Ngày sinh</label>
              <div className="signup-input-wrapper">
                <FaCalendar className="input-icon" />
                <input
                  type="date"
                  id="dob"
                  value={formData.dob}
                  onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
                  disabled={loading}
                  className="signup-input date-input"
                />
              </div>
            </div>

            <div className="signup-input-group">
              <label htmlFor="password">Mật khẩu</label>
              <div className="signup-input-wrapper">
                <FaLock className="input-icon" />
                <input
                  type={showPassword ? "text" : "password"}
                  id="password"
                  placeholder="Nhập mật khẩu"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  disabled={loading}
                  className="signup-input"
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

            <button type="submit" className="signup-submit-btn" disabled={loading}>
              {loading ? (
                <span className="signup-spinner"></span>
              ) : (
                "Đăng ký"
              )}
            </button>
          </form>

          <div className="signup-divider">
            <span>hoặc</span>
          </div>

          <div className="signup-login-prompt">
            Đã có tài khoản?{" "}
            <Link to="/login" className="login-link">Đăng nhập</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;