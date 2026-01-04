import React from "react";
import { Link } from "react-router-dom";
import { FaBuilding, FaUsers, FaShieldAlt, FaChartLine, FaComments, FaMobileAlt } from "react-icons/fa";
import "./Home.css";

const Home = () => {
  const features = [
    {
      icon: <FaBuilding />,
      title: "Quản lý phòng",
      description: "Theo dõi thông tin phòng, cư dân và các tiện ích một cách dễ dàng"
    },
    {
      icon: <FaUsers />,
      title: "Quản lý cư dân",
      description: "Hệ thống quản lý thông tin cư dân toàn diện và hiệu quả"
    },
    {
      icon: <FaShieldAlt />,
      title: "Bảo mật cao",
      description: "Bảo vệ thông tin cá nhân với hệ thống bảo mật hiện đại"
    },
    {
      icon: <FaChartLine />,
      title: "Báo cáo thống kê",
      description: "Theo dõi doanh thu, chi phí và các khoản phí một cách trực quan"
    },
    {
      icon: <FaComments />,
      title: "Hỗ trợ 24/7",
      description: "Chatbot AI thông minh hỗ trợ giải đáp mọi thắc mắc"
    },
    {
      icon: <FaMobileAlt />,
      title: "Đa nền tảng",
      description: "Truy cập mọi lúc mọi nơi trên nhiều thiết bị khác nhau"
    }
  ];

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          <h1>
            Chào mừng đến với
            <span className="brand-highlight"> ApartmentAI</span>
          </h1>
          <p className="hero-description">
            Hệ thống quản lý chung cư thông minh, hiện đại và hiệu quả. 
            Giải pháp toàn diện cho việc quản lý cư dân, phí dịch vụ và tiện ích.
          </p>
          <div className="hero-buttons">
            <Link to="/signup" className="btn-primary-hero">
              Bắt đầu ngay
            </Link>
            <Link to="/login" className="btn-secondary-hero">
              Đăng nhập
            </Link>
          </div>
        </div>
        <div className="hero-visual">
          <div className="hero-card">
            <FaBuilding className="hero-icon" />
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features-section">
        <div className="section-header">
          <h2>Tính năng nổi bật</h2>
          <p>Khám phá các tính năng giúp việc quản lý chung cư trở nên dễ dàng hơn</p>
        </div>
        <div className="features-grid">
          {features.map((feature, index) => (
            <div className="feature-card" key={index}>
              <div className="feature-icon">{feature.icon}</div>
              <h3>{feature.title}</h3>
              <p>{feature.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta-section">
        <div className="cta-content">
          <h2>Sẵn sàng để bắt đầu?</h2>
          <p>Đăng ký ngay hôm nay để trải nghiệm hệ thống quản lý chung cư hiện đại</p>
          <Link to="/signup" className="cta-button">
            Đăng ký miễn phí
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="home-footer">
        <p>© 2024 ApartmentAI. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default Home;
  