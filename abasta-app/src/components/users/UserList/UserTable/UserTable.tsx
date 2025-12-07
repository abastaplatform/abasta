import type { User } from '../../../../types/user.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';
import './UserTable.scss';

interface UserTableProps {
  users: User[];
  onDelete: (userUuid: string, userName: string) => void;
}

const UserTable: React.FC<UserTableProps> = ({ users, onDelete }) => {
  if (users.length === 0) {
    return (
      <div className="user-table-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat usuaris</p>
      </div>
    );
  }

  return (
    <div className="user-table-container">
      <table className="user-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Correu electrònic</th>
            <th>Telèfon</th>
            <th>Estat</th>
            <th>Verificat</th>
            <th className="text-end">Accions</th>
          </tr>
        </thead>
        <tbody>
          {users.map(user => (
            <tr key={user.uuid}>
              <td className="user-name">
                {user.firstName} {user.lastName}
              </td>
              <td>{user.email}</td>
              <td>{user.phone}</td>
              <td>{user.isActive ? 'Actiu' : 'Inactiu'}</td>
              <td>{user.emailVerified ? 'Sí' : 'No'}</td>
              <td className="text-end">
                <ActionDropdown
                  userUuid={user.uuid}
                  onDelete={() => onDelete(user.uuid, user.firstName)}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserTable;
