import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../../context/useAuth';
import Navbar from '../Navbar/Navbar';
import './Leftbar.scss';

const Leftbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [isCollapsed, setIsCollapsed] = useState(true);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const toggleCollapse = () => setIsCollapsed(!isCollapsed);
  const toggleMobileMenu = () => setIsMobileMenuOpen(!isMobileMenuOpen);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 992) {
        document.body.style.marginLeft = '80px';
      } else {
        document.body.style.marginLeft = '0px';
      }
    };
    handleResize();
    window.addEventListener('resize', handleResize);
    return () => {
      window.removeEventListener('resize', handleResize);
      document.body.style.marginLeft = '0px';
    };
  }, []);

  const allMenuItems = [
    { icon: 'bi-grid', label: 'Panell principal', path: '/dashboard' },
    { icon: 'bi-cart', label: 'Comandes', path: '/orders' },
    { icon: 'bi-truck', label: 'Proveïdors', path: '/suppliers' },
    { icon: 'bi-box', label: 'Productes', path: '/products' },
    { icon: 'bi-bar-chart', label: 'Informes', path: '/reports' },
    {
      icon: 'bi-building',
      label: 'Empresa',
      path: '/company',
      adminOnly: true,
    },
    { icon: 'bi-people', label: 'Usuaris', path: '/users', adminOnly: true },
  ];
  const menuItems = allMenuItems.filter(
    item => !item.adminOnly || user?.role === 'ADMIN'
  );

  return (
    <>
      {/*Topbar solo móvil*/}
      <nav className="navbar navbar-dark bg-primary d-lg-none px-3">
        <div className="d-flex justify-content-between align-items-center w-100">
          <Link to="/" className="navbar-brand fw-bold text-white">
            Abasta
          </Link>
          <button
            className="navbar-toggler border-0"
            type="button"
            onClick={toggleMobileMenu}
          >
            <i
              className={`bi ${isMobileMenuOpen ? 'bi-x-lg' : 'bi-list'} fs-1 text-white`}
            ></i>
          </button>
        </div>
      </nav>

      {/*Sidebar (solo escritorio)*/}
      <aside
        className={`leftbar bg-primary text-white d-none d-lg-flex flex-column ${
          isCollapsed ? 'collapsed' : 'expanded'
        }`}
      >
        {/* Header */}
        <div
          className={`leftbar-header d-flex align-items-center bg-primary`}
          onClick={toggleCollapse}
        >
          <i className="bi bi-layout-sidebar fs-3 text-white"></i>
        </div>

        {/* Menu */}
        <ul className="nav flex-column leftbar-menu mt-3">
          {menuItems.map(item => (
            <li key={item.path} className="nav-item">
              <Link
                to={item.path}
                aria-label={item.label}
                className={`nav-link text-white d-flex align-items-center gap-3 px-4 py-3 ${
                  location.pathname === item.path ? 'active fw-semibold' : ''
                }`}
              >
                <i className={`bi ${item.icon} fs-3 me-2`}></i>
                {!isCollapsed && <span>{item.label}</span>}
              </Link>
            </li>
          ))}
        </ul>
      </aside>

      {/*Navbar Nomes Escriptori*/}
      {!isMobileMenuOpen && (
        <div
          className="flex top-0 d-none d-lg-block"
          style={{
            zIndex: 1020,
          }}
        >
          <Navbar />
        </div>
      )}

      {/*Menú mòbil (pantalla completa) */}
      {isMobileMenuOpen && (
        <div className="mobile-menu bg-primary position-fixed top-0 start-0 w-100 h-100 d-flex flex-column px-2 pb-4 pt-3">
          <div
            className="d-flex justify-content-between align-items-center mb-4 px-3"
            style={{ height: '56px' }}
          >
            <Link
              to="/dashboard"
              className="navbar-brand fw-bold text-white fs-2 m-0"
            >
              Abasta
            </Link>

            <button
              className="btn text-white border-0 d-flex align-items-center justify-content-center p-0"
              onClick={toggleMobileMenu}
            >
              <i className="bi bi-x-lg fs-2"></i>
            </button>
          </div>

          <ul className="nav flex-column mb-auto">
            {menuItems.map(item => (
              <li key={item.path} className="nav-item mb-2">
                <Link
                  to={item.path}
                  onClick={toggleMobileMenu}
                  className="nav-link text-white d-flex align-items-center gap-3 fs-5"
                >
                  <i className={`bi ${item.icon}`}></i>
                  <span>{item.label}</span>
                </Link>
              </li>
            ))}
          </ul>

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
              className="btn btn-outline-light w-100 mt-2 bg-transparent"
              onClick={handleLogout}
            >
              <i className="bi bi-box-arrow-left me-2"></i>
              Tancar sessió
            </button>
          </div>
        </div>
      )}
    </>
  );
};

export default Leftbar;
