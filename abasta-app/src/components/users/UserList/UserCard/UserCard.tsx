import type { User } from '../../../../types/user.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';
import './UserCard.scss';

interface UserCardProps {
  users: User[];
  onDelete: (userUuid: string, firsName: string) => void;
}

const UserCard: React.FC<UserCardProps> = ({ users, onDelete }) => {
  if (users.length === 0) {
    return (
      <div className="user-card-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat proveïdors</p>
      </div>
    );
  }

  return (
    <div className="user-card-container">
      {users.map(user => (
        <div key={user.uuid} className="user-card">
          <div className="user-card-header">
            <h3 className="user-card-name">
              {user.firstName} {user.lastName}
            </h3>
            <ActionDropdown
              userUuid={user.uuid}
              onDelete={() => onDelete(user.uuid, user.firstName)}
            />
          </div>

          <div className="user-card-body">
            <div className="user-card-row">
              <span className="user-card-label">Correu electrònic</span>
              <span className="user-card-value user-card-email">
                {user.email}
              </span>
            </div>

            <div className="user-card-row">
              <span className="user-card-label">Telèfon</span>
              <span className="user-card-value">{user.phone}</span>
            </div>

            <div className="user-card-row">
              <span className="user-card-label">Estat</span>
              <span className="user-card-value user-card-email">
                {user.isActive ? 'Actiu' : 'Inactiu'}
              </span>
            </div>

            <div className="user-card-row">
              <span className="user-card-label">Verificat</span>
              <span className="user-card-value user-card-email">
                {user.emailVerified ? 'Sí' : 'No'}
              </span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default UserCard;
