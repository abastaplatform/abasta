import { Link } from 'react-router-dom';
import './Topbar.scss';

const Topbar = () => {
  return (
    <div className="topbar">
      <Link to="/" className="back-button d-inline-flex align-items-center">
        <i className="bi bi-arrow-left-short"></i>
      </Link>
    </div>
  );
};

export default Topbar;
