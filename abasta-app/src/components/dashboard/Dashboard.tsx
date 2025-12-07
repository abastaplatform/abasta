import { useEffect, useState } from 'react';
import { useAuth } from '../../context/useAuth';
import PageHeader from '../common/PageHeader/PageHeader';
import type { OrderResponseData } from '../../types/order.types';
import { useOrderService } from '../../hooks/useOrderService';
import Alert from '../common/Alert/Alert';
import OrdersTable from '../orders/OrderList/OrdersTable/OrdersTable';
import OrderCard from '../orders/OrderList/OrderCard/OrderCard';
import { Card, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Dashboard = () => {
  const { user } = useAuth();
  const { isLoading, error, fetchOrders, supplierNames } = useOrderService();

  const [orders, setOrders] = useState<OrderResponseData[]>([]);

  useEffect(() => {
    const params = {
      page: 0,
      size: 5,
      sort: 'createdAt,DESC',
    };

    const loadOrders = async () => {
      try {
        const data = await fetchOrders(params);
        setOrders(data!.content || []);
      } catch (err) {
        console.error('Error fetching orders:', err);
      }
    };

    loadOrders();
  }, []);

  return (
    <div className="form-container">
      <div className="container-fluid py-4">
        <PageHeader title={`Hola, ${user?.firstName}`} />
        <p>
          Aquest és el teu panell de control on pots veure un resum de les teves
          activitats recents.
        </p>

        {error && <Alert variant="danger" message={error} />}

        {isLoading && (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Carregant...</span>
            </div>
          </div>
        )}

        {!isLoading && (
          <Row>
            <Col md={4}>
              <Card className="mb-4">
                <Card.Body className="text-center">
                  <h5>TOTAL COMANDES</h5>
                  <p className="display-6">42</p>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="mb-4">
                <Card.Body className="text-center">
                  <h5>DESPESA MES</h5>
                  <p className="display-6">42</p>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="mb-4">
                <Card.Body className="text-center">
                  <h5>COMANDES PENDENTS</h5>
                  <p className="display-6">42</p>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        )}

        {!isLoading && (
          <div>
            <h4 className="mb-3 mt-4 text-primary">Comandes Recents</h4>
            <div className="d-none d-md-block">
              <OrdersTable
                orders={orders}
                supplierNames={supplierNames}
                sortBy={'name'}
                sortDir={'asc'}
                showActions={false}
              />
            </div>

            <div className="d-block d-md-none">
              <OrderCard orders={orders} supplierNames={supplierNames} />
            </div>

            <Link to="/orders" className="btn btn-link mt-3">
              Veure totes les comandes
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
