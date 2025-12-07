import { useEffect, useState } from 'react';
import { useAuth } from '../../context/useAuth';
import { Card, Col, Row } from 'react-bootstrap';
import { Link } from 'react-router-dom';

import type { OrderResponseData } from '../../types/order.types';
import type { DashboardData } from '../../types/dashboard.types';

import { useOrderService } from '../../hooks/useOrderService';
import { useDashboard } from '../../hooks/useDashboard';

import Alert from '../common/Alert/Alert';
import PageHeader from '../common/PageHeader/PageHeader';
import OrdersTable from '../orders/OrderList/OrdersTable/OrdersTable';
import OrderCard from '../orders/OrderList/OrderCard/OrderCard';

const Dashboard = () => {
  const { user } = useAuth();
  const {
    isLoading: isLoadingOrders,
    error: ordersError,
    searchOrders,
    supplierNames,
  } = useOrderService();
  const {
    isLoading: isLoadingDashboard,
    error: dashboardError,
    fetchDashboardData,
  } = useDashboard();

  const [orders, setOrders] = useState<OrderResponseData[]>([]);
  const [dashboardData, setDashboardData] = useState<DashboardData>({
    totalComandes: 0,
    despesaComandes: 0,
    comandesPendents: 0,
  });

  useEffect(() => {
    let isMounted = true;

    const loadData = async () => {
      try {
        const dashboard = await fetchDashboardData();
        if (dashboard && isMounted) {
          setDashboardData(dashboard);
        }

        const now = new Date();
        const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
        const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

        const startDate = firstDay.toISOString().split('T')[0];
        const endDate = lastDay.toISOString().split('T')[0];

        const params = {
          filters: {
            createdAtFrom: startDate,
            createdAtTo: endDate,
          },
          page: 0,
          size: 5,
          isAdvanced: true,
          sortBy: 'createdAt',
          sortDir: 'desc' as const,
        };

        const data = await searchOrders(params);
        if (isMounted) {
          setOrders(data!.content || []);
        }
      } catch (err) {
        console.error('Error fetching orders:', err);
      }
    };

    loadData();

    return () => {
      isMounted = false;
    };
  }, []);

  const isLoading = isLoadingOrders || isLoadingDashboard;
  const error = ordersError || dashboardError;

  return (
    <div className="form-container">
      <div className="container-fluid py-4">
        <PageHeader title={`Hola, ${user?.firstName}`} />
        <p>
          Aquest és el teu panell de control on pots veure un resum de les teves
          activitats recents
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
                  <p className="display-6 mb-0">
                    {dashboardData.totalComandes}
                  </p>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="mb-4">
                <Card.Body className="text-center">
                  <h5>DESPESA MES</h5>
                  <p className="display-6 mb-0">
                    {dashboardData.despesaComandes.toFixed(2)}€
                  </p>
                </Card.Body>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="mb-4">
                <Card.Body className="text-center">
                  <h5>COMANDES PENDENTS</h5>
                  <p className="display-6 mb-0">
                    {dashboardData.comandesPendents}
                  </p>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        )}

        {!isLoading && (
          <div>
            <h4 className="mb-3 mt-4 text-primary">Comandes del mes actual</h4>
            <div className="d-none d-md-block">
              <OrdersTable
                orders={orders}
                supplierNames={supplierNames}
                sortBy={'deliveryDate'}
                sortDir={'desc'}
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
