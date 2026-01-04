import React from "react";
import { NavLink } from "react-router-dom";
import { FaCog, FaUserPlus, FaSignInAlt, FaBuilding } from "react-icons/fa";
import "./NavbarDefault.css";

const NavbarDefault = () => {
  return (
    <nav className="navbar-default">
      <div className="navbar-default-container">
        <NavLink to="/" className="navbar-default-brand">
          <FaBuilding className="brand-icon" />
          <span>ApartmentAI</span>
        </NavLink>
        
        <div className="navbar-default-nav">
          <NavLink to="/signup" className="nav-link-default">
            <FaUserPlus className="nav-icon" />
            <span>Đăng ký</span>
          </NavLink>
          <NavLink to="/login" className="nav-link-default">
            <FaSignInAlt className="nav-icon" />
            <span>Đăng nhập</span>
          </NavLink>
          <NavLink to="/settings" className="nav-link-default settings-link">
            <FaCog className="settings-icon" />
          </NavLink>
        </div>
      </div>
    </nav>
  );
};

export default NavbarDefault;
