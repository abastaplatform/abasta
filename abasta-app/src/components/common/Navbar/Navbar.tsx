import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import './Navbar.scss';

const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => {
    setIsMenuOpen(false);
  };

  const isActiveLink = (path: string) => {
    return location.pathname === path ? 'active' : '';
  };

  // Not auth users Navbar
  if (!isAuthenticated) {
    return (
      <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm sticky-top">
        <div className="container">
          <Link className="navbar-brand fw-bold" to="/" onClick={closeMenu}>
            Abasta
          </Link>

          {/* Mobile hamburger */}
          <button
            className="navbar-toggler border-0"
            type="button"
            onClick={toggleMenu}
            aria-expanded={isMenuOpen}
            aria-label="Toggle navigation"
          >
            <i className={`bi ${isMenuOpen ? 'bi-x' : 'bi-list'} fs-2`}></i>
          </button>

          {/* Menu items */}
          <div
            className={`collapse navbar-collapse ${isMenuOpen ? 'show' : ''}`}
          >
            <ul className="navbar-nav ms-auto align-items-lg-center gap-2">
              <li className="nav-item">
                <Link
                  className="btn btn-outline-0 bg-transparent"
                  to="/login"
                  onClick={closeMenu}
                >
                  Inicia sessió
                </Link>
              </li>
              <li className="nav-item">
                <Link
                  className="btn btn-outline-light bg-transparent"
                  to="/register"
                  onClick={closeMenu}
                >
                  Registra't
                </Link>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    );
  }

  // Authenticated users Navbar
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm sticky-top">
      <div className="container-fluid px-3 px-lg-4">
        <Link
          className="navbar-brand fw-bold"
          to="/dashboard"
          onClick={closeMenu}
        >
          Abasta
        </Link>

        {/* Mobile hamburger */}
        <button
          className="navbar-toggler border-0"
          type="button"
          onClick={toggleMenu}
          aria-expanded={isMenuOpen}
          aria-label="Toggle navigation"
        >
          <i className={`bi ${isMenuOpen ? 'bi-x' : 'bi-list'} fs-2`}></i>
        </button>

        {/* Menu items */}
        <div className={`collapse navbar-collapse ${isMenuOpen ? 'show' : ''}`}>
          {/* Navigation links */}
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <Link
                className={`nav-link ${isActiveLink('/dashboard')}`}
                to="/dashboard"
                onClick={closeMenu}
              >
                Inici
              </Link>
            </li>
          </ul>

          {/* User menu - Desktop */}
          <div className="d-none d-lg-flex align-items-center">
            <div className="dropdown">
              <button
                className="btn btn-link text-white text-decoration-none dropdown-toggle d-flex align-items-center gap-2"
                type="button"
                onClick={() => setIsUserDropdownOpen(!isUserDropdownOpen)}
                aria-expanded={isUserDropdownOpen}
              >
                <div className="user-avatar">
                  {user?.firstName?.charAt(0)}
                  {user?.lastName?.charAt(0)}
                </div>
                <span className="fw-medium">
                  {user?.firstName} {user?.lastName}
                </span>
              </button>
              <ul
                className={`dropdown-menu dropdown-menu-end ${isUserDropdownOpen ? 'show' : ''}`}
              >
                <li>
                  <button className="dropdown-item" onClick={handleLogout}>
                    <i className="bi bi-box-arrow-right me-2"></i>
                    Tancar sessió
                  </button>
                </li>
              </ul>
            </div>
          </div>

          {/* User menu - Mobile */}
          <div className="d-lg-none border-top border-light mt-3 pt-3">
            <div className="text-white mb-3">
              <div className="d-flex align-items-center gap-2 mb-2">
                <div className="user-avatar">
                  {user?.firstName?.charAt(0)}
                  {user?.lastName?.charAt(0)}
                </div>
                <div>
                  <div className="fw-medium">
                    {user?.firstName} {user?.lastName}
                  </div>
                  <small className="opacity-75">{user?.email}</small>
                </div>
              </div>
            </div>
            <button
              className="btn btn-outline-light w-100 mt-2"
              onClick={() => {
                handleLogout();
                closeMenu();
              }}
            >
              Tancar sessió
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
