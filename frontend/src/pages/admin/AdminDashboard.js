import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Badge, Spinner } from 'react-bootstrap';
import { hotelAPI, reservationAPI, userAPI } from '../../services/api';
import { toast } from 'react-toastify';
import { FaHotel, FaUsers, FaCalendarCheck, FaRupeeSign } from 'react-icons/fa';

const AdminDashboard = () => {
  const [stats, setStats] = useState({
    totalHotels: 0,
    totalUsers: 0,
    totalReservations: 0,
    totalRevenue: 0
  });
  const [recentReservations, setRecentReservations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      // Load all data in parallel
      const [hotelsResponse, usersResponse, reservationsResponse] = await Promise.all([
        hotelAPI.getAllHotels(),
        userAPI.getAllUsers(),
        reservationAPI.getAllReservations()
      ]);

      const hotels = hotelsResponse.data;
      const users = usersResponse.data;
      const reservations = reservationsResponse.data;

      // Calculate stats - only count actual revenue earned
      const confirmedRevenue = reservations
        .filter(r => r.status === 'CONFIRMED' || r.status === 'COMPLETED')
        .reduce((sum, r) => sum + parseFloat(r.totalCost), 0);
      
      const cancellationRevenue = reservations
        .filter(r => r.status === 'CANCELLED')
        .reduce((sum, r) => {
          const refunded = r.refundedAmount !== null && r.refundedAmount !== undefined ? r.refundedAmount : 0;
          const deductedAmount = r.totalCost - refunded;
          return sum + deductedAmount;
        }, 0);
      
      const totalRevenue = confirmedRevenue + cancellationRevenue;

      setStats({
        totalHotels: hotels.length,
        totalUsers: users.length,
        totalReservations: reservations.length,
        totalRevenue: totalRevenue
      });

      // Create hotel lookup map
      const hotelMap = {};
      hotels.forEach(hotel => {
        hotelMap[hotel.hotelId] = hotel.hotelName;
      });

      // Get recent reservations (last 10) and enrich with hotel names
      const sortedReservations = reservations
        .sort((a, b) => new Date(b.createdAt || b.checkInDate) - new Date(a.createdAt || a.checkInDate))
        .slice(0, 10)
        .map(reservation => ({
          ...reservation,
          hotelName: hotelMap[reservation.hotelId] || `Hotel #${reservation.hotelId}`
        }));
      
      setRecentReservations(sortedReservations);
    } catch (error) {
      toast.error('Failed to load dashboard data');
      console.error('Dashboard error:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const variants = {
      'CONFIRMED': 'success',
      'PENDING': 'warning',
      'CANCELLED': 'danger',
      'COMPLETED': 'info'
    };
    return <Badge bg={variants[status] || 'secondary'}>{status}</Badge>;
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <div className="admin-panel">
      <Container className="py-4">
        <Row>
          <Col>
            <h2 className="mb-4">Admin Dashboard</h2>
          </Col>
        </Row>

        {/* Stats Cards */}
        <Row className="mb-4">
          <Col md={3} className="mb-3">
            <Card className="text-center h-100">
              <Card.Body>
                <FaHotel size={40} className="text-primary mb-2" />
                <h3 className="mb-1">{stats.totalHotels}</h3>
                <p className="text-muted mb-0">Total Hotels</p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3} className="mb-3">
            <Card className="text-center h-100">
              <Card.Body>
                <FaUsers size={40} className="text-success mb-2" />
                <h3 className="mb-1">{stats.totalUsers}</h3>
                <p className="text-muted mb-0">Total Users</p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3} className="mb-3">
            <Card className="text-center h-100">
              <Card.Body>
                <FaCalendarCheck size={40} className="text-info mb-2" />
                <h3 className="mb-1">{stats.totalReservations}</h3>
                <p className="text-muted mb-0">Total Reservations</p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3} className="mb-3">
            <Card className="text-center h-100">
              <Card.Body>
                <FaRupeeSign size={40} className="text-warning mb-2" />
                <h3 className="mb-1">₹{stats.totalRevenue.toLocaleString()}</h3>
                <p className="text-muted mb-0">Total Revenue</p>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* Recent Reservations */}
        <Row>
          <Col>
            <Card>
              <Card.Header>
                <h5 className="mb-0">Recent Reservations</h5>
              </Card.Header>
              <Card.Body>
                {recentReservations.length === 0 ? (
                  <p className="text-muted text-center py-3">No reservations found</p>
                ) : (
                  <Table responsive hover>
                    <thead>
                      <tr>
                        <th>Reservation ID</th>
                        <th>Hotel</th>
                        <th>User ID</th>
                        <th>Check-in</th>
                        <th>Check-out</th>
                        <th>Guests</th>
                        <th>Total Cost</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {recentReservations.map((reservation) => (
                        <tr key={reservation.reservationId}>
                          <td>#{reservation.reservationId}</td>
                          <td>{reservation.hotelName || `Hotel #${reservation.hotelId}`}</td>
                          <td>#{reservation.userId}</td>
                          <td>{new Date(reservation.checkInDate).toLocaleDateString()}</td>
                          <td>{new Date(reservation.checkOutDate).toLocaleDateString()}</td>
                          <td>{reservation.numberOfGuests}</td>
                          <td>₹{reservation.totalCost}</td>
                          <td>{getStatusBadge(reservation.status)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* Quick Actions */}
        <Row className="mt-4">
          <Col>
            <Card>
              <Card.Header>
                <h5 className="mb-0">Quick Actions</h5>
              </Card.Header>
              <Card.Body>
                <Row>
                  <Col md={6} className="mb-3">
                    <Card className="border-primary">
                      <Card.Body className="text-center">
                        <FaHotel size={30} className="text-primary mb-2" />
                        <h6>Manage Hotels</h6>
                        <p className="text-muted small">Add, edit, or remove hotels from the system</p>
                        <a href="/admin/hotels" className="btn btn-primary btn-sm">
                          Go to Hotels
                        </a>
                      </Card.Body>
                    </Card>
                  </Col>
                  <Col md={6} className="mb-3">
                    <Card className="border-success">
                      <Card.Body className="text-center">
                        <FaCalendarCheck size={30} className="text-success mb-2" />
                        <h6>View All Reservations</h6>
                        <p className="text-muted small">Monitor and manage customer reservations</p>
                        <a href="/admin/reservations" className="btn btn-success btn-sm">
                          View All Reservations
                        </a>
                      </Card.Body>
                    </Card>
                  </Col>
                </Row>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default AdminDashboard;
